package ui;

import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AggregatorAgentPanel extends ScenarioPanel {
	private static final long serialVersionUID = 1L;
	
	private static JFormattedTextField Pmax, criticalityMinAggregatorAgent;
	
	public AggregatorAgentPanel() {
		super("\\Aggregator Agent");
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
		Pmax = new JFormattedTextField();
		criticalityMinAggregatorAgent = new JFormattedTextField();
		Pmax.setColumns(5);
		criticalityMinAggregatorAgent.setColumns(5);
		
		JPanel PmaxPanel=new JPanel();
		PmaxPanel.add(new JLabel("Pmax : "));
		PmaxPanel.add(Pmax);
		
		JPanel CrMinPanel=new JPanel();
		CrMinPanel.add(new JLabel("criticality min : "));
		CrMinPanel.add(criticalityMinAggregatorAgent);
		
		this.add(PmaxPanel);
		this.add(CrMinPanel);
		this.setLayout(new GridLayout(56,1));
		
		defineNewConfig(config);
	}
	
	@Override
	protected void defineNewConfig(Map<String, List<String>> pvsConfig) {
		Pmax.setValue(Double.parseDouble(pvsConfig.get("Pmax").get(0)));
		criticalityMinAggregatorAgent.setValue(Double.parseDouble(pvsConfig.get("CrMin").get(0)));
	}

	public double getCriticalityMinAggregatorAgent() {
		return Double.parseDouble(criticalityMinAggregatorAgent.getValue().toString());
	}
	
	public double getPmax() {
		return Double.parseDouble(Pmax.getValue().toString());
	}
	
}
