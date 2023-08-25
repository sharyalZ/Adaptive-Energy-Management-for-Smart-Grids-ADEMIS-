package ui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.JCheckBox;



public class ResultsFrame extends JFrame{

	private static Controller controller;
	
	private JPanel pan, pan2;
	
	private Map<String, List<String>> results; 
	
	JSplitPane splitPane;
	
	public ResultsFrame(Controller controller) {
		this.controller=controller;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		//mainWindowListener = new MainWindowListener(this);
		//addWindowListener(mainWindowListener);
		//setBounds(300, 300, 1350, 900);
		setSize(500, 400);
		
		setTitle("Results");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocation(500, 0);
		
		
		setVisible(true);
		pan=new JPanel();
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		pan.add(new MyButton("Update", this));
		pan.setSize(50, 50);
		pan2=new JPanel();
		//this.setLayout(new GridLayout(1,2));
		//splitPane.add(pan);
		splitPane.setLeftComponent(pan);
		
		//splitPane.add(pan2);
		
		this.setContentPane(splitPane);
		
		
	}
	
	private void updateResults() {
		results=controller.updateResults();
		//System.out.println(results);
		
		
		JPanel myPpan=new JPanel();
		//myPpan.setLayout(new GridLayout(56,1));
		
	    //this.add(scroll1);
	    //scroll1.getVerticalScrollBar().setUnitIncrement(16);
	    
	    
	    
	    JTabbedPane tab = new JTabbedPane(JTabbedPane.LEFT);
	   
	    Object[] plotsObject=results.keySet().toArray();
	    String plots[] =  Arrays.copyOf(results.keySet().toArray(), results.size(), String[].class);
	    Arrays.sort(plots);
	    JPanel[] pan_i=new  JPanel[plots.length];
	    CheckBox[][] Checks=new  CheckBox[plots.length][1000];
	    for(int i=0;i<plots.length;i++){
	    	pan_i[i]=new JPanel();
	    	pan_i[i].setLayout(new GridLayout(300,1));
	    	tab.add(plots[i].toString(),pan_i[i]);
	    	Object[] seriesObject = results.get(plots[i].toString()).toArray();
	    	String series[] =  Arrays.copyOf(seriesObject, seriesObject.length, String[].class);
	    	Arrays.sort(series);
	    	 for(int j=0;j<series.length;j++){
	    		 Checks[i][j] = new CheckBox(series[j].toString(),plots[i].toString(),series[j].toString() , controller);
	    		 pan_i[i].add(Checks[i][j]);
	    	 }
	    }
		
	    //JScrollPane scroll2=new JScrollPane(tab);
	    //scroll2.getVerticalScrollBar().setUnitIncrement(10);
	   // pan.add(scroll2);
	    
	    
	    
	  
	    
	    myPpan.add(tab);
	    JScrollPane scroll2=new JScrollPane(myPpan);
	    scroll2.getVerticalScrollBar().setUnitIncrement(50);
	    //JScrollPane scroll1=new JScrollPane(myPpan);
	    //scroll1.setLayout(new ScrollPaneLayout());
		//scroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//pan2.add(tab);
		//myPpan.setLayout(new GridLayout(100,100));
		
		//JPanel testPan=new JPanel();
		//testPan.add(tab);
		splitPane.setRightComponent(scroll2);
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////
///////////                       Intern classes                          /////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
	
	class CheckBox extends JCheckBox{
		public String plot, serie;
		private Controller controller;
		public CheckBox(String name,String p, String s, Controller controller){
			super(name);
			this.controller=controller;
			plot=p;
			serie=s;
			this.addActionListener(new ActionListener(){
			 	public void actionPerformed(ActionEvent event){
			 		//res.Plot(((CheckBox) event.getSource()).get_plot(),((CheckBox) event.getSource()).get_serie());
			 		//((CheckBox) event.getSource()).setEnabled(false);
			 		if((((CheckBox) event.getSource()).isSelected())){
			 			controller.plotResult(plot, serie);
			 		}else {
			 			controller.deletePlot(plot, serie);
			 		}
			 		
			 		//System.out.println("test :" + ((CheckBox) event.getSource()).get_plot() );
			 	}
			});
			if(serie=="avec VE"&&plot=="Frequence"){
				//res.Plot(plot,serie);
				this.setSelected(true);
		 		this.setEnabled(false);
			}
		}
		
		public String get_plot(){ return plot;}
		public String get_serie(){ return serie;}
	}
	
	
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		private ResultsFrame frame;
		//public Simulation_Parameters simu;
		//public JFormattedTextField path;
		//public Load_scenario window;
		public MyButton(String name,ResultsFrame MyFrame){
			super(name);
			frame=MyFrame;
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event){
					switch(((JButton) event.getSource()).getText()) {
					case "Update": frame.updateResults();
					break;
					default: JOptionPane.showMessageDialog(null, "Not a known button", "Error", JOptionPane.ERROR_MESSAGE);
					}
					/*if(this.getText()=="test") {
	controller.loadButtonPressed();
	}
	setPanelEnabled(ParamatersPanel,false);*/
				}
			});

		}
	}

}
