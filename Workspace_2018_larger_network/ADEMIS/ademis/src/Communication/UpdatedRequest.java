package Communication;

import java.util.GregorianCalendar;

import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.messaging.IAmakMessage;

/**
 * Elle hérite de la classe mais est plutôt un message classique plus qu'une requête. Elle permet à l'agent de continuer à informer de sa situation les agents pouvant potentiellement l'aider. A partir du moment où la ligne envoie une requête, elle enverra une UpdatedRequest à chaque cycle. Ce sera à modifier par la suite.
 * @author jbblanc
 *
 */
public class UpdatedRequest extends Request {

	public UpdatedRequest(Issue issue, double criticality, String requester,  GregorianCalendar time, AID aid, double direction) {
		super(criticality, new RequestContent(issue, requester, time), aid, direction);
	}
	
	public UpdatedRequest(RequestContent request, double criticality, AID aid, double direction) {
		super(criticality, request, aid, direction);
	}

	@Override
	public String toString() {
		return "UpdatedRequest ["+super.toString()+"]";
	}
	
	

}
