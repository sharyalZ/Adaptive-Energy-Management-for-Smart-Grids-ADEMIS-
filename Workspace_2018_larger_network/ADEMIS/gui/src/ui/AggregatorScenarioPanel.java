package ui;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AggregatorScenarioPanel extends ScenarioPanel {
	private static final long serialVersionUID = 1L;
	
	private JFormattedTextField aggregatorPeriodCommitmentField, aggregatorCommitmentValueField1, aggregatorCommitmentValueField2, 
								aggregatorCommitmentValueField3, aggregatorCommitmentValueField4;
	
	public AggregatorScenarioPanel() {
		super("\\Aggregator Scenarios");
		
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
		aggregatorPeriodCommitmentField = new JFormattedTextField();
		aggregatorCommitmentValueField1 = new JFormattedTextField();
		aggregatorCommitmentValueField2 = new JFormattedTextField();
		aggregatorCommitmentValueField3 = new JFormattedTextField();
		aggregatorCommitmentValueField4 = new JFormattedTextField();
		
		JPanel commitmenPeriodPanel=new JPanel();
		commitmenPeriodPanel.add(new JLabel("Commitment period : "));
		commitmenPeriodPanel.add(aggregatorPeriodCommitmentField);
		commitmenPeriodPanel.add(new JLabel(" min"));
		
		JPanel commitmenValuePanel1=new JPanel();
		commitmenValuePanel1.add(new JLabel("Commitment value for Period 1: "));
		commitmenValuePanel1.add(aggregatorCommitmentValueField1);
		commitmenValuePanel1.add(new JLabel(" kW"));
		
		JPanel commitmenValuePanel2=new JPanel();
		commitmenValuePanel2.add(new JLabel("commitment value for Period 2: "));
		commitmenValuePanel2.add(aggregatorCommitmentValueField2);
		commitmenValuePanel2.add(new JLabel(" kW"));
		
		JPanel commitmenValuePanel3=new JPanel();
		commitmenValuePanel3.add(new JLabel("commitment value for Period 3: "));
		commitmenValuePanel3.add(aggregatorCommitmentValueField3);
		commitmenValuePanel3.add(new JLabel(" kW"));
		
		JPanel commitmenValuePanel4=new JPanel();
		commitmenValuePanel4.add(new JLabel("commitment value for Period 4: "));
		commitmenValuePanel4.add(aggregatorCommitmentValueField4);
		commitmenValuePanel4.add(new JLabel(" kW"));
		
		this.add(commitmenPeriodPanel);
		this.add(commitmenValuePanel1);
		this.add(commitmenValuePanel2);
		this.add(commitmenValuePanel3);
		this.add(commitmenValuePanel4);
		this.setLayout(new GridLayout(56,1));
		
		defineNewConfig(config);
	}
	
	@Override
	protected void defineNewConfig(Map<String, List<String>> pvsConfig) {
		//selectedPVs.clear();
		aggregatorPeriodCommitmentField.setValue(Integer.parseInt(pvsConfig.get("CommitmentPeriod").get(0)));
		aggregatorCommitmentValueField1.setValue(Double.parseDouble(pvsConfig.get("CommitmentValue1").get(0)));
		aggregatorCommitmentValueField2.setValue(Double.parseDouble(pvsConfig.get("CommitmentValue2").get(0)));
		aggregatorCommitmentValueField3.setValue(Double.parseDouble(pvsConfig.get("CommitmentValue3").get(0)));
		aggregatorCommitmentValueField4.setValue(Double.parseDouble(pvsConfig.get("CommitmentValue4").get(0)));
	}

	
	public int getAggregatorPeriodCommitmentField() {
		return (int) aggregatorPeriodCommitmentField.getValue();
	}
	
	public List<Double> getAggregatorValueCommitmentField() 
	{
		List<Double> CommitementValues = new ArrayList<Double>();
		CommitementValues.add((double) aggregatorCommitmentValueField1.getValue());
		CommitementValues.add((double) aggregatorCommitmentValueField2.getValue());
		CommitementValues.add((double) aggregatorCommitmentValueField3.getValue());
		CommitementValues.add((double) aggregatorCommitmentValueField4.getValue());
		
		return CommitementValues;
	}

}
