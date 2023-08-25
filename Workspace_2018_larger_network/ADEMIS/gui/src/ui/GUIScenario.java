package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneLayout;



public class GUIScenario extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String scenario;
	
	protected JSplitPane splitPane;
	
	
	private JPanel bottomPanel;
	private JScrollPane scrollEV, scrollPV, scrollAggregator, scrollLines;
	
	private EVsScenarioPanel EVPanel;
	private PVsScenarioPanel PVPanel;
	private AggregatorScenarioPanel aggregatorPanel;
	private LineScenarioPanel lineScenarioPanel;
	
	private MyButton OKButton;
	
	
	public GUIScenario(Controller controller) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(700, 400);
		
		setTitle(scenario);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
	
		
		
		
		splitPane= new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		this.add(splitPane);
		
		
		
		//Creation of EVPanel in a scroll
		EVPanel=new EVsScenarioPanel();
		scrollEV=new JScrollPane(EVPanel);
		scrollEV.setLayout(new ScrollPaneLayout());
		scrollEV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollEV.getVerticalScrollBar().setUnitIncrement(100);
		
		//Creation of PVPanel in a scroll
		PVPanel=new PVsScenarioPanel();
		scrollPV=new JScrollPane(PVPanel);
		scrollPV.setLayout(new ScrollPaneLayout());
		scrollPV.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPV.getVerticalScrollBar().setUnitIncrement(100);
		
		
		//Creation of AggregatorPanel
		aggregatorPanel=new AggregatorScenarioPanel();
		scrollAggregator=new JScrollPane(aggregatorPanel);
		scrollAggregator.setLayout(new ScrollPaneLayout());
		scrollAggregator.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollAggregator.getVerticalScrollBar().setUnitIncrement(100);
		
		//Creation of LinePanel
		lineScenarioPanel=new LineScenarioPanel();
		scrollLines=new JScrollPane(lineScenarioPanel);
		scrollLines.setLayout(new ScrollPaneLayout());
		scrollLines.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollLines.getVerticalScrollBar().setUnitIncrement(100);
		
		//Creation of JTabbedPane
		JTabbedPane tab = new JTabbedPane(JTabbedPane.NORTH);
		tab.add("EVs", scrollEV);
		tab.add("PVs", scrollPV);
		tab.add("Aggregator", scrollAggregator);
		tab.add("Lines", scrollLines);
		
		
		bottomPanel=new JPanel();
		JPanel OKPanel=new JPanel();
		OKButton=new MyButton("OK", controller);
		OKPanel.add(OKButton);
		bottomPanel.add(OKPanel);
		splitPane.setTopComponent(tab);
		splitPane.setBottomComponent(bottomPanel);
		splitPane.setResizeWeight(1);
	}
	
	public EVsScenarioPanel getEVPanel() {
		return EVPanel;
	}
	
	public PVsScenarioPanel getPVPanel() {
		return PVPanel;
	}
	
	public AggregatorScenarioPanel getAggregatorPanel() {
		return aggregatorPanel;
	}
	
	public LineScenarioPanel getLineScenarioPanel() {
		return lineScenarioPanel;
	}
	
	public void showWindow() {
		this.setVisible(true);
		OKButton.setEnabled(false);
	}
	
	
	
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		private Controller controller;
		public MyButton(String name, Controller controller){
			super(name);
			this.controller=controller;
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					switch(((JButton) event.getSource()).getText()) {
						case "OK": ((MyButton) event.getSource()).getController().loadScenarioButtonPressed();
							break;
						default: JOptionPane.showMessageDialog(null, "Not a known button", "Error", JOptionPane.ERROR_MESSAGE);
					}
			 	}
			});
			
		}
		public Controller getController() {
			return controller;
		}
	}

}
