package application;

import java.util.List;
import java.util.Map;

import Exceptions.UnknownSimulationException;
import ui.Results;

public class CurrentSimulation extends Simulation {

	public CurrentSimulation(String simulationPath) throws UnknownSimulationException {
		super(simulationPath, true);
	}
	
	@Override
	public Map<String, List<String>> getResultsName() {
		super.getResultsName();
		if(resultsName.isEmpty()) {
			resultsName=Results.getResults();
		}
		
		return resultsName;
	}

}
