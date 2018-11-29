package profusians.demos.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

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
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.display.PaintListener;
import prefuse.visual.VisualItem;
import profusians.render.SmoothEdgeRenderer;
import profusians.render.WaveEdgeRenderer;

/**
 * This demo shows two newly developed edge renderer drawing curved edges in a
 * slightly different way. Different shaped curved edges are achieved by by
 * overriding the getCurveControlPoints() method of the EdgeRenderer class.
 * Through this method the control points of the used cubic (Bezier) curve to
 * draw the curved edges can be set. One of the new edge renderer, the
 * WaveLikeEdgeRenderer has an additional parameter waveSize, which determines
 * the size of the "waves" of the edge. Feel expected to play with this
 * parameter.
 * 
 * Please press CTRL-SPACE to switch between the original edge renderer and the
 * other two new curve edge renderer
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumpsall.net">martin dudek</a>
 * 
 */

public class SelfArisenGraphNewCurvedEdgeRenderer extends Display implements
	KeyListener {

    private double waveSize = 2;

    public static final String GRAPH = "graph";

    public static final String NODES = "graph.nodes";

    public static final String EDGES = "graph.edges";

    private EdgeRenderer edgeRendererOriginal = new EdgeRenderer();

    private WaveEdgeRenderer edgeRendererWave = new WaveEdgeRenderer();

    private SmoothEdgeRenderer edgeRendererSmooth = new SmoothEdgeRenderer();

    private DefaultRendererFactory drf;

    public static int renderType = 0;

    public SelfArisenGraphNewCurvedEdgeRenderer() {
	// initialize display and data
	super(new Visualization());

	initDataGroups();

	LabelRenderer nodeRenderer = new LabelRenderer("label");

	drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeRenderer);

	edgeRendererWave.setEdgeType(Constants.EDGE_TYPE_CURVE);
	edgeRendererSmooth.setEdgeType(Constants.EDGE_TYPE_CURVE);
	edgeRendererOriginal.setEdgeType(Constants.EDGE_TYPE_CURVE);

	drf.setDefaultEdgeRenderer(edgeRendererWave);

	edgeRendererWave.setWaveSize(waveSize);

	m_vis.setRendererFactory(drf);

	// set up the visual operators
	// first set up all the color actions

	ColorAction nText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
	nText.setDefaultColor(ColorLib.rgb(100, 0, 0));

	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(ColorLib.gray(100));

	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.gray(255));

	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	ColorAction nEdgesFill = new ColorAction(EDGES, VisualItem.FILLCOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	// bundle the color actions
	ActionList draw = new ActionList();

	draw.add(nText);
	draw.add(nStroke);
	draw.add(nFill);
	draw.add(nEdges);
	draw.add(nEdgesFill);

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

	addKeyListener(this);
	addPaintListener(new RenderTypePainter());

	zoom(new Point2D.Double(getWidth() / 2, getHeight() / 2), 1.7);

	// set things running
	m_vis.run("draw");

    }

    private void initDataGroups() {

	Graph g = new Graph();

	g.addColumn("label", String.class);

	String prosa = "Finally I found a way to publish my great lyrics but I still wonder who is going to read this amazing piece of work you are reading right now.";

	String content[] = prosa.split(" ");

	Node[] n = new Node[content.length];
	for (int i = 0; i < content.length; i++) {
	    n[i] = g.addNode();
	    n[i].setString("label", content[i]);
	}

	for (int i = 0; i < content.length; i++) {
	    g.addEdge(i, (i + 1) % content.length);
	}

	for (int i = 0; i < content.length / 3; i++) {
	    int source = (int) (Math.random() * content.length);
	    int target = (int) (Math.random() * content.length);
	    if ((source != target) && (source != target - 1)) {
		g.addEdge(n[source], n[target]);
	    }

	}

	m_vis.addGraph(GRAPH, g);

    }

    public static void main(String[] argv) {
	JFrame frame = demo();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
    }

    public static JFrame demo() {
	SelfArisenGraphNewCurvedEdgeRenderer sag = new SelfArisenGraphNewCurvedEdgeRenderer();
	JFrame frame = new JFrame(
		"press CTRL-SPACE to switch the edge rendering");
	frame.getContentPane().add(sag);
	frame.pack();
	return frame;
    }

    public void keyPressed(KeyEvent e) {
	// TODO Auto-generated method stub
	int code = e.getKeyCode();

	if (e.isControlDown() && (code == KeyEvent.VK_SPACE)) {
	    // m_vis.cancel("animate");
	    renderType = (renderType + 1) % 3;
	    if (renderType == 0) {
		drf.setDefaultEdgeRenderer(edgeRendererWave);
	    } else if (renderType == 1) {
		drf.setDefaultEdgeRenderer(edgeRendererSmooth);
	    } else {
		drf.setDefaultEdgeRenderer(edgeRendererOriginal);
	    }

	    m_vis.run("draw");
	}
	writeRenderType();
    }

    public void writeRenderType() {

    }

    public void keyReleased(KeyEvent e) {
	// TODO Auto-generated method stub

    }

    public void keyTyped(KeyEvent e) {
	// TODO Auto-generated method stub

    }

    public static class RenderTypePainter implements PaintListener {
	public void prePaint(Display d, Graphics2D g) {
	}

	public void postPaint(Display d, Graphics2D g) {
	    g.setColor(Color.RED);
	    String text;
	    if (renderType == 0) {
		text = "new wave like edge rendering";
	    } else if (renderType == 1) {
		text = "new smooth edge rendering";
	    } else {
		text = "original curved edge rendering";
	    }

	    g.drawString(text, 10, 20);
	}
    }

}
