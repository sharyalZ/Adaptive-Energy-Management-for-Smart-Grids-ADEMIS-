package Exceptions;

public class UnknownSimulationException extends Exception {

	public UnknownSimulationException() {
		
	}

	public UnknownSimulationException(String arg0) {
		super(arg0);
	}

	public UnknownSimulationException(Throwable arg0) {
		super(arg0);
	}

	public UnknownSimulationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnknownSimulationException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
