package Communication;

public class Content {

	private String msg;
	private double value;
	
	public Content(String msg, double value) {
		this.msg=msg;
		this.value=value;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Content [msg=" + msg + ", value=" + value + "]";
	}
	
	

}
