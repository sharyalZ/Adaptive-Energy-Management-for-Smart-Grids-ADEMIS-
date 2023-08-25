package fr.irit.smac.lxplot.example.network;

import fr.irit.smac.lxplot.LxPlot;

/**
 * Example showing the creation of an instance of the server.
 *
 * @author Alexandre Perles
 *
 */
public class Server {

	public static void main(final String[] args) {
		LxPlot.createServer(6090);
	}
}
