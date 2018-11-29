package profusians.demos.treefuse;

import java.util.ArrayList;

import prefuse.data.Node;
import prefuse.data.Tree;

/**
 * Utility class enabeling
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */
public class TreeHistory {
    ArrayList history;

    int pointer;

    public TreeHistory() {

	history = new ArrayList();
	pointer = -1;
    }

    public int storeTree(Tree t, Node currentNode) {

	if (pointer != history.size() - 1) {
	    for (int i = history.size() - 1; i > pointer; i--) {
		history.remove(i);
	    }
	}
	history.add(new PieceOfHistory(t, currentNode));
	pointer = history.size() - 1;
	return history.size();
    }

    public boolean isLast() {
	return pointer == history.size() - 1;
    }

    public PieceOfHistory getPrevious() {
	if (pointer <= 0) {
	    return null;
	}
	return (PieceOfHistory) history.get(--pointer);
    }

    public PieceOfHistory getNext() {
	if (pointer == history.size() - 1) {
	    return null;
	}
	return (PieceOfHistory) history.get(++pointer);
    }

}
