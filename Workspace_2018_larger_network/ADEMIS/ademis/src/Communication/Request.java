package Communication;

import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.messaging.IAmakMessage;

/**
 * Requête qui est envoyé lorsqu'ils rencontrent un problème. D'autres classe requêtes héritent de cette classe mais les différencier n'étaient pas nécessaires
 * @author jbblanc
 *
 */
public class Request implements IAmakMessage {

	protected final double criticality;//criticité (ou criticité anticipé) de l'agent au moment de l'envoie de la requête
	protected final RequestContent requestContent;//D'autres informations peuvent être transmises
	protected final AID previousAidSender;//aid de l'agent qui avait précédemment envoyé cette requête (dans le cas ou les agents la transfère)
	public double direction;
	public Request(double criticality, RequestContent requestContent, AID aid, double direction) {
		
		this.criticality=criticality;
		this.requestContent=requestContent;
		this.previousAidSender=aid;
		this.direction = direction;
		
	}
	
	public double getCriticality() {
		return criticality;
	}
	
	public RequestContent getRequestContent() {
		return requestContent;
	}
	
	public AID getPreviousAidSender() {
		return previousAidSender;
	}
	
	public double getDirection() {
		return direction;
	}
	
	@Override
	public String toString() {
		//if (direction == 0 ) {
		return "Request [criticality=" + criticality + ", requestContent=" + requestContent + ", previousAidSender="
				+ previousAidSender + " direction=" +direction +"]";
		//}else {
			//return "Request ***[criticality=" + criticality + ", requestContent=" + requestContent + ", previousAidSender="
				//	+ previousAidSender + " direction=" +direction +"]";
	//	}
	}
	
	

}
