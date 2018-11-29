package profusians.demos.treefuse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
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
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import profusians.controls.GenericToolTipControl;
import profusians.render.LimitedLabelRenderer;
import profusians.util.TreeLib;

public class Treefuse extends Display implements KeyListener {

    private static final long serialVersionUID = 1L;

    private boolean circularSiblingVisit = false;

    private static Color FOREGROUND = ColorLib.getColor(0, 0, 100);

    private static Color BACKGROUND = ColorLib.getColor(255, 255, 255);

    private static int treeWidth = 600;

    private static int treeHeight = 400;

    private static int textAreaRow = 12;

    private static int textAreaCol = 60;

    private static String tree = "tree";

    private static String treeNodes = "tree.nodes";

    private static String treeEdges = "tree.edges";

    private LabelRenderer m_nodeRenderer;

    private EdgeRenderer m_edgeRenderer;

    private String m_label;

    private ColorAction textColor;

    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;

    private boolean newModification = false;

    private String[] toolTipFields = { "title", "text" };

    private String[] toolTipText = { "node title", "node text" };

    private Tree data;

    private boolean editing = false;

    private boolean textEditing = false;

    private boolean readOnly = false;

    private String originalText = "";

    private static JTextArea textArea;

    private static final String newNodeText = "press space to edit";

    private static LocalTrace localTrace;

    private static TreeMemory treeMem;

    private final static String readmeFile = "data/profusians/readme.treeml";

    private static String treefuseFile = "data/profusians/treefuse.treeml";

    private static String lastUsedFile;

    private static FileManager fileManager = new FileManager();

    private static TreeHistory history = new TreeHistory();

    private static ZoomControl zoomControl;

    private static ZoomToFitControl zoomToFitControl;

    private static PanControl panControl;

    private static FocusControl focusControl;

    private static AutoPanAction autoPan;

    private static TreefuseHelper telper;

    public Treefuse(Tree t, String label) {
	super(new Visualization());

	m_label = label;

	m_vis.add(tree, t);
	data = t;

	telper = new TreefuseHelper(data, m_vis, new HashMap());

	localTrace = new LocalTrace();

	treeMem = new TreeMemory();

	m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(
		(Tuple) m_vis.items(treeNodes).next());

	TreeLib.setSubTreeNodesPosition((NodeItem) m_vis.getVisualItem(
		treeNodes, data.getRoot()), 0, getHeight() / 2);

	m_nodeRenderer = new LimitedLabelRenderer(m_label, 20);
	m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
	m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
	m_nodeRenderer.setRoundedCorner(8, 8);
	m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

	DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
	rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
	m_vis.setRendererFactory(rf);

	// colors
	ItemAction nodeColor = new NodeColorAction(treeNodes);

	textColor = new ColorAction(treeNodes, VisualItem.TEXTCOLOR, ColorLib
		.color(FOREGROUND));
	m_vis.putAction("textColor", textColor);

	ItemAction edgeColor = new ColorAction(treeEdges,
		VisualItem.STROKECOLOR, ColorLib.rgb(200, 200, 200));

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
	NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
		m_orientation, 50, 0, 8);
	treeLayout.setLayoutAnchor(new Point2D.Double(25, treeHeight / 2));
	m_vis.putAction("treeLayout", treeLayout);

	CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree,
		m_orientation);
	m_vis.putAction("subLayout", subLayout);

	autoPan = new AutoPanAction();

	// create the filtering and layout

	ActionList filter = new ActionList();
	filter.add(new FisheyeTreeFilter(tree, 2));
	filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma",
		Font.PLAIN, 16)));
	filter.add(treeLayout);
	filter.add(subLayout);
	filter.add(textColor);
	filter.add(nodeColor);
	filter.add(edgeColor);
	m_vis.putAction("filter", filter);

	ActionList fadingAnimation = new ActionList(444);
	fadingAnimation.add(textColor);
	fadingAnimation.add(new ColorAnimator(treeNodes));
	fadingAnimation.add(new RepaintAction());

	m_vis.putAction("treeFading", fadingAnimation);

	// animated transition
	ActionList animateBase = new ActionList();
	animateBase.setPacingFunction(new SlowInSlowOutPacer());
	animateBase.add(autoPan);
	animateBase.add(new QualityControlAnimator());
	animateBase.add(new VisibilityAnimator(tree));
	animateBase.add(new LocationAnimator(treeNodes));
	animateBase.add(new ColorAnimator(treeNodes));
	animateBase.add(new RepaintAction());

	ActionList animate = new ActionList(444);
	animate.add(animateBase);
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

	TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
	focusGroup.addTupleSetListener(new TupleSetListener() {
	    public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
		NodeItem ni = (NodeItem) add[0];
		NodeItem pi = (NodeItem) ni.getParent();

		if (pi != null) {
		    localTrace.storeTrace(pi, ni);
		}

		updateTextArea(ni);

		m_vis.cancel("filter");
		m_vis.cancel("animate");
		m_vis.run("filter");

		m_vis.repaint();
	    }
	});

	// ------------------------------------------------

	// initialize the display
	setHighQuality(true);

	zoomToFitControl = new ZoomToFitControl();
	zoomControl = new ZoomControl();
	panControl = new PanControl();
	focusControl = new FocusControl(1);

	PopupMenuController popup = new PopupMenuController(this, m_vis);

	setSize(treeWidth, treeHeight);
	setItemSorter(new TreeDepthItemSorter());
	// addControlListener(zoomToFitControl);
	addControlListener(zoomControl);
	addControlListener(panControl);
	addControlListener(focusControl);
	addControlListener(popup);
	addControlListener(new GenericToolTipControl(toolTipText,
		toolTipFields, 176));

	addKeyListener(this);

	getTextEditor().addKeyListener(this);

	registerKeyboardAction(new OrientAction(Constants.ORIENT_LEFT_RIGHT),
		"left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
	// registerKeyboardAction(new OrientAction(Constants.ORIENT_TOP_BOTTOM),
	// "top-to-bottom", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
	registerKeyboardAction(new OrientAction(Constants.ORIENT_RIGHT_LEFT),
		"right-to-left", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
	// registerKeyboardAction(new OrientAction(Constants.ORIENT_BOTTOM_TOP),
	// "bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), WHEN_FOCUSED);

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

    // ------------------------------------------------------------------------

    private void updateTextArea(Node n) {
	ArrayList allProse = new ArrayList();
	Node aNode = n;
	do {
	    String toBeAdded = "";
	    if (aNode.getString("title").compareToIgnoreCase(newNodeText) != 0) {
		toBeAdded = aNode.getString("title") + "\n";
	    }

	    if (aNode.getString("text").compareToIgnoreCase(newNodeText) != 0) {
		toBeAdded += aNode.getString("text") + "\n";
	    }

	    if (toBeAdded.length() > 0) {
		toBeAdded += "\n";
		allProse.add(toBeAdded);
	    }

	    aNode = aNode.getParent();
	} while (aNode != null);

	Collections.reverse(allProse);
	String text = "";
	Iterator iter = allProse.iterator();
	while (iter.hasNext()) {
	    text += iter.next();
	}
	textArea.setText(text);

	if (allProse.size() < 2) { // TODO
	    textArea.setCaretPosition(0);
	}

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
		    "Unrecognized orientation value: " + orientation);
	}
	m_orientation = orientation;
	rtl.setOrientation(orientation);
	stl.setOrientation(orientation);
    }

    public int getOrientation() {
	return m_orientation;
    }

    public void pasteSubtrees() {
	treeToHistory();
	stopFilterAction();

	Iterator destinationIter = m_vis.getGroup(Visualization.FOCUS_ITEMS)
		.tuples();
	Iterator<Tree> sourceIter = treeMem.getAllTrees();

	while (destinationIter.hasNext()) {
	    Node destination = (Node) ((NodeItem) destinationIter.next())
		    .getSourceTuple();
	    NodeItem destinationItem = (NodeItem) m_vis.getVisualItem(
		    treeNodes, destination);
	    while (sourceIter.hasNext()) {
		Tree subtree = sourceIter.next();
		Node subtreeRoot = TreeLib.copySubTree(destination.getGraph(),
			TreeLib.getTreeRoot(subtree), destination);
		NodeItem newSubTreeRootItem = (NodeItem) m_vis.getVisualItem(
			treeNodes, subtreeRoot);
		TreeLib.setSubTreeNodesPosition(newSubTreeRootItem,
			destinationItem.getX(), destinationItem.getY());
		localTrace.storeTrace(destinationItem, newSubTreeRootItem);
	    }

	}

	runFilterAction();
	writeTree();

    }

    public void moveLeft() {
	NodeItem focusNodeItem = getFocusNodeItem();

	if (focusNodeItem != null) {
	    NodeItem parentItem = (NodeItem) focusNodeItem.getParent();
	    if (parentItem != null) {
		setFocusNode(parentItem);
	    }
	}

    }

    public void moveRight() {
	NodeItem focusNodeItem = getFocusNodeItem();

	NodeItem childItem = localTrace.getTrace(focusNodeItem);
	if (childItem != null) {
	    setFocusNode(childItem);
	} else {
	    childItem = (NodeItem) focusNodeItem.getChild(0);
	    if (childItem != null) {
		setFocusNode(childItem);
	    }
	}

    }

    public void moveUp() {
	NodeItem focusNodeItem = getFocusNodeItem();
	NodeItem previousSibling = TreeLib.getPreviousSiblingCircular(
		focusNodeItem, circularSiblingVisit);
	setFocusNode(previousSibling);
    }

    public void moveDown() {
	NodeItem focusNodeItem = getFocusNodeItem();
	NodeItem nextSibling = TreeLib.getNextSiblingCircular(focusNodeItem,
		circularSiblingVisit);
	setFocusNode(nextSibling);

    }

    public void addNewNodes() {
	treeToHistory();
	telper.addNewNodes(m_vis.getGroup(Visualization.FOCUS_ITEMS).tuples());

	writeTree();

    }

    public void copySubtrees() {
	treeToHistory();

	treeMem.clearMemory();
	treeMem.addSubTrees(m_vis.getGroup(Visualization.FOCUS_ITEMS).tuples());
    }

    public void cutSubtrees() {
	treeToHistory();
	stopFilterAction();
	treeMem.clearMemory();
	treeMem.addSubTrees(m_vis.getGroup(Visualization.FOCUS_ITEMS).tuples());

	Iterator iter = m_vis.getGroup(Visualization.FOCUS_ITEMS).tuples();

	NodeItem parentItem = null;

	while (iter.hasNext()) {
	    Node aNode = (Node) ((NodeItem) iter.next()).getSourceTuple();
	    Node parent = aNode.getParent();

	    if (parent != null) {
		data.removeChild(aNode);
		parentItem = (NodeItem) m_vis.getVisualItem(treeNodes, parent);
		localTrace.removeTraceOf(parentItem);
	    }
	}

	if (parentItem != null) {
	    setFocusNode(parentItem);
	}

	runFilterAction();
	writeTree();
    }

    public void removeNodes() {
	treeToHistory();
	stopFilterAction();

	localTrace.removeTracedAs(m_vis.getGroup(Visualization.FOCUS_ITEMS)
		.tuples());

	Node aParent = TreeLib.removeNodes(m_vis.getGroup(
		Visualization.FOCUS_ITEMS).tuples());
	if (aParent != null) {
	    m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(
		    m_vis.getVisualItem(treeNodes, aParent));
	}

	runFilterAction();
	writeTree();

    }

    public void removeSubtrees() {
	treeToHistory();
	stopFilterAction();

	localTrace.removeTracedAs(m_vis.getGroup(Visualization.FOCUS_ITEMS)
		.tuples());

	Node aParent = TreeLib.removeSubtrees(m_vis.getGroup(
		Visualization.FOCUS_ITEMS).tuples());
	m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(
		m_vis.getVisualItem(treeNodes, aParent));

	runFilterAction();
	writeTree();

    }

    public void editNodeText() {
	editNode(getFocusNodeItem(), true); // edit text
    }

    public void editNodeTitle() {
	editNode(getFocusNodeItem(), false); // edit title
    }

    public void editNode(NodeItem nodeItem, boolean text) {

	Node node = (Node) m_vis.getSourceTuple(nodeItem);

	enableControls(false);

	editing = true;
	textEditing = text;

	String field;
	int editWidth, editHeight;

	if (textEditing == true) {
	    field = "text";
	    nodeItem.setFillColor(ColorLib.rgb(100, 255, 100));
	    editWidth = (int) (400 * m_transform.getScaleX());
	    editHeight = (int) (40 * m_transform.getScaleY());
	} else {
	    field = "title";
	    nodeItem.setFillColor(ColorLib.rgb(100, 200, 255));
	    editWidth = (int) (300 * m_transform.getScaleX());
	    editHeight = (int) (40 * m_transform.getScaleY());
	}
	textColor.setDefaultColor(ColorLib.gray(200));
	m_vis.cancel("filter");
	m_vis.cancel("animation");
	m_vis.run("treeFading");

	originalText = node.getString(field);

	Rectangle2D b = nodeItem.getBounds();
	Rectangle r = m_transform.createTransformedShape(b).getBounds();
	r.width = editWidth;
	r.height = editHeight;
	r.x -= (editWidth - b.getWidth()) / 2;
	r.y -= (editHeight - b.getHeight()) / 2;

	Font f = getFont();
	int size = (int) Math.round(f.getSize() * m_transform.getScaleX());
	Font nf = new Font(f.getFontName(), f.getStyle(), size);
	getTextEditor().setFont(nf);

	if (nodeItem.getString(field).compareTo(newNodeText) == 0) {
	    nodeItem.setString(field, "");
	}
	editText(nodeItem, field, r);

    }

    public void saveAndStopEditing() {
	stopEditing();
	treeToHistory();

	NodeItem focusNodeItem = getFocusNodeItem();

	Node focusNode = (Node) m_vis.getSourceTuple(focusNodeItem);

	String field = textEditing == true ? "text" : "title";

	String newText = focusNode.getString(field).trim();
	if (newText.length() == 0) {
	    focusNode.set(field, originalText);
	}

	editing = false;

	writeTree();

	updateTextArea(focusNode);
	textColor.setDefaultColor(ColorLib.color(FOREGROUND));
	enableControls(true);
	m_vis.run("filter");

    }

    public void cancelEditing() {
	if (editing) {
	    NodeItem focusNodeItem = getFocusNodeItem();

	    Node focusNode = (Node) m_vis.getSourceTuple(focusNodeItem);

	    stopEditing();

	    String field = textEditing == true ? "text" : "title";

	    focusNode.setString(field, originalText);
	    originalText = "";

	    editing = false;
	    enableControls(true);
	    textColor.setDefaultColor(ColorLib.color(FOREGROUND));
	    m_vis.run("filter");

	}

    }

    public void moveNodeUp() {
	treeToHistory();
	stopFilterAction();
	NodeItem focusNodeItem = getFocusNodeItem();
	Node focusNode = (Node) m_vis.getSourceTuple(focusNodeItem);

	TreeLib.moveSiblingUp(focusNode);

	runFilterAction();

	writeTree();

    }

    public void moveNodeDown() {
	/*
         * MOVE current node down
         */
	treeToHistory();
	stopFilterAction();

	NodeItem focusNodeItem = getFocusNodeItem();
	Node focusNode = (Node) m_vis.getSourceTuple(focusNodeItem);

	TreeLib.moveSiblingDown(focusNode);

	runFilterAction();
	writeTree();

    }

    public void undoLast() {

	if (history.isLast() && newModification) {
	    treeToHistory();
	}
	PieceOfHistory poh = history.getPrevious();
	if (poh != null) {
	    rememberHistory(poh);
	}

    }

    public void redoLast() {

	PieceOfHistory poh = history.getNext();
	if (poh != null) {
	    rememberHistory(poh);
	}
	newModification = false;
    }

    public void stopFilterAction() {
	m_vis.cancel("filter");
	m_vis.cancel("animate");
    }

    public void runFilterAction() {
	m_vis.cancel("filter");
	m_vis.cancel("animate");
	m_vis.run("filter");
    }

    // -------------------------------

    public void writeTree() {
	fileManager.writeTree(data, treefuseFile);
    }

    // Key event handling

    public void keyPressed(KeyEvent e) {
	// TODO Auto-generated method stub
	int code = e.getKeyCode();

	if (!editing
		&& (((code == KeyEvent.VK_LEFT) && (m_orientation == Constants.ORIENT_LEFT_RIGHT)) || ((code == KeyEvent.VK_RIGHT) && (m_orientation == Constants.ORIENT_RIGHT_LEFT)))) {
	    /*
                 * LEFT
                 */

	    moveLeft();
	} else if (!editing
		&& (((code == KeyEvent.VK_RIGHT) && (m_orientation == Constants.ORIENT_LEFT_RIGHT)) || ((code == KeyEvent.VK_LEFT) && (m_orientation == Constants.ORIENT_RIGHT_LEFT)))) {
	    /*
                 * RIGHT
                 */

	    moveRight();
	} else if (!editing && (code == KeyEvent.VK_UP) && !e.isControlDown()) {
	    /*
                 * UP
                 */
	    moveUp();
	} else if (!editing && (code == KeyEvent.VK_DOWN) && !e.isControlDown()) {
	    /*
                 * DOWN
                 */
	    moveDown();

	} else if ((code == KeyEvent.VK_H) && e.isControlDown()) {
	    showHelp();
	} else if ((code == KeyEvent.VK_ESCAPE)
		&& (treefuseFile.compareTo(readmeFile) == 0)) {
	    hideHelp();
	}

	if (readOnly) {
	    return;
	}

	if (e.isControlDown() && (code == KeyEvent.VK_N) && !editing) {
	    /*
                 * NEW NODES
                 */

	    addNewNodes();
	} else if (e.isControlDown() && (code == KeyEvent.VK_C) && !editing) {
	    /*
                 * copy subtrees into mem
                 */

	    // telper.copySubTrees(m_vis.getGroup(
	    // Visualization.FOCUS_ITEMS).tuples());
	    copySubtrees();

	} else if (e.isControlDown() && (code == KeyEvent.VK_X) && !editing) {
	    /*
                 * cut subtrees into mem
                 */

	    cutSubtrees();

	} else if (e.isControlDown() && (code == KeyEvent.VK_V) && !editing) {

	    /*
                 * paste subtrees
                 */

	    pasteSubtrees();

	} else if (e.isControlDown() && (code == KeyEvent.VK_R) && !editing) {
	    /*
                 * REMOVE all subtrees rooted by focused nodes
                 */
	    removeNodes();
	} else if (e.isControlDown() && (code == KeyEvent.VK_T) && !editing) {
	    /*
                 * REMOVE all focused nodes
                 */
	    removeSubtrees();

	} else if (!editing
		&& ((code == KeyEvent.VK_SPACE) || (code == KeyEvent.VK_F2))) {
	    /*
                 * editing
                 */
	    if (e.isControlDown()) {
		editNodeText();
	    } else {
		editNodeTitle();
	    }

	} else if ((code == KeyEvent.VK_ENTER) && editing) {
	    /*
                 * stop editing
                 */
	    saveAndStopEditing();

	} else if (code == KeyEvent.VK_ESCAPE) {
	    /*
                 * ESCAPE
                 */

	    cancelEditing();

	} else if (!editing && (code == KeyEvent.VK_UP) && e.isControlDown()) {
	    /*
                 * MOVE current node up
                 */

	    moveNodeUp();

	} else if (!editing && (code == KeyEvent.VK_DOWN) && e.isControlDown()) {
	    moveNodeDown();

	} else if (!editing && (code == KeyEvent.VK_Z) && e.isControlDown()) {
	    undoLast();
	} else if (!editing && (code == KeyEvent.VK_Y) && e.isControlDown()) {
	    redoLast();
	}

    }

    public void keyReleased(KeyEvent e) {
	// TODO Auto-generated method stub

    }

    public void keyTyped(KeyEvent e) {
	// TODO Auto-generated method stub

    }

    private void showHelp() {
	if (treefuseFile.compareTo(readmeFile) == 0) {
	    return;
	}
	loadNewFile(readmeFile);
	readOnly = true;
    }

    private void hideHelp() {
	if (treefuseFile.compareTo(readmeFile) != 0) {
	    return;
	}
	loadNewFile(lastUsedFile);
	readOnly = false;
    }

    private void loadNewFile(String fileName) {
	try {
	    data.clear();

	    m_vis.removeGroup(tree);

	    data = fileManager.readTree(fileName);
	    telper.changeTree(data);

	    m_vis.add(tree, data);
	    m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(
		    (Tuple) m_vis.items(treeNodes).next());

	    TreeLib.setSubTreeNodesPosition((NodeItem) m_vis.getVisualItem(
		    treeNodes, data.getRoot()), 0, getHeight() / 2);

	    lastUsedFile = treefuseFile;
	    treefuseFile = fileName;

	    runFilterAction();

	} catch (Exception e) {
	    System.out.println("Couldn't load new file " + e.getMessage());
	}

    }

    private void rememberHistory(PieceOfHistory poh) {
	try {

	    NodeItem oni = getFocusNodeItem();
	    double x = oni.getX();
	    double y = oni.getY();
	    TreeLib.replaceTree(data, poh.getTree());

	    Node n = poh.getCurrentNode(data);
	    NodeItem ni = (NodeItem) m_vis.getVisualItem(treeNodes, n);

	    TreeLib.setSubTreeNodesPosition((NodeItem) m_vis.getVisualItem(
		    treeNodes, data.getRoot()), x, y);

	    telper.setFocusNode(ni);

	    runFilterAction();

	} catch (Exception e) {
	    System.out.println("Couldn't undo/redo " + e.getMessage());
	}
    }

    // -------------------------------

    private void enableControls(boolean enable) {
	zoomToFitControl.setEnabled(enable);
	zoomControl.setEnabled(enable);
	panControl.setEnabled(enable);
    }

    private void setFocusNode(NodeItem ni) {
	try {
	    if (ni != null) {
		m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(ni);
	    }
	} catch (Exception ignore) {

	}
    }

    private NodeItem getFocusNodeItem() {
	try {
	    return (NodeItem) m_vis.getGroup(Visualization.FOCUS_ITEMS)
		    .tuples().next();
	} catch (Exception e) {
	    return null;
	}
    }

    public TreefuseHelper getTelper() {
	return telper;
    }

    private void treeToHistory() {
	history
		.storeTree(data, (Node) m_vis
			.getSourceTuple(getFocusNodeItem()));
	newModification = true;
    }

    // ------------------------------------------------------------------------

    public static void main(String argv[]) {

	String label = "title";

	JComponent treeview = demo(label);

	JFrame frame = new JFrame(
		"t r e e f u s e | put your confusions into a tree");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setContentPane(treeview);
	frame.pack();
	frame.setVisible(true);
    }

    public static JComponent demo(final String label) {

	Tree t;

	t = fileManager.readTree(treefuseFile);

	if (t == null) {

	    t = new Tree();

	    t.addColumn("title", String.class);
	    t.addColumn("text", String.class);

	    Node n1 = t.addRoot();
	    n1.setString("title", "good morning ");
	    n1
		    .set(
			    "text",
			    "Welcome to treefuse my friend. Please press CTRL-H to get an overview over possible commands and ESC to escape from the read only help tree.");
	    fileManager.writeTree(t, treefuseFile);

	}

	// create a new treemap
	final Treefuse tview = new Treefuse(t, label);
	tview.setBackground(BACKGROUND);
	tview.setForeground(FOREGROUND);

	// the text area
	Font f = FontLib.getFont("Tahoma", 16);
	textArea = new JTextArea(textAreaRow, textAreaCol);
	textArea.setBackground(BACKGROUND);
	textArea.setForeground(FOREGROUND);
	textArea.setSize(treeWidth, treeHeight);
	textArea.setFocusable(false);
	textArea.setFont(f);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

	NodeItem root = (NodeItem) tview.getVisualization().getGroup(
		Visualization.FOCUS_ITEMS).tuples().next();

	textArea.setText(root.getString("title") + "\n"
		+ root.getString("text"));

	JScrollPane scroll = new JScrollPane(textArea,
		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

	// create a search panel for the tree map
	JSearchPanel search = new JSearchPanel(tview.getVisualization(),
		treeNodes, Visualization.SEARCH_ITEMS, label, true, true);
	search.setShowResultCount(true);
	search.setBorder(BorderFactory.createEmptyBorder(5, 5, 4, 0));
	search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
	search.setBackground(BACKGROUND);
	search.setForeground(FOREGROUND);

	final JFastLabel title = new JFastLabel("                 ");
	title.setPreferredSize(new Dimension(350, 20));
	title.setVerticalAlignment(SwingConstants.BOTTOM);
	title.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
	title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
	title.setBackground(BACKGROUND);
	title.setForeground(FOREGROUND);

	tview.addControlListener(new ControlAdapter() {
	    public void itemEntered(VisualItem item, MouseEvent e) {
		if (item.canGetString(label)) {
		    title.setText(item.getString(label));
		}
	    }

	    public void itemExited(VisualItem item, MouseEvent e) {
		title.setText(null);
	    }
	});

	Box centerBox = new Box(BoxLayout.Y_AXIS);
	centerBox.add(tview);
	centerBox.add(scroll);

	Box bottomBox = new Box(BoxLayout.X_AXIS);
	bottomBox.add(Box.createHorizontalStrut(10));
	bottomBox.add(title);
	bottomBox.add(Box.createHorizontalGlue());
	// bottomBox.add(search);
	bottomBox.add(Box.createHorizontalStrut(3));
	bottomBox.setBackground(BACKGROUND);
	bottomBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

	JPanel panel = new JPanel(new BorderLayout());
	panel.setBackground(BACKGROUND);
	panel.setForeground(FOREGROUND);
	panel.add(centerBox, BorderLayout.CENTER);

	panel.add(bottomBox, BorderLayout.SOUTH);

	panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
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

	private Point2D m_end = new Point2D.Double();

	private Point2D m_cur = new Point2D.Double();

	private int m_bias = 150;

	public void run(double frac) {
	    TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
	    if (ts.getTupleCount() == 0) {
		return;
	    }

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

    // Inner classes

    public static class NodeColorAction extends ColorAction {

	public NodeColorAction(String group) {
	    super(group, VisualItem.FILLCOLOR);
	}

	public int getColor(VisualItem item) {

	    if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
		return ColorLib.rgb(255, 190, 190);
	    } else if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
		return ColorLib.rgb(255, 200, 0);
	    } else if (item.getString("title").compareTo(newNodeText) == 0) {
		return ColorLib.rgb(240, 200, 230);
	    } else if (item.getDOI() > -1) {
		return ColorLib.rgb(255, 255, 200);
	    } else {
		return ColorLib.rgba(255, 255, 255, 0);
	    }
	}

    } // end of inner class TreeMapColorAction

    public static class PopupMenuController extends ControlAdapter implements
	    ActionListener {
	Visualization vis;

	Display d;

	private JPopupMenu nodePopupMenu;

	private Treefuse tf;

	private NodeItem clickedItem;

	private JMenuItem pasteSubtree;

	public PopupMenuController(Treefuse tf, Visualization vis) {
	    this.tf = tf;
	    this.vis = vis;

	    this.d = vis.getDisplay(0);

	    // create popupMenu for nodes
	    nodePopupMenu = new JPopupMenu();

	    JMenuItem editNodeTitle = new JMenuItem("edit node title - SPACE");
	    JMenuItem editNodeText = new JMenuItem(
		    "edit node text - CTRL-SPACE");
	    JMenuItem addNode = new JMenuItem("add node - CTRL-N");
	    JMenuItem removeNode = new JMenuItem("remove node - CTRL-R");
	    JMenuItem removeSubtree = new JMenuItem("remove subtree - CTRL-T");
	    JMenuItem cutSubtree = new JMenuItem("cut subtree - CTRL-X");
	    JMenuItem copySubtree = new JMenuItem("copy subtree - CTRL-C");
	    pasteSubtree = new JMenuItem("paste subtree - CTRL-V");

	    editNodeTitle.setActionCommand("editNodeTitle");
	    editNodeTitle.addActionListener(this);

	    editNodeText.setActionCommand("editNodeText");
	    editNodeText.addActionListener(this);

	    addNode.setActionCommand("addNewNode");
	    addNode.addActionListener(this);

	    removeSubtree.setActionCommand("removeSubTree");
	    removeSubtree.addActionListener(this);

	    removeNode.setActionCommand("removeNode");
	    removeNode.addActionListener(this);

	    cutSubtree.setActionCommand("cutSubTree");
	    cutSubtree.addActionListener(this);

	    copySubtree.setActionCommand("copySubTree");
	    copySubtree.addActionListener(this);

	    pasteSubtree.setActionCommand("pasteSubTree");
	    pasteSubtree.addActionListener(this);

	    nodePopupMenu.add(editNodeTitle);
	    nodePopupMenu.add(editNodeText);
	    nodePopupMenu.addSeparator();
	    nodePopupMenu.add(addNode);
	    nodePopupMenu.add(removeNode);
	    nodePopupMenu.add(removeSubtree);
	    nodePopupMenu.addSeparator();
	    nodePopupMenu.add(cutSubtree);
	    nodePopupMenu.add(copySubtree);
	    nodePopupMenu.add(pasteSubtree);

	}

	public void actionPerformed(ActionEvent e) {
	    ArrayList job = new ArrayList();
	    job.add(clickedItem);

	    if (e.getActionCommand().compareTo("editNodeTitle") == 0) {
		autoPan.setEnabled(false);
		tf.setFocusNode(clickedItem);
		tf.editNode(clickedItem, false);
		autoPan.setEnabled(true);
	    } else if (e.getActionCommand().compareTo("editNodeText") == 0) {
		autoPan.setEnabled(false);
		tf.setFocusNode(clickedItem);
		tf.editNode(clickedItem, true);
		autoPan.setEnabled(true);
	    } else if (e.getActionCommand().compareTo("addNewNode") == 0) {
		tf.getTelper().addNewNodes(job.iterator());
	    } else if (e.getActionCommand().compareTo("removeSubTree") == 0) {
		tf.setFocusNode(clickedItem);
		tf.removeSubtrees();

	    } else if (e.getActionCommand().compareTo("removeNode") == 0) {
		tf.setFocusNode(clickedItem);
		tf.removeNodes();
	    } else if (e.getActionCommand().compareTo("cutSubTree") == 0) {
		tf.cutSubtrees();

	    } else if (e.getActionCommand().compareTo("copySubTree") == 0) {
		tf.copySubtrees();
	    } else if (e.getActionCommand().compareTo("pasteSubTree") == 0) {
		tf.pasteSubtrees();
	    }
	}

	public void itemClicked(VisualItem item, MouseEvent e) {
	    if (SwingUtilities.isRightMouseButton(e)) {
		if (item instanceof NodeItem) {
		    nodePopupMenu.show(e.getComponent(), e.getX(), e.getY());
		    clickedItem = (NodeItem) item;

		}
	    }
	}

	public void mouseClicked(MouseEvent e) {
	    if (SwingUtilities.isRightMouseButton(e)) {
		clickedItem = null;
	    }
	}

    }

} // end of class Treefuse
