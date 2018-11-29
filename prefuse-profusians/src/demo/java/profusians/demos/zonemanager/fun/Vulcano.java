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
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.CircularZoneShape;

/**
 */

public class Vulcano extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    ZoneManager zoneManager;

    int round = 0;

    int step = 1;

    ActionList catchThem;

    public Vulcano() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	initZoneManager();

	addNodesToZones(round++);

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
	nFill.setDefaultColor(ColorLib.rgb(0, 0, 200));
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

	catchThem = new ActionList(74);

	catchThem.setPacingFunction(new SlowInSlowOutPacer());
	catchThem.add(colors);
	catchThem.add(new ZoneGuardAction(zoneManager));
	catchThem.add(fdl);

	catchThem.add(zoneManager.getZoneLayout(false, false));

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

	// set things running
	// m_vis.run("layout");
    }

    public void shuffleZones() {

	m_vis.cancel("catchThem");
	m_vis.cancel("keepThem");

	if (round > zoneManager.getNumberOfZones() + 2) { // a little
                                                                // break please
	    step = -1;
	} else if ((step == -1)
		&& (round == zoneManager.getNumberOfZones() - 1)) {
	    catchThem.setDuration(2000);
	} else if (round < 1) {
	    step = 1;
	} else {
	    catchThem.setDuration(74);
	}

	addNodesToZones(round);
	round += step;
	m_vis.run("catchThem");

    }

    private void initDataGroups() {
	// create sample graph
	// 12 nodes
	Graph g = new Graph();

	int numberOfNodes = 32;

	for (int i = 0; i < numberOfNodes; ++i) {
	    g.addNode();
	}

	for (int i = 0; i < 1.1 * numberOfNodes; ++i) {
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

	if (true) {
	    zoneManager.addZonesFromFile("data/zones_vulcano.xml");
	} else {

	    for (int i = 0; i < 111; i++) {
		zoneManager.createAndAddZone(new CircularZoneShape(0, 0,
			20 + 2 * i), new ZoneColors(ColorLib.rgb(150 - i, 0,
			2 * i), ColorLib.rgba(150 - i, 0, 2 * i, 111)));
	    }
	}

    }

    private void addNodesToZones(int choice) {

	Iterator nodeItems = m_vis.items(NODES);

	// wired but working, just a quick hack to reverse the process ...
	choice = Math.max(0, zoneManager.getNumberOfZones() + 1
		- choice);
	
	//bad hack, takes for granted that zone numbers are starting from 0 ...

	while (nodeItems.hasNext()) {
	    NodeItem aNodeItem = (NodeItem) nodeItems.next();
	    zoneManager.addItemToZone(aNodeItem, choice);

	}
    }

    
    
    private ForceSimulatorRemovableForces getForceSimulator() {

	float gravConstant = -1.1f; // -1.0f;
	float minDistance = -1.0f; // -1.0f;
	float theta = 0.9f; // 0.9f;

	float drag = 0.017f; // 0.01f;

	float springCoeff = 1E-7f; // 1E-4f;
	float defaultLength = 50f; // 50;

	ForceSimulatorRemovableForces fsim;

	fsim = new ForceSimulatorRemovableForces();

	fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
	fsim.addForce(new DragForce(drag));
	fsim.addForce(new SpringForce(springCoeff, defaultLength));

	return fsim;
    }

    public static void main(String[] argv) {
	Vulcano ad = new Vulcano();
	JFrame frame = demo(ad);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	new Show(ad).start();
    }

    public static JFrame demo(Vulcano sd) {

	JFrame frame = new JFrame("z o n e m a n a g e r | vulcano");
	frame.getContentPane().add(sd);
	frame.pack();

	return frame;
    }

    public static class Show extends Thread {
	Vulcano sd;

	public Show(Vulcano sd) {
	    super();
	    this.sd = sd;
	}

	public void run() {
	    int what = 0, old;
	    while (true) {

		try {

		    sd.shuffleZones();

		    sleep((2000));
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

