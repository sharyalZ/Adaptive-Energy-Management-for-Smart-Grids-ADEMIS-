package ui;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;

/**
 * Cette classe permet de gérer les résultats de la simulation. La principale fonctionnalité (et la plus générique) est le stockage des résultats dans la map results qui peut être affichée avec LxPlot en cours de simulation et exportée à la fin de la simulation dans des fichiers csv.
 * @author jbblanc
 *
 */
public class Results {

	private static Map<String,Map<String,ArrayList<Point>>> results=new HashMap<>();//Contient tous les résultats de base sous la forme (nom du graphique, nom de la courbe, abscisse, ordonnée) dans une map (nom du graphique) de map (nom de la courbe)
	private static Map<String,Point> lastResults=new HashMap<>();//résultats obtenus lors du dernier cycle pour que ceux qui nous intéresses soit affichés
	private static Map<String,Map<String,Boolean>> plotPlot=new HashMap<>();//non utilisé
	private static Map<String,Map<String,Double>> SoEs=new HashMap<>();//Etat d'énergie des batteries au départ des véhicules
	private static Map<String, Mean> profiler=new HashMap<>();
	
	
	private static Map<ConsumerType, Double> currentConsumptionByType = new EnumMap<>(ConsumerType.class);//Pour avoir la consommation par type (EV, charges non flexibles)
	private static Map<String, Double> currentConsumptionByZone = new HashMap<>();////Pour avoir la consommation par zone (ZONEA, ZONEB, ZONEB)
	
	private static Map<String, List<String>> activePlots=new HashMap<>();//Pour identifier les courbes qu'on veut afficher en cours de simulation
	
	private static final String COMMA_DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	private static boolean plotResults=false;
	
	private static double currentTime;

	
	private static double soeMin=1;
	
	private static int cycle=-1;
	private static final int cycleMax=10;//Tous les "cycleMax" cycles, la map results est exportée dans des fichiers csv
	
	private static String filePath;
	
	/**
	 * Ajout d'un point dans les résultats
	 * @param plot : nom du graphique
	 * @param Serie : nom de la courbe
	 * @param x : abscisse
	 * @param val : ordonnée
	 */
	public static void addResult(String plot, String Serie, double x,double val){
		if(!results.containsKey(plot)) {
			Map<String,ArrayList<Point>> map = new HashMap<>();
			results.put(plot, map);
		}
		if(!results.get(plot).containsKey(Serie)) {
			ArrayList<Point> list =new ArrayList<>();
			results.get(plot).put(Serie, list);
		}
		results.get(plot).get(Serie).add(new Point(x,val));
		if(plotResults) {
			LxPlot.getChart(plot).add(Serie, x,val);
		}
		
		lastResults.put(plot+":"+Serie, new Point(x,val));
		
	}
	
	@Deprecated
	public static void plotResults(){
		for(Map.Entry<String,Map<String,ArrayList<Point>>> entryPlot : results.entrySet()) {
		    String plotName = entryPlot.getKey();
		    Map<String, ArrayList<Point>> plotValue = entryPlot.getValue();
		    for(Map.Entry<String,ArrayList<Point>> entrySerie : plotValue.entrySet()) {
		    	String serieName = entrySerie.getKey();
			    ArrayList<Point> serieValue = entrySerie.getValue();
			    for(int i=0; i<serieValue.size(); i++){
        			Point p=serieValue.get(i);
        			LxPlot.getChart(plotName,ChartType.LINE,true).add(serieName,p.x, p.y);
        		}
		    }
		}
	}
	
	public static void addSoE(String departure, String ev, double id, double soe) {
		if(SoEs.containsKey(departure)) {
			SoEs.get(departure).put(ev, soe);
		}else {
			Map<String, Double> map=new HashMap<>();
			map.put(ev, soe);
			SoEs.put(departure, map);
		}
		if(soe<soeMin) {
			soeMin=soe;
		}
		//LxPlot.getChart(departure, ChartType.BAR).add(ev, id, soe);
	}
	
	public static void addConsumptionByType(ConsumerType type, double time, double y) {
		double currentConsumption=0;
		currentTime=time;
		if(currentConsumptionByType.containsKey(type)){
			currentConsumption=currentConsumptionByType.get(type);
		}
		currentConsumption+=y;
		currentConsumptionByType.put(type, currentConsumption);
	}
	
	public static void addConsumptionByZone(String zone, double time, double y) {
		double currentConsumption=0;
		currentTime=time;
		if(currentConsumptionByZone.containsKey(zone)){
			currentConsumption=currentConsumptionByZone.get(zone);
		}
		currentConsumption+=y;
		currentConsumptionByZone.put(zone, currentConsumption);
	}
	
	@Deprecated
	public static void endSimu() {
		//VUI.get("end Simu").createString(-400, 0, "soe min : "+soeMin);
		plotResults();
		/*VUI.get("end Simu").createString(-400, 100, "PF mean pf simulation time : "+Results.getMeanProfilerTime("PFSimu")+" ms");
		VUI.get("end Simu").createString(0, 100, "total pf simulation time : "+Results.getTotalProfilerTime("PFSimu")+" ms");
		VUI.get("end Simu").createString(-400, 120, "mean get results time : "+Results.getMeanProfilerTime("GetAllResults")+" ms");
		VUI.get("end Simu").createString(-0, 120, "total get results time : "+Results.getTotalProfilerTime("GetAllResults")+" ms");
		VUI.get("end Simu").createString(-400, 140, "mean SMA cycle time : "+Results.getMeanProfilerTime("SMAcycle")+" ms");
		VUI.get("end Simu").createString(-0, 140, "total SMA cycle time : "+Results.getTotalProfilerTime("SMAcycle")+" ms");
		//VUI.get("end Simu").createString(-400, 160, "mean EVAgent cycle time : "+Results.getMeanProfilerTime("EVAgent")+" ms");
		//VUI.get("end Simu").createString(-0, 160, "total EVAgent cycle time : "+Results.getTotalProfilerTime("EVAgent")+" ms");
		VUI.get("end Simu").createString(-400, 180, "mean PVAgent cycle time : "+Results.getMeanProfilerTime("PVAgent")+" ms");
		VUI.get("end Simu").createString(-0, 180, "total PVAgent cycle time : "+Results.getTotalProfilerTime("PVAgent")+" ms");
		VUI.get("end Simu").createString(-400, 200, "mean AggregatorAgent cycle time : "+Results.getMeanProfilerTime("AggregatorAgent")+" ms");
		VUI.get("end Simu").createString(-0, 200, "total AggregatorAgent cycle time : "+Results.getTotalProfilerTime("AggregatorAgent")+" ms");
		VUI.get("end Simu").createString(-400, 220, "mean environmentonCycleBegin cycle time : "+Results.getMeanProfilerTime("environmentonCycleBegin")+" ms");
		VUI.get("end Simu").createString(-0, 220, "total environmentonCycleBegin cycle time : "+Results.getTotalProfilerTime("environmentonCycleBegin")+" ms");
		//VUI.get("end Simu").createString(-400, 240, "mean DefineSoE cycle time : "+Results.getMeanProfilerTime("DefineSoE")+" ms");
		//VUI.get("end Simu").createString(-0, 240, "total DefineSoE cycle time : "+Results.getTotalProfilerTime("DefineSoE")+" ms");
		VUI.get("end Simu").createString(-400, 260, "mean WriteFiles cycle time : "+Results.getMeanProfilerTime("WriteFiles")+" ms");
		VUI.get("end Simu").createString(-0, 260, "total WriteFiles cycle time : "+Results.getTotalProfilerTime("WriteFiles")+" ms");
		//VUI.get("end Simu").createString(-400, 200, "mean GetSoEs cycle time : "+Results.getMeanProfilerTime("GetSoEs")+" ms");
		//VUI.get("end Simu").createString(-0, 200, "total GetSoEs cycle time : "+Results.getTotalProfilerTime("GetSoEs")+" ms");
		//VUI.get("end Simu").createString(-400, 220, "mean GetPVPower cycle time : "+Results.getMeanProfilerTime("GetPVPower")+" ms");
		//VUI.get("end Simu").createString(-0, 220, "total GetPVPower cycle time : "+Results.getTotalProfilerTime("GetPVPower")+" ms");
		//VUI.get("end Simu").createString(-400, 240, "mean GetExternalGridPower cycle time : "+Results.getMeanProfilerTime("GetExternalGridPower")+" ms");
		//VUI.get("end Simu").createString(-0, 240, "total GetExternalGridPower cycle time : "+Results.getTotalProfilerTime("GetExternalGridPower")+" ms");
		
		
		VUI.get("end Simu").createString(-400, 300, "mean TotalCycle cycle time : "+Results.getMeanProfilerTime("TotalCycle")+" ms");
		VUI.get("end Simu").createString(-0, 300, "total TotalCycle cycle time : "+Results.getTotalProfilerTime("TotalCycle")+" ms");*/
	}
	
	/*public static void saveResults(String simulation) {
		
		
		FileWriter fileWriter = null;
		String filePath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\Results\\ADEMISResults\\"+simulation+"\\";
		try {
			
			
			for(Map.Entry<String,Map<String,ArrayList<Point>>> entryPlot : results.entrySet()) {
			    String plotName = entryPlot.getKey();
			    //System.out.println("plotName : "+plotName);
			    Map<String, ArrayList<Point>> plotValue = entryPlot.getValue();
			    for(Map.Entry<String,ArrayList<Point>> entrySerie : plotValue.entrySet()) {
			    	String serieName = entrySerie.getKey();
			    	//System.out.println("serieName : "+serieName);
			    	String fileName=filePath+plotName;
			    	File file=new File(fileName);
			    	file.mkdirs();
			    	fileWriter = new FileWriter(fileName+"\\"+serieName+".csv");
				    ArrayList<Point> serieValue = entrySerie.getValue();
				    for(int i=0; i<serieValue.size(); i++){
	        			Point p=serieValue.get(i);
	        			//LxPlot.getChart(plotName,ChartType.LINE,true).add(serieName,p.x, p.y);
	        			fileWriter.append(String.valueOf(p.x));
	        			fileWriter.append(COMMA_DELIMITER);
	        			fileWriter.append(String.valueOf(p.y));
	        			fileWriter.append(NEW_LINE_SEPARATOR);
	        		}
				    fileWriter.flush();
					fileWriter.close();
				    
			    }
			}
			JOptionPane.showMessageDialog(null, "Results written", "Info", JOptionPane.INFORMATION_MESSAGE);
		}catch(Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
		}finally {
			try {
			fileWriter.close();
			} catch (IOException e) {
			System.out.println("Error while flushing/closing fileWriter !!!");
			JOptionPane.showMessageDialog(null, "Error while flushing/closing fileWriter !!!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			}
		}

	}*/
	
	public static void saveResults(String simulation) {
		//String originPath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\tmp\\";
		//String destinationPath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\Results\\ADEMISResults\\"+simulation+"\\";
		final String dir = System.getProperty("user.dir");
		Path workingPath=Paths.get(dir);
        String communicationPath=workingPath.getParent().getParent().toString();
		String destinationPath=communicationPath+"\\Results\\ADEMISResults\\"+simulation+"\\";
		File tmpFolder=new File(filePath);
		ArrayList<File> listFolders = new ArrayList<File>(Arrays.asList(tmpFolder.listFiles()));
		for(File folder:listFolders) {
			ArrayList<File> listFiles = new ArrayList<File>(Arrays.asList(folder.listFiles()));
			File destionationFolder=new File(destinationPath+folder.getName());
			destionationFolder.mkdirs();
			for(File file:listFiles) {
				try {
					Files.copy(file.toPath(), new File(destinationPath+"\\"+folder.getName()+"\\"+file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
	}
	
	
	/*public static  void updateParameters() {
		plotResults=Parameters.getPlotResults();
		
		
	}*/
	
	public static void addProfilerTime(String name, long time) {
		if(profiler.containsKey(name)) {
			profiler.get(name).addValue(time);
		}else {
			profiler.put(name, new Mean(time));
		}
		if(name.equals("PFSimu")) {
			LxPlot.getChart("PF simu time (in ms)").add(profiler.get(name).getNbVal(), (double)time/1000000);
		}
		
	}
	
	public static double getMeanProfilerTime(String name) {
		return (double)profiler.get(name).mean()/1000000;//in ms
	}
	
	public static double getTotalProfilerTime(String name) {
		return (double)profiler.get(name).getTot()/1000000;//in ms
	}
	
	
	public static void writeResults(String path, String simulationTime, Calendar endTime) {
		File folder=new File(path);
    	folder.mkdirs();
    	DateFormat format = new SimpleDateFormat("HH:mm:ss");
    	
    	try {
    		
    	FileWriter fileWriter = new FileWriter(path+"\\"+"Results.txt");
			//fileWriter.append("end Time : "+);
			//fileWriter.append(NEW_LINE_SEPARATOR);
    	fileWriter.append("SoE min : "+soeMin);
    	fileWriter.append(NEW_LINE_SEPARATOR);
    	fileWriter.append(NEW_LINE_SEPARATOR);
    	
    	fileWriter.append("endTime : "+format.format(endTime.getTime()));
    	fileWriter.append(NEW_LINE_SEPARATOR);
    	fileWriter.append(NEW_LINE_SEPARATOR);
    	
    	fileWriter.append("Simulation time : "+simulationTime);
    	fileWriter.append(NEW_LINE_SEPARATOR);
    	for(Map.Entry<String,Mean> entryProfiler : profiler.entrySet()) {
	    	String profilerName = entryProfiler.getKey();
		    Mean meanObject = entryProfiler.getValue();
		    fileWriter.append(profilerName+"               mean : "+Results.getMeanProfilerTime(profilerName)+" ms, total : "+Results.getTotalProfilerTime(profilerName)+" ms");
		    fileWriter.append(NEW_LINE_SEPARATOR);
    	
    	}
    	
    	
			
		fileWriter.flush();
		fileWriter.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Map<String, List<String>> getResults() {
		Map<String, List<String>> myResults=new HashMap<>();
		for(String chart:results.keySet()) {
			List<String> list=new ArrayList<>();
			for(String plot:results.get(chart).keySet()) {
				list.add(plot);
			}
			myResults.put(chart, list);
		}
		
		return myResults;
	}
	
	public static void writeLine(String path, String line) throws IOException{
		FileWriter fileWriter = new FileWriter(path, true);
		fileWriter.append(line);
    	fileWriter.append(NEW_LINE_SEPARATOR);
    	fileWriter.close();
	}
	
	public static void plotResult(String plotName, String serieName) {
		ArrayList<Point> serieValue = results.get(plotName).get(serieName);
		for(int i=0; i<serieValue.size(); i++){
        	Point p=serieValue.get(i);
        	LxPlot.getChart(plotName,ChartType.LINE,true).add(serieName,p.x, p.y);
        }
		List<String> myPLotList;
		if(activePlots.containsKey(plotName)) {
			myPLotList=activePlots.get(plotName);
		}else {
			myPLotList=new ArrayList<>();
			activePlots.put(plotName, myPLotList);
		}
		myPLotList.add(serieName);
		System.out.println("activePlots : "+activePlots);
	}
	
	public static void endCycle() {
		double totalConsumption=0;
		for(ConsumerType type:currentConsumptionByType.keySet()) {
			Results.addResult("consumption by type", type.toString(), currentTime, currentConsumptionByType.get(type).doubleValue());
			totalConsumption+=currentConsumptionByType.get(type).doubleValue();
		}
		Results.addResult("consumption by type", "total", currentTime, totalConsumption);
		currentConsumptionByType.clear();
		
		for(String zone:currentConsumptionByZone.keySet()) {
			System.out.println(zone);
			Results.addResult("consumption by zone", zone.toString(), currentTime, currentConsumptionByZone.get(zone).doubleValue());
			totalConsumption+=currentConsumptionByZone.get(zone).doubleValue();
		}
		currentConsumptionByZone.clear();
		
		
		
		
		for (String plot:activePlots.keySet()) {
			for(String serie:activePlots.get(plot)) {
				if(lastResults.containsKey(plot+":"+serie)) {
					Point p=lastResults.get(plot+":"+serie);
					LxPlot.getChart(plot).add(serie, p.x, p.y);
				}
				
			}
		}
		lastResults.clear();
		
		if(cycle==-1) {
			//String filePath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\tmp\\";
			
			
			final String dir = System.getProperty("user.dir");
			Path workingPath=Paths.get(dir);
	        String communicationPath=workingPath.getParent().getParent().toString();
			filePath=communicationPath+"\\tmp\\";
			File file = new File(filePath);
			deleteFile(file);
		}
		
		cycle++;
		if(cycle>=cycleMax) {
			System.out.println("reinit memory");
			saveLastResults();
			cycle=0;
		}
		
	}
	
	public static void deleteFile(File element) {
	    if (element.isDirectory()) {
	        for (File sub : element.listFiles()) {
	            deleteFile(sub);
	        }
	    }
	    element.delete();
	}
	
	public static void saveLastResults() {
		
		
		FileWriter fileWriter = null;
		//String filePath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\tmp\\";
		try {
			
			
			for(Map.Entry<String,Map<String,ArrayList<Point>>> entryPlot : results.entrySet()) {
			    String plotName = entryPlot.getKey();
			    //System.out.println("plotName : "+plotName);
			    Map<String, ArrayList<Point>> plotValue = entryPlot.getValue();
			    for(Map.Entry<String,ArrayList<Point>> entrySerie : plotValue.entrySet()) {
			    	String serieName = entrySerie.getKey();
			    	//System.out.println("serieName : "+serieName);
			    	String fileName=filePath+plotName;
			    	File file=new File(fileName);
			    	file.mkdirs();
			    	fileWriter = new FileWriter(fileName+"\\"+serieName+".csv", true);
				    ArrayList<Point> serieValue = entrySerie.getValue();
				    for(int i=0; i<serieValue.size(); i++){
	        			Point p=serieValue.get(i);
	        			//LxPlot.getChart(plotName,ChartType.LINE,true).add(serieName,p.x, p.y);
	        			fileWriter.append(String.valueOf(p.x));
	        			fileWriter.append(COMMA_DELIMITER);
	        			fileWriter.append(String.valueOf(p.y));
	        			fileWriter.append(NEW_LINE_SEPARATOR);
	        		}
				    fileWriter.flush();
					fileWriter.close();
				    
			    }
			}
		}catch(Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
		}finally {
			try {
				if(fileWriter!=null) {
					fileWriter.close();
				}
			
			} catch (IOException e) {
			System.out.println("Error while flushing/closing fileWriter !!!");
			JOptionPane.showMessageDialog(null, "Error while flushing/closing fileWriter !!!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			}
		}
		results.clear();

	}
	
	
	public static void deletePlot(String plot, String serie) {
		activePlots.get(plot).remove(serie);
	}
	
	public static String getFilePath() {
		final String dir = System.getProperty("user.dir");
		Path workingPath=Paths.get(dir);
        String communicationPath=workingPath.getParent().getParent().toString();
		filePath=communicationPath+"\\tmp\\";
		return filePath;
	}
	
	
	public static class Point{
		public double x;
		public double y;
		public Point(double x, double y) {
			this.x=x;
			this.y=y;
		}
	}
	
	public static class Mean{
		private int nbVal;
		private long Tot;
		public Mean(long value) {
			nbVal=1;
			Tot=value;
		}
		public void addValue(long value) {
			nbVal++;
			Tot+=value;
		}
		public long mean() {
			return Tot/nbVal;
		}
		public long getTot() {
			return Tot;
		}
		public int getNbVal() {
			return nbVal;
		}
	}


}
