package ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;


public class NewPlotPan extends JPanel {

	private static final long serialVersionUID = 1L;
	private JFormattedTextField plotNameField;
	private GUIResultsController controller;
	
	public NewPlotPan(GUIResultsController controller) {
		JPanel pan=new JPanel();
		this.controller=controller;
		plotNameField = new JFormattedTextField();
		plotNameField.setValue("New Plot");
		plotNameField.setColumns(15);
		pan.add(plotNameField);
		pan.add(new MyButton("Plot", this));
		this.add(pan);
		this.setPreferredSize(new Dimension(200, 20));
	}
	
	public void newPlot() {
		String plotName = plotNameField.getText();
		controller.newPlot(plotName);
	}
	
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		private NewPlotPan newPlotPan;
		public MyButton(String name, NewPlotPan newPlotPan){
			super(name);
			this.newPlotPan=newPlotPan;
			this.addActionListener(new ActionListener(){
				@Override
			 	public void actionPerformed(ActionEvent event){
					newPlotPan.newPlot();
			 	}
			});
			
		}
	}

}
