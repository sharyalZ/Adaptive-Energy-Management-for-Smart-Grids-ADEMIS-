/**
 * @author sharyal et jbblanc
 */

package AMAS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.GregorianCalendar;

import Communication.Issue;
import Communication.Request;
import Communication.RequestFromBus;
import Communication.RequestFromLine;
import Communication.UpdatedRequest;
import Interface.PowerFactoryCaller;
import Interface.PowerFactoryResults;
import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.aid.AddressableAID;
import fr.irit.smac.amak.messaging.IAmakEnvelope;
import fr.irit.smac.amak.messaging.IAmakMessageMetaData;
import fr.irit.smac.amak.messaging.IAmakReceivedEnvelope;
import ui.Results;



public class BusAgent extends MyAgent { 
	
	
	private String voltageSensorName;//Used to get the bus voltage from PowerFactoryResults
	private double voltage, past_voltage=0;
	private double criticality, criticalityMin=0 , anticipatedCriticality = 0;
	private double deltaVMax=0.1;
	private boolean supportedAgent = false;// Pour savoir si l'agent a déjà envoyé une requête
	private boolean VoltageRequestSentLastCycle = false, CheckProsumerResponse = false; // Boolean Variables to check the voltage response of 
																						//the attached bus prosumer and decide if ask other 
																						//prosumers for help or not
	private double Kbus = 0.1;// Utilisé dans le calcul de la criticité anticipée
	private double v_nom=1; //nominal voltage of the bus (pu)
	private double v_max=0.1; //(6/100)*v_nom; //rated deviation from the nominal voltage
	private double v_th=0.09; //(9/100)*v_nom; //threshold deviation from the nominal voltage 
	private List<AID> lineAIDs=new ArrayList<>();//Used to communicate with LineAgents
	private List<AID> prosumerAIDs=new ArrayList<>();//Used to communicate with EVAgents and PV
	
	private Request requestToSend; // Request to be forwarded to neighboring agents
	private AID AIDtoSource;//Not used in the actual system. BusAgents only transfers requests
	protected Collection<IAmakReceivedEnvelope<UpdatedRequest, IAmakMessageMetaData, AddressableAID>> allUpdatedRequestReceived;
	/**
	 * 
	 * @param amas
	 * @param aggregator
	 * @param busPath
	 * @param name
	 */
	// Initializing bus agent
	public BusAgent(MyAMAS amas, AggregatorAgent aggregator, String busPath, String name) {
		super(amas, name);
		String sensorPath=this.getEnvironment().getVoltageSensorsPaths(name);
		voltageSensorName=PowerFactoryCaller.GetComplete1Param(sensorPath, "loc_name");	
	}
	
	// Bus agent's percieve method
	@Override
	protected void onPerceive() {
		super.onPerceive();
		voltage=PowerFactoryResults.GetVoltage(voltageSensorName);
		Results.addResult("voltage", name, this.getEnvironment().getCurrentTimeInHours(), voltage);
		allRqtsReceived = this.getAllMessages();
		allUpdatedRequestReceived=this.getReceivedEnvelopesGivenMessageType(UpdatedRequest.class);//Updated requests always transferred
	}
	
	// Bus agent's decision method
	@Override
	protected void onDecide() {
		this.calcCriticality();  //agent's criticality
		if (!prosumerAIDs.isEmpty()){
			if(!allRqtsReceived.isEmpty()) {//If it received a request
				VoltageRequestSentLastCycle = false;
				CheckProsumerResponse = false;
				IAmakEnvelope moreCriticalRequestEnvelope = findMoreCriticalEnvelope();  // Finding most critical request
				if(anticipatedCriticality<(((Request) moreCriticalRequestEnvelope.getMessage()).getCriticality())){//It creates a new request from the received most critical request 
					Request pastRequest=(Request) moreCriticalRequestEnvelope.getMessage();
					requestToSend=new RequestFromBus (pastRequest.getRequestContent().getIssue(), pastRequest.getCriticality(), pastRequest.getRequestContent().getRequester(),  pastRequest.getRequestContent().getTimeOfRequest(), moreCriticalRequestEnvelope.getMessageSenderAID(), pastRequest.getDirection());
				}else if(anticipatedCriticality>criticalityMin){//If criticality of the bus is greater 
					manageIssue();					
				}
			}else if(anticipatedCriticality>criticalityMin){
					manageIssue();
			}
		}else {
			if(!allRqtsReceived.isEmpty()) {//If it received a request
				IAmakEnvelope moreCriticalRequestEnvelope = findMoreCriticalEnvelope();  // same function as above
				if(criticalityMin<(((Request) moreCriticalRequestEnvelope.getMessage()).getCriticality())){
					Request pastRequest=(Request) moreCriticalRequestEnvelope.getMessage();
					requestToSend=new RequestFromBus (pastRequest.getRequestContent().getIssue(), pastRequest.getCriticality(), pastRequest.getRequestContent().getRequester(),  pastRequest.getRequestContent().getTimeOfRequest(), moreCriticalRequestEnvelope.getMessageSenderAID(), pastRequest.getDirection());
				}
			}
		}
		
	}
	
	
	private void manageIssue() {
		if (!supportedAgent) {// If the bus was not supported, and if its criticality is higher than a
							  // threshold, it begins to be supported, 
			supportedAgent = true;
			
			// Generating request for either overvoltage or undervoltage issue depending on the voltage magnitude
			if(voltage>1) {
				requestToSend=new RequestFromBus (Issue.overVoltage, anticipatedCriticality, name, 
						(GregorianCalendar) this.getEnvironment().getCurrentTime().clone(), null, 0);
			}else if(voltage<1) {
				requestToSend=new RequestFromBus(Issue.underVoltage, anticipatedCriticality, name, 
						(GregorianCalendar) this.getEnvironment().getCurrentTime().clone(), null, 0);
			}

		} else {
			
			//Boolean variable to start checking prosumer response to voltage issue after 2 MAS cycles 
			if (VoltageRequestSentLastCycle == true) {
				CheckProsumerResponse = true;
			}			
			VoltageRequestSentLastCycle = true; // Boolean variable to indicate if a voltage request was sent in the last cycle
			
			// Generating request for either overvoltage or undervoltage issue depending on the voltage magnitude
			if(voltage>1) {
				requestToSend=new RequestFromBus (Issue.overVoltage, anticipatedCriticality, name, 
						(GregorianCalendar) this.getEnvironment().getCurrentTime().clone(), null, 0);
			}else if(voltage<1) {
				requestToSend=new RequestFromBus(Issue.underVoltage, anticipatedCriticality, name, 
						(GregorianCalendar) this.getEnvironment().getCurrentTime().clone(), null, 0);
			}
			//adding results to the output file
			Results.addResult("anticipated criticalities Bus", name, this.getEnvironment().getCurrentTimeInHours(),
					anticipatedCriticality);
		}
	}

	// Bus agent's action step
	@Override
	protected void onAct() {
		
		// if it is a prosumer bus then it is sending requests just to EV otherwise it is sending requests to connected lines and EVs
		if (!prosumerAIDs.isEmpty()){
			if(requestToSend!=null) {//If there is a request to send
				
				//If there was a request sent 2 MAS cycles back and still no desirable response from the connected EV than ask other EVs for help
				if ( ((requestToSend.getRequestContent().getIssue().equals(Issue.overVoltage)) && (voltage-past_voltage>0) && (VoltageRequestSentLastCycle) && (criticality>0) && (CheckProsumerResponse)) || ((requestToSend.getRequestContent().getIssue().equals(Issue.underVoltage)) && (voltage-past_voltage<0) && (VoltageRequestSentLastCycle)&& (criticality>0) && (CheckProsumerResponse)) ) {
					for(AID aid:lineAIDs) {
						this.sendRequest(requestToSend, aid);
					}
					for(AID aid:prosumerAIDs) {
						this.sendRequest(requestToSend, aid);
					}					
				}else {  //If no request was sent previously then send request to the EV 
					for(AID aid:prosumerAIDs) {	
						this.sendRequest(requestToSend, aid);	
					}					
				}							
			}			
			requestToSend=null;
		} else {
			if(requestToSend!=null) {//If there is a request to send, it sends it to connected lines and prosumers (EV agents and PV agents)
				for(AID aid:lineAIDs) {		
					this.sendRequest(requestToSend, aid);			
				}
				for(AID aid:prosumerAIDs) {			
					this.sendRequest(requestToSend, aid);			
				}
			}			
			requestToSend=null;
		}
		past_voltage = voltage;
	}
		
	@Override
	protected void onAgentCycleEnd() {
		
	}
	
	/**
	 * Add the aid of a connected line
	 * @param aid
	 */
	
	// Method to add neighboring electrical line agents
	public void addLineAID(AID aid) {
		lineAIDs.add(aid);
	}
	
	/**
	 * Add the aid of a connected prosumer (PV or EV)
	 * @param aid
	 */
	// Method to add neighboring prosumer agents
	public void addProsumerAID(AID aid) {
		prosumerAIDs.add(aid);
	}

	@Override
	public String toString() {
		return "BusAgent [name=" + name + "]";
	}
	
	/**
	 * criticality calculation. The criticality is not used in the actual system therefore the criticality of the bus is explicitly initialized to 0
	 */
	private void calcCriticality() {
		//Calculating Bus criticality
		criticality = Math.min(Math.max((1/(v_max - v_th))*(Math.abs(voltage-1)-v_th), 0), 1);

		if(criticality>1) {
			criticality=1;
		}else if(criticality<0){
			criticality=0;
		}
		
		//if it was supported before then requests based on updated criticality are transfered
		if (!supportedAgent) {
			anticipatedCriticality = criticality;
		}
		else {
			anticipatedCriticality += (Kbus) * (criticality - anticipatedCriticality);
		}
		
		//To set boolean variables to default values after voltage deviation was successfully avoided and bus voltage becomes stable 
		if (anticipatedCriticality < 0.0001) {
			VoltageRequestSentLastCycle = false;
			CheckProsumerResponse = false;
		}
		Results.addResult("Buses criticalities", name, this.getEnvironment().getCurrentTimeInHours(), criticality);
	}
	

	/**
	 * The AIDtoSource is used by the agent to identify when it send a request to a connected line if it sends a request towards the upstream of the grid (towards the external grid) or towards the downstream of the grid
	 * @param aIDtoSource
	 */
	public void setAIDtoSource(AID aIDtoSource) {
		AIDtoSource = aIDtoSource;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj.getClass().equals(BusAgent.class)) {
			BusAgent b=(BusAgent) obj;
			return name.equals(b.name);
		}else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return name.hashCode()*7;
	}

}
