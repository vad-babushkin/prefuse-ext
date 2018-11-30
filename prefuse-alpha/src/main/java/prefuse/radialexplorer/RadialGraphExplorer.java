package prefuse.radialexplorer;

import java.awt.Color;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
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
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.ColorMap;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;
import edu.berkeley.guir.prefuse.util.display.ExportDisplayAction;
import edu.berkeley.guir.prefusex.controls.DragControl;
import edu.berkeley.guir.prefusex.controls.FocusControl;
import edu.berkeley.guir.prefusex.controls.NeighborHighlightControl;
import edu.berkeley.guir.prefusex.controls.PanControl;
import edu.berkeley.guir.prefusex.controls.ZoomControl;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceConfigAction;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;

/**
 * Demo application showcasing the use of an animated radial tree layout to
 * visualize a graph.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class RadialGraphExplorer extends JFrame {

	public static final String GRAPH_FRIENDSTER = "/friendster.xml";
	public static final String GRAPH_TERROR     = "/terror.xml";
		
	private ItemRegistry registry;
	private Graph g;
	private Display display;
	private ForceSimulator fsim;
	private RadialTreeLayout radialLayout;
    private ActionList layout, update, animate, forces;
    
    private boolean runForces = true;
    
    public static void main(String[] args) {
        String infile = GRAPH_FRIENDSTER;
        if ( args.length > 0 )
            infile = args[0];
        new RadialGraphExplorer(infile);
    } //
		
	public RadialGraphExplorer(String datafile) {
	    super("Radial Graph Explorer -- "+datafile);
		try {
		    // create graph and registry
		    URL input = getClass().getResource(datafile);
            g = new XMLGraphReader().loadGraph(input);
            registry = new ItemRegistry(g);
            
            // intialize renderers
            TextItemRenderer nodeR = new TextItemRenderer();
            nodeR.setRoundedCorner(8,8);
            nodeR.setMaxTextWidth(75);
            nodeR.setAbbrevType(StringAbbreviator.NAME);
            
            Renderer edgeR = new DefaultEdgeRenderer();
			
            registry.setRendererFactory(
                    new DefaultRendererFactory(nodeR, edgeR));
            
            // initialize action lists
            ColorFunction colorFunction = new RadialColorFunction(3);
            
            layout = new ActionList(registry);
            layout.add(new TreeFilter(true));
            layout.add((radialLayout=new RadialTreeLayout()));
            layout.add(colorFunction);
            
            animate = new ActionList(registry,1500,20);
            animate.setPacingFunction(new SlowInSlowOutPacer());
            animate.add(new PolarLocationAnimator());
            animate.add(new ColorAnimator());
            animate.add(new RepaintAction());
            animate.alwaysRunAfter(layout);
            
            update = new ActionList(registry);
            update.add(colorFunction);
            update.add(new RepaintAction());
            
            // add force repulsion
            fsim = new ForceSimulator();
            fsim.addForce(new NBodyForce(-0.1f, 15f, 0.9f));
            fsim.addForce(new DragForce());
            
            forces = new ActionList(registry, 1000);
            forces.add(new ForceDirectedLayout(fsim, true));
            forces.add(new RepaintAction());
			forces.alwaysRunAfter(animate);
            
            // initialize display
			display = new Display(registry);
            display.setSize(600,600);
            display.setBackground(Color.WHITE);
            display.addControlListener(new FocusControl(layout));
            display.addControlListener(new DragControl());
            display.addControlListener(new PanControl());
            display.addControlListener(new ZoomControl());
            display.addControlListener(new NeighborHighlightControl(update));
            
			// create and display application window
            initMenus();
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			getContentPane().add(display);
			pack();
			setVisible(true);
			
			// start action lists
            layout.runNow();
		} catch ( Exception e ) {
			e.printStackTrace();
		}	
	} //
	
	private void initMenus() {
        JMenuItem openItem   = new JMenuItem("Open...");
        JMenuItem exportItem = new JMenuItem("Export Image...");
        JMenuItem exitItem   = new JMenuItem("Exit");
        final JCheckBoxMenuItem scaleItem  = new JCheckBoxMenuItem("Toggle Layout AutoScale", true);
        final JCheckBoxMenuItem forcesItem = new JCheckBoxMenuItem("Toggle Forces", true);
        JMenuItem paramItem = new JMenuItem("Configure Forces..."); 
        
        exportItem.addActionListener(new ExportDisplayAction(display));
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                System.exit(0);
            } //
        });
        scaleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean val = scaleItem.getState();
                radialLayout.setAutoScale(val);
            } //
        });
        forcesItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean val = forcesItem.getState();
                forces.setEnabled(val);
            } //
        });
        paramItem.addActionListener(new ForceConfigAction(this, fsim));
        
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(openItem);
        fileMenu.add(exportItem);
        fileMenu.add(exitItem);
        
        JMenu optMenu = new JMenu("Options");
        optMenu.add(scaleItem);
        optMenu.add(forcesItem);
        optMenu.add(paramItem);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(optMenu);
        this.setJMenuBar(menuBar);
	} //
	
    public class RadialColorFunction extends ColorFunction {
	    private Color graphEdgeColor = Color.LIGHT_GRAY;
        private Color highlightColor = new Color(50,50,255);
        private Color focusColor = new Color(255,50,50);
        private ColorMap colorMap;
	   
	   	public RadialColorFunction(int thresh) {
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
   } // end of inner class RadialColorFunction

} // end of classs RadialGraphExplorer
