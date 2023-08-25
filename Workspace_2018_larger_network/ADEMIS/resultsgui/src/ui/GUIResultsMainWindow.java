package ui;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;

import Exceptions.UnknownSimulationException;

public class GUIResultsMainWindow extends JFrame {

	private JSplitPane splitPane;
	private GUIResultsController controller;
	private JTabbedPane tab;
	
	private boolean onLineSimulation;
	
	public GUIResultsMainWindow(boolean online){
		onLineSimulation=online;
		setLocation(1000, 0);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//mainWindowListener = new MainWindowListener(this);
		//addWindowListener(mainWindowListener);
		//setBounds(300, 300, 1350, 900);
		setSize(700, 600);
		
		setTitle("Results");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.setPreferredSize(new Dimension(300, 450));
		controller = new GUIResultsController();
		
		
		if(onLineSimulation) {
			//String resultsPath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\workspace\\ADEMIS\\temp";
			String resultsPath=Results.getFilePath();
			addSimulation(resultsPath, "current simulation", online);
		}else {
			newSimulation();
		}
		
		
		
		
		tab.add("",  new JScrollPane());
		tab.setTabComponentAt(tab.getTabCount() - 1, new MyButton("+", this));
		
		
		splitPane.setTopComponent(tab);
		splitPane.setBottomComponent(new NewPlotPan(controller));
		//splitPane.setDividerLocation(0);
		
		this.add(splitPane);
		splitPane.setDividerLocation(0.5); 
		setVisible(true);
	}
	
	/*public GUIResultsMainWindow(boolean online){
		onLineSimulation=online;
		System.out.println("online simulation");
		
		String resultsPath="C:\\Users\\jbblanc\\Documents\\Thèse\\JAVA\\workspace\\ADEMIS\\temp";
		addSimulation(resultsPath, "current simulation", online);
		
	}*/
	
	public void newSimulation() {
		JFileChooser fileChooser= new JFileChooser();
		final String dir = System.getProperty("user.dir");
		Path workingPath=Paths.get(dir);
        String path=workingPath.getParent().getParent().toString()+"\\Results\\ADEMISResults\\ELVTF\\ISGT scenarios";
		fileChooser.setCurrentDirectory(new java.io.File(path));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(null);
		String resultsPath=fileChooser.getSelectedFile().getAbsolutePath();
		String resultsName=fileChooser.getSelectedFile().getName();
		
		addSimulation(resultsPath, resultsName, false);
	}

	private void addSimulation(String resultsPath, String resultsName, boolean onlineSimulation) {
		try {
			SimulationPanel simulationPanel;
			if(onlineSimulation) {
				simulationPanel=new CurrentSimulationPanel(resultsPath, resultsName, onlineSimulation);
			}else {
				simulationPanel=new SimulationPanel(resultsPath, resultsName, onlineSimulation);
			}
			
			//tab.add(resultsName, simulationPanel);
			if(tab.getTabCount()==0) {
				tab.add(resultsName, simulationPanel);
			}else {
				tab.add(simulationPanel, tab.getTabCount()-1);
				tab.setTitleAt(tab.getTabCount()-2, resultsName);
			}
			
			controller.addSimulationPanel(simulationPanel);
		} catch (UnknownSimulationException e) {
			e.printStackTrace();
		}
	}
	
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		private GUIResultsMainWindow mainWindow;
		public MyButton(String name, GUIResultsMainWindow mainWindow){
			super(name);
			this.mainWindow=mainWindow;
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					mainWindow.newSimulation();
			 	}
			});
			
		}
	}

}
