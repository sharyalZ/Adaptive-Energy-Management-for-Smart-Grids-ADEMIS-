package ui;


import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;



import Interface.PowerFactoryCaller;
import Runs.StartADEMIS;

public class Controller {

	private MainWindow mainWindow;
	private SMAParametersWindow smaParametersWindow;
	private GUIScenario guiScenario;
	
	public Controller(MainWindow mainWindow) {
		this.mainWindow=mainWindow;
	}
	
	public void loadButtonPressed() {
		//Parameters parameters=new Parameters();
		
		Parameters.setGridName(mainWindow.selectedGrid());
		Parameters.setFirstBusName(mainWindow.getFirstBusName());
		Parameters.setSMAPeriod(mainWindow.getSMAPeriod());
		Parameters.setPFTimeStep(mainWindow.getPFtimeStep());
		Parameters.setEndTime(mainWindow.getEndTime());
		Parameters.setStartTime(mainWindow.getStartTime());
		//Parameters.setConsumptionCommitment(mainWindow.getCommitment());
		Parameters.setIntelligentEVs(mainWindow.getIntelligentEVs());
		
		
		/*Thread thread = new Thread(){
            public void run(){
              System.out.println("new thread");
              
            }
          };
          thread.start();*/
         
		new StartADEMIS();
        
		//new ResultsFrame(this);
		
		new GUIResultsMainWindow(true);
		
		
		
		
	}
	
	public void plotResult(String plot, String serie) {
		Results.plotResult(plot,  serie);
	}
	
	
	public void deletePlot(String plot, String serie) {
		Results.deletePlot(plot,  serie);
	}
	
	
	
	public void OpenPFButtonPressed() {
		PowerFactoryCaller.OpenPF();
	}
	
	public void EndSimuButtonPressed() {
		System.out.println("End simu");
	}
	
	public void LoadScenarioButtonPressed() {
		if(guiScenario==null) {
			guiScenario=new GUIScenario(this);
		}else {
			guiScenario.showWindow();
		}
	}
	
	public void DefineSMAParametersButtonPressed() {
		if(smaParametersWindow==null) {
			smaParametersWindow=new SMAParametersWindow(this);
		}else {
			smaParametersWindow.showWindow();
		}
		
	}
	
	public void loadSMAParemetersPressed() {
		smaParametersWindow.setVisible(false);
		
		
		LineAgentPanel lineAgentPanel=smaParametersWindow.getLineAgentPanel();
		Parameters.setKLine((lineAgentPanel.getKLine()));
		Parameters.setCriticalityMinLineAgent(lineAgentPanel.getCriticalityMinLineAgent());
		
		AggregatorAgentPanel aggregatorAgentPanel=smaParametersWindow.getAggregatorAgentPanel();
		Parameters.setPmaxAggregator(aggregatorAgentPanel.getPmax());
		Parameters.setCriticalityMinAggregatorAgent(aggregatorAgentPanel.getCriticalityMinAggregatorAgent());
		
		EVAgentPanel evAgentPanel=smaParametersWindow.getEvAgentPanel();
		//Parameters.setKEV((evAgentPanel.getKEV()));
		Parameters.setCriticalityMinEVAgent(evAgentPanel.getCriticalityMinEVAgent());
		//Parameters.setPevRegulation(evAgentPanel.getPevRegulation());
	}
	
	
	public void loadScenarioButtonPressed() {
		guiScenario.setVisible(false);
		EVsScenarioPanel evPanel=guiScenario.getEVPanel();
		Map<String, GregorianCalendar> EvDepartureTime =evPanel.getEvDepartureTime();
		Parameters.setEvDeparture(EvDepartureTime);
		Map<String, GregorianCalendar> EvArrivalTime =evPanel.getEvArrivalTime();
		Parameters.setEvArrival(EvArrivalTime);
		Map<String, Double> EvSoE0 =evPanel.getEvSoE0();
		Parameters.setEvSoE0(EvSoE0);
		Map<String, Boolean> activatedEVs =evPanel.getEvActivated();
		Parameters.setActivatedEVs(activatedEVs);
		
		PVsScenarioPanel pvPanel=guiScenario.getPVPanel();
		Map<String, Boolean> activatedPVs =pvPanel.getPvActivated();
		Parameters.setActivatedPVs(activatedPVs);
		Map<String, Double> pvSurface =pvPanel.getPvSurface();
		Parameters.setPvSurface(pvSurface);
		Map<String, Double> pvRate =pvPanel.getPvRate();
		Parameters.setPvRate(pvRate);
		
		AggregatorScenarioPanel aggregatorPanel=guiScenario.getAggregatorPanel();
		Parameters.setAggregatorPeriodCommitment(aggregatorPanel.getAggregatorPeriodCommitmentField());
		Parameters.setConsumptionCommitment(aggregatorPanel.getAggregatorValueCommitmentField());
		
		LineScenarioPanel lineScenarioPanel=guiScenario.getLineScenarioPanel();
		Parameters.setCurrentMinimumRate(lineScenarioPanel.getCurrentMinumumRate());
		Parameters.setDefaultRatedCurrent(lineScenarioPanel.getDefaultRatedCurrent());
		Parameters.setAdditionnalLineConfigByType(lineScenarioPanel.getAdditionnalLineConfigByType());
		Parameters.setAdditionnalLineConfigByName(lineScenarioPanel.getAdditionnalLineConfigByName());
	}
	
	
	public Map<String, List<String>> updateResults() {
		return Results.getResults();
	}
	
	
	
	
	
	
	

	
	
}
