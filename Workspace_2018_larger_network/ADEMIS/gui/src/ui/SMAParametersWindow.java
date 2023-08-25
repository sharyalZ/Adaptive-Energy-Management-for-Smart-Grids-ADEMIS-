package ui;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneLayout;

public class SMAParametersWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private static Controller controller;
	
	private JScrollPane scrollLineAgent, scrollAggregatorAgent, scrollEVAgent;
	
	private MyButton OKButton;
	
	private LineAgentPanel lineAgentPanel;
	private AggregatorAgentPanel aggregatorAgentPanel;
	private EVAgentPanel evAgentPanel;
	
	public SMAParametersWindow(Controller controller) {
		SMAParametersWindow.controller=controller;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//mainWindowListener = new MainWindowListener(this);
		//addWindowListener(mainWindowListener);
		//setBounds(300, 300, 1350, 900);
		setSize(500, 420);
		
		setTitle("SMA parameters");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setVisible(true);
		
		
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		
		
		JTabbedPane tabbedPane = new JTabbedPane();
		
		
		NumberFormat format = NumberFormat.getInstance();
		format.setMinimumFractionDigits(2);
		
		
		
		lineAgentPanel=new LineAgentPanel();
		scrollLineAgent=new JScrollPane(lineAgentPanel);
		scrollLineAgent.setLayout(new ScrollPaneLayout());
		scrollLineAgent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollLineAgent.getVerticalScrollBar().setUnitIncrement(100);
		tabbedPane.add("Line Agent", scrollLineAgent);
		
		aggregatorAgentPanel=new AggregatorAgentPanel();
		scrollAggregatorAgent=new JScrollPane(aggregatorAgentPanel);
		scrollAggregatorAgent.setLayout(new ScrollPaneLayout());
		scrollAggregatorAgent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollAggregatorAgent.getVerticalScrollBar().setUnitIncrement(100);
		tabbedPane.add("Aggregator Agent", scrollAggregatorAgent);
		
		evAgentPanel=new EVAgentPanel();
		scrollEVAgent=new JScrollPane(evAgentPanel);
		scrollEVAgent.setLayout(new ScrollPaneLayout());
		scrollEVAgent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollEVAgent.getVerticalScrollBar().setUnitIncrement(100);
		tabbedPane.add("EV Agent", scrollEVAgent);
		
		
		
		
		splitPane.setTopComponent(tabbedPane);
		
		JPanel OKPan=new JPanel();
		OKButton=new MyButton("OK");
		OKPan.add(OKButton);
		
		splitPane.setBottomComponent(OKPan);
		this.add(splitPane);
		splitPane.setResizeWeight(1);
	}
	
	public void showWindow() {
		this.setVisible(true);
		OKButton.setEnabled(false);
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////
///////////                           Getter                              /////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	public LineAgentPanel getLineAgentPanel() {
		return lineAgentPanel;
	}
	
	public AggregatorAgentPanel getAggregatorAgentPanel() {
		return aggregatorAgentPanel;
	}
	
	public EVAgentPanel getEvAgentPanel() {
		return evAgentPanel;
	}
	
	


///////////////////////////////////////////////////////////////////////////////////////////////
///////////                       Intern classes                          /////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		//public Simulation_Parameters simu;
		//public JFormattedTextField path;
		//public Load_scenario window;
		public MyButton(String name){
			super(name);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event){
					switch(((JButton) event.getSource()).getText()) {
					case "OK": controller.loadSMAParemetersPressed();
					break;
					default: JOptionPane.showMessageDialog(null, "Not a known button", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
	}
	
}
