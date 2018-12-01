package prefuse.demos.pap;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

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
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;


public class GraphViewDynamicV2 extends Display {

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";

    public static final int NODES_PER_ROUND = 17;
    public static final int MILISEC_BETWEEN_ROUNDS = 500;

    public GraphViewDynamicV2() {
	// initialize display and data
	super(new VisualizationSynch());

	//data
	Graph g = new Graph();

	g.addColumn("label", Integer.class);

	Node n1 = g.addNode();
	Node n2 = g.addNode();
	Node n3 = g.addNode();

	n1.set("label",0);
	n2.set("label",0);
	n3.set("label",0);

	g.addEdge(n1, n2);
	g.addEdge(n1, n3);

	m_vis.addGraph(GRAPH, g);


	LabelRenderer nodeRenderer = new LabelRenderer("label");

	DefaultRendererFactory drf = new DefaultRendererFactory();
	drf.setDefaultRenderer(nodeRenderer);
	m_vis.setRendererFactory(drf);

	// set up the visual operators
	// first set up all the color actions

	ColorAction nText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
	nText.setDefaultColor(ColorLib.rgb(100,0,0));


	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(ColorLib.gray(100));


	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.gray(255));


	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

	// bundle the color actions
	ActionList color = new ActionList();

	color.add(nText);
	color.add(nStroke);
	color.add(nFill);
	color.add(nEdges);

	m_vis.putAction("color", color);

	// now create the main animate routine
	ActionList animate = new ActionList(Activity.INFINITY);
	animate.add(new ForceDirectedLayout(GRAPH));
	animate.add(color);
	animate.add(new RepaintAction());

	m_vis.putAction("animate", animate);



	// set up the display
	setSize(500,500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new PanControl());
	addControlListener(new DragControl());

	addItemBoundsListener(new FitOverviewListener3());

	// set things running
	m_vis.run("animate");

	// wait a little bit
	try {
	    Thread.sleep(2000);
	} catch (Exception ignore) {

	}

	LaterInsight3 ls = new LaterInsight3(g,m_vis,this,GRAPH);


	ls.start(MILISEC_BETWEEN_ROUNDS,NODES_PER_ROUND);

    }



    public static void main(String[] argv) {
	JFrame frame = demo();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
    }

    public static JFrame demo() {
	GraphViewDynamicV2 sag = new GraphViewDynamicV2();
	JFrame frame = new JFrame("pretendig to be more dynamic");
	frame.getContentPane().add(sag);
	frame.pack();
	return frame;
    }

}

/**
 * this class implements the dynamic adding of nodes to the graph
 *
 */

class LaterInsight3 {

    private Graph data;

    private Visualization vis;


    private String GRAPH,NODES;

    public LaterInsight3(Graph data, Visualization vis, Display dis,String groupName) {

	this.data = data;
	this.vis = vis;


	this.GRAPH = groupName;
	this.NODES = groupName + ".nodes";

    }
    /**
     *
     * @param milisec break between two rounds in seconds
     * @param nodesPerRound number of nodes to be added to the graph per round
     */

    public void start(double milisec,int nodesPerRound) {
	for (int i = 1; i <= 1000; i++) {
	    Timer timer = new Timer();
	    timer.schedule(new AddNodes(timer, i,nodesPerRound), (int) (i * milisec));
	}

    }

    class AddNodes extends TimerTask {
	private Timer tim;

	private int round;

	int nodesPerRound;


	public AddNodes(Timer tim, int round, int  nodesPerRound ) {
	    this.tim = tim;
	    this.round = round;
	    this.nodesPerRound = nodesPerRound;

	}

	public synchronized  void run() { //MAD synchronizing the modifaction process

	    vis.cancel("animate"); //MAD first of all stopping the running action
	    NodeItem  connectorNodeItem = getConnectorNodeItem();

	    Node connectorNode = (Node) connectorNodeItem.getSourceTuple();

	    double connectorX = connectorNodeItem.getX();
	    double connectorY = connectorNodeItem.getY();

	    //System.out.println(connectorX + " - " + connectorY);

	    for (int i = 0; i < nodesPerRound; i++) {
		Node aNode = data.addNode();

		aNode.set("label", round);

		data.addEdge(connectorNode, aNode);

		VisualItem aNodeItem = (NodeItem) vis.getVisualItem(GRAPH,
			aNode);

		/**
		 * setting the position of the new nodeitem according to the position
		 * of the connector item -
		 * this doesn't work reliable in this prog!
		 */
		PrefuseLib.setX(aNodeItem,null,connectorX);
		PrefuseLib.setY(aNodeItem,null,connectorY);

	    }
	    tim.cancel();
	    vis.run("animate"); // MAD - starting the animation again
	}
    }

    private NodeItem getConnectorNodeItem() {
	TupleSet ts = vis.getGroup(NODES);
	Iterator iter = ts.tuples();

	int num = (int)(ts.getTupleCount() * Math.random());

	int i=0;
	while (i++<num && iter.hasNext()) { //this should go more elegant ...
	    iter.next();
	}
	return (NodeItem)iter.next();

    }
}

class FitOverviewListener3 implements ItemBoundsListener {
    private Rectangle2D m_bounds = new Rectangle2D.Double();
    private Rectangle2D m_temp = new Rectangle2D.Double();
    private double m_d = 15;
    public void itemBoundsChanged(Display d) {
	d.getItemBounds(m_temp);
	GraphicsLib.expand(m_temp, 25/d.getScale());

	double dd = m_d/d.getScale();
	double xd = Math.abs(m_temp.getMinX()-m_bounds.getMinX());
	double yd = Math.abs(m_temp.getMinY()-m_bounds.getMinY());
	double wd = Math.abs(m_temp.getWidth()-m_bounds.getWidth());
	double hd = Math.abs(m_temp.getHeight()-m_bounds.getHeight());
	if ( xd>dd || yd>dd || wd>dd || hd>dd ) {
	    m_bounds.setFrame(m_temp);
	    DisplayLib.fitViewToBounds(d, m_bounds, 0);
	}
    }
}

class VisualizationSynch extends Visualization {

    public synchronized VisualItem getVisualItem(String group, Tuple t) {
	//System.out.println("-> getVisualItem " + t.getRow() );
        TupleSet ts = getVisualGroup(group);
        VisualTable vt;
        if ( ts instanceof VisualTable ) {
            vt = (VisualTable)ts;
        } else if ( ts instanceof Graph ) {
            Graph g = (Graph)ts;
            vt = (VisualTable)(t instanceof Node ? g.getNodeTable()
                                                 : g.getEdgeTable());
        } else {
            return null;
        }
        int pr = t.getRow();
        int cr = vt.getChildRow(pr);
        //System.out.println("<- getVisualItem ");
        return cr<0 ? null : vt.getItem(cr);
    }


}


