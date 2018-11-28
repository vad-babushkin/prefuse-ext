package prefuse.demos.tangwing.test;

import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.graph.BalloonTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

public class GraphExample {

    public static void main(String[] argv) {
        
        // -- 1. load the data ------------------------------------------------
        
        // load the socialnet.xml file. it is assumed that the file can be
        // found at the root of the java classpath
        Graph graph = null;
        try {
            //graph = new GraphMLReader().readGraph("suivi.xml");//
            graph = new GraphMLReader().readGraph("/socialnet.xml");
        } catch ( DataIOException e ) {
            e.printStackTrace();
            System.err.println("Error loading graph. Exiting...");
            System.exit(1);
        }
        
        
        // -- 2. the visualization --------------------------------------------
        
        // add the graph to the visualization as the data group "graph"
        // nodes and edges are accessible as "graph.nodes" and "graph.edges"
        Visualization vis = new Visualization();
        vis.add("graph", graph);
        vis.setInteractive("graph.edges", null, false);
        
        // -- 3. the renderers and renderer factory ---------------------------
        
        // draw the "name" label for NodeItems
        LabelRenderer r = new LabelRenderer("name");
        r.setRoundedCorner(8, 8); // round the corners
        
        // create a new default renderer factory
        // return our name label renderer as the default for all non-EdgeItems
        // includes straight line edges for EdgeItems by default
        vis.setRendererFactory(new DefaultRendererFactory(r));
        
        
        // -- 4. the processing actions ---------------------------------------
        
        // create our nominal color palette
        // pink for females, baby blue for males
        int[] palette = new int[] {
            ColorLib.rgb(255,180,180), 
            ColorLib.rgb(0, 55, 222),
            ColorLib.rgb(255, 0,180),
            ColorLib.rgb(190,0,255),
            ColorLib.rgb(128, 128, 64),
            ColorLib.rgb(190,190,255),
            ColorLib.rgb(255, 128, 64)
        };
        // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "gender",
                Constants.NOMINAL, VisualItem.FILLCOLOR, palette);
        // use black for node text
        ColorAction text = new ColorAction("graph.nodes",
                VisualItem.TEXTCOLOR, ColorLib.gray(0));
        // use light grey for edges
        ColorAction edges = new ColorAction("graph.edges",
                VisualItem.STROKECOLOR, ColorLib.gray(200));
        
        // create an action list containing all color assignments
        ActionList color = new ActionList();
        color.add(fill);
        color.add(text);
        color.add(edges);
        
        // create an action list with an animated layout
        ActionList layout = new ActionList(Activity.INFINITY);
        //layout.add(new ForceDirectedLayout("graph"));
        //layout.add(new RadialTreeLayout("graph"));
        //layout.add(new FruchtermanReingoldLayout("graph"));
        layout.add(new BalloonTreeLayout("graph"));
        //layout.add(new CircleLayout("graph"));
        layout.add(new RepaintAction());
        
        // add the actions to the visualization
        vis.putAction("color", color);
        vis.putAction("layout", layout);
        
        
        // -- 5. the display and interactive controls -------------------------
        
        Display d = new Display(vis);
        d.setSize(720, 500); // set display size
        // drag individual items around
        d.addControlListener(new DragControl());
        // pan with left-click drag on background
        d.addControlListener(new PanControl()); 
        // zoom with right-click drag
        d.addControlListener(new ZoomControl());
        
        // -- 6. launch the visualization -------------------------------------
        
        // create a new window to hold the visualization
        JFrame frame = new JFrame("prefuse example");
        // ensure application exits when window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(d);
        frame.pack();           // layout components in window
        frame.setVisible(true); // show the window
        // assign the colors
        vis.run("color");
        // start up the animated layout
        vis.run("layout");
    }
    
}