package Communication;

import java.util.GregorianCalendar;

import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.messaging.IAmakMessage;

public class RequestFromLine extends Request{
	

	public RequestFromLine(Issue issue, double criticality, String requester,  GregorianCalendar time, AID aid, double direction) {
		super(criticality, new RequestContent(issue, requester, time), aid, direction);
		
	}


}
