package ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import Exceptions.UnknownSimulationException;
import application.CurrentSimulation;
import application.Simulation;
import ui.SimulationPanel.CheckBox;

public class CurrentSimulationPanel extends SimulationPanel {

	
	
	private JSplitPane splitPan;
	
	
	public CurrentSimulationPanel(String simulationPath, String simulationName, boolean onlineSimulation) throws UnknownSimulationException {
		super(simulationPath, simulationName, onlineSimulation);
		
		
		splitPan=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JPanel topPan=new JPanel();
		JPanel bottomPanel = new JPanel();
		
		topPan.add(new MyButton("Update", this));
		//bottomPanel.add(new JLabel("test 2"));
		
		
		splitPan.setTopComponent(topPan);
		//splitPan.setBottomComponent(bottomPanel);
		splitPan.setPreferredSize(new Dimension(500, 400));
		this.add(splitPan);
	}
	
	
	@Override
	public void addTabTabToMainWindow(JTabbedPane tab) {
		splitPan.setBottomComponent(tab);
	}
	
	@Override
	protected void createSeriePan(String plot, JPanel plotPan, String serie) {
		JPanel seriePan=new JPanel(new FlowLayout(FlowLayout.LEFT));
		CheckBox checkBox=new CheckBox(serie.substring(0,serie.length()-4), plot, serie, simulation);
		checkBoxList.add(checkBox);
		seriePan.add(checkBox);
		seriePan.add(new MyDisplayButton("Display", plot, serie, simulation));
		plotPan.add(seriePan);
	}
	
	
	
	/*class CheckBox extends JCheckBox{
		public String plot, serie;
		private Simulation simulation;
		public CheckBox(String name,String p, String s, Simulation simulation){
			super(name);
			System.out.println("test");
			this.simulation=simulation;
			plot=p;
			serie=s;
		}
		
		public String get_plot(){ return plot;}
		public String get_serie(){ return serie;}
	}*/
	
	private static class MyDisplayButton extends JButton{
		private static final long serialVersionUID = 1L;
		public String plot, serie;
		private Simulation simulation;
		//public Simulation_Parameters simu;
		//public JFormattedTextField path;
		//public Load_scenario window;
		public MyDisplayButton(String name, String plot, String serie, Simulation simulation){
			super(name);
			this.plot=plot;
			this.serie=serie;
			this.simulation=simulation;
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					System.out.println("online");
					simulation.plotResult(plot, serie);
					Results.plotResult(plot,  serie.substring(0,serie.length()-4));
			 	}
			});
			
		}
	}
	
	
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		private CurrentSimulationPanel simulationPan;
		public MyButton(String name, CurrentSimulationPanel simulationPan){
			super(name);
			this.simulationPan=simulationPan;
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					//simulation.plotResult(plot, serie);
					simulationPan.updatePlotsPan();
			 	}
			});
			
		}
	}
	
	

}
