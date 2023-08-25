package ui;

import java.util.ArrayList;
import java.util.List;

public class GUIResultsController {

	private List<SimulationPanel> simulationPanelList = new ArrayList<>();
	
	public GUIResultsController() {
		
	}

	
	public void newPlot(String plotName) {
		for(SimulationPanel simulationPanel:simulationPanelList) {
			simulationPanel.plotSelectedSeries(plotName);
		}
		
	}
	
	public void addSimulationPanel(SimulationPanel simulationPanel) {
		simulationPanelList.add(simulationPanel);
	}
	
	

}
