package prefuse.demos.pap;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JFrame;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

public class EdgeRendererTest extends Display {
    private Graph graph = null;

    public EdgeRendererTest() {
        super(new Visualization());
        initGraph();

        LabelRenderer nodeRenderer = new LabelRenderer("label");
        nodeRenderer.setRoundedCorner(6, 6);
        DefaultRendererFactory rendererFactory = new DefaultRendererFactory(nodeRenderer,new SelfReferenceRenderer());
        m_vis.setRendererFactory(rendererFactory);

        // Color Actions
        ColorAction nodeText = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR,ColorLib.color(Color.BLACK));
        ColorAction nodeStroke = new ColorAction("graph.nodes", VisualItem.STROKECOLOR,ColorLib.color(Color.BLACK));
        ColorAction nodeFill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.color(Color.RED));
        ColorAction edgeStrokes = new ColorAction("graph.edges", VisualItem.STROKECOLOR,ColorLib.color(Color.BLACK));

        // bundle the color actions
        ActionList draw = new ActionList();
        draw.add(edgeStrokes);
        draw.add(nodeFill);
        draw.add(nodeStroke);
        draw.add(nodeText);
        draw.add(new RepaintAction());
        m_vis.putAction("draw", draw);

        // create the layout action for the graph
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout("graph");
        m_vis.putAction("animate", treeLayout);

        // run actions
        m_vis.run("animate");
        m_vis.run("draw");
        
    }

    private void initGraph() {
        graph = new Graph();
        // first define a column
        graph.addColumn("label", String.class);

        Node n1 = addNode("node 1");
        Node n2 = addNode("node 2");
        Node n3 = addNode("node 3");
        Node n4 = addNode("node 4");
        Node n5 = addNode("node 5");
        
        // then add the edges
        graph.addEdge(n1, n2);
        graph.addEdge(n1, n3);
        graph.addEdge(n1, n1);
        graph.addEdge(n3,n4);
        graph.addEdge(n4, n5);
        graph.addEdge(n5, n5);
       
        m_vis.addGraph("graph", graph);

    }

    private Node addNode(String label) {
        Node node = graph.addNode();
        node.setString("label", label);
        return node;
    }
    
    public class SelfReferenceRenderer extends EdgeRenderer {
	    private Ellipse2D m_ellipse = new Ellipse2D.Float();
	    protected Shape getRawShape(VisualItem item) {
		    try  	  {
		        EdgeItem edge = (EdgeItem) item;
			    VisualItem item1 = edge.getSourceItem();
			    VisualItem item2 = edge.getTargetItem();           
			    	    
			    //  self interaction
			    if (item1 == item2)
			    {	
				    getAlignedPoint(m_tmpPoints[0], item1.getBounds(), m_xAlign1, m_yAlign1);
				    getAlignedPoint(m_tmpPoints[1], item2.getBounds(), m_xAlign2, m_yAlign2);
				    m_curWidth = (int) Math.round(m_width * getLineWidth(item));
			        m_ellipse.setFrame(m_tmpPoints[0].getX(), m_tmpPoints[0].getY(), 40, 30);
			        return m_ellipse;
			    }
			    
		    }
		    catch(Exception ex) {
		    	ex.printStackTrace();
		    	return null;
		    }

		    return super.getRawShape(item);
	  } //getRawShape
    }

    public static void main(String[] args) {
        EdgeRendererTest test = new EdgeRendererTest();
        JFrame frame = new JFrame("Node Renderer Test");
        frame.getContentPane().add(test);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocation(10,10);
        frame.setSize(800,400);
        frame.setVisible(true);
    }

}
