package fr.irit.smac.lxplot.example.local;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;

public class ConcurrencyTest {

	public static void main(String[] args) {
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for (int i=0;i<100000;i++)
					LxPlot.getChart("test", ChartType.LINE, false).add(i, Math.random());
			}
		});
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				for (int i=0;i<100000;i++)
					LxPlot.getChart("test", ChartType.LINE, false).add(i, Math.random());
			}
		});
		t1.start();
		t2.start();
	}

}
