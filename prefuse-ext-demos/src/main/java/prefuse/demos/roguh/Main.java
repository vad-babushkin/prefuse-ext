// Prefuse demos:
// Expand tree to n- depth:
// http://sourceforge.net/p/prefuse/discussion/343013/thread/0b03b0e4/

// How to expand tree to arbitrary node distance?
// http://sourceforge.net/p/prefuse/discussion/343013/thread/0b03b0e4/
// Answered by 
// shapes
// on 2011-04-10
// Hi you can do this in 6 steps...

// http://stackoverflow.com/questions/1978933/a-quick-and-easy-way-to-join-array-elements-with-a-separator-the-opposite-of-sp
// By Zedas
// answered Mar 8 '13 at 11:44

// Orientation change code is broken. 
// Doesn't matter if node names are long

// Hugo Rivera, February 2015:
// Also display nodes sharing extra information, if available. 
// Extra information = "::" separated list of locations in an attribute "institutions."

package prefuse.demos.roguh;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.assignment.SizeAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.TreeMLReader;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * Demonstration of a node-link tree viewer
 *
 * @version 1.0
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class Main extends Display {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TREE_CHI = "data/roguh/chi-ontology.xml";

	private static final String tree = "tree";
	private static final String treeNodes = "tree.nodes";
	private static final String treeEdges = "tree.edges";

	private LabelRenderer m_nodeRenderer;
	private EdgeRenderer m_edgeRenderer;

	public static final int SEARCHEDCOLOR = ColorLib.hex("#b2182b");
	public static final int SHAREDATTRCOLOR = ColorLib.hex("#2166ac");
	public static final int FOCUSCOLOR = ColorLib.hex("#d1e5f0");
	public static final int NEARBYCOLOR = ColorLib.hex("#fddbc7");

	public static final Color BACKGROUND = Color.WHITE;
	public static final Color FOREGROUND = Color.BLACK;

	private String m_label = "label";
	private int m_orientation = Constants.ORIENT_LEFT_RIGHT;

	public static String extraInfoLabel;

	public static String extraInfoDelimiter;

	private static VisualItem itemHovered = null;
	private static String[] itemHoveredAttrs = null;

	private static FisheyeTreeFilter fisheyeTreeFilter;
	
	public Main(Tree t, String label, String extraInfoLabel, String extraInfoDelimiter) {
		super(new Visualization());
		
		m_label = label;
		Main.extraInfoDelimiter = extraInfoDelimiter;
		Main.extraInfoLabel = extraInfoLabel;

		m_vis.add(tree, t);

		m_nodeRenderer = new LabelRenderer(m_label);
		m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
		m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
		m_nodeRenderer.setRoundedCorner(8, 8);
		m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

		DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
		rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
		m_vis.setRendererFactory(rf);

		// colors
		ItemAction nodeColor = new NodeColorAction(treeNodes, VisualItem.FILLCOLOR);
		ItemAction nodeStroke = new NodeStrokeAction(treeNodes);
		ItemAction textColor = new NodeColorAction(treeNodes, VisualItem.TEXTCOLOR);
		m_vis.putAction("textColor", textColor);
		
        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(200,200,200));

		// quick repaint
		ActionList repaint = new ActionList();
		repaint.add(nodeColor);
		repaint.add(nodeStroke);
		repaint.add(new RepaintAction());
		m_vis.putAction("repaint", repaint);

		// full paint
		ActionList fullPaint = new ActionList();
		fullPaint.add(nodeColor);
		fullPaint.add(nodeStroke);
		m_vis.putAction("fullPaint", fullPaint);

		// animate paint change
		ActionList animatePaint = new ActionList(400);
		animatePaint.add(new ColorAnimator(treeNodes));
		animatePaint.add(new RepaintAction());
		m_vis.putAction("animatePaint", animatePaint);

		// create the tree layout action
		NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
				m_orientation, 50, 0, 8);
		treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
		m_vis.putAction("treeLayout", treeLayout);

		CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree,
				m_orientation);
		m_vis.putAction("subLayout", subLayout);

		fisheyeTreeFilter = new FisheyeTreeFilter(tree, 3);

		AutoPanAction autoPan = new AutoPanAction();

		// create the filtering and layout
		ActionList filter = new ActionList();
		filter.add(fisheyeTreeFilter);
		// filter.add(new FisheyeTreeFilter(tree, 2));
		filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 16)));
		filter.add(treeLayout);
		filter.add(subLayout);
		filter.add(textColor);
		filter.add(nodeColor);
		filter.add(nodeStroke);
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
		setSize(700, 600);
		setItemSorter(new TreeDepthItemSorter());
		addControlListener(new ZoomToFitControl());
		addControlListener(new ZoomControl());
		addControlListener(new WheelZoomControl());
		addControlListener(new PanControl());
		addControlListener(new FocusControl(1, "filter"));

		registerKeyboardAction(new OrientAction(Constants.ORIENT_LEFT_RIGHT),
				"left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_TOP_BOTTOM),
				"top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_RIGHT_LEFT),
				"right-to-left", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_BOTTOM_TOP),
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
	}

	public void AutoSetFilterLevel(Integer distance) {
		fisheyeTreeFilter.setDistance(distance); // Change to display more data

		m_vis.cancel("animatePaint");
		m_vis.run("fullPaint");
		m_vis.run("animatePaint");
		m_vis.run("filter");
	}

	// ------------------------------------------------------------------------

	public void setOrientation(int orientation) {
		NodeLinkTreeLayout rtl = (NodeLinkTreeLayout) m_vis
				.getAction("treeLayout");
		CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout) m_vis
				.getAction("subLayout");
		switch (orientation) {
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
			m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER)
			// ------------------------------------------------
;
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
					"Unrecognized orientation value: " + orientation);
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
		// String docfile = TREE_CHI;
		String docfile = "data/roguh/Gauss.xml";
		String label = "name";
		String extraInfoLabel = "institutions";
		String extraInfoDelimiter = "::";
		if (argv.length == 1) {
			docfile = argv[0];
		} else {
			System.out.println("Pass a TreeML file as an argument to visualize it.\n"
							+ "Includes datasets:\n"
							+ "data/roguh/Gauss.xml data/roguh/Laplace.xml data/roguh/Zygmund.xml data/roguh/chi-ontology.xml\n\n");
		}
		System.out.println("Using TreeML file: " + docfile);
		
		JComponent treeview = demo(docfile, label, extraInfoLabel, extraInfoDelimiter);

		JFrame frame = new JFrame("p r e f u s e  |  t r e e v i e w");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(treeview);
		frame.pack();
		frame.setVisible(true);
	}

	public static JComponent demo() {
		return demo(TREE_CHI, "name", "", "");
	}

	public static JComponent demo(String docfile, final String label, final String extraInfoLabel, final String extraInfoDelimiter) {

		Tree docs = null;
		try {
			docs = (Tree) new TreeMLReader().readGraph(docfile);

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		final Main tview = new Main(docs, label, extraInfoLabel, extraInfoDelimiter);
		tview.setBackground(BACKGROUND);
		tview.setForeground(FOREGROUND);

		// TODO make this a smarter number
		int maxNodeDistance = 20;

		// create a distance number spinner
		final SpinnerNumberModel spinner = new SpinnerNumberModel(3, 0,
				maxNodeDistance, 1);
		final JSpinner setFilterLevel = new JSpinner(spinner);
		applyTheme(setFilterLevel);
		
		setFilterLevel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tview.AutoSetFilterLevel((Integer) spinner.getValue());
			}
		});

		// create a search panel for the tree map
		JSearchPanel search = new JSearchPanel(tview.getVisualization(),
				treeNodes, Visualization.SEARCH_ITEMS, label, true, true);
		search.setShowResultCount(true);
		search.setLabelText("Word-prefix search:");

		final JTextArea title = new JTextArea(
				"Hover and click for more information about nodes.", 3, 1);
		title.setWrapStyleWord(true);
		title.setLineWrap(true);
		title.setEditable(false);
		applyTheme(title);

		final JFastLabel instructions = new JFastLabel(
				"Mouse to navigate.   Select node distance:");
		applyTheme(instructions);

		tview.addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {

				if (item.canGetString(label)
						&& item.canGetString(extraInfoLabel)) {
					String instis = strJoin(item.getString(extraInfoLabel)
							.split(extraInfoDelimiter), ", ");
					title.setText(item.getString(label) + ". "
							+ instis);
				}

				else if (item.canGetString(label))
					title.setText(item.getString(label));
				itemHovered = item;
				if (itemHovered.canGet(extraInfoLabel, String.class))
					itemHoveredAttrs = ((String) itemHovered
							.get(extraInfoLabel)).split(extraInfoDelimiter);
			}

			public void itemExited(VisualItem item, MouseEvent e) {
				title.setText(null);
				itemHovered = null;
				itemHoveredAttrs = null;
			}
		});

		Box hbox = new Box(BoxLayout.X_AXIS);
		hbox.add(Box.createHorizontalStrut(5));
		hbox.add(instructions);
		hbox.add(Box.createHorizontalStrut(5));
		hbox.add(setFilterLevel);
		hbox.add(Box.createHorizontalGlue());
		hbox.add(search);
		hbox.add(Box.createHorizontalStrut(5));
		hbox.setBackground(BACKGROUND);

		
		JFastLabel sharedattrColor = new JFastLabel(" Nodes sharing institutions ");
		applyTheme(sharedattrColor);
		sharedattrColor.setBackground(ColorLib.getColor(SHAREDATTRCOLOR));
		sharedattrColor.setForeground(BACKGROUND);

		JFastLabel searchedColor = new JFastLabel(" Nodes satisfying search ");
		applyTheme(searchedColor);
		searchedColor.setBackground(ColorLib.getColor(SEARCHEDCOLOR));
		searchedColor.setForeground(BACKGROUND);
		
		JFastLabel focusColor = new JFastLabel(" Focused node ");
		applyTheme(focusColor);
		focusColor.setBackground(ColorLib.getColor(FOCUSCOLOR));
		
		JFastLabel nearbyColor = new JFastLabel(" Nodes in lineage ");
		applyTheme(nearbyColor);
		nearbyColor.setBackground(ColorLib.getColor(NEARBYCOLOR));
		
		Box colorcodes = new Box(BoxLayout.X_AXIS);
		colorcodes.add(sharedattrColor);
		colorcodes.add(searchedColor);
		colorcodes.add(focusColor);
		colorcodes.add(nearbyColor);
		
		Box infobox = new Box(BoxLayout.Y_AXIS);
		infobox.add(hbox);
		infobox.add(colorcodes);
		
		Box titlebox = new Box(BoxLayout.Y_AXIS);
		titlebox.add(title);

		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(BACKGROUND);
		panel.setForeground(FOREGROUND);
		panel.add(tview, BorderLayout.CENTER);
		panel.add(infobox, BorderLayout.SOUTH);
		panel.add(titlebox, BorderLayout.NORTH);
		return panel;
	}

	private static void applyTheme(JComponent comp) {
		comp.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
		comp.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 14));
		comp.setBackground(BACKGROUND);
		comp.setForeground(FOREGROUND);
		comp.setPreferredSize(new Dimension(400, 30));
	}
	
	// ------------------------------------------------------------------------

	public class OrientAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
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
		private Point2D m_end = new Point2D.Double();
		private Point2D m_cur = new Point2D.Double();
		private int m_bias = 150;

		public void run(double frac) {
			TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
			if (ts.getTupleCount() == 0)
				return;

			if (frac == 0.0) {
				int xbias = 0, ybias = 0;
				switch (m_orientation) {
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

				VisualItem vi = (VisualItem) ts.tuples().next();
				m_cur.setLocation(getWidth() / 2, getHeight() / 2);
				getAbsoluteCoordinate(m_cur, m_start);
				m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
			} else {
				m_cur.setLocation(m_start.getX() + frac
						* (m_end.getX() - m_start.getX()), m_start.getY()
						+ frac * (m_end.getY() - m_start.getY()));
				panToAbs(m_cur);
			}
		}
	}

	public static class NodeColorAction extends ColorAction {
		String colorType;
		public NodeColorAction(String group, String colorType) {
			super(group, colorType);
			this.colorType = colorType;
		}

		public int getColor(VisualItem item) {
			Boolean isText = colorType.equals(VisualItem.TEXTCOLOR);
			if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS))
				return isText ? ColorLib.gray(0) : FOCUSCOLOR; 
			else if (itemSharesAttrs(item, itemHoveredAttrs)) 
				return isText ? ColorLib.gray(255) : SHAREDATTRCOLOR; 
			else if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS))
				return isText ? ColorLib.gray(255) : SEARCHEDCOLOR; 
			else if (item.getDOI() > -1)
				return isText ? ColorLib.gray(0) : NEARBYCOLOR; 
			else
				return isText ? ColorLib.gray(0) : ColorLib.rgba(255, 255, 255, 0);
		}

	} // end of inner class TreeMapColorAction

	public static class NodeStrokeAction extends SizeAction {

		public NodeStrokeAction(String group) {
			super(group);
		}

		public double getSize(VisualItem item) {
			if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS))
				return 1.3;
			else if (itemSharesAttrs(item, itemHoveredAttrs)) {
				return 1.5;
			} else if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS))
				return 1.5;
			else
				return getDefaultSize();
		}

	}

	public static boolean itemSharesAttrs(VisualItem item, String[] otherAttrs) {
		if (item.canGetString(extraInfoLabel) && otherAttrs != null) {
			String[] instis = ((String) item.get(extraInfoLabel)).split(extraInfoDelimiter);

			// SPECIAL KEYWORD
			// Prevent noise
			if (otherAttrs[0].equals("none"))
				return false;
			
			for (int i = otherAttrs.length - 1; i >= 0; i--) {
				if (Arrays.asList(instis).contains(otherAttrs[i])) 
					return true;
			}
			
		}
		return false;
	}
	
	// http://stackoverflow.com/questions/1978933/a-quick-and-easy-way-to-join-array-elements-with-a-separator-the-opposite-of-sp
	// By Zedas
	// answered Mar 8 '13 at 11:44

	public static String strJoin(String[] aArr, String sSep) {
		StringBuilder sbStr = new StringBuilder();
		for (int i = 0, il = aArr.length; i < il; i++) {
			if (i > 0)
				sbStr.append(sSep);
			sbStr.append(aArr[i]);
		}
		return sbStr.toString();
	}

} // end of class TreeMap
