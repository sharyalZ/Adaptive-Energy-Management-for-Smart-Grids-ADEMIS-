package Communication;

import java.util.GregorianCalendar;

public class RequestContent {

	protected final Issue issue;//Type d'incident de l'agent envoyant cette requête (surcourant par exemple)
	protected final String requester;//Le nom de l'agent ayant initialement créé cette requête
	private final GregorianCalendar timeOfRequest;//Heure à laquelle la requête a été créé
	
	
	public RequestContent(Issue issue, String requester, GregorianCalendar time) {
		this.issue=issue;
		this.requester=requester;
		this.timeOfRequest=time;
	
	}
	
	public Issue getIssue() {
		return issue;
	}
	
	public String getRequester() {
		return requester;
	}
	
	public GregorianCalendar getTimeOfRequest() {
		return timeOfRequest;
	}
	
	@Override
	public String toString() {
		return "RequestContent [issue=" + issue + ", requester=" + requester +"]";
	}
	
	

}


