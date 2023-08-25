package ui;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * Classe permettant de stocker tous les paramètres de simulation (heures de
 * départ et d'arrivé des véhicules, valeur de Kline et Kagg dans les calculs de
 * criticité des agents...) Toutes ces données sont accessibles en écriture et
 * lecture en statique
 * 
 * @author jbblanc
 *
 */
public class Parameters {

	private static String pythonPath;
	private static String OpenDSSPath;
	private static String simulationPath;
	private static String communicationPath;
	private static String endSimulationPath;

	private static String firstBusName;

	private static List<String> selectedEVs;

	private static Map<String, Boolean> activatedEVs, activatedPVs;

	private static Map<String, GregorianCalendar> evDeparture;
	private static Map<String, GregorianCalendar> evArrival;
	private static Map<String, Double> evSoE0;
	private static Map<String, Double> pvSurface;
	private static Map<String, Double> pvRate;

	private static double currentMinimumRate, defaultRatedCurrent;
	private static Map<String, Double> additionnalLineConfigByName;
	private static Map<String, Double> additionnalLineConfigByType;

	private static boolean plotResults, intelligentEVs;

	private static double SMAPeriod, PFTimeStep, criticalityRate;

	private static GregorianCalendar startTime, endTime;

	private static String gridName;

	private static final String NEW_LINE_SEPARATOR = "\n";

	private static List<Double> consumptionCommitment;

	private static double PmaxAggregator;

	private static double KLine;

	private static int aggregatorPeriodCommitment;
	// private static int aggregatorPeriodCommitment=15;//Possibilité de le mettre
	// en dur

	private static double criticalityMinAggregatorAgent, criticalityMinEVAgent, criticalityMinLineAgent;

	public Parameters() {

	}

	//////////////////////////// Getter ////////////////////////////
	public static String getPythonPath() {
		return pythonPath;
	}

	public static double getCurrentMinimumRate() {
		return currentMinimumRate;
	}

	public static double getDefaultRatedCurrent() {
		return defaultRatedCurrent;
	}

	public static String getOpenDSSPath() {
		return OpenDSSPath;
	}

	public static String getFirstBusName() {
		return firstBusName;
	}

	public static Map<String, Double> getAdditionnalLineConfigByName() {
		return additionnalLineConfigByName;
	}

	public static Map<String, Double> getAdditionnalLineConfigByType() {
		return additionnalLineConfigByType;
	}

	public static double getPFTimeStep() {
		return PFTimeStep;
	}

	public static double getSMAPeriod() {
		return SMAPeriod;
	}

	public static GregorianCalendar getStartTime() {
		return startTime;
	}

	public static GregorianCalendar getEndTime() {
		return endTime;
	}

	public static boolean getPlotResults() {
		return plotResults;
	}

	public static String getEndSimulationPath() {
		return endSimulationPath;
	}

	public static String getGridName() {
		return gridName;
	}

	public static double getCriticalityRate() {
		return criticalityRate;
	}

	public static String getSimulationPath() {
		return simulationPath;
	}

	public static int getNbEVs() {
		if (evDeparture.size() == activatedEVs.size() && activatedEVs.size() == evSoE0.size()) {
			return evDeparture.size();
		} else {
			return -1;
		}

	}

	public static double getConsumptionCommitment(int study_period) {
		
		return (study_period-1 < consumptionCommitment.size()) ? consumptionCommitment.get(study_period-1) : (int) 0;
	}

	public static boolean getIntelligentEVs() {
		return intelligentEVs;
	}

	public static double getPmaxAggregator() {
		return PmaxAggregator;
	}

	public static double getKLine() {
		return KLine;
	}

	public static GregorianCalendar getEvDeparture(String name) {
		if (evDeparture.containsKey(name)) {
			return evDeparture.get(name);

		} else {
			JOptionPane.showMessageDialog(null, "Unknown vehicle : getEvDeparture", "Error [" + Parameters.class + "]",
					JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public static GregorianCalendar getEvArrival(String name) {
		if (evArrival.containsKey(name)) {
			return evArrival.get(name);
		} else {
			JOptionPane.showMessageDialog(null, "Unknown vehicle : getEvArrival", "Error [" + Parameters.class + "]",
					JOptionPane.ERROR_MESSAGE);
		}
		return null;
	}

	public static double getEvSoE0(String name) {
		if (evSoE0.containsKey(name)) {
			return evSoE0.get(name);
		} else {
			// JOptionPane.showMessageDialog(null, "Unknown vehicle : getEvSoE0", "Error
			// ["+Parameters.class+"]", JOptionPane.ERROR_MESSAGE);
		}
		return 0;
	}

	public static boolean getEVActivated(String name) {
		if (activatedEVs.containsKey(name)) {
			return activatedEVs.get(name);
		} else {
			// JOptionPane.showMessageDialog(null, "Unknown vehicle : getEVActivated",
			// "Error ["+Parameters.class+"]", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	public static boolean getPVActivated(String name) {
		if (activatedPVs.containsKey(name)) {
			return activatedPVs.get(name);
		} else {
			// JOptionPane.showMessageDialog(null, "Unknown PV", "Error",
			// JOptionPane.ERROR_MESSAGE);
			return false;
		}

	}

	public static double getPvSurface(String name) {
		if (pvSurface.containsKey(name)) {
			return pvSurface.get(name);
		} else {
			JOptionPane.showMessageDialog(null, "Unknown pv", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return 0;
	}

	public static double getPvRate(String name) {
		if (pvRate.containsKey(name)) {
			return pvRate.get(name);
		} else {
			JOptionPane.showMessageDialog(null, "Unknown pv", "Error", JOptionPane.ERROR_MESSAGE);
		}
		return 0;
	}

	public static double getCriticalityMinAggregatorAgent() {
		return criticalityMinAggregatorAgent;
	}

	public static double getCriticalityMinEVAgent() {
		return criticalityMinEVAgent;
	}

	public static double getCriticalityMinLineAgent() {
		return criticalityMinLineAgent;
	}

	public static int getAggregatorPeriodCommitment() {
		return aggregatorPeriodCommitment;
	}

	public static String getCommunicationPath() {
		return communicationPath;
	}

	//////////////////////////// Setter ////////////////////////////
	public static void setPythonPath(String pythonPath) {
		Parameters.pythonPath = pythonPath;
	}

	public static void setOpenDSSPath(String openDSSPath) {
		OpenDSSPath = openDSSPath;
	}

	public static void setAdditionnalLineConfigByName(Map<String, Double> additionnalLineConfigByName) {
		Parameters.additionnalLineConfigByName = additionnalLineConfigByName;
	}

	public static void setAdditionnalLineConfigByType(Map<String, Double> additionnalLineConfigByType) {
		Parameters.additionnalLineConfigByType = additionnalLineConfigByType;
	}

	public static void setCurrentMinimumRate(double currentMinimumRate) {
		Parameters.currentMinimumRate = currentMinimumRate;
	}

	public static void setEndSimulationPath(String endSimulationPath) {
		Parameters.endSimulationPath = endSimulationPath;
	}

	public static void setPFTimeStep(double pFTimeStep) {
		PFTimeStep = pFTimeStep;
	}

	public static void setSMAPeriod(double sMAPeriod) {
		SMAPeriod = sMAPeriod;
	}

	public static void setDefaultRatedCurrent(double defaultRatedCurrent) {
		Parameters.defaultRatedCurrent = defaultRatedCurrent;
	}

	public static void setStartTime(GregorianCalendar startTime) {
		Parameters.startTime = startTime;
	}

	public static void setEndTime(GregorianCalendar endTime) {
		Parameters.endTime = endTime;
	}

	public static void setPlotResults(boolean plotResults) {
		Parameters.plotResults = plotResults;
	}

	public static void setGridName(String gridName) {
		Parameters.gridName = gridName;
	}

	public static void setCriticalityRate(double criticalityRate) {
		Parameters.criticalityRate = criticalityRate;
	}

	public static void setSimulationPath(String simulationPath) {
		Parameters.simulationPath = simulationPath;
	}

	public static void setIntelligentEVs(boolean intelligentEVs) {
		Parameters.intelligentEVs = intelligentEVs;
	}

	public static void setConsumptionCommitment(List<Double> consumptionCommitment) {
		Parameters.consumptionCommitment = consumptionCommitment;
	}

	public static void setPmaxAggregator(double pmaxAggregator) {
		PmaxAggregator = pmaxAggregator;
	}

	public static void setEvArrival(Map<String, GregorianCalendar> evArrival) {
		Parameters.evArrival = evArrival;
	}

	public static void setEvDeparture(Map<String, GregorianCalendar> evDeparture) {
		Parameters.evDeparture = evDeparture;
	}

	public static void setEvSoE0(Map<String, Double> evSoE0) {
		Parameters.evSoE0 = evSoE0;
	}

	public static void setPvRate(Map<String, Double> pvRate) {
		Parameters.pvRate = pvRate;
	}

	public static void setPvSurface(Map<String, Double> pvSurface) {
		Parameters.pvSurface = pvSurface;
	}

	public static void setActivatedEVs(Map<String, Boolean> activatedEVs) {
		Parameters.activatedEVs = activatedEVs;
	}

	public static void setActivatedPVs(Map<String, Boolean> activatedPVs) {
		Parameters.activatedPVs = activatedPVs;
	}

	public static void setKLine(double kLine) {
		KLine = kLine;
	}

	public static void setFirstBusName(String firstBusName) {
		Parameters.firstBusName = firstBusName;
	}

	public static void setCriticalityMinAggregatorAgent(double criticalityMinAggregatorAgent) {
		Parameters.criticalityMinAggregatorAgent = criticalityMinAggregatorAgent;
	}

	public static void setCriticalityMinEVAgent(double criticalityMinEVAgent) {
		Parameters.criticalityMinEVAgent = criticalityMinEVAgent;
	}

	public static void setCriticalityMinLineAgent(double criticalityMinLineAgent) {
		Parameters.criticalityMinLineAgent = criticalityMinLineAgent;
	}

	public static void setAggregatorPeriodCommitment(int aggregatorPeriodCommitment) {
		Parameters.aggregatorPeriodCommitment = aggregatorPeriodCommitment;
	}

	public static void setCommunicationPath(String communicationPath) {
		Parameters.communicationPath = communicationPath;
	}

	public static void writeParameters(String simulation) {

		String path = Parameters.getEndSimulationPath();

		File folder = new File(path);
		folder.mkdirs();
		DateFormat format = new SimpleDateFormat("HH:mm:ss");

		try {

			FileWriter fileWriter = new FileWriter(path + "\\" + "Parameters.txt");
			fileWriter.append("Grid : " + gridName);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("startTime : " + format.format(startTime.getTime()));
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("endTime : " + format.format(endTime.getTime()));
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("PFTimeStep : " + PFTimeStep + " s");
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("SMAPeriod : " + SMAPeriod + " s");
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("criticalityRate : " + criticalityRate);
			fileWriter.append(NEW_LINE_SEPARATOR);
			for (String ev : evDeparture.keySet()) {
				fileWriter.append("evDeparture : " + ev + " : " + format.format(evDeparture.get(ev).getTime()));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (String ev : evArrival.keySet()) {
				fileWriter.append("evArrival : " + ev + " : " + format.format(evArrival.get(ev).getTime()));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (String ev : evSoE0.keySet()) {
				fileWriter.append("evSoE0 : " + ev + " : " + evSoE0.get(ev));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (String ev : activatedEVs.keySet()) {
				fileWriter.append("activated EV : " + ev + " : " + activatedEVs.get(ev));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (String pv : activatedPVs.keySet()) {
				fileWriter.append("activated PV : " + pv + " : " + activatedPVs.get(pv));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (String pv : pvRate.keySet()) {
				fileWriter.append("pvRate : " + pv + " : " + pvRate.get(pv));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			for (String pv : pvSurface.keySet()) {
				fileWriter.append("pvSurface : " + pv + " : " + pvSurface.get(pv));
				fileWriter.append(NEW_LINE_SEPARATOR);
			}
			fileWriter.append("consumptionCommitment : " + consumptionCommitment);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("consumption Commitment period : " + aggregatorPeriodCommitment);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("intelligentEVs : " + intelligentEVs);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("PmaxAggregator : " + PmaxAggregator);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("K line : " + KLine);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("criticalityMin EV : " + criticalityMinEVAgent);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("criticalityMin line : " + criticalityMinLineAgent);
			fileWriter.append(NEW_LINE_SEPARATOR);
			fileWriter.append("criticalityMin aggregator : " + criticalityMinAggregatorAgent);
			fileWriter.append(NEW_LINE_SEPARATOR);

			fileWriter.flush();
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
