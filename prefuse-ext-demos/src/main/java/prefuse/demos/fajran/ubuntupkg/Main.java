package prefuse.demos.fajran.ubuntupkg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.Layout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.Control;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.collections.Queue;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;

public class Main {
	public static void main(String[] args) {
		Graph graph = null;
		try {
			String BASE = "/Users/iang/kuliah2/tesis/test/ubuntu/data/mid/";
			if (args.length > 0) {
				BASE = args[0];
			}
		    graph = new DataReader().readGraph(BASE + "/nodes.txt", BASE + "/edges.txt");
		} catch ( IOException e ) {
		    e.printStackTrace();
		    System.err.println("Error loading graph. Exiting...");
		    System.exit(1);
		}

		// add the graph to the visualization as the data group "graph"
		// nodes and edges are accessible as "graph.nodes" and "graph.edges"
		final Visualization vis = new Visualization();
		vis.add("graph", graph);

		// draw the "name" label for NodeItems
		LabelRenderer r = new LabelRenderer("pkg");
		r.setRoundedCorner(8, 8); // round the corners

		// create a new default renderer factory
		// return our name label renderer as the default for all non-EdgeItems
		// includes straight line edges for EdgeItems by default
		vis.setRendererFactory(new DefaultRendererFactory(r));
		

		// create our nominal color palette
		// pink for females, baby blue for males
        ColorAction fill = new ColorAction("graph.nodes", 
                VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
        fill.add(VisualItem.FIXED, ColorLib.rgb(255,100,100));
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255,200,125));
        
		// use black for node text
		ColorAction text = new ColorAction("graph.nodes",
		    VisualItem.TEXTCOLOR, ColorLib.gray(0));
		// use light grey for edges
		ColorAction edges = new ColorAction("graph.edges",
		    VisualItem.STROKECOLOR, ColorLib.gray(200));
			
		// create an action list containing all color assignments
		ActionList color = new ActionList();//Activity.INFINITY);
		color.add(fill);
		color.add(text);
		color.add(edges);

		// create an action list with an animated layout
		// the INFINITY parameter tells the action list to run indefinitely
		//ActionList layout = new ActionList(Activity.INFINITY);
		ActionList layout = new ActionList();
		layout.add(new RandomLayout());
		//layout.add(new CollapsedStackLayout("graph"));
		//layout.add(new RepaintAction());
		
		Graph vg = new Graph();
		vis.addFocusGroup("vg", vg);
		

		ActionList fdlayout = new ActionList(Activity.INFINITY);
		final ForceDirectedLayout fdl = new ForceDirectedLayout("graph");
		fdlayout.add(fdl);
//		ActionList fdlayout = new ActionList();
//		
//		fdlayout.add(new TreeRootAction("graph"));
//		fdlayout.add(new RadialTreeLayout("graph"));
		fdlayout.add(new RepaintAction());

		ActionList ch = new ActionList();//Activity.INFINITY);
		ch.add(fill);
		ch.add(new RepaintAction());
		
				// add the actions to the visualization
		vis.putAction("color", color);
		vis.putAction("layout", layout);
		vis.putAction("fdlayout", fdlayout);
		vis.putAction("ch", ch);
		
		ActionList filter = new ActionList();
		filter.add(new DirectedGraphDistanceFilter("graph", Visualization.SEARCH_ITEMS, 0));
		filter.add(new RepaintAction());
		vis.putAction("filter", filter);
		
		ActionList filterfocus = new ActionList();
		filterfocus.add(new DirectedGraphDistanceFilter("graph", Visualization.FOCUS_ITEMS, -1));
		filterfocus.add(new RepaintAction());
		vis.putAction("filterfocus", filterfocus);
		
		color.alwaysRunAfter(filter);
		color.alwaysRunAfter(filterfocus);
		
		//vis.runAfter("filter", "color");
		//vis.runAfter("color", "layout");
		
		
		// create a new Display that pull from our Visualization
		Display display = new Display(vis);
		display.setSize(720, 500); // set display size
		display.addControlListener(new DragControl()); // drag items around
		display.addControlListener(new PanControl(Control.MIDDLE_MOUSE_BUTTON));  // pan with background left-drag
		display.addControlListener(new ZoomControl()); // zoom with vertical right-drag
		display.addControlListener(new ZoomToFitControl());
		
		display.addControlListener(new FocusControl(1));

		// create a new window to hold the visualization
		JFrame frame = new JFrame("prefuse example");
		// ensure application exits when window is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cp = frame.getContentPane();
		cp.setLayout(new BorderLayout());
		cp.add(display, BorderLayout.CENTER);
		
		SearchQueryBinding sqb = new SearchQueryBinding(vis.getGroup("graph.nodes"), "pkg");
		
        JSearchPanel search = sqb.createSearchPanel();
        
        final SearchTupleSet sts = sqb.getSearchSet();
        sts.addTupleSetListener(new TupleSetListener() {

			public void tupleSetChanged(TupleSet tset, Tuple[] add,
					Tuple[] rem) {

				if (tset.getTupleCount() < 25) {
					vis.cancel("fdlayout");
					
					//System.out.println("search tuple set changed");
					int i;
					for (i=0; i<rem.length; i++) {
						((VisualItem)rem[i]).setFixed(false);
					}
					for (i=0; i<add.length; i++) {
						((VisualItem)add[i]).setFixed(false);
						((VisualItem)add[i]).setFixed(true);
					}
					
					vis.run("filter");
					
					//vis.run("fdlayout");
				}
			}
        
        });
        
        vis.addFocusGroup(Visualization.SEARCH_ITEMS, sts);
        
        TupleSet focusGroup = vis.getGroup(Visualization.FOCUS_ITEMS);
        focusGroup.addTupleSetListener(new TupleSetListener() {

			public void tupleSetChanged(TupleSet tset, Tuple[] add,
					Tuple[] rem) {
				
				//System.out.println("focus count="+tset.getTupleCount()+", add="+add.length+", rem="+rem.length);
				
				//System.out.println("q: " + sts.getQuery());
				sts.search("");
				//System.out.println("sts count: " + sts.getTupleCount());
				
				int i;
				for (i=0; i<rem.length; i++) {
					((VisualItem)rem[i]).setFixed(false);
				}
				for (i=0; i<add.length; i++) {
					((VisualItem)add[i]).setFixed(false);
					((VisualItem)add[i]).setFixed(true);
				}
				
				vis.run("filterfocus");
				
				vis.run("fdlayout");
				//vis.run("layout");
			}
        	
        });
        
        
        search.setShowResultCount(true);
        search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
        search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
        search.setBackground(Color.WHITE);
        search.setForeground(Color.BLACK);
            
        cp.add(search, BorderLayout.SOUTH);
		
		frame.pack();           // layout components in window
		frame.setVisible(true); // show the window
		
		sts.search("");
		
		vis.putAction("init", new InitialAction("graph"));
		
		//vis.run("filter");
		vis.run("init");
		vis.run("color");  // assign the colors
		//vis.run("layout"); // start up the animated layout
		vis.run("ch");
		
	}
	
//    public static class TreeRootAction extends GroupAction {
//        public TreeRootAction(String graphGroup) {
//            super(graphGroup);
//        }
//        public void run(double frac) {
//            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
//            if ( focus==null || focus.getTupleCount() == 0 ) return;
//            
//            Graph g = (Graph)m_vis.getGroup(m_group);
//            Node f = null;
//            Iterator tuples = focus.tuples();
//            while (tuples.hasNext() && !g.containsTuple(f=(Node)tuples.next()))
//            {
//                f = null;
//            }
//            if ( f == null ) return;
//            g.getSpanningTree(f);
//        }
//    }
    
	public static class InitialAction extends Layout {

		private Random r = new Random(12345678L);
		private String m_group;
		
		public InitialAction(String group) {
			m_group = group;
		}
		
		public void run(double frac) {
			
	        Rectangle2D b = getLayoutBounds();
	        double x, y;
	        double w = b.getWidth();
	        double h = b.getHeight();
	        Iterator iter = getVisualization().visibleItems(m_group);
	        while ( iter.hasNext() ) {
	            VisualItem item = (VisualItem)iter.next();
	            x = (int)(b.getX() + r.nextDouble()*w);
	            y = (int)(b.getY() + r.nextDouble()*h);
	            setX(item,null,x);
	            setY(item,null,y);
	            
	            item.setDOI(Constants.MINIMUM_DOI);
	            PrefuseLib.updateVisible(item, false);
	        }
			
		}
		
	}
	
    public static class DirectedGraphDistanceFilter extends GraphDistanceFilter {

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
	        
	        Graph g = (Graph)m_vis.getFocusGroup("vg");
	        g.clear();
	        
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
}
