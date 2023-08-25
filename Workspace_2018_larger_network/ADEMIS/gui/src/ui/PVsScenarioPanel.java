package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class PVsScenarioPanel extends ScenarioPanel {
	private static final long serialVersionUID = 1L;
	
	private List<String> pvList;
	
	private Map<String, JPanel> pvPanels;
	private Map<String, JCheckBox> pvActivated;
	
	private Map<String, JFormattedTextField> pvRateField;
	private Map<String, JFormattedTextField> pvSurfaceField;
	
	private List<String> otherParameters;
	
	private int nbParameters;
	private int nbPVs;

	public PVsScenarioPanel() {
		super("\\PVs Scenarios");
	}

	@Override
	protected void parametersInitialisation(){
		String ConfigFilePath=defaulConfigPath+"\\Default.csv";
		Map<String, List<String>> config=null;
		try {
			config = Reader.readFile(ConfigFilePath);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		nbPVs=Integer.parseInt(config.get("nbPVs").get(0));
		pvList = new ArrayList<>();
		/*if(nbEVs==3) {
			for(int i=1;i<=2;i++) {
				pvList.add("PV"+i);
			}
		}else if(nbEVs==55) {
			for(int i=1;i<=55;i++) {
				pvList.add("PV"+i);
			}
		}else {
			JOptionPane.showMessageDialog(null, "Not a known number of EVs", "Error", JOptionPane.ERROR_MESSAGE);
		}*/
		for(int i=1;i<=nbPVs;i++) {
			pvList.add("PV"+i);
		}
		
		nbParameters=2;
		pvActivated=new HashMap<>();
		pvRateField=new HashMap<>();
		pvSurfaceField=new HashMap<>();
		pvPanels=new HashMap<>();
		otherParameters=new ArrayList<>();
		otherParameters.add("nbPVs");
		
		
		parametersMapsInitialization();
		frameCreation();
		
		/*this.setLayout(new GridLayout(56,1));
		for(String pvName:pvList) {
			this.add(pvPanels.get(pvName));
		}*/
		defineNewConfig(config);
	}

	private void parametersMapsInitialization() {
		for(String pvName:pvList) {
			if(!pvActivated.containsKey(pvName)) {
				pvActivated.put(pvName, new JCheckBox("activated", false));
				pvRateField.put(pvName, new JFormattedTextField());
				pvRateField.get(pvName).setValue(0.15);
				pvSurfaceField.put(pvName, new JFormattedTextField());
				pvSurfaceField.get(pvName).setValue(10.0);
			}
			
		}
	}

	private void frameCreation() {
		for(String pvName:pvList) {
			if(!pvPanels.containsKey(pvName)) {
				pvPanels.put(pvName, new JPanel());
				pvPanels.get(pvName).add(new JLabel(pvName+" : "));
				pvPanels.get(pvName).add(new JLabel("Surface = "));
				pvPanels.get(pvName).add(pvSurfaceField.get(pvName));
				pvPanels.get(pvName).add(new JLabel("m², rate = "));
				pvPanels.get(pvName).add(pvRateField.get(pvName));
				pvPanels.get(pvName).add(pvActivated.get(pvName));
				this.add(pvPanels.get(pvName));
			}
		}
		
		this.setLayout(new GridLayout(pvPanels.size()+1,1));
		this.updateUI();
	}
	
	
	@Override
	protected void defineNewConfig(Map<String, List<String>> pvsConfig) {
		//selectedPVs.clear();
		for(String pv:pvActivated.keySet()) {
			pvActivated.get(pv).setSelected(false);
		}
		
		for(String pv:pvsConfig.keySet()) {
			if(!pvList.contains(pv) && !otherParameters.contains(pv)) {
				pvList.add(pv);
			}
		}
		parametersMapsInitialization();
		frameCreation();
		boolean bugDetection =true;
		for(String pv:pvsConfig.keySet()) {
			if(pvActivated.containsKey(pv)) {
				pvActivated.get(pv).setSelected(true);
				if(pvsConfig.get(pv).size()==nbParameters) {
					pvSurfaceField.get(pv).setValue(Double.parseDouble(pvsConfig.get(pv).get(0)));
					pvRateField.get(pv).setValue(Double.parseDouble(pvsConfig.get(pv).get(1)));
				}else if(bugDetection){
					System.out.println(pv);
					System.out.println(pvsConfig.get(pv).size());
					System.out.println(nbParameters);
					JOptionPane.showMessageDialog(null, "Uncorrect number of parameters", "Error : PVScenarioPanel", JOptionPane.ERROR_MESSAGE);
					bugDetection=false;
				}
			}else if(bugDetection && !otherParameters.contains(pv)){
				JOptionPane.showMessageDialog(null, "Unknown PV : "+pv, "Error : PVScenarioPanel", JOptionPane.ERROR_MESSAGE);
				bugDetection=false;
			}
		}
	}
	
	public Map<String, Boolean> getPvActivated() {
		Map<String, Boolean> activatedPVs= new HashMap<>();
		for(String evName:pvList) {
			activatedPVs.put(evName, pvActivated.get(evName).isSelected());
		}
		return activatedPVs;
	}
	
	public Map<String, Double> getPvSurface() {
		Map<String, Double> pvSurface=new HashMap<>();
		for(String pvName:pvActivated.keySet()) {
			pvSurface.put(pvName, (double) pvSurfaceField.get(pvName).getValue());
		}
		return pvSurface;
	}
	
	public Map<String, Double> getPvRate() {
		Map<String, Double> pvRate=new HashMap<>();
		for(String pvName:pvActivated.keySet()) {
			pvRate.put(pvName, (double) pvRateField.get(pvName).getValue());
		}
		return pvRate;
	}
	
	
}
