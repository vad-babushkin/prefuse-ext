package edu.berkeley.guir.prefuse.demos;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Paint;

import javax.swing.JFrame;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.animate.ColorAnimator;
import edu.berkeley.guir.prefuse.action.animate.LocationAnimator;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.WindowedTreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.collections.DOIItemComparator;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.io.HDirTreeReader;
import edu.berkeley.guir.prefuse.graph.io.TreeReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultNodeRenderer;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.RendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.ColorMap;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.SubtreeDragControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.layout.BalloonTreeLayout;

/**
 * Visualizes a tree structure using a balloon tree layout.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class BalloonGraphDemo extends JFrame {

	public static final String TREE_CHI = "data/alpha/chitest.hdir";
    
	public static void main(String argv[]) {
	    String datafile = TREE_CHI;
	    if ( argv.length > 0 ) {
	        datafile = argv[0];
	    }
	    new BalloonGraphDemo(datafile);
	} //
	
    public BalloonGraphDemo(String inputFile) {
        super("BalloonTree Demo");
		try {
			// load graph
			TreeReader tr = new HDirTreeReader();
			Tree tree = tr.loadTree(inputFile);
			
			// create display and filter
            ItemRegistry registry = new ItemRegistry(tree);
            registry.setItemComparator(new DOIItemComparator());
            Display display = new Display();

			// initialize renderers
			TextItemRenderer nodeRenderer = new TextItemRenderer();
			nodeRenderer.setMaxTextWidth(75);
			nodeRenderer.setAbbrevType(StringAbbreviator.NAME);
            nodeRenderer.setRoundedCorner(8,8);
			
			Renderer nodeRenderer2 = new DefaultNodeRenderer();
			Renderer edgeRenderer = new DefaultEdgeRenderer();
			
			registry.setRendererFactory(new DemoRendererFactory(
				nodeRenderer, nodeRenderer2, edgeRenderer));
			
            // initialize action lists
			ActionList filter = new ActionList(registry);
            filter.add(new WindowedTreeFilter(-4,true));
            filter.add(new BalloonTreeLayout());
            filter.add(new DemoColorFunction(4));
            
            ActionList update = new ActionList(registry);
            update.add(new DemoColorFunction(4));
            update.add(new RepaintAction());
            
            ActionList animate = new ActionList(registry, 1500, 20);
            animate.setPacingFunction(new SlowInSlowOutPacer());
            animate.add(new LocationAnimator());
            animate.add(new ColorAnimator());
            animate.add(new RepaintAction());
            animate.alwaysRunAfter(filter);
            
            // initialize display
            display.setItemRegistry(registry);
            display.setSize(700,700);
            display.setBackground(Color.WHITE);
            display.addControlListener(new FocusControl(filter));
            display.addControlListener(new SubtreeDragControl());
            display.addControlListener(new PanControl());
            display.addControlListener(new ZoomControl());
            display.addControlListener(new NeighborHighlightControl(update));
            
			// create and display application window
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().add(display, BorderLayout.CENTER);
			pack();
			setVisible(true);
			
			// run filter and perform initial animation
            filter.runNow();
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
    /**
     * A RendererFactory instance that assigns node renderers of varying size
     * in response to a node's depth in the tree.
     */
    public class DemoRendererFactory implements RendererFactory {
        private Renderer nodeRenderer1;
        private Renderer nodeRenderer2;
        private Renderer edgeRenderer;
        public DemoRendererFactory(Renderer nr1, Renderer nr2, Renderer er) {
            nodeRenderer1 = nr1;
            nodeRenderer2 = nr2;
            edgeRenderer = er;
        } //
        public Renderer getRenderer(VisualItem item) {
            if ( item instanceof NodeItem ) {
                int d = ((NodeItem)item).getDepth();
                if ( d > 1 ) {
                    int r = (d == 2 ? 5 : 1);
                    ((DefaultNodeRenderer)nodeRenderer2).setRadius(r);
                    return nodeRenderer2;
                } else {
                    return nodeRenderer1;
                }
            } else if ( item instanceof EdgeItem ) {
                return edgeRenderer;
            } else {
                return null;
            }
        } //
    } // end of inner class DemoRendererFactory
	
    public class DemoColorFunction extends ColorFunction {
        private Color graphEdgeColor = Color.LIGHT_GRAY;
        private Color highlightColor = Color.BLUE;
        private ColorMap cmap; 
        
        public DemoColorFunction(int thresh) {
            cmap = new ColorMap(
                ColorMap.getInterpolatedMap(Color.RED, Color.BLACK),0,thresh);
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
            if ( item.isHighlighted() ) {
                return Color.BLUE;
            } else if (item instanceof NodeItem) {
                int d = ((NodeItem)item).getDepth();
                return cmap.getColor(d);
            } else if (item instanceof EdgeItem) {
                EdgeItem e = (EdgeItem) item;
                if ( e.isTreeEdge() ) {
                    int d, d1, d2;
                    d1 = ((NodeItem)e.getFirstNode()).getDepth();
                    d2 = ((NodeItem)e.getSecondNode()).getDepth();
                    d = Math.max(d1, d2);
                    return cmap.getColor(d);
                } else {
                    return graphEdgeColor;
                }
            } else {
                return Color.BLACK;
            }
        } //
    } // end of inner class DemoColorFunction

} // end of classs RadialGraphDemo
