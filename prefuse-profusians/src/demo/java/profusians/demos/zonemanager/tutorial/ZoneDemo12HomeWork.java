package profusians.demos.zonemanager.tutorial;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
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
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.expression.AbstractPredicate;
import prefuse.data.expression.AndPredicate;
import prefuse.data.expression.NotPredicate;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
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
import profusians.controls.PredicateDragControl;
import profusians.demos.zonemanager.tutorial.zonefactories.ZoneDemo08_ZoneFactory;
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;
import profusians.zonemanager.zone.Zone;

/**
 * let's get technical - Homework
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class ZoneDemo12HomeWork extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    private static final Schema DECORATOR_SCHEMA = PrefuseLib
	    .getVisualItemSchema();

    public static final String ZONEAGGR_DECORATORS = "zoneAggrDeco";

    ZoneManager zoneManager;

    int round = 0;

    public ZoneDemo12HomeWork() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	initZoneManager();

	zoneManager.recalculateFlexibility();

	// MAD - The decorator schema
	DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(100));
	DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",
		Font.BOLD, 12));
	m_vis.addDecorators(ZONEAGGR_DECORATORS, ZoneManager.ZONEAGGREGATES,
		new NonEmptyZonePredicate(), DECORATOR_SCHEMA);

	// set up the renderers
	// draw the nodes as basic shapes
	Renderer nodeR = new ShapeRenderer(20);

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeR);

	// the renderer for our zone aggregate decorators
	// drf.add(new InGroupPredicate(ZONEAGGR_DECORATORS), new
        // LabelRenderer("zoneName"));
	drf.add(new InGroupPredicate(ZONEAGGR_DECORATORS),
		new MyDecoratorLabelRenderer());

	zoneManager.addZoneRenderer(drf);

	m_vis.setRendererFactory(drf);

	// set up the visual operators
	// first set up all the color actions
	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(ColorLib.gray(100));
	nStroke.add("_hover", ColorLib.gray(50));

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.gray(255));

	Collection allZones = zoneManager.getZones().values();
	Iterator iter = allZones.iterator();

	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    Predicate hover_and_zone = new AndPredicate(ExpressionParser
		    .predicate("_hover"), zoneManager
		    .getZoneFocusGroupPredicate(aZone));
	    nFill.add(hover_and_zone, ColorLib.setAlpha(aZone.getColors()
		    .getItemColor(), 111));
	}

	nFill.add("_hover", ColorLib.gray(200));

	zoneManager.addZoneItemColorMapping(nFill);

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	zoneManager.setZoneAggregatesInteractive(true);
	ColorAction aFill = new ColorAction(ZoneManager.ZONEAGGREGATES,
		VisualItem.FILLCOLOR);

	iter = allZones.iterator();

	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    Predicate hover_and_zoneAggregate = new AndPredicate(
		    ExpressionParser.predicate("_hover"), zoneManager
			    .getZoneAggregatePredicate(aZone));
	    aFill.add(hover_and_zoneAggregate, ColorLib.setAlpha(aZone
		    .getColors().getItemColor(), 177));
	}
	zoneManager.addZoneAggregateColorMapping(aFill);

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

	int duration = 3234;

	ActionList catchThem = new ActionList(duration);
	catchThem.setPacingFunction(new SlowInSlowOutPacer());
	catchThem.add(colors);
	catchThem.add(new ZoneGuardAction(zoneManager));
	catchThem.add(fdlZone);
	catchThem.add(zoneManager.getZoneLayout(true));
	catchThem.add(new ColorAnimator(NODES));
	catchThem.add(new LocationAnimator(NODES));
	catchThem.add(new ZoneLabelLayout(ZONEAGGR_DECORATORS));
	catchThem.add(new RepaintAction());

	m_vis.putAction("catchThem", catchThem);

	ActionList keepThem = new ActionList(Activity.INFINITY);
	keepThem.add(new ZoneGuardAction(zoneManager));
	keepThem.add(fdlZone);
	keepThem.add(colors);
	keepThem.add(new RepaintAction());

	m_vis.putAction("keepThem", keepThem);
	m_vis.alwaysRunAfter("catchThem", "keepThem");

	ActionList keepFree = new ActionList(Activity.INFINITY);

	keepFree.add(colors);
	keepFree.add(fdlChaos);
	keepFree.add(new RepaintAction());
	m_vis.putAction("keepFree", keepFree);

	// set up the display
	setSize(500, 500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new PredicateDragControl(new NotPredicate(
		new InGroupPredicate(ZoneManager.ZONEAGGREGATES))));
	addControlListener(new PanControl());

    }

    public void zoneRound() {

	m_vis.cancel("catchThem");
	m_vis.cancel("keepThem");
	m_vis.cancel("keepFree");

	try {
	    Thread.sleep(100);
	} catch (Exception ignore) {

	}

	zoneManager.setAllZonesVisible(true);

	rearrangeNodes();

	zoneManager.recalculateFlexibility();

	m_vis.run("catchThem");

    }

    public void freeRound() {
	m_vis.cancel("catchThem");
	m_vis.cancel("keepThem");

	try {
	    Thread.sleep(100);
	} catch (Exception ignore) {

	}

	zoneManager.removeAllItems();

	zoneManager.setAllZonesVisible(false);

	m_vis.run("keepFree");

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
	int numberOfNodes = 24;
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
	zoneManager.setZoneFactory(new ZoneDemo08_ZoneFactory(zoneManager));

	zoneManager.addZonesFromFile("data/profusians/zones08.xml");

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
	ZoneDemo12HomeWork ad = new ZoneDemo12HomeWork();
	JFrame frame = demo(ad);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);

	new ZoneShow(ad).start();
    }

    public static JFrame demo(ZoneDemo12HomeWork sd) {

	JFrame frame = new JFrame(
		"z o n e m a n a g e r  d e m o  12 | Homework");
	frame.getContentPane().add(sd);
	frame.pack();

	return frame;
    }

    public static class ZoneShow extends Thread {
	ZoneDemo12HomeWork sd;

	public ZoneShow(ZoneDemo12HomeWork sd) {
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

    /**
         * MAD - A predicate indicating if a zone aggregate is non empty
         * 
         */

    class NonEmptyZonePredicate extends AbstractPredicate {
	public boolean getBoolean(Tuple t) {
	    try {
		int zoneNumber = ((AggregateItem) t).getInt("zoneNumber");
		Zone aZone = zoneManager.getZone(zoneNumber);
		if (aZone.getNumberOfItems() > 0) {
		    return true;
		} else {
		    return false;

		}
	    } catch (Exception e) {
		return false;
	    }
	}
    }

    class MyDecoratorLabelRenderer extends LabelRenderer {
	public String getText(VisualItem vi) {
	    AggregateItem decoratedItem = (AggregateItem) ((DecoratorItem) vi)
		    .getDecoratedItem();
	    int zoneNumber = decoratedItem.getInt("zoneNumber");
	    Zone aZone = zoneManager.getZone(zoneNumber);
	    return vi.getString("zoneName") + " (" + aZone.getNumberOfItems()
		    + ")";
	}
    }

    /**
         * MAD - the layout for our decorators taking care of positioning the
         * labels above the respective zones. In the case of a empty zone, the
         * decorator is set invisible
         * 
         */

    class ZoneLabelLayout extends Layout {
	public ZoneLabelLayout(String group) {
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

		    Rectangle2D b = decoratedItem.getBounds();
		    double x = b.getCenterX();
		    double y = b.getCenterY();

		    double height = b.getHeight();

		    double textWidth = decorator.getBounds().getWidth();

		    if ((aZone.getName().compareToIgnoreCase("blue zone") == 0)
			    || (aZone.getName().compareToIgnoreCase(
				    "green zone") == 0)) {
			setX(decorator, null, x + textWidth / 2);
		    } else {
			setX(decorator, null, x - textWidth / 2);
		    }

		    if (y < 0) {
			setY(decorator, null, y - height / 2 - 10);
		    } else {
			setY(decorator, null, y + height / 2 + 10);
		    }
		} else {
		    decorator.setVisible(false);
		}
	    }

	}

    } // end of inner class LabelLayout

} 

