package fr.irit.smac.lxplot.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.interfaces.ILxPlotChart;
import fr.irit.smac.lxplot.interfaces.ILxPlotServer;

/**
 * Representation of the server made by a client
 *
 * @author Alexandre Perles
 *
 */
public class LxPlotDistantServer implements ILxPlotServer {
	private final Map<String, ILxPlotChart> charts = new TreeMap<String, ILxPlotChart>();
	private Socket socket;
	private PrintWriter out;
	private boolean uniqueWindow;

	public LxPlotDistantServer(final String _host, final int _port) {
		try {
			socket = new Socket(_host, _port);
			out = new PrintWriter(socket.getOutputStream());
		} catch (final UnknownHostException e) {
			
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean getUniqueWindow() {
		return uniqueWindow;
	}

	@Override
	public synchronized ILxPlotChart getChart(String _name, ChartType _chartType, boolean _blocking, final int _maxItemCount) {
		if (!charts.containsKey(_name)) {
			charts.put(_name,
					new LxPlotDistantChart(_name, _chartType, _maxItemCount, out));
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

	@Override
	public ILxPlotChart getChart(String _name, List<ChartType> _chartType, boolean _blocking, int _maxItemCount) {
		if (!charts.containsKey(_name)) {
			charts.put(_name,
					new LxPlotDistantChart(_name, _chartType, _maxItemCount, out));
		}
		return charts.get(_name);
	}
}
