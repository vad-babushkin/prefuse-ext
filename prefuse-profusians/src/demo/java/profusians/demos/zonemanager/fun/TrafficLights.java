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
import profusians.demos.zonemanager.fun.zonefactories.TrafficLights_ZoneFactory;
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;

/**
 */

public class TrafficLights extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    ZoneManager zoneManager;

    int round = 0;

    public TrafficLights() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	initZoneManager();

	zoneManager.recalculateFlexibility(); // sets the flexible zone to the
                                                // size if they contan no nodes

	// set up the renderers
	// draw the nodes as basic shapes
	Renderer nodeR = new ShapeRenderer(20);

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeR);

	zoneManager.addZoneRenderer(drf);

	m_vis.setRendererFactory(drf);

	// set up the visual operators
	// first set up all the color actions
	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(ColorLib.gray(100));
	nStroke.add("_hover", ColorLib.gray(200));

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.rgb(0, 0, 200));
	nFill.add("_hover", ColorLib.rgb(0, 0, 100));

	zoneManager.addZoneItemColorMapping(nFill);

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(200));

	ColorAction aFill = zoneManager.getZoneColorAction();

	// bundle the color actions
	ActionList colors = new ActionList();
	colors.add(nStroke);
	colors.add(nFill);

	colors.add(aFill);
	colors.add(nEdges);

	// ------------------ layout

	ForceDirectedLayout fdlZone = new ForceDirectedLayout(GRAPH,
		zoneManager.getForceSimulator(), false);

	ForceDirectedLayout fdlChaos = new ForceDirectedLayout(GRAPH, true);

	int duration = 3234;

	ActionList catchThem = new ActionList(duration);
	catchThem.setPacingFunction(new SlowInSlowOutPacer());
	catchThem.add(colors);
	catchThem.add(new ZoneGuardAction(zoneManager));
	catchThem.add(fdlZone);
	catchThem.add(zoneManager.getZoneLayout(true));
	catchThem.add(new ColorAnimator(NODES));
	catchThem.add(new LocationAnimator(NODES));
	catchThem.add(new RepaintAction());

	m_vis.putAction("catchThem", catchThem);

	ActionList keepThem = new ActionList(Activity.INFINITY);
	keepThem.add(new ZoneGuardAction(zoneManager));
	keepThem.add(fdlZone);
	keepThem.add(colors);
	keepThem.add(new RepaintAction());

	m_vis.putAction("keepThem", keepThem);
	m_vis.alwaysRunAfter("catchThem", "keepThem");

	ActionList freeThem = new ActionList(duration / 2);
	freeThem.setPacingFunction(new SlowInSlowOutPacer());
	freeThem.add(colors);
	freeThem.add(fdlChaos);
	freeThem.add(zoneManager.getZoneLayout(true));
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
	setSize(500, 500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new DragControl());
	addControlListener(new PanControl());

	// set things running
	// m_vis.run("layout");
    }

    public void zoneRound() {

	m_vis.cancel("keepFree");
	m_vis.cancel("keepThem");

	try {
	    Thread.sleep(100);
	} catch (Exception ignore) {
	}

	rearrangeNodes();

	zoneManager.recalculateFlexibility();

	m_vis.run("catchThem");

    }

    public void freeRound() {
	m_vis.cancel("keepFree");
	m_vis.cancel("keepThem");

	try {
	    Thread.sleep(100);
	} catch (Exception ignore) {
	}

	zoneManager.removeAllItems();

	zoneManager.recalculateFlexibility();

	m_vis.run("freeThem");

    }

    public void rearrangeNodes() {
	Iterator nodes = m_vis.getVisualGroup(NODES).tuples();

	int numberOfZones = zoneManager.getNumberOfZones();
	int[] zoneNumbers = zoneManager.getZoneNumbers();

	int choice;

	while (nodes.hasNext()) {
	    NodeItem aNodeItem = (NodeItem) nodes.next();
	    if ((Math.random()>0.5) || (zoneManager.getZoneNumber(aNodeItem) == -1))  {
		choice = (int) (numberOfZones * Math.random())
		% numberOfZones;
		zoneManager.addItemToZone(aNodeItem, zoneNumbers[choice]);
	    }
	}
    }

    private void initDataGroups() {
	// create sample graph

	Graph g = new Graph();
	int numberOfNodes = 37;
	for (int i = 0; i < numberOfNodes; ++i) {
	    g.addNode();
	}

	for (int i = 0; i < 1.5 * numberOfNodes; ++i) {
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

	zoneManager = new ZoneManager(m_vis, fsim);
	zoneManager.setZoneFactory(new TrafficLights_ZoneFactory(zoneManager));

	zoneManager.addZonesFromFile("data/profusians/zones_trafficLights.xml");

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
	TrafficLights ad = new TrafficLights();
	JFrame frame = demo(ad);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	new ZoneShow(ad).start();
    }

    public static JFrame demo(TrafficLights sd) {

	JFrame frame = new JFrame("z o n e m a n a g e r | traffic lights");
	frame.getContentPane().add(sd);
	frame.pack();

	return frame;
    }

    public static class ZoneShow extends Thread {
	TrafficLights sd;

	public ZoneShow(TrafficLights sd) {
	    super();
	    this.sd = sd;
	}

	public void run() {
	    int what = 0, old = -1;
	    while (true) {

		try {
		    if (what > 0) {
			sd.zoneRound();
		    } else if (old != 0) {
			sd.freeRound();
		    }

		    sleep((long) (4000 + Math.random() * 2000));
		    old = what;

		    what = (int) (Math.random() * 6);

		} catch (InterruptedException e) {
		}
	    }

	}
    }
} // end of class TrafficLights

