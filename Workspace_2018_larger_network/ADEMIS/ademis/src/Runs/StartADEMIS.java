package Runs;

import AMAS.MyAMAS;
import AMAS.MyEnvironment;
import fr.irit.smac.amak.Scheduling;
import ui.MyLog;
import ui.Parameters;

public class StartADEMIS {

	public StartADEMIS() {
		
		MyLog.deleteFile();
		
		
		
		MyEnvironment network = new MyEnvironment(Scheduling.DEFAULT, null);
		
		MyAMAS amas=new MyAMAS(network, Scheduling.DEFAULT, null);
	}

}
