package ui;

public enum ConsumerType {
	EV("EV"), PV("PV"), Load("Load");
	
	private final String name;       

    private ConsumerType(String s) {
        name = s;
    }
    
    public String toString() {
        return this.name;
     }
}
