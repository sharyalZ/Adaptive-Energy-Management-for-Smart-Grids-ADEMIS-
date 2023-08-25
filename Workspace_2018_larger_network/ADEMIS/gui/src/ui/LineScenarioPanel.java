package ui;

import java.awt.GridLayout;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class LineScenarioPanel extends ScenarioPanel {

	private JFormattedTextField iMinRateField, ratedCurrentField;
	private Map<String, JPanel> additionalValuePanel;
	
	private Map<String,JComboBox<String>> configTypeComboBox;
	private Map<String,JFormattedTextField> configTypeValue;
	
	public LineScenarioPanel() {
		super("\\Lines Scenarios");
		
	}

	@Override
	protected void parametersInitialisation() {
		additionalValuePanel=new HashMap<>();
		configTypeComboBox=new HashMap<>();
		configTypeValue=new HashMap<>();
		String ConfigFilePath=defaulConfigPath+"\\ISGT Case Study.csv";
		Map<String, List<String>> config=null;
		try {
			config = Reader.readFile(ConfigFilePath);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		iMinRateField = new JFormattedTextField();
		ratedCurrentField = new JFormattedTextField();
		
		JPanel iMinPanel=new JPanel();
		iMinPanel.add(new JLabel("current minumum rate : "));
		iMinPanel.add(iMinRateField);
		iMinPanel.add(new JLabel(" * rated current"));
		
		JPanel ratedCurrentPanel=new JPanel();
		ratedCurrentPanel.add(new JLabel("rated current : "));
		ratedCurrentPanel.add(ratedCurrentField);
		ratedCurrentPanel.add(new JLabel(" kA"));
		
		this.add(iMinPanel);
		this.add(ratedCurrentPanel);
		this.setLayout(new GridLayout(56,1));
		
		
		
		
		defineNewConfig(config);
	}
	
	
	
	@Override
	protected void defineNewConfig(Map<String, List<String>> linesConfig) {
		iMinRateField.setValue(Double.parseDouble(linesConfig.get("Imin rate").get(0)));
		linesConfig.remove("Imin rate");
		ratedCurrentField.setValue(Double.parseDouble(linesConfig.get("Default ratedCurrent").get(0)));
		linesConfig.remove("Default ratedCurrent");
		List<String> remainingPanels=new ArrayList<String>(additionalValuePanel.keySet());
		for(String key:linesConfig.keySet()) {
			remainingPanels.remove(key);
			List<String> line=linesConfig.get(key);
			if(!additionalValuePanel.containsKey(key)) {
				addLine(key, line);
			}else {
				configTypeComboBox.get(key).setSelectedItem(line.get(0));
				configTypeValue.get(key).setValue(Double.parseDouble(line.get(1)));
			}
			this.updateUI();
		}
		for(String remaingPanel:remainingPanels) {
			this.remove(additionalValuePanel.get(remaingPanel));
			additionalValuePanel.remove(remaingPanel);
		}
	}
	
	private void addLine(String key, List<String> list) {
		JPanel newPanel=new JPanel();
		additionalValuePanel.put(key, newPanel);
		newPanel.add(new JLabel(key+" : "));
		this.add(newPanel);
		String[] items = {"NAME", "TYPE"};
		JComboBox<String> newComboBox=new JComboBox<>(items);
		newComboBox.setSelectedItem(list.get(0));
		configTypeComboBox.put(key, newComboBox);
		newPanel.add(newComboBox);
		newPanel.add(new JLabel(" : "));
		JFormattedTextField newField=new JFormattedTextField();
		newField.setValue(Double.parseDouble(list.get(1)));
		newPanel.add(newField);
		configTypeValue.put(key, newField);
	}
	
	public double getCurrentMinumumRate() {
		return (double) iMinRateField.getValue();
	}
	
	public double getDefaultRatedCurrent() {
		return (double) ratedCurrentField.getValue();
	}
	
	public Map<String, Double> getAdditionnalLineConfigByType(){
		Map<String, Double> res=new HashMap<>();
		for(String key:additionalValuePanel.keySet()) {
			if(configTypeComboBox.get(key).getSelectedItem().equals("TYPE")){
				res.put(key, (double)configTypeValue.get(key).getValue());
			}
		}
		return res;
	}
	
	public Map<String, Double> getAdditionnalLineConfigByName(){
		Map<String, Double> res=new HashMap<>();
		for(String key:additionalValuePanel.keySet()) {
			if(configTypeComboBox.get(key).getSelectedItem().equals("NAME")){
				res.put(key, (double)configTypeValue.get(key).getValue());
			}
		}
		return res;
	}
	
}
