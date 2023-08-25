package ui;

import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LineAgentPanel extends ScenarioPanel {
	private static final long serialVersionUID = 1L;
	private static JFormattedTextField KLine, criticalityMinLineAgent;
	
	
	public LineAgentPanel() {
		super("\\Line Agent");
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
		KLine = new JFormattedTextField();
		criticalityMinLineAgent = new JFormattedTextField();
		
		JPanel KLinePanel=new JPanel();
		KLinePanel.add(new JLabel("K line : "));
		KLinePanel.add(KLine);
		
		JPanel CrMinPanel=new JPanel();
		CrMinPanel.add(new JLabel("criticality min : "));
		CrMinPanel.add(criticalityMinLineAgent);
		
		this.add(KLinePanel);
		this.add(CrMinPanel);
		this.setLayout(new GridLayout(56,1));
		
		defineNewConfig(config);
	}
	
	@Override
	protected void defineNewConfig(Map<String, List<String>> pvsConfig) {
		KLine.setValue(Double.parseDouble(pvsConfig.get("KLine").get(0)));
		criticalityMinLineAgent.setValue(Double.parseDouble(pvsConfig.get("CrMin").get(0)));
	}
	
	public double getKLine() {
		return Double.parseDouble(KLine.getValue().toString());
	}
	
	public double getCriticalityMinLineAgent() {
		return Double.parseDouble(criticalityMinLineAgent.getValue().toString());
	}

}
