package edu.berkeley.guir.prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.animate.ColorAnimator;
import edu.berkeley.guir.prefuse.action.animate.PolarLocationAnimator;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.focus.DefaultFocusSet;
import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.ColorMap;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;

/**
 * Demo application showcasing the use of an animated radial tree layout to
 * visualize a graph. This is a re-implementation of <a 
 * href="http://zesty.ca/pubs/yee-gtv-infovis2001.pdf">Ping Yee et al.'s 
 * Animated Exploration of Dynamic Graphs with Radial Layout</a>.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class RadialGraphDemo extends JFrame {

	public static final String GRAPH_GUIR       = "etc/guir.xml";
	public static final String GRAPH_FRIENDSTER = "etc/friendster.xml";
	public static final String GRAPH_TERROR     = "etc/terror.xml";
	public static final String nameField = "label";
    
    public static void main(String[] argv) {
        new RadialGraphDemo();
    } //
		
	public RadialGraphDemo() {
	    super("RadialLayout Demo");
		try {
			// load graph
			String inputFile = GRAPH_TERROR;
			XMLGraphReader gr = new XMLGraphReader();
			gr.setNodeType(DefaultTreeNode.class);
			Graph graph = gr.loadGraph(inputFile);
			
			// create display and filter
            ItemRegistry registry = new ItemRegistry(graph);
            Display display = new Display();
            
			// initialize renderers
			TextItemRenderer nodeRenderer = new TextItemRenderer();
			nodeRenderer.setMaxTextWidth(75);
			nodeRenderer.setAbbrevType(StringAbbreviator.NAME);
            nodeRenderer.setRoundedCorner(8,8);
			
			Renderer edgeRenderer = new DefaultEdgeRenderer();
			
			registry.setRendererFactory(new DefaultRendererFactory(
				nodeRenderer, edgeRenderer));
			
			// initialize action pipelines
			ActionList layout = new ActionList(registry);
            layout.add(new TreeFilter(true));
            layout.add(new RadialTreeLayout());
            layout.add(new DemoColorFunction(3));
            
            ActionList update = new ActionList(registry);
            update.add(new DemoColorFunction(3));
            update.add(new RepaintAction());
            
            ActionList animate = new ActionList(registry, 1500, 20);
            animate.setPacingFunction(new SlowInSlowOutPacer());
            animate.add(new PolarLocationAnimator());
            animate.add(new ColorAnimator());
            animate.add(new RepaintAction());
            animate.alwaysRunAfter(layout);
            
            // initialize display 
            display.setItemRegistry(registry);
            display.setSize(700,700);
            display.setBackground(Color.WHITE);
            display.addControlListener(new FocusControl(layout));
            display.addControlListener(new FocusControl(0,FocusManager.HOVER_KEY));
            display.addControlListener(new DragControl());
            display.addControlListener(new PanControl());
            display.addControlListener(new ZoomControl());
            display.addControlListener(new NeighborHighlightControl(update));
            
            registry.getFocusManager().putFocusSet(
                    FocusManager.HOVER_KEY, new DefaultFocusSet());

			// create and display application window
            setDefaultCloseOperation(EXIT_ON_CLOSE);
			getContentPane().add(display, BorderLayout.CENTER);
			pack();
			setVisible(true);
            
            // run filter+layout, and perform initial animation
            layout.runNow();
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
    public class DemoColorFunction extends ColorFunction {
	    private Color graphEdgeColor = Color.LIGHT_GRAY;
        private Color highlightColor = new Color(50,50,255);
        private Color focusColor = new Color(255,50,50);
        private ColorMap colorMap;
	   
	   	public DemoColorFunction(int thresh) {
	   	    colorMap = new ColorMap(
	   	        ColorMap.getInterpolatedMap(thresh+1, Color.RED, Color.BLACK),
	   	        0, thresh);
	   	} //
	   
	   	public Paint getFillColor(VisualItem item) {
	   		if ( item instanceof NodeItem ) {
	   			return Color.WHITE;
	   		} else if ( item instanceof AggregateItem ) {
	   			return Color.LIGHT_GRAY;
	   		} else if ( item instanceof EdgeItem ) {
	   			return getColor(item);
	   		} else {
	   			return Color.BLACK;
	   		}
	   	} //
	   
		public Paint getColor(VisualItem item) {
		    if ( item.isFocus() ) {
		        return focusColor;
		    } else if ( item.isHighlighted() ) {
                return highlightColor;
            } else if (item instanceof NodeItem) {
                int d = ((NodeItem)item).getDepth();
                return colorMap.getColor(d);
			} else if (item instanceof EdgeItem) {
				EdgeItem e = (EdgeItem) item;
				if ( e.isTreeEdge() ) {
					int d, d1, d2;
                    d1 = ((NodeItem)e.getFirstNode()).getDepth();
                    d2 = ((NodeItem)e.getSecondNode()).getDepth();
                    d = Math.max(d1, d2);
                    return colorMap.getColor(d);
				} else {
					return graphEdgeColor;
				}
			} else {
				return Color.BLACK;
			}
		} //
   } // end of inner class DemoColorFunction

} // end of classs RadialGraphDemo
