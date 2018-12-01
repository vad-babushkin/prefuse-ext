package prefuse.demos.pap;

import javax.swing.JFrame;

import prefuse.Constants;
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
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.RungeKuttaIntegrator;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualItem;

/**
 * Using images as nodes ...
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumpsall.net">martin dudek</a>
 *
 */


public class GraphViewImages extends Display {

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
   
    
    public GraphViewImages() {
        // initialize display and data
        super(new Visualization());
        
        initDataGroups();
        
        LabelRenderer nodeRenderer = new LabelRenderer(null, "image");
        nodeRenderer.setTextField(null);
        nodeRenderer.setVerticalAlignment(Constants.BOTTOM);
        nodeRenderer.setHorizontalPadding(0);
        nodeRenderer.setVerticalPadding(0);
        nodeRenderer.setMaxImageDimensions(100,100);
        
        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(nodeRenderer);
        m_vis.setRendererFactory(drf);
         
        ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
        nEdges.setDefaultColor(ColorLib.gray(100));
        
        // bundle the color actions
        ActionList draw = new ActionList();
        
        draw.add(nEdges);
        
        m_vis.putAction("draw", draw);
        

        /*
         * All used parameters values are the default ones and just explicitly set
         * to show the way to go if you want to customize them
         * 
         * One exception is the defaultLength, where we choose a larger value 
         * (you might wonder why it is called defaultLength) 
         */
        
        ForceSimulator fsim = new ForceSimulator(new RungeKuttaIntegrator());

	float gravConstant = -1f; 
	float minDistance = -1f;
	float theta = 0.9f;

	float drag = 0.01f; 
	float springCoeff = 1E-4f;  
	float defaultLength = 150f;  //default: 50f

	fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
	fsim.addForce(new DragForce(drag));
	fsim.addForce(new SpringForce(springCoeff, defaultLength));
	
        ForceDirectedLayout fdl = new ForceDirectedLayout(GRAPH, fsim,true);
        
        // now create the main animate routine
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(fdl);
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
	
	g.addColumn("image", String.class);
	
	Node n1 = g.addNode();    
	Node n2 = g.addNode();
        
        n1.setString("image","a.jpg");
        n2.setString("image","b.jpg");
        
        g.addEdge(n1, n2);
        
        m_vis.addGraph(GRAPH, g);
        
    }
    
    
    public static void main(String[] argv) {
        JFrame frame = demo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public static JFrame demo() {
        GraphViewImages sag = new GraphViewImages();
        JFrame frame = new JFrame("impressing your mum with images as nodes");
        frame.getContentPane().add(sag);
        frame.pack();
        return frame;
    }
    
} 

