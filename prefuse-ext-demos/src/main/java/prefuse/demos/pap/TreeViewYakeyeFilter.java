package prefuse.demos.pap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Predicate;
import prefuse.data.io.TreeMLReader;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.FilterIterator;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTree;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.expression.StartVisiblePredicate;
import prefuse.visual.sort.TreeDepthItemSorter;


/**
 * Demonstration of a node-link SUB tree viewer
 *
 * @version 1.0
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net>martin dudek</a>
 */
public class TreeViewYakeyeFilter extends Display {

    public static final String TREE_CHI = "data/chi-ontology.xml.gz";

    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";


    private static final int YAK_DISTANCE = 2;

    private LabelRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;

    private String m_label = "label";
    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;

    public TreeViewYakeyeFilter(Tree t, String label) {
	super(new Visualization());
	m_label = label;

	VisualTree vt = m_vis.addTree(tree, t);

	final NodeItem treeRoot = (NodeItem) vt.getRoot();

	m_nodeRenderer = new MyLabelRenderer(m_label);
	m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
	m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
	m_nodeRenderer.setRoundedCorner(8,8);
	m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

	DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
	rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
	m_vis.setRendererFactory(rf);

	// colors
	ItemAction nodeColor = new NodeColorAction(treeNodes);
	ItemAction textColor = new ColorAction(treeNodes,
		VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0));
	m_vis.putAction("textColor", textColor);

	ItemAction edgeColor = new ColorAction(treeEdges,
		VisualItem.STROKECOLOR, ColorLib.rgb(200,200,200));

	// quick repaint
	ActionList repaint = new ActionList();
	repaint.add(nodeColor);
	repaint.add(new RepaintAction());
	m_vis.putAction("repaint", repaint);

	// full paint
	ActionList fullPaint = new ActionList();
	fullPaint.add(nodeColor);
	m_vis.putAction("fullPaint", fullPaint);

	// animate paint change
	ActionList animatePaint = new ActionList(400);
	animatePaint.add(new ColorAnimator(treeNodes));
	animatePaint.add(new RepaintAction());
	m_vis.putAction("animatePaint", animatePaint);

	// create the tree layout action
	final NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
		m_orientation, 50, 0, 8);
	treeLayout.setLayoutAnchor(new Point2D.Double(25,300));
	m_vis.putAction("treeLayout", treeLayout);


	NodeItem root = ((NodeItem)vt.getNode(0));
	m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(root);

	CollapsedSubtreeLayoutChangingTreeRoot subLayout = 
	    new CollapsedSubtreeLayoutChangingTreeRoot(tree, m_orientation);
	m_vis.putAction("subLayout", subLayout);

	AutoPanAction autoPan = new AutoPanAction();


	// create the filtering and layout
	ActionList filter = new ActionList();

	final YakeyeTreeFilter yakfilter = new YakeyeTreeFilter(tree, YAK_DISTANCE);
	filter.add(yakfilter);

	filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 16)));
	filter.add(treeLayout);
	filter.add(subLayout);
	filter.add(textColor);
	filter.add(nodeColor);
	filter.add(edgeColor);
	m_vis.putAction("filter", filter);

	// animated transition
	ActionList animate = new ActionList(1000);
	animate.setPacingFunction(new SlowInSlowOutPacer());
	animate.add(autoPan);
	animate.add(new QualityControlAnimator());
	animate.add(new VisibilityAnimator(tree));
	animate.add(new LocationAnimator(treeNodes));
	animate.add(new ColorAnimator(treeNodes));
	animate.add(new RepaintAction());
	m_vis.putAction("animate", animate);
	m_vis.alwaysRunAfter("filter", "animate");

	// create animator for orientation changes
	ActionList orient = new ActionList(2000);
	orient.setPacingFunction(new SlowInSlowOutPacer());
	orient.add(autoPan);
	orient.add(new QualityControlAnimator());
	orient.add(new LocationAnimator(treeNodes));
	orient.add(new RepaintAction());
	m_vis.putAction("orient", orient);

	// ------------------------------------------------

	// initialize the display
	setSize(700,600);
	setItemSorter(new TreeDepthItemSorter());
	addControlListener(new ZoomToFitControl());
	addControlListener(new ZoomControl());
	addControlListener(new PanControl());
	addControlListener(new DragControl());
	addControlListener(new FocusControl(1));
	//addControlListener(new FocusControl(1, "filter"));

	registerKeyboardAction(
		new OrientAction(Constants.ORIENT_LEFT_RIGHT),
		"left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
	registerKeyboardAction(
		new OrientAction(Constants.ORIENT_TOP_BOTTOM),
		"top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
	registerKeyboardAction(
		new OrientAction(Constants.ORIENT_RIGHT_LEFT),
		"right-to-left", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
	registerKeyboardAction(
		new OrientAction(Constants.ORIENT_BOTTOM_TOP),
		"bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), WHEN_FOCUSED);

	// ------------------------------------------------

	// filter graph and perform layout
	setOrientation(m_orientation);
	m_vis.run("filter");

	TupleSet search = new PrefixSearchTupleSet(); 
	m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
	search.addTupleSetListener(new TupleSetListener() {
	    public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
		m_vis.cancel("animatePaint");
		m_vis.run("fullPaint");
		m_vis.run("animatePaint");
	    }
	});

//	fix selected focus nodes
	TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS); 
	focusGroup.addTupleSetListener(new TupleSetListener() {
	    public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
	    {

		NodeItem root = (NodeItem)add[0];

		for (int i=0;i<yakfilter.getPredecessorDistance();i++) {//TODO: should be offered by the filter
		    if (root != treeRoot) {
			root = (NodeItem) root.getParent();
		    }
		}
		treeLayout.setLayoutRoot(root);        	
		m_vis.run("filter");
	    }
	});

    }



    // ------------------------------------------------------------------------

    public void setOrientation(int orientation) {
	NodeLinkTreeLayout rtl 
	= (NodeLinkTreeLayout)m_vis.getAction("treeLayout");
	CollapsedSubtreeLayoutChangingTreeRoot stl
	= (CollapsedSubtreeLayoutChangingTreeRoot)m_vis.getAction("subLayout");
	switch ( orientation ) {
	case Constants.ORIENT_LEFT_RIGHT:
	    m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
	    m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
	    m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
	    m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
	    m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
	    break;
	case Constants.ORIENT_RIGHT_LEFT:
	    m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
	    m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
	    m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
	    m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
	    m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
	    break;
	case Constants.ORIENT_TOP_BOTTOM:
	    m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
	    m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
	    m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
	    m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
	    m_edgeRenderer.setVerticalAlignment2(Constants.TOP);
	    break;
	case Constants.ORIENT_BOTTOM_TOP:
	    m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
	    m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
	    m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
	    m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
	    m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Unrecognized orientation value: "+orientation);
	}
	m_orientation = orientation;
	rtl.setOrientation(orientation);
	stl.setOrientation(orientation);
    }

    public int getOrientation() {
	return m_orientation;
    }

    // ------------------------------------------------------------------------

    public static void main(String argv[]) {
	String infile = TREE_CHI;
	String label = "name";
	if ( argv.length > 1 ) {
	    infile = argv[0];
	    label = argv[1];
	}
	JComponent treeview = demo(infile, label);

	JFrame frame = new JFrame("p r e f u s e  |  s u b t r e e v i e w  -  y a k e y e t r e e f i l t e r");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setContentPane(treeview);
	frame.pack();
	frame.setVisible(true);
    }

    public static JComponent demo() {
	return demo(TREE_CHI, "name");
    }

    public static JComponent demo(String datafile, final String label) {
	Color BACKGROUND = Color.WHITE;
	Color FOREGROUND = Color.BLACK;

	Tree t = null;
	try {
	    t = (Tree)new TreeMLReader().readGraph(datafile);
	} catch ( Exception e ) {
	    e.printStackTrace();
	    System.exit(1);
	}

	// create a new treemap
	final TreeViewYakeyeFilter tview = new TreeViewYakeyeFilter(t, label);
	tview.setBackground(BACKGROUND);
	tview.setForeground(FOREGROUND);

	// create a search panel for the tree map
	JSearchPanel search = new JSearchPanel(tview.getVisualization(),
		treeNodes, Visualization.SEARCH_ITEMS, label, true, true);
	search.setShowResultCount(true);
	search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
	search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
	search.setBackground(BACKGROUND);
	search.setForeground(FOREGROUND);

	final JFastLabel title = new JFastLabel("                 ");
	title.setPreferredSize(new Dimension(350, 20));
	title.setVerticalAlignment(SwingConstants.BOTTOM);
	title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
	title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
	title.setBackground(BACKGROUND);
	title.setForeground(FOREGROUND);

	tview.addControlListener(new ControlAdapter() {
	    public void itemEntered(VisualItem item, MouseEvent e) {
		if ( item.canGetString(label) )
		    title.setText(item.getString(label));
	    }
	    public void itemExited(VisualItem item, MouseEvent e) {
		title.setText(null);
	    }
	});



	Box box = new Box(BoxLayout.X_AXIS);
	box.add(Box.createHorizontalStrut(10));
	box.add(title);
	box.add(Box.createHorizontalGlue());
	box.add(search);
	box.add(Box.createHorizontalStrut(3));
	box.setBackground(BACKGROUND);

	JPanel panel = new JPanel(new BorderLayout());
	panel.setBackground(BACKGROUND);
	panel.setForeground(FOREGROUND);
	panel.add(tview, BorderLayout.CENTER);
	panel.add(box, BorderLayout.SOUTH);
	return panel;
    }

    // ------------------------------------------------------------------------

    public class OrientAction extends AbstractAction {
	private int orientation;

	public OrientAction(int orientation) {
	    this.orientation = orientation;
	}
	public void actionPerformed(ActionEvent evt) {
	    setOrientation(orientation);
	    getVisualization().cancel("orient");
	    getVisualization().run("treeLayout");
	    getVisualization().run("orient");
	}
    }

    public class AutoPanAction extends Action {
	private Point2D m_start = new Point2D.Double();
	private Point2D m_end   = new Point2D.Double();
	private Point2D m_cur   = new Point2D.Double();
	private int     m_bias  = 150;

	public void run(double frac) {
	    TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
	    if ( ts.getTupleCount() == 0 )
		return;

	    if ( frac == 0.0 ) {
		int xbias=0, ybias=0;
		switch ( m_orientation ) {
		case Constants.ORIENT_LEFT_RIGHT:
		    xbias = m_bias;
		    break;
		case Constants.ORIENT_RIGHT_LEFT:
		    xbias = -m_bias;
		    break;
		case Constants.ORIENT_TOP_BOTTOM:
		    ybias = m_bias;
		    break;
		case Constants.ORIENT_BOTTOM_TOP:
		    ybias = -m_bias;
		    break;
		}

		VisualItem vi = (VisualItem)ts.tuples().next();
		m_cur.setLocation(getWidth()/2, getHeight()/2);
		getAbsoluteCoordinate(m_cur, m_start);
		m_end.setLocation(vi.getX()+xbias, vi.getY()+ybias);
	    } else {
		m_cur.setLocation(m_start.getX() + frac*(m_end.getX()-m_start.getX()),
			m_start.getY() + frac*(m_end.getY()-m_start.getY()));
		panToAbs(m_cur);
	    }
	}
    }

    public static class NodeColorAction extends ColorAction {

	public NodeColorAction(String group) {
	    super(group, VisualItem.FILLCOLOR);
	}

	public int getColor(VisualItem item) {
	    if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS) )
		return ColorLib.rgb(255,190,190);
	    else if ( m_vis.isInGroup(item, Visualization.FOCUS_ITEMS) )
		return ColorLib.rgb(198,229,229);
	    else if ( item.getDOI() > -1 )
		return ColorLib.rgb(164,193,193);
	    else
		return ColorLib.rgba(255,255,255,0);
	}

    } // end of inner class TreeMapColorAction

    public class MyLabelRenderer extends LabelRenderer {

	public MyLabelRenderer(String textField)  {
	    super(textField);
	}
	public String getText(VisualItem vi) {
	    //return vi.getString(m_labelName) + "\nDOI: " + vi.getDOI() + " " + (vi.isExpanded() ? "E" : "O");
	    return vi.getString(m_labelName);
	}
    }


    // 

    private class YakeyeTreeFilter extends GraphDistanceFilterDirectedSteps {

	public YakeyeTreeFilter(String group, int distance) {
	    super(group,distance,distance-1);
	}	
    }

    public class GraphDistanceFilterDirectedSteps extends GroupAction {

	protected int m_successorDistance;
	protected int m_predecessorDistance;
	protected String m_sources;
	protected Predicate m_groupP;
	protected BreadthFirstIteratorDirectedSteps m_bfs;

	public GraphDistanceFilterDirectedSteps(String group) {
	    this(group, 1,1);
	}

	public GraphDistanceFilterDirectedSteps(String group, int successorDistance, int predecessorDistance) {
	    this(group, Visualization.FOCUS_ITEMS, successorDistance,predecessorDistance);
	}

	public GraphDistanceFilterDirectedSteps(String group, String sources, int successorDistance, int predecessorDistance)
	{
	    super(group);
	    m_sources = sources;
	    m_predecessorDistance = predecessorDistance;
	    m_successorDistance = successorDistance;
	    m_groupP = new InGroupPredicate(
		    PrefuseLib.getGroupName(group, Graph.NODES));
	    m_bfs = new BreadthFirstIteratorDirectedSteps();
	}

	public int getPredecessorDistance() {
	    return m_predecessorDistance;
	}

	public void setPredecessorDistance(int predecessorDistance) {
	    m_predecessorDistance = predecessorDistance;
	}

	public int getSuccessorDistance() {
	    return m_successorDistance;
	}

	public void setSuccessorDistance(int successorDistance) {
	    m_successorDistance = successorDistance;
	}
	public String getSources() {
	    return m_sources;
	}

	public void setSources(String sources) {
	    m_sources = sources;
	}

	public void run(double frac) {
	    // mark the items
	    Iterator items = m_vis.visibleItems(m_group);
	    while ( items.hasNext() ) {
		VisualItem item = (VisualItem)items.next();
		item.setDOI(Constants.MINIMUM_DOI);
	    }

	    // set up the graph traversal
	    TupleSet src = m_vis.getGroup(m_sources);
	    Iterator srcs = new FilterIterator(src.tuples(), m_groupP);
	    m_bfs.init(srcs, m_successorDistance,m_predecessorDistance, Constants.NODE_AND_EDGE_TRAVERSAL);

	    // traverse the graph
	    while ( m_bfs.hasNext() ) {
		VisualItem item = (VisualItem)m_bfs.next();
		int ds = m_bfs.getSuccessorDepth(item);
		int dp = m_bfs.getPredecessorDepth(item);
		PrefuseLib.updateVisible(item, true);
		item.setDOI(-ds-dp);

		item.setExpanded(ds < m_successorDistance);

	    }

	    // mark unreached items
	    items = m_vis.visibleItems(m_group);
	    while ( items.hasNext() ) {
		VisualItem item = (VisualItem)items.next();
		if ( item.getDOI() == Constants.MINIMUM_DOI ) {
		    PrefuseLib.updateVisible(item, false);
		    item.setExpanded(false);
		}
	    }
	}

    }

    public class BreadthFirstIteratorDirectedSteps implements Iterator {

	protected DirectedDepthQueue m_queue = new DirectedDepthQueue();

	protected int   m_successorDepth;
	protected int   m_predecessorDepth;

	protected int   m_traversal;
	protected boolean m_includeNodes;
	protected boolean m_includeEdges;

	public BreadthFirstIteratorDirectedSteps() {
	    // do nothing, requires init call
	}

	public BreadthFirstIteratorDirectedSteps(Node n, int successorDepth, int predecessorDepth, int traversal) {
	    init(new Node[] {n}, successorDepth, predecessorDepth, traversal);
	}

	public BreadthFirstIteratorDirectedSteps(Iterator it, int successorDepth, int predecessorDepth, int traversal) {
	    init(it, successorDepth, predecessorDepth, traversal);
	}
	public void init(Object o, int successorDepth, int predecessorDepth, int traversal) {
	    // initialize the member variables
	    m_queue.clear();

	    m_successorDepth = successorDepth;
	    m_predecessorDepth = predecessorDepth;

	    if ( traversal < 0 || traversal >= Constants.TRAVERSAL_COUNT )
		throw new IllegalArgumentException(
			"Unrecognized traversal type: "+traversal);
	    m_traversal = traversal;
	    m_includeNodes = (traversal == Constants.NODE_TRAVERSAL || 
		    traversal == Constants.NODE_AND_EDGE_TRAVERSAL);
	    m_includeEdges = (traversal == Constants.EDGE_TRAVERSAL ||
		    traversal == Constants.NODE_AND_EDGE_TRAVERSAL);

	    // seed the queue
	    // TODO: clean this up? (use generalized iterator?)



	    if ( m_includeNodes ) {
		if ( o instanceof Node ) {
		    m_queue.add(o,0,0);
		} else {
		    Iterator tuples = (Iterator)o;
		    while ( tuples.hasNext() )
			m_queue.add(tuples.next(), 0,0);
		}
	    } else {
		if ( o instanceof Node ) {
		    Node n = (Node)o;
		    markVisit(n);
		} else {
		    Iterator tuples = (Iterator)o;
		    while ( tuples.hasNext() ) {

			Node n = (Node)tuples.next();
			markVisit(n);
		    }
		}
	    }
	}

	// ------------------------------------------------------------------------

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove() {
	    throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
	    return !m_queue.isEmpty();
	}

	protected Iterator getEdges(Node n) {
	    return n.edges(); // TODO: add support for all edges, in links only, out links only
	}

	public int getSuccessorDepth(Tuple t) {
	    return m_queue.getSuccessorDepth(t);
	}

	public int getPredecessorDepth(Tuple t) {
	    return m_queue.getPredecessorDepth(t);
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public Object next() {        
	    Tuple t = (Tuple)m_queue.removeFirst();

	    switch ( m_traversal ) {

	    case Constants.NODE_TRAVERSAL:
	    case Constants.NODE_AND_EDGE_TRAVERSAL:
		for ( ; true; t = (Tuple)m_queue.removeFirst() ) {
		    if ( t instanceof Edge ) {
			return t;
		    } else {
			Node n = (Node)t;

			int sd = m_queue.getSuccessorDepth(n);

			if ( sd < m_successorDepth ) {
			    int dd = sd+1;
			    Iterator edges = n.outEdges();
			    while ( edges.hasNext() ) {
				Edge e = (Edge)edges.next();
				Node v = e.getAdjacentNode(n);

				if ( m_includeEdges && m_queue.getSuccessorDepth(e) < 0 )
				    m_queue.add(e, dd,m_queue.getPredecessorDepth(n));
				if ( m_queue.getSuccessorDepth(v) < 0 )
				    m_queue.add(v,dd,m_queue.getPredecessorDepth(n));
			    }
			}


			int pd = m_queue.getPredecessorDepth(n);

			if ( pd < m_predecessorDepth ) {
			    int dd = pd+1;
			    Iterator edges = n.inEdges();
			    while ( edges.hasNext() ) {
				Edge e = (Edge)edges.next();
				Node v = e.getAdjacentNode(n);

				if ( m_includeEdges && m_queue.getPredecessorDepth(e) < 0 )
				    m_queue.add(e, m_queue.getSuccessorDepth(n),dd);
				if ( m_queue.getPredecessorDepth(v) < 0 )
				    m_queue.add(v, m_queue.getSuccessorDepth(n),dd);
			    }
			}


			else if ( m_includeEdges) {
			    sd = m_queue.getSuccessorDepth(n);
			    if( sd == m_successorDepth ) {
				Iterator edges = n.outEdges();
				while ( edges.hasNext() ) {
				    Edge e = (Edge)edges.next();
				    Node v = e.getAdjacentNode(n);
				    int dv = m_queue.getSuccessorDepth(v);
				    if ( dv > 0 && m_queue.getSuccessorDepth(e) < 0 ) {
					int un = m_queue.getPredecessorDepth(n);
					int uv = m_queue.getPredecessorDepth(v);
					if (sd+un<dv+uv) {
					    m_queue.add(e, sd,un);
					} else if (sd+un>dv+uv) {
					    m_queue.add(e, dv,uv);
					} else {
					    if (sd < dv ) {
						m_queue.add(e, sd,un);
					    } else {
						m_queue.add(e, dv,uv);
					    }
					}
				    }
				}
			    }
			    pd = m_queue.getPredecessorDepth(n);
			    if( pd == m_predecessorDepth ) {
				Iterator edges = n.inEdges();
				while ( edges.hasNext() ) {
				    Edge e = (Edge)edges.next();
				    Node v = e.getAdjacentNode(n);
				    int dv = m_queue.getPredecessorDepth(v);
				    if ( dv > 0 && m_queue.getPredecessorDepth(e) < 0 ) {
					int un = m_queue.getSuccessorDepth(n);
					int uv = m_queue.getSuccessorDepth(v);
					if (un+pd<uv+dv) {
					    m_queue.add(e, un,pd);
					} else if (un+pd>uv+dv) {
					    m_queue.add(e, uv,dv);
					} else {
					    if (un < uv ) {
						m_queue.add(e, un,pd);
					    } else {
						m_queue.add(e, uv,dv);
					    }
					}
				    }
				}
			    }
			}	
			return n;
		    }
		}

	    case Constants.EDGE_TRAVERSAL:
		Edge e = (Edge)t;
		Node u = e.getSourceNode();
		Node v = e.getTargetNode();

		int du = m_queue.getSuccessorDepth(u);
		int dv = m_queue.getSuccessorDepth(v);

		if ( du != dv ) {
		    Node n = (dv > du ? v : u);
		    int d1, d2;
		    if (du>dv) {
			d1=du;
			d2=m_queue.getPredecessorDepth(u);
		    } else {
			d1=dv;
			d2=m_queue.getPredecessorDepth(v);
		    }


		    if ( d1 < m_successorDepth ) {
			int dd = d1+1;
			Iterator edges = n.outEdges();
			while ( edges.hasNext() ) {
			    Edge ee = (Edge)edges.next();
			    if ( m_queue.getSuccessorDepth(ee) >= 0 )
				continue; // already visited

			    Node nn = ee.getAdjacentNode(n);
			    m_queue.visit(nn, dd,d2);
			    m_queue.add(ee, dd,d2);
			}
		    }


		    du = m_queue.getPredecessorDepth(u);
		    dv = m_queue.getPredecessorDepth(v);

		    if ( du != dv ) {
			n = (dv > du ? v : u);

			if (du>dv) {
			    d1=du;
			    d2=m_queue.getSuccessorDepth(u);
			} else {
			    d1=dv;
			    d2=m_queue.getSuccessorDepth(v);
			}


			if ( d1 < m_predecessorDepth ) {
			    int dd = d1+1;
			    Iterator edges = n.inEdges();
			    while ( edges.hasNext() ) {
				Edge ee = (Edge)edges.next();
				if ( m_queue.getPredecessorDepth(ee) >= 0 )
				    continue; // already visited

				Node nn = ee.getAdjacentNode(n);
				m_queue.visit(nn, d2,dd);
				m_queue.add(ee, d2,dd);
			    }
			}
		    }

		}

		return e;

	    default:
		throw new IllegalStateException();
	    }
	}

	private void markVisit(Node n) {
	    m_queue.visit(n,0,0);
	    Iterator edges = n.edges();
	    while ( edges.hasNext() ) {
		Edge e = (Edge)edges.next();
		Node nn = e.getAdjacentNode(n);

		if (nn == e.getTargetNode()) {

		    m_queue.visit(nn, 1,0);
		    if ( m_queue.getSuccessorDepth(e) < 0 ) {
			m_queue.add(e,1,0);
		    }
		} else {
		    m_queue.visit(nn, 0,1);
		    if ( m_queue.getPredecessorDepth(e) < 0 ) {
			m_queue.add(e,0,1);
		    }
		}
	    }

	}
    } // end of class BreadthFirstIterator
    public class DirectedDepthQueue {

	private LinkedList m_list = new LinkedList();
	private HashMap    m_map  = new HashMap();

	public void clear() {
	    m_list.clear();
	    m_map.clear();
	}

	public boolean isEmpty() {
	    return m_list.isEmpty();
	}

	public void add(Object o, int downDepth, int upDepth) {
	    m_list.add(o);
	    m_map.put(o,getArray(downDepth,upDepth));    
	}


	public void visit(Object o,int downDepth, int upDepth) {
	    m_map.put(o, getArray(downDepth,upDepth));
	}

	public int getSuccessorDepth(Object o) {
	    int[] d = (int[])m_map.get(o);
	    return ( d==null ? -1 : d[0] );
	}

	public int getPredecessorDepth(Object o) {
	    int[] d = (int[])m_map.get(o);
	    return ( d==null ? -1 : d[1] );
	}


	public Object removeFirst() {
	    return m_list.removeFirst();
	}

	public Object removeLast() {
	    return m_list.removeLast();
	}

	private int[] getArray(int a, int b) {
	    int[] d = new int[2];
	    d[0] = a;
	    d[1] = b;
	    return d;
	}

    } // end of class Queue
    public class CollapsedSubtreeLayoutChangingTreeRoot extends Layout {

	private int m_orientation;
	private Point2D m_point = new Point2D.Double();

	public CollapsedSubtreeLayoutChangingTreeRoot(String group) {
	    this(group, Constants.ORIENT_CENTER);
	}
	public CollapsedSubtreeLayoutChangingTreeRoot(String group, int orientation) {
	    super(group);
	    m_orientation = orientation;
	}

	// ------------------------------------------------------------------------

	public int getOrientation() {
	    return m_orientation;
	}
	public void setOrientation(int orientation) {
	    if ( orientation < 0 || orientation >= Constants.ORIENTATION_COUNT )
		throw new IllegalArgumentException(
			"Unrecognized orientation value: "+orientation);
	    m_orientation = orientation;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see prefuse.action.Action#run(double)
	 */
	public void run(double frac) {
	    // handle newly expanded subtrees - ensure they emerge from
	    // a visible ancestor node or successor node

	    NodeItem root = (NodeItem) m_vis.items(m_group,"ISNODE() AND VISIBLE()").next();

	    while (root.getParent() != null) {
		NodeItem parent = (NodeItem) root.getParent();
		if (parent.isVisible()) {
		    root = parent;
		} else {
		    break;
		}
	    }


	    Iterator items = m_vis.visibleItems(m_group);
	    while ( items.hasNext() ) {
		VisualItem item = (VisualItem) items.next();
		if ( item instanceof NodeItem && !item.isStartVisible() ) {
		    NodeItem n = (NodeItem)item;
		    Point2D p = getPoint(n, true,root);
		    n.setStartX(p.getX());
		    n.setStartY(p.getY());
		}
	    }

	    // handle newly collapsed nodes - ensure they collapse to
	    // the greatest visible ancestor node
	    items = m_vis.items(m_group, StartVisiblePredicate.TRUE);
	    while ( items.hasNext() ) {
		VisualItem item = (VisualItem) items.next();
		if ( item instanceof NodeItem && !item.isEndVisible() ) {
		    NodeItem n = (NodeItem)item;
		    Point2D p = getPoint(n, false,root);
		    n.setStartX(n.getEndX());
		    n.setStartY(n.getEndY());
		    n.setEndX(p.getX());
		    n.setEndY(p.getY());
		}
	    }
	}

	private Point2D getPoint(NodeItem n, boolean start,NodeItem root) {
	    // find the visible ancestor
	    NodeItem p = (NodeItem)n.getParent();

	    if ( start ) {
		for (; p!=null && !p.isStartVisible(); p=(NodeItem)p.getParent());
	    } else {
		for (; p!=null && !p.isEndVisible(); p=(NodeItem)p.getParent());
	    }
	    if ( p == null ) {    
		m_point.setLocation(root.getX(), root.getY());
		return m_point;
	    }

	    // get the vanishing/appearing point
	    double x = start ? p.getStartX() : p.getEndX();
	    double y = start ? p.getStartY() : p.getEndY();
	    Rectangle2D b = p.getBounds();
	    switch ( m_orientation ) {
	    case Constants.ORIENT_LEFT_RIGHT:
		m_point.setLocation(x+b.getWidth(), y);
		break;
	    case Constants.ORIENT_RIGHT_LEFT:
		m_point.setLocation(x-b.getWidth(), y);
		break;
	    case Constants.ORIENT_TOP_BOTTOM:
		m_point.setLocation(x, y+b.getHeight());
		break;
	    case Constants.ORIENT_BOTTOM_TOP:
		m_point.setLocation(x, y-b.getHeight());
		break;
	    case Constants.ORIENT_CENTER:
		m_point.setLocation(x, y);
		break;
	    }
	    return m_point;
	}

    } 
}
