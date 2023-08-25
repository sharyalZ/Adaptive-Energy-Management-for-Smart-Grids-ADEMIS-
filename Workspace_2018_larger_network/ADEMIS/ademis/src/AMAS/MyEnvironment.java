package AMAS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import Exceptions.PowerFactoryException;
import Interface.PowerFactoryCaller;
import Interface.PowerFactoryResults;
import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import ui.Parameters;

/**
 * Correspond à l'environnement du système multi-agent. Concrètement, il gère les simulation PowerFactory.
 * @author jbblanc
 *
 */
public class MyEnvironment extends Environment {
	
	//electric grid in PowerFactory
	
	private GregorianCalendar currentTime;
	private double initialHourOfTheDay, currentTimeInSeconds;

	
	private double SMAperiod, pfStepSize;
	
	private String communicationPath;
	
	private Map<String, String> voltageSensorsPaths;
	//private Map<String, String> powerSensorsPaths; //for power from bus @vm

	/**
	 * Initialize PowerFactory starting the Python process
	 * @param _scheduling : used by AMAK to manage the execution frequency of the cycle
	 * @param params : not used in ADEMIS. Proposed by AMAK to define some parameters
	 */
	public MyEnvironment(Scheduling _scheduling, Object[] params) {
		super(_scheduling, params);
		readParameters();
		powerFactoryInitialisation();
		timeInitialisation();
	}


	/**
	 * Initialize parameters of the environment
	 */
	private void readParameters() {
		SMAperiod=Parameters.getSMAPeriod();
		pfStepSize=Parameters.getPFTimeStep();
	}
	
	/**
	 * Initialize time variables
	 */
	private void timeInitialisation() {
		currentTime=Parameters.getStartTime();
		initialHourOfTheDay=currentTime.get(Calendar.HOUR_OF_DAY)+((double) currentTime.get(Calendar.MINUTE))/60+((double) currentTime.get(Calendar.SECOND))/3600;
		currentTimeInSeconds=initialHourOfTheDay*3600;
		Parameters.setStartTime((GregorianCalendar)currentTime.clone());
	}
	

	/**
	 * Start Python and PowerFactory using PowerFactoryCaller.InitPythonPowerFactory(..)
	 * Creation of voltageSensorsPaths map used by BusAgents to get the PF path of the voltage sensor
	 */
	private void powerFactoryInitialisation() {
		String workingDir = System.getProperty("user.dir");
		DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy HH-mm-ss");
		Calendar startSimulationDate = Calendar.getInstance();
		String simulationPath=workingDir+"\\Simulations\\"+dateFormat.format(startSimulationDate.getTime());
		Parameters.setSimulationPath(simulationPath);//Used to export into a file the PowerFactory console in case of warning or error in PowerFactory
		voltageSensorsPaths=new HashMap<>();
		
		try {
			PowerFactoryCaller.InitPythonPowerFactory(Parameters.getGridName(), simulationPath);//Start PowerFactory
			PowerFactoryCaller.GetResultsFiles();
			List<String> voltageSensors;
			List<String> powerSensors;
			voltageSensors=PowerFactoryCaller.Get2Param(PowerFactoryResults.getVoltageRes(), "element");
			voltageSensors.remove(0);//delete the first element : AllCalculation
			for(String sensor:voltageSensors) {
				String busPath;
				busPath=PowerFactoryCaller.Get1Param(sensor, "pbusbar");
				String busName;
				busName=PowerFactoryCaller.GetComplete1Param(busPath, "loc_name");
				voltageSensorsPaths.put(busName, sensor);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		//to get the bus power from PF//
		powerSensorsPaths=new HashMap<>(); //for power from bus @vm
		try {
			PowerFactoryCaller.InitPythonPowerFactory(Parameters.getGridName(), simulationPath);//Start PowerFactory
			PowerFactoryCaller.GetResultsFiles();
			
			List<String> powerSensors;
			powerSensors=PowerFactoryCaller.Get2Param(PowerFactoryResults.getPowerRes(), "element");
			powerSensors.remove(0);//delete the first element : AllCalculation
			for(String sensor:powerSensors) {
				String busPath;
				busPath=PowerFactoryCaller.Get1Param(sensor, "pbusbar");
				String busName;
				busName=PowerFactoryCaller.GetComplete1Param(busPath, "loc_name");
				
				powerSensorsPaths.put(busName, sensor);   //for power from bus @vm
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
	}
	
	
	
	/**
	 * At the beginning of each cycle, a PowerFactory simulation is run
	 */
	public void onCycleBegin()  {
		communicationPath=Parameters.getCommunicationPath();//Used to find files used to communicate from JAVA to PF
//		System.out.println("==>communicationPath"+communicationPath);
		try {
			PowerFactoryCaller.SimPowerFactoryScript(pfStepSize,currentTimeInSeconds,currentTimeInSeconds+SMAperiod,communicationPath+"\\DefinePevs.csv", communicationPath+"\\DefineSoE0.csv", communicationPath+"\\DefineRatePVs.csv" );//Execution of a PF simulation 
		}catch(PowerFactoryException e){//In case of error or warning in PowerFactory
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Simulation error : "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, "See "+Parameters.getSimulationPath()+" for more informations", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		currentTimeInSeconds+=SMAperiod;
		currentTime.add(Calendar.SECOND, (int) SMAperiod);
		try {//Extract results from the simulation into the PowerFactoryResults class
			PowerFactoryResults.GetAllSoEs();
			PowerFactoryResults.GetAllPVPowers();
			PowerFactoryResults.GetExternalGridPower();
			PowerFactoryResults.GetAllVoltages();
			PowerFactoryResults.GetAllPLine();
			PowerFactoryResults.GetAllILine();
			PowerFactoryResults.GetAllQLine();
			PowerFactoryResults.GetAllPowerLoad();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getVoltageSensorsPaths(String busName) {
		return voltageSensorsPaths.get(busName);
	}
	
	/*
	//get the bus power @vm//
	public String getPowerSensorsPaths(String busName) {
		return powerSensorsPaths.get(busName);
	}
	*/
	
	public double getCurrentTimeInHours() {
		return currentTimeInSeconds/3600;
	}
	
	
	public GregorianCalendar getCurrentTime() {
		return currentTime;
	}
}
