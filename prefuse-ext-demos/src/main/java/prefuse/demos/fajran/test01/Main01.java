package prefuse.demos.fajran.test01;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.RandomLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.Control;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.query.SearchQueryBinding;
import prefuse.data.search.SearchTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.demos.fajran.ubuntupkg.DataReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.ui.JSearchPanel;
import prefuse.visual.VisualItem;

public class Main01 extends JPanel {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		
		/*
		 * Graph, Visualization, and Display
		 */
		Graph graph = loadGraph(args);
		Visualization vis = new Visualization();
		vis.add("graph", graph);
		
		JPanel mainPanel = new Main01(vis);
		frame.getContentPane().add(mainPanel);
		
		frame.pack();
		frame.setVisible(true);
		
		vis.run("init");
		
	}
	
	private Display m_display;
	private Visualization m_vis;
	private JSearchPanel searchPanel;
	
	public Main01(Visualization vis) {
	
		m_vis = vis;
		m_display = new Display(vis);
		m_display.setSize(800, 600);
		
		initNodeEdgeRenderer();
		initRenderer();
		initControl();
		initFocusAction();
		initSearchPanel();
		
		setLayout(new BorderLayout());
		add(m_display, BorderLayout.CENTER);
		add(searchPanel, BorderLayout.SOUTH);
		
	}
	
	private void initFocusAction() {
		TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
		focusGroup.addTupleSetListener(new TupleSetListener() {

			public void tupleSetChanged(TupleSet tset, Tuple[] add,
					Tuple[] rem) {
				
				searchPanel.setQuery("");
				
				m_vis.cancel("filter");
				m_vis.cancel("search");
				
				int i;
				for (i=0; i<rem.length; i++) {
					((VisualItem)rem[i]).setFixed(false);
				}
				for (i=0; i<add.length; i++) {
					((VisualItem)add[i]).setFixed(false);
					((VisualItem)add[i]).setFixed(true);
				}
				
				m_vis.run("filter");
			}
        	
        });
	}
	
	private void initSearchPanel() {
		
		ActionList searchAction = new ActionList();
		searchAction.add(new DirectedGraphDistanceFilter("graph", Visualization.SEARCH_ITEMS, 0));
		searchAction.add(m_vis.getAction("color"));
		searchAction.add(new RepaintAction());
		m_vis.putAction("search", searchAction);
		
		SearchQueryBinding sqb = new SearchQueryBinding(m_vis.getGroup("graph.nodes"), "pkg");
		
		searchPanel = sqb.createSearchPanel();
		searchPanel.setShowResultCount(true);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
        searchPanel.setFont(FontLib.getFont("Tahoma", Font.PLAIN, 11));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setForeground(Color.BLACK);
		
		final SearchTupleSet sts = sqb.getSearchSet();
        sts.addTupleSetListener(new TupleSetListener() {

			public void tupleSetChanged(TupleSet tset, Tuple[] add,
					Tuple[] rem) {

				if (tset.getTupleCount() <= 25) {
					m_vis.cancel("search");
					m_vis.cancel("filter");
					
					//System.out.println("search tuple set changed");
					int i;
					for (i=0; i<rem.length; i++) {
						((VisualItem)rem[i]).setFixed(false);
					}
					for (i=0; i<add.length; i++) {
						((VisualItem)add[i]).setFixed(false);
						((VisualItem)add[i]).setFixed(true);
					}
					
					m_vis.run("search");
				}
				
			}
        
        });
        
        m_vis.addFocusGroup(Visualization.SEARCH_ITEMS, sts);
	}
	
	private void initNodeEdgeRenderer() {
		
		// Label
		LabelRenderer r = new LabelRenderer("pkg");
		r.setRoundedCorner(8, 8);
		m_vis.setRendererFactory(new DefaultRendererFactory(r));
		
		// Edges
		ColorAction edges = new ColorAction("graph.edges", VisualItem.STROKECOLOR, ColorLib.gray(200));
		
		// Text
		ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR, ColorLib.gray(0));
		
		// Nodes
        ColorAction fill = new ColorAction("graph.nodes", VisualItem.FILLCOLOR, ColorLib.rgb(200,200,255));
		
        /* Action
         */
        
		ActionList color = new ActionList();
		color.add(fill);
		color.add(text);
		color.add(edges);
		color.add(new RepaintAction());
		
		m_vis.putAction("color", color);

	}
	
	private void initRenderer() {
		
		// Node Filter
		DirectedGraphDistanceFilter nodeFilter = new DirectedGraphDistanceFilter("graph", Visualization.FOCUS_ITEMS, -1);
		
		// Filter
		
		ActionList filter = new ActionList();
		filter.add(nodeFilter);
		filter.add(m_vis.getAction("color"));
		filter.add(new LevelLayout("graph"));
		
		m_vis.putAction("filter", filter);
		
		// Animation
		
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator("graph"));
        animate.add(new LocationAnimator("graph.nodes"));
        animate.add(new ColorAnimator("graph.nodes"));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);
        m_vis.alwaysRunAfter("filter", "animate");
		
		// Initial action
		
		ActionList initAction = new ActionList();
		initAction.add(new RandomLayout("graph.nodes"));
		m_vis.putAction("init", initAction);
		
	}
	
	private void initControl() {
		
		m_display.addControlListener(new DragControl()); // drag items around
		m_display.addControlListener(new PanControl());  // pan with background left-drag
		m_display.addControlListener(new ZoomControl()); // zoom with vertical right-drag
		m_display.addControlListener(new ZoomToFitControl());
		m_display.addControlListener(new FocusControl(1));
		
	}
	
	private static Graph loadGraph(String[] args) {
		Graph graph = null;
		try {
			String BASE = "../../ubuntu/data/mid/";
			if (args.length > 0) {
				BASE = args[0];
			}
		    graph = new DataReader().readGraph(BASE + "/nodes.txt", BASE + "/edges.txt");
		} catch ( IOException e ) {
		    e.printStackTrace();
		    System.err.println("Error loading graph. Exiting...");
		    System.exit(1);
		}

		return graph;
	}
	
}
