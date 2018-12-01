package prefuse.demos.yamvia;
//import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
//import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.distortion.Distortion;
import prefuse.action.distortion.FisheyeDistortion;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
//import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
//import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.data.Node;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
//import prefuse.data.io.TreeMLReader;
import prefuse.data.search.PrefixSearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;


import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.text.*;

/**
 * Demonstration of a node-link tree viewer
 *
 * @version 1.0
 * @author <a href="http://jheer.org">jeffrey heer</a>
 */
public class ChoicePanel extends Display 
{
    
    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private static final String LABEL = "name";
    private static final String LABEL_TYPE = "type";
    
    public static ChoicePanel tview;
    
    private LabelRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;
    
    private String m_label = "label";
    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;
    
    // TEST pour voir si on peut rendre ça dynamique
    public static Tree copy_t = null;
    public static Node root = null;

    
    public ChoicePanel(Tree t, String label) {
        super(new Visualization());
        m_label = label;

        m_vis.add(tree, t);
        
        m_nodeRenderer = new LabelRenderer(m_label);
        m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
        m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
        m_nodeRenderer.setRoundedCorner(8,8);
        m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);
        
        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        m_vis.setRendererFactory(rf);
               
        // colors
        ItemAction nodeColor = new NodeColorAction(treeNodes);
        ItemAction textColor = new ColorAction(treeNodes,
                VisualItem.TEXTCOLOR, ColorLib.rgb(200,200,200));
        m_vis.putAction("textColor", textColor);
        
        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgb(200,200,200));
        
        // quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);
        
        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        m_vis.putAction("fullPaint", fullPaint);
        
        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);
        
        // create the tree layout action
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
                m_orientation, 50, 0, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(25,100));
        m_vis.putAction("treeLayout", treeLayout);
        
        CollapsedSubtreeLayout subLayout = 
            new CollapsedSubtreeLayout(tree, m_orientation);
        m_vis.putAction("subLayout", subLayout);
        
        AutoPanAction autoPan = new AutoPanAction();
        
        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new FisheyeTreeFilter(tree, 1)); // PERSO : The change here for 1 deep of the visualisation, 1 is good for us
        filter.add(new FontAction(treeNodes, FontLib.getFont("Tahoma", 10)));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeColor);
        m_vis.putAction("filter", filter);
        
        // animated transition
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(autoPan);
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new LocationAnimator(treeNodes));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);
        m_vis.alwaysRunAfter("filter", "animate");
        
        // create animator for orientation changes
        ActionList orient = new ActionList(2000);
        orient.setPacingFunction(new SlowInSlowOutPacer());
        orient.add(autoPan);
        orient.add(new QualityControlAnimator());
        orient.add(new LocationAnimator(treeNodes));
        orient.add(new RepaintAction());
        m_vis.putAction("orient", orient);
        
        
        // ------------------------------------------------
        
        // initialize the display
        setSize(700,600);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new PanControl());
        //addControlListener(new FocusControl(1, "filter"));
        addControlListener(Ivipi.focusControler);
        
        registerKeyboardAction(
            new OrientAction(Constants.ORIENT_LEFT_RIGHT),
            "left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
        registerKeyboardAction(
            new OrientAction(Constants.ORIENT_TOP_BOTTOM),
            "top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
        registerKeyboardAction(
            new OrientAction(Constants.ORIENT_RIGHT_LEFT),
            "right-to-left", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
        registerKeyboardAction(
            new OrientAction(Constants.ORIENT_BOTTOM_TOP),
            "bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), WHEN_FOCUSED);
        
        // ------------------------------------------------
        
        // filter graph and perform layout
        setOrientation(m_orientation);
        m_vis.run("filter");
        
        TupleSet search = new PrefixSearchTupleSet(); 
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, search);
        search.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                m_vis.cancel("animatePaint");
                m_vis.run("fullPaint");
                m_vis.run("animatePaint");
                
            	Node n = ChoicePanel.copy_t.addChild (ChoicePanel.root);
            	n.set ("name", "TEST ON ASK SEARCH");
            }
        });
    }
    
    // ------------------------------------------------------------------------
    
    public void setOrientation(int orientation) {
        NodeLinkTreeLayout rtl 
            = (NodeLinkTreeLayout)m_vis.getAction("treeLayout");
        CollapsedSubtreeLayout stl
            = (CollapsedSubtreeLayout)m_vis.getAction("subLayout");
        switch ( orientation ) {
        case Constants.ORIENT_LEFT_RIGHT:
            m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
            m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
            m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
            m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
            m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
            break;
        case Constants.ORIENT_RIGHT_LEFT:
            m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
            m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
            m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
            m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
            m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
            break;
        case Constants.ORIENT_TOP_BOTTOM:
            m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
            m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
            m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
            m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
            m_edgeRenderer.setVerticalAlignment2(Constants.TOP);
            break;
        case Constants.ORIENT_BOTTOM_TOP:
        	
            m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
            m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
            m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
            m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
            m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);
            break;
        default:
            throw new IllegalArgumentException(
                "Unrecognized orientation value: "+orientation);
        }
        m_orientation = orientation;
        rtl.setOrientation(orientation);
        stl.setOrientation(orientation);
    }
    
    public int getOrientation() {
        return m_orientation;
    }
    

    // ------------------------------------------------------------------------
    
  
    //public static JComponent demo(String datafile, final String label) {
    public static JComponent init () {
        Color BACKGROUND = Color.WHITE;
        Color FOREGROUND = Color.BLACK;
                
        // Create GRAPH
        Tree t = new Tree();
        t.addColumn(LABEL, String.class); 
        root = t.addRoot(); 
        root.set (LABEL, "Selection");
        
        for (int i=0; i<IvipiData.choiceMainCat.length; i++)
        {
        	Node n = t.addChild(root); 
        	n.set(LABEL, IvipiData.choiceMainCat[i]);
        }
        
        /*for (int i = 0; i < 4; i++) 
        { 
	        Node n = t.addChild(root); 
	        n.set(label, "child "+i+" of root"); 
	        
	        for (int j = 0; j < 4; j++) 
	        { 
		        Node m = t.addChild(n); 
		        m.set(label, "child "+i+"."+j+" of root");
		        
		        for (int k = 0; k < 4; k++) 
		        { 
			        Node l = t.addChild(m); 
			        l.set(label, "child "+i+"."+j+"."+k+" of root"); 
		        } 
	        } 
        }*/
        copy_t = t; // Little hack to change for clean code
    
        
        // create a new treemap
        tview = new ChoicePanel(t, LABEL);
        //tview.setBackground(BACKGROUND);
        //tview.setForeground(FOREGROUND);
        tview.setBackground(FOREGROUND);
        tview.setForeground(BACKGROUND);
        tview.setSize(1024, 200);
        
        JLabel tf = new JLabel ("test");
        tf.setVisible(true);
        tview.add(tf);
        
        //JTextField tf = new JTextField (); 
    	//tview.setTextEditor(tf);
        //JTextComponent tf = tview.getTextEditor();
        //System.out.println("TEST : " + tf.toString());
        
        // create a search panel for the tree map
        JSearchPanel search = new JSearchPanel(tview.getVisualization(),
            treeNodes, Visualization.SEARCH_ITEMS, LABEL, true, true);
        search.setShowResultCount(true);
        search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
        search.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
        search.setBackground(BACKGROUND);
        search.setForeground(FOREGROUND);
        
        //tview.add(search);
        /*
        final JFastLabel title = new JFastLabel("LAAAAAABEL");
        title.setPreferredSize(new Dimension(350, 20));
        //title.setVerticalAlignment(SwingConstants.BOTTOM);
        title.setBounds(12, 45, 12, 12);
        title.setBorder(BorderFactory.createEmptyBorder(3,0,0,0));
        title.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 16));
        title.setBackground(BACKGROUND);
        title.setForeground(FOREGROUND);
        
        tview.addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
                if ( item.canGetString(LABEL) )
                    title.setText(item.getString(LABEL));
            }
            public void itemExited(VisualItem item, MouseEvent e) {
                title.setText(null);
            }
        });
        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalStrut(10));
        box.add(title);
        box.add(Box.createHorizontalGlue());
        box.add(search);
        box.add(Box.createHorizontalStrut(3));
        box.setBackground(BACKGROUND);*/
        
        JPanel panel = new JPanel(new BorderLayout());
        //panel.setBackground(BACKGROUND);
        panel.setBackground(FOREGROUND);
        //panel.setForeground(FOREGROUND);
        panel.setForeground(BACKGROUND);
        panel.add(tview, BorderLayout.CENTER);
        //panel.add(box, BorderLayout.SOUTH);
        
        return panel;
    }

    
    public static void updateDynamicNode (Node node, Vector<String> data)
    {
    	for (int i=0; i<data.size(); i++)
        {
        	Node n = ChoicePanel.copy_t.addChild(node); 
        	n.set("name", data.get(i));
        }
    }    
    
    public static void deleteNode (Node node)
    {
    	Node node_tmp;
    	while ((node_tmp = ChoicePanel.copy_t.getFirstChild(node))!=null)
    		copy_t.removeChild(node_tmp);
    	ChoicePanel.tview.repaint();
    }
   
    
    /*public static void test (VisualItem item)
    {
    	//tview.editText(item, VisualItem.LABEL);
    	System.out.println (item.canSet("name", JTextField.class));
    	//JTextField tf = new JTextField (); 
    	//tview.setTextEditor(tf);
    }*/
    
    
    public static void startEditing (VisualItem item)
    {
    	tview.editText(item, "name");
    }
    
    // ------------------------------------------------------------------------
   
    public class OrientAction extends AbstractAction {
        private int orientation;
        
        public OrientAction(int orientation) {
            this.orientation = orientation;
        }
        public void actionPerformed(ActionEvent evt) {
            setOrientation(orientation);
            getVisualization().cancel("orient");
            getVisualization().run("treeLayout");
            getVisualization().run("orient");
        }
    }
    
    public class AutoPanAction extends Action {
        private Point2D m_start = new Point2D.Double();
        private Point2D m_end   = new Point2D.Double();
        private Point2D m_cur   = new Point2D.Double();
        private int     m_bias  = 150;
        
        public void run(double frac) {
            TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
            if ( ts.getTupleCount() == 0 )
                return;
            
            if ( frac == 0.0 ) {
                int xbias=0, ybias=0;
                switch ( m_orientation ) {
                case Constants.ORIENT_LEFT_RIGHT:
                    xbias = m_bias;
                    break;
                case Constants.ORIENT_RIGHT_LEFT:
                    xbias = -m_bias;
                    break;
                case Constants.ORIENT_TOP_BOTTOM:
                    ybias = m_bias;
                    break;
                case Constants.ORIENT_BOTTOM_TOP:
                    ybias = -m_bias;
                    break;
                }

                VisualItem vi = (VisualItem)ts.tuples().next();
                m_cur.setLocation(getWidth()/2, getHeight()/2);
                getAbsoluteCoordinate(m_cur, m_start);
                m_end.setLocation(vi.getX()+xbias, vi.getY()+ybias);
            } else {
                m_cur.setLocation(m_start.getX() + frac*(m_end.getX()-m_start.getX()),
                                  m_start.getY() + frac*(m_end.getY()-m_start.getY()));
                panToAbs(m_cur);
            }
        }
    }
    
    public static class NodeColorAction extends ColorAction {
        
        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }
        
        public int getColor(VisualItem item) {
            if ( m_vis.isInGroup(item, Visualization.SEARCH_ITEMS) )
                return ColorLib.rgb(255,190,190);
            else if ( m_vis.isInGroup(item, Visualization.FOCUS_ITEMS) )
                return ColorLib.rgb(198,229,229);
            else if ( item.getDOI() > -1 )
                return ColorLib.rgb(164,193,193);
            else
                return ColorLib.rgba(255,255,255,0);
        }
        
    } // end of inner class TreeMapColorAction
    
}
