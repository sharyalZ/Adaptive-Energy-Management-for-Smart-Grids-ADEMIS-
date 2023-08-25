/**
 * @author sharyal et jbblanc
 */

package AMAS;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JOptionPane;

import Communication.InfoMsg;
import Communication.Issue;
import Communication.Request;
import Communication.RequestContent;
import Communication.UpdatedRequest;
import Interface.DSLModelType;
import Interface.PowerFactoryCaller;
import Interface.PowerFactoryParameters;
import Interface.PowerFactoryResults;
import fr.irit.smac.amak.aid.AddressableAID;
import fr.irit.smac.amak.messaging.IAmakEnvelope;
import fr.irit.smac.amak.messaging.IAmakMessageMetaData;
import fr.irit.smac.amak.messaging.IAmakReceivedEnvelope;
import ui.ConsumerType;
import ui.MyLog;
import ui.Parameters;
import ui.Results;

public class EVAgent extends ProsumerAgent {

	private String batteryName;
	private double soe0;
	private double criticality = 0, criticalityMin;
	private double power, plannedPower, past_power = 0;
	private double soeMin = 0.7;// Etat d'énergie minimum a atteindre
	private double Ebat = 30;// Capacité de la batterie en kWh
	private double Pmax = 7;// Puissance maximum de recharge de la batterie en kW
	private double tr;// Temps restant avant le départ du véhicule
	private boolean connectedEV = true;// Véhicule connecté au réseau (true) ou en déplacement (false)
	private boolean intelligentEVs;// Véhicule contrôlé par un agent coopératif (true) ou respectant un
								   // comportement basique (recharge à la puissance de recharge prévue Pplanned.
	private Request currentRequest;// Requête qui a permis de définir la puissance de recharge de l'EV au cycle
								   // précédent
	private EV ev;// EV est une classe interne gérant le départ et d'arrivé des véhicules
	double alpha = 0.9;
	double beta = 0.1;
	double Tlmin = 0.1;
	double Tlmax = 0.9;
	double h;
	long startTime,stopTime;
	protected Collection<IAmakReceivedEnvelope<UpdatedRequest, IAmakMessageMetaData, AddressableAID>> allUpdatedRequestReceived;

	/**
	 * Initialisation de l'agent, avec les modèles DSL, le scénario...
	 * 
	 * @param amas
	 * @param name
	 * @param evPath
	 */

	// Initializing EV agent
	public EVAgent(MyAMAS amas, String name, String evPath) {
		super(amas, name);
		currentRequest = new Request(0, new RequestContent(null, name, null), null, 0);
		ev = new EV(name, amas.getEnvironment().getCurrentTime(), evPath, this);
		intelligentEVs = Parameters.getIntelligentEVs();
		criticalityMin = Parameters.getCriticalityMinEVAgent();
		initDSLModels(evPath);
		this.initScenario();
	}

	/**
	 * Permet de retrouver les modèles DSL nécessaires associé à ce véhicule par
	 * leurs chemins dans PF : P_Input_path permet de définir la puissance de
	 * recharge du véhicule dans PF et BatteryPath de récupérer le SoE de la
	 * batterie
	 * 
	 * @param evPath
	 */
	private void initDSLModels(String evPath) {
		String CompositeModelPath = PowerFactoryCaller.Get1Param(evPath, "c_pmod");
		List<String> pelm = PowerFactoryCaller.Get2Param(CompositeModelPath, "pelm");
		List<String> pblk = PowerFactoryCaller.Get2Param(CompositeModelPath, "pblk");
		for (int i = 0; i < pelm.size(); i++) {
			String DSLName = PowerFactoryCaller.GetComplete1Param(pblk.get(i), "loc_name");
			switch (DSLName) {
			case "P-input":
				dslmodels.addModel(DSLModelType.P_Input_path, pelm.get(i));
				break;
			case "SlotBattery":
				dslmodels.addModel(DSLModelType.BatteryPath, pelm.get(i));
				break;
			default: System.out.println("Unknown DSL mode : "+DSLName);
			}
		}
		batteryName = PowerFactoryCaller.GetComplete1Param(dslmodels.getModel(DSLModelType.BatteryPath), "loc_name");// To get SoE																									
	}

	// Percieve method of the EV agent
	@Override
	protected void onPerceive() { // This was done to make sure the EVagent do not do anything after its departure
									// (do the same for onDecide and onAct)time
		if (this.connectedEV) {
			onPerceive1();
		}
	}

	protected void onPerceive1() {
		super.onPerceive();
		if (ev.initialConnection) {
			ev.soe = soe0;
		} else {
			ev.soe = PowerFactoryResults.GetSoE(batteryName);
		}

		Results.addResult("soe", name, this.getEnvironment().getCurrentTimeInHours(), ev.soe);

		//Reading all received requests
		allRqtsReceived = this.getAllMessages();
		allUpdatedRequestReceived = this.getReceivedEnvelopesGivenMessageType(UpdatedRequest.class);
		MyLog.print(name + " : allRqtsReceived : " + allRqtsReceived);
		tr = ev.getRemainingTime(this.getEnvironment().getCurrentTime());// Temps restant avant le départ du véhicule

	}

	// Decision method of the EV agent
	@Override
	protected void onDecide() {
		if (this.connectedEV) {
			onDecide1();
		}
	}

	protected void onDecide1() {
		//Calculating EV criticality 
		this.calcCriticality();
		
		
		if (intelligentEVs && connectedEV) {
			if (!allRqtsReceived.isEmpty()) {// Requêtes reçues ?				
				IAmakEnvelope moreCriticalRequestEnvelope = findMoreCriticalEnvelope();
				double requestCriticality = ((Request) moreCriticalRequestEnvelope.getMessage()).getCriticality();
				if (this.criticality < requestCriticality) {
					power = definePower(requestCriticality, (Request) moreCriticalRequestEnvelope.getMessage());
					MyLog.print(name + " : [power=" + power + ", currentRequest=" + (Request) moreCriticalRequestEnvelope.getMessage() + "]");
				} else {		
					correctPlannedPower();
					power = plannedPower;
					MyLog.print(name + " : [power=" + power + ", noCooperation]");
				}
			} else {	
				correctPlannedPower();
				power = plannedPower;
				MyLog.print(name + " : [power=" + power + ", noCooperation]");
			}
		} else if (connectedEV) {
			correctPlannedPower();
			power = plannedPower;// by default, if it does not cooperate
		} else {
			power = 0;// Véhicule non connecté au réseau : pas de recharge
		}
		Results.addResult("EV power", name, this.getEnvironment().getCurrentTimeInHours(), power);
	}

	// Action method of the EV agent
	@Override
	protected void onAct() {
		if (this.connectedEV) {
			onAct1();
		}
	}

	protected void onAct1() {
		this.setPower(power);
		InfoMsg msg = new InfoMsg("Change of the EV consumption", power - past_power);
		this.sendMessage(msg, aggregatorAID);
		Results.addConsumptionByType(ConsumerType.EV, this.getEnvironment().getCurrentTimeInHours(), past_power);	
		past_power = power;
	}

	// Setting powers of EV models in powerFactory
	private void setPower(double p) {
		if (Math.abs(power) > Pmax) {
			JOptionPane.showMessageDialog(null, "EVAgent : " + name + "Unaccepted EV power : " + power, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		PowerFactoryParameters.addPev(this.dslmodels.getModel(DSLModelType.P_Input_path), p / 1000);// In PF, defined in
																								// MW, in JAVA
																									// defined in kW
	}

	@Override
	protected void onAgentCycleEnd() {
		PowerFactoryParameters.addSoE0(this.dslmodels.getModel(DSLModelType.BatteryPath), ev.soe);
		this.stopTime = System.nanoTime();
	}

	@Override
	public String toString() {
		return "EVAgent [name=" + name + "]";
	}

	protected void initScenario() {
		// reading EVs input SOE values and setting parameters in PowerFactory
		soe0 = Parameters.getEvSoE0(name);
		PowerFactoryCaller.Set1ParamList(dslmodels.getModel(DSLModelType.BatteryPath), "params", 1, soe0);
		PowerFactoryCaller.Set1ParamList(dslmodels.getModel(DSLModelType.P_Input_path), "params", 0, 0);
		setPower(0);
		
		//Calculating planned EV Power (these lines are executed when basic charging strategy is selected 
		tr = ev.getRemainingTime(Parameters.getStartTime());
		if (tr != 0) {
			plannedPower = (soeMin - soe0) * Ebat / (tr / 60);
		} else {
			plannedPower = 0;
		}
		if (plannedPower > Pmax) {
			power = Pmax;
			plannedPower = Pmax;
		} else if (plannedPower < 0) {
			plannedPower = 0;
		}
		connectedEV = ev.getConnected();
	}

	private void calcCriticality() {
		criticality = (soeMin - ev.soe) * Ebat / (Pmax * tr / 60);
		if (criticality > 1) {
			criticality = 1;
		} else if (criticality < 0) {
			criticality = 0;
		}
		Results.addResult("EV criticalities", name, this.getEnvironment().getCurrentTimeInHours(), criticality);
	}
	private double definePower(double criticality, Request request) {
		double direction = request.direction;
		
		double AntagonistCriticality  = FindAntagonistCriticality();
		double HighestCriticality = criticality;
		double H = calculateH(HighestCriticality);
		

		if ((request.getRequestContent().getIssue() == Communication.Issue.underConsumption) ||
			(request.getRequestContent().getIssue() == Communication.Issue.overVoltage) ||
			((request.getRequestContent().getIssue() == Communication.Issue.highCurrent) && (direction == -1))) {
			
			power = past_power + (HighestCriticality - H * AntagonistCriticality) * (Pmax); // charge more or discharge less
		}
		
		
		if ((request.getRequestContent().getIssue() == Communication.Issue.overConsumption) ||
				(request.getRequestContent().getIssue() == Communication.Issue.underVoltage) ||
				((request.getRequestContent().getIssue() == Communication.Issue.highCurrent) && (direction == 1))) {
				
				power = past_power - (HighestCriticality - H * AntagonistCriticality) * (Pmax); // charge less or discharge more
			}
		power = correctPower(power);
		correctPlannedPower();
		return power;
	}


	/*
	 * Method to calculate h(Cl) for the aggregator agent
	 */
	private double calculateH(double Cline) {
		double Ka = (Tlmax - Tlmin) / (Math.log(beta) - Math.log(alpha));
		double Kb = Math.log(alpha) - (Tlmin * (Math.log(beta) - Math.log(alpha))) / (Tlmax - Tlmin);

		if (Cline < Tlmin) {
			h = 1 + (((alpha - 1) / Tlmin) * Cline);
		} else if (Cline <= Tlmax) {
			h = Math.exp((Cline) / Ka + Kb);
		} else {
			h = 1 - ((beta / (1 - Tlmax)) * Cline);
		}		
		return h;
	}


	// Method to find the highest antagonistic request
	private double FindAntagonistCriticality() {
		double secondMaxCr = this.criticality;

		if (!allRqtsReceived.isEmpty()) {
			Request moreCriticalRequest = (Request) findMoreCriticalEnvelope().getMessage();
			Issue moreCriticalIssue = moreCriticalRequest.getRequestContent().getIssue();
			
			if ((moreCriticalIssue.equals(Issue.highCurrent) && (moreCriticalRequest.direction==1)) || 
				 (moreCriticalIssue.equals(Issue.overConsumption)) ||
				 (moreCriticalIssue.equals(Issue.underVoltage))) {
				secondMaxCr = this.criticality;
			} else {
				secondMaxCr = 0;
			}
			
			for (IAmakEnvelope envelope : allRqtsReceived) {
				Request message = (Request) envelope.getMessage();
				if (message.getRequestContent().getRequester()
						.equals(moreCriticalRequest.getRequestContent().getRequester())) {
				} else {
					if (!message.getRequestContent().getIssue()
							.equals(moreCriticalRequest.getRequestContent().getIssue())) {
						if ((moreCriticalIssue.equals(Issue.highCurrent) && (moreCriticalRequest.direction==1)) || 
							(moreCriticalIssue.equals(Issue.overConsumption)) ||
							(moreCriticalIssue.equals(Issue.underVoltage))) {
							if ((moreCriticalIssue.equals(Issue.highCurrent) && (moreCriticalRequest.direction==-1)) || 
								(moreCriticalIssue.equals(Issue.underConsumption)) ||
								(moreCriticalIssue.equals(Issue.overVoltage))) {
								if (message.getCriticality() > secondMaxCr) {
									secondMaxCr = message.getCriticality();
								}
							}
						} else if ((moreCriticalIssue.equals(Issue.highCurrent) && (moreCriticalRequest.direction==-1)) || 
								   (moreCriticalIssue.equals(Issue.underConsumption)) ||
								   (moreCriticalIssue.equals(Issue.overVoltage))) {
							if ((moreCriticalIssue.equals(Issue.highCurrent) && (moreCriticalRequest.direction==1)) || 
								(moreCriticalIssue.equals(Issue.overConsumption)) ||
								(moreCriticalIssue.equals(Issue.underVoltage))) {
								if (message.getCriticality() > secondMaxCr) {
									secondMaxCr = message.getCriticality();
								}
							}
						}

					}
				}
			}
		}
		return secondMaxCr;
	}

		
	
	@Override
	protected void onAgentCycleBegin() {
		this.startTime = System.nanoTime();
		super.onAgentCycleBegin();
		connectedEV = ev.beginCycle();
	}

	private void correctPlannedPower() {
		tr = ev.getRemainingTime(this.getEnvironment().getCurrentTime());
		if (tr != 0) {
			plannedPower = (soeMin - ev.soe) * Ebat / (tr / 60);
		} else {
			plannedPower = 0;
		}

		if (plannedPower < 0) {   // may have to be corrected: EVs should be able to discharge, even during planification stage
			plannedPower = 0;
		}
		if (plannedPower > Pmax) {
			plannedPower = Pmax;
		}
		
	}

	private double correctPower(double power) {
		if (power > Pmax) {
			power = Pmax;
		} else if (power < -Pmax) {
			power = -Pmax;
		}

		return power;
	}

	@Override
	public void endSimulation() {
		System.out.println("end simu from " + name);
	}

	private class EV {
		private String name;
		protected double soe;
		protected GregorianCalendar departureTime;// Heure de départ du véhicule
		protected GregorianCalendar arrivalTime;// Heure d'arrivé du véhicule
		private boolean connected = true;
		private GregorianCalendar currentTime;
		private String EVPath;
		private DateFormat format;
		private EVAgent evAgent;
		private boolean initialConnection = false; // to check if in the first cycle EV is connected or not

		public EV(String name, GregorianCalendar currentTime, String EVPath, EVAgent evAgent) {
			this.name = name;
			this.evAgent = evAgent;
			format = new SimpleDateFormat("d/M/y H:mm:ss");
			departureTime = Parameters.getEvDeparture(name);
			arrivalTime = Parameters.getEvArrival(name);
			this.currentTime = currentTime;
			this.EVPath = EVPath;

			if (currentTime.before(departureTime) && currentTime.after(arrivalTime)) {// La simulation commence avant
																						// l'heure de départ du véhicule
																						// : il
				// est connecté
				connected = true;
				initialConnection = true;
			} else {
				connected = false;
			}
			/*
			 * else if (currentTime.before(arrivalTime)) {// La simulation commence après
			 * l'heure de départ du véhicule // mais avant l'heure de retour du véhicule :
			 * il est // déconnecté connected = false; } else {// La simulation après
			 * l'heure de retour du véhicule : il s'est reconnecté. Les // prochains départ
			 * et arrivés sont pour le lendemaint (les gregorianCalendar // sont
			 * incrémentés) connected = true; departureTime.add(Calendar.DAY_OF_MONTH, 1);
			 * arrivalTime.add(Calendar.DAY_OF_MONTH, 1); }
			 */

			PowerFactoryCaller.EVConnection(EVPath, connected);// Envoi à PF si le EV est connecté ou non
		}

		public boolean getConnected() {
			return connected;
		}

		public boolean beginCycle() {

			if (connected) {

				initialConnection = false;

				if (currentTime.after(departureTime)) {// Si le véhicule est connecté alors que l'heure courante est
														// après l'heure de départ : il doit partir
					connected = false;
					PowerFactoryCaller.EVConnection(EVPath, false);
				}
			} else {
				if (currentTime.after(arrivalTime) && currentTime.before(departureTime)) {// Si le véhicule est
																							// déconnecté alors que
																							// l'heure courante est
					// après l'heure d'arrivé : il revient. Le prochain départ est
					// pour le lendemain (nécessaire de le mettre à jour pour le
					// calcul du plannedPower. L'heure d'arrivée n'est cependant pas
					// modifié : A faire si la simulation dure sur plusieurs jours
					if (connected == false) {
						initialConnection = true;
					}
					connected = true;
					PowerFactoryCaller.EVConnection(EVPath, true);
					if (intelligentEVs) {
						evAgent.correctPlannedPower();
					}
				}
			}
			return connected;
		}

		public double getRemainingTime(GregorianCalendar time) 
		{// in minutes
			if (time.after(arrivalTime)) 
			{
				return (double) (departureTime.get(Calendar.DAY_OF_MONTH) - time.get(Calendar.DAY_OF_MONTH)) * 24 * 60
						+ (departureTime.get(Calendar.HOUR_OF_DAY) - time.get(Calendar.HOUR_OF_DAY)) * 60
						+ departureTime.get(Calendar.MINUTE) - time.get(Calendar.MINUTE)
						+ (double) (departureTime.get(Calendar.SECOND) - time.get(Calendar.SECOND)) / 60;
			} else {
				return (double)0;
			}
		}

		@Override
		public String toString() {
			return "EV [name=" + name + ", departureTime=" + format.format(departureTime.getTime()) + ", arrivalTime="
					+ format.format(arrivalTime.getTime()) + "]";
		}		
	}		
}

