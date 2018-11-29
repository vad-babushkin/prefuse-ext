package profusians.demos.treefuse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import prefuse.Visualization;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.visual.NodeItem;

/**
 * This class contains all the dirty tree hacks which are too treefuse specific
 * to put them in the TreeLib utility class
 * 
 * @author goose
 * 
 */

public class TreefuseHelper {

    Tree data;

    Visualization vis;

    HashMap lastVisitedChild;

    int pasteCounter = 0;

    private static final String newNodeText = "press space to edit";

    private static String treeNodes = "tree.nodes";

    public TreefuseHelper(Tree data, Visualization vis, HashMap lastVisitedChild) {
	this.data = data;
	this.vis = vis;
	this.lastVisitedChild = lastVisitedChild;
    }

    public void changeTree(Tree data) {
	this.data = data;
    }

    public NodeItem getFocusNodeItem() {
	return (NodeItem) vis.getGroup(Visualization.FOCUS_ITEMS).tuples()
		.next();
    }

    public boolean focusIsVisible() {
	NodeItem focusItem = getFocusNodeItem();
	try {
	    if ((focusItem == null) || !focusItem.isVisible()) {
		return false;
	    }
	    return true;
	} catch (Exception e) { // prefuse lazy loading problem ...
	    return false;
	}
    }

    public void setFocusNode(NodeItem ni) {
	if (ni != null) {
	    vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(ni);
	}
    }

    public void addFocusNode(NodeItem ni) {
	if (ni != null) {
	    vis.getGroup(Visualization.FOCUS_ITEMS).addTuple(ni);
	}
    }

    public void addNewNodes(Iterator iter) {

	NodeItem childItem = null;

	ArrayList newChildren = new ArrayList();
	while (iter.hasNext()) {
	    NodeItem aFocusItem = (NodeItem) iter.next();
	    childItem = addNode(aFocusItem);
	    newChildren.add(childItem);

	}

	Iterator iterChildren = newChildren.iterator();
	int round = 0;
	while (iterChildren.hasNext()) {
	    if (++round == 1) {
		setFocusNode((NodeItem) iterChildren.next());
	    } else {
		addFocusNode((NodeItem) iterChildren.next());
	    }
	}

    }

    private NodeItem addNode(NodeItem parentNode) {
	Node parent = (Node) parentNode.getSourceTuple();

	Node child;
	try {
	    child = data.addChild(parent);
	} catch (Exception e) {
	    return null;
	}
	child.set("title", newNodeText);
	child.set("text", newNodeText);

	NodeItem childItem = (NodeItem) vis.getVisualItem(treeNodes, child);

	childItem.setX(parentNode.getX());
	childItem.setY(parentNode.getY());

	return childItem;
    }

}
