package AMAS;

import Interface.DSLModels;
import fr.irit.smac.amak.aid.AID;

public class ProsumerAgent extends MyAgent {

	protected DSLModels dslmodels=new DSLModels();
	protected AID aggregatorAID;
	protected AID busAID;
	
	protected AID neighborAID1, neighborAID2;
	
	public ProsumerAgent(MyAMAS amas, String name) {
		super(amas, name);
	}
	
	public void setAggregatorAID(AID aggregatorAID) {
		this.aggregatorAID = aggregatorAID;
	}
	
	public void setBusAID(AID busAID) {
		this.busAID = busAID;
	}
	
	public void setNeighborAID1(AID neighborAID1) {
		this.neighborAID1 = neighborAID1;
	}
	
	public void setNeighborAID2(AID neighborAID2) {
		this.neighborAID2 = neighborAID2;
	}
	
	@Override
	protected void onPerceive() {
		super.onPerceive();
	}

}
