package profusians.demos.layout;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.PolarLocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.HoverActionControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import profusians.action.animate.RotationAnimator;
import profusians.action.assingment.TreeColorRangeAction;
import profusians.action.layout.graph.RadialRotationTreeLayout;
import profusians.render.RotationLabelRenderer;

/**
 * 
 * This demo introduces three new classes of the profuse library.
 * 
 * RadialRotationTreeLayout: This layout, which is meant to be used in
 * connection with the RotationLabelRenderer by Christopher Collins, enables the
 * rotation of nodes within the layout according to different rotation
 * orientation. The resulting angle can be scaled by setting the rotation scale
 * factor accordingly.
 * 
 * RotationAnimator: This ItemAction extension aimates the rotation of nodes
 * between two different rotation angles according to different rotation
 * animation styles.
 * 
 * TreeColorRangeAction: This action colors the visible text labels of a tree
 * with colors within a given color range.
 * 
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class RadialGraphViewRotationTreeLayout extends Display {

    public static final String DATA_FILE = "data/socialnet.xml";

    private static final String tree = "tree";

    private static final String treeNodes = "tree.nodes";

    private static final String treeEdges = "tree.edges";

    private static final String linear = "linear";

    private RotationLabelRenderer m_nodeRenderer;

    private EdgeRenderer m_edgeRenderer;

    private String m_label = "name";

    private static RadialRotationTreeLayout treeLayout;

    private static TreeColorRangeAction textColorRange;

    private static RotationAnimator rotationAnimator;

    public RadialGraphViewRotationTreeLayout(Graph g, String label) {
	super(new Visualization());
	m_label = label;

	// -- set up visualization --
	m_vis.add(tree, g);

	m_vis.setInteractive(treeEdges, null, false);

	// -- set up renderers --
	m_nodeRenderer = new RotationLabelRenderer(m_label);
	m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
	m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
	m_nodeRenderer.setRoundedCorner(8, 8);
	m_edgeRenderer = new EdgeRenderer();

	DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
	rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
	m_vis.setRendererFactory(rf);

	// -- set up processing actions --

	// colors
	ItemAction nodeColor = new NodeColorAction(treeNodes);
	// ItemAction textColor = new TextColorAction(treeNodes);
	// m_vis.putAction("textColor", textColor);

	textColorRange = new TreeColorRangeAction(tree,
		ColorLib.rgb(255, 0, 0), ColorLib.rgb(0, 0, 255));
	m_vis.putAction("textColorRange", textColorRange);

	ItemAction edgeColor = new ColorAction(treeEdges,
		VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));

	FontAction fonts = new FontAction(treeNodes, FontLib.getFont("Tahoma",
		10));
	fonts.add("ingroup('_focus_')", FontLib.getFont("Tahoma", 11));

	// recolor
	ActionList recolor = new ActionList();
	recolor.add(nodeColor);
	m_vis.putAction("recolor", recolor);

	// repaint
	ActionList repaint = new ActionList();
	repaint.add(recolor);
	repaint.add(new RepaintAction());
	m_vis.putAction("repaint", repaint);

	// animate paint change
	ActionList animatePaint = new ActionList(400);
	animatePaint.add(new ColorAnimator(treeNodes));
	animatePaint.add(new RepaintAction());
	m_vis.putAction("animatePaint", animatePaint);

	// create the tree layout action
	treeLayout = new RadialRotationTreeLayout(tree);
	// treeLayout.setAngularBounds(-Math.PI/2, Math.PI);
	m_vis.putAction("treeLayout", treeLayout);

	CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree);
	m_vis.putAction("subLayout", subLayout);

	// create the filtering and layout
	ActionList filter = new ActionList();
	filter.add(new TreeRootAction(tree));
	filter.add(fonts);
	filter.add(treeLayout);
	filter.add(subLayout);
	filter.add(textColorRange);
	filter.add(nodeColor);
	filter.add(edgeColor);
	m_vis.putAction("filter", filter);

	// animated transition
	rotationAnimator = new RotationAnimator(treeNodes);

	ActionList animate = new ActionList(2250);
	animate.setPacingFunction(new SlowInSlowOutPacer());
	animate.add(new QualityControlAnimator());
	animate.add(new VisibilityAnimator(tree));
	animate.add(new PolarLocationAnimator(treeNodes, linear));
	animate.add(new ColorAnimator(treeNodes));
	animate.add(rotationAnimator);
	animate.add(new RepaintAction());
	m_vis.putAction("animate", animate);
	m_vis.alwaysRunAfter("filter", "animate");

	// ------------------------------------------------

	// initialize the display
	setSize(700, 500);
	treeLayout.setRescale(700, 500);

	setHighQuality(true);

	setItemSorter(new TreeDepthItemSorter());
	addControlListener(new DragControl());
	addControlListener(new ZoomToFitControl());
	addControlListener(new ZoomControl());
	addControlListener(new PanControl());
	addControlListener(new FocusControl(1, "filter"));
	addControlListener(new HoverActionControl("repaint"));

	// -------------- MAD
	UpdateListener lstnr = treeLayout.getUpdateListener("filter");
	addComponentListener(lstnr);
	// ------------------------------------------------

	// filter graph and perform layout
	m_vis.run("filter");

	// maintain a set of items that should be interpolated linearly
	// this isn't absolutely necessary, but makes the animations nicer
	// the PolarLocationAnimator should read this set and act accordingly
	m_vis.addFocusGroup(linear, new DefaultTupleSet());
	m_vis.getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
		new TupleSetListener() {
		    public void tupleSetChanged(TupleSet t, Tuple[] add,
			    Tuple[] rem) {
			TupleSet linearInterp = m_vis.getGroup(linear);
			if (add.length < 1) {
			    return;
			}
			linearInterp.clear();
			for (Node n = (Node) add[0]; n != null; n = n
				.getParent()) {
			    linearInterp.addTuple(n);
			}
		    }
		});

	SearchTupleSet search = new PrefixSearchTupleSet();
	m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
	search.addTupleSetListener(new TupleSetListener() {
	    public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
		m_vis.cancel("animatePaint");
		m_vis.run("recolor");
		m_vis.run("animatePaint");
	    }
	});

    }

    // ------------------------------------------------------------------------

    public static void main(String argv[]) {
	String infile = DATA_FILE;
	String label = "name";

	if (argv.length > 1) {
	    infile = argv[0];
	    label = argv[1];
	}

	UILib.setPlatformLookAndFeel();

	JFrame frame = new JFrame(
		"p r o f u s e  |  RadialRotationTreeLayout & RotationAnimator & TreeColorRangeAction");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setContentPane(demo(infile, label));
	frame.pack();
	frame.setVisible(true);
    }

    public static JPanel demo() {
	return demo(DATA_FILE, "name");
    }

    public static JPanel demo(String datafile, final String label) {
	Graph g = null;
	try {
	    g = new GraphMLReader().readGraph(datafile);
	} catch (Exception e) {
	    e.printStackTrace();
	    System.exit(1);
	}
	return demo(g, label);
    }

    public static JPanel demo(Graph g, final String label) {
	// create a new radial tree view
	final RadialGraphViewRotationTreeLayout gview = new RadialGraphViewRotationTreeLayout(
		g, label);
	final Visualization vis = gview.getVisualization();

	// create a search panel for the tree map

	final JRadioButton rootOrientation = new JRadioButton("root node");
	rootOrientation.setSelected(true);
	rootOrientation.setBackground(Color.WHITE);

	final JRadioButton parentOrientation = new JRadioButton("parent node");
	parentOrientation.setSelected(false);
	parentOrientation.setBackground(Color.WHITE);

	final JRadioButton childrenOrientation = new JRadioButton(
		"children nodes");
	childrenOrientation.setSelected(false);
	childrenOrientation.setBackground(Color.WHITE);

	ButtonGroup orientationGroup = new ButtonGroup();
	orientationGroup.add(rootOrientation);
	orientationGroup.add(parentOrientation);
	orientationGroup.add(childrenOrientation);

	rootOrientation.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    treeLayout
			    .setRotationOrientation(RadialRotationTreeLayout.ROOT_ORIENTATION);
		    vis.run("filter");
		}
	    }
	});
	parentOrientation.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    treeLayout
			    .setRotationOrientation(RadialRotationTreeLayout.PARENT_ORIENTATION);
		    vis.run("filter");
		}
	    }
	});
	childrenOrientation.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    treeLayout
			    .setRotationOrientation(RadialRotationTreeLayout.CHILDREN_ORIENTATION);
		    vis.run("filter");
		}
	    }
	});

	Box obox = new Box(BoxLayout.X_AXIS);

	obox.add(Box.createHorizontalStrut(4));
	obox.add(rootOrientation);
	obox.add(parentOrientation);
	obox.add(childrenOrientation);
	obox
		.setBorder(BorderFactory
			.createTitledBorder("rotation orientation"));

	final JRadioButton subtreeSizePolicy = new JRadioButton("subtree size");
	subtreeSizePolicy.setSelected(true);
	subtreeSizePolicy.setBackground(Color.WHITE);

	final JRadioButton equalChildPolicy = new JRadioButton(
		"equal per child");
	equalChildPolicy.setSelected(false);
	equalChildPolicy.setBackground(Color.WHITE);

	ButtonGroup policyGroup = new ButtonGroup();
	policyGroup.add(subtreeSizePolicy);
	policyGroup.add(equalChildPolicy);

	subtreeSizePolicy.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    textColorRange
			    .setColorRangePolicy(TreeColorRangeAction.COLORRANGEPOLICY_EQUALPERLEAVE);
		    vis.run("filter");
		}
	    }
	});

	equalChildPolicy.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    textColorRange
			    .setColorRangePolicy(TreeColorRangeAction.COLORRANGEPOLICY_EQUALPERCHILD);
		    vis.run("filter");
		}
	    }
	});

	Box pbox = new Box(BoxLayout.X_AXIS);

	pbox.add(Box.createHorizontalStrut(4));
	pbox.add(subtreeSizePolicy);
	pbox.add(equalChildPolicy);
	pbox.setBorder(BorderFactory.createTitledBorder("color range policy"));

	final JRadioButton straightRotation = new JRadioButton("straight");
	straightRotation.setSelected(true);
	straightRotation.setBackground(Color.WHITE);

	final JRadioButton loopRotation = new JRadioButton("single loop");
	loopRotation.setSelected(false);
	loopRotation.setBackground(Color.WHITE);

	final JRadioButton doubleloopRotation = new JRadioButton("double loop");
	doubleloopRotation.setSelected(false);
	doubleloopRotation.setBackground(Color.WHITE);

	final JRadioButton tripleloopRotation = new JRadioButton("triple loop");
	tripleloopRotation.setSelected(false);
	tripleloopRotation.setBackground(Color.WHITE);

	ButtonGroup rotationGroup = new ButtonGroup();
	rotationGroup.add(straightRotation);
	rotationGroup.add(loopRotation);
	rotationGroup.add(doubleloopRotation);
	rotationGroup.add(tripleloopRotation);

	straightRotation.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    rotationAnimator
			    .setRotationStyle(RotationAnimator.STRAIGHT_ROTATION);
		}
	    }
	});

	loopRotation.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    rotationAnimator
			    .setRotationStyle(RotationAnimator.SINGLELOOP_ROTATION);
		}
	    }
	});

	doubleloopRotation.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    rotationAnimator
			    .setRotationStyle(RotationAnimator.DOUBLELOOP_ROTATION);
		}
	    }
	});
	tripleloopRotation.addItemListener(new ItemListener() {
	    public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
		    rotationAnimator
			    .setRotationStyle(RotationAnimator.TRIPLELOOP_ROTATION);
		}
	    }
	});

	Box rbox = new Box(BoxLayout.X_AXIS);

	rbox.add(Box.createHorizontalStrut(4));
	rbox.add(straightRotation);
	rbox.add(loopRotation);
	rbox.add(doubleloopRotation);
	rbox.add(tripleloopRotation);
	rbox.setBorder(BorderFactory
		.createTitledBorder("rotation animation style"));

	final JValueSlider rotationScale = new JValueSlider("", 0., 1., 1.);
	rotationScale.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
		treeLayout.setLabelRotationScale(rotationScale.getValue()
			.doubleValue());
		vis.run("filter");
	    }
	});
	rotationScale.setBackground(Color.WHITE);
	rotationScale.setMaximumSize(new Dimension(277, 60));
	rotationScale.setBorder(BorderFactory
		.createTitledBorder("rotation scale factor"));

	Box hbox = new Box(BoxLayout.X_AXIS);
	hbox.add(obox);
	hbox.add(rbox);

	Box hbox2 = new Box(BoxLayout.X_AXIS);
	hbox2.add(pbox);
	hbox2.add(rotationScale);

	Box vbox = new Box(BoxLayout.Y_AXIS);
	vbox.add(hbox2);
	vbox.add(hbox);

	Box nbox = new Box(BoxLayout.X_AXIS);
	nbox.add(Box.createHorizontalStrut(10));
	nbox.add(vbox);
	nbox.add(Box.createHorizontalStrut(3));

	final JFastLabel stitle = new JFastLabel("                 ");
	stitle.setPreferredSize(new Dimension(350, 20));
	stitle.setVerticalAlignment(SwingConstants.BOTTOM);
	stitle.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
	stitle.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));

	gview.addControlListener(new ControlAdapter() {
	    public void itemEntered(VisualItem item, MouseEvent e) {
		if (item.canGetString(label)) {
		    stitle.setText(item.getString(label));
		}
	    }

	    public void itemExited(VisualItem item, MouseEvent e) {
		stitle.setText(null);
	    }
	});

	SearchQueryBinding sq = new SearchQueryBinding((Table) vis
		.getGroup(treeNodes), label, (SearchTupleSet) vis
		.getGroup(Visualization.SEARCH_ITEMS));
	JSearchPanel search = sq.createSearchPanel();
	search.setShowResultCount(true);
	search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
	search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

	Box sbox = new Box(BoxLayout.X_AXIS);
	sbox.add(Box.createHorizontalStrut(10));
	sbox.add(stitle);
	sbox.add(Box.createHorizontalGlue());
	sbox.add(search);
	sbox.add(Box.createHorizontalStrut(3));

	JPanel panel = new JPanel(new BorderLayout());
	panel.add(nbox, BorderLayout.NORTH);
	panel.add(gview, BorderLayout.CENTER);
	panel.add(sbox, BorderLayout.SOUTH);

	Color BACKGROUND = Color.WHITE;
	Color FOREGROUND = Color.DARK_GRAY;
	UILib.setColor(panel, BACKGROUND, FOREGROUND);

	return panel;
    }

    // ------------------------------------------------------------------------

    /**
         * Switch the root of the tree by requesting a new spanning tree at the
         * desired root
         */
    public static class TreeRootAction extends GroupAction {
	public TreeRootAction(String graphGroup) {
	    super(graphGroup);
	}

	public void run(double frac) {
	    TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
	    if ((focus == null) || (focus.getTupleCount() == 0)) {
		return;
	    }

	    Graph g = (Graph) m_vis.getGroup(m_group);
	    Node f = null;
	    Iterator tuples = focus.tuples();
	    while (tuples.hasNext()
		    && !g.containsTuple(f = (Node) tuples.next())) {
		f = null;
	    }
	    if (f == null) {
		return;
	    }
	    g.getSpanningTree(f);
	}
    }

    /**
         * Set node fill colors
         */
    public static class NodeColorAction extends ColorAction {
	public NodeColorAction(String group) {
	    super(group, VisualItem.FILLCOLOR, ColorLib.rgba(255, 255, 255, 0));
	    add("_hover", ColorLib.gray(220, 230));
	    add("ingroup('_search_')", ColorLib.rgb(255, 190, 190));
	    add("ingroup('_focus_')", ColorLib.rgb(198, 229, 229));
	}

    } // end of inner class NodeColorAction

    /**
         * Set node text colors
         */
    public static class TextColorAction extends ColorAction {
	public TextColorAction(String group) {
	    super(group, VisualItem.TEXTCOLOR, ColorLib.gray(0));
	    add("_hover", ColorLib.rgb(255, 0, 0));
	}
    }

}
