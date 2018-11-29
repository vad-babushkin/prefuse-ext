package profusians.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import prefuse.Visualization;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.util.PrefuseLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Tree related utility class
 * 
 * 
 */

public class TreeLib {

    private static String treeNodes = "tree.nodes";

    /**
         * Set the name of the tree group
         * 
         * @param name
         */
    public static void setTreeGroupName( String name) {
    }

    /**
         * Set the name of the tree nodes group
         * 
         * @param name
         */
    public static void setTreeNodesGroupName( String name) {
	treeNodes = name;
    }

    /**
         * Set the name of the tree edges group
         * 
         * @param name
         */

    public static void setTreeEdgesGroupName( String name) {
    }

    /**
         * Returns an iterator over all nodes contained in the subtree rooted by
         * the given node.
         * 
         * @param n
         *                the root of the subtree
         * @return
         */

    public static Iterator subtreeNodes( Node n) {
	 ArrayList result = new ArrayList();
	 LinkedList jobList = new LinkedList();

	jobList.add(n);

	while (!jobList.isEmpty()) {
	     Node aNode = (Node) jobList.removeFirst();
	    result.add(aNode);
	     Iterator iter = aNode.children();
	    while (iter.hasNext()) {
		jobList.add(iter.next());
	    }

	}

	return result.iterator();
    }

    /**
         * Returns an iterator over all node items contained in the subtree
         * rooted by the given node. Parents are always iterated before their
         * children
         * 
         * @param n
         *                the root of the subtree
         * @return
         */

    public static Iterator subtreeNodeItems( NodeItem n) {
	 ArrayList result = new ArrayList();
	 LinkedList jobList = new LinkedList();

	jobList.add(n);

	while (!jobList.isEmpty()) {
	     NodeItem aNodeItem = (NodeItem) jobList.removeFirst();
	    result.add(aNodeItem);
	     Iterator iter = aNodeItem.children();
	    while (iter.hasNext()) {
		jobList.add(iter.next());
	    }
	}

	return result.iterator();
    }

    /**
         * Clears the given tree "data" and fills it with the tree newTree
         * 
         * @param data
         * @param newTree
         */

    public static void replaceTree(Tree data, Tree newTree) {
	data.clear();

	data = newTree;
	if (true) {
	    return;
	}

	 Node tmpRoot = TreeLib.getTreeRoot(newTree);

	 Iterator iter = TreeLib.subtreeNodes(tmpRoot);
	 HashMap oldNew = new HashMap();
	while (iter.hasNext()) {
	     Node oNode = (Node) iter.next();
	     Node nNode = TreeLib.copyNode(data, oNode);
	    oldNew.put(oNode, nNode);
	    if (oNode != tmpRoot) {
		 Node oParent = oNode.getParent();
		data.addEdge((Node) oldNew.get(oParent), (Node) oldNew
			.get(oNode));
	    }
	}

    }

    /**
         * Copy the subtree based on the given root node to the given
         * destination node as its children Returns the root node of the newly
         * created subtree
         * 
         * @param root
         *                the root node of the subtree to be copied
         * @param destination
         *                the destination node which acts as the parent of the
         *                newly created subtree
         */

    public static Node copySubTree( Graph g, Node root,
	     Node destination) {
	 HashMap oldNew = new HashMap();

	 Iterator iter = TreeLib.subtreeNodes(root);

	oldNew.put(root.getParent(), destination);

	while (iter.hasNext()) {
	     Node o = (Node) iter.next();
	     Node n = TreeLib.copyNode(g, o);
	    oldNew.put(o, n);
	    g.addEdge((Node) oldNew.get(o.getParent()), n);
	}

	return (Node) oldNew.get(root);
    }

    /**
         * create a copy of this tree
         * 
         * @param t
         *                the tree to be copied
         * @return
         */
    public static Tree createCopyOfTree( Tree t) {
	return createCopyOfSubtree(getTreeRoot(t));
    }

    /**
         * creates a copy of the subtree specified by the given node as its root
         * 
         * @param subtreeRoot
         *                the root of the subtree
         * @return
         */

    public static Tree createCopyOfSubtree( Node subtreeRoot) {

	 HashMap oldNew = new HashMap();

	 Tree result = new Tree();

	 Schema s = subtreeRoot.getSchema();
	 int numCol = s.getColumnCount();
	for (int i = 0; i < numCol; i++) {
	     String field = s.getColumnName(i);
	    result.addColumn(field, s.getColumnType(field));
	}

	 Iterator iter = TreeLib.subtreeNodes(subtreeRoot);

	while (iter.hasNext()) {
	     Node o = (Node) iter.next();
	     Node n = TreeLib.copyNode(result, o);
	    oldNew.put(o, n);
	    if (o != subtreeRoot) {
		result.addEdge((Node) oldNew.get(o.getParent()), n);
	    }
	}
	return result;
    }

    /**
         * adds the given subtree as a copy as a child to the destination node
         * 
         * @param subtree
         *                the subtree to be copied
         * @param destination
         *                the destination node
         */
    public static void addCopyOfSubtree( Tree subtree,
	     Node destination) {

	copySubTree(destination.getGraph(), subtree.getRoot(), destination);

    }

    /**
         * move the subtree based on the given node root to the node destination
         * as its children
         * 
         * @param root
         *                the root of the subtree to be moved
         * @param destination
         *                the destination node
         */

    public static void moveSubTree( Node root, Node destination) {

	 Graph g = root.getGraph();

	 Node parent = root.getParent();
	if (parent != null) {
	    g.removeEdge(g.getEdge(parent, root));
	}

	g.addEdge(destination, root);

    }

    /**
         * moves the given node one position down in the order of the siblings
         * 
         * @param aNode
         *                the node to be moved down
         */

    public static void moveSiblingDown( Node aNode) {
	 Node nextSibling = aNode.getNextSibling();
	if (nextSibling == null) { // also includes the case that the
	    // current node is the root node
	    return;
	}

	TreeLib.moveSiblingUp(nextSibling);

    }

    /**
         * moves the given node one position up in the order of the siblings
         * 
         * @param aNode
         *                the node to be moved up
         */

    public static void moveSiblingUp( Node aNode) {

	 Node prevSibling = aNode.getPreviousSibling();
	if (prevSibling == null) { // also includes the case that the
	    // current node is the root node
	    return;
	}

	 Node parentNode = aNode.getParent();

	 LinkedList jobList = new LinkedList();

	jobList.add(prevSibling);

	Node nextSibling = aNode.getNextSibling();

	while (nextSibling != null) {
	    jobList.add(nextSibling);
	    nextSibling = nextSibling.getNextSibling();
	}

	while (!jobList.isEmpty()) {
	     Node job = (Node) jobList.removeFirst();
	    moveSubTree(job, parentNode);
	}

    }

    /**
         * Sets the subtree based by the given nodeitem visible/invisible
         * 
         * @param root
         *                the root of the subtree
         * @param visible
         *                if true, the subtree will be set visible, otherwise
         *                invisible
         */
    public static void setSubTreeVisible( NodeItem root,
	     boolean visible) {

	 Iterator iter = subtreeNodeItems(root);

	while (iter.hasNext()) {
	     NodeItem item = (NodeItem) iter.next();

	    setVisible(item, visible);
	     Iterator iterEdges = item.childEdges();
	    while (iterEdges.hasNext()) {
		 EdgeItem ei = (EdgeItem) iterEdges.next();
		setVisible(ei, visible);
	    }
	}
    }

    /**
         * Set the node position of the given root based subtree to the given
         * values x and y
         * 
         * @param root
         *                the root of the subtree
         * @param x
         *                the x-position
         * @param y
         *                the y-position
         */

    public static void setSubTreeNodesPosition( NodeItem root,
	     double x, double y) {
	 Iterator iter = subtreeNodeItems(root);

	while (iter.hasNext()) {
	     NodeItem item = (NodeItem) iter.next();
	    PrefuseLib.setX(item, null, x);
	    PrefuseLib.setY(item, null, y);

	}
    }

    /**
         * Determines if the given candidate is in the subtree rooted by the
         * node root
         * 
         * @param root
         *                the root of the subtree
         * @param candidate
         *                the node to be checked
         * @return true if the candidate node is in the subtree, otherwise false
         */

    public static boolean isInSubTree( Node root, Node candidate) {
	Node checkNode = candidate;
	while (checkNode != null) {
	    if (checkNode == root) {
		return true;
	    }
	    checkNode = checkNode.getParent();
	}
	return false;
    }

    /**
         * add a copy of the given node n to the graph g. if the node n is not
         * from the graph g, the schema of the graph g and the graph of the node
         * n must be the same
         * 
         * @param g
         *                the graph the copy of the node should be added to
         * @param n
         *                the node to be added as copy
         * @return the newly created node
         */
    public static Node copyNode( Graph g, Node n) {

	 Node newNode = g.addNode();
	 Schema s = n.getSchema();
	 int numCol = s.getColumnCount();
	for (int i = 0; i < numCol; i++) {
	     String field = s.getColumnName(i);
	    newNode.set(field, n.get(field));
	}

	return newNode;
    }

    /**
         * Get this node item's previous tree sibling. If the node item is the
         * first sibling and the parameter circular is set true the last sibling
         * is returned
         * 
         * @param ni
         *                the node item
         * @param circular
         *                if true the previous sibling of the first nodeitem is
         *                the last sibling of it
         * @return the previous sibling if any
         */

    public static NodeItem getPreviousSiblingCircular( NodeItem ni,
	     boolean circular) {
	NodeItem previousSibling = (NodeItem) ni.getPreviousSibling();
	if ((previousSibling == null) && circular) {
	     NodeItem parentItem = (NodeItem) ni.getParent();
	    if (parentItem != null) {
		previousSibling = (NodeItem) parentItem.getChild(parentItem
			.getChildCount() - 1);
	    }
	}

	return previousSibling;
    }

    /**
         * Get this node item's next tree sibling. If the node item is the first
         * sibling and the parameter circular is set true the first sibling is
         * returned
         * 
         * @param ni
         *                the node item
         * @param circular
         *                if true the next sibling of the last node item is the
         *                first sibling
         * @return the next sibling if any
         */

    public static NodeItem getNextSiblingCircular( NodeItem ni,
	     boolean circular) {
	NodeItem nextSibling = (NodeItem) ni.getNextSibling();
	if ((nextSibling == null) && circular) {
	     NodeItem parentItem = (NodeItem) ni.getParent();
	    if (parentItem != null) {
		nextSibling = (NodeItem) parentItem.getChild(0);
	    }
	}
	return nextSibling;
    }

    /**
         * Removes a node and the subtree rooted by it from the tree. If the
         * node is the root of the tree, nothing is done.
         * 
         * @param aNode
         * @return
         */

    public static Node removeSubtree(Node aNode) {

	if (aNode instanceof NodeItem) {
	    aNode = (Node) ((NodeItem) aNode).getSourceTuple();
	}

	 Node parent = aNode.getParent();
	if ((parent == null) || (aNode == null)) { // don't remove the tree
	    // root
	    return null;
	}

	 Tree data = (Tree) aNode.getGraph();
	try {
	    data.removeChild(aNode);
	} catch ( Exception ex) {
	    System.out.println("problems while removing the node - never mind");
	}

	return parent;

    }

    /**
         * Removes all nodes specified by the iterator from their tree. The
         * nodes does not necessary need to be from the same tree. This
         * functions doesn't remove the root of a tree
         * 
         * @param iter
         *                the iterator over all the nodes to be removed
         * @return
         */

    public static Node removeSubtrees( Iterator iter) {

	Node aNode;
	Node aParent = null;
	Node tmpNode;
	while (iter.hasNext()) {
	     Object o = iter.next();
	    if (o instanceof NodeItem) {
		aNode = (Node) ((NodeItem) o).getSourceTuple();
	    } else {
		aNode = (Node) iter.next();
	    }

	    tmpNode = removeSubtree(aNode);
	    if ((aParent == null) && (tmpNode != null)) {
		aParent = tmpNode;
	    }
	}

	return aParent;
    }

    /**
         * removes the node from its tree. the children of the node will become
         * children of the parent of the node
         * 
         * @param remove
         *                the node to be removed
         * @return the parent of the removed node
         */

    public static Node removeNode( Node remove) {

	 Tree t = (Tree) remove.getGraph();
	 Node parent = remove.getParent();

	if (parent == null) {
	    return null; // disable the removal of the root node
	}

	int numberOfNextSiblings = 0;

	Node nextSibling = remove.getNextSibling();
	while (nextSibling != null) {
	    numberOfNextSiblings++;
	    nextSibling = nextSibling.getNextSibling();
	}

	Iterator iter = remove.children();

	 ArrayList allChildren = new ArrayList();
	while (iter.hasNext()) {
	    allChildren.add(iter.next());
	}

	iter = allChildren.iterator();

	while (iter.hasNext()) {
	     Node child = (Node) iter.next();
	    t.removeEdge(t.getEdge(remove, child));
	    t.addEdge(parent, child);

	    for (int i = 0; i < numberOfNextSiblings; i++) {
		moveSiblingUp(child);
	    }

	}
	t.removeEdge(t.getEdge(parent, remove));
	t.removeNode(remove);

	return parent;

    }

    /**
         * Removes all nodes specified by the iterator from their tree. The
         * children of a removed node will become the children of the parent of
         * the node. Doesn't remove the root of a tree
         * 
         * @param iter
         *                the iterator over the nodes to be removed
         * @return
         */

    public static Node removeNodes( Iterator iter) {

	Node aNode;
	Node aParent = null;
	Node tmpNode;
	while (iter.hasNext()) {
	     Object o = iter.next();
	    if (o instanceof NodeItem) {
		aNode = (Node) ((NodeItem) o).getSourceTuple();

	    } else {
		aNode = (Node) iter.next();
	    }

	    tmpNode = removeNode(aNode);
	    if ((aParent == null) && (tmpNode != null)) {
		aParent = tmpNode;
	    }
	}

	return aParent;
    }

    /**
         * moves the subtree specified by the root node to the destination node
         * as its new parent The node position of the moved subtree will be set
         * to the position of the destination node if the given visualization is
         * not null
         * 
         * @param vis
         *                the visualization for which the node positions should
         *                be set
         * @param root
         *                the root of the subtree to be moved
         * @param destination
         *                the destination node
         */

    public static void moveSubTree( Visualization vis, Node root,
	     Node destination) {

	TreeLib.moveSubTree(root, destination);

	 NodeItem rootItem = (NodeItem) vis.getVisualItem(treeNodes, root);
	 NodeItem destinationItem = (NodeItem) vis.getVisualItem(
		treeNodes, destination);

	TreeLib.setSubTreeNodesPosition(rootItem, destinationItem.getX(),
		destinationItem.getY());
	TreeLib.setSubTreeVisible((NodeItem) vis.getVisualItem(treeNodes,
		destination), true);

    }

    /**
         * Get the tree root of this tree. Seems to be more stable than
         * t.getRoot()
         * 
         * @param t
         *                the tree
         * @return
         */

    public static Node getTreeRoot( Tree t) { // seems to be more save
	// the
	// t.getRoot()
	Node current = (Node) t.nodes().next();
	Node parent = null;
	while ((parent = current.getParent()) != null) {
	    current = parent;
	}
	return current;
    }

    /**
         * Get an Arraylist filled with the sibling number to be choosen per
         * generation to come from the root node to the given destination node
         * (sibling number means the position among its siblings starting from
         * the first sibling)
         * 
         * @param destination
         *                the list of child numbers
         * @return
         */
    public static ArrayList getRootNodeTrack(Node destination) {

	 ArrayList result = new ArrayList();
	do {
	     int pos = getSiblingsPosition(destination);
	    result.add(Integer.valueOf(pos));
	    destination = destination.getParent();
	} while ((destination != null) && (destination.getParent() != null));
	Collections.reverse(result);

	return result;
    }

    /**
         * Get the node of this tree which can be reached trough traversing from
         * the root according to the given track, which is an Arraylist filled
         * with the number of the sibling to be choosen per generation on the
         * way (sibling number means the position among its siblings starting
         * from the first sibling)
         * 
         * @param t
         *                the tree
         * @param track
         * @return the node found
         */
    public static Node getNodeThroughTrack( Tree t, ArrayList track) {

	Node current = getTreeRoot(t);

	for (int i = 0; i < track.size(); i++) {
	     Integer numberOfEdgeToFollow = (Integer) track.get(i);
	    current = current.getChild(numberOfEdgeToFollow.intValue());
	}

	return current;
    }

    /**
         * Get the sibling position of this node
         * 
         * @param n
         *                the node
         * @return the sibling position
         */
    public static int getSiblingsPosition(Node n) {

	int i = 0;
	while ((n = n.getPreviousSibling()) != null) {
	    i++;
	}
	return i;
    }

    /**
         * Returns a hashmap containing for all nodes within the subtree rooted
         * by this node the (nodeitem, number of nodeitems in subtree) pair
         * 
         * @param ni
         *                the nodeitem
         * @return the hashmap containing the subtree size informations per
         *         nodeitem
         */

    public static HashMap getSubtreeSizeMap( NodeItem ni) {
	 HashMap sizeMap = new HashMap();
	calculateSubtreeSize(ni, sizeMap);
	return sizeMap;
    }

    private static int calculateSubtreeSize( NodeItem ni,
	     HashMap sizeMap) {
	 Iterator iter = ni.children();
	if (!iter.hasNext()) {
	    sizeMap.put(ni, new Integer(1));
	    return 1;
	}
	int value = 0;
	while (iter.hasNext()) {
	     NodeItem aItem = (NodeItem) iter.next();
	    value += calculateSubtreeSize(aItem, sizeMap);
	}
	sizeMap.put(ni, new Integer(value));
	return value;
    }

    /**
         * set this visualitem visible/invisible
         * 
         * @param item
         *                the visual item
         * @param visible
         *                if true the item is set visible, otherwise invisible
         */

    private static void setVisible( VisualItem item, boolean visible) {
	item.setStartVisible(item.isVisible());
	item.setEndVisible(visible);
	item.setVisible(visible);
    }

}
