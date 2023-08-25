package Communication;

import java.util.GregorianCalendar;

import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.messaging.IAmakMessage;

/**
 * Elle h�rite de la classe mais est plut�t un message classique plus qu'une requ�te. Elle permet � l'agent de continuer � informer de sa situation les agents pouvant potentiellement l'aider. A partir du moment o� la ligne envoie une requ�te, elle enverra une UpdatedRequest � chaque cycle. Ce sera � modifier par la suite.
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
