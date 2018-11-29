package profusians.demos.zonemanager.fun;

import java.util.Iterator;

import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import profusians.demos.zonemanager.fun.zonefactories.TwoTruth_ZoneFactory;
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;

/**
 * flexible non convex polygon zones - enjoy
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class TwoTruth extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    ZoneManager m_zoneManager;

    boolean m_lastRoundWithZones;

    public TwoTruth() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	initZoneManager();

	/**
         * calling the following method here will set size of both zones to zero
         * size since no nodes have been added to zones yet
         */
	m_zoneManager.recalculateFlexibility();

	// set up the renderers
	// draw the nodes as basic shapes
	Renderer nodeR = new ShapeRenderer(20);

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeR);

	m_zoneManager.addZoneRenderer(drf);

	m_vis.setRendererFactory(drf);

	// set up the visual operators
	// first set up all the color actions
	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(ColorLib.gray(100));
	nStroke.add("_hover", ColorLib.gray(50));

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.rgb(2550, 0, 0));
	nFill.add("_hover", ColorLib.gray(200));
	m_zoneManager.addZoneItemColorMapping(nFill);

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(200));

	ColorAction aFill = m_zoneManager.getZoneColorAction();

	// bundle the color actions
	ActionList colors = new ActionList();
	colors.add(nStroke);
	colors.add(nFill);
	colors.add(aFill);
	colors.add(nEdges);

	// ------------------ layout

	ForceDirectedLayout fdlZone = new ForceDirectedLayout(GRAPH,
		m_zoneManager.getForceSimulator(), false);

	ForceDirectedLayout fdlChaos = new ForceDirectedLayout(GRAPH, true);

	int duration = 3234;

	ActionList catchThem = new ActionList(duration);
	catchThem.setPacingFunction(new SlowInSlowOutPacer());
	catchThem.add(colors);
	catchThem.add(new ZoneGuardAction(m_zoneManager));
	catchThem.add(fdlZone);
	catchThem.add(m_zoneManager.getZoneLayout(true));
	catchThem.add(new ColorAnimator(NODES));
	catchThem.add(new LocationAnimator(NODES));
	catchThem.add(new RepaintAction());

	m_vis.putAction("catchThem", catchThem);

	ActionList shuffleThem = new ActionList(duration);
	shuffleThem.setPacingFunction(new SlowInSlowOutPacer());
	shuffleThem.add(colors);
	shuffleThem.add(new ZoneGuardAction(m_zoneManager));
	shuffleThem.add(fdlZone);
	shuffleThem.add(m_zoneManager.getZoneLayout());
	shuffleThem.add(new ColorAnimator(NODES));
	shuffleThem.add(new LocationAnimator(NODES));
	shuffleThem.add(new RepaintAction());

	m_vis.putAction("shuffleThem", shuffleThem);

	ActionList keepThem = new ActionList(Activity.INFINITY);
	keepThem.add(new ZoneGuardAction(m_zoneManager));
	keepThem.add(fdlZone);
	keepThem.add(new RepaintAction());

	m_vis.putAction("keepThem", keepThem);
	m_vis.alwaysRunAfter("catchThem", "keepThem");
	m_vis.alwaysRunAfter("shuffleThem", "keepThem");

	ActionList freeThem = new ActionList(duration);
	freeThem.setPacingFunction(new SlowInSlowOutPacer());
	freeThem.add(colors);
	freeThem.add(fdlChaos);
	freeThem.add(m_zoneManager.getZoneLayout(true));
	freeThem.add(new ColorAnimator(NODES));
	freeThem.add(new LocationAnimator(NODES));
	freeThem.add(new RepaintAction());

	m_vis.putAction("freeThem", freeThem);

	ActionList keepFree = new ActionList(Activity.INFINITY);

	keepFree.add(colors);
	keepFree.add(fdlChaos);
	keepFree.add(new RepaintAction());
	m_vis.putAction("keepFree", keepFree);

	m_vis.alwaysRunAfter("freeThem", "keepFree");

	// set up the display
	setSize(800, 700);
	pan(400, 350);

	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new DragControl());
	addControlListener(new PanControl());

    }

    public void zoneRound() {

	m_vis.cancel("keepThem");
	m_vis.cancel("keepFree");

	try {
	    Thread.sleep(100);
	} catch (Exception ignore) {

	}
	rearrangeNodes();

	m_zoneManager.recalculateFlexibility();

	if (m_lastRoundWithZones) {
	    m_vis.run("shuffleThem");
	} else {
	    m_vis.run("catchThem");
	}

	m_lastRoundWithZones = true;
    }

    public void freeRound() {

	m_vis.cancel("keepThem");
	m_vis.cancel("keepFree");

	try {
	    Thread.sleep(100);
	} catch (Exception ignore) {
	}

	m_zoneManager.removeAllItems();

	if (m_lastRoundWithZones) {
	    m_zoneManager.recalculateFlexibility();
	    m_vis.run("freeThem");
	} else {
	    m_vis.run("keepFree");
	}

	m_lastRoundWithZones = false;

    }

    public void rearrangeNodes() {
	Iterator nodes = m_vis.getVisualGroup(NODES).tuples();

	int numberOfZones = m_zoneManager.getNumberOfZones();
	int[] zoneNumbers = m_zoneManager.getZoneNumbers();

	int choice;

	while (nodes.hasNext()) {
	    NodeItem aNodeItem = (NodeItem) nodes.next();
	    if ((Math.random()>0.5) || (m_zoneManager.getZoneNumber(aNodeItem) == -1))  {
		choice = (int) (numberOfZones * Math.random())
		% numberOfZones;
		m_zoneManager.addItemToZone(aNodeItem, zoneNumbers[choice]);
	    }
	}
    }

    private void initDataGroups() {
	// create sample graph

	Graph g = new Graph();
	int numberOfNodes = 111;
	for (int i = 0; i < numberOfNodes; i++) {
	    g.addNode();
	}

	for (int i = 0; i < numberOfNodes; ++i) {
	    int source = (int) (Math.random() * numberOfNodes);
	    int shift = 1 + (int) (Math.random() * (numberOfNodes - 1));
	    int target = (source + shift) % numberOfNodes;

	    g.addEdge(source, target);

	}

	// add visual data groups
	VisualGraph vg = m_vis.addGraph(GRAPH, g);
	m_vis.setInteractive(EDGES, null, false);
	m_vis.setValue(NODES, null, VisualItem.SHAPE, new Integer(
		Constants.SHAPE_ELLIPSE));
    }

    private void initZoneManager() {

	ForceSimulatorRemovableForces fsim = getForceSimulator();

	m_zoneManager = new ZoneManager(m_vis, fsim);

	m_zoneManager.setZoneFactory(new TwoTruth_ZoneFactory());

	m_zoneManager.addZonesFromFile("data/profusians/zones_twoTruth.xml");

    }

    private ForceSimulatorRemovableForces getForceSimulator() {

	float gravConstant = -0.6f; // -1.0f;
	float minDistance = 100f; // -1.0f;
	float theta = 0.1f; // 0.9f;

	float drag = 0.01f; // 0.01f;

	float springCoeff = 1E-9f; // 1E-4f;
	float defaultLength = 150f; // 50;

	ForceSimulatorRemovableForces fsim;

	fsim = new ForceSimulatorRemovableForces();

	fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
	fsim.addForce(new DragForce(drag));
	fsim.addForce(new SpringForce(springCoeff, defaultLength));

	return fsim;
    }

    public static void main(String[] argv) {
	TwoTruth ad = new TwoTruth();
	JFrame frame = demo(ad);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	new ZoneShow(ad).start();
    }

    public static JFrame demo(TwoTruth sd) {

	JFrame frame = new JFrame("enjoy");
	frame.getContentPane().add(sd);
	frame.pack();

	return frame;
    }

    public static class ZoneShow extends Thread {
	TwoTruth sd;

	int what = 0;

	public ZoneShow(TwoTruth sd) {
	    super();
	    this.sd = sd;
	}

	public void run() {

	    while (true) {

		try {
		    if (what > 0) {
			sd.zoneRound();
		    } else {
			sd.freeRound();
		    }
		    what = (what + 1) % 4;
		    sleep(6266);

		} catch (InterruptedException ignore) {
		}
	    }
	}
    }
} 

