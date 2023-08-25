package AMAS;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import Communication.Request;
import fr.irit.smac.amak.CommunicatingAgent;
import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.messaging.IAmakEnvelope;

public class MyAgent extends CommunicatingAgent<MyAMAS, MyEnvironment> {

	protected String name;
	protected Collection<IAmakEnvelope> allRqtsReceived;

	protected DateFormat format;

	/**
	 * 
	 * @param amas
	 * @param name : agent name = name of the corresponding element in PowerFactory
	 */
	public MyAgent(MyAMAS amas, String name) {
		super(amas, (Object[]) null);
		this.name = name;
		format = new SimpleDateFormat("H:mm:ss");
	}

	@Override
	public String toString() {
		return "MyAgent [name=" + name + "]";
	}

	/**
	 * Method to select the envelop of the more critical request received by the
	 * agent from other agents
	 * 
	 * @return
	 */
	protected IAmakEnvelope findMoreCriticalEnvelope() {
		IAmakEnvelope moreCriticalRequestEnvelope = null;
		for (IAmakEnvelope envelope : allRqtsReceived) {
			if (moreCriticalRequestEnvelope == null) {
				moreCriticalRequestEnvelope = envelope;
			} else {
				Request request = (Request) envelope.getMessage();
				if (((Request) moreCriticalRequestEnvelope.getMessage()).getCriticality() < request.getCriticality()) {
					moreCriticalRequestEnvelope = envelope;
				}
			}
		}
		return moreCriticalRequestEnvelope;
	}

	/**
	 * Method to compare a EV critiaclity with a request based on weights crit :
	 * criticality of the agent, request : request from agent
	 * 
	 * @return
	 */
	protected Boolean compare_criticalities(Double crit, Request request) {
		double w1 = 1; // weight of aggregator
		double w2 = 1; // weight of ev
		double w3 = 1; // weight of line
		String requestRequestor = null;
		if (request.getRequestContent().getRequester().matches("(.*)Line(.*)")) {
			requestRequestor = "Line";
		}
		if (request.getRequestContent().getRequester().matches("(.*)EV(.*)")) {
			requestRequestor = "EV";
		}
		if (request.getRequestContent().getRequester().matches("(.*)Aggregator(.*)")) {
			requestRequestor = "Aggregator";
		}

		switch (requestRequestor) {
		case "Line":
			return (crit * w2 < request.getCriticality() * w3);
		case "Aggregator":
			return (crit * w2 < request.getCriticality() * w1);
		case "EV":
			return (crit * w2 < request.getCriticality() * w2);
		default:
			return false;
		}
	}

	/**
	 * Method to compare two requests
	 * 
	 * @return
	 */
	protected Boolean compare_requests(Request request1, Request request2) {
		double w1 = 1; // weight of aggregator
		double w2 = 1; // weight of ev
		double w3 = 1; // weight of line
		String requestRequestor1 = null;
		String requestRequestor2 = null;
		if (request1.getRequestContent().getRequester().matches("(.*)Line(.*)")
				|| request2.getRequestContent().getRequester().matches("(.*)Line(.*)")) {
			requestRequestor1 = "Line";
			requestRequestor2 = "Line";
		}
		if (request1.getRequestContent().getRequester().matches("(.*)Aggregator(.*)")
				|| request2.getRequestContent().getRequester().matches("(.*)Aggregator(.*)")) {
			requestRequestor1 = "Aggregator";
			requestRequestor2 = "Aggregator";
		}
		if (request1.getRequestContent().getRequester().matches("(.*)Aggregator(.*)")
				|| request2.getRequestContent().getRequester().matches("(.*)Aggregator(.*)")) {
			requestRequestor1 = "EV";
			requestRequestor2 = "EV";
		}

		double weight = 0;
		if (requestRequestor1 == "Line" || requestRequestor2 == "Line") {
			weight = w3;
		}
		if (requestRequestor1 == "Aggregator" || requestRequestor2 == "Aggregator") {
			weight = w1;
		}
		if (requestRequestor1 == "EV" || requestRequestor2 == "EV") {
			weight = w2;
		}
		return (request1.getCriticality() * weight < request2.getCriticality() * weight);
	}

	/**
	 * send a request to an other agent
	 * 
	 * @param requestToSend
	 * @param receiver      : AID type : address of the agent. The communication
	 *                      (and AIDs) is managed by AMAK
	 * @return
	 */
	public boolean sendRequest(Request requestToSend, AID receiver) {
		if (requestToSend.getPreviousAidSender() != null) {
			if (!requestToSend.getPreviousAidSender().equals(receiver)) {// If an agent transfers a request, it can't
																			// send it back to the agent that send it to
																			// him
				return super.sendMessage(requestToSend, receiver);
			} else {
				return false;
			}
		} else {
			return super.sendMessage(requestToSend, receiver);
		}
	}

	public void endSimulation() {
	}

	public String getName() {
		return name;
	}

}
