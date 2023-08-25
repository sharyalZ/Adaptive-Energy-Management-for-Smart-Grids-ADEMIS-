package AMAS;

import java.util.Collection;
import java.util.List;

import Communication.InfoMsg;
import Communication.RequestAggregatotProsumer;
import Communication.RequestFromBus;
import Interface.DSLModelType;
import Interface.PowerFactoryCaller;
import Interface.PowerFactoryParameters;
import Interface.PowerFactoryResults;
import fr.irit.smac.amak.aid.AddressableAID;
import fr.irit.smac.amak.messaging.IAmakMessageMetaData;
import fr.irit.smac.amak.messaging.IAmakReceivedEnvelope;
import ui.ConsumerType;
import ui.Parameters;
import ui.Results;

public class PVAgent extends ProsumerAgent {

	private double pvpower, past_pvpower = 0, rate = 1;
	private double criticality; // PV Criticality 
	private double surface;// In m²
	private double PVRate;// output rate

	public PVAgent(MyAMAS amas, String name, String pvPath) {
		super(amas, name);
		surface = Parameters.getPvSurface(name);
		PVRate = Parameters.getPvRate(name);
		initDSLModels(pvPath);
		PowerFactoryCaller.Set1ParamList(this.dslmodels.getModel(DSLModelType.ModelPV), "params", 0, surface);
		PowerFactoryCaller.Set1ParamList(this.dslmodels.getModel(DSLModelType.ModelPV), "params", 1, PVRate);
	}

	private void initDSLModels(String pvPath) {
		String CompositeModelPath = PowerFactoryCaller.Get1Param(pvPath, "c_pmod");
		List<String> pelm = PowerFactoryCaller.Get2Param(CompositeModelPath, "pelm");
		List<String> pblk = PowerFactoryCaller.Get2Param(CompositeModelPath, "pblk");
		for (int i = 0; i < pelm.size(); i++) {
			String DSLName = PowerFactoryCaller.GetComplete1Param(pblk.get(i), "loc_name");
			switch (DSLName) {
			case "Slot-Shedding":
				dslmodels.addModel(DSLModelType.Shedding_Path, pelm.get(i));
				break;
			case "Model-PV":
				dslmodels.addModel(DSLModelType.ModelPV, pelm.get(i));
				break;
			default: System.out.println("Unknown DSL mode : "+DSLName);
			}
		}
	}

	@Override
	public String toString() {
		return "PVAgent [surface=" + surface + ", PVRate=" + PVRate + "]";
	}

	@Override
	protected void onPerceive() {
		super.onPerceive();
		pvpower = PowerFactoryResults.GetPVPower(name);
		Results.addResult("PVPower", name, this.getEnvironment().getCurrentTimeInHours(), pvpower);

		Collection<IAmakReceivedEnvelope<RequestAggregatotProsumer, IAmakMessageMetaData, AddressableAID>> AggrRqtsReceived = getReceivedEnvelopesGivenMessageType(
				RequestAggregatotProsumer.class);

		Collection<IAmakReceivedEnvelope<RequestFromBus, IAmakMessageMetaData, AddressableAID>> BusRqtsReceived = getReceivedEnvelopesGivenMessageType(
				RequestFromBus.class);
	}

	@Override
	protected void onDecide() {
		this.calcCriticality();
	}

	@Override
	protected void onAct() {
		this.setRate(rate);
		InfoMsg msg = new InfoMsg("Change of the PV \"consumption\"", -(pvpower - past_pvpower));
		Results.addConsumptionByType(ConsumerType.PV, this.getEnvironment().getCurrentTimeInHours(), -past_pvpower);
		this.sendMessage(msg, aggregatorAID);
		past_pvpower = pvpower;
	}

	private void setRate(double r) {
		PowerFactoryParameters.addRatePV(this.dslmodels.getModel(DSLModelType.Shedding_Path), r);
	}

	private void calcCriticality() {
		if (rate != 1) {
			criticality = 0.5 * (1 + ((1 - rate) * pvpower / rate) / (pvpower / rate));
		} else {
			criticality = 0;
		}

		if (criticality > 1) {
			criticality = 1;
		} else if (criticality < 0) {
			criticality = 0;
		}
		Results.addResult("PV criticalities", name, this.getEnvironment().getCurrentTimeInHours(), criticality);
	}
}
