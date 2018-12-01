package prefuse.demos.pap;

import java.awt.Font;

import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.RungeKuttaIntegrator;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualItem;

/**
 * Using images and texts as nodes ...
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumpsall.net">martin dudek</a>
 *
 */


public class GraphViewImagesAndText extends Display {

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";
   
    
    public GraphViewImagesAndText() {
        // initialize display and data
        super(new Visualization());
        
        initDataGroups();
        
        LabelRenderer nodeRenderer = new LabelRenderer("text", "image");
        nodeRenderer.setVerticalAlignment(Constants.BOTTOM);
        nodeRenderer.setHorizontalPadding(0);
        nodeRenderer.setVerticalPadding(0);
        nodeRenderer.setImagePosition(Constants.TOP);
        nodeRenderer.setMaxImageDimensions(100,100);
        
        DefaultRendererFactory drf = new DefaultRendererFactory();
        drf.setDefaultRenderer(nodeRenderer);
        m_vis.setRendererFactory(drf);
         
        ColorAction nText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
        nText.setDefaultColor(ColorLib.gray(100));
        
        
        ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
        nEdges.setDefaultColor(ColorLib.gray(100));
        
        // bundle the color actions
        ActionList draw = new ActionList();
        
        draw.add(nText);
        draw.add(new FontAction(NODES, FontLib.getFont("Tahoma",Font.BOLD, 32)));
        draw.add(nEdges);
        
        m_vis.putAction("draw", draw);
        
        
        /*
         * All used parameters values are the default ones and just explicitly set
         * to show the way to go if you want to customize them
         * 
         * One exception is the defaultLength, for whic we choose a larger value 
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
	g.addColumn("text", String.class);
	
	Node n1 = g.addNode();    
	Node n2 = g.addNode();
        
        n1.setString("image","c.gif");
        n1.setString("text","this");
        n2.setString("image","b.jpg");
        n2.setString("text","and that");
        g.addEdge(n1, n2);
        
        m_vis.addGraph(GRAPH, g);

        Node a = (Node) g.tuples(ExpressionParser.predicate("image='c.gif'")).next();
        
        g.getEdge(n2,a);
    }
    
    
    public static void main(String[] argv) {
        JFrame frame = demo();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public static JFrame demo() {
        GraphViewImagesAndText sag = new GraphViewImagesAndText();
        JFrame frame = new JFrame("impressing your grand children with text and images as nodes");
        frame.getContentPane().add(sag);
        frame.pack();
        return frame;
    }
    
} 

