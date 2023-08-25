package Communication;

import fr.irit.smac.amak.messaging.IAmakMessage;

public class MyMsg implements IAmakMessage {
	private final Content content;

	public MyMsg(String msg, double value) {
		this.content = new Content(msg, value);
	}

	public Content getContent() {
        return content;
    }
	
}
