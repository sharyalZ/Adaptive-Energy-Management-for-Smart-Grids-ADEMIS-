package fr.irit.smac.lxplot.server;

import java.io.PrintWriter;
import java.util.List;

import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.interfaces.ILxPlotChart;

/**
 * Representation of a distant chart made by a client.
 *
 * @author Alexandre Perles
 *
 */
public class LxPlotDistantChart implements ILxPlotChart {

	private final String name;
	private final PrintWriter out;

	public LxPlotDistantChart(final String _name, final ChartType _chartType, final int _maximumItemCount,
			final PrintWriter _out) {
		name = _name;
		out = _out;
		out.println("config;" + _name + ";" + _chartType.toString()+";"+_maximumItemCount);
		out.flush();
	}
	

	public LxPlotDistantChart(final String _name, final List<ChartType> _chartType, final int _maximumItemCount,
			final PrintWriter _out) {
		name = _name;
		out = _out;
		out.println("config;" + _name + ";" + _chartType.toString()+";"+_maximumItemCount);
		out.flush();
	}

	@Override
	public synchronized void add(final double _x, final double _y) {
		add("", _x, _y);
	}

	@Override
	public synchronized void add(final String _serieName, final double _x, final double _y) {
		out.println("add;" + name + ";" + _serieName + ";" + _x + ";" + _y);
		out.flush();
	}

	@Override
	public void close() {
		out.println("close;" + name);
		out.flush();
	}


	@Override
	public void addBox(List<Double> list) {
		// TODO Auto-generated method stub
		
	}

}
