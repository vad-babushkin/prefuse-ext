package profusians.controls;

import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;

import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * A neighbour highlight control for directed graphs.
 * 
 * It basically works by defining three focus groups for source nodes, target
 * nodes and nodes which are both. These focus groups are filled with the
 * respective nodes at "itemEntered" and cleared at "itemExit".
 * 
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class DirectedGraphNeighborHighlightControl extends ControlAdapter {

    private String m_activity = null;

    private HighlightColorAction colorAction;

    private static final String sourceGroupName = "sourceNodes";

    private static final String targetGroupName = "targetNodes";

    private static final String bothGroupName = "sourceAndTargetNdoes";

    TupleSet sourceTupleSet = null, targetTupleSet = null, bothTupleSet = null;

    public DirectedGraphNeighborHighlightControl( int[] colorPalette) {
	this(colorPalette, null);
    }

    public DirectedGraphNeighborHighlightControl( int colorPalette[],
	     String activity) {

	m_activity = activity;

	colorAction = new HighlightColorAction(colorPalette);

    }

    /**
         * returns the color action to be used to highlight the neighbors
         * 
         * @return the highlight color action
         */

    public ColorAction getHighlightColorAction() {
	return colorAction;
    }

    public void itemEntered( VisualItem item, MouseEvent e) {

	if (sourceTupleSet == null) {
	    /*
                 * this delayed initialization is done to avoid that the
                 * visualization has to be given to the contructor of this
                 * control;
                 */

	     Visualization vis = item.getVisualization();
	    try {
		vis.addFocusGroup(sourceGroupName);
		vis.addFocusGroup(targetGroupName);
		vis.addFocusGroup(bothGroupName);
	    } catch ( Exception ex) {
		System.out
			.println("Problems over problems while adding foucs groups to visualization "
				+ ex.getMessage());
	    }

	    sourceTupleSet = vis.getFocusGroup(sourceGroupName);
	    targetTupleSet = vis.getFocusGroup(targetGroupName);
	    bothTupleSet = vis.getFocusGroup(bothGroupName);

	    colorAction.setFocusGroups(new TupleSet[] { sourceTupleSet,
		    targetTupleSet, bothTupleSet });
	}

	if (item instanceof NodeItem) {
	    setNeighbourHighlight((NodeItem) item, true);
	}
    }

    public void itemExited( VisualItem item, MouseEvent e) {
	if (item instanceof NodeItem) {
	    setNeighbourHighlight((NodeItem) item, false);
	}

	if (m_activity != null) {
	    item.getVisualization().run(m_activity);
	}
    }

    protected void setNeighbourHighlight( NodeItem centerNode,
	     boolean state) {

	 HashSet source = new HashSet();
	 HashSet target = new HashSet();
	 HashSet both = new HashSet();

	 Iterator iterInEdges = centerNode.inEdges();
	while (iterInEdges.hasNext()) {
	     EdgeItem edge = (EdgeItem) iterInEdges.next();
	     NodeItem sourceNode = edge.getSourceItem();
	    if (state) {
		source.add(sourceNode);
	    }
	    sourceNode.setHighlighted(state);
	}

	 Iterator iterOutEdges = centerNode.outEdges();
	while (iterOutEdges.hasNext()) {
	     EdgeItem edge = (EdgeItem) iterOutEdges.next();
	     NodeItem targetNode = edge.getTargetItem();

	    if (state) {
		if (source.contains(targetNode)) {
		    both.add(targetNode);
		} else {
		    target.add(targetNode);
		}
	    }

	    targetNode.setHighlighted(state);
	}

	if (state) {
	    source.removeAll(both);

	     Iterator iterSource = source.iterator();
	    while (iterSource.hasNext()) {
		sourceTupleSet.addTuple((NodeItem) iterSource.next());
	    }

	     Iterator iterTarget = target.iterator();
	    while (iterTarget.hasNext()) {
		targetTupleSet.addTuple((NodeItem) iterTarget.next());
	    }

	     Iterator iterBoth = both.iterator();
	    while (iterBoth.hasNext()) {
		bothTupleSet.addTuple((NodeItem) iterBoth.next());
	    }
	} else {
	    sourceTupleSet.clear();
	    targetTupleSet.clear();
	    bothTupleSet.clear();
	}

	if (m_activity != null) {
	    centerNode.getVisualization().run(m_activity);
	}
    }

    private class HighlightColorAction extends ColorAction {

	private int[] colorPalette;

	private TupleSet[] focusGroups;

	public HighlightColorAction( int[] colorPalette) {
	    super(Graph.NODES, VisualItem.FILLCOLOR);

	    this.colorPalette = colorPalette;

	}

	public void setFocusGroups( TupleSet[] focusGroups) {
	    this.focusGroups = focusGroups;
	}

	public TupleSet[] getFocusGroups() {
	    return this.focusGroups;
	}

	public void setColorPalette( int[] colorPalette) {
	    this.colorPalette = colorPalette;
	}

	public int[] getColorPalette() {
	    return this.colorPalette;
	}

	public int getColor( VisualItem item) {
	    if (item == null) {
		return 0;
	    }
	    if (item.isHighlighted()) {
		for (int i = 0; i < 3; i++) {
		    if (focusGroups[i].containsTuple(item)) {
			return colorPalette[i];
		    }
		}
	    }
	    return 0;
	}
    }
}
