package profusians.demos.data.expression;

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
import prefuse.data.expression.Predicate;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.Renderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;
import profusians.data.expression.BidirectionalEdgePredicate;

/**
 * This demos uses the BidirectionalEdgePredicate class from the profuse
 * library to draw bidrectional edges without arrow heads while unidirectional
 * edges are drawn with a head. The predicates maintains internally a cache that
 * would have to be cleared whenever edges are removed or added to the graph.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class BidirectionalEdgeDemo extends Display {

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    public BidirectionalEdgeDemo() {
	// initialize display and data
	super(new Visualization());
	initDataGroups();

	// set up the renderers
	// draw the nodes as basic shapes
	Renderer nodeR = new ShapeRenderer(20);

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeR);
	m_vis.setRendererFactory(drf);

	EdgeRenderer edgeR = new EdgeRenderer(Constants.EDGE_TYPE_LINE,
		Constants.EDGE_ARROW_FORWARD);
	edgeR.setArrowHeadSize(40, 40);
	edgeR.setDefaultLineWidth(2);
	drf.setDefaultEdgeRenderer(edgeR);

	EdgeRenderer edgeR2 = new EdgeRenderer(Constants.EDGE_TYPE_LINE,
		Constants.EDGE_ARROW_NONE);
	edgeR2.setDefaultLineWidth(4);
	Predicate bidirectionalEdgePredicate = new BidirectionalEdgePredicate();
	drf.add(bidirectionalEdgePredicate, edgeR2);

	// set up the visual operators
	// first set up all the color actions
	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(ColorLib.gray(100));
	nStroke.add("_hover", ColorLib.gray(50));

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.gray(255));
	nFill.add("_hover", ColorLib.gray(200));

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	nEdges.add(bidirectionalEdgePredicate, ColorLib.rgb(0, 0, 200));

	ColorAction nArrow = new ColorAction(EDGES, VisualItem.FILLCOLOR,
		ColorLib.rgb(200, 0, 0));

	// bundle the color actions
	ActionList colors = new ActionList();
	colors.add(nStroke);
	colors.add(nFill);
	colors.add(nEdges);
	colors.add(nArrow);
	m_vis.putAction("colors", colors);

	// now create the main layout routine
	ActionList layout = new ActionList(Activity.INFINITY);
	layout.add(new ForceDirectedLayout(GRAPH, getForceSimulator(), true));
	layout.add(new RepaintAction());
	m_vis.putAction("layout", layout);

	m_vis.alwaysRunAfter("colors", "layout");

	// set up the display
	setSize(500, 500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new PanControl());
	addControlListener(new DragControl());

	// set things running
	m_vis.run("colors");
    }

    private void initDataGroups() {

	Graph g = new Graph(true);

	for (int i = 0; i < 4; i++) {
	    g.addNode();
	}

	for (int i = 0; i < 4; i++) {
	    g.addEdge(i, (i + 1) % 4);
	}
	g.addEdge(0, 2);
	g.addEdge(2, 0);
	g.addEdge(1, 3);
	g.addEdge(3, 1);

	// add visual data groups
	VisualGraph vg = m_vis.addGraph(GRAPH, g);
	m_vis.setInteractive(EDGES, null, false);
	m_vis.setValue(NODES, null, VisualItem.SHAPE, new Integer(
		Constants.SHAPE_ELLIPSE));

    }

    public static void main(String[] argv) {
	JFrame frame = demo();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
    }

    public static JFrame demo() {
	BidirectionalEdgeDemo ad = new BidirectionalEdgeDemo();
	JFrame frame = new JFrame(
		"p r o f u s e  |  BidirectionalEdgePredicate");
	frame.getContentPane().add(ad);
	frame.pack();
	return frame;
    }

    private ForceSimulator getForceSimulator() {

	float gravConstant = -1.0f; // -1.0f;
	float minDistance = -1.0f; // -1.0f;
	float theta = 0.09f; // 0.9f;

	float drag = 0.01f; // 0.01f;

	float springCoeff = 1E-4f; // 1E-4f;
	float defaultLength = 150f; // 50;

	ForceSimulator fsim;

	fsim = new ForceSimulator();

	fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
	fsim.addForce(new DragForce(drag));
	fsim.addForce(new SpringForce(springCoeff, defaultLength));

	return fsim;

    }

} 

