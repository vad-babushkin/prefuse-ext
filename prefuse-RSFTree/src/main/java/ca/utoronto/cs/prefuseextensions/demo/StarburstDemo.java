package ca.utoronto.cs.prefuseextensions.demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.HoverActionControl;
import prefuse.controls.PanControl;
import prefuse.controls.RotationControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.OrPredicate;
import prefuse.data.io.GraphMLReader;
import prefuse.data.io.TreeMLReader;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.CompositeTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.expression.StartVisiblePredicate;
import prefuse.visual.expression.VisiblePredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import ca.utoronto.cs.prefuseextensions.control.MouseWheelControl;
import ca.utoronto.cs.prefuseextensions.layout.StarburstLayout;
import ca.utoronto.cs.prefuseextensions.render.ArcLabelRenderer;
import ca.utoronto.cs.prefuseextensions.render.DecoratorLabelRenderer;
import ca.utoronto.cs.prefuseextensions.render.SectorRenderer;

/**
 * Demonstration of a radial space-filling tree viewer.
 *
 * Generalized fisheye filter applied, revealing only nodes of depth 1 from focus (root), selected nodes (gold), and search results (orange).
 * Select nodes with a single click.  Select multiple with ctrl-click.  Deselect with ctrl-click on a selected node.
 * Recenter graph by double clicking any node; new spanning tree is calculated and the graph will be shown centered at that 
 * node.
 * 
 * There are some strange rendering effects, but these are documented Java2D bugs with Arc2D rendering (see notes in Sector2D class).
 * 
 * @version 1.1
 * @author Christopher Collins through modification of original code by jeffrey heer
 */
public class StarburstDemo extends Display {

	/////////////////////////////
	///// DATASETS (select one) 
	/////////////////////////////
	
	// SMALL DATASET:
	//public static final String DATA_FILE = "/socialnet.xml";

	// LARGE DATASET:
	public static final String DATA_FILE= "data/chi-ontology.xml.gz";
	
	//////////////////////////////
	///// PRIVATE DATA
	//////////////////////////////
	
	private static final String tree = "tree";
	private static final String treeNodes = "tree.nodes";
	private static final String treeEdges = "tree.edges";
	private static final String labels = "labels";
	
	private String m_label;
	private Action resizeAction;
	private FisheyeTreeFilter fisheyeTreeFilter; 
	
	////////////////////////////
	////// CONSTRUCTOR
	////////////////////////////
	
	public StarburstDemo(Graph g, String label) {
		super(new Visualization());
		m_label = label;

		// -- set up visualization --
		m_vis.add(tree, g);
		m_vis.setVisible(treeEdges, null, false);
	
		// -- set up renderers --
		DefaultRendererFactory rf = createRenderers();
		m_vis.setRendererFactory(rf);

		// -- set up processing actions --

		// create the tree layout action; adds layout schema to nodes
		StarburstLayout treeLayout = new StarburstLayout(tree);
		// set location and turn off autoscale so graph layout doesn't revert to original view when mouse wheel is rolled
		treeLayout.setAutoScale(false);
		treeLayout.setLayoutAnchor(new Point2D.Double());
		// Uncomment next line to restrict graph to semi-circle
		//treeLayout.setAngularBounds(-Math.PI/2, Math.PI);  // TODO add widget to interactively adjust this
		m_vis.putAction("treeLayout", treeLayout);

		// add decorators (has to be after layout because decorators rendered rely on Schema provided by StarburstLayout
		m_vis.addDecorators(labels, treeNodes, new OrPredicate(new VisiblePredicate(), new StartVisiblePredicate()), ArcLabelRenderer.LABEL_SCHEMA);
		
		// fonts and colors for decorator items (labels)
		FontAction fonts = new StarburstLayout.ScaleFontAction(labels, m_label);
		ItemAction textColor = new TextColorAction(labels);

		CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree);
		m_vis.putAction("subLayout", subLayout);

		// define focus groups
		TupleSet selected = m_vis.getFocusGroup(Visualization.SELECTED_ITEMS);
		SearchTupleSet search = new PrefixSearchTupleSet();
		m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
		
		// filter the tree to 2 levels from selected items and search results
		CompositeTupleSet searchAndSelect = new CompositeTupleSet();
		searchAndSelect.addSet(Visualization.SELECTED_ITEMS, m_vis
				.getFocusGroup(Visualization.SELECTED_ITEMS));
		searchAndSelect.addSet(Visualization.SEARCH_ITEMS, m_vis
				.getFocusGroup(Visualization.SEARCH_ITEMS));
		m_vis.addFocusGroup("searchAndSelect", searchAndSelect);
		fisheyeTreeFilter = new FisheyeTreeFilter(tree, "searchAndSelect", 1);

		// colors
		ItemAction nodeColor = new NodeColorAction(treeNodes);
		ColorAction nodeStrokeColor = new ColorAction(treeNodes, VisualItem.STROKECOLOR) {
			public int getColor(VisualItem item) {
				return ColorLib.darker(item.getFillColor());
			}
		};

		// recolor
		ActionList recolor = new ActionList();
		recolor.add(nodeColor);
		recolor.add(nodeStrokeColor);
		recolor.add(textColor);
		m_vis.putAction("recolor", recolor);

		// animate paint change
		ActionList animatePaint = new ActionList(400);
		animatePaint.add(new ColorAnimator(treeNodes));
		animatePaint.add(new RepaintAction());
		m_vis.putAction("animatePaint", animatePaint);
		
		// recentre and rezoom on reload
		resizeAction = new Action() {
			public void run(double frac) {
				// animate reset zoom to fit the data (must run only AFTER layout)
				Rectangle2D bounds = m_vis.getBounds(tree);
				
	            if (bounds.getWidth() == 0) return;
				GraphicsLib.expand(bounds, 10+(int)(1/m_vis.getDisplay(0).getScale()));
	            DisplayLib.fitViewToBounds(m_vis.getDisplay(0), bounds, (long) 1250);
			}
		};
		m_vis.putAction("resize", resizeAction);
		
		// create the filtering and layout
		ActionList filter = new ActionList();
		filter.add(fisheyeTreeFilter);
		filter.add(new TreeRootAction(tree));
		filter.add(treeLayout);
		filter.add(new StarburstLayout.LabelLayout(labels));
		filter.add(fonts);
		filter.add(subLayout);
		filter.add(textColor);
		filter.add(nodeColor);
		filter.add(nodeStrokeColor);
		m_vis.putAction("filter", filter);

		// animated transition
		final ActionList animate = new ActionList(1250);
		animate.setPacingFunction(new SlowInSlowOutPacer());
		animate.add(new QualityControlAnimator());
		animate.add(new VisibilityAnimator(tree));
		animate.add(new ColorAnimator(treeNodes));
		animate.add(new VisibilityAnimator(labels));
		animate.add(new ColorAnimator(labels));
		animate.add(new RepaintAction());
		m_vis.putAction("animate", animate);
		m_vis.alwaysRunAfter("filter", "animate");
		m_vis.alwaysRunAfter("animate", "resize");
	
		// repaint
		ActionList repaint = new ActionList() {
			public void run(double frac) {
				// only repaint if animation is not already running; otherwise we get flicker if repaint
				// is called from HoverActionControl while a visibility animation is running
				if (!animate.isRunning()) {
					super.run(frac);
				}
			}
		};
		repaint.add(recolor);
		repaint.add(new RepaintAction());
		m_vis.putAction("repaint", repaint);
			
		// ------------------------------------------------

		// initialize the display
		setSize(600, 600);
		setItemSorter(new TreeDepthItemSorter());
		addControlListener(new ZoomToFitControl());
		addControlListener(new ZoomControl());
		addControlListener(new PanControl(false));
		addControlListener(new FocusControl(2, "filter"));
		addControlListener(new FocusControl(Visualization.SELECTED_ITEMS, 1, "filter"));
		addControlListener(new HoverActionControl("repaint"));
		addControlListener(new MouseWheelControl("filter", "angleFactor"));

		// ------------------------------------------------

		// filter graph and perform layout
		m_vis.run("filter");
		
		// maintain a set of items that should be interpolated linearly
		// this isn't absolutely necessary, but makes the animations nicer
		// the PolarLocationAnimator should read this set and act accordingly
		
		selected.addTupleSetListener(new TupleSetListener() {
			public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
				m_vis.cancel("animate");
				m_vis.run("filter");
			}
		});
		
		search.addTupleSetListener(new TupleSetListener() {
			public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
				m_vis.cancel("animate");
				m_vis.run("filter");
			}
		});
	}
	
	public void setAutoResize(boolean autoResize) {
		resizeAction.setEnabled(autoResize);
	}
	
	public void setFilterLevel(int level) {
		fisheyeTreeFilter.setDistance(level);
		m_vis.run("filter");
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

		JFrame frame = new JFrame("Starburst Demo for prefuse");
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
		// Can accept either a graph or a tree; try reading a graph first, if it fails, try tree
		try {
			g = new GraphMLReader().readGraph(datafile);
		} catch (Exception e1) {
			try { 
				g = new TreeMLReader().readGraph(datafile);
			} catch (Exception e2) {
				e2.printStackTrace();
				System.exit(1);
			}
		}
		return demo(g, label);
	}

	public static JPanel demo(Graph g, final String label) {
		// create a new radial tree view
		final StarburstDemo gview = new StarburstDemo(g, label);
		final Visualization vis = gview.getVisualization();
		
		// create a search panel for the tree map
		SearchQueryBinding sq = new SearchQueryBinding((Table) vis
				.getGroup(treeNodes), label, (SearchTupleSet) vis
				.getGroup(Visualization.SEARCH_ITEMS));
		JSearchPanel search = sq.createSearchPanel();
		search.setShowResultCount(true);
		search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
		search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

		final JFastLabel title = new JFastLabel("                 ");
		title.setPreferredSize(new Dimension(350, 20));
		title.setVerticalAlignment(SwingConstants.BOTTOM);
		title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));

		gview.addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
				if (item.canGetString(label))
					title.setText(item.getString(label));
			}

			public void itemExited(VisualItem item, MouseEvent e) {
				title.setText(null);
			}
		});

		JCheckBox resizeCheckBox = new JCheckBox("auto zoom");
		resizeCheckBox.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
		resizeCheckBox.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 12));
		resizeCheckBox.setSelected(true);
		resizeCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gview.setAutoResize(((JCheckBox)e.getSource()).isSelected());
			}
		});
		
		JValueSlider filterLevelSlider = new JValueSlider("Filter level", 1, 20, 1);
		filterLevelSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				gview.setFilterLevel(((JValueSlider)e.getSource()).getValue().intValue());
			}
		});
		
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(Box.createHorizontalStrut(10));
		box.add(title);
		box.add(Box.createHorizontalGlue());
		box.add(resizeCheckBox);
		box.add(Box.createHorizontalStrut(30));
		box.add(filterLevelSlider);
		box.add(Box.createHorizontalStrut(30));
		box.add(search);
		box.add(Box.createHorizontalStrut(3));

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(gview, BorderLayout.CENTER);
		panel.add(box, BorderLayout.SOUTH);

		Color BACKGROUND = Color.WHITE;
		Color FOREGROUND = Color.DARK_GRAY;
		UILib.setColor(panel, BACKGROUND, FOREGROUND);
		
		return panel;
	}

	// ------------------------------------------------------------------------

	/**
	 * Switch the root of the tree by requesting a new spanning tree
	 * at the desired root and hiding all nodes above
	 */
	public static class TreeRootAction extends GroupAction {
		public TreeRootAction(String graphGroup) {
			super(graphGroup);
		}

		public void run(double frac) {
			TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
			if (focus == null || focus.getTupleCount() == 0)
				return;

			Graph g = (Graph) m_vis.getGroup(m_group);
			Node f = null;
			Iterator tuples = focus.tuples();
			while (tuples.hasNext()
					&& !g.containsTuple(f = (Node) tuples.next())) {
				f = null;
			}
			if (f == null)
				return;
			g.getSpanningTree(f);
		}
	}

	/**
	 * Set node fill colors
	 */
	public static class NodeColorAction extends ColorAction {
		public NodeColorAction(String group) {
			super(group, VisualItem.FILLCOLOR,  ColorLib.rgb(61, 130, 246));
			add("_hover and ingroup('"+Visualization.SELECTED_ITEMS+"')",ColorLib.brighter(ColorLib.rgb(0,190,204)));
			// search results
			add("_hover and ingroup('_search_')", ColorLib.brighter(ColorLib.rgb(152, 255, 92)));
			add("_hover", ColorLib.brighter( ColorLib.rgb(61, 130, 246)));
			// selected subtrees
			add("ingroup('"+Visualization.SELECTED_ITEMS+"')",ColorLib.rgb(0,190,204));
			// search results
			add("ingroup('_search_')", ColorLib.rgb(152, 255, 92));
			// the root
			//add("ingroup('_focus_')", ColorLib.rgb(198, 229, 229));
		}
	} // end of inner class NodeColorAction

	/**
	 * Set node text colors
	 */
	public static class TextColorAction extends ColorAction {
		public TextColorAction(String group) {
			super(group, VisualItem.TEXTCOLOR, ColorLib.gray(20));
			add("_hover", ColorLib.rgb(255, 0, 0));
		}
	} // end of inner class TextColorAction

	private DefaultRendererFactory createRenderers() {
		DefaultRendererFactory rf = new DefaultRendererFactory();
		
		//renderer to draw shapes for filled nodes
		SectorRenderer sectorRenderer = new SectorRenderer();
		sectorRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_DRAW_AND_FILL);

		// for angular rotating of non-curved labels 
		DecoratorLabelRenderer decoratorLabelRenderer = new DecoratorLabelRenderer(m_label, false, 2);
		// decoratorLabelRenderer.setHorizontalAlignment(Constants.LEFT);

		// for arching of labels within node
		ArcLabelRenderer arcLabelRenderer = new ArcLabelRenderer(m_label, 2, 30);

		// set up RendererFactory
		rf.add("ingroup('labels') and rotation == 0", arcLabelRenderer); // all sector labels that are not rotated
		rf.add("ingroup('labels') and rotation != 0", decoratorLabelRenderer); // all rotated sector labels
		rf.add(new InGroupPredicate(treeEdges), null);
		rf.setDefaultRenderer(sectorRenderer); // filled sectors
		return rf;
	}

} // end of class StarburstDemo
