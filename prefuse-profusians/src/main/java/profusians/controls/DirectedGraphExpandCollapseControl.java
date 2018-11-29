package profusians.controls;

import java.awt.event.MouseEvent;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import prefuse.controls.ControlAdapter;
import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.util.collections.Queue;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Implements a expand/collapse control for directed graphs. A right mouse click
 * collapses the subtree exclusively rooted by the clicked node, a left mouse
 * click expands (previous collapsed) subtrees rooted by the clicked node.
 * 
 * @author <a href="http://goosebumps4all.net"> Martin Dudek </a>
 * 
 */

public class DirectedGraphExpandCollapseControl extends ControlAdapter {

    boolean m_setNodePosition = true;

    /**
         * Creates a new ExpandCollapseControlForDirectedGraphs
         * 
         */
    public DirectedGraphExpandCollapseControl() {
	this(true);
    }

    /**
         * Creates a new ExpandCollapseControlForDirectedGraphs
         * 
         * @param setNodePosition
         *                if true, the position of the nodes to be expanded are
         *                set to the coordinates of the left mouse clicked node,
         *                otherwise their previously stored position is used
         */

    public DirectedGraphExpandCollapseControl( boolean setNodePosition) {

	m_setNodePosition = setNodePosition;

    }

    public void itemPressed( VisualItem item, MouseEvent ev) {
	boolean expand;
	if (SwingUtilities.isLeftMouseButton(ev)) {
	    expand = true;
	} else if (SwingUtilities.isRightMouseButton(ev)) {
	    expand = false;
	} else {
	    return;
	}

	if (item instanceof NodeItem) {

	     HashSet involvedEdgeItems = new HashSet();

	     Queue q = new Queue(); // prefuse queue util
	     BitSet visit = new BitSet();
	    q.add(item, 0);

	    while (!q.isEmpty()) {

		 NodeItem aNodeItem = (NodeItem) q.removeFirst();

		if (visit.get(aNodeItem.getRow())) {
		    continue;
		}

		visit.set(aNodeItem.getRow());

		if (expand) {
		    continue;
		}

		for ( Iterator iter = aNodeItem.outEdges(); iter.hasNext();) {

		     EdgeItem aEdgeItem = (EdgeItem) iter.next();
		     NodeItem targetNodeItem = aEdgeItem.getTargetItem();

		    involvedEdgeItems.add(aEdgeItem);

		    if (!visit.get(targetNodeItem.getRow())) {
			 Node r = aEdgeItem.getAdjacentNode(aNodeItem);
			q.add(r, q.getDepth(aNodeItem) + 1);
		    } else {
			continue;
		    }

		    if (expand && !targetNodeItem.isVisible()
			    && m_setNodePosition) {
			PrefuseLib.setX(targetNodeItem, null, item.getEndX());
			PrefuseLib.setY(targetNodeItem, null, item.getEndY());
		    }

		    if (expand) {
			PrefuseLib.updateVisible(targetNodeItem, expand);
		    } else {
			 Iterator inNeighbors = targetNodeItem
				.inNeighbors();
			boolean visibleNeighbor = false;
			while (inNeighbors.hasNext() && !visibleNeighbor) {
			     NodeItem aInNeighbor = (NodeItem) inNeighbors
				    .next();
			    if (aInNeighbor != item) {
				visibleNeighbor = aInNeighbor.isVisible();
			    }
			}
			PrefuseLib.updateVisible(targetNodeItem,
				visibleNeighbor);
		    }

		    targetNodeItem.setExpanded(expand);

		     Iterator addEdges = targetNodeItem.edges();

		    while (addEdges.hasNext()) {
			involvedEdgeItems.add(addEdges.next());
		    }

		}

		 Iterator iter = involvedEdgeItems.iterator();

		while (iter.hasNext()) {
		     EdgeItem aEdgeItem = (EdgeItem) iter.next();
		     NodeItem nodeItemA = aEdgeItem.getSourceItem();
		     NodeItem nodeItemB = aEdgeItem.getTargetItem();
		    if (nodeItemA.isVisible() && nodeItemB.isVisible()) {
			PrefuseLib.updateVisible(aEdgeItem, true);
			aEdgeItem.setExpanded(true);
		    } else {
			PrefuseLib.updateVisible(aEdgeItem, false);
			aEdgeItem.setExpanded(false);
		    }

		}

	    }

	}

    }
}
