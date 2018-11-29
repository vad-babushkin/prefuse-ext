package profusians.demos.zonemanager.polygons;

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
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;

/**
 * nodes changing their polygonal zones
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class ZoneDemoPolygon02 extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    ZoneManager zoneManager;

    int round = 0;

    public ZoneDemoPolygon02() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	initZoneManager();

	addNodesToZones();

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
	nStroke.add("_hover", ColorLib.gray(50));

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.gray(255));
	nFill.add("_hover", ColorLib.gray(200));
	zoneManager.addZoneItemColorMapping(nFill);

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	ColorAction aFill = zoneManager.getZoneColorAction();

	// bundle the color actions
	ActionList colors = new ActionList();
	colors.add(nStroke);
	colors.add(nFill);

	colors.add(aFill);
	colors.add(nEdges);

	ForceDirectedLayout fdl = new ForceDirectedLayout(GRAPH, zoneManager
		.getForceSimulator(), false);

	int duration = 4234;

	ActionList catchThem = new ActionList(duration);
	catchThem.setPacingFunction(new SlowInSlowOutPacer());
	catchThem.add(colors);
	catchThem.add(new ZoneGuardAction(zoneManager));
	catchThem.add(fdl);

	catchThem.add(zoneManager.getZoneLayout(true));

	catchThem.add(new ColorAnimator(NODES));
	catchThem.add(new LocationAnimator(NODES));
	catchThem.add(new RepaintAction());

	m_vis.putAction("catchThem", catchThem);

	ActionList keepThem = new ActionList(Activity.INFINITY);
	keepThem.add(new ZoneGuardAction(zoneManager));
	keepThem.add(fdl);
	keepThem.add(new RepaintAction());

	m_vis.putAction("keepThem", keepThem);

	m_vis.alwaysRunAfter("catchThem", "keepThem");

	// set up the display
	setSize(500, 500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new DragControl());
	addControlListener(new PanControl());

    }

    public void shuffleZones() {

	m_vis.cancel("catchThem");
	m_vis.cancel("keepThem");

	rearrangeNodes();

	m_vis.run("catchThem");

    }

    public void rearrangeNodes() {

	Iterator nodes = m_vis.getVisualGroup(NODES).tuples();

	int numberOfZones = zoneManager.getNumberOfZones();
	int zoneNumber;

	while (nodes.hasNext()) {
	    NodeItem aNodeItem = (NodeItem) nodes.next();

	    zoneNumber = zoneManager.getZoneNumber(aNodeItem);

	    if ((((int) (numberOfZones * Math.random())) < 3)
		    || (zoneNumber < 0) || (zoneNumber >= numberOfZones)) {
		zoneNumber = (int) (numberOfZones * Math.random())
			% numberOfZones;

		zoneManager.addItemToZone(aNodeItem, zoneNumber);
	    }

	}

    }

    private void initDataGroups() {
	// create sample graph
	// 12 nodes
	Graph g = new Graph();

	int numberOfNodes = 22;

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

	zoneManager.addZonesFromFile("data/profusians/zonesPolygon02.xml");

    }

    
    private void addNodesToZones() {

	Iterator nodeItems = m_vis.items(NODES);

	int numberOfZones = zoneManager.getNumberOfZones();
	int[] zoneNumbers = zoneManager.getZoneNumbers();

	while (nodeItems.hasNext()) {
	    int choice = (int) (Math.random() * numberOfZones);
	    NodeItem aNodeItem = (NodeItem) nodeItems.next();
	    zoneManager.addItemToZone(aNodeItem, zoneNumbers[choice]);

	}
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
	ZoneDemoPolygon02 ad = new ZoneDemoPolygon02();
	JFrame frame = demo(ad);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	new Show(ad).start();
    }

    public static JFrame demo(ZoneDemoPolygon02 sd) {

	JFrame frame = new JFrame("polygon zones 02");
	frame.getContentPane().add(sd);
	frame.pack();

	return frame;
    }

    public static class Show extends Thread {
	ZoneDemoPolygon02 sd;

	public Show(ZoneDemoPolygon02 sd) {
	    super();
	    this.sd = sd;
	}

	public void run() {
	    int what = 0, old;
	    while (true) {

		try {

		    sd.shuffleZones();

		    sleep((long) (4000 + Math.random() * 2000));
		    old = what;
		    do {
			what = (int) (Math.random() * 6);
		    } while (false && (what == old));
		} catch (InterruptedException e) {
		}
	    }

	}
    }
}
