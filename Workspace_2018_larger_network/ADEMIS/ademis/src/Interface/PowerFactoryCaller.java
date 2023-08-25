package Interface;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import Exceptions.PowerFactoryException;
import fr.irit.smac.amak.tools.Profiler;
import fr.irit.smac.amak.ui.VUI;
import ui.Parameters;
import ui.Results;
import java.io.*;

/**
 * Classe utilisée comme interface avec PowerFactory par l'intermédiaire de Python.
 * Chaque méthode permet l'appel d'une méthode Python permettant de gérer une fonctionnalité de PowerFactory. Par exemple, la méthode InitPythonPowerFactory permet
 * d'initialiser la communication avec Python et PowerFactory
 * @author sharyal et jbblanc
 *
 */
public class PowerFactoryCaller {

	public static BufferedReader inp;
	public static BufferedWriter out;
	private static Runtime rt;
	private static Process pr;
	 
	
	
	public PowerFactoryCaller() throws IOException{
		
	}
	
	/**
	 * Initialisation of the communication between PowerFactory and JAVA. It executes the Python script "InterfaceJavaPowerFactory.py"
	 * @param grid : PowerFactory grid name ("SimpleGrid1.1.1", "SimpleGrid2.1.1", "SimpleGrid3.1.1", "SimpleGrid4.1.1", "Simplified_ELVT", "Simplified_ELVT_saved")
	 * @param simulationPath : used to export errors or warnings from PowerFactory
	 * @throws IOException
	 */
	
	public static void InitPythonPowerFactory(String grid, String simulationPath) throws IOException{
		final String dir = System.getProperty("user.dir");
		Path workingPath=Paths.get(dir);
        String pythonScriptPath=workingPath.getParent().toString()+"\\ademis\\InterfaceJavaPowerFactory2.py";
		String[] cmd = new String[2];
		cmd[0] = "C:\\Users\\szafar\\AppData\\Local\\Programs\\Python\\Python36-32\\python"; // python path (check version of installed python: python -V 3.6.3)//TODO Chemin en dur
		cmd[1] = pythonScriptPath;//File to execute
		//cmd[2] : Parameters. Not used in this system
		String pfprof = "C:\\Users\\szafar\\AppData\\Local\\Programs\\Python\\Python36-32\\python";
		// create runtime to execute external command (Python)
		rt = Runtime.getRuntime();
		pr = rt.exec(cmd);
		inp = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		out = new BufferedWriter( new OutputStreamWriter(pr.getOutputStream()));
		String str1=Read();
		if ( str1==null) System.out.println("**** lancement 2 ");
		pipe("Init");
		pipe(grid);
		pipe(simulationPath);
		String str=Read();
		if(str==null) {
			JOptionPane.showMessageDialog(null, "PowerFactory licence not found", "Error", JOptionPane.ERROR_MESSAGE);
		}else {
			if(str.equals("error")) {
				VUI.get().createString(0, 0, "PowerFactory already running");
				JOptionPane.showMessageDialog(null, "PowerFactory already running", "Error", JOptionPane.ERROR_MESSAGE);
			}else if(str.equals("None")) {
				VUI.get().createString(0, 0, "Project couldn't be found");
				JOptionPane.showMessageDialog(null, "Project couldn't be found", "Error", JOptionPane.ERROR_MESSAGE);
			}
			str=Read();
		}
		int a = 1; 
	}
	
	
	/**
	 * Simulation directly with Inc and Sim object from PowerFactory
	 * @param stepSize : stepsize from the PowerFactory simulation
	 * @param tStart : start time (in second) of the PowerFactory simulation
	 * @param tStop : end time (in second) of the PowerFactory simulation
	 * 
	 * This function is not used in the final version. the SimPowerFactoryScript method is used instead
	 */
	@Deprecated
	public static void SimPowerFactory(double stepSize,double tStart, double tStop) {
		try {
			pipe("Sim");
			pipe(String.valueOf(stepSize));
			pipe(String.valueOf(tStart));
			pipe(String.valueOf(tStop));
			String str=Read();
			str=Read();
			str=Read();
			str=Read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * cf Simulation script of Python in the manual
	 * @param stepSize : stepsize from the PowerFactory simulation
	 * @param tStart : start time (in second) of the PowerFactory simulation
	 * @param tStop : end time (in second) of the PowerFactory simulation
	 * @param PInputFile : path of the file to communicate the EVs charging power from JAVA to PowerFactory
	 * @param SoE0File : path of the file to communicate the initial state of energy of batteries from JAVA to PowerFactory
	 * @param RatepvsFile : path of the file to communicate the PV rate from JAVA to PowerFactory
	 * @throws IOException : throw by PowerFactoryParameters method called
	 * @throws PowerFactoryException : is throw if Python detects errors or warnings in the execution of the PowerFactory simulation
	 */
	public static void SimPowerFactoryScript (double stepSize,double tStart, double tStop, String PInputFile, String SoE0File, String RatepvsFile) throws IOException, PowerFactoryException{
			
			String str, res;
			Profiler.start("WriteFiles");
			PowerFactoryParameters.definePevs();
			PowerFactoryParameters.defineRatePVs();
			PowerFactoryParameters.defineSoE0();
			Results.addProfilerTime("WriteFiles", Profiler.end("WriteFiles"));
			pipe("ScriptSim");
			pipe(PInputFile);
			pipe(SoE0File);
			pipe(RatepvsFile);
			pipe(String.valueOf(stepSize));
			pipe(String.valueOf(tStart));
			pipe(String.valueOf(tStop));
			
			long startTime = System.nanoTime();
			res=Read();
			long stopTime = System.nanoTime();
			System.out.println("-----Execution Time:" + (stopTime - startTime));
			System.out.println("---PowerFactory Res: " + res);
			if (res==null)
			{   System.out.println("================> powerfactory relaunch  ==========");
				PowerFactoryCaller.InitPythonPowerFactory(Parameters.getGridName(), Parameters.getSimulationPath());
				PowerFactoryCaller.SimPowerFactoryScript(stepSize, tStart, tStop, PInputFile, SoE0File, RatepvsFile);
				return;
				//PowerFactoryCaller.RelauchPF(stepSize, tStart, tStop, PInputFile, SoE0File, RatepvsFile);
				}
			if(!res.equals("Simulation ok")) {
				throw new PowerFactoryException(res);
			}
			str=Read();
	}
	
	
	public static void RelauchPF(double stepSize,double tStart, double tStop, String PInputFile, String SoE0File, String RatepvsFile)throws IOException, PowerFactoryException {
		PowerFactoryCaller.InitPythonPowerFactory(Parameters.getGridName(), Parameters.getSimulationPath());
		PowerFactoryCaller.SimPowerFactoryScript(stepSize, tStart, tStop, PInputFile, SoE0File, RatepvsFile);
		return;
		
	}
	
	
	
	
	
	/**
	 * Get results files path defined in PowerFactory (results object) and send them to PowerFactoryResults class
	 */
	public static void GetResultsFiles() {
		try {
			pipe("GetResultsFiles");
			String SoEFile=Read();
			String PVFile=Read();
			String ExternalGridFile=Read();
			String VoltageFile=Read();
			String VoltageRes=Read();
			String PLineFile=Read();
			String QLineFile=Read();
			String ILineFile=Read();
			String PowerLoadFile=Read();
			PowerFactoryResults.setSoEfile(SoEFile);
			PowerFactoryResults.setPVfile(PVFile);
			PowerFactoryResults.setExternalGridfile(ExternalGridFile);
			PowerFactoryResults.setVoltagefile(VoltageFile);
			PowerFactoryResults.setVoltageRes(VoltageRes);
			PowerFactoryResults.setPLinefile(PLineFile);
			PowerFactoryResults.setQLinefile(QLineFile);
			PowerFactoryResults.setILinefile(ILineFile);
			PowerFactoryResults.setPowerLoadFile(PowerLoadFile);
			String str=Read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Unused in the final version. Change a battery charging power
	 * @param PInputObject : PowerFactory path of the battery
	 * @param power : new charging power
	 */
	@Deprecated
	public static void DefineBatteryState(String PInputObject, double power) {
		try {
			pipe("DefineBatteryState");
			pipe(PInputObject);
			pipe(String.valueOf(power));
			String str=Read();
			//System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * cf Set1Param of Python in the manual
	 * @param Object
	 * @param param
	 * @param index
	 * @param value
	 */
	public static void Set1ParamList(String Object, String param, int index, double value) {
		try {
			pipe("Set1Param");
			pipe(Object);
			pipe(param);
			pipe(String.valueOf(index));
			pipe(String.valueOf(value));
			String str=Read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * cf SetParam of Python in the manual
	 * @param Object
	 * @param param
	 * @param value
	 */
	public static void SetParam(String Object, String param, int value) {
		try {
			pipe("SetParam");
			pipe(Object);
			pipe(param);
			pipe(String.valueOf(value));
			String str=Read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * cf Get1Param of Python in the manual : must be used if the result is a PowerFactory path
	 * @param ObjectName
	 * @param param
	 * @return 
	 */
	public static String Get1Param(String ObjectName, String param) {
		try {
			pipe("Get1Param");
			pipe(ObjectName);
			pipe(param);
			String result=Read();
			String str=Read();
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * cf GetComplete1Param of Python in the manual : must be used if the result is not a PowerFactory path
	 * @param ObjectName
	 * @param param
	 * @return 
	 */
	public static String GetComplete1Param(String ObjectName, String param) {
		try {
			pipe("GetComplete1Param");
			System.out.println("1");
			pipe(ObjectName);
			System.out.println("2");
			pipe(param);
			System.out.println("3");
			String result=Read();
			System.out.println("4");
			String str=Read();
			System.out.println("5");
		System.out.println(result);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * cf Get2Param of Python in the manual : must be used if the result is a list including a PowerFactory path
	 * @param ObjectName
	 * @param param
	 * @return 
	 */
	public static List<String> Get2Param(String ObjectName, String param) {
		try {
			pipe("Get2Param");
			pipe(ObjectName);
			pipe(param);
			String msg=Read();
			List<String> params=new ArrayList<>();
			while(!msg.equals("End params")) {
				params.add(msg);
				msg=Read();
			}
			String str=Read();
			return params;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * cf GetComplete2Param of Python in the manual : must be used if the searched result is a not a PowerFactory path
	 * @param ObjectName
	 * @param param
	 * @return
	 */
	public static List<String> GetComplete2Param(String ObjectName, String param) {
		try {
			pipe("GetComplete2Param");
			pipe(ObjectName);
			pipe(param);
			String msg=Read();
			List<String> params=new ArrayList<>();
			while(!msg.equals("End params")) {
				params.add(msg);
				msg=Read();
			}
			String str=Read();
			return params;
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * cf GetEVs of Python in the manual
	 * @return list of EVs PowerFactory path
	 */
	public static List<String> GetEVs() {
		List<String> EVs=new ArrayList<>();
		try {
			pipe("GetEVs");
			String msg=Read();
			while(!msg.equals("End EVs")) {
				EVs.add(msg);
				msg=Read();
			}
			String str=Read();
			return EVs;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * cf GetBuses of Python in the manual
	 * @return list of buses PowerFactory path
	 */
	public static List<String> GetBuses() {
		List<String> Buses=new ArrayList<>();
		try {
			pipe("GetBuses");
			String msg=Read();
			while(!msg.equals("End Buses")) {
				Buses.add(msg);
				msg=Read();
			}
			String str=Read();
			return Buses;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * cf GetLines of Python in the manual
	 * @return list of lines PowerFactory path
	 */
	public static List<String> GetLines() {
		List<String> Lines=new ArrayList<>();
		try {
			pipe("GetLines");
			String msg=Read();
			while(!msg.equals("End Lines")) {
				Lines.add(msg);
				msg=Read();
			}
			String str=Read();
			return Lines;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * cf GetLoads of Python in the manual
	 * @return list of loads PowerFactory path
	 */
	public static List<String> GetLoads() {
		List<String> Loads=new ArrayList<>();
		try {
			pipe("GetLoads");
			String msg=Read();
			while(!msg.equals("End Loads")) {
				Loads.add(msg);
				msg=Read();
			}
			String str=Read();
			return Loads;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * cf GetPVs of Python in the manual
	 * @return list of PVs PowerFactory path
	 */
	public static List<String> GetPVs() {
		List<String> PVs=new ArrayList<>();
		try {
			pipe("GetPVs");
			String msg=Read();
			while(!msg.equals("End PVs")) {
				PVs.add(msg);
				msg=Read();
			}
			String str=Read();
			return PVs;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Deprecated
	public static String GetSoEResults() {
		try {
			pipe("GetSoE");
			String SoEfile=Read();
			String str=Read();
			return SoEfile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Deprecated
	public static String GetPVPowerResults() {
		try {
			pipe("GetPVPower");
			String PVfile=Read();
			String str=Read();
			return PVfile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Deprecated
	public static String GetExternalGridPower() {
		try {
			pipe("GetExternalGridPower");
			String ExternalGridfile=Read();
			String str=Read();
			return ExternalGridfile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	@Deprecated
	public static void DefinePVProfile(String irr_input_Path, String filePath, double stepSize) {
		try {
			pipe("DefinePVProfile");
			pipe(irr_input_Path);
			pipe(filePath);
			pipe(String.valueOf(stepSize));
			String str=Read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * cf EVConnection of Python in the manual
	 * @param EVPath : PowerFactory path of the EV to connect/disconnect
	 * @param connection : =true to connect the EV, =false to disconnect it
	 */
	public static void EVConnection(String EVPath, boolean connection) {
		try {
			pipe("EVConnection");
			pipe(EVPath);
			pipe(String.valueOf(connection));
			String str=Read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Deprecated
	public static void DefinePevs(String fileName) {
		try {
			pipe("DefinePevs");
			pipe(fileName);
			String res=Read();
			System.out.println(res);
			String str=Read();
			System.out.println(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * cf OpenPF of Python in the manual
	 */
	public static void OpenPF() {//connetion=true to connection the EV, =flase to disconnect it
		try {
			pipe("OpenPF");
			String str=Read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Write a message in the pipe to communicate with Python. This message is read from Python using the input() method
	 * @param msg : message to write
	 * @throws IOException
	 */
	public static void pipe(String msg) throws IOException{
	    out.write( msg + "\n" );
	    out.flush();
	}
	
	/**
	 * Read a message from the pipe send by Python using the print() method
	 * @return 
	 * @throws IOException
	 */
	public static String Read() throws IOException{
		String line = inp.readLine();
		return line;
	}

}
