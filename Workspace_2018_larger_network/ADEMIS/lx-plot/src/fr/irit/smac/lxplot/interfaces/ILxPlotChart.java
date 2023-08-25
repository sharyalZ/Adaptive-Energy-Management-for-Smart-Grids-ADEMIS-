package fr.irit.smac.lxplot.interfaces;

import java.util.List;

/**
 * Common chart's interface between the server and the client.
 *
 * @author Alexandre Perles
 *
 */
public interface ILxPlotChart {
	/**
	 * Add the point (_x,_y) to the first serie of the chart.
	 *
	 * @param _x
	 * @param _y
	 */
	public void add(double _x, double _y);

	/**
	 * Add the point (_x, _y) to the _serie serie of the chart.
	 *
	 * @param _serieName
	 * @param _x
	 * @param _y
	 */
	public void add(String _serieName, double _x, double _y);

	/**
	 * Close the chart on the server.
	 */
	public void close();

	/**
	 * Add the data for a box chart
	 * 
	 * @param list
	 */
	public void addBox(List<Double> list);

}
