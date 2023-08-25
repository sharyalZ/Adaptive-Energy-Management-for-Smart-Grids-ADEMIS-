package fr.irit.smac.lxplot;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;

import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.interfaces.ILxPlotChart;
import fr.irit.smac.lxplot.interfaces.ILxPlotServer;
import fr.irit.smac.lxplot.server.LxPlotDistantServer;
import fr.irit.smac.lxplot.server.LxPlotServer;

/**
 * Main class giving access to all the functionnalities of the library
 *
 * @author Alexandre Perles
 *
 */
public class LxPlot {
	private static ReentrantLock serverLock = new ReentrantLock();

	/**
	 * Configure the connection with the default server.
	 *
	 * @param _host
	 * @param _port
	 */
	public static void configServer(final String _host, final int _port) {
		LxPlot.defaultServer = new LxPlotDistantServer(_host, _port);
	}

	/**
	 * Configure the connection to a server and set a local name. The name _name
	 * will be then used with the method getServer(String _name).
	 *
	 * @param _name
	 * @param _host
	 * @param _port
	 */
	public static void configServer(final String _name, final String _host,
			final int _port) {
		LxPlot.servers.put(_name, new LxPlotDistantServer(_host, _port));
	}

	/**
	 * Create a new server which will be accessible through network with the
	 * port _port
	 *
	 * @param _port
	 */
	public static void createServer(final int _port) {
		LxPlot.defaultServer = new LxPlotServer("default", _port);
	}

	/**
	 * Create a new server with the name _name and which will be accessible
	 * through network with the port _port. The name is only used in the
	 * server's side
	 *
	 * @param _name
	 * @param _port
	 */
	public static void createServer(final String _name, final int _port) {
		LxPlot.servers.put(_name, new LxPlotServer(_name, _port));
	}

	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name) {
		return getChart(_name, ChartType.LINE);
	}
	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final ChartType _chartType) {
		return getChart(_name, _chartType, true, -1);
	}
	
	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final ChartType _chartType, final boolean _blocking) {
		return getChart(_name, _chartType, _blocking, -1);
	}
	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final ChartType _chartType, final int _maxItemCount) {
		return LxPlot.getServer().getChart(_name, _chartType, true, _maxItemCount);
	}
	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final ChartType _chartType, final boolean _blocking, final int _maxItemCount) {
		return LxPlot.getServer().getChart(_name, _chartType, _blocking, _maxItemCount);
	}
	

	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final List<ChartType> types) {
		return getChart(_name, types, true, -1);
	}

	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final List<ChartType> types, final boolean _blocking) {
		return getChart(_name, types, _blocking, -1);
	}
	

	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final List<ChartType> types, final int _maxItemCount) {
		return LxPlot.getServer().getChart(_name, types, true, _maxItemCount);
	}
	

	/**
	 * Get the chart with the given name if any. Otherwise, create it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotChart getChart(final String _name, final List<ChartType> types, final boolean _blocking, final int _maxItemCount) {
		return LxPlot.getServer().getChart(_name, types, _blocking, _maxItemCount);
	}

	/**
	 * Get default server if any. Otherwise create it.
	 *
	 * @return
	 */
	public static ILxPlotServer getServer() {
		serverLock.lock();
		if (LxPlot.defaultServer == null) {
			LxPlot.defaultServer = new LxPlotServer("default");
		}
		serverLock.unlock();
		return LxPlot.defaultServer;
	}

	/**
	 * Get server with the given name if it exists. Otherwise create a
	 * connection to it.
	 *
	 * @param _name
	 * @return
	 */
	public static ILxPlotServer getServer(final String _name) {

		serverLock.lock();
		if (!LxPlot.servers.containsKey(_name)) {
			LxPlot.servers.put(_name, new LxPlotServer(_name));
		}
		serverLock.unlock();
		return LxPlot.servers.get(_name);
	}

	/**
	 * If no name is given for the server, this one will be used
	 */
	private static ILxPlotServer defaultServer;

	/**
	 * Server's map linking name and server
	 */
	private static Map<String, ILxPlotServer> servers = new TreeMap<String, ILxPlotServer>();
	
	public static Map<String, ILxPlotChart> getCharts(){
		return getServer().getCharts();
	}
}
