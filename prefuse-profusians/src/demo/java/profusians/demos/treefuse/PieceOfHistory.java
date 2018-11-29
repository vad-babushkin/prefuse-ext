package profusians.demos.treefuse;

import java.util.ArrayList;

import prefuse.data.Node;
import prefuse.data.Tree;
import profusians.util.TreeLib;

/**
 * Utility class to store a copy of a tree. In addition information about the
 * current node can be stored in the memory.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class PieceOfHistory {

    static final FileManager fm = new FileManager();

    Tree storeTree;

    ArrayList nodeTrack;

    final String tmpStoreFile = "tmp.treeml";

    public PieceOfHistory(Tree t, Node currentNode) {
	// fm.writeTree(t,tmpStoreFile ); //TODO
	// this.storeTree = fm.readTree(tmpStoreFile);

	this.storeTree = TreeLib.createCopyOfTree(t);
	this.nodeTrack = TreeLib.getRootNodeTrack(currentNode);
    }

    public Tree getTree() {
	return storeTree;
    }

    public Node getCurrentNode(Tree t) {
	return TreeLib.getNodeThroughTrack(t, nodeTrack);
    }

}
