package fr.irit.smac.lxplot.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.interfaces.ILxPlotChart;
import fr.irit.smac.lxplot.interfaces.ILxPlotServer;

/**
 * Server which is able to receive requests from local or distant client.
 *
 * @author Alexandre Perles
 *
 */
public class LxPlotServer implements ILxPlotServer, Runnable {

	private ServerSocket socket;
	private boolean running = false;
	private final Map<String, ILxPlotChart> charts = new TreeMap<String, ILxPlotChart>();
	private boolean uniqueWindow;

	/**
	 * Create server NOT accessible through the network
	 *
	 * @param _name
	 */
	public LxPlotServer(final String _name) {
		LxPlotChart.setFrameName(_name);
	}

	/**
	 * Create server accessible through the network and locally
	 *
	 * @param _name
	 * @param _port
	 */
	public LxPlotServer(final String _name, final int _port) {
		this(_name + " - localhost:" + _port);
		try {
			socket = new ServerSocket(_port);
			running = true;
			new Thread(this).start();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean getUniqueWindow() {
		return uniqueWindow;
	}

	@Override
	public void run() {
		while (running) {
			try {
				final Socket clientSocket = socket.accept();
				new LxPlotConnectedClient(clientSocket, this);
			} catch (final IOException e) {
				e.printStackTrace();
			}

		}
	}


	@Override
	public synchronized ILxPlotChart getChart(String _name, ChartType _chartType, boolean _blocking, final int _maxItemCount) {
		if (!charts.containsKey(_name)) {
			charts.put(_name, new LxPlotChart(_name, _chartType, this, _blocking, _maxItemCount));
		}
		return charts.get(_name);
	}
	
	@Override
	public synchronized ILxPlotChart getChart(String _name, List<ChartType> _chartType, boolean _blocking, final int _maxItemCount) {
		if (!charts.containsKey(_name)) {
			charts.put(_name, new LxPlotChart(_name, _chartType, this, _blocking, _maxItemCount));
		}
		return charts.get(_name);
	}

	@Override
	public void removeChart(String name) {
		
		charts.remove(name);
	}

	@Override
	public synchronized ILxPlotChart getChart(String _name, ChartType _chartType, boolean _blocking) {
		return getChart(_name, _chartType, _blocking, -1);
	}

	@Override
	public synchronized ILxPlotChart getChart(String _name, ChartType _chartType, int _maxItemCount) {
		return getChart(_name, _chartType, true, _maxItemCount);
	}

	@Override
	public synchronized ILxPlotChart getChart(String _name, ChartType _chartType) {
		return getChart(_name, _chartType, true, -1);
	}

	@Override
	public synchronized ILxPlotChart getChart(String _name) {
		return getChart(_name, ChartType.LINE, true, -1);
	}

	@Override
	public Map<String, ILxPlotChart> getCharts() {
		return charts;
	}

}
