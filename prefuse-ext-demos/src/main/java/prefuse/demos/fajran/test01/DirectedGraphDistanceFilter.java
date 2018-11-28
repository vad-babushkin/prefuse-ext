package prefuse.demos.fajran.test01;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.util.PrefuseLib;
import prefuse.util.collections.Queue;
import prefuse.visual.VisualItem;

public class DirectedGraphDistanceFilter extends GraphDistanceFilter {

	public DirectedGraphDistanceFilter(String group, int distance) {
		super(group, distance);
		// TODO Auto-generated constructor stub
	}

	public DirectedGraphDistanceFilter(String group, String sources,
			int distance) {
		super(group, sources, distance);
		// TODO Auto-generated constructor stub
	}

	public DirectedGraphDistanceFilter(String group) {
		super(group);
		// TODO Auto-generated constructor stub
	}
	
    public void run(double frac) {
        // mark the items
    	
        Iterator items = m_vis.visibleItems(m_group);
        while ( items.hasNext() ) {
            VisualItem item = (VisualItem)items.next();
            item.setDOI(Constants.MINIMUM_DOI);
        }
        
        Queue q = new Queue();
        
        // set up the graph traversal
        TupleSet src = m_vis.getGroup(m_sources);
        Iterator iter = src.tuples();
        
        while (iter.hasNext()) {
        	//System.out.println("add");
        	q.add(iter.next(), 0);
        }
        
        Tuple tuple;
        int d;
        while (!q.isEmpty()) {
        	
        	tuple = (Tuple)q.removeFirst();
        	
        	//System.out.println("rem");
        	VisualItem item = (VisualItem)tuple;
        	
        	d = q.getDepth(tuple);
        	
        	PrefuseLib.updateVisible(item, true);
        	item.setDOI(-d);
        	item.setExpanded(d < m_distance);

        	if (tuple instanceof Node) {
	        	Node n = (Node)tuple;
	        	
	        	//System.out.println("node="+n+", depth="+d+", distance="+m_distance);

	        	if ((m_distance == -1) || (d < m_distance)) {
	        		iter = n.outEdges();
	        		while (iter.hasNext()) {
	        			Edge e = (Edge)iter.next();
	        			Node v = e.getAdjacentNode(n);
	        			
	        			if (q.getDepth(v) == -1) {
		        			q.add(e, d+1);
		        			q.add(v, d+1);
	        			}
	        		}
	        	}
        	}
        	
        }
        
        // mark unreached items
        items = m_vis.visibleItems(m_group);
        while ( items.hasNext() ) {
            VisualItem item = (VisualItem)items.next();
            if ( item.getDOI() == Constants.MINIMUM_DOI ) {
                PrefuseLib.updateVisible(item, false);
                item.setExpanded(false);
            }
            	
        }
    }
}