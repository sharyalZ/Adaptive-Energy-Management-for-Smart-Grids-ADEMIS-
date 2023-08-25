/**
 * @author sharyal et jbblanc
 */

package AMAS;

import java.util.Collection;
import java.util.GregorianCalendar;

import Communication.Issue;
import Communication.Request;
import Communication.RequestFromLine;
import Communication.UpdatedRequest;
import Interface.PowerFactoryResults;
import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.aid.AddressableAID;
import fr.irit.smac.amak.messaging.IAmakEnvelope;
import fr.irit.smac.amak.messaging.IAmakMessageMetaData;
import fr.irit.smac.amak.messaging.IAmakReceivedEnvelope;
import ui.Parameters;
import ui.Results;

public class LineAgent extends MyAgent {

	private double i, p;// Courant dans la ligne
	private double criticality, criticalityMin, anticipatedCriticality = 0;
	private double direction; // +1 means power entering the line and -1 means power leaving the line @vm
	private double Kline;// Utilisé dans le calcul de la criticité anticipée
	private boolean supportedAgent = false;// Pour savoir si l'agent a déjà envoyé une requête
	protected Collection<IAmakReceivedEnvelope<UpdatedRequest, IAmakMessageMetaData, AddressableAID>> allUpdatedRequestReceived;
	private AID AIDtoSource;
	private String PFPath;// Unused
	private double ratedCurrent;// Courant nominal de la ligne en kA
	private double Imin;// Courant minimal utilisé dans le calcul de criticité en kA
	private Bus bus1, bus2;
	private Request requestToSend, updatedRequestToSend;// Requête et updatedRequest qui seront à envoyer à la fin du
														// cycle

	// Initializing line agent
	public LineAgent(MyAMAS amas, String name, String linePath) {
		super(amas, name);
		this.PFPath = linePath;
		Kline = 0.001; // AMAS is not able to fetch the value of Kline from the file @vm error exists
		// Kline = Parameters.getKLine();// Parameters
		criticalityMin = Parameters.getCriticalityMinLineAgent();
		ratedCurrent = Parameters.getDefaultRatedCurrent();// The rated current of the line may be defined by default or
															// by its name (or by type by the interface, but not
															// implemented in this class)
		/*
		 * if(Parameters.getAdditionnalLineConfigByType().containsKey(type)) {//In the
		 * actual system, no type is defined for the line
		 * ratedCurrent=Parameters.getAdditionnalLineConfigByType().get(type); }
		 */

		if (Parameters.getAdditionnalLineConfigByName().containsKey(name)) {
			ratedCurrent = Parameters.getAdditionnalLineConfigByName().get(name);
		}
		Imin = Parameters.getCurrentMinimumRate() * ratedCurrent;
	}

	@Override
	public String toString() {
		return "LineAgent [name=" + name + "]";
	}

	// Perceive method of the Line agent
	@Override
	protected void onPerceive() {
		super.onPerceive();
		i = PowerFactoryResults.GetILine(name);
		p = PowerFactoryResults.GetPLine(name);

		Results.addResult("Iline (kA)", name, this.getEnvironment().getCurrentTimeInHours(), i);
		Results.addResult("Pline (kW)", name, this.getEnvironment().getCurrentTimeInHours(), p);
		allRqtsReceived = this.getAllMessages();
		allUpdatedRequestReceived = this.getReceivedEnvelopesGivenMessageType(UpdatedRequest.class);

	}

	// Decision method of the line agent
	@Override
	protected void onDecide() {
		this.calcCriticality();
		if (!allRqtsReceived.isEmpty()) {
			IAmakEnvelope moreCriticalRequestEnvelope = findMoreCriticalEnvelope();
			if (anticipatedCriticality < ((Request) moreCriticalRequestEnvelope.getMessage()).getCriticality()) {
				Request pastRequest = (Request) moreCriticalRequestEnvelope.getMessage();

				manageIssue();
				requestToSend = new RequestFromLine(pastRequest.getRequestContent().getIssue(),
						pastRequest.getCriticality(), pastRequest.getRequestContent().getRequester(),
						pastRequest.getRequestContent().getTimeOfRequest(),
						moreCriticalRequestEnvelope.getMessageSenderAID(), pastRequest.direction);
			} else if (anticipatedCriticality > 0.0) {
				manageIssue();
			}
		} else if (anticipatedCriticality > 0.0) {
			manageIssue();
		}
	}

	private void manageIssue() {
		if (!supportedAgent) {// If the line was not supported, and if its criticality is higher than a
								// threshold, it begins to be supported, and will always update its state with
								// "updateRequest type request"
			supportedAgent = true;
			requestToSend = new RequestFromLine(Issue.highCurrent, anticipatedCriticality, name,
					(GregorianCalendar) this.getEnvironment().getCurrentTime().clone(), null, direction);
		} else {

			requestToSend = new RequestFromLine(Issue.highCurrent, anticipatedCriticality, name,
					(GregorianCalendar) this.getEnvironment().getCurrentTime().clone(), null, direction);

			Results.addResult("anticipated criticalitiesLine", name, this.getEnvironment().getCurrentTimeInHours(),
					anticipatedCriticality);
			Results.addResult("anticipated criticalitiesLine", name + " (criticality)",
					this.getEnvironment().getCurrentTimeInHours(), this.criticality);
			Results.addResult("Line Current Direction", name + " (direction)",
					this.getEnvironment().getCurrentTimeInHours(), this.direction);
			Results.addResult("Line Power", name + " (power)", this.getEnvironment().getCurrentTimeInHours(), this.p);
		}
	}

	// Action method of the line agent
	@Override
	protected void onAct() {
		if (requestToSend != null) {
			if (requestToSend.getRequestContent().getIssue().equals(Issue.highCurrent)) {// Envoi de la requête vers le
																							// bus en aval du réseau
				if (!bus1.aid.equals(AIDtoSource)) {
					this.sendRequest(requestToSend, bus1.aid);
				} else {
					this.sendRequest(requestToSend, bus2.aid);
				}
			} else {// Sinon c'est une requête à transférer
				this.sendRequest(requestToSend, bus1.aid);
				this.sendRequest(requestToSend, bus2.aid);
			}
			requestToSend = null;
		}
	}

	/**
	 * Calcul de la criticité
	 */
	private void calcCriticality() {

		criticality = (i - Imin) / (ratedCurrent - Imin);
		if (criticality > 1) {
			criticality = 1;
		} else if (criticality < 0) {
			criticality = 0;
		}
		
		if (criticality != 0)
		{
			this.calcDirection();
		}
		
		
		if (!supportedAgent) {
			anticipatedCriticality = criticality;
		}
		else {
			anticipatedCriticality += (Kline) * (criticality - anticipatedCriticality);
		}
		Results.addResult("criticalitiesLine", name, this.getEnvironment().getCurrentTimeInHours(), criticality);
	}

	// Calculating direction of the current
	private void calcDirection() {
		direction = (Math.abs(p)) / p;
	}

	/**
	 * Définition de l'aid utilisé pour communiquer avec un des deux bus
	 * 
	 * @param aidBus : aid du bus
	 * @param name   : nom du bus (non utilisé)
	 */
	public void setBus1(AID aidBus, String name) {
		this.bus1 = new Bus(name, aidBus);
	}

	/**
	 * Idem pour le second bus
	 * 
	 * @param aidBus
	 * @param name
	 */
	public void setBus2(AID aidBus, String name) {
		this.bus2 = new Bus(name, aidBus);
	}

	/**
	 * Définition de l'aid du bus vers l'amont du réseau pour que la ligne sache à
	 * qui envoyer sa requête
	 * 
	 * @param aIDtoSource
	 */
	public void setAIDtoSource(AID aIDtoSource) {
		AIDtoSource = aIDtoSource;
	}

	public String getBus1Name() {
		return bus1.name;
	}

	public String getBus2Name() {
		return bus2.name;
	}

	private class Bus {
		protected String name;
		protected AID aid;

		public Bus(String name, AID aid) {
			this.name = name;
			this.aid = aid;
		}
	}

}
