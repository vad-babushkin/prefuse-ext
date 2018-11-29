package profusians.demos.zonemanager.tutorial;

import java.util.Iterator;

import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.CircularZoneShape;
import profusians.zonemanager.zone.shape.RectangularZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * getting colorful
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class ZoneDemo03 extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    int[] zoneNumbers;

    ZoneManager zoneManager;

    public ZoneDemo03() {
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

	zoneManager.addZoneItemColorMapping(nFill); // disable this line if
                                                        // the nodes should stay
                                                        // white

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	ColorAction aFill = zoneManager.getZoneColorAction();

	// bundle the color actions
	ActionList colors = new ActionList();
	colors.add(nStroke);
	colors.add(nFill);
	colors.add(aFill); // disable this mapping if you don't want the
                                // zones to be drawn
	colors.add(nEdges);

	// now create the main layout routine

	ForceDirectedLayout fdl = new ForceDirectedLayout(GRAPH, zoneManager
		.getForceSimulator(), false);

	ActionList layout = new ActionList(Activity.INFINITY);
	layout.add(colors);
	layout.add(new ZoneGuardAction(zoneManager));
	layout.add(fdl);
	layout.add(new RepaintAction());
	m_vis.putAction("layout", layout);

	// set up the display
	setSize(500, 500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new DragControl());
	addControlListener(new PanControl());

	// set things running
	m_vis.run("layout");
    }

    private void initDataGroups() {
	// create sample graph
	// 12 nodes
	Graph g = new Graph();

	int numberOfNodes = 12;

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

	ZoneShape[] zoneShapes = new ZoneShape[4];

	zoneShapes[0] = new RectangularZoneShape(-100, 100, 110, 90);
	zoneShapes[1] = new RectangularZoneShape(100, -100, 120, 120);
	zoneShapes[2] = new CircularZoneShape(-100, -100, 60);
	zoneShapes[3] = new CircularZoneShape(100, 100, 50);

	ZoneColors[] zoneColors = new ZoneColors[4];

	zoneColors[0] = new ZoneColors(ColorLib.rgb(200, 0, 0), ColorLib.rgba(
		200, 0, 0, 111));
	zoneColors[1] = new ZoneColors(ColorLib.rgb(0, 100, 200), ColorLib
		.rgba(0, 100, 200, 111));
	zoneColors[2] = new ZoneColors(ColorLib.rgb(100, 0, 100), ColorLib
		.rgba(100, 0, 100, 111));
	zoneColors[3] = new ZoneColors(ColorLib.rgb(0, 200, 0), ColorLib.rgba(
		0, 200, 0, 111));

	ForceSimulator fsim = getForceSimulator();

	zoneManager = new ZoneManager(m_vis, fsim);

	zoneNumbers = new int[zoneShapes.length];

	for (int i = 0; i < zoneShapes.length; i++) {
	    zoneNumbers[i] = zoneManager.createAndAddZone(zoneShapes[i],
		    zoneColors[i]);
	}

    }

    private void addNodesToZones() {

	Iterator nodeItems = m_vis.items(NODES);

	int numberOfZones = zoneManager.getNumberOfZones();

	while (nodeItems.hasNext()) {
	    int choice = (int) (Math.random() * numberOfZones);
	    NodeItem aNodeItem = (NodeItem) nodeItems.next();
	    zoneManager.addItemToZone(aNodeItem, zoneNumbers[choice]);

	}
    }

    private ForceSimulator getForceSimulator() {

	float gravConstant = -0.6f; // -1.0f;
	float minDistance = 100f; // -1.0f;
	float theta = 0.1f; // 0.9f;

	float drag = 0.01f; // 0.01f;

	float springCoeff = 1E-9f; // 1E-4f;
	float defaultLength = 150f; // 50;

	ForceSimulator fsim;

	fsim = new ForceSimulator();

	fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
	fsim.addForce(new DragForce(drag));
	fsim.addForce(new SpringForce(springCoeff, defaultLength));

	return fsim;

    }

    public static void main(String[] argv) {
	JFrame frame = demo();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
    }

    public static JFrame demo() {
	ZoneDemo03 ad = new ZoneDemo03();
	JFrame frame = new JFrame(
		"z o n e m a n a g e r  d e m o  3  |  getting colorful");
	frame.getContentPane().add(ad);
	frame.pack();
	return frame;
    }

} // end of class ZoneDemo3

