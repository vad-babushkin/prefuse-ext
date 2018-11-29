package profusians.demos.renderer;

import java.awt.Color;
import java.awt.Point;

import javax.swing.JFrame;

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
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import profusians.render.RainbowEdgeRenderer;

/**
 * This demo implements a multicolor edge renderer. It is based on a post by
 * Rythmic in source forge forum Please see the comment about the
 * RainbowEdgeRenderer class for further information
 * 
 */

public class SelfArisenGraphRainbowEdges extends Display {

    // please customize freely to find the limits of the RainbowEdgeRenderer

    private int arrowHeadWidth = 16;

    private int arrowHeadHeight = 11;

    private boolean directedGraph = true;

    private double oneColorEdgeWidth = 2;

    private int edgeType = prefuse.Constants.EDGE_TYPE_CURVE;

    // private int edgeType = prefuse.Constants.EDGE_TYPE_LINE;

    private int arrowType = prefuse.Constants.EDGE_ARROW_FORWARD;

    // private int arrowType = prefuse.Constants.EDGE_ARROW_REVERSE;

    // the number of edges is determined by the number of colors you define
        // here

    // private final Color[] edgeColors =
        // {Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE};
    private final Color[] edgeColors = { Color.RED, Color.YELLOW, Color.GREEN,
	    Color.BLUE };

    // private final Color[] edgeColors =
        // {Color.RED,Color.YELLOW,Color.BLUE};
    // private final Color[] edgeColors = {Color.RED,Color.BLUE};

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    public SelfArisenGraphRainbowEdges() {
	// initialize display and data
	super(new Visualization());

	initDataGroups();

	LabelRenderer nodeRenderer = new LabelRenderer("label");
	EdgeRenderer edgeRenderer = new RainbowEdgeRenderer(edgeColors);
	edgeRenderer.setArrowHeadSize(arrowHeadWidth, arrowHeadHeight);

	edgeRenderer.setEdgeType(edgeType);
	edgeRenderer.setArrowType(arrowType);

	/*
         * couldn't get the width thing done yet
         */
	edgeRenderer.setDefaultLineWidth(oneColorEdgeWidth);

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeRenderer);
	drf.setDefaultEdgeRenderer(edgeRenderer);
	m_vis.setRendererFactory(drf);

	// set up the visual operators
	// first set up all the color actions

	ColorAction nText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
	nText.setDefaultColor(ColorLib.rgb(100, 0, 100));

	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(Color.YELLOW.getRGB());

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.gray(255));

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	// bundle the color actions
	ActionList draw = new ActionList();

	draw.add(nText);
	draw.add(nStroke);
	draw.add(nFill);
	draw.add(nEdges);

	m_vis.putAction("draw", draw);

	// now create the main animate routine
	ActionList animate = new ActionList(Activity.INFINITY);
	animate.add(new ForceDirectedLayout(GRAPH, true));
	animate.add(new RepaintAction());
	m_vis.putAction("animate", animate);

	m_vis.runAfter("draw", "animate");

	// set up the display
	setSize(500, 500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new PanControl());
	addControlListener(new DragControl());

	setBackground(ColorLib.getColor(0, 0, 44)); // in honour of Mr Can

	zoom(new Point(getWidth() / 2, getHeight() / 2), 2);

	// set things running
	m_vis.run("draw");

    }

    private void initDataGroups() {

	Graph g = new Graph(directedGraph);

	g.addColumn("label", String.class);

	Node n1 = g.addNode();
	Node n2 = g.addNode();
	Node n3 = g.addNode();
	Node n4 = g.addNode();
	Node n5 = g.addNode();

	n1.setString("label", "Do");
	n2.setString("label", "you");
	n3.setString("label", "love");
	n4.setString("label", "rainbows");
	n5.setString("label", "too?");

	g.addEdge(n1, n2);
	g.addEdge(n2, n3);
	g.addEdge(n3, n4);
	g.addEdge(n4, n5);

	m_vis.addGraph(GRAPH, g);

	m_vis.getVisualItem(NODES, n1).setFixed(true);

    }

    public static void main(String[] argv) {
	JFrame frame = demo();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
    }

    public static JFrame demo() {
	SelfArisenGraphRainbowEdges sag = new SelfArisenGraphRainbowEdges();

	JFrame frame = new JFrame("rainbows4all");
	frame.getContentPane().add(sag);
	frame.pack();
	return frame;
    }

}
