package prefuse.demos.pap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.Layout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceItem;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.util.io.IOLib;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;


/**
 * This graph view demo shows applications of the graph distance filter to two foucs groups
 * 1) The focus group containing the node under the mouse cursor if any
 * 2) The search result if non empty
 * 
 * In addition, a modification of the ForceDirectedLayout is used, which allows invisible
 * nodes/edges to be taken into account if desired
 *  
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net/34all">martin dudek</a>
 */

public class GraphViewDistanceFilterVariationsInDMinorMagic extends JPanel {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";

    private static final String hover = "hover";


    private Visualization m_vis;

    public GraphViewDistanceFilterVariationsInDMinorMagic(Graph g, String label) {

	// create a new, empty visualization for our data
	m_vis = new Visualization();

	// --------------------------------------------------------------------
	// set up the renderers

	LabelRenderer tr = new LabelRenderer();
	tr.setRoundedCorner(8, 8);
	m_vis.setRendererFactory(new DefaultRendererFactory(tr));

	// --------------------------------------------------------------------
	// register the data with a visualization

	// adds graph to visualization and sets renderer label field
	setGraph(g, label);

	// --------------
	// The filter for the hover group

	GraphDistanceFilter hoverFilter = new GraphDistanceFilter(graph,hover,Integer.MAX_VALUE);
	hoverFilter.setEnabled(false);

	// The tuple set listener for the hover group
	final GroupDistanceListener hoverListener = new GroupDistanceListener(graph,m_vis,hoverFilter,"onlydraw");

	m_vis.addFocusGroup(hover);
	TupleSet hoverGroup = m_vis.getFocusGroup(hover);
	hoverGroup.addTupleSetListener(hoverListener);

	// The filter for the search group

	GraphDistanceFilter searchFilter = new GraphDistanceFilter(graph,Visualization.SEARCH_ITEMS,Integer.MAX_VALUE);
	searchFilter.setEnabled(false);

	final GroupDistanceListener searchListener = new GroupDistanceListener(graph,m_vis,searchFilter,"onlydraw");

	SearchTupleSet search = new PrefixSearchTupleSet();
	m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
	search.addTupleSetListener(searchListener);

	// --------------------------------------------------------------------
	// create actions to process the visual data

	ColorAction fill = new ColorAction(nodes, 
		VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
	fill.add("ingroup('_search_')", ColorLib.rgb(255,190,190));
	fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
	fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));

	//ItemAction edgeColor = new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200));
	//ItemAction edgeColor2 = new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200));

	ActionList draw = new ActionList();
	draw.add(hoverFilter);
	draw.add(searchFilter);
	draw.add(fill);
	draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
	draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));

	//draw.add(edgeColor);
	//draw.add(edgeColor2);
	draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
	draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));

	final ForceDirectedLayoutMagic fdlm = new ForceDirectedLayoutMagic(graph);

	ActionList animate = new ActionList(Activity.INFINITY);
	animate.add(fdlm);
	animate.add(fill);
	animate.add(new RepaintAction());

	// finally, we register our ActionList with the Visualization.
	// we can later execute our Actions by invoking a method on our
	// Visualization, using the name we've chosen below.
	m_vis.putAction("draw", draw);
	m_vis.putAction("onlydraw", draw); //draw only
	m_vis.putAction("layout", animate);

	m_vis.runAfter("draw", "layout");


	// --------------------------------------------------------------------
	// set up a display to show the visualization

	Display display = new Display(m_vis);
	display.setSize(700,700);
	display.pan(350, 350);
	display.setForeground(Color.GRAY);
	display.setBackground(Color.WHITE);

	// main display controls

	display.addControlListener(new DragControl());
	display.addControlListener(new PanControl());
	display.addControlListener(new ZoomControl());
	display.addControlListener(new WheelZoomControl());
	display.addControlListener(new ZoomToFitControl());
	display.addControlListener(new NeighborHighlightControl());
	display.addControlListener(new HoverControl(hover));

	// overview display
//	Display overview = new Display(vis);
//	overview.setSize(290,290);
//	overview.addItemBoundsListener(new FitOverviewListener());

	display.setForeground(Color.GRAY);
	display.setBackground(Color.WHITE);

	// --------------------------------------------------------------------        
	// launch the visualization

	// create a panel for editing force values
	ForceSimulator fsim = ((ForceDirectedLayoutMagic)animate.get(0)).getForceSimulator();
	JForcePanel fpanel = new JForcePanel(fsim);

//	JPanel opanel = new JPanel();
//	opanel.setBorder(BorderFactory.createTitledBorder("Overview"));
//	opanel.setBackground(Color.WHITE);
//	opanel.add(overview);

	final JValueSlider slider = new JValueSlider("Distance", 0, 4, 1);
	slider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		hoverListener.setDistance(slider.getValue().intValue());
		searchListener.setDistance(slider.getValue().intValue());
		m_vis.run("onlydraw");

	    }
	});


	slider.setBackground(Color.WHITE);
	slider.setPreferredSize(new Dimension(300,30));
	slider.setMaximumSize(new Dimension(300,30));


	Box cf = new Box(BoxLayout.Y_AXIS);
	cf.add(slider);
	cf.setBorder(BorderFactory.createTitledBorder("Distance"));
	fpanel.add(cf);


	final JCheckBox magicNodes = new JCheckBox("Magic nodes");
	magicNodes.setSelected(true);
	magicNodes.setBackground(Color.WHITE);
	//magicNodes.setPreferredSize(new Dimension(100,30));
	magicNodes.setMaximumSize(new Dimension(200,30));

	
	magicNodes.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		fdlm.setMagicNodes(e.getStateChange() == ItemEvent.SELECTED);	
	    }
	});
	
	final JCheckBox magicEdges = new JCheckBox("Magic edges");
	magicEdges.setSelected(true);
	magicEdges.setBackground(Color.WHITE);
	magicEdges.setMaximumSize(new Dimension(200,30));


	magicEdges.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		fdlm.setMagicEdges(e.getStateChange() == ItemEvent.SELECTED);	
	    }
	});


	Box df = new Box(BoxLayout.X_AXIS);
	df.add(magicNodes);
	df.add(magicEdges);
	
	df.setBorder(BorderFactory.createTitledBorder("Magic force directed layout"));
	fpanel.add(df);




//	create a search panel for the tree map
	SearchQueryBinding sq = new SearchQueryBinding(
		(Table)m_vis.getGroup(nodes), label,
		(SearchTupleSet)m_vis.getGroup(Visualization.SEARCH_ITEMS));
	JSearchPanel searchPanel = sq.createSearchPanel();
	searchPanel.setShowResultCount(true);
	searchPanel.setMaximumSize(new Dimension(300,20));

	searchPanel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

	Box sf = new Box(BoxLayout.Y_AXIS);
	sf.add(searchPanel);
	sf.setBorder(BorderFactory.createTitledBorder("Search"));
	fpanel.add(sf);

	fpanel.add(Box.createVerticalGlue());

	// create a new JSplitPane to present the interface
	JSplitPane split = new JSplitPane();
	split.setLeftComponent(display);
	split.setRightComponent(fpanel);
	split.setOneTouchExpandable(true);
	split.setContinuousLayout(false);
	split.setDividerLocation(700);

	// now we run our action list
	m_vis.run("draw");

	add(split);
    }

    public void setGraph(Graph g, String label) {
	// update labeling
	DefaultRendererFactory drf = (DefaultRendererFactory)
	m_vis.getRendererFactory();
	((LabelRenderer)drf.getDefaultRenderer()).setTextField(label);

	// update graph
	m_vis.removeGroup(graph);
	VisualGraph vg = m_vis.addGraph(graph, g);
	m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);


    }

    // ------------------------------------------------------------------------
    // Main and demo methods

    public static void main(String[] args) {
	UILib.setPlatformLookAndFeel();

	// create graphview
	//String datafile = null;
	//String label = "label";

	String datafile = "data/socialnet.xml";
	String label = "name";

	if ( args.length > 1 ) {
	    datafile = args[0];
	    label = args[1];
	}

	JFrame frame = demo(datafile, label);
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static JFrame demo() {
	return demo((String)null, "label");
    }

    public static JFrame demo(String datafile, String label) {
	Graph g = null;
	if ( datafile == null ) {
	    g = GraphLib.getGrid(15,15);
	    label = "label";
	} else {
	    try {
		g = new GraphMLReader().readGraph(datafile);
	    } catch ( Exception e ) {
		e.printStackTrace();
		System.exit(1);
	    }
	}
	return demo(g, label);
    }

    public static JFrame demo(Graph g, String label) {
	final GraphViewDistanceFilterVariationsInDMinorMagic view = new GraphViewDistanceFilterVariationsInDMinorMagic(g, label);

	// set up menu
	JMenu dataMenu = new JMenu("Data");
	dataMenu.add(new OpenGraphAction(view));
	dataMenu.add(new GraphMenuAction("Grid","ctrl 1",view) {
	    protected Graph getGraph() {
		return GraphLib.getGrid(15,15);
	    }
	});
	dataMenu.add(new GraphMenuAction("Clique","ctrl 2",view) {
	    protected Graph getGraph() {
		return GraphLib.getClique(10);
	    }
	});
	dataMenu.add(new GraphMenuAction("Honeycomb","ctrl 3",view) {
	    protected Graph getGraph() {
		return GraphLib.getHoneycomb(5);
	    }
	});
	dataMenu.add(new GraphMenuAction("Balanced Tree","ctrl 4",view) {
	    protected Graph getGraph() {
		return GraphLib.getBalancedTree(3,5);
	    }
	});
	dataMenu.add(new GraphMenuAction("Diamond Tree","ctrl 5",view) {
	    protected Graph getGraph() {
		return GraphLib.getDiamondTree(3,3,3);
	    }
	});
	JMenuBar menubar = new JMenuBar();
	menubar.add(dataMenu);

	// launch window
	JFrame frame = new JFrame("p r e f u s e  |  graph distance filter variations in d minor magic");
	frame.setJMenuBar(menubar);
	frame.setContentPane(view);
	frame.pack();
	frame.setVisible(true);

	frame.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {
		view.m_vis.run("layout");
	    }
	    public void windowDeactivated(WindowEvent e) {
		view.m_vis.cancel("layout");
	    }
	});

	return frame;
    }


    // ------------------------------------------------------------------------

    /**
     * Swing menu action that loads a graph into the graph viewer.
     */
    public abstract static class GraphMenuAction extends AbstractAction {
	private GraphViewDistanceFilterVariationsInDMinorMagic m_view;
	public GraphMenuAction(String name, String accel, GraphViewDistanceFilterVariationsInDMinorMagic view) {
	    m_view = view;
	    this.putValue(AbstractAction.NAME, name);
	    this.putValue(AbstractAction.ACCELERATOR_KEY,
		    KeyStroke.getKeyStroke(accel));
	}
	public void actionPerformed(ActionEvent e) {
	    m_view.setGraph(getGraph(), "label");
	}
	protected abstract Graph getGraph();
    }

    public static class OpenGraphAction extends AbstractAction {
	private GraphViewDistanceFilterVariationsInDMinorMagic m_view;

	public OpenGraphAction(GraphViewDistanceFilterVariationsInDMinorMagic view) {
	    m_view = view;
	    this.putValue(AbstractAction.NAME, "Open File...");
	    this.putValue(AbstractAction.ACCELERATOR_KEY,
		    KeyStroke.getKeyStroke("ctrl O"));
	}
	public void actionPerformed(ActionEvent e) {
	    Graph g = IOLib.getGraphFile(m_view);
	    if ( g == null ) return;
	    String label = getLabel(m_view, g);
	    if ( label != null ) {
		m_view.setGraph(g, label);
	    }
	}
	public static String getLabel(Component c, Graph g) {
	    // get the column names
	    Table t = g.getNodeTable();
	    int  cc = t.getColumnCount();
	    String[] names = new String[cc];
	    for ( int i=0; i<cc; ++i )
		names[i] = t.getColumnName(i);

	    // where to store the result
	    final String[] label = new String[1];

	    // -- build the dialog -----
	    // we need to get the enclosing frame first
	    while ( c != null && !(c instanceof JFrame) ) {
		c = c.getParent();
	    }
	    final JDialog dialog = new JDialog(
		    (JFrame)c, "Choose Label Field", true);

	    // create the ok/cancel buttons
	    final JButton ok = new JButton("OK");
	    ok.setEnabled(false);
	    ok.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    dialog.setVisible(false);
		}
	    });
	    JButton cancel = new JButton("Cancel");
	    cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    label[0] = null;
		    dialog.setVisible(false);
		}
	    });

	    // build the selection list
	    final JList list = new JList(names);
	    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    list.getSelectionModel().addListSelectionListener(
		    new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
			    int sel = list.getSelectedIndex(); 
			    if ( sel >= 0 ) {
				ok.setEnabled(true);
				label[0] = (String)list.getModel().getElementAt(sel);
			    } else {
				ok.setEnabled(false);
				label[0] = null;
			    }
			}
		    });
	    JScrollPane scrollList = new JScrollPane(list);

	    JLabel title = new JLabel("Choose a field to use for node labels:");

	    // layout the buttons
	    Box bbox = new Box(BoxLayout.X_AXIS);
	    bbox.add(Box.createHorizontalStrut(5));
	    bbox.add(Box.createHorizontalGlue());
	    bbox.add(ok);
	    bbox.add(Box.createHorizontalStrut(5));
	    bbox.add(cancel);
	    bbox.add(Box.createHorizontalStrut(5));

	    // put everything into a panel
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.add(title, BorderLayout.NORTH);
	    panel.add(scrollList, BorderLayout.CENTER);
	    panel.add(bbox, BorderLayout.SOUTH);
	    panel.setBorder(BorderFactory.createEmptyBorder(5,2,2,2));

	    // show the dialog
	    dialog.setContentPane(panel);
	    dialog.pack();
	    dialog.setLocationRelativeTo(c);
	    dialog.setVisible(true);
	    dialog.dispose();

	    // return the label field selection
	    return label[0];
	}
    }

    public static class FitOverviewListener implements ItemBoundsListener {
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

    /**
     * A simple control maintaining a focus group which contains
     * only the node under the mouse cursor if any.
     * 
     * @author martin dudek
     *
     */

    public class HoverControl extends ControlAdapter {
	String hoverGroupName;

	public HoverControl(String hoverGroupName) {
	    this.hoverGroupName = hoverGroupName;
	}
	public void itemEntered(VisualItem item, MouseEvent e) {

	    if (item instanceof NodeItem) {
		Visualization vis = item.getVisualization();
		vis.getGroup(this.hoverGroupName).setTuple(item);
	    }
	}

	public void itemExited(VisualItem item, MouseEvent e) {

	    if (item instanceof NodeItem) {
		Visualization vis = item.getVisualization();
		vis.getGroup(this.hoverGroupName).removeTuple(item);
	    }
	}
    }

    /**
     *	Generic TupleSetListener applying a distance filter to a given focus group 
     *  If the focus group is empty, the filter is disabled. (you might modify this 
     *  according to your needs)
     *
     * @author martin dudek
     *
     */

    public class GroupDistanceListener implements TupleSetListener {

	String graph;
	Visualization vis;
	GraphDistanceFilter filter;
	String drawAction;

	ArrayList previousVisibleItems;
	int distance;

	boolean lastTimeFiltered = false;

	public GroupDistanceListener(String graphName,Visualization vis,GraphDistanceFilter filter,String drawAction) {
	    this(graphName,vis,filter,drawAction,1);
	}

	public GroupDistanceListener(String graph,Visualization vis,GraphDistanceFilter filter,String drawAction,int distance) {
	    this.graph = graph;
	    this.vis = vis;
	    this.filter = filter;
	    this.drawAction = drawAction;
	    this.distance = distance;
	    previousVisibleItems = new ArrayList();
	}

	public void setDistance(int distance) {
	    this.distance = distance;
	    filter.setDistance(distance);
	}

	public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
	{
	    if ( ts.getTupleCount() == 0 ) {
		if (previousVisibleItems != null) {
		    Iterator iter = previousVisibleItems.iterator(); // reconstructimg the pre filtered state
		    while (iter.hasNext()) {
			VisualItem aItem = (VisualItem) iter.next();
			aItem.setVisible(true);
		    }
		}
		lastTimeFiltered = false;
		filter.setEnabled(false);

	    } else {
		if (!lastTimeFiltered) { // remembering the last unfiltered set of visible items
		    previousVisibleItems.clear();
		    Iterator iter  = vis.visibleItems(graph);
		    while (iter.hasNext()) {
			VisualItem aItem = (VisualItem) iter.next();
			previousVisibleItems.add(aItem);
		    }
		}
		lastTimeFiltered = true;
		filter.setEnabled(true);
		filter.setDistance(distance);

	    }
	    vis.run(drawAction);
	}
    }


} // end of class GraphView

/**
 * A modification of the ForceDirectedLayout, which allows to set all
 * edges and/or nodes magic. The forces of magic nodes/edges are still computed
 * even if the nodes/edges is invisible.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net"> martin dudek</a>
 *
 */
class ForceDirectedLayoutMagic extends Layout {
    private boolean magicNodes = true;
    private boolean magicEdges = true;

    private ForceSimulator m_fsim;
    private long m_lasttime = -1L;
    private long m_maxstep = 50L;
    private boolean m_runonce;
    private int m_iterations = 100;
    private boolean m_enforceBounds;

    protected transient VisualItem referrer;

    protected String m_nodeGroup;
    protected String m_edgeGroup;

    /**
     * Create a new ForceDirectedLayoutMagic. By default, this layout will not
     * restrict the layout to the layout bounds and will assume it is being
     * run in animated (rather than run-once) fashion.
     * @param graph the data group to layout. Must resolve to a Graph instance.
     */
    public ForceDirectedLayoutMagic(String graph)
    {
	this(graph, false, false);
    }

    /**
     * Create a new ForceDirectedLayoutMagic. The layout will assume it is being
     * run in animated (rather than run-once) fashion.
     * @param group the data group to layout. Must resolve to a Graph instance.
     * @param enforceBounds indicates whether or not the layout should require
     * that all node placements stay within the layout bounds.
     */
    public ForceDirectedLayoutMagic(String group, boolean enforceBounds)
    {
	this(group, enforceBounds, false);
    }

    /**
     * Create a new ForceDirectedLayoutMagic.
     * @param group the data group to layout. Must resolve to a Graph instance.
     * @param enforceBounds indicates whether or not the layout should require
     * that all node placements stay within the layout bounds.
     * @param runonce indicates if the layout will be run in a run-once or
     * animated fashion. In run-once mode, the layout will run for a set number
     * of iterations when invoked. In animation mode, only one iteration of the
     * layout is computed.
     */
    public ForceDirectedLayoutMagic(String group,
	    boolean enforceBounds, boolean runonce)
    {
	super(group);
	m_nodeGroup = PrefuseLib.getGroupName(group, Graph.NODES);
	m_edgeGroup = PrefuseLib.getGroupName(group, Graph.EDGES);

	m_enforceBounds = enforceBounds;
	m_runonce = runonce;
	m_fsim = new ForceSimulator();
	m_fsim.addForce(new NBodyForce());
	m_fsim.addForce(new SpringForce());
	m_fsim.addForce(new DragForce());
    }

    /**
     * Create a new ForceDirectedLayoutMagic. The layout will assume it is being
     * run in animated (rather than run-once) fashion.
     * @param group the data group to layout. Must resolve to a Graph instance.
     * @param fsim the force simulator used to drive the layout computation
     * @param enforceBounds indicates whether or not the layout should require
     * that all node placements stay within the layout bounds.
     */
    public ForceDirectedLayoutMagic(String group,
	    ForceSimulator fsim, boolean enforceBounds) {
	this(group, fsim, enforceBounds, false);
    }

    /**
     * Create a new ForceDirectedLayoutMagic.
     * @param group the data group to layout. Must resolve to a Graph instance.
     * @param fsim the force simulator used to drive the layout computation
     * @param enforceBounds indicates whether or not the layout should require
     * that all node placements stay within the layout bounds.
     * @param runonce indicates if the layout will be run in a run-once or
     * animated fashion. In run-once mode, the layout will run for a set number
     * of iterations when invoked. In animation mode, only one iteration of the
     * layout is computed.
     */
    public ForceDirectedLayoutMagic(String group, ForceSimulator fsim,
	    boolean enforceBounds, boolean runonce)
    {
	super(group);
	m_nodeGroup = PrefuseLib.getGroupName(group, Graph.NODES);
	m_edgeGroup = PrefuseLib.getGroupName(group, Graph.EDGES);

	m_enforceBounds = enforceBounds;
	m_runonce = runonce;
	m_fsim = fsim;
    }

    // ------------------------------------------------------------------------

    /**
     * Get the maximum timestep allowed for integrating node settings between
     * runs of this layout. When computation times are longer than desired,
     * and node positions are changing dramatically between animated frames,
     * the max step time can be lowered to suppress node movement.
     * @return the maximum timestep allowed for integrating between two
     * layout steps.
     */
    public long getMaxTimeStep() {
	return m_maxstep;
    }

    /**
     * Set the maximum timestep allowed for integrating node settings between
     * runs of this layout. When computation times are longer than desired,
     * and node positions are changing dramatically between animated frames,
     * the max step time can be lowered to suppress node movement.
     * @param maxstep the maximum timestep allowed for integrating between two
     * layout steps
     */
    public void setMaxTimeStep(long maxstep) {
	this.m_maxstep = maxstep;
    }

    /**
     * Get the force simulator driving this layout.
     * @return the force simulator
     */
    public ForceSimulator getForceSimulator() {
	return m_fsim;
    }

    /**
     * Set the force simulator driving this layout.
     * @param fsim the force simulator
     */
    public void setForceSimulator(ForceSimulator fsim) {
	m_fsim = fsim;
    }

    /**
     * Get the number of iterations to use when computing a layout in
     * run-once mode.
     * @return the number of layout iterations to run
     */
    public int getIterations() {
	return m_iterations;
    }

    /**
     * Set the number of iterations to use when computing a layout in
     * run-once mode.
     * @param iter the number of layout iterations to run
     */
    public void setIterations(int iter) {
	if ( iter < 1 )
	    throw new IllegalArgumentException(
	    "Iterations must be a positive number!");
	m_iterations = iter;
    }

    /**
     * Explicitly sets the node and edge groups to use for this layout,
     * overriding the group setting passed to the constructor.
     * @param nodeGroup the node data group
     * @param edgeGroup the edge data group
     */
    public void setDataGroups(String nodeGroup, String edgeGroup) {
	m_nodeGroup = nodeGroup;
	m_edgeGroup = edgeGroup;
    }

    // ------------------------------------------------------------------------

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
	// perform different actions if this is a run-once or
	// run-continuously layout
	if ( m_runonce ) {
	    Point2D anchor = getLayoutAnchor();

	    Iterator iter = getMagicIterator(m_nodeGroup);


	    while ( iter.hasNext() ) {
		VisualItem  item = (NodeItem)iter.next();
		item.setX(anchor.getX());
		item.setY(anchor.getY());
	    }
	    m_fsim.clear();
	    long timestep = 1000L;
	    initSimulator(m_fsim);
	    for ( int i = 0; i < m_iterations; i++ ) {
		// use an annealing schedule to set time step
		timestep *= (1.0 - i/(double)m_iterations);
		long step = timestep+50;
		// run simulator
		m_fsim.runSimulator(step);
		// debugging output
//		if (i % 10 == 0 ) {
//		System.out.println("iter: "+i);
//		}
	    }
	    updateNodePositions();
	} else {
	    // get timestep
	    if ( m_lasttime == -1 )
		m_lasttime = System.currentTimeMillis()-20;
	    long time = System.currentTimeMillis();
	    long timestep = Math.min(m_maxstep, time - m_lasttime);
	    m_lasttime = time;

	    // run force simulator
	    m_fsim.clear();
	    initSimulator(m_fsim);
	    m_fsim.runSimulator(timestep);
	    updateNodePositions();
	}
	if ( frac == 1.0 ) {
	    reset();
	}
    }

    private void updateNodePositions() {
	Rectangle2D bounds = getLayoutBounds();
	double x1=0, x2=0, y1=0, y2=0;
	if ( bounds != null ) {
	    x1 = bounds.getMinX(); y1 = bounds.getMinY();
	    x2 = bounds.getMaxX(); y2 = bounds.getMaxY();
	}

	// update positions
	Iterator iter = getMagicIterator(m_nodeGroup);

	while ( iter.hasNext() ) {
	    VisualItem item = (VisualItem)iter.next();
	    ForceItem fitem = (ForceItem)item.get(FORCEITEM);

	    if ( item.isFixed() ) {
		// clear any force computations
		fitem.force[0] = 0.0f;
		fitem.force[1] = 0.0f;
		fitem.velocity[0] = 0.0f;
		fitem.velocity[1] = 0.0f;

		if ( Double.isNaN(item.getX()) ) {
		    super.setX(item, referrer, 0.0);
		    super.setY(item, referrer, 0.0);
		}
		continue;
	    }

	    double x = fitem.location[0];
	    double y = fitem.location[1];

	    if ( m_enforceBounds && bounds != null) {
		Rectangle2D b = item.getBounds();
		double hw = b.getWidth()/2;
		double hh = b.getHeight()/2;
		if ( x+hw > x2 ) x = x2-hw;
		if ( x-hw < x1 ) x = x1+hw;
		if ( y+hh > y2 ) y = y2-hh;
		if ( y-hh < y1 ) y = y1+hh;
	    }

	    // set the actual position
	    super.setX(item, referrer, x);
	    super.setY(item, referrer, y);
	}
    }

    /**
     * Reset the force simulation state for all nodes processed
     * by this layout.
     */
    public void reset() {
	Iterator iter = getMagicIterator(m_nodeGroup); 


	while ( iter.hasNext() ) {
	    VisualItem item = (VisualItem)iter.next();
	    ForceItem fitem = (ForceItem)item.get(FORCEITEM);
	    if ( fitem != null ) {
		fitem.location[0] = (float)item.getEndX();
		fitem.location[1] = (float)item.getEndY();
		fitem.force[0]    = fitem.force[1]    = 0;
		fitem.velocity[0] = fitem.velocity[1] = 0;
	    }
	}
	m_lasttime = -1L;
    }

    /**
     * Loads the simulator with all relevant force items and springs.
     * @param fsim the force simulator driving this layout
     */
    protected void initSimulator(ForceSimulator fsim) {     
	// make sure we have force items to work with
	TupleSet ts = m_vis.getGroup(m_nodeGroup);
	if ( ts == null ) return;
	try {
	    ts.addColumns(FORCEITEM_SCHEMA);
	} catch ( IllegalArgumentException iae ) { /* ignored */ }

	float startX = (referrer == null ? 0f : (float)referrer.getX());
	float startY = (referrer == null ? 0f : (float)referrer.getY());
	startX = Float.isNaN(startX) ? 0f : startX;
	startY = Float.isNaN(startY) ? 0f : startY;

	Iterator iter = getMagicIterator(m_nodeGroup);
	while ( iter.hasNext() ) {
	    VisualItem item = (VisualItem)iter.next();
	    ForceItem fitem = (ForceItem)item.get(FORCEITEM);
	    fitem.mass = getMassValue(item);
	    double x = item.getEndX();
	    double y = item.getEndY();
	    fitem.location[0] = (Double.isNaN(x) ? startX : (float)x);
	    fitem.location[1] = (Double.isNaN(y) ? startY : (float)y);
	    fsim.addItem(fitem);
	}
	if ( m_edgeGroup != null ) {
	    iter = getMagicIterator(m_edgeGroup);
	    while ( iter.hasNext() ) {
		EdgeItem  e  = (EdgeItem)iter.next();
		NodeItem  n1 = e.getSourceItem();
		ForceItem f1 = (ForceItem)n1.get(FORCEITEM);
		NodeItem  n2 = e.getTargetItem();
		ForceItem f2 = (ForceItem)n2.get(FORCEITEM);
		float coeff = getSpringCoefficient(e);
		float slen = getSpringLength(e);
		fsim.addSpring(f1, f2, (coeff>=0?coeff:-1.f), (slen>=0?slen:-1.f));
	    }
	}
    }

    /**
     * Get the mass value associated with the given node. Subclasses should
     * override this method to perform custom mass assignment.
     * @param n the node for which to compute the mass value
     * @return the mass value for the node. By default, all items are given
     * a mass value of 1.0.
     */
    protected float getMassValue(VisualItem n) {
	return 1.0f;
    }

    /**
     * Get the spring length for the given edge. Subclasses should
     * override this method to perform custom spring length assignment.
     * @param e the edge for which to compute the spring length
     * @return the spring length for the edge. A return value of
     * -1 means to ignore this method and use the global default.
     */
    protected float getSpringLength(EdgeItem e) {
	return -1.f;
    }

    /**
     * Get the spring coefficient for the given edge, which controls the
     * tension or strength of the spring. Subclasses should
     * override this method to perform custom spring tension assignment.
     * @param e the edge for which to compute the spring coefficient.
     * @return the spring coefficient for the edge. A return value of
     * -1 means to ignore this method and use the global default.
     */
    protected float getSpringCoefficient(EdgeItem e) {
	return -1.f;
    }

    /**
     * Get the referrer item to use to set x or y coordinates that are
     * initialized to NaN.
     * @return the referrer item.
     * @see prefuse.util.PrefuseLib#setX(VisualItem, VisualItem, double)
     * @see prefuse.util.PrefuseLib#setY(VisualItem, VisualItem, double)
     */
    public VisualItem getReferrer() {
	return referrer;
    }

    /**
     * Set the referrer item to use to set x or y coordinates that are
     * initialized to NaN.
     * @param referrer the referrer item to use.
     * @see prefuse.util.PrefuseLib#setX(VisualItem, VisualItem, double)
     * @see prefuse.util.PrefuseLib#setY(VisualItem, VisualItem, double)
     */
    public void setReferrer(VisualItem referrer) {
	this.referrer = referrer;
    }

    // ------------------------------------------------------------------------
    // ForceItem Schema Addition

    /**
     * The data field in which the parameters used by this layout are stored.
     */
    public static final String FORCEITEM = "_forceItem";
    /**
     * The schema for the parameters used by this layout.
     */
    public static final Schema FORCEITEM_SCHEMA = new Schema();
    static {
	FORCEITEM_SCHEMA.addColumn(FORCEITEM,
		ForceItem.class,
		new ForceItem());
    }

    public Iterator getMagicIterator(String group) {
	    
	if (group == m_nodeGroup) {
	    if (magicNodes) {
		return m_vis.items(group);
	    } else {
		return m_vis.visibleItems(group);
	    }
	} else {
	    if (magicEdges) {
		return m_vis.items(group);
	    } else {
		return m_vis.visibleItems(group);
	    }
	}
    }

    public boolean hasMagicEdges() {
	return magicEdges;
    }

    public void setMagicEdges(boolean enabled) {
	this.magicEdges = enabled;
    }

    public boolean hasMagicNodes() {
	return magicNodes;
    }

    public void setMagicNodes(boolean enabled) {
	this.magicNodes = enabled;
    }

}

