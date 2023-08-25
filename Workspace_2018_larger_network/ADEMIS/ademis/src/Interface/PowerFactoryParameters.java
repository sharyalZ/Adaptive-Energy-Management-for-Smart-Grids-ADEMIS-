package Interface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ui.Parameters;


/**
 * Permet de stocker les variables d'entrées de la simulation de PF (etat d'énergie initial de la batterie pour une simulation de PF (et pas de la simulation comlpète du système), la puissance de recharge de l'EV décidé par l'agent et le taux de production du PV décidé (toujours égale à 1 dans le système))
 * dans la  RAM pendant le cycle de l'AMAS. A la fin dy cycle complet de l'AMAS, ces variables sont exportées dans des fichiers csv qui seront lus par PowerFactory par le script pour les envoyer aux objets les utilsant pour la simulation. Par exemple l'agent EV1 décide de la puissance de recharge, lors de la lecture du fichier cette puissance est définie dans l'élément "P-input EV1" du "Composite Model EV1"
 * @author jbblanc
 *
 */
public class PowerFactoryParameters {

	
	private static Map<String, Double> Pevs = new HashMap<>();
	private static Map<String, Double> Ratepvs = new HashMap<>();
	private static Map<String, Double> SoE0 = new HashMap<>();
	
	
	private static final String SEMICOLON_DELIMITER = ";";
	private static final String NEW_LINE_SEPARATOR = "\n";
	
	
	/**
	 * Adds the EV charging power decided by the EV agent to the map until the end of the cycle
	 * @param ev :  PowerFactory path of the P-input ElmDsl element of the considered EV
	 * @param power : decided power
	 */
	public static void addPev(String ev, double power) {
		Pevs.put(ev, power);
	}
	
	/**
	 * Adds the production rate of the PV decided by the PV agent to the map until the end of the cycle (always =1 in the final version)
	 * @param pv : PowerFactory path of the shedding ElmDsl element of the considered PV
	 * @param rate : decided production rate (=1)
	 */
	public static void addRatePV(String pv, double rate) {
		Ratepvs.put(pv,rate);
	}
	
	/**
	 * Adds the battery initial state of energy for the next cycle to the map until the end of the cycle. It corresponds to the final state of energy of the current cycle
	 * @param ev : PowerFactory path of the Battery ElmDsl element of the considered EV
	 * @param soe0 : initial state of energy
	 */
	public static void addSoE0(String ev, double soe0) {
		SoE0.put(ev, soe0);
	}
	
	
	/**
	 * Writes EVs charging power from the Pevs map into the DefinePevs.csv file
	 */
	public static void definePevs() {
		double power;
		FileWriter fileWriter = null;
		String communicationfolderPath=Parameters.getCommunicationPath();
		try {
			File file=new File(communicationfolderPath);
			file.mkdirs();
			fileWriter = new FileWriter(communicationfolderPath+"\\DefinePevs.csv");
			for(String ev:Pevs.keySet()) {
				power=Pevs.get(ev);
				fileWriter.append(ev);
    			fileWriter.append(SEMICOLON_DELIMITER);
    			fileWriter.append(String.valueOf(power));
    			fileWriter.append(NEW_LINE_SEPARATOR);
			}
		}catch(Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
		}finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Writes PVs production rate from the Ratepvs map into the DefineRatePVs.csv file
	 */
	public static void defineRatePVs() {
		double rate;
		
		FileWriter fileWriter = null;
		String communicationfolderPath=Parameters.getCommunicationPath();
		try {
			File file=new File(communicationfolderPath);
			file.mkdirs();
			fileWriter = new FileWriter(communicationfolderPath+"\\DefineRatePVs.csv");
			
			for(String pv:Ratepvs.keySet()) {
				rate=Ratepvs.get(pv);
				fileWriter.append(pv);
    			fileWriter.append(SEMICOLON_DELIMITER);
    			fileWriter.append(String.valueOf(rate));
    			fileWriter.append(NEW_LINE_SEPARATOR);
			}
		}catch(Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
		}finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Writes initial state of energy of batteries from the SoE0 map into the DefineSoE0.csv file
	 */
	public static void defineSoE0() {
		double soe;
		FileWriter fileWriter = null;
		String communicationfolderPath=Parameters.getCommunicationPath();
		try {
			File file=new File(communicationfolderPath);
			file.mkdirs();
			fileWriter = new FileWriter(communicationfolderPath+"\\DefineSoE0.csv");
			
			for(String ev:SoE0.keySet()) {
				soe=SoE0.get(ev);
				fileWriter.append(ev);
    			fileWriter.append(SEMICOLON_DELIMITER);
    			fileWriter.append(String.valueOf(soe));
    			fileWriter.append(NEW_LINE_SEPARATOR);
			}
		}catch(Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
		}finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error while flushing/closing fileWriter !!!");
				e.printStackTrace();
			}
		}
	}
}
