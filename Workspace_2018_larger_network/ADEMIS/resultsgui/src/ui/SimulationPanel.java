package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import Exceptions.UnknownSimulationException;
import application.CurrentSimulation;
import application.Simulation;

public class SimulationPanel extends JPanel {

	protected Simulation simulation;
	private String simulationName;
	
	private boolean onlineSimulation;
	
	protected List<CheckBox> checkBoxList=new ArrayList<>();
	
	//private List<JPanel> plotsPan=new ArrayList<>();
	
	public SimulationPanel(String simulationPath, String simulationName, boolean onlineSimulation) throws UnknownSimulationException {
		this.onlineSimulation=onlineSimulation;
		if(onlineSimulation) {
			simulation=new CurrentSimulation(simulationPath);
		}else {
			simulation=new Simulation(simulationPath);
		}
		
		this.simulationName=simulationName;
		
		if(!onlineSimulation) {
			updatePlotsPan();
		} 
		
	}
	
	public void updatePlotsPan() {
		Map<String, List<String>> resultsMap = simulation.getResultsName();
		System.out.println(resultsMap);
		JTabbedPane tab = new JTabbedPane(JTabbedPane.LEFT);
		for(String plot:resultsMap.keySet()) {
			createPlotPan(resultsMap, tab, plot);
			System.out.println(plot);
		}
		this.setPreferredSize(new Dimension(200, 400));
		addTabTabToMainWindow(tab);
	}
	
	public void addTabTabToMainWindow(JTabbedPane tab) {
		this.add(tab);
	}

	private void createPlotPan(Map<String, List<String>> resultsMap, JTabbedPane tab, String plot) {
		JPanel plotPan=new JPanel();
		int layoutMin=8;
		if(resultsMap.get(plot).size()>layoutMin) {
			plotPan.setLayout(new GridLayout(resultsMap.get(plot).size(),1));
		}else {
			plotPan.setLayout(new GridLayout(layoutMin,1));
		}
		
		for(String serie:resultsMap.get(plot)) {
			createSeriePan(plot, plotPan, serie);
		}
		JScrollPane scroll2=new JScrollPane(plotPan);
		scroll2.setPreferredSize(new Dimension(250, 330));
		scroll2.getVerticalScrollBar().setUnitIncrement(50);
		tab.add(plot, scroll2);
	}

	protected void createSeriePan(String plot, JPanel plotPan, String serie) {
		JPanel seriePan=new JPanel(new FlowLayout(FlowLayout.LEFT));
		CheckBox checkBox=new CheckBox(serie.substring(0,serie.length()-4), plot, serie, simulation);
		checkBoxList.add(checkBox);
		seriePan.add(checkBox);
		seriePan.add(new MyButton("Display", plot, serie, simulation));
		plotPan.add(seriePan);
	}
	
	public void plotSelectedSeries(String plotName) {
		for(CheckBox check:checkBoxList) {
			if(check.isSelected()) {
				check.setSelected(false);
				simulation.plotResult(check.get_plot(), check.get_serie(),  plotName, simulationName+"::"+check.get_plot()+"::"+check.get_serie());
			}
		}
	}
	
	class CheckBox extends JCheckBox{
		public String plot, serie;
		private Simulation simulation;
		public CheckBox(String name,String p, String s, Simulation simulation){
			super(name);
			this.simulation=simulation;
			plot=p;
			serie=s;
		}
		
		public String get_plot(){ return plot;}
		public String get_serie(){ return serie;}
	}
	
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		public String plot, serie;
		private Simulation simulation;
		//public Simulation_Parameters simu;
		//public JFormattedTextField path;
		//public Load_scenario window;
		public MyButton(String name, String plot, String serie, Simulation simulation){
			super(name);
			this.plot=plot;
			this.serie=serie;
			this.simulation=simulation;
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					simulation.plotResult(plot, serie);
			 	}
			});
			
		}
	}
	
	

}
