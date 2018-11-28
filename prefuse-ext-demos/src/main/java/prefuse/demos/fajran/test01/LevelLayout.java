package prefuse.demos.fajran.test01;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import prefuse.Visualization;
import prefuse.action.layout.Layout;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.tuple.TupleSet;
import prefuse.util.PrefuseLib;
import prefuse.util.collections.Queue;
import prefuse.visual.VisualItem;

public class LevelLayout extends Layout {

	public LevelLayout(String group) {
		super(group);
	}

	public void run(double frac) {
		long tstart = System.currentTimeMillis();
		System.out.println("level layout: start="+tstart);
		Queue q = new Queue();
		TupleSet src = m_vis.getGroup(Visualization.FOCUS_ITEMS);
		
		Iterator iter = src.tuples();
        while (iter.hasNext()) {
        	Tuple t = (Tuple)iter.next();
        	if (t instanceof Node) {
        		q.add(t, 0);
        	}
        }
        
        HashMap<Integer, ArrayList<VisualItem>> level = new HashMap<Integer, ArrayList<VisualItem>>();

        Tuple tuple;
        int d;
        int maxdepth = 0;
        while (!q.isEmpty()) {
        	
        	tuple = (Tuple)q.removeFirst();
        	
        	VisualItem item = (VisualItem)tuple;
        	
        	d = q.getDepth(tuple);
        	if (d > maxdepth) {
        		maxdepth = d;
        	}
        	
        	ArrayList<VisualItem> list = level.get(d);
        	if (list == null) {
        		list = new ArrayList<VisualItem>();
        		level.put(d, list);
        	}
        	list.add(item);
        	
        	Node n = (Node)tuple;
        	
    		iter = n.outEdges();
    		while (iter.hasNext()) {
    			Edge e = (Edge)iter.next();
    			Node v = e.getAdjacentNode(n);
    			
    			if (q.getDepth(v) == -1) {
        			q.add(v, d+1);
    			}
        	}
        }
        
        System.out.println("level layout: bfs="+(System.currentTimeMillis()-tstart));
        
        Rectangle2D r = getLayoutBounds();  
        double cy = r.getCenterY();
        
        double hh = 20;
        double ww = 100;
        
        int i;
        for (i=0; i<=maxdepth; i++) {
        	ArrayList<VisualItem> list = level.get(i);
        	
        	int total = list.size();
        	double y = cy - total * hh / 2.0;
        	
        	int j;
        	for (j=0; j<total; j++) {
        		VisualItem item = list.get(j);
            		
        		PrefuseLib.setX(item, item, ww+i*ww);
        		PrefuseLib.setY(item, item, y + hh * j);
        		
        	}
        	
        }
        
        System.out.println("level layout: finish="+(System.currentTimeMillis()-tstart));
        
	}

}
