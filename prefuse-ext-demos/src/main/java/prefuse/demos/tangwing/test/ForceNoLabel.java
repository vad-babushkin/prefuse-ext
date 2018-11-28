package prefuse.demos.tangwing.test;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataSizeAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.expression.ComparisonPredicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.io.CSVTableReader;
import prefuse.data.io.DataIOException;
import prefuse.demos.tangwing.MyForceDirectedLayout;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.ForceSimulator;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.RungeKuttaIntegrator;
import prefuse.util.force.SpringForce;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

/**
 * @author shang
 *
 */
public class ForceNoLabel {

	private enum LayoutType{
		FORCE,
		RADIAL,
		OTHER
	};
    public static void main(String[] argv) {
        
// -- 1. load the data ------------------------------------------------
    	CSVTableReader csvReader = new CSVTableReader();
    	Table t = null;
		try {
			t = csvReader.readTable("./test.csv");
		} catch (DataIOException e) {
			e.printStackTrace();
		}
        Graph graph = new Graph(t, false);
        
        // -- Add edges
        //createEdgeBySimilarity(graph);
        createEdgeByGenre(graph);//System.out.println(graph.getNodeCount()+"+"+graph.getEdgeCount());
        
// -- 2. the visualization --------------------------------------------
        Visualization vis = new Visualization();
        VisualGraph vg = vis.addGraph("graph", graph);
        vis.setInteractive("graph.edges", null, false);
        
// -- 3. the renderers and renderer factory ---------------------------
        
        LabelRenderer r = new LabelRenderer("title");
        r.setRoundedCorner(8, 8); // round the corners
        ShapeRenderer sr = new ShapeRenderer();
        // create a new default renderer factory
        // return our name label renderer as the default for all non-EdgeItems
        // includes straight line edges for EdgeItems by default
        vis.setRendererFactory(new DefaultRendererFactory(sr));
        vis.setValue("graph.nodes", null, VisualItem.SHAPE, new Integer(Constants.SHAPE_ELLIPSE));
        vis.setValue("graph.nodes", new ComparisonPredicate(
        		ComparisonPredicate.EQ, 
        		ExpressionParser.parse("[genre]"),
        		ExpressionParser.parse("'GENRE'")), 
        		VisualItem.SHAPE, new Integer(Constants.SHAPE_CROSS));
        vis.setValue("graph.nodes", new ComparisonPredicate(
        		ComparisonPredicate.EQ, 
        		ExpressionParser.parse("[genre]"),
        		ExpressionParser.parse("'ROOT'")), 
        		VisualItem.SHAPE, new Integer(Constants.SHAPE_STAR));
//        vis.setValue("graph.nodes", new ComparisonPredicate(
//        		ComparisonPredicate.EQ, 
//        		ExpressionParser.parse("[genre]"),
//        		ExpressionParser.parse("'ROOT'")), 
//        		VisualItem.FILLCOLOR, new Integer(Constants.SHAPE_STAR));
        
// -- 4. the processing actions ---------------------------------------
        ActionList color = getColorActionList();
        ActionList size = getSizeActionList();
        //ActionList layout = getLayoutActionList(LayoutType.FORCE, true);
        //ActionList layout = getLayoutActionList(LayoutType.RADIAL, false);
        ActionList layout = getLayoutActionList(LayoutType.OTHER, true);
        // add the actions to the visualization
        vis.putAction("color", color);
        vis.putAction("size", size);
        vis.putAction("layout", layout);
        
// -- 5. the display and interactive controls -------------------------
        
        Display d = new Display(vis);
        d.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        d.addControlListener(new DragControl());
        d.addControlListener(new PanControl()); 
        d.addControlListener(new ZoomControl());
    
// -- 6. launch the visualization -------------------------------------
        
        // create a new window to hold the visualization
        JFrame frame = new JFrame("�����ӰTOP250");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(d);
        frame.pack();           // layout components in window
        frame.setVisible(true); // show the window
        vis.run("color");
        vis.run("size");
        vis.run("layout");
    }

	/**Set layout for graph
	 * @return
	 */
	private static ActionList getLayoutActionList(LayoutType lt, boolean repaintAction) {
        ActionList layout = new ActionList(Activity.INFINITY);
        switch(lt)
        {
	        case FORCE:
	        	MyForceDirectedLayout mfdl = new MyForceDirectedLayout("graph", false);
	        	layout.add(mfdl);
	        	break;
	        case RADIAL:
	        	layout.add(new RadialTreeLayout("graph"));
	        	break;
	        case OTHER:
	        	// make sure that no node is covered by another one and all are scattered nicely
	        	ForceSimulator fsim = new ForceSimulator(new RungeKuttaIntegrator());
	        	float gravConstant = -20f;  // the more negative, the more repelling
	        	float minDistance = 160f;	    // -1 for always on, the more positive, the more space between nodes
	        	float theta = 0.3f;			// the lower, the more single-node repell calculation
	        	float drag = 0.01f; 
	        	float springCoeff = 1E-4f;  	//1E-4
	        	fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
	        	fsim.addForce(new DragForce(drag));
	        	fsim.addForce(new SpringForce(springCoeff, 5));
	        	ForceDirectedLayout fdl = new ForceDirectedLayout("graph", false);
	        	fdl.setForceSimulator(fsim);
	        	layout.add(fdl);
	        	//layout.add(new FruchtermanReingoldLayout("graph"));
	        	//layout.add(new BalloonTreeLayout("graph"));
	        	//layout.add(new CircleLayout("graph"));
        	
        }

        if(repaintAction)layout.add(new RepaintAction());
        return layout;
	}

	/**Set size for graph elements
	 * @return
	 */
	private static ActionList getSizeActionList() {
        //create an action list with a size action
        DataSizeAction dsa = new DataSizeAction("graph.nodes", "watchedcount");
        dsa.setIs2DArea(false);
        
        DataSizeAction dsaEdge = new DataSizeAction("graph.edges", "length");
        dsa.setIs2DArea(false);
        
        ActionList size = new ActionList();
        size.add(dsa);
        size.add(dsaEdge);
        return size;
	}

	/**Set color for graph layout
	 * @return
	 */
	private static ActionList getColorActionList() {
        // create our nominal color palette
        // pink for females, baby blue for males
//        int[] palette = new int[] {
//            ColorLib.rgb(255,180,180), 
//            ColorLib.rgb(0, 55, 222),
//            ColorLib.rgb(255, 0,180),
//            ColorLib.rgb(190,0,255),
//            ColorLib.rgb(128, 128, 64),
//            ColorLib.rgb(190,190,255),
//            ColorLib.rgb(255, 128, 64)
//        };
		int[] palette = new int[] {
	            ColorLib.hex("1B9772"), 
	            ColorLib.hex("FFE7B8"), 
	            ColorLib.hex("C96889"), 
	            ColorLib.hex("72B8AF"), 
	        };
        // map nominal data values to colors using our provided palette
        DataColorAction fill = new DataColorAction("graph.nodes", "wishcount",
                Constants.NUMERICAL, VisualItem.FILLCOLOR, palette);
        
        ColorAction fillRoot = new ColorAction("graph.nodes", 
        		ExpressionParser.predicate("[genre]=='ROOT'"), 
        		VisualItem.FILLCOLOR, ColorLib.hex("EFC137"));
        
        ColorAction fillGenre = new ColorAction("graph.nodes", 
        		ExpressionParser.predicate("[genre]=='GENRE'"), 
        		VisualItem.FILLCOLOR, ColorLib.hex("595CAD"));
        // use black for node text
        ColorAction text = new ColorAction("graph.nodes",
                VisualItem.TEXTCOLOR, ColorLib.gray(0));
        // use light grey for edges
        ColorAction edges = new ColorAction("graph.edges",
                VisualItem.STROKECOLOR, ColorLib.gray(200));
        
        // create an action list containing all color assignments
        ActionList color = new ActionList();
        color.add(fill);
        color.add(fillRoot);
        color.add(fillGenre);
        color.add(text);
        color.add(edges);
        return color;
	}

	/** Add edges according to the similarity of movie genres
	 * @param graph
	 */
	private static void createEdgeBySimilarity(Graph graph) {
		graph.getEdgeTable().addColumn("length", float.class);
		for(int i=0; i<graph.getNodeCount()-1; i++)
		{
			for(int j=i+1; j<graph.getNodeCount();j++)
			{//calculate the length of edges, according to 
			 //the similarity of movie genres
				Node n1 = graph.getNode(i);
				Node n2 = graph.getNode(j);
				String genre1 = n1.canGet("genre", String.class)?n1.getString("genre"):"";
				String genre2 = n2.canGet("genre", String.class)?n2.getString("genre"):"";
				
				float simi = computeSimilarity(genre1, genre2);
				if(simi<0.3)continue;//No edge
				
				float edgeLength = (float) Math.pow(2, 3-simi/0.3)*250;
				float edgeWidth = (float) Math.pow(2, simi/0.3)*250;
				Edge e = graph.addEdge(n1, n2);
				if(e.canSet("length", float.class))
					e.set("length", edgeWidth );
			}
		}
	}


	/**Create edges between movies and genre node, and between genre node and the root
	 * @param graph
	 */
	@SuppressWarnings("unused")
	private static void createEdgeByGenre(Graph graph) {
		//don't consider the count of genre node
		int nodeCount = graph.getNodeCount();
		for(int i=0; i<nodeCount; i++)
		{
			Node n1 = graph.getNode(i);
			String genre1 = n1.canGet("genre", String.class)?n1.getString("genre"):"";
			
			List<Node> nodeGenre = getGenreNodeByName(genre1, graph);
			if(nodeGenre.isEmpty())continue;
			for(Node n : nodeGenre)
				graph.addEdge(n1, n);
//			Edge e = graph.addEdge(n1, n2);
//			if(e.canSet("length", float.class))
//				e.set("length", edgeWidth );
		}
	}

	private static HashMap<String, Node> genreNodes = new HashMap<String, Node>();
	
	/**Create genre node for each genre, store them in genreNodes, and link thew to the root
	 * @param genre1
	 * @param g
	 * @return
	 */
	private static List<Node> getGenreNodeByName(String genre1, Graph g) {
		//create root node
		Node root;
		if(genreNodes.isEmpty())
		{
			root = g.addNode();
			if(root.canSet("title", String.class))
				root.setString("title", "ROOT");
			if(root.canSet("genre", String.class))
				root.setString("genre", "ROOT");//The genre of genre is "GENRE"
			if(root.canSet("watchedcount", int.class))
				root.setInt("watchedcount", 500000);
			genreNodes.put("root", root);
		}else
		{
			root = genreNodes.get("root");
		}
		
		List<Node> res = new ArrayList<Node>();
		for(String genre : genre1.split(","))
		{
			if(genre.isEmpty())continue;
			Node n;
			if(genreNodes.containsKey(genre))
			{
				n=(genreNodes.get(genre));
			}
			else
			{//Create new genre node
				n = g.addNode();
				if(n.canSet("title", String.class))
					n.setString("title", genre);
				if(n.canSet("genre", String.class))
					n.setString("genre", "GENRE");//The genre of genre is "GENRE"
				if(n.canSet("watchedcount", int.class))
					n.setInt("watchedcount", 150000);
				genreNodes.put(genre, n);
				g.addEdge(root, n);
			}
			res.add(n);
			return res;
		}
		return res;
	}

	/** compute the similarity of genre between 2 movies. Genre = "genre1, genre2, genre3" where genreN can be empty 
	 * @param genre1
	 * @param genre2
	 * @return
	 */
	private static float computeSimilarity(String genre1, String genre2) {
		int n = 0;//Number of same genre
		String[] g1 = genre1.split(",");
		String[] g2 = genre2.split(",");
		for(String sg1 : g1)
		{
			for(String sg2 : g2)
			{
				if(!sg1.isEmpty() && sg1.equals(sg2))
					n++;
			}
		}
		return n/3.0f;
	}
    
}