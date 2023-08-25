package AMAS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import Interface.PowerFactoryCaller;
import Interface.PowerFactoryResults;
import ui.ConsumerType;
import ui.Results;

/**
 * Ce n'est pas un agent. A chaque cycle, il récupère la consommation de la
 * charge dont cet entité s'occupe et informe l'agent aggrégateur du changememnt
 * 
 * 
 *
 */
public class Load {

	// active entity, not an agent

	private String path;
	private String name;
	private double consumption, past_consumption = 0;
	private String zone;
	private GregorianCalendar currentTime;
	private AggregatorAgent aggregator;
	private List<String> zoneA = new ArrayList<>();
	private List<String> zoneB = new ArrayList<>();
	private List<String> zoneC = new ArrayList<>();
	private List<String> zoneD = new ArrayList<>();
	private List<String> zoneE = new ArrayList<>();
	private List<String> zoneF = new ArrayList<>();
	private List<String> zoneG = new ArrayList<>();
	private List<String> zoneH = new ArrayList<>();
	private List<String> zoneI = new ArrayList<>();

	/**
	 * 
	 * @param path       : PowerFactory path of the load
	 * @param time       : time of simulation
	 * @param aggregator : reference to the aggregator agent
	 */
	public Load(String path, GregorianCalendar time, AggregatorAgent aggregator) {
		this.path = path;
		name = PowerFactoryCaller.GetComplete1Param(path, "loc_name");// To get the load name in PowerFactory
		this.currentTime = time;
		this.aggregator = aggregator;
		zoneXDefinition();
	}

	/**
	 * Used to identify in which zone (A,B,C) is this load in the European low
	 * voltage test feeder
	 */
	private void zoneXDefinition() {
		// Zone A
		zoneA.add("LOAD24");
		zoneA.add("LOAD28");
		zoneA.add("LOAD27");
		zoneA.add("LOAD26");
		zoneA.add("LOAD44");
		zoneA.add("LOAD32");
		zoneA.add("LOAD38");
		zoneA.add("LOAD46");
		zoneA.add("LOAD48");
		zoneA.add("LOAD49");
		zoneA.add("LOAD42");
		zoneA.add("LOAD39");
		zoneA.add("LOAD40");
		zoneA.add("LOAD54");
		zoneA.add("LOAD51");
		zoneA.add("LOAD41");
		zoneA.add("LOAD45");
		zoneA.add("LOAD43");
		zoneA.add("LOAD47");
		zoneA.add("LOAD52");
		zoneA.add("LOAD55");
		zoneA.add("LOAD50");
		zoneA.add("LOAD53");
		zoneA.add("LOAD22");
		zoneA.add("LOAD20");
		zoneA.add("LOAD18");
		zoneA.add("LOAD23");
		zoneA.add("LOAD34");
		zoneA.add("LOAD30");
		zoneA.add("LOAD25");
		zoneA.add("LOAD31");
		zoneA.add("LOAD29");
		zoneA.add("LOAD37");
		zoneA.add("LOAD36");
		zoneA.add("LOAD33");
		zoneA.add("LOAD35");
		zoneA.add("LOAD19");
		zoneA.add("LOAD3");
		zoneA.add("LOAD1");
		zoneA.add("LOAD2");
		zoneA.add("LOAD6");
		zoneA.add("LOAD4");
		zoneA.add("LOAD5");
		zoneA.add("LOAD7");
		zoneA.add("LOAD8");
		zoneA.add("LOAD12");
		zoneA.add("LOAD9");
		zoneA.add("LOAD10");
		zoneA.add("LOAD11");
		zoneA.add("LOAD21");
		zoneA.add("LOAD14");
		zoneA.add("LOAD13");
		zoneA.add("LOAD15");
		zoneA.add("LOAD16");
		zoneA.add("LOAD17");
		
		//ZoneB
		zoneB.add("LOAD1(1)");
		zoneB.add("LOAD10(1)");
		zoneB.add("LOAD11(1)");
		zoneB.add("LOAD12(1)");
		zoneB.add("LOAD13(1)");
		zoneB.add("LOAD14(1)");
		zoneB.add("LOAD15(1)");
		zoneB.add("LOAD16(1)");
		zoneB.add("LOAD17(1)");
		zoneB.add("LOAD18(1)");
		zoneB.add("LOAD19(1)");
		zoneB.add("LOAD2(1)");
		zoneB.add("LOAD20(1)");
		zoneB.add("LOAD21(1)");
		zoneB.add("LOAD22(1)");
		zoneB.add("LOAD23(1)");
		zoneB.add("LOAD24(1)");
		zoneB.add("LOAD25(1)");
		zoneB.add("LOAD26(1)");
		zoneB.add("LOAD27(1)");
		zoneB.add("LOAD28(1)");
		zoneB.add("LOAD29(1)");
		zoneB.add("LOAD3(1)");
		zoneB.add("LOAD30(1)");
		zoneB.add("LOAD31(1)");
		zoneB.add("LOAD32(1)");
		zoneB.add("LOAD33(1)");
		zoneB.add("LOAD34(1)");
		zoneB.add("LOAD35(1)");
		zoneB.add("LOAD36(1)");
		zoneB.add("LOAD37(1)");
		zoneB.add("LOAD38(1)");
		zoneB.add("LOAD39(1)");
		zoneB.add("LOAD4(1)");
		zoneB.add("LOAD40(1)");
		zoneB.add("LOAD41(1)");
		zoneB.add("LOAD42(1)");
		zoneB.add("LOAD43(1)");
		zoneB.add("LOAD44(1)");
		zoneB.add("LOAD45(1)");
		zoneB.add("LOAD46(1)");
		zoneB.add("LOAD47(1)");
		zoneB.add("LOAD48(1)");
		zoneB.add("LOAD49(1)");
		zoneB.add("LOAD5(1)");
		zoneB.add("LOAD50(1)");
		zoneB.add("LOAD51(1)");
		zoneB.add("LOAD52(1)");
		zoneB.add("LOAD53(1)");
		zoneB.add("LOAD54(1)");
		zoneB.add("LOAD55(1)");
		zoneB.add("LOAD6(1)");
		zoneB.add("LOAD7(1)");
		zoneB.add("LOAD8(1)");
		zoneB.add("LOAD9(1)");
		
		//ZoneC
		
		zoneC.add("LOAD1(2)");
		zoneC.add("LOAD10(2)");
		zoneC.add("LOAD11(2)");
		zoneC.add("LOAD12(2)");
		zoneC.add("LOAD13(2)");
		zoneC.add("LOAD14(2)");
		zoneC.add("LOAD15(2)");
		zoneC.add("LOAD16(2)");
		zoneC.add("LOAD17(2)");
		zoneC.add("LOAD18(2)");
		zoneC.add("LOAD19(2)");
		zoneC.add("LOAD2(2)");
		zoneC.add("LOAD20(2)");
		zoneC.add("LOAD21(2)");
		zoneC.add("LOAD22(2)");
		zoneC.add("LOAD23(2)");
		zoneC.add("LOAD24(2)");
		zoneC.add("LOAD25(2)");
		zoneC.add("LOAD26(2)");
		zoneC.add("LOAD27(2)");
		zoneC.add("LOAD28(2)");
		zoneC.add("LOAD29(2)");
		zoneC.add("LOAD3(2)");
		zoneC.add("LOAD30(2)");
		zoneC.add("LOAD31(2)");
		zoneC.add("LOAD32(2)");
		zoneC.add("LOAD33(2)");
		zoneC.add("LOAD34(2)");
		zoneC.add("LOAD35(2)");
		zoneC.add("LOAD36(2)");
		zoneC.add("LOAD37(2)");
		zoneC.add("LOAD38(2)");
		zoneC.add("LOAD39(2)");
		zoneC.add("LOAD4(2)");
		zoneC.add("LOAD40(2)");
		zoneC.add("LOAD41(2)");
		zoneC.add("LOAD42(2)");
		zoneC.add("LOAD43(2)");
		zoneC.add("LOAD44(2)");
		zoneC.add("LOAD45(2)");
		zoneC.add("LOAD46(2)");
		zoneC.add("LOAD47(2)");
		zoneC.add("LOAD48(2)");
		zoneC.add("LOAD49(2)");
		zoneC.add("LOAD5(2)");
		zoneC.add("LOAD50(2)");
		zoneC.add("LOAD51(2)");
		zoneC.add("LOAD52(2)");
		zoneC.add("LOAD53(2)");
		zoneC.add("LOAD54(2)");
		zoneC.add("LOAD55(2)");
		zoneC.add("LOAD6(2)");
		zoneC.add("LOAD7(2)");
		zoneC.add("LOAD8(2)");
		zoneC.add("LOAD9(2)");
		
		//ZoneD
		
		zoneD.add("LOAD1(3)");
		zoneD.add("LOAD10(3)");
		zoneD.add("LOAD11(3)");
		zoneD.add("LOAD12(3)");
		zoneD.add("LOAD13(3)");
		zoneD.add("LOAD14(3)");
		zoneD.add("LOAD15(3)");
		zoneD.add("LOAD16(3)");
		zoneD.add("LOAD17(3)");
		zoneD.add("LOAD18(3)");
		zoneD.add("LOAD19(3)");
		zoneD.add("LOAD2(3)");
		zoneD.add("LOAD20(3)");
		zoneD.add("LOAD21(3)");
		zoneD.add("LOAD22(3)");
		zoneD.add("LOAD23(3)");
		zoneD.add("LOAD24(3)");
		zoneD.add("LOAD25(3)");
		zoneD.add("LOAD26(3)");
		zoneD.add("LOAD27(3)");
		zoneD.add("LOAD28(3)");
		zoneD.add("LOAD29(3)");
		zoneD.add("LOAD3(3)");
		zoneD.add("LOAD30(3)");
		zoneD.add("LOAD31(3)");
		zoneD.add("LOAD32(3)");
		zoneD.add("LOAD33(3)");
		zoneD.add("LOAD34(3)");
		zoneD.add("LOAD35(3)");
		zoneD.add("LOAD36(3)");
		zoneD.add("LOAD37(3)");
		zoneD.add("LOAD38(3)");
		zoneD.add("LOAD39(3)");
		zoneD.add("LOAD4(3)");
		zoneD.add("LOAD40(3)");
		zoneD.add("LOAD41(3)");
		zoneD.add("LOAD42(3)");
		zoneD.add("LOAD43(3)");
		zoneD.add("LOAD44(3)");
		zoneD.add("LOAD45(3)");
		zoneD.add("LOAD46(3)");
		zoneD.add("LOAD47(3)");
		zoneD.add("LOAD48(3)");
		zoneD.add("LOAD49(3)");
		zoneD.add("LOAD5(3)");
		zoneD.add("LOAD50(3)");
		zoneD.add("LOAD51(3)");
		zoneD.add("LOAD52(3)");
		zoneD.add("LOAD53(3)");
		zoneD.add("LOAD54(3)");
		zoneD.add("LOAD55(3)");
		zoneD.add("LOAD6(3)");
		zoneD.add("LOAD7(3)");
		zoneD.add("LOAD8(3)");
		zoneD.add("LOAD9(3)");
		
		//ZoneE
		zoneE.add("LOAD1(4)");
		zoneE.add("LOAD10(4)");
		zoneE.add("LOAD11(4)");
		zoneE.add("LOAD12(4)");
		zoneE.add("LOAD13(4)");
		zoneE.add("LOAD14(4)");
		zoneE.add("LOAD15(4)");
		zoneE.add("LOAD16(4)");
		zoneE.add("LOAD17(4)");
		zoneE.add("LOAD18(4)");
		zoneE.add("LOAD19(4)");
		zoneE.add("LOAD2(4)");
		zoneE.add("LOAD20(4)");
		zoneE.add("LOAD21(4)");
		zoneE.add("LOAD22(4)");
		zoneE.add("LOAD23(4)");
		zoneE.add("LOAD24(4)");
		zoneE.add("LOAD25(4)");
		zoneE.add("LOAD26(4)");
		zoneE.add("LOAD27(4)");
		zoneE.add("LOAD28(4)");
		zoneE.add("LOAD29(4)");
		zoneE.add("LOAD3(4)");
		zoneE.add("LOAD30(4)");
		zoneE.add("LOAD31(4)");
		zoneE.add("LOAD32(4)");
		zoneE.add("LOAD33(4)");
		zoneE.add("LOAD34(4)");
		zoneE.add("LOAD35(4)");
		zoneE.add("LOAD36(4)");
		zoneE.add("LOAD37(4)");
		zoneE.add("LOAD38(4)");
		zoneE.add("LOAD39(4)");
		zoneE.add("LOAD4(4)");
		zoneE.add("LOAD40(4)");
		zoneE.add("LOAD41(4)");
		zoneE.add("LOAD42(4)");
		zoneE.add("LOAD43(4)");
		zoneE.add("LOAD44(4)");
		zoneE.add("LOAD45(4)");
		zoneE.add("LOAD46(4)");
		zoneE.add("LOAD47(4)");
		zoneE.add("LOAD48(4)");
		zoneE.add("LOAD49(4)");
		zoneE.add("LOAD5(4)");
		zoneE.add("LOAD50(4)");
		zoneE.add("LOAD51(4)");
		zoneE.add("LOAD52(4)");
		zoneE.add("LOAD53(4)");
		zoneE.add("LOAD54(4)");
		zoneE.add("LOAD55(4)");
		zoneE.add("LOAD6(4)");
		zoneE.add("LOAD7(4)");
		zoneE.add("LOAD8(4)");
		zoneE.add("LOAD9(4)");
		
		//ZoneF
		zoneF.add("LOAD1(5)");
		zoneF.add("LOAD10(5)");
		zoneF.add("LOAD11(5)");
		zoneF.add("LOAD12(5)");
		zoneF.add("LOAD13(5)");
		zoneF.add("LOAD14(5)");
		zoneF.add("LOAD15(5)");
		zoneF.add("LOAD16(5)");
		zoneF.add("LOAD17(5)");
		zoneF.add("LOAD18(5)");
		zoneF.add("LOAD19(5)");
		zoneF.add("LOAD2(5)");
		zoneF.add("LOAD20(5)");
		zoneF.add("LOAD21(5)");
		zoneF.add("LOAD22(5)");
		zoneF.add("LOAD23(5)");
		zoneF.add("LOAD24(5)");
		zoneF.add("LOAD25(5)");
		zoneF.add("LOAD26(5)");
		zoneF.add("LOAD27(5)");
		zoneF.add("LOAD28(5)");
		zoneF.add("LOAD29(5)");
		zoneF.add("LOAD3(5)");
		zoneF.add("LOAD30(5)");
		zoneF.add("LOAD31(5)");
		zoneF.add("LOAD32(5)");
		zoneF.add("LOAD33(5)");
		zoneF.add("LOAD34(5)");
		zoneF.add("LOAD35(5)");
		zoneF.add("LOAD36(5)");
		zoneF.add("LOAD37(5)");
		zoneF.add("LOAD38(5)");
		zoneF.add("LOAD39(5)");
		zoneF.add("LOAD4(5)");
		zoneF.add("LOAD40(5)");
		zoneF.add("LOAD41(5)");
		zoneF.add("LOAD42(5)");
		zoneF.add("LOAD43(5)");
		zoneF.add("LOAD44(5)");
		zoneF.add("LOAD45(5)");
		zoneF.add("LOAD46(5)");
		zoneF.add("LOAD47(5)");
		zoneF.add("LOAD48(5)");
		zoneF.add("LOAD49(5)");
		zoneF.add("LOAD5(5)");
		zoneF.add("LOAD50(5)");
		zoneF.add("LOAD51(5)");
		zoneF.add("LOAD52(5)");
		zoneF.add("LOAD53(5)");
		zoneF.add("LOAD54(5)");
		zoneF.add("LOAD55(5)");
		zoneF.add("LOAD6(5)");
		zoneF.add("LOAD7(5)");
		zoneF.add("LOAD8(5)");
		zoneF.add("LOAD9(5)");
		
		//ZoneG
		zoneG.add("LOAD1(6)");
		zoneG.add("LOAD10(6)");
		zoneG.add("LOAD11(6)");
		zoneG.add("LOAD12(6)");
		zoneG.add("LOAD13(6)");
		zoneG.add("LOAD14(6)");
		zoneG.add("LOAD15(6)");
		zoneG.add("LOAD16(6)");
		zoneG.add("LOAD17(6)");
		zoneG.add("LOAD18(6)");
		zoneG.add("LOAD19(6)");
		zoneG.add("LOAD2(6)");
		zoneG.add("LOAD20(6)");
		zoneG.add("LOAD21(6)");
		zoneG.add("LOAD22(6)");
		zoneG.add("LOAD23(6)");
		zoneG.add("LOAD24(6)");
		zoneG.add("LOAD25(6)");
		zoneG.add("LOAD26(6)");
		zoneG.add("LOAD27(6)");
		zoneG.add("LOAD28(6)");
		zoneG.add("LOAD29(6)");
		zoneG.add("LOAD3(6)");
		zoneG.add("LOAD30(6)");
		zoneG.add("LOAD31(6)");
		zoneG.add("LOAD32(6)");
		zoneG.add("LOAD33(6)");
		zoneG.add("LOAD34(6)");
		zoneG.add("LOAD35(6)");
		zoneG.add("LOAD36(6)");
		zoneG.add("LOAD37(6)");
		zoneG.add("LOAD38(6)");
		zoneG.add("LOAD39(6)");
		zoneG.add("LOAD4(6)");
		zoneG.add("LOAD40(6)");
		zoneG.add("LOAD41(6)");
		zoneG.add("LOAD42(6)");
		zoneG.add("LOAD43(6)");
		zoneG.add("LOAD44(6)");
		zoneG.add("LOAD45(6)");
		zoneG.add("LOAD46(6)");
		zoneG.add("LOAD47(6)");
		zoneG.add("LOAD48(6)");
		zoneG.add("LOAD49(6)");
		zoneG.add("LOAD5(6)");
		zoneG.add("LOAD50(6)");
		zoneG.add("LOAD51(6)");
		zoneG.add("LOAD52(6)");
		zoneG.add("LOAD53(6)");
		zoneG.add("LOAD54(6)");
		zoneG.add("LOAD55(6)");
		zoneG.add("LOAD6(6)");
		zoneG.add("LOAD7(6)");
		zoneG.add("LOAD8(6)");
		zoneG.add("LOAD9(6)");
		
		//ZoneH
		zoneH.add("LOAD1(7)");
		zoneH.add("LOAD10(7)");
		zoneH.add("LOAD11(7)");
		zoneH.add("LOAD12(7)");
		zoneH.add("LOAD13(7)");
		zoneH.add("LOAD14(7)");
		zoneH.add("LOAD15(7)");
		zoneH.add("LOAD16(7)");
		zoneH.add("LOAD17(7)");
		zoneH.add("LOAD18(7)");
		zoneH.add("LOAD19(7)");
		zoneH.add("LOAD2(7)");
		zoneH.add("LOAD20(7)");
		zoneH.add("LOAD21(7)");
		zoneH.add("LOAD22(7)");
		zoneH.add("LOAD23(7)");
		zoneH.add("LOAD24(7)");
		zoneH.add("LOAD25(7)");
		zoneH.add("LOAD26(7)");
		zoneH.add("LOAD27(7)");
		zoneH.add("LOAD28(7)");
		zoneH.add("LOAD29(7)");
		zoneH.add("LOAD3(7)");
		zoneH.add("LOAD30(7)");
		zoneH.add("LOAD31(7)");
		zoneH.add("LOAD32(7)");
		zoneH.add("LOAD33(7)");
		zoneH.add("LOAD34(7)");
		zoneH.add("LOAD35(7)");
		zoneH.add("LOAD36(7)");
		zoneH.add("LOAD37(7)");
		zoneH.add("LOAD38(7)");
		zoneH.add("LOAD39(7)");
		zoneH.add("LOAD4(7)");
		zoneH.add("LOAD40(7)");
		zoneH.add("LOAD41(7)");
		zoneH.add("LOAD42(7)");
		zoneH.add("LOAD43(7)");
		zoneH.add("LOAD44(7)");
		zoneH.add("LOAD45(7)");
		zoneH.add("LOAD46(7)");
		zoneH.add("LOAD47(7)");
		zoneH.add("LOAD48(7)");
		zoneH.add("LOAD49(7)");
		zoneH.add("LOAD5(7)");
		zoneH.add("LOAD50(7)");
		zoneH.add("LOAD51(7)");
		zoneH.add("LOAD52(7)");
		zoneH.add("LOAD53(7)");
		zoneH.add("LOAD54(7)");
		zoneH.add("LOAD55(7)");
		zoneH.add("LOAD6(7)");
		zoneH.add("LOAD7(7)");
		zoneH.add("LOAD8(7)");
		zoneH.add("LOAD9(7)");
		
		//ZoneI
		zoneI.add("LOAD1(8)");
		zoneI.add("LOAD10(8)");
		zoneI.add("LOAD11(8)");
		zoneI.add("LOAD12(8)");
		zoneI.add("LOAD13(8)");
		zoneI.add("LOAD14(8)");
		zoneI.add("LOAD15(8)");
		zoneI.add("LOAD16(8)");
		zoneI.add("LOAD17(8)");
		zoneI.add("LOAD18(8)");
		zoneI.add("LOAD19(8)");
		zoneI.add("LOAD2(8)");
		zoneI.add("LOAD20(8)");
		zoneI.add("LOAD21(8)");
		zoneI.add("LOAD22(8)");
		zoneI.add("LOAD23(8)");
		zoneI.add("LOAD24(8)");
		zoneI.add("LOAD25(8)");
		zoneI.add("LOAD26(8)");
		zoneI.add("LOAD27(8)");
		zoneI.add("LOAD28(8)");
		zoneI.add("LOAD29(8)");
		zoneI.add("LOAD3(8)");
		zoneI.add("LOAD30(8)");
		zoneI.add("LOAD31(8)");
		zoneI.add("LOAD32(8)");
		zoneI.add("LOAD33(8)");
		zoneI.add("LOAD34(8)");
		zoneI.add("LOAD35(8)");
		zoneI.add("LOAD36(8)");
		zoneI.add("LOAD37(8)");
		zoneI.add("LOAD38(8)");
		zoneI.add("LOAD39(8)");
		zoneI.add("LOAD4(8)");
		zoneI.add("LOAD40(8)");
		zoneI.add("LOAD41(8)");
		zoneI.add("LOAD42(8)");
		zoneI.add("LOAD43(8)");
		zoneI.add("LOAD44(8)");
		zoneI.add("LOAD45(8)");
		zoneI.add("LOAD46(8)");
		zoneI.add("LOAD47(8)");
		zoneI.add("LOAD48(8)");
		zoneI.add("LOAD49(8)");
		zoneI.add("LOAD5(8)");
		zoneI.add("LOAD50(8)");
		zoneI.add("LOAD51(8)");
		zoneI.add("LOAD52(8)");
		zoneI.add("LOAD53(8)");
		zoneI.add("LOAD54(8)");
		zoneI.add("LOAD55(8)");
		zoneI.add("LOAD6(8)");
		zoneI.add("LOAD7(8)");
		zoneI.add("LOAD8(8)");
		zoneI.add("LOAD9(8)");

		if (zoneA.contains(name)) {
			zone = "zoneA";
		}
		if (zoneB.contains(name)) {
			zone = "zoneB";
		}
		if (zoneC.contains(name)) {
			zone = "zoneC";
		}
		if (zoneD.contains(name)) {
			zone = "zoneD";
		}
		if (zoneE.contains(name)) {
			zone = "zoneE";
		}
		if (zoneF.contains(name)) {
			zone = "zoneF";
		}
		if (zoneG.contains(name)) {
			zone = "zoneG";
		}
		if (zoneH.contains(name)) {
			zone = "zoneH";
		}
		if (zoneI.contains(name)) {
			zone = "zoneI";
		}
	}

	/**
	 * Read its new consumption in PowerFactory and inform the aggregator of the
	 * change
	 */
	public void checkConsumption() {
		consumption = PowerFactoryResults.GetPowerLoad(name);
		Results.addResult("Load consumption", name, timeInHour(), consumption);
		Results.addConsumptionByType(ConsumerType.Load, timeInHour(), consumption);
		Results.addConsumptionByZone(zone, timeInHour(), consumption);
		aggregator.changeConsumption(consumption - past_consumption);
		past_consumption = consumption;
	}

	private double timeInHour() {
		return currentTime.get(Calendar.HOUR_OF_DAY) + (double) currentTime.get(Calendar.MINUTE) / 60
				+ (double) currentTime.get(Calendar.SECOND) / 3600;
	}

	@Override
	public String toString() {
		return "Load [path=" + path + "]";
	}

}
