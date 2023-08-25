package fr.irit.smac.lxplot.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.WaferMapPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.WaferMapRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.WaferMapDataset;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerXYDataset;
import org.jfree.data.time.Day;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.interfaces.ILxPlotChart;
import fr.irit.smac.lxplot.interfaces.ILxPlotServer;

/**
 * Real chart displayed by a server.
 *
 * @author Alexandre Perles
 */
public class LxPlotChart implements ILxPlotChart, Runnable {
	public static int cols = 2;
	private static MainWindow window;
	private static int chartCount = 0;
	private static JDesktopPane desktopPane;
	private static ReentrantLock frameLock = new ReentrantLock();
	private final Map<String, XYSeries> series = new TreeMap<String, XYSeries>();
	private final String name;
	private final ILxPlotServer server;
	// private JPanel chartContainer;
	private XYSeriesCollection dataset;
	private JFreeChart chart;

	private ChartType chartType = ChartType.PLOT;
	private List<ChartType> chartTypes;
	private ChartPanel chartPanel;

	private String firstSerie;
	// private JFrame chartFrame;
	private JInternalFrame internalChartFrame;
	private DefaultCategoryDataset categoryDataset;

	private DefaultPieDataset pieDataset;
	private WaferMapDataset waferDataset;
	private LinkedList<PointRequest> queue = new LinkedList<>();
	private Semaphore threadSemaphore = new Semaphore(-1);

	private PointRequest lastPoint;
	private ReentrantLock queueLock = new ReentrantLock();
	private boolean blocking;
	private int maxItemCount = -1;

	private boolean multiple = true;

	private List<Double> datasX;
	private List<Double> datasY;

	private XYSeriesCollection xyDataset;
	private XYPlot plot;
	private WaferMapPlot plot2;
	private WaferMapRenderer wafR;

	private Day d ;
	private DefaultBoxAndWhiskerXYDataset boxDataset;

	/**
	 * Default constructor
	 * @param _server
	 * 			The server.
	 */
	public LxPlotChart(final ILxPlotServer _server) {
		this("Untitled", _server);
	}

	/**
	 * Construct a chart.
	 * 
	 * @param _name
	 * 			The name.
	 * @param _chartType
	 * 			The type.
	 * @param _server
	 * 			The server.
	 * @param _blocking
	 * 			If it is blocking.
	 * @param _maxItemCount
	 * 			The max item it can have.
	 */
	public LxPlotChart(final String _name, final ChartType _chartType, final ILxPlotServer _server, final boolean _blocking, final int _maxItemCount) {
		name = _name;
		chartType = _chartType;
		server = _server;
		blocking = _blocking;
		maxItemCount = _maxItemCount;
		datasX = new ArrayList<Double>();
		datasY = new ArrayList<Double>();
		multiple = (chartType == ChartType.MULTIPLE);

		LxPlotChart.chartCount++;
		// getChartContainer(true).add(getChartPanel());
		LxPlotChart.getDesktopPane().add(getChartInternalFrame());
		// getChartContainer().revalidate();
		// getChartContainer().repaint();

		new Thread(this).start();
	}

	/**
	 * Construct a chart.
	 * 
	 * @param _name
	 * 			The name.
	 * @param _chartType
	 * 			The type.
	 * @param _server
	 * 			The server.
	 * @param _blocking
	 * 			If it is blocking.
	 * @param _maxItemCount
	 * 			The max item it can have.
	 */
	public LxPlotChart(final String _name, final List<ChartType> _chartType, final ILxPlotServer _server, final boolean _blocking, final int _maxItemCount) {
		name = _name;
		chartTypes = new ArrayList<ChartType>();
		chartTypes = _chartType;
		server = _server;
		blocking = _blocking;
		maxItemCount = _maxItemCount;
		datasX = new ArrayList<Double>();
		datasY = new ArrayList<Double>();

		LxPlotChart.chartCount++;
		// getChartContainer(true).add(getChartPanel());
		LxPlotChart.getDesktopPane().add(getChartInternalFrame());
		// getChartContainer().revalidate();
		// getChartContainer().repaint();

		new Thread(this).start();
	}

	/**
	 * Construct a chart with several type.
	 * 
	 * @param _name
	 * 			The name.
	 * @param _chartType
	 * 			The type.
	 * @param _server
	 * 			The server.
	 * @param _blocking
	 * 			If it is blocking.
	 * @param _maxItemCount
	 * 			The max item it can have.
	 * @param _multiple
	 * 			If there is more than one type.
	 * 			
	 */
	public LxPlotChart(final String _name, final ChartType _chartType, final ILxPlotServer _server, final boolean _blocking, final int _maxItemCount,boolean _multiple) {
		name = _name;
		chartType = _chartType;
		server = _server;
		blocking = _blocking;
		maxItemCount = _maxItemCount;
		datasX = new ArrayList<Double>();
		datasY = new ArrayList<Double>();
		multiple = _multiple;
		LxPlotChart.chartCount++;
		// getChartContainer(true).add(getChartPanel());
		LxPlotChart.getDesktopPane().add(getChartInternalFrame());
		// getChartContainer().revalidate();
		// getChartContainer().repaint();

		new Thread(this).start();
	}

	/**
	 * Construct a default chart with a name.
	 * 
	 * @param _name
	 * 			The name.
	 * @param _server
	 * 			The server.
	 */
	public LxPlotChart(final String _name, final ILxPlotServer _server) {
		this(_name, ChartType.LINE, _server, true, -1);
	}

	/**
	 * Return the desktopPane.
	 * 
	 * @return desktopPane
	 */
	public synchronized static JDesktopPane getDesktopPane() {
		if (LxPlotChart.desktopPane == null) {
			LxPlotChart.desktopPane = new JDesktopPane() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Component add(Component comp) {
					Component t = super.add(comp);
					refreshLayout();
					return t;
				}

			};

			LxPlotChart.desktopPane.setDesktopManager(new CustomDesktopManager());
			LxPlotChart.desktopPane.setPreferredSize(new Dimension(900, 600));
		}
		return LxPlotChart.desktopPane;
	}

	/**
	 * Return the mainWindow
	 * 
	 * @return window
	 */
	private synchronized static MainWindow getMainWindow() {
		frameLock.lock();
		if (LxPlotChart.window == null) {
			LxPlotChart.window = new MainWindow("LxPlot");
			LxPlotChart.window.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			LxPlotChart.window.getFrame().addWindowListener(new WindowListener() {

				@Override
				public void windowOpened(WindowEvent e) {

				}

				@Override
				public void windowIconified(WindowEvent e) {

				}

				@Override
				public void windowDeiconified(WindowEvent e) {

				}

				@Override
				public void windowDeactivated(WindowEvent e) {

				}

				@Override
				public void windowClosing(WindowEvent e) {

				}

				@Override
				public void windowClosed(WindowEvent e) {
					JInternalFrame[] allFrames = getDesktopPane().getAllFrames();
					for (JInternalFrame jInternalFrame : allFrames) {
						jInternalFrame.doDefaultCloseAction();
					}
				}

				@Override
				public void windowActivated(WindowEvent e) {

				}
			});
			LxPlotChart.window.getFrame().addComponentListener(new ComponentListener() {

				@Override
				public void componentShown(ComponentEvent e) {

				}

				@Override
				public void componentResized(ComponentEvent e) {
					refreshLayout();
				}

				@Override
				public void componentMoved(ComponentEvent e) {

				}

				@Override
				public void componentHidden(ComponentEvent e) {

				}
			});
			LxPlotChart.window.getFrame().getContentPane().add((LxPlotChart.getDesktopPane()), BorderLayout.CENTER);
			LxPlotChart.window.getFrame().pack();
			LxPlotChart.window.getFrame().setVisible(true);
		}
		frameLock.unlock();
		return LxPlotChart.window;
	}

	// private JFrame getFrame() {
	// if (chartFrame == null) {
	// chartFrame = new JFrame(frameName);
	// chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// JScrollPane jScrollPane = new JScrollPane(getChartContainer());
	// jScrollPane.setPreferredSize(new Dimension(320, 240));
	// chartFrame.getContentPane().add(jScrollPane, BorderLayout.CENTER);
	// chartFrame.pack();
	// chartFrame.setVisible(true);
	// }
	// return chartFrame;
	// }

	// private JPanel getChartContainer() {
	// return getChartContainer(false);
	// }

	/**
	 * Refresh the window to adjust the size of all non icon window.
	 */
	public static void refreshLayout() {
		int x = 0;
		int width = 10, height = 10;
		JInternalFrame[] allFrames = getDesktopPane().getAllFrames();

		List<JInternalFrame> visibleFrames = new ArrayList<>();
		for (JInternalFrame jInternalFrame : allFrames) {
			if (!jInternalFrame.isIcon()) {
				visibleFrames.add(jInternalFrame);
			}
		}

		int w = getDesktopPane().getWidth();
		int h = getDesktopPane().getHeight() - (visibleFrames.size()!=allFrames.length?30:0);

		if (visibleFrames.isEmpty())
			return;
		if (visibleFrames.size() == 1) {
			width = w;
			height = h;
		} else if (visibleFrames.size() <= cols) {
			width = w / visibleFrames.size();
			height = h;
		} else {
			width = w / cols;
			int rowCount = (int) (visibleFrames.size() / cols) + (visibleFrames.size() % cols == 0 ? 0 : 1);
			height = h / rowCount;
		}
		if (width < 10)
			width = 10;
		if (height < 200)
			height = 200;

		for (JInternalFrame frame : visibleFrames) {
			frame.setLocation(width * (x % cols), height * ((int) (x / cols)));
			frame.setSize(width, height);
			x++;
		}
	}

	@Override
	public synchronized void addBox(List<Double> list) {
		if(d == null)
			d = new Day(new Date());
		
		List<Double> l = new ArrayList<Double>();
		l.add(3.0);
		BoxAndWhiskerItem item = new BoxAndWhiskerItem(
				list.get(0), list.get(1), list.get(2),
				list.get(3), list.get(4), list.get(5),
				null, null, new ArrayList<Double>());
		this.getBoxDataset().add(new Date(), item);
	}
	
	@Override
	public synchronized void add(final double _x, final double _y) {
		if (firstSerie == null) {
			firstSerie = "Default";
		}
		add(firstSerie, _x, _y);
	}

	@Override
	public synchronized void add(final String _serieName, final double _x, final double _y) {
		lastPoint = new PointRequest(_serieName, _x, _y);

		if (blocking)
			drawPoint(lastPoint);
		else {
			queueLock.lock();
			queue.add(lastPoint);
			threadSemaphore.release();
			queueLock.unlock();
		}
	}

	@Override
	public void close() {
		getChartInternalFrame().dispose();
	}

	// public void close() {
	// getChartContainer().remove(getChartPanel());
	// getChartContainer().revalidate();
	// getChartContainer().repaint();
	// }

	// private JPanel getChartContainer(boolean _refresh) {
	// if (chartContainer == null) {
	// chartContainer = new JPanel();
	// }
	// if (_refresh) {
	// switch (chartCount) {
	// case 1:
	// case 2:
	// chartContainer.setLayout(new GridLayout(0, chartCount));
	// break;
	// default:
	// chartContainer.setLayout(new GridLayout(0, 3));
	// break;
	// }
	// }
	// return chartContainer;
	// }
	/**
	 * Return the internalChartFrame, if it is null then it is created.
	 * 
	 * @return internalChartFrame
	 */
	private synchronized JInternalFrame getChartInternalFrame() {
		if (internalChartFrame == null) {
			internalChartFrame = new JInternalFrame(getInternalFrameName(), true, true, true,
					true);
			internalChartFrame.addInternalFrameListener(new InternalFrameListener() {

				@Override
				public void internalFrameOpened(InternalFrameEvent e) {

				}

				@Override
				public void internalFrameIconified(InternalFrameEvent e) {

				}

				@Override
				public void internalFrameDeiconified(InternalFrameEvent e) {

				}

				@Override
				public void internalFrameDeactivated(InternalFrameEvent e) {

				}

				@Override
				public void internalFrameClosing(InternalFrameEvent e) {

				}

				@Override
				public void internalFrameClosed(InternalFrameEvent e) {

					internalChartFrame = null;
					chartPanel = null;
					chart = null;
					server.removeChart(name);
				}

				@Override
				public void internalFrameActivated(InternalFrameEvent e) {

				}
			});
			// final int wx = 900 / LxPlotChart.cols;
			// final int wy = 600 / LxPlotChart.rows;
			// internalChartFrame
			// .setBounds(
			// wx
			// * ((LxPlotChart.chartCount - 1) % LxPlotChart.cols),
			// wy
			// * ((LxPlotChart.chartCount - 1) / LxPlotChart.cols),
			// wx, wy);

			internalChartFrame.add(getChartPanel());
			internalChartFrame.setVisible(true);
		}
		return internalChartFrame;
	}

	/**
	 * Return the name of the internalFrame.
	 * 
	 * @return name + " (" + (LxPlotChart.chartCount) + ") "+(!blocking?"ASYNC":"")
	 */
	private String getInternalFrameName() {
		return name + " (" + (LxPlotChart.chartCount) + ") "+(!blocking?"ASYNC":"");
	}

	/**
	 * Return the ChartPanel.
	 * 
	 * @return chartPanel
	 */
	private synchronized ChartPanel getChartPanel() {
		// we put the chart into a panel
		if (chartPanel == null) {
			chartPanel = new ChartPanel(getJFreeChart());
			//			border = BorderFactory.createTitledBorder(name);
			//			chartPanel.setBorder(border);
			chartPanel.setMinimumSize(new Dimension(10, 10));
			// default size
			// chartPanel.setPreferredSize(new Dimension(300, 300));
			this.chartPanel.setDomainZoomable(true);
			this.chartPanel.setRangeZoomable(true);
			this.chartPanel.setMouseWheelEnabled(true);
		}
		return chartPanel;
	}

	/**
	 * Return a XYSeriesCollection, if it is null, it is created.
	 * 
	 * @return dataset
	 */
	private synchronized XYSeriesCollection getDataset() {
		if (dataset == null) {
			dataset = new XYSeriesCollection();
		}
		return dataset;
	}


	/**
	 * Return a XYSeriesCollection, if it is null, it is created.
	 * 
	 * @return xyDataset
	 */
	private synchronized XYSeriesCollection getDatasetMultiple() {
		if (xyDataset == null) {
			xyDataset = new XYSeriesCollection();
		}
		return xyDataset;
	}

	/**
	 * Return the chart
	 * In the case null a new chart is created depending of the type.
	 * 
	 * @return chart
	 */
	public synchronized JFreeChart getJFreeChart() {
		if(chartTypes != null)
			getMultipleJFreeChart();
		else{
			if (chart == null) {
				NumberAxis range;

				switch (chartType) {
				//Creation of a line chart
				case LINE:
					chart = ChartFactory.createXYLineChart("", // chart
							// title
							"", // x axis label
							"", // y axis label
							getDataset(), // data
							PlotOrientation.VERTICAL, true, // include legend
							true, // tooltips
							false // urls
							);

					plot = (XYPlot) chart.getPlot();

					range = (NumberAxis) plot.getRangeAxis();
					range.setAutoRange(true);
					range.setAutoRangeIncludesZero(false);
					plot.setBackgroundPaint(Color.white);
					plot.setRangeGridlinePaint(Color.black);

					//plot.setRenderer(1,new SamplingXYLineRenderer());
					break;
					//Creation of a Plot chart
				case PLOT:
					chart = ChartFactory.createScatterPlot("", // chart
							// title
							"", // x axis label
							"", // y axis label
							getDataset(), // data
							PlotOrientation.VERTICAL, true, // include legend
							true, // tooltips
							false // urls
							);

					plot = (XYPlot) chart.getPlot();

					range = (NumberAxis) plot.getRangeAxis();
					range.setAutoRange(true);
					range.setAutoRangeIncludesZero(false);
					plot.setBackgroundPaint(Color.white);
					plot.setRangeGridlinePaint(Color.black);
					break;
					//Creation of a Bar chart
				case BAR:
					chart = ChartFactory.createBarChart("", "", "", getCategoryDataset(), PlotOrientation.VERTICAL, true,
							true, false);

					break;
					//Creation of a Pie chart	
				case PIE:
					chart = ChartFactory.createPieChart3D("",getPieDataset(),true,true,false);
					break;
					//Creation of a Wafer chart TODO
				case WAFER:
					wafR = new WaferMapRenderer(10,10);
					wafR.setSeriesPaint(0, Color.RED);
					wafR.setSeriesPaint(1, Color.BLUE);
					wafR.setSeriesPaint(2, Color.YELLOW);
					wafR.setSeriesPaint(3, Color.GREEN);
					wafR.setSeriesPaint(4, Color.ORANGE);
					//TODO
					plot2 = new WaferMapPlot(getWaferDataset());
					plot2.setRenderer(wafR);
					wafR.setPlot(plot2);
					chart = new JFreeChart("",JFreeChart.DEFAULT_TITLE_FONT, plot2,true);
					break;
					//Creation of a bar and line chart	
				case MULTIPLE: 
					chart = multiple();
					break;
				case BOX:

					DateAxis domainAxis = new DateAxis("Time");
					NumberAxis rangeAxis = new NumberAxis("");
					XYBoxAndWhiskerRenderer renderer = new XYBoxAndWhiskerRenderer();
					
					plot = new XYPlot(getBoxDataset(), domainAxis, rangeAxis, renderer);

					chart = new JFreeChart(
							"Box chart", 
							JFreeChart.DEFAULT_TITLE_FONT,
							plot, 
							true);
					break;
				case SPIDER:
					chart = new JFreeChart(
							"",
							new Font("SansSerif", Font.BOLD, 14),
							new SpiderWebPlot(getCategoryDataset()),
							true);
					break;
				case SHAPE:
					plot = new XYPlot();
					domainAxis = new DateAxis("");   
					domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);  
					plot.setDomainAxis(domainAxis);
					rangeAxis = new NumberAxis("Value");   
					plot.setRangeAxis(rangeAxis);
					plot.setDataset(getDataset());
					plot.setRenderer(new XYLineAndShapeRenderer());
					chart = new JFreeChart(
							"LineAndShape chart", 
							JFreeChart.DEFAULT_TITLE_FONT,
							plot, 
							true);
				}
			}
		}
		return chart;
	}

	private void getMultipleJFreeChart() {
		int nbChart = 0;
		plot = new XYPlot();
		for(ChartType type : this.chartTypes){
			switch(type){
			case PLOT: 
				plot.setDataset(nbChart,getDataset());
				XYDotRenderer plotRenderer = new XYDotRenderer();
				plot.setRenderer(nbChart,plotRenderer);
				nbChart++;
				break;
			case SHAPE: 
				plot.setDataset(nbChart,getDataset());
				XYLineAndShapeRenderer shapeRenderer = new XYLineAndShapeRenderer();
				plot.setRenderer(nbChart,shapeRenderer);
				nbChart++;
				break;
			case LINE:
				plot.setDataset(nbChart,getDataset());
				XYItemRenderer lineRenderer = new StandardXYItemRenderer();
				plot.setRenderer(nbChart,lineRenderer);
				nbChart++;
				break;
			case BAR:
				plot.setDataset(nbChart,getDataset());
				XYItemRenderer barRenderer = new XYBarRenderer(0.20);
				plot.setRenderer(nbChart,barRenderer);
				nbChart++;
				break;
			case PIE:
				/*plot.setDataset(nbChart,getDataset());
				XYItemRenderer pieRenderer = new XY(0.20);
				plot.setRenderer(nbChart,barRenderer);
				nbChart++;*/
				break;
			default:
				break;
			}


		}
		DateAxis domainAxis = new DateAxis("");   
		domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);  
		plot.setDomainAxis(domainAxis);
		ValueAxis rangeAxis = new NumberAxis("Value");   
		plot.setRangeAxis(rangeAxis);
		chart = new JFreeChart(plot);

	}

	/**
	 * Initialize two renderer, one xyRenderer and a Bar renderer.
	 * 
	 * @return new JFreeChart
	 */
	private synchronized JFreeChart multiple(){
		XYItemRenderer renderer1 = new XYBarRenderer(0.20);   
		DateAxis domainAxis = new DateAxis("");   
		domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);   
		ValueAxis rangeAxis = new NumberAxis("Value");   
		plot = new XYPlot(getDataset(), domainAxis, rangeAxis, renderer1);    

		// add a second dataset and renderer...   
		XYItemRenderer renderer2 = new StandardXYItemRenderer();
		plot.setDataset(1, getDatasetMultiple());   
		plot.setRenderer(1, renderer2);   

		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
		return new JFreeChart("Multiple", JFreeChart.DEFAULT_TITLE_FONT, plot, true); 
	}

	/**
	 * Return a DefaultCategoryDataset for the bar chart
	 * 
	 * @return categoryDataset
	 */
	private synchronized DefaultCategoryDataset getCategoryDataset() {
		if (categoryDataset == null)
			categoryDataset = new DefaultCategoryDataset();
		return categoryDataset;
	}

	/**
	 * Creates a sample dataset.
	 * 
	 * @return A sample dataset.
	 */
	private DefaultBoxAndWhiskerXYDataset getBoxDataset() {
		if(boxDataset == null)
			boxDataset = new DefaultBoxAndWhiskerXYDataset("Default");

		return boxDataset;
	}

	/**
	 * Return a DefaultPieDataset
	 * 
	 * @return pieDataset
	 */
	private synchronized DefaultPieDataset getPieDataset() {
		if (pieDataset == null)
			pieDataset = new DefaultPieDataset();
		return pieDataset;
	}

	/**
	 * Return a waferDataset
	 * 
	 * @return waferDataset;
	 */
	private synchronized WaferMapDataset getWaferDataset() {
		if (waferDataset == null)
			waferDataset = new WaferMapDataset(30,30);
		return waferDataset;
	}

	/**
	 * Return a XYSeries or create it.
	 * 
	 * @param _serieName
	 * 			The name of the serie.
	 * @return series.get(_serieName)
	 */
	private synchronized XYSeries getSeries(final String _serieName) {
		if (firstSerie == null) {
			firstSerie = _serieName;
		}
		if (!series.containsKey(_serieName)) {
			final XYSeries xySeries = new XYSeries(_serieName, true, (chartType == ChartType.PLOT));
			if (maxItemCount >0)
				xySeries.setMaximumItemCount(maxItemCount);
			series.put(_serieName, xySeries);
			getDataset().addSeries(xySeries);
			if(multiple)
				getDatasetMultiple().addSeries(xySeries);
		}
		return series.get(_serieName);
	}

	/**
	 * 
	 * @author Marcillaud Guilhem
	 *
	 * Intern class for a point corresponding to a serie_name
	 */
	private class PointRequest {
		public final String serieName;
		public final double x, y;

		/**
		 * 
		 * @param serieName
		 * 			The name of the serie.
		 * @param x
		 * @param y
		 */
		public PointRequest(String serieName, double x, double y) {
			super();
			this.serieName = serieName;
			this.x = x;
			this.y = y;
		}

	}

	@Override
	public void run() {
		while (true) {
			try {
				PointRequest point;
				getMainWindow().setStatus("READY");
				threadSemaphore.acquire();
				getMainWindow().setStatus("WORKING ...");
				queueLock.lock();
				point = queue.removeFirst();
				queueLock.unlock();
				drawPoint(point);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//TODO
	/**
	 * Add the new point to the dataset to update the chart.
	 * 
	 * @param _pointRequest
	 * 			The point to add.
	 */
	private void drawPoint(PointRequest _pointRequest) {
		if(chartTypes != null){
			getSeries(_pointRequest.serieName).add(_pointRequest.x, _pointRequest.y);
		}
		else{
			switch (chartType) {
			case PLOT:
				getSeries(_pointRequest.serieName).add(_pointRequest.x, _pointRequest.y);
				break;
			case SHAPE:
				getSeries(_pointRequest.serieName).add(_pointRequest.x, _pointRequest.y);
				break;
			case LINE:
				getSeries(_pointRequest.serieName).addOrUpdate(_pointRequest.x, _pointRequest.y);
				datasX.add(_pointRequest.x);
				datasY.add(_pointRequest.y);
				//calcul of the trending line
				/*double sx = 0;
				double scx = 0;
				double sy = 0;
				double prod = 0;
				for(int i = 0; i< datasX.size();i++){
					sx += datasX.get(i);
					scx += Math.pow(datasX.get(i),2);
					sy += datasY.get(i);
					prod += datasX.get(i) * datasY.get(i);
				}
				double b = (datasX.size()*prod - (sx*sy))/((datasX.size()*scx)-Math.pow(sx, 2));
				double a = sy/datasX.size() - b*(sx/datasX.size());
				for(int i =0; i < datasX.get(datasX.size()-1);i++){
					double res = b*i+a;
					getSeries("Trending").addOrUpdate(i,res);
				}*/
				break;
			case BAR:
				getCategoryDataset().addValue(_pointRequest.y, _pointRequest.serieName, String.valueOf(_pointRequest.x));
				break;
			case PIE:
				getPieDataset().insertValue(0,""+_pointRequest.x,_pointRequest.y);
				break;
			case WAFER:
				//TODO
				/*System.out.println(waferx);
			System.out.println(wafery);
			getWaferDataset().addValue(_pointRequest.y, waferx, wafery);
			plot2 = new WaferMapPlot(getWaferDataset(),wafR);
			wafR.setPlot(plot2);
			chart = ChartFactory.createWaferMapChart("", getWaferDataset(), PlotOrientation.VERTICAL, true, true, false);
			LxPlotChart.getDesktopPane().remove(internalChartFrame);
			internalChartFrame = new JInternalFrame();
			chartPanel = new ChartPanel(chart);
			internalChartFrame.add(chartPanel);
			internalChartFrame.setVisible(true);
			LxPlotChart.getDesktopPane().add(internalChartFrame);
			if(wafery == 29 ){
				wafery = 0;
				if(waferx == 29){
					waferx = 0;
				}else
					waferx++;
			}
			else
				wafery++;*/
				break;
			case MULTIPLE:
				getSeries(_pointRequest.serieName).addOrUpdate(_pointRequest.x, _pointRequest.y);
				break;
			/*case BOX:
				ArrayList<Double> listBox = new ArrayList<Double>();
				listBox.add(_pointRequest.x);
				getBoxDataset().add(listBox, "Value "+_pointRequest.y, "Type "+_pointRequest.y);
				break;*/
			case SPIDER:
				getCategoryDataset().addValue(_pointRequest.y, _pointRequest.serieName, String.valueOf(_pointRequest.x));
				break;
			case BOX:
				System.err.println("Unavailable type BOX");
				break;
			default:
				break;
			}
			if (!getMainWindow().getFrame().isVisible())
				getMainWindow().getFrame().setVisible(true);
		}

	}

	/**
	 * All internal frame become icon.
	 */
	public static void minimizeAll() {
		for (JInternalFrame jif : desktopPane.getAllFrames()) {
			try {
				jif.setIcon(true);
			} catch (PropertyVetoException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Change the name of the frame.
	 * 
	 * @param _name
	 * 			The new name.
	 */
	public static void setFrameName(String _name) {
		getMainWindow().setFrameName(_name);
	}

	/**
	 * Set if the chart is multiple or not.
	 * 
	 * @param multiple
	 */
	public void setMultiple(boolean multiple){
		this.multiple = multiple;
	}

}
