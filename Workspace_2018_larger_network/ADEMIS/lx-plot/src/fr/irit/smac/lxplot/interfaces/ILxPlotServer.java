package fr.irit.smac.lxplot.interfaces;

import java.util.List;
import java.util.Map;

import fr.irit.smac.lxplot.commons.ChartType;

/**
 * Common server's interface between the server and the client.
 *
 * @author Alexandre Perles
 *
 */
public interface ILxPlotServer {
	/**
	 * Get or create the chart which is called _name and set the chart type and the blocking state
	 *
	 * @param _name
	 * @param _chartType
	 * @param _blocking
	 * @param _maxItemCount 
	 * @return
	 */
	public ILxPlotChart getChart(String _name, List<ChartType> _chartType, boolean _blocking, final int _maxItemCount);
	public ILxPlotChart getChart(String _name, ChartType _chartType, boolean _blocking, final int _maxItemCount);
	public ILxPlotChart getChart(String _name, ChartType _chartType, boolean _blocking);
	public ILxPlotChart getChart(String _name, ChartType _chartType, final int _maxItemCount);
	public ILxPlotChart getChart(String _name, ChartType _chartType);
	public ILxPlotChart getChart(String _name);

	/**
	 * Get whether charts must be in different windows or not
	 *
	 * @return
	 */
	public boolean getUniqueWindow();

	/**
	 * Remove the chart corresponding of the given name.
	 * @param name
	 */
	public void removeChart(String name);
	
	public Map<String, ILxPlotChart> getCharts();

}
