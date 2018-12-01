package prefuse.demos.pap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 * This demo implements a multicolor edge renderer.
 * It is based on a post by Rythmic in source forge forum
 * Please see the comment about the RainbowEdgeRenderer class for further information
 *
 */

public class SelfArisenGraphRainbowEdges extends Display {

//  please customize freely to find the limits of the RainbowEdgeRenderer

    private int arrowHeadWidth = 16;
    private int arrowHeadHeight = 11;
    private boolean directedGraph = true;
    private double oneColorEdgeWidth = 2;

    private int edgeType = prefuse.Constants.EDGE_TYPE_CURVE;
//  private int edgeType = prefuse.Constants.EDGE_TYPE_LINE;

    private int arrowType = prefuse.Constants.EDGE_ARROW_FORWARD;
//  private int arrowType = prefuse.Constants.EDGE_ARROW_REVERSE;


//  the number of edges is determined by the number of colors you define here

//  private final Color[] edgeColors = {Color.RED,Color.ORANGE,Color.YELLOW,Color.GREEN,Color.BLUE};
    private final Color[] edgeColors = {Color.RED,Color.YELLOW,Color.GREEN,Color.BLUE};    
//  private final Color[] edgeColors = {Color.RED,Color.YELLOW,Color.BLUE};
//  private final Color[] edgeColors = {Color.RED,Color.BLUE};


    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";




    public SelfArisenGraphRainbowEdges() {
//	initialize display and data
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

//	set up the visual operators
//	first set up all the color actions

	ColorAction nText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
	nText.setDefaultColor(ColorLib.rgb(100,0,100));


	ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
	nStroke.setDefaultColor(Color.YELLOW.getRGB());


	ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
	nFill.setDefaultColor(ColorLib.gray(255));


	ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
	nEdges.setDefaultColor(ColorLib.gray(100));

//	bundle the color actions
	ActionList draw = new ActionList();

	draw.add(nText);
	draw.add(nStroke);
	draw.add(nFill);
	draw.add(nEdges);


	m_vis.putAction("draw", draw);

//	now create the main animate routine
	ActionList animate = new ActionList(Activity.INFINITY);
	animate.add(new ForceDirectedLayout(GRAPH, true));
	animate.add(new RepaintAction());
	m_vis.putAction("animate", animate);

	m_vis.runAfter("draw","animate");

//	set up the display
	setSize(500,500);
	pan(250, 250);
	setHighQuality(true);
	addControlListener(new ZoomControl());
	addControlListener(new PanControl());
	addControlListener(new DragControl());

	setBackground(ColorLib.getColor(0, 0, 44)); // in honour of Mr Can

	zoom(new Point(getWidth()/2,getHeight()/2),2);

//	set things running
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

	n1.setString("label","Do");
	n2.setString("label","you");
	n3.setString("label","love");
	n4.setString("label","rainbows");
	n5.setString("label","too?");


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


    /**
     * The development of this edge renderer was caused by a post by Rythmic in the source forge
     * prefuse forum. 
     * 
     * The essence and core of it is Rythmic's strategy of placing 
     * the beginning and end points of the multiline edge.
     * 
     * Based on that this edge renderer extends the original class:
     * 
     * + full multiple line support
     * + arrow heads are drawn in multiple colors 
     * + curved edges 
     * + the edge width can be specified (or more precise the line width of one color)
     *
     * Please report any problem you encounter with this edge renderer. 
     * 
     * @author <a href="http://jheer.org">jeffrey heer (original edge renderer)</a>
     * @author rythmic (key insight provider)
     * @author <a href="http://goosebumps4all.net">martin dudek (the questionable rest)</a>
     */


    public class RainbowEdgeRenderer extends SmoothCurvedLinesEdgeRenderer {

	protected Polygon[] m_arrowRainbowHead;
	protected Shape[] m_curRainbowArrow;  

	protected Line2D[] line;
	protected CubicCurve2D[] curve;

	private Color[] edgeColor;
	private int numberOfEdges;

	protected Point2D[] m_rainbowCtrlPoints[];

	double[] x1;
	double[] x2;
	double[] y1;
	double[] y2;


	public RainbowEdgeRenderer(Color[] edgeColor) {
	    super();
	    setup(edgeColor);
	    updateArrowHeadRainbow(m_arrowWidth, m_arrowHeight);

	}

	public void render(Graphics2D g, VisualItem item) { 
//	    render the edge line 

	    getShape(item);

	    for (int i=0;i<numberOfEdges;i++) {

		item.setStrokeColor(edgeColor[i].getRGB());
		if (m_edgeType == prefuse.Constants.EDGE_TYPE_CURVE) {		    
		    drawShape(g, item,curve[i]);
		} else {		    
		    drawShape(g, item,line[i]);		  
		}


	    }

//	    render the edge arrow head, if appropriate


	    for (int i=0;i<numberOfEdges;i++) {
		if ( m_curRainbowArrow[i] != null ) {

		    g.setPaint(edgeColor[i]);
		    g.fill(m_curRainbowArrow[i]);
		}
	    }
	} 



	public void setArrowHeadSize(int width, int height) {
	    m_arrowWidth = width;
	    m_arrowHeight = height;
	    updateArrowHeadRainbow(width, height);
	}

	public void setColors(Color[] edgeColor) {
	    setup(edgeColor);
	}

	public Color[] getColors() {
	    return edgeColor;
	}

	public int getNumberOfEdges() {
	    return numberOfEdges;
	}

	public void setBounds(VisualItem item) { //TODO
	    if (!m_manageBounds ) return;
	    Shape shape = getShape(item);
	    if ( shape == null ) {
		item.setBounds(item.getX(), item.getY(), 0, 0);

	    } else {
		GraphicsLib.setBounds(item, shape, getStroke(item));
	    }

	    if ( m_curRainbowArrow[0] != null ) {
		Rectangle2D bbox = (Rectangle2D)item.get(VisualItem.BOUNDS);
		for (int i=0;i<numberOfEdges;i++) {
		    Rectangle2D.union(bbox, m_curRainbowArrow[i].getBounds2D(), bbox);
		}
	    }
	}


	protected void updateArrowHeadRainbow(int w, int h) {

	    if ( m_arrowRainbowHead == null ) {
		m_arrowRainbowHead = new Polygon[numberOfEdges];
		for (int i=0;i<numberOfEdges;i++) {
		    m_arrowRainbowHead[i] = new Polygon();
		}

	    } else {
		for (int i=0;i<numberOfEdges;i++) {
		    m_arrowRainbowHead[i].reset();
		}
	    }

	    double rainbowHeadPieceWidth = (w*m_width)/numberOfEdges;

	    for (int i=0;i<numberOfEdges;i++) {
		m_arrowRainbowHead[i].addPoint(0, 0);
		m_arrowRainbowHead[i].addPoint((int)( w/2.0 - i*rainbowHeadPieceWidth), -h);
		m_arrowRainbowHead[i].addPoint((int)( w/2.0 - (i+1)*rainbowHeadPieceWidth ), -h);
		m_arrowRainbowHead[i].addPoint(0, 0);
	    }
	}



	protected Shape getRawShape(VisualItem item) {
	    EdgeItem   edge = (EdgeItem)item;
	    VisualItem item1 = edge.getSourceItem();
	    VisualItem item2 = edge.getTargetItem();

	    int type = m_edgeType;

	    getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
		    m_xAlign1, m_yAlign1);
	    getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
		    m_xAlign2, m_yAlign2);
	    m_curWidth = (float)(m_width * getLineWidth(item));

//	    create the arrow head, if needed
	    EdgeItem e = (EdgeItem)item;
	    if ( e.isDirected() && m_edgeArrow != Constants.EDGE_ARROW_NONE ) {
//		get starting and ending edge endpoints
		boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
		Point2D start = null, end = null;
		start = m_tmpPoints[forward?0:1];
		end   = m_tmpPoints[forward?1:0];

//		compute the intersection with the target bounding box
		VisualItem dest = forward ? e.getTargetItem() : e.getSourceItem();
		int i = GraphicsLib.intersectLineRectangle(start, end,
			dest.getBounds(), m_isctPoints);
		if ( i > 0 ) end = m_isctPoints[0];

//		create the arrow head shape
		AffineTransform at = getArrowTrans(start, end, m_curWidth * numberOfEdges*0.5);

		for (int j=0;j<numberOfEdges;j++) {
		    m_curRainbowArrow[j] = at.createTransformedShape(m_arrowRainbowHead[j]);

		}

//		update the endpoints for the edge shape
//		need to bias this by arrow head size
		Point2D lineEnd = m_tmpPoints[forward?1:0];

		/*
		 * added +1 to ensure that all multiple edges are covered by the head
		 * Takes advantage of the fact that the arrow head is drawn after the edges
		 * and by that above them if intersection occurs
		 */		
		lineEnd.setLocation(0, -m_arrowHeight+1);


		at.transform(lineEnd, lineEnd);
	    } else {

		for (int j=0;j<numberOfEdges;j++) {
		    m_curRainbowArrow[j] = null;  
		}
	    }

//	    create the edge shape
	    Shape shape = null;
	    double n1x = m_tmpPoints[0].getX();
	    double n1y = m_tmpPoints[0].getY();
	    double n2x = m_tmpPoints[1].getX();
	    double n2y = m_tmpPoints[1].getY();

	    double c, radAngle, degAngle;

//	    a = length of x-axis, b = length of y-axis 
	    double a,b; 

	    a = ( (n1x-n2x) ); 
	    b = ( (n1y-n2y) ); 
	    c = Math.sqrt( (a*a) + (b*b) ); 

	    radAngle = Math.acos( ( ( Math.abs(a) )/c) ); 
	    degAngle = radAngle * ( 180/Math.PI); 

	    for (int i=0;i<numberOfEdges;i++) {
		x1[i] = n1x;
		x2[i] = n2x;
		y1[i] = n1y;
		y2[i] = n2y;
	    }

	    getCurveControlPoints(edge, m_ctrlPoints,n1x,n1y,n2x,n2y);

	    double bx1 =  m_ctrlPoints[0].getX();
	    double bx2 =  m_ctrlPoints[1].getX();

	    double by1 =  m_ctrlPoints[0].getY();
	    double by2 =  m_ctrlPoints[1].getY();


	    boolean xDirection;
	    int flip;

//	    a <= 0 -> target is left to source, or vertical if a == 0 
//	    b < 0  -> source is above target, or horizontal if b == 0 

	    flip = (a>b) ? -1 : 1 ;	    
	    xDirection = (degAngle>45) ? true : false;

	    double offset = - flip * (numberOfEdges-1)*m_width/2;

	    for (int i=0;i<numberOfEdges;i++) {
		if (xDirection) {
		    x1[i] += offset; 
		    x2[i] += offset;

		    if (m_edgeType == prefuse.Constants.EDGE_TYPE_CURVE) {				    				    
			m_rainbowCtrlPoints[i][0].setLocation(bx1+offset,by1);
			m_rainbowCtrlPoints[i][1].setLocation(bx2+offset,by2);
		    }
		} else {
		    y1[i] += offset; 
		    y2[i] += offset;

		    if (m_edgeType == prefuse.Constants.EDGE_TYPE_CURVE) {				    				    
			m_rainbowCtrlPoints[i][0].setLocation(bx1,by1+offset);
			m_rainbowCtrlPoints[i][1].setLocation(bx2,by2+offset);
		    }
		}
		offset += flip*m_width;
	    }

	    switch (type) {
	    case Constants.EDGE_TYPE_LINE:

		line[0] = new Line2D.Double(x1[0],y1[0], x2[0], y2[0] );
		Rectangle2D bbox = line[0].getBounds2D();
		for (int i=1;i<numberOfEdges;i++) {
		    line[i] = new Line2D.Double(x1[i],y1[i], x2[i], y2[i] );
		    Rectangle2D.union(bbox, line[i].getBounds2D(),bbox);
		}
		shape = bbox;
		break;

	    case Constants.EDGE_TYPE_CURVE:

		curve[0] = new CubicCurve2D.Double(x1[0],y1[0],
			m_rainbowCtrlPoints[0][0].getX(), m_rainbowCtrlPoints[0][0].getY(),
			m_rainbowCtrlPoints[0][1].getX(), m_rainbowCtrlPoints[0][1].getY(),
			x2[0], y2[0]);
		bbox = curve[0].getBounds2D();
		for (int i=1;i<numberOfEdges;i++) {
		    curve[i] = new CubicCurve2D.Double(x1[i],y1[i],
			    m_rainbowCtrlPoints[i][0].getX(), m_rainbowCtrlPoints[i][0].getY(),
			    m_rainbowCtrlPoints[i][1].getX(), m_rainbowCtrlPoints[i][1].getY(),
			    x2[i], y2[i]);
		    Rectangle2D.union(bbox, curve[i].getBounds2D(),bbox);
		}
		shape = bbox;
		break;

	    default:
		throw new IllegalStateException("Unknown edge type");
	    }

//	    return the edge shape
	    return shape;
	}

	private void setup(Color[] edgeColor) {
	    this.edgeColor = edgeColor;
	    numberOfEdges = edgeColor.length;
	    x1 = new double[numberOfEdges];
	    x2 = new double[numberOfEdges];
	    y1 = new double[numberOfEdges];
	    y2 = new double[numberOfEdges];

	    line = new Line2D[numberOfEdges];
	    curve = new CubicCurve2D[numberOfEdges];    
	    m_curRainbowArrow = new Shape[numberOfEdges];

	    m_rainbowCtrlPoints = new Point2D[numberOfEdges][2];

	    for (int i = 0;i<numberOfEdges;i++) {
		m_rainbowCtrlPoints[i][0] = new Point2D.Double();
		m_rainbowCtrlPoints[i][1] = new Point2D.Double();
	    }
	}

    }

    /*
     * This edge renderer class draws curved edges less "wild" than the
     * extended EdgeRenderer. In addition it is symmetric, should mean
     * x and y distances between beginning and end point of the curve are treated
     * equal (in difference to the extend class which only takes x distance into
     * account, loosly spoken) An effect of this is that the edge is straight 
     * if x and y distances between beginning and end point are equal. 
     * Smooth like the palms of an elephant ...
     * 
     * @author <a href="http://goosebumps4all.net">martin dudek</a>
     */

    public class SmoothCurvedLinesEdgeRenderer extends EdgeRenderer {

	protected void getCurveControlPoints(EdgeItem eitem, Point2D[] cp, 
		double x1, double y1, double x2, double y2) 
	{
	    double dx = x2-x1, dy = y2-y1;

	    double c = Math.sqrt( (dx*dx) + (dy*dy) ); 

	    double radAngle = Math.acos( ( ( Math.abs(dx) )/c) );
	    double degAngle = radAngle * ( 180/Math.PI);  

	    double wx = getWeight(90-degAngle);
	    double wy = getWeight(degAngle);

	    if (eitem.isDirected() && m_edgeArrow != Constants.EDGE_ARROW_NONE) {
		if (m_edgeArrow == Constants.EDGE_ARROW_FORWARD) {
		    cp[0].setLocation(x1+wx*2*dx/3,y1+wy*2*dy/3);
		    cp[1].setLocation(x2-dx/8,y2-dy/8);
		} else {
		    cp[0].setLocation(x1+dx/8,y1+dy/8);
		    cp[1].setLocation(x2-wx*2*dx/3,y2-wy*2*dy/3);		    
		}
	    } else {
		cp[0].setLocation(x1+wx*1*dx/3,y1+wy*1*dy/3);
		cp[1].setLocation(x1+wx*2*dx/3,y1+wy*2*dy/3);
	    }
	}

	private double getWeight(double x) {
	    return 1-Math.min(Math.abs(3-x/22.5),1);
	}
    }
} 

