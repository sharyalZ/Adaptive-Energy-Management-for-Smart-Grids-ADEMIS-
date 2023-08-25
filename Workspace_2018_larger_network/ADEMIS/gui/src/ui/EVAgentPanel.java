package ui;

import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EVAgentPanel extends ScenarioPanel {
	private static final long serialVersionUID = 1L;
	
	private static JFormattedTextField criticalityMinEVAgent;
	
	public EVAgentPanel() {
		super("\\EV Agent");
	}
	
	@Override
	protected void parametersInitialisation() {
		String ConfigFilePath=defaulConfigPath+"\\Default.csv";
		Map<String, List<String>> config=null;
		try {
			config = Reader.readFile(ConfigFilePath);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		criticalityMinEVAgent = new JFormattedTextField();
		criticalityMinEVAgent.setColumns(5);
		
		
		JPanel CrMinPanel=new JPanel();
		CrMinPanel.add(new JLabel("criticality min : "));
		CrMinPanel.add(criticalityMinEVAgent);
		
		
		this.add(CrMinPanel);
		this.setLayout(new GridLayout(56,1));
		
		defineNewConfig(config);
	}
	
	@Override
	protected void defineNewConfig(Map<String, List<String>> evConfig) {
		criticalityMinEVAgent.setValue(Double.parseDouble(evConfig.get("CrMin").get(0)));
	}
	
	public double getCriticalityMinEVAgent() {
		return Double.parseDouble(criticalityMinEVAgent.getValue().toString());
	}

}
