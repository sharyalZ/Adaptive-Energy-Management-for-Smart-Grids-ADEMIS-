package fr.irit.smac.lxplot.commons;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.function.Consumer;

import javax.swing.JFrame;

/**
 * Extension of JFrame aiming at storing position and size of windows
 * 
 * @author Alexandre Perles
 *
 */
public class XJFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4534786729272462775L;
	private Consumer<XJFrame> disposeListener;

	public XJFrame(String title) {
		super(title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (disposeListener != null)
			disposeListener.accept(this);
	}

	@Override
	public void pack() {
		super.pack();
		String title = getTitle();
		setSize(Integer.parseInt(Data.get("gui_log", title + "_width", () -> 600 + "")),
				Integer.parseInt(Data.get("gui_log", title + "_height", () -> 600 + "")));
		setLocation(Integer.parseInt(Data.get("gui_log", title + "_x", () -> 0 + "")),
				Integer.parseInt(Data.get("gui_log", title + "_y", () -> 0 + "")));
		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				Data.set("gui_log", title + "_width", "" + getWidth());
				Data.set("gui_log", title + "_height", "" + getHeight());
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				Data.set("gui_log", title + "_x", "" + getX());
				Data.set("gui_log", title + "_y", "" + getY());
			}

			@Override
			public void componentHidden(ComponentEvent arg0) {
			}
		});
	}

	public void setDisposeListener(Consumer<XJFrame> listener) {
		disposeListener = listener;
	}
}
