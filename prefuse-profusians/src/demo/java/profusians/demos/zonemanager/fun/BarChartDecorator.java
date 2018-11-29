package profusians.demos.zonemanager.fun;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
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
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.AggregateItem;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import profusians.controls.ExtendedNeighborHighlightControl;
import profusians.demos.zonemanager.fun.aggregatecontent.BarChartDecorator_ZoneAggregateItemFieldValueAssignment;
import profusians.demos.zonemanager.fun.zonefactories.BarChartDecorator_ZoneFactory;
import profusians.render.RotationLabelRenderer;
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;
import profusians.zonemanager.data.expression.NonEmptyZonePredicate;
import profusians.zonemanager.zone.Zone;


/**
 * a decorated bar chart demo
 * 
 * 
 */
public class BarChartDecorator extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    private static final Schema DECORATOR_SCHEMA = PrefuseLib
	    .getVisualItemSchema();

    public static final String NUMBEROFITEMS_DECORATOR = "zoneAggrDeco";

    public static final String COUNTRY_DECORATOR = "zoneAggrDeco2";

    ZoneManager zoneManager;

    int round = 0;

    public BarChartDecorator() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	initZoneManager();
	
	zoneManager.recalculateFlexibility();
	

	DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(100));
	DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",
		Font.BOLD, 12));


	m_vis.addDecorators(NUMBEROFITEMS_DECORATOR,
		ZoneManager.ZONEAGGREGATES, new NonEmptyZonePredicate(
			zoneManager), DECORATOR_SCHEMA);
	m_vis.addDecorators(COUNTRY_DECORATOR, ZoneManager.ZONEAGGREGATES,
		new NonEmptyZonePredicate(zoneManager), DECORATOR_SCHEMA);

	// set up the renderers
	// draw the nodes as basic shapes
	Renderer nodeR = new ShapeRenderer(20);

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeR);
	drf.add(new InGroupPredicate(NUMBEROFITEMS_DECORATOR),
		new NumberOfItemsLabelRenderer());
	drf.add(new InGroupPredicate(COUNTRY_DECORATOR),
		new RotationLabelRenderer("zoneName"));

	zoneManager.addZoneRenderer(drf);

	m_vis.setRendererFactory(drf);

	ExtendedNeighborHighlightControl enhc = new ExtendedNeighborHighlightControl(
		4, ColorLib.rgb(210, 210, 255), ColorLib.rgb(0, 0, 210));

	// set up the visual operators
	// first set up all the color actions
	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(ColorLib.rgb(100, 100, 100));
	nStroke.add("_hover", ColorLib.rgb(200, 200, 200));

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.rgb(0, 0, 200));

	zoneManager.addZoneItemColorMapping(nFill);
	nFill.add(VisualItem.HIGHLIGHT, enhc.getHighlightColorAction());

	nFill.add("_hover", ColorLib.gray(255));

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

	int duration = 2234;

	ActionList catchThem = new ActionList(duration);
	catchThem.setPacingFunction(new SlowInSlowOutPacer());
	catchThem.add(colors);
	catchThem.add(new ZoneGuardAction(zoneManager));
	catchThem.add(fdlZone);
	catchThem.add(zoneManager.getZoneLayout(true));
	catchThem.add(new ColorAnimator(NODES));
	catchThem.add(new LocationAnimator(NODES));
	catchThem.add(new ItemsPerZoneDecoratorLayout(NUMBEROFITEMS_DECORATOR));
	catchThem.add(new CountryDecoratorLayout(COUNTRY_DECORATOR));
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
	addControlListener(enhc);
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

	zoneManager.recalculateFlexibility();

	m_vis.run("catchThem");

    }

    public void freeRound() {

	m_vis.cancel("keepThem");
	m_vis.cancel("keepFree");

	try {
	    Thread.sleep(100);
	} catch (Exception ignore) {

	}

	zoneManager.removeAllItems();

	zoneManager.recalculateFlexibility();

	m_vis.run("freeThem");

    }

    public void setDecoratorVisible(String group, boolean visible) {
	Iterator iter = m_vis.getGroup(group).tuples();
	while (iter.hasNext()) {

	    DecoratorItem di = (DecoratorItem) iter.next();

	    di.setVisible(visible);

	}

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
	
	zoneManager.setZoneFactory(new BarChartDecorator_ZoneFactory());
	zoneManager.setZoneAggregateItemFieldValueAssignment(new BarChartDecorator_ZoneAggregateItemFieldValueAssignment());

	zoneManager.addColumnToZoneAggregateTable("rotation", double.class);

	zoneManager.addZonesFromFile("data/zones_barChart.xml");
	

    }

    private ForceSimulatorRemovableForces getForceSimulator() {

	float gravConstant = -0.4f; // -1.0f;
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
	BarChartDecorator ad = new BarChartDecorator();
	JFrame frame = demo(ad);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	new ZoneShow(ad).start();
    }

    public static JFrame demo(BarChartDecorator sd) {

	JFrame frame = new JFrame("z o n e m a n a g e r | bar chart decorated");
	frame.getContentPane().add(sd);
	frame.pack();

	return frame;
    }

    public static class ZoneShow extends Thread {
	BarChartDecorator sd;

	int what = 1;

	public ZoneShow(BarChartDecorator sd) {
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
		    sleep(6000);

		} catch (InterruptedException e) {
		}
	    }
	}
    }

    class NumberOfItemsLabelRenderer extends LabelRenderer {

	public String getText(VisualItem vi) {

	    AggregateItem decoratedItem = (AggregateItem) ((DecoratorItem) vi)
		    .getDecoratedItem();

	    int zoneNumber = decoratedItem.getInt("zoneNumber");

	    Zone aZone = zoneManager.getZone(zoneNumber);

	    return "(" + aZone.getNumberOfItems() + ")";

	}
    }

    class ItemsPerZoneDecoratorLayout extends Layout {

	public ItemsPerZoneDecoratorLayout(String group) {
	    super(group);
	}

	public void run(double frac) {

	    Iterator iter = m_vis.items(m_group);
	    while (iter.hasNext()) {
		DecoratorItem decorator = (DecoratorItem) iter.next();
		AggregateItem decoratedItem = (AggregateItem) decorator
			.getDecoratedItem();

		int zoneNumber = decoratedItem.getInt("zoneNumber");

		Zone aZone = zoneManager.getZone(zoneNumber);

		if (aZone.getNumberOfItems() > 0) {
		    decorator.setVisible(true);
		    decorator.setTextColor(aZone.getColors().getItemColor());
		    Rectangle2D b = decoratedItem.getBounds();
		    double x = b.getCenterX();
		    double y = b.getCenterY();

		    double height = b.getHeight();

		    setX(decorator, null, x);
		    setY(decorator, null, y - height / 2 - 10);

		} else {
		    decorator.setVisible(false);
		}
	    }

	}

    } // 

    class CountryDecoratorLayout extends Layout {

	public CountryDecoratorLayout(String group) {
	    super(group);
	}

	public void run(double frac) {

	    Iterator iter = m_vis.items(m_group);
	    while (iter.hasNext()) {
		DecoratorItem decorator = (DecoratorItem) iter.next();
		AggregateItem decoratedItem = (AggregateItem) decorator
			.getDecoratedItem();

		int zoneNumber = decoratedItem.getInt("zoneNumber");

		Zone aZone = zoneManager.getZone(zoneNumber);

		if (aZone.getNumberOfItems() > 0) {
		    decorator.setVisible(true);
		    decorator.setTextColor(aZone.getColors().getItemColor());
		    Rectangle2D b = decoratedItem.getBounds();
		    double x = b.getCenterX();
		    double y = b.getCenterY();

		    double height = b.getHeight();

		    double textHeight = decorator.getBounds().getHeight();
		    double textWidth = decorator.getBounds().getWidth();

		    setX(decorator, null, x - textWidth / 2 + 5);
		    setY(decorator, null, y + height / 2 + textHeight / 2 + 2);

		} else {
		    decorator.setVisible(false);
		}
	    }

	}

    } // 

} // end of class BarChartDecorator

