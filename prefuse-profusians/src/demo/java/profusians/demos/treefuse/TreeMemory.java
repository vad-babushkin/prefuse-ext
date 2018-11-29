package profusians.demos.treefuse;

import java.util.ArrayList;
import java.util.Iterator;

import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.visual.NodeItem;
import profusians.util.TreeLib;

/**
 * Utility class to store tree for cut, copy, paste actions
 * 
 * @author goose
 * 
 */

public class TreeMemory {

    ArrayList<Tree> listOfTrees;

    public TreeMemory() {
	listOfTrees = new ArrayList<Tree>();
    }

    /**
         * adding a tree to the memory
         * 
         * @param o
         *                the tree to be added
         */

    public void addTree(Tree o) {
	listOfTrees.add(o);
    }

    /**
         * add all iterator specified trees to the memory
         * 
         * @param iter
         */

    public void addTrees(Iterator<Tree> iter) {
	while (iter.hasNext()) {
	    Tree aTree = iter.next();
	    addTree(aTree);
	}
    }

    /**
         * adding a local copy of all subtrees rooted by the nodes of the
         * iterator to the memory the roots of the subtree might be NodeItems or
         * Nodes
         * 
         * @param iter
         */

    public void addSubTrees(Iterator iter) {
	Node aNode;
	while (iter.hasNext()) {
	    Object o = iter.next();
	    if (o instanceof NodeItem) {
		aNode = (Node) ((NodeItem) o).getSourceTuple();
	    } else if (o instanceof Node) {
		aNode = (Node) o;
	    } else {
		continue;
	    }

	    addTree(TreeLib.createCopyOfSubtree(aNode));
	}
    }

    /**
         * getting an iterator over all trees in the memory. (doesn't clear the
         * memory)
         * 
         * @return iterator over all trees
         */
    public Iterator<Tree> getAllTrees() {
	return listOfTrees.iterator();
    }

    /**
         * clearing all trees from the memory
         * 
         */
    public void clearMemory() {
	listOfTrees.clear();
    }

}
