package prefuse.demos.pap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.expression.AndPredicate;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphLib;
import prefuse.util.PrefuseLib;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JPrefuseApplet;
import prefuse.util.ui.JValueSlider;
import prefuse.util.ui.UILib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

/**
 * This demo is based on the prefuse.demos.applets.GraphView.java demo and shows
 * some simple uses of predicates and of the prefuse expression language. While
 * implementing I recognized that everything from this demo is already shown in
 * other demos, so you may get what you want by searching for 'expression' or 
 * 'predicate' within all files in the prefuse.demos folder. 
 * For example the process of creating and registering a new function or predicate
 * in the prefuse expression language and creating derived columns is also
 * shown in prefuse.demos.ZipDecode.java.
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author Bj�rn Kruse
 */
public class GraphViewPredicatesDemo extends JPrefuseApplet {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
	private static LabelRenderer tr;
    
    public void init() {
        UILib.setPlatformLookAndFeel();
        JComponent graphview = demo("data/pap/socialnet2.xml", "name");
        this.getContentPane().add(graphview);
        this.setSize(new Dimension(800, 400));
    }

    public static JComponent demo(String datafile, String label) {
        Graph g = null;
        if ( datafile == null ) {
            g = GraphLib.getGrid(15,15);
        } else {
            try {
                g = new GraphMLReader().readGraph(datafile);
            } catch ( Exception e ) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return demo(g, label);
    }
    
    public static JComponent demo(Graph g, String label) {
    	/* 
    	 * some Predicates that will be used throughout the demo, especially the
    	 * filter predicate will be used from the checkboxes to color the nodes  
    	 */
        final AndPredicate filter = new AndPredicate();
        final Predicate ageGreater24 = ExpressionParser.predicate("age > 24");
        final Predicate weightLess50 = ExpressionParser.predicate("weight < 50");
        final Predicate nameStartswithT = 
        	ExpressionParser.predicate("LEFT(name, 1) == 'T'");
        final Predicate nameContainsA = 
        	ExpressionParser.predicate("POSITION('a', name) != -1");

        // demonstrate the use of predicates to add new derived columns
    	g.addColumn("fullage", "age >= 18");
    	g.addColumn(VisualItem.LABEL, "CONCAT(name, '(', age, ')')");
    	g.addColumn("multiline", "CONCAT(name, '\\n', " +
    			"'is ', age, ' years old', '\\n', " +
    			"'and weights ', weight, ' kilos')");
    	
        // create a new, empty visualization for our data
        final Visualization vis = new Visualization();
        VisualGraph vg = vis.addGraph(graph, g);
        vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);
        
        TupleSet focusGroup = vis.getGroup(Visualization.FOCUS_ITEMS); 
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem)
            {
                for ( int i=0; i<rem.length; ++i )
                    ((VisualItem)rem[i]).setFixed(false);
                for ( int i=0; i<add.length; ++i ) {
                    ((VisualItem)add[i]).setFixed(false);
                    ((VisualItem)add[i]).setFixed(true);
                }
                vis.run("draw");
            }
        });
        
        // set up the renderers
        tr = new LabelRenderer(label);
        tr.setRoundedCorner(8, 8);
        vis.setRendererFactory(new DefaultRendererFactory(tr));
        

        
        // -- set up the actions ----------------------------------------------
        
        int maxhops = 4, hops = 4;
        final GraphDistanceFilter gdf = new GraphDistanceFilter(graph, hops);

        ActionList draw = new ActionList();
        draw.add(gdf);
        draw.add(new ColorAction(nodes, VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255)));
        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0,0,0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));
        
        Predicate highlight_and_filter = 
        	new AndPredicate(filter, ExpressionParser.predicate("_highlight"));

        /*
         * NOTE: the highlight_and_filter rule (rule is nothing else than a 
         * predicate) has to be added to the ColorAction BEFORE one of the rules
         * 'filter' or '_highlight' is added. The reason: Rules are evaluated in
         * the order in which they are added to the ColorAction, so earlier 
         * rules will have precedence over rules added later. See ColorAction
         * javadocs comment.
         * 
         */
        ColorAction fill = new ColorAction(nodes, 
                VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
        fill.add("_fixed", ColorLib.rgb(255,100,100));
        fill.add(highlight_and_filter, ColorLib.rgb(200,155,255));
        fill.add("_highlight", ColorLib.rgb(255,200,125));
        fill.add(filter, ColorLib.rgb(100,100,255));
        
        ForceDirectedLayout fdl = new ForceDirectedLayout(graph);
        ForceSimulator fsim = fdl.getForceSimulator();
        fsim.getForces()[0].setParameter(0, -1.2f);
        fsim.getForces()[2].setParameter(1, 100f);
        
        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(fdl);
        animate.add(fill);
        animate.add(new RepaintAction());
        
        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        vis.putAction("draw", draw);
        vis.putAction("layout", animate);
        vis.runAfter("draw", "layout");
        
        
        // --------------------------------------------------------------------
        // STEP 4: set up a display to show the visualization
        
        Display display = new Display(vis);
        display.setSize(500,500);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);
        
        // main display controls
        display.addControlListener(new FocusControl(1));
        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new NeighborHighlightControl());
        display.addControlListener(new ControlAdapter() {
        	@Override
        	public void itemClicked(VisualItem item, MouseEvent e) {
        		Display d = item.getVisualization().getDisplay(0);
        		d.animatePanToAbs(new Point2D.Double(item.getX(), item.getY()), 1000);
        	}
        });


        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);
        
        // --------------------------------------------------------------------        
        // STEP 5: launching the visualization
        
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // create a panel for editing force values
        //first box
        final JValueSlider slider = new JValueSlider("Distance", 0, maxhops, hops);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                gdf.setDistance(slider.getValue().intValue());
                vis.run("draw");
            }
        });
        slider.setBackground(Color.WHITE);
        slider.setPreferredSize(new Dimension(300,30));
        slider.setMaximumSize(new Dimension(300,30));
        
        Box firstBox = new Box(BoxLayout.Y_AXIS);
        firstBox.setMaximumSize(new Dimension(300,30));
        firstBox.add(slider);
        firstBox.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
        panel.add(firstBox);
        panel.add(Box.createVerticalStrut(5));

        //second box
        JComboBox labelsCombobox = new JComboBox(new String[]{"normal", "extended", "multiline"});
        labelsCombobox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        String action = (String)cb.getSelectedItem();
		        if (action.equals("normal"))
		        	tr.setTextField("name");
		        if (action.equals("extended"))
		        	tr.setTextField(VisualItem.LABEL);
		        if (action.equals("multiline"))
		        	tr.setTextField("multiline");
			}
        });
        
        Box secondBox = new Box(BoxLayout.X_AXIS);
        secondBox.setMaximumSize(new Dimension(300,30));
        secondBox.add(new JLabel("Label:   "));
        secondBox.add(labelsCombobox);
        secondBox.setBorder(BorderFactory.createTitledBorder("Choose a label"));
        panel.add(secondBox);
        panel.add(Box.createVerticalStrut(5));
        
        //third box
        JCheckBox nameContainsCB = new JCheckBox("Name contains 'A'", false);
        JCheckBox nameStartswithCB = new JCheckBox("Name starts with 'T'", false);
        JCheckBox ageCB = new JCheckBox("Age greater than 24", false);
        JCheckBox weightCB = new JCheckBox("Weight smaller than 50", false);

        nameContainsCB.setBackground(Color.white);
        nameStartswithCB.setBackground(Color.white);
        ageCB.setBackground(Color.white);
        weightCB.setBackground(Color.white);

        nameContainsCB.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		JCheckBox cb = (JCheckBox) e.getSource();
        		if (cb.isSelected()) filter.add(nameContainsA);
        		else filter.remove(nameContainsA);
        	}
        });
        nameStartswithCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				if (cb.isSelected()) filter.add(nameStartswithT);
				else filter.remove(nameStartswithT);
			}
        });
        ageCB.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		JCheckBox cb = (JCheckBox) e.getSource();
        		if (cb.isSelected()) filter.add(ageGreater24);
        		else filter.remove(ageGreater24);
        	}
        });
        weightCB.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		JCheckBox cb = (JCheckBox) e.getSource();
        		if (cb.isSelected()) filter.add(weightLess50);
        		else filter.remove(weightLess50);
        	}
        });
        
        Box checkboxesBox = new Box(BoxLayout.Y_AXIS);
        checkboxesBox.add(nameContainsCB);
        checkboxesBox.add(nameStartswithCB);
        checkboxesBox.add(ageCB);
        checkboxesBox.add(weightCB);
        
        Box thirdBox = new Box(BoxLayout.X_AXIS);
        thirdBox.setMaximumSize(new Dimension(300,30));
        thirdBox.add(checkboxesBox);
        thirdBox.setBorder(BorderFactory.createTitledBorder("Choose a restriction to highlight"));
        panel.add(thirdBox);
        panel.add(Box.createVerticalStrut(5));
        
        panel.add(Box.createVerticalGlue());
        
        // create a new JSplitPane to present the interface
        JSplitPane split = new JSplitPane();
        split.setLeftComponent(display);
        split.setRightComponent(panel);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(550);
        split.setBackground(Color.WHITE);
        
        // position and fix the default focus node
        NodeItem focus = (NodeItem)vg.getNode(0);
        PrefuseLib.setX(focus, null, 400);
        PrefuseLib.setY(focus, null, 250);
        focusGroup.setTuple(focus);

        // now we run our action list and return
        return split;
    }
    
    
} // end of class GraphView
