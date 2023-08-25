package Runs;

import AMAS.MyAMAS;
import AMAS.MyEnvironment;
import fr.irit.smac.amak.Scheduling;

public class TestAMAS {

	public static void main(String[] args) {

		MyEnvironment network = new MyEnvironment(Scheduling.DEFAULT, null);
		
		new MyAMAS(network, Scheduling.DEFAULT, null);

	}

}
