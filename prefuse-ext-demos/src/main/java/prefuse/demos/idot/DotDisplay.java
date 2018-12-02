package prefuse.demos.idot;

import prefuse.demos.idot.util.DotFileReader;
import prefuse.demos.idot.util.InvertedWheelZoomControl;
import prefuse.demos.idot.util.MultilineTextItemRenderer;
import prefuse.demos.idot.util.ShapeDecoder;
import prefuse.demos.idot.util.TransitionRenderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.assignment.StrokeAction;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.NullRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * A Display component used to show state diagram visualizations. 
 * 
 * Loosely based on a sample graph editor in the 
 * prefuse toolkit (alpha version).
 */
public class DotDisplay extends Display {
        public static Controller controller;
        /** name of the main graph as used by prefuse */ 
	public static final String GRAPH_GROUP = "graph";
	
	/** name of the group containing the edges of the graph */
	public static final String GRAPH_EDGES = "graph.edges";
	
	/** name of the group containing the nodes of the graph */
	public static final String GRAPH_NODES = "graph.nodes";
	
	// node and edge attribute names
	
	/** name of the attribute storing the label of states */
	public static final String NODE_LABEL = "label";
	
	/** name of the attribute storing the control points for curved transitions */
	public static final String EDGE_COORDS = "coords";
	
	/** 
	 * name of the attribute telling whether a state or 
	 * transition has been explicitly clicked open 
	 */
	public static final String EXPLICIT_SHOW = "explicitShow";

    /** the current font used for drawing the node labels */
    private Font currFont = FontLib.getFont(Config.NODE_FONT_NAME, 
    		Config.NODE_FONT_STYLE, Config.NODE_FONT_DEFAULT_SIZE);

	/** the contents of the current graph (in DOT format) */
	private String dotFileContents;

	/** 
	 * Used by {@link #storeVisibleState()} and {@link #restoreVisibleState()}
	 * to store the names of the nodes that are visible before a major graph 
	 * modification (such as pressing the apply button in the editor window)
	 * and restoring the visibility to that state after the operation. 
	 */
	private HashSet<String> explicitlyShownNodes;
	private HashSet<String> visibleNodes;

	/** the command and parameters for executing dot */
	private String[] dotCommand = Config.DOT_COMMAND;

	/** the reader used during layout operations */
	DotFileReader dotReader = new DotFileReader();

	
    /**
     * Creates a new DotDisplay initialized with an empty graph
     */
    public DotDisplay() {
    	this(new Graph(true));
    }
    
    /**
     * Creates a new DotDisplay initialized with the given graph
     * 
     * @param graph the graph to load to the display
     */
    public DotDisplay(Graph graph) {
    	this(graph, new Visualization());
    }
    
    /**
     * Creates a new DotDisplay initialized the given graph and
     * visualization
     * 
     * @param graph  the graph to load to the display
     * @param visualization  the visualization that will be used provide the 
     *     visual counterparts for the objects in the graph
     */
    public DotDisplay(Graph graph, Visualization visualization) {
    	super();
    	setHighQuality(true);
    	
    	setVisualization(visualization);
    	
    	// initialize renderers
    	MultilineTextItemRenderer nodeRenderer = 
    		new MultilineTextItemRenderer(MultilineTextItemRenderer.CENTER);    	    	
    	TransitionRenderer edgeRenderer = new TransitionRenderer();

    	DefaultRendererFactory rf = new DefaultRendererFactory(nodeRenderer, edgeRenderer);
    	
    	rf.add("INGROUP('edgeLabels')", new LabelRenderer() {
    	    public void render(Graphics2D g, VisualItem item) {

    	    	// make sure edge labels are at correct positions before rendering them
    	    	if(item.canGet("lp2", double[].class)) {
    	    		double[] xy = (double[])item.get("lp2");
    	    		if(xy != null) {
    	    			item.setX(xy[0]);
    	    			item.setY(xy[1]);
    	    			super.render(g, item);
    	    		}
    	    	}
    	    }
    	});
    	
    	// hide invisible nodes and edges
    	rf.add("style='invis'", new NullRenderer());    	
    	m_vis.setRendererFactory(rf);
    	
    	// initialize display
    	setBackground(Color.WHITE);

    	addControlListeners();
    	addLayouts();

    	setGraph(graph);
    }
	
    /**
     * Sets the contents of the graph to be used in subsequent layout
     * and visualization operations.
     * 
     * @param contents  the new contents for the graph
     */
    protected void setDotFileContents(String contents) {
    	dotFileContents = contents;
    }

    /**
     * Returns the contents of the current graph
     * 
     *  @return  the current graph in DOT format
     */
    protected String getDotFileContents() {
    	return dotFileContents;
    }
    
    /**
     * Adds the needed control listeners and action lists to the display.
     */
	private void addControlListeners() {
		// update takes care of colors and font sizes
		ActionList update = new ActionList(m_vis);
		
		ColorAction color = new ColorAction(GRAPH_GROUP, VisualItem.STROKECOLOR) {
			@Override
			public int getColor(VisualItem item) {
				// honor the value of the color attribute
				if(item.canGetInt("color")) {
					return item.getInt("color");
				}
				return super.getColor(item);
			}			
		};
		// base color is black
		color.setDefaultColor(ColorLib.rgb(0, 0, 0));
		update.add(color);
		
		// text is always black
		color = new ColorAction(GRAPH_GROUP, VisualItem.TEXTCOLOR);
		color.setDefaultColor(ColorLib.gray(0));
		update.add(color);
		
		// arrow heads are filled with color from the attribute, and nodes with white
		color = new ColorAction(GRAPH_GROUP, VisualItem.FILLCOLOR) {
			@Override
			public int getColor(VisualItem item) {
				// honor the value of the color attribute
				if(item instanceof EdgeItem && item.canGetInt("color")) {
					return item.getInt("color");
				} else if(item instanceof NodeItem 
						&& item.getString("shape").equalsIgnoreCase("point")) {
					return item.getStrokeColor();
				}
				return super.getColor(item);
			}
		};
		
		color.add("ingroup('"+ GRAPH_EDGES +"')",  ColorLib.gray(0));
		
		color.setDefaultColor(ColorLib.gray(255));
		update.add(color);
		
		// hilighted nodes and edges are drawn with doubled lines
//		StrokeAction stroke = new StrokeAction(GRAPH_GROUP);		
//		stroke.add("_highlight", StrokeLib.getStroke(2));		
//		update.add(stroke);
		
		update.add(new FontAction() {
			public Font getFont(VisualItem item) {
				String fontname = currFont.getFamily();
				int fontsize = currFont.getSize();
				
				if(item.canGetString("fontname")) {
					fontname = item.getString("fontname");
				}
				
				if(item.canGetInt("fontsize")) {
					fontsize = item.getInt("fontsize");
				}
				
				return FontLib.getFont(fontname, fontsize);
				
				// return currFont;
			} //
		});
		
		ShapeDecoder sd = new ShapeDecoder("shape");
		update.add(sd);
		
		StrokeAction sa = new StrokeAction(GRAPH_GROUP, new BasicStroke());
		sa.add("style='bold'", new BasicStroke(2));
		
		// this is alternative to using a NullRenderer, although this is not completely invisible
		// sa.add("style='invis'", new BasicStroke(0));
		update.add(sa);
		
		update.add(new RepaintAction());
		m_vis.putAction("update", update);
				
		addControlListener(new ZoomControl());
		addControlListener(new PanControl());
		addControlListener(new InvertedWheelZoomControl());			
		addControlListener(new FocusControl(0));
                Controller c = new Controller();
                controller = c;
		addControlListener(c);
	}
	
	/**
	 * Adds the possible layouts. Currently only the dot based layout
	 * is implemented.
	 */
	private void addLayouts() {
            ActionList dotLayout = new ActionList(m_vis);
            dotLayout.add(new DotLayout());
            dotLayout.add(m_vis.getAction("update"));
            Action cursorRestore = new Action() {
				// restore cursor after layout
				public void run(double frac) {
					try {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								setCursor(Cursor.getDefaultCursor());			
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}            	
            };
            
            dotLayout.add(cursorRestore);
            
            dotLayout.add(new Action() {			
				@Override
				public void run(double frac) {
					Iterator iter = m_vis.items("edgeLabels");
		            while ( iter.hasNext() ) {
		                DecoratorItem item = (DecoratorItem)iter.next();
		                
		                // update edge label positions immediately after layout
		    	    	if(item.canGet("lp2", double[].class)) {
		    	    		double[] xy = (double[])item.get("lp2");
		    	    		if(xy != null) {
		    	    			//if (Config.print) System.out.println("set item " + item + " at " + xy[0] + "," + xy[1]);
		    	    			item.setX(xy[0]);
		    	    			item.setY(xy[1]);
		    	    		}
		    	    	}
		            }
				}
			});
            
            m_vis.putAction("dotLayout", dotLayout);
	} //
	
    
	/**
	 * Input controller for interacting with the visualization.
	 */
	private class Controller extends ControlAdapter 
		implements MouseListener, KeyListener, ActionListener {
		
		/** last location where mouse button was pressed down */
		private int xDown, yDown;
		
		/* mouse has entered an item */
		public void itemEntered(VisualItem item, MouseEvent e) {
			// item = getRealItem(item);
			// if (item instanceof NodeItem) {
				// showNodeToolTip((NodeItem) item);
			// } else if(item instanceof EdgeItem) {
				// showEdgeToolTip((EdgeItem) item);
			// }
		} //
		
		/**
		 * Because the DecoratorItems are set interactive, most mouse
		 * actions happen first on them, but the code expects to handle 
		 * NodeItems and EdgeItems directly. This method returns the item behind
		 * the item if it is a DecoratorItem, or the item itself, if it is a
		 * normal item. 
		 * 
		 * @param item
		 * @return
		 */
		private VisualItem getRealItem(VisualItem item) {
			if(item instanceof DecoratorItem) {
				return ((DecoratorItem)item).getDecoratedItem();
			}
			return item;
		}

		/**
		 * Could be used to show information about an edge in a tooltip
		 * 
		 * @param item the edge
		 */
//		private void showEdgeToolTip(EdgeItem item) {
			//setToolTipText(item.getAttribute(...));
//		}
		
		/**
		 * Could show information about a node in a tooltip.
		 * Currently nothing is shown (what should it show?).
		 *  
		 * @param item the node
		 */
//		private void showNodeToolTip(NodeItem item) {
			// hmm..., what to show for nodes
			//setToolTipText(item.getAttribute(...));	
//		}

		/* mouse has exited an item */
		public void itemExited(VisualItem item, MouseEvent e) {
//			setToolTipText(null);
		} //
		
		/* mouse button was pressed on an item */
		public void itemPressed(VisualItem item, MouseEvent e) {
			showItemMenu(item, e);
			
			if ( !e.isControlDown() ) {
			    xDown = e.getX();
			    yDown = e.getY();
			    m_vis.run("update");
			    
                item.setFixed(true);
            }
		} //
		
		/* mouse button was released on an item */
		public void itemReleased(VisualItem item, MouseEvent e) {
			showItemMenu(item, e);
			
			item.setFixed(false);
		} //

		/* mouse was clicked on an item */
		public void itemClicked(VisualItem item, MouseEvent e) {
			item = getRealItem(item);
			// double click "opens" a node
			if(e.getClickCount() == 2 && item instanceof NodeItem) {
				NodeItem ni = (NodeItem) item;
				Iterator edges = ni.outEdges();
				while(edges.hasNext()) {
					EdgeItem ei = (EdgeItem) edges.next();
					NodeItem other = ei.getAdjacentItem(ni);
					ei.setBoolean(EXPLICIT_SHOW, true);
					ei.setVisible(true);
					if(! other.getBoolean(EXPLICIT_SHOW)) {
						other.setBoolean(EXPLICIT_SHOW, true);
						other.setVisible(true);												
					}
				}
				
				m_vis.run("update");
			}
		}
		
		/** the menu shown after right clicking on the display (not on an item) */
//		private JPopupMenu bgMenu;
		
		/** 
		 * a toggle indicating whether the next press of ESC should 
		 * make visible all items or only those that have been explicitly
		 * clicked open 
		 */
		private boolean escapeShowsAll = true;
		
		/**
		 * Shows the right click menu for an item. This method builds the 
		 * popup menu dynamically based on the clicked-on item.
		 * 
		 * @param item the item for which to open the menu
		 * @param e the original mouse event
		 */
                 private void showItemMenu(VisualItem item, MouseEvent e) {}			
/*
                 private void showItemMenu(VisualItem item, MouseEvent e) {			
			if(e.isPopupTrigger()) {
				JPopupMenu pm = new JPopupMenu("filters");

				// add the showAllFilter to the menu

				JMenuItem mi = new JMenuItem("Show all");
				mi.setActionCommand("filter_0");
				mi.addActionListener(this);

				pm.add(mi);
				pm.show(e.getComponent(), e.getX(), e.getY());
			}
		}
*/
		/* item was dragged */
		public void itemDragged(VisualItem item, MouseEvent e) {
			if(item instanceof DecoratorItem) {
				VisualItem realItem = ((DecoratorItem)item).getDecoratedItem();
						
	            if(realItem instanceof EdgeItem) {
	            	itemDragged(realItem, e);
	            }
				
			} else if (item instanceof EdgeItem) {
				// simulate the drag of end points
				EdgeItem ei = (EdgeItem) item;
				updateNodePos((NodeItem) ei.getSourceNode(), e);
				if(ei.getSourceNode() != ei.getTargetNode())
					updateNodePos((NodeItem) ei.getTargetNode(), e);
							
			} else if (item instanceof NodeItem) {
				updateNodePos(item, e);
			}
			
			m_vis.run("update");
			
			xDown = e.getX();
			yDown = e.getY();
		} //

		/**
		 * Updates the location of a node in response to a drag event
		 * 
		 * @param item the moved node
		 * @param e the original mouse event
		 */
		private void updateNodePos(VisualItem item, MouseEvent e) {
			double oldx = item.getX();
			double oldy = item.getY();
			
			double x = oldx + (e.getX() - xDown)/getScale();
			double y = oldy + (e.getY() - yDown)/getScale();
            item.setX(x);
            item.setY(y);
			
            
            
            updateEdgeCoords((NodeItem) item, oldx, oldy, x, y);
		}
		
		/**
		 * Updates the locations of control points for edges attached to a node.
		 * 
		 * @param item the moved node
		 * @param oldX the old x coordinate of the node
		 * @param oldY the old y coordinate of the node
		 * @param newX the new x coordinate of the node
		 * @param newY the new y coordinate of the node
		 */
		private void updateEdgeCoords(NodeItem item, double oldX, 
				double oldY, double newX, double newY) {
			// update the "coords" of connecting edges if set
			AffineTransform at = new AffineTransform();
			
			Graph g = ((Graph) m_vis.getVisualGroup(GRAPH_GROUP));
			Iterator edges = g.edges(item);
			while(edges.hasNext()) {
				EdgeItem ei = (EdgeItem) edges.next();
				NodeItem neighborNode = (NodeItem) g.getAdjacentNode(ei, item);
				
				double nX = neighborNode.getX(), nY = neighborNode.getY();
				
				if(ei.get(EDGE_COORDS) != null) {
					Rectangle2D r = ei.getBounds(); 
						//((TransitionRenderer)ei.getRenderer()).getShape(ei).getBounds2D();
					damageReport(r);
					
					if(item != neighborNode) {
						double angle = 
							Math.atan2(newY-nY, newX-nX) - 
							Math.atan2(oldY-nY, oldX-nX);	
						double dist = Point2D.distance(nX, nY, newX, newY) / 
							Point2D.distance(nX, nY, oldX, oldY);
						
						at.setToTranslation(nX, nY);
						at.rotate(angle);
						at.scale(dist, dist);
						at.translate(-nX, -nY);
						
						double[] coords = (double[]) ei.get(EDGE_COORDS);
						int n = coords.length;
						
						at.transform(coords, 0, coords, 0, n/2);
						
						if(ei.canGet("lp2", double[].class)) {
							double[] labelxy = (double[]) ei.get("lp2");
							if(labelxy != null)
								at.transform(labelxy, 0, labelxy, 0, 1);
						}
					} else {
						// self loop - only move
						
						double dx = newX - oldX;
						double dy = newY - oldY;
						
						// this edge will be considered twice - because it is leading
						// both in and out from this node - so do half of the job at time 
						at.setToTranslation(dx/2., dy/2.);
						double[] coords = (double[]) ei.get(EDGE_COORDS);
						at.transform(coords, 0, coords, 0, coords.length/2);
						
						if(ei.canGet("lp2", double[].class)) {
							double[] labelxy = (double[]) ei.get("lp2");
							if(labelxy != null)
								at.transform(labelxy, 0, labelxy, 0, 1);
						}
					}					
				}
			}
		}
		
		/* mouse is dragged */
		public void mouseDragged(MouseEvent e) {
			// if(bgMenu != null && bgMenu.isVisible())
				// bgMenu.setVisible(false);
	    }
				
		public void mouseMoved(MouseEvent e) {}
		
		/* key is typed while mouse is over an item */
		public void itemKeyTyped(VisualItem item, KeyEvent e) {
			// keyTyped(e);
		}
		
		/* key is typed - check for ESC */
/*
		public void keyTyped(KeyEvent e) {
			// back to "show all" or show only those opened by dbl-click with escape
			// After this "export filtered graph" still gives
			// only the last "real" filtered graph
			if(e.getKeyChar() == KeyEvent.VK_ESCAPE) {
				if(escapeShowsAll) {
					m_vis.setVisible(GRAPH_GROUP, null, true);
				} else {
					showOnlyOpened();
				}
				m_vis.run("update");
				escapeShowsAll = ! escapeShowsAll; // toggle
			}
		}
*/
		// menu callbacks

		/**
		 * Handles the menu actions for the right click menus.
		 * 
		 * @param e the event that happened
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals(iDot.SHOW_OPENED)) {
			    showOnlyOpened();
                            iDot.toolShow.setText(iDot.SHOW_ALL);
                        }
                        else if(cmd.equals(iDot.SHOW_ALL)) {
			    m_vis.setVisible(GRAPH_GROUP, null, true);
                            iDot.toolShow.setText(iDot.SHOW_OPENED);
                        }
			  m_vis.run("update");
                }
/*
                  public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if(cmd.equals("filter_0") || "bgExpandAll".equals(cmd)) { 
				m_vis.setVisible(GRAPH_GROUP, null, true);
				m_vis.run("update");
				                
			} else if("bgInitialize".equals(cmd)) {				
				explicitHide((Graph) m_vis.getVisualGroup(GRAPH_GROUP));				
				m_vis.run("update");
                        } else {
				throw new IllegalStateException("menu action not implemented");
			}
		} //
*/

public void mouseClicked(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {
//			showBackgroundPopup(e);
		}

		public void mouseReleased(MouseEvent e) {			
//			showBackgroundPopup(e);
		}

		// private void showBackgroundPopup(MouseEvent e) {
			// if(bgMenu == null) {
				// bgMenu = new JPopupMenu("background actions");
				// 
				// JMenuItem mi = new JMenuItem("Initialize", KeyEvent.VK_I);
				// bgMenu.add(mi);
				// mi.setActionCommand("bgInitialize");
				// mi.addActionListener(this);
				// 
				// mi = new JMenuItem("Expand all");
				// bgMenu.add(mi);
				// mi.setActionCommand("bgExpandAll");
				// mi.addActionListener(this);
			// }
			// 
			// if(e.isPopupTrigger())
				// bgMenu.show(DotDisplay.this, e.getX(), e.getY());
		// }

		public void mouseEntered(MouseEvent e) {}		
		public void mouseExited(MouseEvent e) {}
						
	} // end of inner class Controller

	/**
	 * Schedules the dot layout to be performed immediately
	 */
	private void runDotLayout() {
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    m_vis.run("dotLayout");	    
	} //


	/**
	 * Adds the field to a table if not already present
	 *  
	 * @param table  the table to add the field to
	 * @param field  the field to add
	 * @param type   the class of the field
	 */
	private void addField(Table table, String field, Class type) {
		if(!table.canSet(field, type)) {
			if(table.getColumnNumber(field) != -1) {
				System.err.println("field " + field + " has wrong type, fixing");
				table.removeColumn(field);
			}
			
			table.addColumn(field, type);			
		}
	}

	/**
	 * Sets the graph shown on this display.
	 * 
	 * @param g
	 */
	public void setGraph(Graph g) {		
        // update graph
		m_vis.removeGroup(GRAPH_GROUP);
        m_vis.addGraph(GRAPH_GROUP, g);
        
        // create data description of labels, setting colors, fonts ahead of time        
        Schema edgeLabelSchema = createEdgeLabelSchema();
                
        m_vis.removeGroup("edgeLabels");
        explicitHide((Graph) m_vis.getVisualGroup(GRAPH_GROUP));

        m_vis.addDecorators("edgeLabels", GRAPH_EDGES, null, edgeLabelSchema);
        
		runFilterUpdate();
		runLayout();
	}

	/**
	 * Creates the schema used while creating the decorator items
	 * for edge labels.
	 * In short, schema determines the columns that are created for
	 * each edge label item. The columns ("attributes") that are not
	 * defined here, are inherited from the decorated edge.
	 * For example, the visibility is inherited from the decorated edge,
	 * and it doesn't have to be set manually for the labels.
	 * 
	 * @return the schema for edge labels
	 */
	private Schema createEdgeLabelSchema() {
        Schema s = new Schema();
        
        // booleans
        s.addColumn(VisualItem.VALIDATED, boolean.class, Boolean.FALSE);
        s.addColumn(VisualItem.INTERACTIVE, boolean.class, Boolean.TRUE);
        s.addColumn(VisualItem.EXPANDED, boolean.class, Boolean.TRUE);
        s.addColumn(VisualItem.FIXED, boolean.class, Boolean.FALSE);
        s.addColumn(VisualItem.HIGHLIGHT, boolean.class, Boolean.FALSE);
        s.addColumn(VisualItem.HOVER, boolean.class, Boolean.FALSE);
        
        // bounding box
        s.addColumn(VisualItem.BOUNDS, Rectangle2D.class, new Rectangle2D.Double());
        
        // color
        Integer defStroke = new Integer(ColorLib.rgba(0,0,0,0));
        s.addInterpolatedColumn(VisualItem.STROKECOLOR, int.class, defStroke);

        Integer defFill = new Integer(ColorLib.rgba(0,0,0,0));
        s.addInterpolatedColumn(VisualItem.FILLCOLOR, int.class, defFill);

        Integer defTextColor = new Integer(ColorLib.rgb(0,0,0)); //,0));
        s.addInterpolatedColumn(VisualItem.TEXTCOLOR, int.class, defTextColor);

        // size
        s.addInterpolatedColumn(VisualItem.SIZE, double.class, new Double(1));
        
        // shape
        s.addColumn(VisualItem.SHAPE, int.class,
            new Integer(Constants.SHAPE_RECTANGLE));
        
        // stroke
        s.addColumn(VisualItem.STROKE, Stroke.class, new BasicStroke());
        
        // font
        Font defFont = FontLib.getFont("SansSerif",Font.PLAIN,10);
        s.addInterpolatedColumn(VisualItem.FONT, Font.class, defFont);

        return s;
	}

	/**
	 * Initially show only the "root" state.
	 * 
	 * @param graph
	 */
	private void explicitHide(Graph graph) {
		addField(graph.getNodeTable(), EXPLICIT_SHOW, boolean.class);
		addField(graph.getEdgeTable(), EXPLICIT_SHOW, boolean.class);
		m_vis.setValue(GRAPH_NODES, null, EXPLICIT_SHOW, false);
		m_vis.setValue(GRAPH_EDGES, null, EXPLICIT_SHOW, false);
		m_vis.setVisible(GRAPH_GROUP, null, false);
		if(graph.getNodeCount() > 0) {
			NodeItem rootNode = ((NodeItem) graph.getNode(0)); 
			rootNode.setVisible(true);
			rootNode.set(EXPLICIT_SHOW, true);
		}
	}

	/**
	 * Stores the names of the nodes that are currrently set visible
	 * for later use by {@link #restoreVisibleState()}.
	 */
	public void storeVisibleState() {
		explicitlyShownNodes = new HashSet<String>();		
		Iterator items = getVisualization().items(GRAPH_NODES, EXPLICIT_SHOW + "=true");
		while(items.hasNext()) {
			Node n = (Node) items.next();
			explicitlyShownNodes.add(n.getString("nodename"));
		}
		
		visibleNodes = new HashSet<String>();
		items = getVisualization().visibleItems(GRAPH_NODES);
		while(items.hasNext()) {
			Node n = (Node) items.next();
			visibleNodes.add(n.getString("nodename"));
		}
	}

	/**
	 * Restores the visibility of nodes, and partially also edges, to the state
	 * during the last call to {@link #storeVisibleState()}. 
	 * If the node names haven't changed in between, this helps to keep 
	 * the general appearance of the graph consistent while clicking the
	 * "Apply changes" button in the dot editor.
	 */
	public void restoreVisibleState() {
		Graph g = (Graph) getVisualization().getVisualGroup(GRAPH_GROUP);
		Iterator items = g.nodes();
		while(items.hasNext()) {
			NodeItem n = (NodeItem) items.next();
			if(explicitlyShownNodes.contains(n.getString("nodename"))) {
				n.set(EXPLICIT_SHOW, true);
			} else {
				n.set(EXPLICIT_SHOW, false);
			}
			
			if(visibleNodes.contains(n.getString("nodename"))) {
				n.setVisible(true);
			} else {
				n.setVisible(false);
			}
		}
		
		items = g.edges();
		while(items.hasNext()) {
			EdgeItem e = (EdgeItem) items.next();
			NodeItem from = e.getSourceItem();
			NodeItem to = e.getTargetItem();
			
			if(from.getBoolean(EXPLICIT_SHOW) && to.getBoolean(EXPLICIT_SHOW)) {
				e.set(EXPLICIT_SHOW, true);
			} else {
				e.set(EXPLICIT_SHOW, false);
			}
			
			if(from.isVisible() && to.isVisible()) {
				e.setVisible(true);
			} else {
				e.setVisible(false);
			}

		}
		
		if(g.getNodeCount() > 0) {
			NodeItem rootNode = ((NodeItem) g.getNode(0)); 
			rootNode.setVisible(true);
			rootNode.set(EXPLICIT_SHOW, true);
		}
	}

	
	/**
	 * Shows only those nodes and edges whose neighbor (parent) node has
	 * been double-clicked.
	 */
	public void showOnlyOpened() {
		m_vis.setVisible(GRAPH_GROUP, null, false);
		m_vis.setVisible(GRAPH_GROUP, 
				(Predicate) ExpressionParser.parse(EXPLICIT_SHOW + "= true"), true);
	}
	
	/**
	 * Updates the view making sure all the colors etc. are correct.
	 */
	public void runFilterUpdate() {
		m_vis.run("update");
	}

	/**
	 * Runs the layout. All nodes and edges are moved to the positions determined
	 * by the used layout algorithm. Currently the layout is performed by dot.
	 */
	public void runLayout() {
		runDotLayout();
	}
	
	/**
	 * Returns the graph this display is showing.
	 * 
	 * @return the graph this display is showing
	 */
	public Graph getGraph() {
		return (Graph) m_vis.getSourceData(GRAPH_GROUP);
	}
	
	/**
	 * Sets the font used by the display
	 * 
	 * @param f the font to use
	 */
	public void setFont(Font f) {
		super.setFont(f);
		currFont = f;
	}

	/**
	 * Tries to perform the layout of the current graph by executing DOT
	 * with the current graph content (in DOT format) as input.
	 * If DOT is executed succesfully, the graph is lo9aded
	 */
	protected void runDOTLayout() {
		Graph g = null;
		try {							 
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			ProcessBuilder pb = new ProcessBuilder(dotCommand);
			/* If dot gives a lot of warnings the error stream
			 * may get full which hangs the program --> have to empty
			 * it somehow.
			 * Reading it through getErrorStream() would require
			 * some extra effort.
			 */
			pb.redirectErrorStream(true);
			Process p = pb.start();
			
			OutputStream os = new BufferedOutputStream(p.getOutputStream());			
			InputStream is = new BufferedInputStream(p.getInputStream());

			// pipe graph to dot

			os.write(getDotFileContents().getBytes());
			
			// dot will only close after getting EOF
			os.close();
			// pipe output to graph layout
	
			g = dotReader.loadGraph(is);

			p.waitFor();
			if(p.exitValue() != 0) {
				System.err.println("Process " + dotCommand + " exited with value " + p.exitValue());
				throw new IOException("DOT exit value: " + p.exitValue());
			}
			
			setGraph(g);

		} catch ( Exception ex ) {
			JOptionPane.showMessageDialog(
					this,
					"Sorry, an error occurred while running DOT.\n" +
					"("+ ex.getLocalizedMessage() + ")",
					"Error Running DOT",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
			
			setCursor(Cursor.getDefaultCursor());
		}
	}	
	
} // end of class DotDisplay
