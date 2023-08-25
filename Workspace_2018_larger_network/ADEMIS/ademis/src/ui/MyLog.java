package ui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Classe permettant de créer un Log.
 * @author jbblanc
 *
 */
public class MyLog {

	private static boolean console=false;
	private static boolean myFile =true;
	private static String logPath;
	
	public MyLog() {
		
	}
	
	public static void deleteFile() {
		
		final String dir = System.getProperty("user.dir");
		Path workingPath=Paths.get(dir);
        String communicationPath=workingPath.getParent().toString();
		logPath=communicationPath+"\\ADEMIS\\log.txt";
		 File file = new File(logPath);
		 file.delete();
	}
	
	/**
	 * Permet d'ajouter une ligne au log
	 * @param toWrite
	 */
	public static void print(String toWrite) {
		BufferedWriter out = null;
		
		if(console) {
			System.out.println(toWrite);
		}
		if(myFile) {
			try {
			    FileWriter fstream = new FileWriter(logPath, true); //true tells to append data.
			    out = new BufferedWriter(fstream);
			    out.write("\n"+toWrite);
			}

			catch (IOException e) {
			    System.err.println("Error: " + e.getMessage());
			}

			finally {
			    if(out != null) {
			        try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    }
			}
		}
	}
	
	/**
	 * A la fin de la simulation, le fichier log est créé
	 * @param simulation
	 */
	public static void endSimu(String simulation) {
		System.out.println("end simulation from MyLog");
		File file = new File(logPath);
		//String destinationPath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\Results\\ADEMISResults\\"+simulation+"\\";
		String destinationPath=Parameters.getEndSimulationPath();
		try {
			Files.copy(file.toPath(), new File(destinationPath + "log.txt").toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
