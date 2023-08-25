package fr.irit.smac.lxplot.example.network;

import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;

/**
 * Example showing the distant connection to the server.
 *
 * @author Alexandre Perles
 *
 */
public class Client {

	public static void main(final String[] args) {
		// Server's configuration
		LxPlot.configServer("My server", "localhost", 6090);

		// Create "My chart" on "My server" and plot two points
		LxPlot.getServer("My server").getChart("My chart").add(1, 2);
		LxPlot.getServer("My server").getChart("My chart").add(2, 3);

		// Plot two points in serie "My serie"
		LxPlot.getServer("My server").getChart("My chart")
		.add("My serie", 3, 2.5);
		LxPlot.getServer("My server").getChart("My chart")
		.add("My serie", 5, 2);

		// Preconfigure chart by setting name and type
		LxPlot.getServer("My server")
		.getChart("My chart 2", ChartType.PLOT);

		// Plot 3 points
		LxPlot.getServer("My server").getChart("My chart 2").add(1, 2);
		LxPlot.getServer("My server").getChart("My chart 2").add(2, 3);
		LxPlot.getServer("My server").getChart("My chart 2").add(3, 2.5);

		// Close charts
		// LxPlot.getChart("My chart", "My server").close();
		// LxPlot.getChart("My chart 2", "My server").close();
	}
}
