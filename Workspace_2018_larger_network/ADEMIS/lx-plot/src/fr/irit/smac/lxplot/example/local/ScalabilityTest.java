package fr.irit.smac.lxplot.example.local;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;

public class ScalabilityTest {

	public static void main(String[] args) {
		long time = System.currentTimeMillis();
		for (int i = 0; i < 20000; i++) {
			LxPlot.getChart("test", ChartType.LINE, false,1000).add(i, Math.sin(i/100.0));
			try {
				if (i % 100 == 0)
					Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("time : " + (System.currentTimeMillis() - time));
	}

}
