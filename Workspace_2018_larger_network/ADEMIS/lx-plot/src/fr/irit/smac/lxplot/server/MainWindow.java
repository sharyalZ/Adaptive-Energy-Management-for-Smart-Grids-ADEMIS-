package fr.irit.smac.lxplot.server;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.irit.smac.lxplot.Constants;
import fr.irit.smac.lxplot.commons.Data;
import fr.irit.smac.lxplot.commons.XJFrame;

public class MainWindow {

	private XJFrame frame;
	private JLabel status;
	private Component horizontalGlue;
	private JLabel lblNewLabel_1;
	private JButton btnNewButton_4;
	private JButton btnForceQuit;
	private Component horizontalStrut;
	private JPanel panel_6;
	private JLabel lblNewLabel_2;
	private JSpinner spinner_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow("");
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @param title
	 */
	public MainWindow(String title) {
		initialize(title);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String title) {
		frame = new XJFrame(title);
		frame.setBounds(100, 100, 608, 562);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
				lblNewLabel_1 = new JLabel("lx-plot v" + Constants.VERSION);
				panel.add(lblNewLabel_1);
				
				horizontalStrut = Box.createHorizontalStrut(20);
				panel.add(horizontalStrut);

		status = new JLabel("Status");
		panel.add(status);
		
		btnForceQuit = new JButton("Force quit");
		btnForceQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
				horizontalGlue = Box.createHorizontalGlue();
				panel.add(horizontalGlue);
		
		panel_6 = new JPanel();
		panel.add(panel_6);
		panel_6.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		
		lblNewLabel_2 = new JLabel("Columns");
		panel_6.add(lblNewLabel_2);
		
		spinner_1 = new JSpinner(new SpinnerNumberModel(Integer.parseInt(Data.get("lxplot", "cols", ()->"1")), 1, 100, 1));
		spinner_1.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = (int) spinner_1.getValue();
				Data.set("lxplot", "cols", String.valueOf(value));
				LxPlotChart.cols = value;
				LxPlotChart.refreshLayout();
			}
		});
		int value = (int) spinner_1.getValue();
		LxPlotChart.cols = value;
		LxPlotChart.refreshLayout();
		panel_6.add(spinner_1);
		
		btnNewButton_4 = new JButton("Minimize all");
		panel.add(btnNewButton_4);
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LxPlotChart.minimizeAll();
			}
		});
		panel.add(btnForceQuit);
	}

	/**
	 * Return the frame.
	 * 
	 * @return frame.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Change the status
	 * 
	 * @param string
	 */
	public void setStatus(String string) {
		status.setText(string);
	}

	/**
	 * Change the name of the frame
	 * 
	 * @param _name
	 */
	public void setFrameName(String _name) {
		frame.setName(_name);
	}
}
