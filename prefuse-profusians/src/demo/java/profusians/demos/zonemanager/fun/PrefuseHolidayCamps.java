package profusians.demos.zonemanager.fun;

import java.util.HashMap;
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
import profusians.zonemanager.zone.Zone;

/**
 * zonefree - sometimes
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class PrefuseHolidayCamps extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    ZoneManager zoneManager;

    int round = 0;

    public PrefuseHolidayCamps() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	initZoneManager();
	
	// set up the renderers
	// draw the nodes as basic shapes
	Renderer nodeR = new ShapeRenderer(20);

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeR);

	zoneManager.addZoneRenderer(drf, ZoneManager.CONVEXHULLZONERENDERER);

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

	// ------------------ layout

	ForceDirectedLayout fdlZone = new ForceDirectedLayout(GRAPH,
		zoneManager.getForceSimulator(), false);

	ForceDirectedLayout fdlChaos = new ForceDirectedLayout(GRAPH);

	int duration = 2000;

	ActionList catchThem = new ActionList(duration);
	catchThem.setPacingFunction(new SlowInSlowOutPacer());
	catchThem.add(colors);
	catchThem.add(new ZoneGuardAction(zoneManager));
	catchThem.add(zoneManager
		.getZoneLayout(ZoneManager.CONVEXHULLZONERENDERER));
	catchThem.add(fdlZone);

	catchThem.add(new ColorAnimator(NODES));
	catchThem.add(new LocationAnimator(NODES));
	catchThem.add(new RepaintAction());

	m_vis.putAction("catchThem", catchThem);

	ActionList keepThem = new ActionList(Activity.INFINITY);
	keepThem.add(new ZoneGuardAction(zoneManager));
	keepThem.add(fdlZone);
	keepThem.add(zoneManager
		.getZoneLayout(ZoneManager.CONVEXHULLZONERENDERER));
	keepThem.add(new RepaintAction());

	m_vis.putAction("keepThem", keepThem);
	m_vis.alwaysRunAfter("catchThem", "keepThem");

	ActionList freeThem = new ActionList(Activity.INFINITY);

	freeThem.add(colors);
	freeThem.add(fdlChaos);
	freeThem.add(new RepaintAction());
	m_vis.putAction("freeThem", freeThem);

	// set up the display

	setSize(800, 500);
	pan(400, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new DragControl());
	addControlListener(new PanControl());

    }

    public void zoneRound() {

	m_vis.cancel("catchThem");
	m_vis.cancel("keepThem");
	m_vis.cancel("freeThem");

	sleep(100);
	zoneManager.setAllZonesVisible(true);

	rearrangeNodes();

	m_vis.run("catchThem");

    }

    public void freeRound() {
	m_vis.cancel("catchThem");
	m_vis.cancel("keepThem");

	sleep(100);
	zoneManager.removeAllItems();

	zoneManager.setAllZonesVisible(false);

	m_vis.run("freeThem");

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


    public void rearrangeNodes() {
	Iterator nodes = m_vis.getVisualGroup(NODES).tuples();

	int numberOfZones = zoneManager.getNumberOfZones();
	int[] zoneNumbers = zoneManager.getZoneNumbers();

	int choice;

	while (nodes.hasNext()) {
	    NodeItem aNodeItem = (NodeItem) nodes.next();
	    if ((Math.random()>0.7) || (zoneManager.getZoneNumber(aNodeItem) == -1))  {
		choice = (int) (numberOfZones * Math.random())
		% numberOfZones;
		zoneManager.addItemToZone(aNodeItem, zoneNumbers[choice]);
	    }
	}
    }

    private void initDataGroups() {
	// create sample graph

	Graph g = new Graph();
	int numberOfNodes = 44;
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

	zoneManager.addZonesFromFile("data/profusians/zones_prefuseHolidayCamps.xml");

	// setting the colors

	HashMap allZones = zoneManager.getZones();
	Iterator iter = allZones.values().iterator();

	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();

	    int r = (int) (Math.random() * 244) + 10;
	    int g = (int) (Math.random() * 244) + 10;
	    int b = (int) (Math.random() * 216) + 30;
	    aZone.getColors().setItemColor(ColorLib.rgb(r, g, b));
	    aZone.getColors().setFillColor(ColorLib.rgba(r, g, b, 111));

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
	PrefuseHolidayCamps ad = new PrefuseHolidayCamps();
	JFrame frame = demo(ad);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	new ZoneShow(ad).start();
    }

    public static JFrame demo(PrefuseHolidayCamps sd) {

	JFrame frame = new JFrame(
	"z o n e m a n a g e r  |  prefuse holiday camps");
	frame.getContentPane().add(sd);
	frame.pack();

	return frame;
    }

    public static class ZoneShow extends Thread {
	PrefuseHolidayCamps sd;

	public ZoneShow(PrefuseHolidayCamps sd) {
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

		    what = (int) (Math.random() * 7);

		} catch (InterruptedException e) {
		}
	    }
	}
    }

    private void sleep(int ms) {
	try {
	    Thread.sleep(ms);
	} catch (Exception ignore) {

	}
    }
} // end of class PrefuseHolidayCamps

