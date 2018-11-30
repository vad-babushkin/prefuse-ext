//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.radialexplorer;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.animate.ColorAnimator;
import edu.berkeley.guir.prefuse.action.animate.PolarLocationAnimator;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.ColorMap;
import edu.berkeley.guir.prefuse.util.display.ExportDisplayAction;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceConfigAction;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;
import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class RadialGraphExplorer extends JFrame {
	public static final String GRAPH_FRIENDSTER = "src/friendster.xml";
	public static final String GRAPH_TERROR = "src/terror.xml";
	public static final String nameField = "label";
	private ItemRegistry registry;
	private Graph g;
	private Display display;
	private ForceSimulator fsim;
	private RadialTreeLayout radialLayout;
	private ActionList layout;
	private ActionList update;
	private ActionList animate;
	private ActionList forces;
	private boolean runForces = true;

	public static void main(String[] args) {
		String infile = "src/friendster.xml";
		if (args.length > 0) {
			infile = args[0];
		}

		new RadialGraphExplorer(infile);
	}

	public RadialGraphExplorer(String datafile) {
		super("Radial Graph Explorer -- " + datafile);

		try {
			URL input = this.getClass().getResource(datafile);
			this.g = (new XMLGraphReader()).loadGraph(input);
			this.registry = new ItemRegistry(this.g);
			TextItemRenderer nodeR = new TextItemRenderer();
			nodeR.setRoundedCorner(8, 8);
			nodeR.setMaxTextWidth(75);
			nodeR.setAbbrevType(0);
			Renderer edgeR = new DefaultEdgeRenderer();
			this.registry.setRendererFactory(new DefaultRendererFactory(nodeR, edgeR));
			ColorFunction colorFunction = new RadialGraphExplorer.RadialColorFunction(3);
			this.layout = new ActionList(this.registry);
			this.layout.add(new TreeFilter(true));
			this.layout.add(this.radialLayout = new RadialTreeLayout());
			this.layout.add(colorFunction);
			this.animate = new ActionList(this.registry, 1500L, 20L);
			this.animate.setPacingFunction(new SlowInSlowOutPacer());
			this.animate.add(new PolarLocationAnimator());
			this.animate.add(new ColorAnimator());
			this.animate.add(new RepaintAction());
			this.animate.alwaysRunAfter(this.layout);
			this.update = new ActionList(this.registry);
			this.update.add(colorFunction);
			this.update.add(new RepaintAction());
			this.fsim = new ForceSimulator();
			this.fsim.addForce(new NBodyForce(-0.1F, 15.0F, 0.9F));
			this.fsim.addForce(new DragForce());
			this.forces = new ActionList(this.registry, 1000L);
			this.forces.add(new ForceDirectedLayout(this.fsim, true));
			this.forces.add(new RepaintAction());
			this.forces.alwaysRunAfter(this.animate);
			this.display = new Display(this.registry);
			this.display.setSize(600, 600);
			this.display.setBackground(Color.WHITE);
			this.display.addControlListener(new FocusControl(this.layout));
			this.display.addControlListener(new DragControl());
			this.display.addControlListener(new PanControl());
			this.display.addControlListener(new ZoomControl());
			this.display.addControlListener(new NeighborHighlightControl(this.update));
			this.initMenus();
			this.setDefaultCloseOperation(3);
			this.getContentPane().add(this.display);
			this.pack();
			this.setVisible(true);
			this.layout.runNow();
		} catch (Exception var6) {
			var6.printStackTrace();
		}

	}

	private void initMenus() {
		JMenuItem openItem = new JMenuItem("Open...");
		JMenuItem exportItem = new JMenuItem("Export Image...");
		JMenuItem exitItem = new JMenuItem("Exit");
		final JCheckBoxMenuItem scaleItem = new JCheckBoxMenuItem("Toggle Layout AutoScale", true);
		final JCheckBoxMenuItem forcesItem = new JCheckBoxMenuItem("Toggle Forces", true);
		JMenuItem paramItem = new JMenuItem("Configure Forces...");
		exportItem.addActionListener(new ExportDisplayAction(this.display));
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		scaleItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean val = scaleItem.getState();
				RadialGraphExplorer.this.radialLayout.setAutoScale(val);
			}
		});
		forcesItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				boolean val = forcesItem.getState();
				RadialGraphExplorer.this.forces.setEnabled(val);
			}
		});
		paramItem.addActionListener(new ForceConfigAction(this, this.fsim));
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(openItem);
		fileMenu.add(exportItem);
		fileMenu.add(exitItem);
		JMenu optMenu = new JMenu("Options");
		optMenu.add(scaleItem);
		optMenu.add(forcesItem);
		optMenu.add(paramItem);
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(optMenu);
		this.setJMenuBar(menuBar);
	}

	public class RadialColorFunction extends ColorFunction {
		private Color graphEdgeColor;
		private Color highlightColor;
		private Color focusColor;
		private ColorMap colorMap;

		public RadialColorFunction(int thresh) {
			this.graphEdgeColor = Color.LIGHT_GRAY;
			this.highlightColor = new Color(50, 50, 255);
			this.focusColor = new Color(255, 50, 50);
			this.colorMap = new ColorMap(ColorMap.getInterpolatedMap(thresh + 1, Color.RED, Color.BLACK), 0.0D, (double)thresh);
		}

		public Paint getFillColor(VisualItem item) {
			if (item instanceof NodeItem) {
				return Color.WHITE;
			} else if (item instanceof AggregateItem) {
				return Color.LIGHT_GRAY;
			} else {
				return (Paint)(item instanceof EdgeItem ? this.getColor(item) : Color.BLACK);
			}
		}

		public Paint getColor(VisualItem item) {
			if (item.isFocus()) {
				return this.focusColor;
			} else if (item.isHighlighted()) {
				return this.highlightColor;
			} else if (item instanceof NodeItem) {
				int d = ((NodeItem)item).getDepth();
				return this.colorMap.getColor((double)d);
			} else if (item instanceof EdgeItem) {
				EdgeItem e = (EdgeItem)item;
				if (e.isTreeEdge()) {
					int d1 = ((NodeItem)e.getFirstNode()).getDepth();
					int d2 = ((NodeItem)e.getSecondNode()).getDepth();
					int dx = Math.max(d1, d2);
					return this.colorMap.getColor((double)dx);
				} else {
					return this.graphEdgeColor;
				}
			} else {
				return Color.BLACK;
			}
		}
	}
}
