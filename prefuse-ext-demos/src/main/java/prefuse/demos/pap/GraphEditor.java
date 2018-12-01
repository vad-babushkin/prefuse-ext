package prefuse.demos.pap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.*;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.ui.UILib;
import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;


/**
 * Demonstration of graph editor functionality.
 * See https://sourceforge.net/forum/forum.php?thread_id=1597565&forum_id=343013
 * for a discussion about the rubberband/drag select/multiple selection of
 * nodes, while the following thread 
 * https://sourceforge.net/forum/message.php?msg_id=3758973&forum_id=343013
 * contains the discussion about drawing edges interactively.
 *
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author Aaron Barsky
 * @author Bj�rn Kruse
 */
public class GraphEditor extends Display {

    private static final String graphNodesAndEdges = "graph";
    private static final String graphNodes = "graph.nodes";
    private static final String graphEdges = "graph.edges";
	private static final String RUBBER_BAND = "rubberband";
	private static final String NODES = graphNodes;
	private static final String SELECTED = "sel";
    
    private LabelRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;
    
    /*
     * 1	=	create a node
     * 2	=	create an edge
     * 3	=	rename node
     */
    private static int action = 1;
    
    
    private Graph g;
    
    public GraphEditor() {
        super(new Visualization());

        initDataGroups();
        
        // -- set up visualization --
        m_vis.add(graphNodesAndEdges, g);
        
        // -- set up renderers --
        m_nodeRenderer = new LabelRenderer(VisualItem.LABEL);
        m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
        m_nodeRenderer.setRoundedCorner(8,8);
        m_edgeRenderer = new EdgeRenderer();
        
        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(graphEdges), m_edgeRenderer);
        m_vis.setRendererFactory(rf);
               
        // -- set up processing actions --
        
        // colors
        ColorAction nodeTextColor = new ColorAction(graphNodes, 
        		VisualItem.TEXTCOLOR);
        ColorAction nodeFillColor = new ColorAction(graphNodes, 
        		VisualItem.FILLCOLOR, ColorLib.rgb(234,234,234));
        nodeFillColor.add("_hover", ColorLib.rgb(220,200,200));
        nodeFillColor.add(VisualItem.HIGHLIGHT, ColorLib.rgb(220,220,0));
        ColorAction nodeStrokeColor = new ColorAction(graphNodes,
        		VisualItem.STROKECOLOR);
        
        ColorAction edgeLineColor = new ColorAction(graphEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(200,200,200));
        edgeLineColor.add("_hover", ColorLib.rgb(220,100,100));
        ColorAction edgeArrowColor = new ColorAction(graphEdges,
        		VisualItem.FILLCOLOR, ColorLib.rgb(100,100,100));
        edgeArrowColor.add("_hover", ColorLib.rgb(220,100,100));
        
        // recolor
        ActionList recolor = new ActionList();
        recolor.add(nodeTextColor);
        recolor.add(nodeFillColor);
        recolor.add(nodeStrokeColor);
        recolor.add(edgeLineColor);
        recolor.add(edgeArrowColor);
        m_vis.putAction("recolor", recolor);
        
        
        ForceDirectedLayout fdl = new ForceDirectedLayout(graphNodesAndEdges);
        ActionList layout = new ActionList(ActionList.INFINITY);
        layout.add(fdl);
        layout.add(recolor);
        layout.add(new RepaintAction());
        m_vis.putAction("layout", layout);

        m_vis.putAction("repaint", new RepaintAction());
        
//      Create the focus group
        TupleSet selectedItems = new DefaultTupleSet(); 
        m_vis.addFocusGroup(SELECTED, selectedItems);

//        listen for changes
        TupleSet focusGroup = m_vis.getGroup(SELECTED); focusGroup.addTupleSetListener(new TupleSetListener() {
                    public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
                        //do whatever you do with newly selected/deselected items
//                    	m_vis.cancel("layout");
                    	for (int i = 0; i < add.length; i++) {
							VisualItem item = (VisualItem) add[i];
							item.setHighlighted(true);
						}
                    	for (int i = 0; i < rem.length; i++) {
                    		VisualItem item = (VisualItem) rem[i];
                    		item.setHighlighted(false);
                    	}
                    	
//                        m_vis.run("layout");
                    }
                });

//      Create the rubber band object for rendering on screen
        Table rubberBandTable = new Table();
        rubberBandTable.addColumn(VisualItem.POLYGON, float[].class);      
        rubberBandTable.addRow();
        m_vis.add(RUBBER_BAND, rubberBandTable);       
        VisualItem rubberBand = (VisualItem) m_vis.getVisualGroup(RUBBER_BAND).tuples().next();
        rubberBand.set(VisualItem.POLYGON, new float[8]);
        rubberBand.setStrokeColor(ColorLib.color(ColorLib.getColor(255,0,0)));

//      render the rubber band with the default polygon renderer
        Renderer rubberBandRenderer = new PolygonRenderer(Constants.POLY_TYPE_LINE);
        rf.add(new InGroupPredicate(RUBBER_BAND), rubberBandRenderer);

//      Link the rubber band control to the rubber band display object
        addControlListener(new RubberBandSelect(rubberBand));

   
    
        // initialize the display
        PopupMenuController popup = new PopupMenuController(m_vis);
        
        pan(400, 300);
        zoom(new Point2D.Double(400,300), 1.75);
        addControlListener(new DragControl());
        addControlListener(new ZoomToFitControl(Control.MIDDLE_MOUSE_BUTTON));
        addControlListener(new ZoomControl());
//        addControlListener(new PanControl());
        addControlListener(popup);
//      makes us able to stop TextEditor by special KeyEvents (e.g. Enter)
        getTextEditor().addKeyListener(popup);	
        
        
        // filter graph and perform layout
        m_vis.run("layout");
        
      }
    
    private void initDataGroups() {
        // create sample graph
        g = new Graph(true);
        for ( int i=0; i<3; ++i ) {
            Node n1 = g.addNode();
            Node n2 = g.addNode();
            Node n3 = g.addNode();
            g.addEdge(n1, n2);
            g.addEdge(n1, n3);
//            g.addEdge(n2, n3);
        }
        g.addEdge(0, 3);
//        g.addEdge(3, 6);
        g.addEdge(6, 0);
        
        // add labels for nodes and edges
        g.addColumn(VisualItem.LABEL, String.class);
        for (int i = 0; i < 9; i++) {
			g.getNode(i).setString(VisualItem.LABEL, "Node "+i);
		}
    }
    
    public static void main(String argv[]) {
        initUI();
    }
    
    private static void initUI() {
        UILib.setPlatformLookAndFeel();
        
        //the main panel = the visual editor
        GraphEditor ed = new GraphEditor();
        
        String html =
        	"<html><ul>" +
        	"<li>drag the mouse (right mousebutton) to 'drag select' " +
        	"multiple nodes at once. use shift + drag between to mouse drags " +
        	"for selecting additional nodes instead of clearing the previous " +
        	"selection</li>" +
            "<li>select the action to perform on leftclick on a node from " +
            "the left radiobuttons</li>" +
            "<li>rightclick is either stopping edge-creation (if actually " +
            "creating an edge) or showing context menu</li>" +
            "<li>finish renaming with either enter or any mouseclick</li>" +
            "</ul></html>"; 
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton node = new JRadioButton(
				"<html>Create a <i>node</i></html>", true);
		JRadioButton edge = new JRadioButton(
				"<html>Create an <i>edge</i></html>", false);
		JRadioButton rename = new JRadioButton(
				"<html><i>Rename</i> node</html>", false);
        buttonGroup.add(node);
        buttonGroup.add(edge);
        buttonGroup.add(rename);
        node.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				action = 1;
			}
        });
        edge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				action = 2;
			}
        });
        rename.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		action = 3;
        	}
        });
        Component[] c = new Component[] {node, edge, rename};
        Box radios = UILib.getBox(c, false, 0, 0);
        radios.setBorder(BorderFactory.createTitledBorder("On leftclick:"));
        
        JLabel info = new JLabel(html);
        info.setBorder(BorderFactory.createTitledBorder("Explanations:"));
        
        
        //the south panel = info panel
        JPanel south = new JPanel(new BorderLayout());
        south.add(radios, BorderLayout.WEST);
        south.add(info, BorderLayout.CENTER);
        
        //add everything together and display the frame
        JPanel main = new JPanel(new BorderLayout());
        main.add(ed, BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);
        
        JFrame frame = new JFrame("p r e f u s e  |  g r a p h   e d i t o r");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800,600));
        frame.setContentPane(main);
        frame.pack();
        frame.setVisible(true);		
	}

    
    public static class PopupMenuController extends ControlAdapter implements
			ActionListener {

    	private Graph g;
		private Display d;
		private Visualization vis;
		private VisualItem clickedItem;

		private JPopupMenu nodePopupMenu; 
    	private JPopupMenu popupMenu; 
    	
		private Point2D mousePosition = new Point2D.Double();
		private VisualItem nodeVisualDummy;
		public Node nodeSourceDummy;
		public Edge edgeDummy;
		private boolean creatingEdge = false;
		private boolean editing;

    	public PopupMenuController(Visualization vis) {
    		this.vis = vis;
    		this.g = (Graph) vis.getSourceData(graphNodesAndEdges);
    		this.d = vis.getDisplay(0);

    		createDummy();
    		
    		//create popupMenu for nodes
    		nodePopupMenu = new JPopupMenu(); 
    		
    		JMenuItem delete = new JMenuItem("delete", 'd');
    		JMenuItem editText = new JMenuItem("edit Name", 'a');
    		JMenuItem addEdge = new JMenuItem("add Edge", 'e');
    		JMenuItem addNode = new JMenuItem("add Node", 'n');
    		
    		delete.setActionCommand("node_delete");
    		editText.setActionCommand("node_editText");
    		addEdge.setActionCommand("node_addEdge");
    		addNode.setActionCommand("node_addNode");
    		
    		nodePopupMenu.addSeparator();
    		nodePopupMenu.add(delete);
    		nodePopupMenu.addSeparator();
    		nodePopupMenu.add(editText);
    		nodePopupMenu.addSeparator();
    		nodePopupMenu.add(addEdge);
    		nodePopupMenu.add(addNode);
    		    		
    		delete.setMnemonic(KeyEvent.VK_D);
    		editText.setMnemonic(KeyEvent.VK_A);
    		addEdge.setMnemonic(KeyEvent.VK_E);
    		addNode.setMnemonic(KeyEvent.VK_N);
    		
    		delete.addActionListener(this);
    		editText.addActionListener(this);
    		addEdge.addActionListener(this);
    		addNode.addActionListener(this);
    		
    		
    		//create popupMenu for 'background'
    		popupMenu = new JPopupMenu(); 
    		addNode = new JMenuItem("add Node", 'n');
    		addNode.setActionCommand("addNode");
    		popupMenu.addSeparator();
    		popupMenu.add(addNode);
    		addNode.setMnemonic(KeyEvent.VK_N);
    		addNode.addActionListener(this);
    		
		}
    	
    	// ---------------------------------------------
    	// --- methods for event processing
    	// ---------------------------------------------
    	
    	@Override
    	public void itemClicked(VisualItem item, MouseEvent e) {
    		if (SwingUtilities.isRightMouseButton(e)) {
    			clickedItem = item;
        		//on rightclick, stop the edge creation 
    			if (creatingEdge) {
    				stopEdgeCreation();
    				return;
    			}
    			
    			if (item instanceof NodeItem) {
    	    		nodePopupMenu.show(e.getComponent(), e.getX(), e.getY());
    			}
    		} else if (SwingUtilities.isLeftMouseButton(e)) {
    			if (creatingEdge) {
    				g.addEdge(edgeDummy.getSourceNode(), (Node)item.getSourceTuple());
    			} else if (item instanceof NodeItem) {	//a node was clicked
    				switch (action) {
					case 1:	//	create a node
						addNewNode(item);
						break;

					case 2:	//	create an edge
    					creatingEdge = true;
    					createTemporaryEdgeFromSourceToDummy(item);
						break;
					
					case 3:	//	rename node
						startEditing(item);
						break;

					default:
						break;
					}

    			} 
    		}
    	}


		@Override
    	public void mouseClicked(MouseEvent e) {
			if (creatingEdge) {
				stopEdgeCreation();
				return;
			} else if (editing) {
				stopEditing();
			}
    		if (SwingUtilities.isRightMouseButton(e)) {
    			clickedItem = null;
    			if (creatingEdge) stopEdgeCreation();
    			popupMenu.show(e.getComponent(), e.getX(), e.getY());
    		}
    	}
    	
    	@Override
    	public void keyReleased(KeyEvent e) {
    		// called, when keyReleased events on displays textEditor are fired
    		if (e.getKeyCode() == KeyEvent.VK_ENTER && editing) {
    			stopEditing();
    		}
    	}
    	
    	@Override
    	public void itemKeyReleased(VisualItem item, KeyEvent e) {
    		keyReleased(e);
    	}

    	
    	/**
    	 * called on popupMenu Action
    	 */
    	public void actionPerformed(ActionEvent e) {
    		if (e.getActionCommand().startsWith("node")) {
    			if (e.getActionCommand().endsWith("delete")) {
    				g.removeNode((Node)clickedItem.getSourceTuple());
    			} else if (e.getActionCommand().endsWith("editText")) {
    				startEditing(clickedItem);
    			} else if (e.getActionCommand().endsWith("addEdge")) {
    				creatingEdge = true;
    				createTemporaryEdgeFromSourceToDummy(clickedItem);
    			} else if (e.getActionCommand().endsWith("addNode")) {
    				addNewNode(clickedItem);
    			}
    		} else {
    			if (e.getActionCommand().equals("addNode")) {
    				int node = (int) (Math.random()*(g.getNodeCount()-1));
					Node source = g.getNode(node);	//random source
					addNewNode(source);
    			} else {

    			}
    		}
    	}

    	// ---------------------------------------------
    	// --- helper methods 
    	// ---------------------------------------------
    	
    	private void startEditing(VisualItem item) {
    		editing = true;
			d.editText(item, VisualItem.LABEL);			
		}

    	private void stopEditing() {
    		editing = false;
    		d.stopEditing();
    	}

		private void addNewNode(VisualItem source) {
			addNewNode((Node)source.getSourceTuple());
		}
		
    	private void addNewNode(Node source) {
    		Node n = g.addNode();		//create a new node
    		n.set(VisualItem.LABEL, "Node "+n.getRow());	//assign a new name
    		g.addEdge(source, n);	//add an edge from source to the new node
    	}

    	// ---------------------------------------------
    	// --- methods for edgeCreation
    	// ---------------------------------------------
    	
    	private void stopEdgeCreation() {
    		creatingEdge = false;
    		removeEdgeDummy();
    	}

    	/**
    	 * Removes all dummies, the node and the two edges. Additionally sets the 
    	 * variables who stored a reference to these dummies to null.
    	 */
    	public void removeAllDummies() {
    		if (nodeSourceDummy != null) g.removeNode(nodeSourceDummy);
    		edgeDummy = null;
    		nodeSourceDummy = null;
    		nodeVisualDummy = null;
    	}
    	
    	/**
    	 * Removes all edge dummies, if the references stored to these dummies are
    	 * not null. Additionally sets the references to these dummies to null.
    	 */
    	private void removeEdgeDummy() {
    		if (edgeDummy != null) {
    			g.removeEdge(edgeDummy);
    			edgeDummy = null;
    		}
    	}

    	public VisualItem createDummy() {
    		//create the dummy node for the creatingEdge mode
    		nodeSourceDummy = g.addNode();
    		nodeSourceDummy.set(VisualItem.LABEL, "");

    		nodeVisualDummy = vis.getVisualItem(graphNodes, nodeSourceDummy);
    		nodeVisualDummy.setSize(0.0);
    		nodeVisualDummy.setVisible(false);

    		/*
    		 * initially set the dummy's location. upon mouseMove events, we 
    		 * will do that there. otherwise, the dummy would appear on top 
    		 * left position until the mouse moves
    		 */
    		double x = d.getBounds().getCenterX();
    		double y = d.getBounds().getCenterY();
    		mousePosition.setLocation(x, y);
    		nodeVisualDummy.setX(mousePosition.getX());
    		nodeVisualDummy.setY(mousePosition.getY());
    		return nodeVisualDummy;
    	}

    	public void removeNodeDummy() {
    		g.removeNode(nodeSourceDummy);
    		nodeSourceDummy = null;
    		nodeVisualDummy = null;
    	}


    	public void createTemporaryEdgeFromSourceToDummy(Node source) {
    		if (edgeDummy == null) {
    			edgeDummy = g.addEdge(source, nodeSourceDummy);
    		} 
    	} 

    	public void createTemporaryEdgeFromDummyToTarget(Node target) {
    		if (edgeDummy == null) {
    			edgeDummy = g.addEdge((Node)nodeVisualDummy.getSourceTuple(), target);
    		} 
    	} 

    	/**
    	 * @param source the item to use as source for the dummy edge
    	 */
    	public void createTemporaryEdgeFromSourceToDummy(VisualItem source) {
    		createTemporaryEdgeFromSourceToDummy((Node)source.getSourceTuple());
    	}

    	/**
    	 * @param target the item to use as target for the dummy edge
    	 */
    	public void createTemporaryEdgeFromDummyToTarget(VisualItem target) {
    		createTemporaryEdgeFromDummyToTarget((Node)target.getSourceTuple());
    	}


    	@Override
    	public void mouseMoved(MouseEvent e) {
    		//necessary, if you have no dummy and this ControlAdapter is running 
    		if (nodeVisualDummy == null) return;
    		// update the coordinates of the dummy-node to the mouselocation so the tempEdge is drawn to the mouselocation too 
    		d.getAbsoluteCoordinate(e.getPoint(), mousePosition);
    		nodeVisualDummy.setX(mousePosition.getX());
    		nodeVisualDummy.setY(mousePosition.getY());
    	}

    	/**
    	 * only necessary if edge-creation is used together with aggregates and
    	 * the edge should move on when mousepointer moves within an aggregate
    	 */
    	@Override
    	public void itemMoved(VisualItem item, MouseEvent e) {
    		if (item instanceof AggregateItem) 
    			mouseMoved(e);
    	}

        
    } // end of inner class PopupMenuController
    
//  Add a control to set the rubber band bounds and perform the actual selection logic 
    public class RubberBandSelect extends ControlAdapter  {
      private int downX1, downY1;
      private VisualItem rubberBand;
      Point2D screenPoint = new Point2D.Float();
      Point2D absPoint = new Point2D.Float();
      Rectangle2D rect = new Rectangle2D.Float();
      Rectangle r = new Rectangle();   

      public RubberBandSelect(VisualItem rubberBand) {
          this.rubberBand = rubberBand;
      }

      public void mousePressed(MouseEvent e) {   
    	  if (!SwingUtilities.isLeftMouseButton(e)) return;

    	  Display d = (Display)e.getComponent();
    	  Visualization vis = d.getVisualization();
    	  TupleSet focus = vis.getFocusGroup(SELECTED);
    	  if (!e.isShiftDown()) {
    		  focus.clear();
    	  }

    	  float[] bandRect = (float[]) rubberBand.get(VisualItem.POLYGON);
    	  bandRect[0] = bandRect[1] = bandRect[2] = bandRect[3] = 
    		  bandRect[4] = bandRect[5] = bandRect[6] = bandRect[7] = 0;

    	  d.setHighQuality(false);
    	  screenPoint.setLocation(e.getX(), e.getY());
    	  d.getAbsoluteCoordinate(screenPoint, absPoint);
    	  downX1 = (int) absPoint.getX();
    	  downY1= (int) absPoint.getY();
    	  rubberBand.setVisible(true);
      }
       
       public void mouseDragged (MouseEvent e) {
           if (!SwingUtilities.isLeftMouseButton(e)) return;
     
           Display d = (Display)e.getComponent();           
           screenPoint.setLocation(e.getX(), e.getY());
           d.getAbsoluteCoordinate(screenPoint, absPoint);
           int x1 = downX1;
           int y1 = downY1;
           int x2 = (int) absPoint.getX();
           int y2 = (int) absPoint.getY();
              
           float[] bandRect = (float[]) rubberBand.get(VisualItem.POLYGON);
           bandRect[0] = x1;
           bandRect[1] = y1;
           bandRect[2] = x2;
           bandRect[3] = y1;
           bandRect[4] = x2;
           bandRect[5] = y2;
           bandRect[6] = x1;
           bandRect[7] = y2;

           if (x2 < x1){
               int temp = x2;
               x2 = x1;
               x1 = temp;
           }
           if (y2 < y1) {
               int temp = y2;
               y2 = y1;
               y1 = temp;
           }
           rect.setRect(x1,y1,x2-x1, y2-y1);

           Visualization vis = d.getVisualization();
           TupleSet focus = vis.getFocusGroup(SELECTED);
     
           if (!e.isShiftDown()) {
                   focus.clear();
           }
        
           //allocate the maximum space we could need
         
           Tuple[] selectedItems = new Tuple[vis.getGroup(NODES).getTupleCount()];
           Iterator it = vis.getGroup(NODES).tuples();
          
           //in this example I'm only allowing Nodes to be selected
           int i=0;
           while (it.hasNext()) {
               VisualItem item = (VisualItem) it.next();             
               if (item.isVisible() && item.getBounds().intersects(rect)){
                   selectedItems[i++] = item;
               }
           }

           //Trim the array down to the actual size
           Tuple[] properlySizedSelectedItems = new Tuple[i];
           System.arraycopy(selectedItems, 0, properlySizedSelectedItems,0, i);
           for (int j = 0; j < properlySizedSelectedItems.length; j++) {
			Tuple tuple = properlySizedSelectedItems[j];
			focus.addTuple(tuple);
		}
            
           rubberBand.setValidated(false);
           d.repaint();
       }
       
       public void mouseReleased (MouseEvent e) {
           if (!SwingUtilities.isLeftMouseButton(e)) return;
           rubberBand.setVisible(false);
           Display d = (Display)e.getComponent();   
           
           d.setHighQuality(true);
           d.getVisualization().repaint();
       }
  }
 
} // end of class GraphEditor