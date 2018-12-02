package prefuse.demos.idot;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.Display;
import prefuse.action.layout.Layout;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.util.display.DisplayLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;


/**
 * Class for performing the layout of a graph by using information
 * produced earlier by the dot executable.
 * The node and edge positions are parsed from the "pos" (for nodes and edges) 
 * and "lp" (edges only) attributes.
 */
public class DotLayout extends Layout {
	/** name of an internal attribute holding edge routing information */
	public static final String EDGE_COORDS = DotDisplay.EDGE_COORDS;
	
	/** internal name of the main graph in prefuse's data structures */
	public static final String GRAPH = DotDisplay.GRAPH_GROUP;

	/** minimum and maximum layout bounds found */
	int minx, miny;
	int maxx, maxy;

	
	public DotLayout() {
		super();
	}

	/**
	 * Performs the layout
	 * 
	 * @param frac  ignored (there is no "fractional" layout)
	 */
	public void run(double frac) {
		Graph g = (Graph) getVisualization().getVisualGroup(GRAPH);

		if(g == null) {
			System.err.println("no filtered graph to layout");
			return;
		}

		// make sure field exists
		Table edgeTable = g.getEdgeTable();
		if(!edgeTable.canSet(EDGE_COORDS, double[].class))
			edgeTable.addColumn(EDGE_COORDS, double[].class);

		// clear previous edge routings
		getVisualization().setValue(DotDisplay.GRAPH_EDGES, null, EDGE_COORDS, null);

		parseLocationAttributes(g);

		// negative coords are probably off-screen now

		if(maxx != Integer.MIN_VALUE) {
			for(int i=0; i<getVisualization().getDisplayCount(); i++) {
				Display display = getVisualization().getDisplay(i);
				Rectangle2D b = DisplayLib.getBounds(g.nodes(), 50);
				DisplayLib.fitViewToBounds(display, b, 0);
				
				// don't zoom too big, even if the graph is small (like sem.dot)
				if(display.getScale() > 1) {
					display.zoom(new Point2D.Double(display.getX() + display.getWidth()/2, 
							display.getY() + display.getHeight()/2), 1./display.getScale());
				}

				// overviews are nice, but too small is too small
				if (Config.print) System.out.println("current zoom: " + display.getScale());
				if(display.getScale() < 0.8) {
					Iterator nodes = g.getNodes().tuples();
					if(nodes.hasNext()) {
						NodeItem n = (NodeItem) nodes.next();
						if (Config.print) System.out.println("graph is quite large for this window size");
						display.zoomAbs(
								new Point2D.Double(n.getBounds().getX(), 
										n.getBounds().getY()), 
										0.8 / display.getScale());
					}						
				}
			}
		}

	} //
		
	/**
	 * Parses the values of the "pos" and "lp" attributes and updates the
	 * locations of nodes and edges accordingly.
	 *  
	 * @param g
	 */
	private void parseLocationAttributes(Graph g) {
		Iterator nodes = g.getNodes().tuples();
	
		minx = Integer.MAX_VALUE; miny = Integer.MAX_VALUE;
		maxx = Integer.MIN_VALUE; maxy = Integer.MIN_VALUE;
		
		while(nodes.hasNext()) {
			NodeItem node = (NodeItem) nodes.next();
		
			String pos = node.getString("pos");
			String[] xy = pos.split(",");

			int x = Integer.parseInt(xy[0]);
			int y = - Integer.parseInt(xy[1]); // all Y coords mirrored

			setX(node, null, x);
			setY(node, null, y);

			minx = Math.min(minx, x); miny = Math.min(miny, y);
			maxx = Math.max(maxx, x); maxy = Math.max(maxy, y);
			if (Config.print) System.out.println("Setting node " + node + " to " + x + ", " + y);
		}

		Iterator edges = g.getEdges().tuples();
		while(edges.hasNext()) {
			EdgeItem edge = (EdgeItem) edges.next();
			String pos = edge.getString("pos");
			String[] points = pos.split(" ");

			NodeItem from = (NodeItem) edge.getSourceNode();
			NodeItem to = (NodeItem) edge.getTargetNode();

			// typical line: "e,91,196 41,90 52,113 74,159 87,187"
			//               "e,start  end   -- interm. points --"
			
			// dot puts the edge ends at the border of nodes
			// center of nodes fits prefuse better
			double tx = to.getX();
			double ty = to.getY();
			double fx = from.getX();
			double fy = from.getY();

			double coords[] = new double[points.length >= 2 ? points.length*2 : 2*2];
			coords[0] = fx; 
			coords[1] = fy;
			
			for(int i=2; i< points.length; i++) {
				int comma = points[i].indexOf(',');
				int x = Integer.parseInt(points[i].substring(0, comma));
				int y = - Integer.parseInt(points[i].substring(comma+1));
				
				coords[(i-1)*2] = x;
				coords[(i-1)*2+1] = y;
				
				minx = Math.min(minx, x); miny = Math.min(miny, y);
				maxx = Math.max(maxx, x); maxy = Math.max(maxy, y);
				
				//if (Config.print) System.out.printf(" -> (%d, %d)", x, y);
			}
			
			coords[coords.length-2] = tx;
			coords[coords.length-1] = ty;
													
			edge.set(EDGE_COORDS, coords);

			if (Config.print) System.out.println("egde from '" + from.getString("label")
					+ "' to '"+ to.getString("label") + "' with "+ coords.length/2 +" points");
						
			String lp = edge.getString("lp");
			if(lp != null) {
				String[] xy = lp.split(",");

				int x = Integer.parseInt(xy[0]);
				int y = - Integer.parseInt(xy[1]); // all Y coords mirrored

				if (Config.print) System.out.println("edge has label at " + x + "," + y);

				if(!edge.canSet("lp2", double[].class)) {
					edge.getTable().addColumn("lp2", double[].class);
				}
				edge.set("lp2", new double[] { x, y });
			}
		}		
	}
}
