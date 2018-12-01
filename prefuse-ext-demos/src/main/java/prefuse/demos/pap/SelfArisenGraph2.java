package prefuse.demos.pap;

import javax.swing.JFrame;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

/**
 * A minimalistic graph demo showing how to add data manually to a graph instead of reading it from a file.
 * More or less a cut down of the AggregateDemo of the prefuse download
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumpsall.net">martin dudek</a>
 *
 */


public class SelfArisenGraph2 extends Display {

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
   
    
    public SelfArisenGraph2() {
        // initialize display and data
        super(new Visualization());
        
        initDataGroups();
        
        LabelRenderer nodeRenderer = new MyLabelRenderer();
        
        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(nodeRenderer);
        m_vis.setRendererFactory(drf);
        
        // set up the visual operators
        // first set up all the color actions
        
        ColorAction nText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
        nText.setDefaultColor(ColorLib.rgb(100,0,0));
        
        
        ColorAction nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
        nStroke.setDefaultColor(ColorLib.gray(100));
        
        
        ColorAction nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
        nFill.setDefaultColor(ColorLib.gray(255));
        
        
        ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
        nEdges.setDefaultColor(ColorLib.gray(100));
        
        // bundle the color actions
        ActionList draw = new ActionList();
        
        draw.add(nText);
        draw.add(nStroke);
        draw.add(nFill);
        draw.add(nEdges);
        
        m_vis.putAction("draw", draw);
        
        // now create the main animate routine
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(new ForceDirectedLayout(GRAPH, true));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);
        
        m_vis.runAfter("draw","animate");
        
        // set up the display
        setSize(500,500);
        pan(250, 250);
        setHighQuality(true);
        addControlListener(new ZoomControl());
        addControlListener(new PanControl());
        addControlListener(new DragControl());
        
        // set things running
        m_vis.run("draw");
        
    }
    
    private void initDataGroups() {
        
	Graph g = new Graph();
	
	g.addColumn("label", String.class);
	g.addColumn("ftype",Flavour.class);
           
	Node n1 = g.addNode();    
	Node n2 = g.addNode();
        Node n3 = g.addNode();
        
        n1.setString("label","ktl3tte");
        n2.setString("label","PAP");
        n3.setString("label","Germany");
        
        n1.set("ftype", new Flavour("a"));
        n2.set("ftype", new Flavour("b"));
        n3.set("ftype", new Flavour("c"));
        
        g.addEdge(n1, n2);
        g.addEdge(n1, n3);
        
        m_vis.addGraph(GRAPH, g);
        
    }
    
    class MyLabelRenderer extends LabelRenderer {
	      public String getText(VisualItem vi) {
	          return vi.getString("label") + "\n" + ((Flavour)vi.get("ftype")).getFlavourType();
	      }
    }
    
    class Flavour {
	    String ftype;
	    public Flavour(String ftype) {
		this.ftype = ftype;
	    }
	    public String getFlavourType() {
		return ftype;
	    }
	}
    
    public static void main(String[] argv) {
        JFrame frame = demo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public static JFrame demo() {
        SelfArisenGraph2 sag = new SelfArisenGraph2();
        JFrame frame = new JFrame("adding objects to nodes");
        frame.getContentPane().add(sag);
        frame.pack();
        return frame;
    }
    
} 

