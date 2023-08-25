package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class ScenarioPanel extends JPanel{

	protected String defaulConfigPath;
	
	
	
	public ScenarioPanel(String defaulConfigPath) {
		final String dir = System.getProperty("user.dir");
		System.out.println("*** user.dir="+dir);
		Path workingPath=Paths.get(dir);
		System.out.println("*** workingpath="+workingPath);
        String communicationPath=workingPath.getParent().getParent().toString()+"\\Configuration";
		this.defaulConfigPath=communicationPath+defaulConfigPath;
		JPanel LoadPanel=new JPanel();
		LoadPanel.add(new MyButton("Load Scenario", this));
		this.add(LoadPanel);
		this.parametersInitialisation();
	}
	
	protected void parametersInitialisation(){
		System.out.println("init");
	}
	
	
	public void loadScenario() {
		JFileChooser fileChooser= new JFileChooser();
		fileChooser.setCurrentDirectory(new java.io.File(defaulConfigPath));
		fileChooser.showOpenDialog(null);
		String ConfigFilePath=fileChooser.getSelectedFile().getAbsolutePath();
		try {
			Map<String, List<String>> config=Reader.readFile(ConfigFilePath);
			defineNewConfig(config);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	protected void defineNewConfig(Map<String, List<String>> config) {
		System.out.println(config);
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////                       Intern classes                          /////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	private static class MyButton extends JButton{
		private static final long serialVersionUID = 1L;
		private ScenarioPanel scenarioPanel;
		public MyButton(String name, ScenarioPanel scePanel){
			super(name);
			scenarioPanel=scePanel;
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent event){
					scePanel.loadScenario();
				}
			});

		}
	}

}
