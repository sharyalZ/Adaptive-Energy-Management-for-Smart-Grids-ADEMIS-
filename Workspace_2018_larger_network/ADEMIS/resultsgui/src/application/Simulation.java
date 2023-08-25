package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Exceptions.UnknownSimulationException;
import fr.irit.smac.lxplot.LxPlot;

public class Simulation {

	
	protected Map<String, List<String>> resultsName;
	private String simulationPath;
	private boolean onlineSimulation;
	
	public Simulation(String simulationPath) throws UnknownSimulationException {
		this.simulationPath=simulationPath;
		getPlotsNames();
	}

	public Simulation(String simulationPath, boolean onlineSimulation) {
		this.simulationPath=simulationPath;
		this.onlineSimulation=onlineSimulation;
	}
	
	
	public void getPlotsNames() throws UnknownSimulationException {
		resultsName=new HashMap<>();
		File f = new File(simulationPath);
		if(f.exists()) {
			ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
			boolean testLog=false, testParameters=false;
			for(File file:files) {
				if(file.isDirectory()) {
					List<String> list=new ArrayList<>();
					ArrayList<File> res = new ArrayList<File>(Arrays.asList(file.listFiles()));
					for(File r:res) {
						list.add(r.getName());
					}
					resultsName.put(file.getName(), list);
				}else if(file.getName().equals("log.txt")) {
					testLog=true;
				}else if(file.getName().equals("Parameters.txt")) {
					testParameters=true;
				}
			}
			if(!(testLog && testParameters)&&!onlineSimulation) {
				throw new UnknownSimulationException("Unkown Simulation : testLog = "+testLog+", testParameters = "+testParameters);
			}
		}
		
	}
	
	public void plotResult(String plot, String serie) {
		plotResult(plot, serie, plot, serie);

    }
	
	public void plotResult(String folderName, String fileName, String plotName, String serieName) {
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
		
		String csvFile = simulationPath+"/"+folderName+"/"+fileName;
		
		try {

            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                String[] st = line.split(cvsSplitBy);
                LxPlot.getChart(plotName).add(serieName.substring(0,serieName.length()-4),Double.parseDouble(st[0]), Double.parseDouble(st[1]));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}
		
	
	
	public Map<String, List<String>> getResultsName() {
		try {
			getPlotsNames();
		} catch (UnknownSimulationException e) {
			e.printStackTrace();
		}
		return resultsName;
	}

}
