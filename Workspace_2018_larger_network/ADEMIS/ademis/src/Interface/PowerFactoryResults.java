package Interface;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JOptionPane;

import ui.Results.Point;

/**
 * Après la simulation PowerFactory, les résultats sont exportés dans des fichiers csv. Cette classe lit ces fichiers et les stockes dans des maps associé au nom de l'objet associé (p.e. "EV1" pour le soe de la batterie du véhicule 1).
 * Les agents peuvent par la suite accéder à cette classes pour récupérer le résultat qui les intéresse lors de sa perception. 
 * @author jbblanc
 *
 */
public class PowerFactoryResults {

	private static Map<String,Map<Double,Double>> SoE=new LinkedHashMap<>();//Etat d'énergie des batteries
	private static Map<String,Map<Double,Double>> PVPower=new LinkedHashMap<>();//Puissance de production des PVs
	private static Map<Double,Double> ExternalGridPower=new LinkedHashMap<>();//power CONSUMED by the smart grid : if >0 it consumes more than it produces (in kW)
	private static Map<String,Map<Double,Double>> Voltage=new LinkedHashMap<>();
	//private static Map<String,Map<Double,Double>> Power=new LinkedHashMap<>();  //@vm
	private static Map<String,Map<Double,Double>> PLine=new LinkedHashMap<>();//Puissance active circulant dans les lignes
	private static Map<String,Map<Double,Double>> ILine=new LinkedHashMap<>();//Courant circulant dans les lignes
	private static Map<String,Map<Double,Double>> QLine=new LinkedHashMap<>();//Puissance réactive circulant dans les lignes
	private static Map<String,Map<Double,Double>> PowerLoad=new LinkedHashMap<>();//Puissances consommées par les charges non flexibles
	
	
	//Chemin des fichiers csv contenant les résultats exportés par PowerFactory
	private static String SoEfile;
	private static String PVfile;
	private static String ExternalGridfile;
	private static String Voltagefile, VoltageRes;
	//private static String Voltagefile, VoltageRes, PowerRes, Powerfile; //@vm
	private static String PLinefile, QLinefile, ILineFile;
	
	private static String PowerLoadFile;
	
	
	public PowerFactoryResults() {
		
	}
	
	
	/**
	 * 
	 * @param filePath : Path of the file to read
	 * @param map : map where the values read in the file should be stored
	 * @throws FileNotFoundException
	 */
	private static void readCSVFile(String filePath, Map<String,Map<Double,Double>> map) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(filePath)); 
	    Scanner lines=scanner.useDelimiter("\r");
	    String stringFirstLine=lines.next();
	    Scanner firstLine=new Scanner(stringFirstLine);
	    firstLine=firstLine.useDelimiter(";");
	    String str;
	    firstLine.next();
	    while(firstLine.hasNext()){
	    	str=firstLine.next();
	    	map.put(str, new HashMap<>());
	    }
	    firstLine.close();
	    scanner.next();
	    Scanner LastLine=null;
	    Scanner BeforeLastLine=null;
	    
	    while(scanner.hasNext()){
	    	String string=scanner.next();
	    	BeforeLastLine=LastLine;
	    		LastLine=new Scanner(string);
	    		
	    }
	    
	    LastLine=BeforeLastLine.useDelimiter(";");
	    int i=0;
	    double time=Double.parseDouble(LastLine.next());
	    double value;
	    while(LastLine.hasNext()){
	    	value=Double.parseDouble(LastLine.next());
	    	map.get((map.keySet().toArray()[i])).put(time, value);
	        i++;
	    }
	    scanner.close();
	}
	
	
	

	/**
	 * Read batteries state of energy from the SoEfile file and store it into the SoE map
	 * @throws FileNotFoundException
	 */
	public static void GetAllSoEs() throws FileNotFoundException{
		readCSVFile(SoEfile, SoE);
	}
	
	/**
	 * Read PV production from the PVfile file and store it into the PVPower map
	 * @throws FileNotFoundException
	 */
	public static void GetAllPVPowers() throws FileNotFoundException{
		readCSVFile(PVfile, PVPower);
	}
	
	/**
	 * Read buses voltage from the Voltagefile file and store it into the Voltage map
	 * @throws FileNotFoundException
	 */
	public static void GetAllVoltages() throws FileNotFoundException{
		readCSVFile(Voltagefile, Voltage);
	}
	
	/**@author mauryav
	 * Read buses power from the Powerfile file and store it into the Power map
	 * @throws FileNotFoundException
	 
	public static void GetAllPower() throws FileNotFoundException{
		readCSVFile(Powerfile, Power);
	}
	*/
	
	/**
	 * Read active power in lines from the PLinefile file and store it into the PLine map
	 * @throws FileNotFoundException
	 */
	public static void GetAllPLine() throws FileNotFoundException{
		readCSVFile(PLinefile, PLine);
	}
	
	/**
	 * Read current in lines from the ILineFile file and store it into the ILine map
	 * @throws FileNotFoundException
	 */
	public static void GetAllILine() throws FileNotFoundException{
		readCSVFile(ILineFile, ILine);
	}
	
	/**
	 * Read reactive power in lines from the QLinefile file and store it into the QLine map
	 * @throws FileNotFoundException
	 */
	public static void GetAllQLine() throws FileNotFoundException{
		readCSVFile(QLinefile, QLine);
	}
	
	/**
	 * Read  load consumption from the PowerLoadFile file and store it into the PowerLoad map
	 * @throws FileNotFoundException
	 */
	public static void GetAllPowerLoad() throws FileNotFoundException{
		readCSVFile(PowerLoadFile, PowerLoad);
	}
	
	

	/**
	 * Read  power exchanged with the external grid from the ExternalGridfile file and store it into the ExternalGridPower map
	 * @throws FileNotFoundException
	 */
	public static void GetExternalGridPower() throws FileNotFoundException{
		Scanner scanner = new Scanner(new File(ExternalGridfile));
	        Scanner lines=scanner.useDelimiter("\r");
	        String stringFirstLine=lines.next();
	        Scanner firstLine=new Scanner(stringFirstLine);
	        firstLine=firstLine.useDelimiter(";");
	        String str;
	        firstLine.close();
	        scanner.next();
	        Scanner LastLine=null;
	        Scanner BeforeLastLine=null;
	        while(scanner.hasNext()){
	        	String string=scanner.next();
	        	BeforeLastLine=LastLine;
	        	LastLine=new Scanner(string);
	        }
	        LastLine=BeforeLastLine.useDelimiter(";");
	        double time=Double.parseDouble(LastLine.next());
	        double gridPower;
	        gridPower=Double.parseDouble(LastLine.next());
	        ExternalGridPower.put(time, gridPower);
	        scanner.close();
	}
	
	/**
	 * Return last state of energy of the battery key
	 * @param key : battery name
	 * @return
	 */
	public static double GetSoE (String key) {
		if(SoE.containsKey(key)) {
			Map<?,?> EVSoE=SoE.get(key);
			double soe=(Double)EVSoE.get((EVSoE.keySet().toArray())[EVSoE.size()-1]);
			return soe;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetSoE) : "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	/**
	 * Return last consumption of the load key
	 * @param key : load name
	 * @return
	 */
	public static double GetPowerLoad (String key) {
		if(PowerLoad.containsKey(key)) {
			Map<?,?> powers=PowerLoad.get(key);
			double power=(Double)powers.get((powers.keySet().toArray())[powers.size()-1]);
			return power;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetPowerLoad) : "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	/**
	 * Return last production of the PV key
	 * @param key : PV name
	 * @return
	 */
	public static double GetPVPower (String key) {
		if(PVPower.containsKey(key)) {
			Map<?,?> pvpower=PVPower.get(key);
			double power=(Double)pvpower.get((pvpower.keySet().toArray())[pvpower.size()-1]);
			return power;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetPVPower) : "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	/**
	 * Return last voltage of the bus key
	 * @param key : bus name
	 * @return
	 */
	public static double GetVoltage (String key) {
		if(Voltage.containsKey(key)) {
			Map<?,?> voltage=Voltage.get(key);
			double v=(Double)voltage.get((voltage.keySet().toArray())[voltage.size()-1]);
			return v;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetVoltage) : "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	/** @author mauryav
	 * Return last power of the bus key
	 * @param key : bus name
	 * @return
	 
	public static double GetPower (String key) {
		if(Power.containsKey(key)) {
			Map<?,?> power=Power.get(key);
			double v=(Double)power.get((power.keySet().toArray())[power.size()-1]);
			return v;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetVoltage) : "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	*/
	
	public static double GetPLine(String key) {
		if(PLine.containsKey(key)) {
			Map<?,?> pline=PLine.get(key);
			double p=(Double)pline.get((pline.keySet().toArray())[pline.size()-1]);
			return p;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetPLine) : "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	public static double GetILine(String key) {
		if(ILine.containsKey(key)) {
			Map<?,?> iline=ILine.get(key);
			double i=(Double)iline.get((iline.keySet().toArray())[iline.size()-1]);
			return i;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetILine) : "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	public static double GetQLine(String key) {
		if(QLine.containsKey(key)) {
			Map<?,?> qline=QLine.get(key);
			double p=(Double)qline.get((qline.keySet().toArray())[qline.size()-1]);
			return p;
		}else {
			JOptionPane.showMessageDialog(null, "No such key (GetQLine): "+key, "Error : PowerFactoryResults", JOptionPane.ERROR_MESSAGE);
			return 0;
		}
	}
	
	
	
	
	/**
	 * Return last power exchanged with the external grid
	 * @return
	 */
	public static double getExternalPower() {
		double power=(Double)ExternalGridPower.get((ExternalGridPower.keySet().toArray())[ExternalGridPower.size()-1]);
		return power;
	}
	
	/**
	 * set file path for the communication file with PowerFactory for the power exchanged with the external grid
	 * @param externalGridfile
	 */
	public static void setExternalGridfile(String externalGridfile) {
		ExternalGridfile = externalGridfile;
	}
	
	public static void setPVfile(String pVfile) {
		PVfile = pVfile;
	}
	
	public static void setSoEfile(String soEfile) {
		SoEfile = soEfile;
	}
	
	public static void setVoltagefile(String voltagefile) {
		Voltagefile = voltagefile;
	}
	
	public static void setVoltageRes(String voltageRes) {
		VoltageRes = voltageRes;
	}
	
	
	/*
	public static void setPowerRes(String powerRes) {   //for bus power @vm
		PowerRes = powerRes;
	}
	*/
	
	public static void setPLinefile(String pLinefile) {
		PLinefile = pLinefile;
	}
	
	public static void setQLinefile(String qLinefile) {
		QLinefile = qLinefile;
	}
	
	public static void setPowerLoadFile(String powerLoadFile) {
		PowerLoadFile = powerLoadFile;
	}
	
	public static void setILinefile(String iLineFile) {
		ILineFile = iLineFile;
	}
	
	
	
	
	public static String getVoltageRes() {
		return VoltageRes;
	}

	/*
	public static String getPowerRes() {    //for bus power @vm
		return PowerRes;
	}
	*/
}
