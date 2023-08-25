package Communication;

import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.messaging.IAmakMessage;

/**
 * Requ�te qui est envoy� lorsqu'ils rencontrent un probl�me. D'autres classe requ�tes h�ritent de cette classe mais les diff�rencier n'�taient pas n�cessaires
 * @author jbblanc
 *
 */
public class Request implements IAmakMessage {

	protected final double criticality;//criticit� (ou criticit� anticip�) de l'agent au moment de l'envoie de la requ�te
	protected final RequestContent requestContent;//D'autres informations peuvent �tre transmises
	protected final AID previousAidSender;//aid de l'agent qui avait pr�c�demment envoy� cette requ�te (dans le cas ou les agents la transf�re)
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
