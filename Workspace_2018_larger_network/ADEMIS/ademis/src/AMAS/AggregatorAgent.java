/**
 * @author sharyal et jbblanc
 */

package AMAS;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import Communication.InfoMsg;
import Communication.Issue;
import Communication.Request;
import Communication.RequestAggregatotProsumer;
import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.aid.AddressableAID;
import fr.irit.smac.amak.messaging.IAmakMessageMetaData;
import fr.irit.smac.amak.messaging.IAmakReceivedEnvelope;
import ui.Parameters;
import ui.Results;
import java.lang.Math;
public class AggregatorAgent extends MyAgent {

	private double commitment;// in kW
	private int period, current_study_period=1;// period of the commitment, in minutes
	private double remainingTimeInThePeriod;// in minutes
	private double remainingTimeMin = 5;// in minutes
	private double SMAPeriod; 
	private double totalConsumption = 0; // Total consumption in BRP area, in kW
	private double currentEnergyMismatch = 0; //Energy mismatch in kW
	private double criticality = 0; // criticality of BRP agent [0,1]
	private double criticalityMin, criticalityMax = 0.9; //BRP criticality limits
	private double Pmax = 100;// kW , K_BRP, tunning parameter
	private GregorianCalendar endOfPeriod = new GregorianCalendar();
	private List<AID> neighborsAID = new ArrayList<>();

	
	// Initializing aggregator agent
	public AggregatorAgent(MyAMAS amas, String name) {
		super(amas, name);
		commitment = Parameters.getConsumptionCommitment(current_study_period);
		period = Parameters.getAggregatorPeriodCommitment();
		SMAPeriod = Parameters.getSMAPeriod();
		criticalityMin = Parameters.getCriticalityMinAggregatorAgent();
		remainingTimeInThePeriod = period;// in minutes
		Pmax = Parameters.getPmaxAggregator();
		endOfPeriod = (GregorianCalendar) Parameters.getStartTime().clone();
		endOfPeriod.add(Calendar.MINUTE, period);
		format = new SimpleDateFormat("H:mm:ss");
	}

	// Percieve method of aggregator agent
	@Override
	public void onPerceive() {
		remainingTimeInThePeriod -= SMAPeriod / 60;
		if (remainingTimeMin > remainingTimeInThePeriod) {
			remainingTimeInThePeriod = remainingTimeMin;
		}
		Collection<IAmakReceivedEnvelope<InfoMsg, IAmakMessageMetaData, AddressableAID>> allMyMgsReceived = getReceivedEnvelopesGivenMessageType(
				InfoMsg.class);
		allMyMgsReceived.forEach(msg -> totalConsumption += msg.getMessage().getContent().getValue());
		Results.addResult("total consumption (kW)", "real consumption", this.getEnvironment().getCurrentTimeInHours(),
				totalConsumption);
		Results.addResult("total consumption (kW)", "commitment", this.getEnvironment().getCurrentTimeInHours(),
				commitment);
		currentEnergyMismatch += (totalConsumption - commitment) * SMAPeriod / 3600;// SMAPeriod in second,
																					// currentEnergyMismatch in kW.h
		Results.addResult("currentEnergyMismatch (kWh)", "Default", this.getEnvironment().getCurrentTimeInHours(),
				currentEnergyMismatch);
	}

	// Decision method of the aggregator agent 
	@Override
	protected void onDecide() {
		this.calcCriticality();
	}

	// Action method of the aggregator agent
	@Override
	protected void onAct() {
		if (criticality > criticalityMin) {
			Issue issue = null;
			if (currentEnergyMismatch > 0) {
				issue = Issue.overConsumption;
			} else if (currentEnergyMismatch < 0) {
				issue = Issue.underConsumption;
			}
			Request request = new RequestAggregatotProsumer(issue, criticality, name,
					(GregorianCalendar) this.getEnvironment().getCurrentTime().clone(), null, 0);
			for (AID aid : neighborsAID) {
				this.sendMessage(request, aid);
			}
		}
	}

	
	// Initialization at the begining of agent's cycle
	@Override
	protected void onAgentCycleBegin() {
		super.onAgentCycleBegin();

		if (this.getEnvironment().getCurrentTime().after(endOfPeriod)) {
			current_study_period = current_study_period +1;
			endOfPeriod.add(Calendar.MINUTE, period);
			remainingTimeInThePeriod = period;
			currentEnergyMismatch = 0;
			commitment = Parameters.getConsumptionCommitment(current_study_period);
		}
	}

	@Override
	public String toString() {
		return "AggregatorAgent [name=" + name + "]";
	}

	// re-evaluating total BRP consumption
	public void changeConsumption(double power) {
		totalConsumption += power;
	}
	

	public void addNeighborAID(AID aid) {
		neighborsAID.add(aid);
	}

	// Calculating BRP agent's criticality
	private void calcCriticality() {		
		criticality = Math.abs(currentEnergyMismatch) / (remainingTimeInThePeriod * Pmax / 60);// remainingTimeInThePeriod/60
		
		if (criticality > criticalityMax) {
			criticality = criticalityMax;
		} else if (criticality < 0) {
			criticality = 0;
		}
		Results.addResult("Aggregator criticality", "Aggregator", this.getEnvironment().getCurrentTimeInHours(),
				criticality);
	}

}
