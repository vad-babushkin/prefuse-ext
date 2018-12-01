package prefuse.demos.pap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;

import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.SquarifiedTreeMapLayout;
import prefuse.action.layout.graph.TreeLayout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.io.TreeMLReader;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.FilterIterator;
import prefuse.data.util.TreeNodeIterator;

import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ColorMap;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTree;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;


/**
 * Based on the TreeMap demo from the prefuse library 
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */
public class YakMap extends Display {

	// create data description of labels, setting colors, fonts ahead of time
	private static final Schema LABEL_SCHEMA = PrefuseLib.getVisualItemSchema();

	public static final String TREE_CHI = "data/chi-ontology.xml.gz";

	static {
		LABEL_SCHEMA.setDefault(VisualItem.INTERACTIVE, false);
		LABEL_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(200));
		LABEL_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Tahoma",16));
	}

	private final int YAK_DISTANCE = 2;

	private static final String tree = "tree";
	private static final String treeNodes = "tree.nodes";
	private static final String treeEdges = "tree.edges";
	private static final String labels = "labels";

	private SearchQueryBinding searchQ;

	private final double nodeGap = 3;

	public YakMap(Tree t, String label) {
		super(new Visualization());

		// add the tree to the visualization
		VisualTree vt = m_vis.addTree(tree, t);

		final NodeItem treeRoot = (NodeItem) vt.getRoot();

		m_vis.setVisible(treeEdges, null, false);

		// now create the labels as decorators of the nodes
		//m_vis.addDecorators(labels, treeNodes, LABEL_SCHEMA);

		// set up the renderers - one for nodes and one for labels
		DefaultRendererFactory rf = new DefaultRendererFactory();

		rf.add(new InGroupPredicate(treeNodes), new NodeRenderer(nodeGap));
		rf.add(new InGroupPredicate(labels), new LabelRenderer(label));
		m_vis.setRendererFactory(rf);

		// get the depth of the tree
		int depth = 0;
		Iterator iter = t.nodes();
		while (iter.hasNext()) {
			Node n = (Node) iter.next();
			int d = n.getDepth();
			if (d>depth) {
				depth=d;
			}
		}

		//Set up root
		NodeItem root = ((NodeItem)vt.getNode(0));
		m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(root);


		ActionList filter = new ActionList();

		final YakeyeTreeFilter yakfilter = new YakeyeTreeFilter(tree, YAK_DISTANCE);
		filter.add(yakfilter);


		// the colors palettes for nodes and search results
		ColorMap cmap = new ColorMap(
				ColorLib.getInterpolatedPalette(depth+1,
						ColorLib.rgb(11,11,111),ColorLib.rgb(66,76,244)), 0, 3);

		ColorMap smap = new ColorMap(
				ColorLib.getInterpolatedPalette(depth+1,
						ColorLib.rgb(111,11,11),ColorLib.rgb(244,76,67)), 0, depth);

		// border colors

		final ColorAction borderColor = new BorderColorAction(treeNodes);
		final ColorAction fillColor = new FillColorAction(treeNodes,cmap,smap);

		// color settings
		ActionList colors = new ActionList();
		colors.add(fillColor);
		colors.add(borderColor);
		m_vis.putAction("colors", colors);


		// animate paint change
		ActionList animatePaint = new ActionList(777);
		animatePaint.add(new ColorAnimator(treeNodes));
		animatePaint.add(new RepaintAction());
		m_vis.putAction("animatePaint", animatePaint);

		// create the single filtering and layout action list

		ActionList layout = new ActionList();

		final SquarifiedTreeMapLayout treeMapLayout = new SquarifiedTreeMapLayout(tree,26);

		layout.add(filter);
		layout.add(treeMapLayout);
		layout.add(new LabelLayout(labels,nodeGap+4));
		layout.add(colors);

		layout.add(new RepaintAction());
		m_vis.putAction("layout", layout);

		// initialize our display
		setSize(700,600);
		setItemSorter(new TreeDepthItemSorter(true));
		addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
				item.setStrokeColor(borderColor.getColor(item));
				item.getVisualization().repaint();
			}
			public void itemExited(VisualItem item, MouseEvent e) {
				item.setStrokeColor(item.getEndStrokeColor());
				item.getVisualization().repaint();
			}
		});
		addControlListener(new FocusControl(1));


		searchQ = new SearchQueryBinding(vt.getNodeTable(), label);
		m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, searchQ.getSearchSet());
		searchQ.getPredicate().addExpressionListener(new UpdateListener() {
			public void update(Object src) {
				m_vis.cancel("animatePaint");
				m_vis.run("colors");
				m_vis.run("animatePaint");
			}
		});

		// perform layout
		m_vis.run("layout");


		TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
		focusGroup.addTupleSetListener(new TupleSetListener() {
			public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
			{
				NodeItem root = (NodeItem)add[0];
				for (int i=0;i<yakfilter.getPredecessorDistance();i++) {//TODO: should be offered by the filter
					if (root != treeRoot) {
						root = (NodeItem) root.getParent();
					}
				}
				treeMapLayout.setLayoutRoot(root);
				m_vis.run("layout");

			}
		});

	}



	public SearchQueryBinding getSearchQuery() {
		return searchQ;
	}

	public static void main(String argv[]) {
		UILib.setPlatformLookAndFeel();


		String infile = TREE_CHI;
		String label = "name";
		if ( argv.length > 1 ) {
			infile = argv[0];
			label = argv[1];
		}
		JComponent treemap = demo(infile, label);

		JFrame frame = new JFrame("p r e f u s e  |  t r e e m a p");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(treemap);
		frame.pack();
		frame.setVisible(true);
	}

	public static JComponent demo() {
		return demo(TREE_CHI, "name");
	}

	public static JComponent demo(String datafile, final String label) {
		Tree t = null;
		try {
			t = (Tree)new TreeMLReader().readGraph(datafile);
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}

		// create a new treemap
		final YakMap treemap = new YakMap(t, label);


		// create a search panel for the tree map
		JSearchPanel search = treemap.getSearchQuery().createSearchPanel();
		search.setShowResultCount(true);
		search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
		search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));

		final JFastLabel title = new JFastLabel("                 ");
		title.setPreferredSize(new Dimension(350, 20));
		title.setVerticalAlignment(SwingConstants.BOTTOM);
		title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
		title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));


		treemap.addControlListener(new ControlAdapter() {
			public void itemEntered(VisualItem item, MouseEvent e) {
		
		/*
		String prose = "";
		Node n = (Node) item;
		while (n.getParent() != null) {    
		   prose = n.getString(label) + " " + prose;
		   n = n.getParent();
		}
		prose = n.getString(label) + " " + prose;    
		title.setText(prose);
		*/
				title.setText(((Node)item).getString(label));
			}
			public void itemExited(VisualItem item, MouseEvent e) {
				title.setText(null);
			}
		});

		Box box = UILib.getBox(new Component[]{title,search}, true, 10, 3, 0);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(treemap, BorderLayout.CENTER);
		panel.add(box, BorderLayout.SOUTH);
		UILib.setColor(panel, Color.BLACK, Color.GRAY);
		return panel;
	}

	// ------------------------------------------------------------------------

	/**
	 * Set the stroke color for drawing treemap node outlines. A graded
	 * grayscale ramp is used, with higer nodes in the tree drawn in
	 * lighter shades of gray.
	 */
	public static class BorderColorAction extends ColorAction {


		public BorderColorAction(String group) {
			super(group, VisualItem.STROKECOLOR);

		}

		public int getColor(VisualItem item) {

			if ( item.isHover() && !m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)  ) {
				return ColorLib.rgb(100,210,220);
			}
			return item.getFillColor();



		}
	}

	/**
	 * Set fill colors for treemap nodes. Search items are colored
	 * in pink, while normal nodes are shaded according to their
	 * depth in the tree.
	 */
	public static class FillColorAction extends ColorAction {
		private ColorMap cmap;
		private ColorMap smap;

		public FillColorAction(String group,ColorMap cmap,ColorMap smap) {
			super(group, VisualItem.FILLCOLOR);
			this.cmap = cmap;
			this.smap = smap;
		}

		public int getColor(VisualItem item) {
			if ( item instanceof NodeItem ) {
				NodeItem nitem = (NodeItem)item;




				if ( m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
					return ColorLib.rgb(160,0,120);
				} else if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
					return smap.getColor(nitem.getDepth());
				} else {

					if ((nitem.getDepth())%3==0) {
						return cmap.getColor(1);
					} else if ((nitem.getDepth())%3==1) {
						return cmap.getColor(2);
					} else {
						return cmap.getColor(3);
					}


				}
			} else {
				return cmap.getColor(0);
			}
		}

	} // end of inner class TreeMapColorAction

	/**
	 * Set label positions. Labels are assumed to be DecoratorItem instances,
	 * decorating their respective nodes.
	 */

	public static class LabelLayout extends Layout {
		double spacing;
		public LabelLayout(String group,double spacing) {
			super(group);
			this.spacing=spacing;
		}
		public void run(double frac) {
			Iterator iter = m_vis.items(m_group						);
			while ( iter.hasNext() ) {

				DecoratorItem item = (DecoratorItem)iter.next();
				Rectangle2D boundsLabel = item.getBounds();
				VisualItem node = item.getDecoratedItem();
				Rectangle2D bounds = node.getBounds();
				item.setVisible(node.isVisible());
				setX(item, null, bounds.getX()+boundsLabel.getWidth()/2.+spacing);
				setY(item, null, bounds.getY()+boundsLabel.getHeight()/2.+spacing);
			}
		}
	} // end of inner class LabelLayout

	/**
	 * A renderer for treemap nodes. Draws simple rectangles, but defers
	 * the bounds management to the layout.
	 */
	public static class NodeRenderer extends AbstractShapeRenderer {
		private Rectangle2D m_bounds = new Rectangle2D.Double();
		private double gap;
		public NodeRenderer(double gap) {
			m_manageBounds = false;
			this.gap = gap;

		}

		protected Shape getRawShape(VisualItem item) {
			Rectangle2D itemBounds = item.getBounds();

			m_bounds.setRect(new Rectangle2D.Double(itemBounds.getX()+gap,itemBounds.getY()+gap,itemBounds.getWidth()-2*gap,itemBounds.getHeight()-2*gap));

			return m_bounds;
		}
	} // end of inner class NodeRenderer

	private class YakeyeTreeFilter extends GraphDistanceFilterDirectedSteps {

		public YakeyeTreeFilter(String group, int distance) {
			super(group,distance,distance-1);
		}
	}

	public class GraphDistanceFilterDirectedSteps extends GroupAction {

		protected int m_successorDistance;
		protected int m_predecessorDistance;
		protected String m_sources;
		protected Predicate m_groupP;
		protected BreadthFirstIteratorDirectedSteps m_bfs;

		public GraphDistanceFilterDirectedSteps(String group) {
			this(group, 1,1);
		}

		public GraphDistanceFilterDirectedSteps(String group, int successorDistance, int predecessorDistance) {
			this(group, Visualization.FOCUS_ITEMS, successorDistance,predecessorDistance);
		}

		public GraphDistanceFilterDirectedSteps(String group, String sources, int successorDistance, int predecessorDistance)
		{
			super(group);
			m_sources = sources;
			m_predecessorDistance = predecessorDistance;
			m_successorDistance = successorDistance;
			m_groupP = new InGroupPredicate(
					PrefuseLib.getGroupName(group, Graph.NODES));
			m_bfs = new BreadthFirstIteratorDirectedSteps();
		}

		public int getPredecessorDistance() {
			return m_predecessorDistance;
		}

		public void setPredecessorDistance(int predecessorDistance) {
			m_predecessorDistance = predecessorDistance;
		}

		public int getSuccessorDistance() {
			return m_successorDistance;
		}

		public void setSuccessorDistance(int successorDistance) {
			m_successorDistance = successorDistance;
		}
		public String getSources() {
			return m_sources;
		}

		public void setSources(String sources) {
			m_sources = sources;
		}

		public void run(double frac) {
			// mark the items
			Iterator items = m_vis.visibleItems(m_group);
			while ( items.hasNext() ) {
				VisualItem item = (VisualItem)items.next();
				item.setDOI(Constants.MINIMUM_DOI);
			}

			// set up the graph traversal
			TupleSet src = m_vis.getGroup(m_sources);
			Iterator srcs = new FilterIterator(src.tuples(), m_groupP);
			m_bfs.init(srcs, m_successorDistance,m_predecessorDistance, Constants.NODE_AND_EDGE_TRAVERSAL);

			// traverse the graph
			while ( m_bfs.hasNext() ) {
				VisualItem item = (VisualItem)m_bfs.next();
				int ds = m_bfs.getSuccessorDepth(item);
				int dp = m_bfs.getPredecessorDepth(item);
				PrefuseLib.updateVisible(item, true);
				item.setDOI(-ds-dp);

				item.setExpanded(ds < m_successorDistance);

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

	public class BreadthFirstIteratorDirectedSteps implements Iterator {

		protected DirectedDepthQueue m_queue = new DirectedDepthQueue();

		protected int   m_successorDepth;
		protected int   m_predecessorDepth;

		protected int   m_traversal;
		protected boolean m_includeNodes;
		protected boolean m_includeEdges;

		public BreadthFirstIteratorDirectedSteps() {
			// do nothing, requires init call
		}

		public BreadthFirstIteratorDirectedSteps(Node n, int successorDepth, int predecessorDepth, int traversal) {
			init(new Node[] {n}, successorDepth, predecessorDepth, traversal);
		}

		public BreadthFirstIteratorDirectedSteps(Iterator it, int successorDepth, int predecessorDepth, int traversal) {
			init(it, successorDepth, predecessorDepth, traversal);
		}
		public void init(Object o, int successorDepth, int predecessorDepth, int traversal) {
			// initialize the member variables
			m_queue.clear();

			m_successorDepth = successorDepth;
			m_predecessorDepth = predecessorDepth;

			if ( traversal < 0 || traversal >= Constants.TRAVERSAL_COUNT )
				throw new IllegalArgumentException(
						"Unrecognized traversal type: "+traversal);
			m_traversal = traversal;
			m_includeNodes = (traversal == Constants.NODE_TRAVERSAL ||
					traversal == Constants.NODE_AND_EDGE_TRAVERSAL);
			m_includeEdges = (traversal == Constants.EDGE_TRAVERSAL ||
					traversal == Constants.NODE_AND_EDGE_TRAVERSAL);

			// seed the queue
			// TODO: clean this up? (use generalized iterator?)



			if ( m_includeNodes ) {
				if ( o instanceof Node ) {
					m_queue.add(o,0,0);
				} else {
					Iterator tuples = (Iterator)o;
					while ( tuples.hasNext() )
						m_queue.add(tuples.next(), 0,0);
				}
			} else {
				if ( o instanceof Node ) {
					Node n = (Node)o;
					markVisit(n);
				} else {
					Iterator tuples = (Iterator)o;
					while ( tuples.hasNext() ) {

						Node n = (Node)tuples.next();
						markVisit(n);
					}
				}
			}
		}

		// ------------------------------------------------------------------------

		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}

		public boolean hasNext() {
			return !m_queue.isEmpty();
		}

		protected Iterator getEdges(Node n) {
			return n.edges(); // TODO: add support for all edges, in links only, out links only
		}

		public int getSuccessorDepth(Tuple t) {
			return m_queue.getSuccessorDepth(t);
		}

		public int getPredecessorDepth(Tuple t) {
			return m_queue.getPredecessorDepth(t);
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public Object next() {
			Tuple t = (Tuple)m_queue.removeFirst();

			switch ( m_traversal ) {

				case Constants.NODE_TRAVERSAL:
				case Constants.NODE_AND_EDGE_TRAVERSAL:
					for ( ; true; t = (Tuple)m_queue.removeFirst() ) {
						if ( t instanceof Edge ) {
							return t;
						} else {
							Node n = (Node)t;

							int sd = m_queue.getSuccessorDepth(n);

							if ( sd < m_successorDepth ) {
								int dd = sd+1;
								Iterator edges = n.outEdges();
								while ( edges.hasNext() ) {
									Edge e = (Edge)edges.next();
									Node v = e.getAdjacentNode(n);

									if ( m_includeEdges && m_queue.getSuccessorDepth(e) < 0 )
										m_queue.add(e, dd,m_queue.getPredecessorDepth(n));
									if ( m_queue.getSuccessorDepth(v) < 0 )
										m_queue.add(v,dd,m_queue.getPredecessorDepth(n));
								}
							}


							int pd = m_queue.getPredecessorDepth(n);

							if ( pd < m_predecessorDepth ) {
								int dd = pd+1;
								Iterator edges = n.inEdges();
								while ( edges.hasNext() ) {
									Edge e = (Edge)edges.next();
									Node v = e.getAdjacentNode(n);

									if ( m_includeEdges && m_queue.getPredecessorDepth(e) < 0 )
										m_queue.add(e, m_queue.getSuccessorDepth(n),dd);
									if ( m_queue.getPredecessorDepth(v) < 0 )
										m_queue.add(v, m_queue.getSuccessorDepth(n),dd);
								}
							}


							else if ( m_includeEdges) {
								sd = m_queue.getSuccessorDepth(n);
								if( sd == m_successorDepth ) {
									Iterator edges = n.outEdges();
									while ( edges.hasNext() ) {
										Edge e = (Edge)edges.next();
										Node v = e.getAdjacentNode(n);
										int dv = m_queue.getSuccessorDepth(v);
										if ( dv > 0 && m_queue.getSuccessorDepth(e) < 0 ) {
											int un = m_queue.getPredecessorDepth(n);
											int uv = m_queue.getPredecessorDepth(v);
											if (sd+un<dv+uv) {
												m_queue.add(e, sd,un);
											} else if (sd+un>dv+uv) {
												m_queue.add(e, dv,uv);
											} else {
												if (sd < dv ) {
													m_queue.add(e, sd,un);
												} else {
													m_queue.add(e, dv,uv);
												}
											}
										}
									}
								}
								pd = m_queue.getPredecessorDepth(n);
								if( pd == m_predecessorDepth ) {
									Iterator edges = n.inEdges();
									while ( edges.hasNext() ) {
										Edge e = (Edge)edges.next();
										Node v = e.getAdjacentNode(n);
										int dv = m_queue.getPredecessorDepth(v);
										if ( dv > 0 && m_queue.getPredecessorDepth(e) < 0 ) {
											int un = m_queue.getSuccessorDepth(n);
											int uv = m_queue.getSuccessorDepth(v);
											if (un+pd<uv+dv) {
												m_queue.add(e, un,pd);
											} else if (un+pd>uv+dv) {
												m_queue.add(e, uv,dv);
											} else {
												if (un < uv ) {
													m_queue.add(e, un,pd);
												} else {
													m_queue.add(e, uv,dv);
												}
											}
										}
									}
								}
							}
							return n;
						}
					}

				case Constants.EDGE_TRAVERSAL:
					Edge e = (Edge)t;
					Node u = e.getSourceNode();
					Node v = e.getTargetNode();

					int du = m_queue.getSuccessorDepth(u);
					int dv = m_queue.getSuccessorDepth(v);

					if ( du != dv ) {
						Node n = (dv > du ? v : u);
						int d1, d2;
						if (du>dv) {
							d1=du;
							d2=m_queue.getPredecessorDepth(u);
						} else {
							d1=dv;
							d2=m_queue.getPredecessorDepth(v);
						}


						if ( d1 < m_successorDepth ) {
							int dd = d1+1;
							Iterator edges = n.outEdges();
							while ( edges.hasNext() ) {
								Edge ee = (Edge)edges.next();
								if ( m_queue.getSuccessorDepth(ee) >= 0 )
									continue; // already visited

								Node nn = ee.getAdjacentNode(n);
								m_queue.visit(nn, dd,d2);
								m_queue.add(ee, dd,d2);
							}
						}


						du = m_queue.getPredecessorDepth(u);
						dv = m_queue.getPredecessorDepth(v);

						if ( du != dv ) {
							n = (dv > du ? v : u);

							if (du>dv) {
								d1=du;
								d2=m_queue.getSuccessorDepth(u);
							} else {
								d1=dv;
								d2=m_queue.getSuccessorDepth(v);
							}


							if ( d1 < m_predecessorDepth ) {
								int dd = d1+1;
								Iterator edges = n.inEdges();
								while ( edges.hasNext() ) {
									Edge ee = (Edge)edges.next();
									if ( m_queue.getPredecessorDepth(ee) >= 0 )
										continue; // already visited

									Node nn = ee.getAdjacentNode(n);
									m_queue.visit(nn, d2,dd);
									m_queue.add(ee, d2,dd);
								}
							}
						}

					}

					return e;

				default:
					throw new IllegalStateException();
			}
		}

		private void markVisit(Node n) {
			m_queue.visit(n,0,0);
			Iterator edges = n.edges();
			while ( edges.hasNext() ) {
				Edge e = (Edge)edges.next();
				Node nn = e.getAdjacentNode(n);

				if (nn == e.getTargetNode()) {

					m_queue.visit(nn, 1,0);
					if ( m_queue.getSuccessorDepth(e) < 0 ) {
						m_queue.add(e,1,0);
					}
				} else {
					m_queue.visit(nn, 0,1);
					if ( m_queue.getPredecessorDepth(e) < 0 ) {
						m_queue.add(e,0,1);
					}
				}
			}

		}
	} // end of class BreadthFirstIterator
	public class DirectedDepthQueue {

		private LinkedList m_list = new LinkedList();
		private HashMap    m_map  = new HashMap();

		public void clear() {
			m_list.clear();
			m_map.clear();
		}

		public boolean isEmpty() {
			return m_list.isEmpty();
		}

		public void add(Object o, int downDepth, int upDepth) {
			m_list.add(o);
			m_map.put(o,getArray(downDepth,upDepth));
		}


		public void visit(Object o,int downDepth, int upDepth) {
			m_map.put(o, getArray(downDepth,upDepth));
		}

		public int getSuccessorDepth(Object o) {
			int[] d = (int[])m_map.get(o);
			return ( d==null ? -1 : d[0] );
		}

		public int getPredecessorDepth(Object o) {
			int[] d = (int[])m_map.get(o);
			return ( d==null ? -1 : d[1] );
		}


		public Object removeFirst() {
			return m_list.removeFirst();
		}

		public Object removeLast() {
			return m_list.removeLast();
		}

		private int[] getArray(int a, int b) {
			int[] d = new int[2];
			d[0] = a;
			d[1] = b;
			return d;
		}

	} // end of class Queue


} // end of class TreeMap