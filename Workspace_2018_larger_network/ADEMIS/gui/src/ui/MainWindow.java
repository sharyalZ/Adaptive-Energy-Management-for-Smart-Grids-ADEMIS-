package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

//import AMAS.AggregatorAgent;
//import Interface.PowerFactoryCaller;




public class MainWindow extends JFrame{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The panel which contains the toolbar
	 */
	//private JPanel toolbarPanel;
	
	
	
	private static JFormattedTextField criticalityRate, SMAPeriod, PFtimeStep, PFProject, ResultsFolder;
	private static JCheckBox plot, intelligentEV;
	
	
	
	private static JLabel ResultsFolderLabel = new JLabel("Results folder");
	
	private JSplitPane splitPane;
	
	private static JPanel ParamatersPanel, SimulationPanel;
	
	private JComboBox<String> selectGrid;
	
	private static Controller controller;
	
	private Map<String, String> firstBusNames = new HashMap<>();
	
	JFormattedTextField startTimeField, endTimeField;
	  
	
	public MainWindow() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//mainWindowListener = new MainWindowListener(this);
		//addWindowListener(mainWindowListener);
		//setBounds(300, 300, 1350, 900);
		setSize(500, 400);
		
		setTitle("Project");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		controller=new Controller(this);
		
		ParamatersPanel=new JPanel();
		SimulationPanel=new JPanel();
		JPanel plotPan=new JPanel();
		JPanel SMAPeriodPan=new JPanel();
		JPanel PFtimeStepPan=new JPanel();
		JPanel ratePan=new JPanel();
		JPanel buttonPan=new JPanel();
		JPanel selectGridPan=new JPanel();
		JPanel intelligentEVPan=new JPanel();
		JPanel simulationTimePan=new JPanel();
		JPanel commitmentPan=new JPanel();
		
		String[] items = {"Larger_network_AB","SimpleGrid1.1.1", "SimpleGrid2.1.1", "SimpleGrid3.1.1", "SimpleGrid4.1.1", "Simplified_ELVT", "Simplified_ELVT_saved", "Simplified_ELVT_error", "Simplified_ELVT_2021"};
		selectGrid = new JComboBox<>(items);
		selectGrid.setSelectedItem("Simplified_ELVT_2021");
		selectGridPan.add(selectGrid);
		selectGridPan.add(new MyButton2("+", this));
		
		
		NumberFormat format = NumberFormat.getInstance();
		format.setMinimumFractionDigits(2);
		criticalityRate = new JFormattedTextField(format);
		
		
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		
		
		plot=new JCheckBox();
		plotPan.add(new JLabel("Plot results "));
		plotPan.add(plot);
		plot.setSelected(true);
		
		
		
		
		
		SMAPeriod = new JFormattedTextField(format);
		PFtimeStep = new JFormattedTextField(format);
		
		PFProject=new JFormattedTextField();
		PFProject.setValue("SimpleGrid");
		
		SMAPeriodPan.add(new JLabel("SMA Period : "));
		SMAPeriodPan.add(SMAPeriod);
		SMAPeriodPan.add(new JLabel(" s"));
		SMAPeriod.setValue(1);
		
		PFtimeStepPan.add(new JLabel("PowerFactory time step : "));
		PFtimeStepPan.add(PFtimeStep);
		PFtimeStepPan.add(new JLabel(" s"));
		PFtimeStep.setValue(0.01);
		
		ratePan.add(new JLabel("criticality rate :"));
		criticalityRate.setValue(0.036);
		ratePan.add(criticalityRate);
		
		//PFProjectPan.add(PFGridLabel);
		//PFProjectPan.add(PFProject);
		
		startTimeField = new JFormattedTextField(new SimpleDateFormat("d/M/yyyy HH:mm:ss"));
		endTimeField = new JFormattedTextField(new SimpleDateFormat("d/M/yyyy HH:mm:ss"));
		GregorianCalendar startTime=new GregorianCalendar(2019,0,1);
		GregorianCalendar endTime=new GregorianCalendar(2019,0,1);
		startTime.set(Calendar.SECOND,0);
		startTime.set(Calendar.MILLISECOND,0);
		startTime.set(Calendar.MINUTE,0);
		startTime.set(Calendar.HOUR_OF_DAY,16);
		endTime.set(Calendar.SECOND,0);
		endTime.set(Calendar.MILLISECOND,0);
		endTime.set(Calendar.MINUTE,15);
		endTime.set(Calendar.HOUR_OF_DAY,16);
		startTimeField.setValue(startTime.getTime());
		endTimeField.setValue(endTime.getTime());
		simulationTimePan.add(startTimeField);
		simulationTimePan.add(new JLabel(" - "));
		simulationTimePan.add(endTimeField);
		
		
		
		
		
		
		intelligentEV=new JCheckBox();
		intelligentEV.setSelected(false);
		intelligentEVPan.add(new JLabel("intelligent EVs : "));
		intelligentEVPan.add(intelligentEV);
		
		buttonPan.add(new MyButton("Load parameters"));
		
		ParamatersPanel.setLayout(new GridLayout(11,1));
		ParamatersPanel.add(plotPan);
		ParamatersPanel.add(SMAPeriodPan);
		ParamatersPanel.add(PFtimeStepPan);
		ParamatersPanel.add(ratePan);
		ParamatersPanel.add(selectGridPan);
		ParamatersPanel.add(intelligentEVPan);
		ParamatersPanel.add(simulationTimePan);
		ParamatersPanel.add(commitmentPan);
		
		ParamatersPanel.add(buttonPan);
		
		
		JPanel scenarioButtonPan=new JPanel();
		scenarioButtonPan.add(new MyButton("Load scenario"));
		ParamatersPanel.add(scenarioButtonPan);
		
		JPanel SMAParametersButtonPan=new JPanel();
		SMAParametersButtonPan.add(new MyButton("Define SMA Parameters"));
		ParamatersPanel.add(SMAParametersButtonPan);
		
		
		SimulationPanel.add(new MyButton("Display PF"));
		SimulationPanel.add(new MyButton("End Simu"));
		
		splitPane.setLeftComponent(ParamatersPanel);
		splitPane.setRightComponent(SimulationPanel);
		
		this.add(splitPane );
		//this.add(SimulationPanel);
		
		setVisible(true);
	}
	
	
	public static void endSimu() {
		ResultsFolder=new JFormattedTextField();
		JPanel ResultsFolderPan=new JPanel();
		ResultsFolderPan.add(ResultsFolderLabel);
		ResultsFolderPan.add(ResultsFolder);
		ResultsFolder.setValue("Simulation");
		SimulationPanel.add(ResultsFolderPan);
		//SimulationPanel.add(new SaveResultsButton("SaveResults"));
		SimulationPanel.updateUI();
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////                      Getters                      //////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	 // PFProject,;
	public double getCriticalityRate() {
		return Double.parseDouble(criticalityRate.getValue().toString());
	}
	
	public double getSMAPeriod() {
		return Double.parseDouble(SMAPeriod.getValue().toString());
	}
	
	public double getPFtimeStep() {
		return Double.parseDouble(PFtimeStep.getValue().toString());
	}
	
	public boolean getPlot() {
		return plot.isSelected();
	}
	
	public boolean getIntelligentEVs() {
		return intelligentEV.isSelected();
	}
	
	public String selectedGrid() {
		return (String) selectGrid.getSelectedItem();
	}
	
	public String getFirstBusName() {
		firstBusNames.put("Larger_network_AB", "1001");
		firstBusNames.put("SimpleGrid1.1.1", "2");
		firstBusNames.put("SimpleGrid2.1.1", "2");
		firstBusNames.put("SimpleGrid3.1.1", "2");
		firstBusNames.put("SimpleGrid4.1.1", "2");
		firstBusNames.put("Simplified_ELVT", "1");
		firstBusNames.put("Simplified_ELVT_saved", "1");
		firstBusNames.put("Simplified_ELVT_error", "1");
		firstBusNames.put("Simplified_ELVT_2021", "1");
		
		if(firstBusNames.containsKey(selectedGrid())) {
			return firstBusNames.get(selectedGrid());
		}else {
			JOptionPane.showMessageDialog(null, "unknown key in getFirstBusName", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
	
	public GregorianCalendar getStartTime() {
		GregorianCalendar cal= new GregorianCalendar(){
			private static final long serialVersionUID = 1L;

			@Override
		    public String toString()
		    {
		    	return String.format("%02d:%02d:%02d", get(HOUR_OF_DAY), get(MINUTE), get(SECOND));
		    }
		};;
		cal.setTime((Date) startTimeField.getValue());
		return cal;
	}
	
	public GregorianCalendar getEndTime() {
		GregorianCalendar cal= new GregorianCalendar(){
			private static final long serialVersionUID = 1L;

			@Override
		    public String toString()
		    {
		    	return String.format("%s:%s:%s", get(HOUR_OF_DAY), get(MINUTE), get(SECOND));
		    }
		};;
		cal.setTime((Date) endTimeField.getValue());
		return cal;
	}
	
	public void addPowerFactoryGrid() {
		String gridToAdd=JOptionPane.showInputDialog("PowerFactory grid name ?");
		selectGrid.addItem(gridToAdd);
		selectGrid.setSelectedItem(gridToAdd);
		String firstBus=JOptionPane.showInputDialog("First bus name ?");
		firstBusNames.put(gridToAdd, firstBus);
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////                       Intern classes                          /////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		public MyButton(String name){
			super(name);
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					switch(((JButton) event.getSource()).getText()) {
						case "Load parameters": controller.loadButtonPressed();
							break;
						case "Display PF": controller.OpenPFButtonPressed();
							break;
						case "End Simu": controller.EndSimuButtonPressed();
							break;
						case "Load scenario": controller.LoadScenarioButtonPressed();
							break;
						case "Define SMA Parameters": controller.DefineSMAParametersButtonPressed();
							break;
						default: JOptionPane.showMessageDialog(null, "Not a known button", "Error", JOptionPane.ERROR_MESSAGE);
					}
			 	}
			});
			
		}
	}
	
	private static class MyButton2 extends JButton{
		private static final long serialVersionUID = 1L;
		private MainWindow mainWindow;
		public MyButton2(String name, MainWindow window){
			super(name);
			mainWindow=window;
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					switch(((JButton) event.getSource()).getText()) {
						case "+": mainWindow.addPowerFactoryGrid();
							break;
						default: JOptionPane.showMessageDialog(null, "Not a known button", "Error", JOptionPane.ERROR_MESSAGE);
					}
			 	}
			});
			
		}
	}
	
	

}
