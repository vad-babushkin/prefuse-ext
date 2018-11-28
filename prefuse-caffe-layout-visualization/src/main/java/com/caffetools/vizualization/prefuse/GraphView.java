package com.caffetools.vizualization.prefuse;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.Activity;
import prefuse.controls.*;
import prefuse.data.Graph;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.force.ForceSimulator;
import prefuse.util.ui.JForcePanel;
import prefuse.util.ui.JValueSlider;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Main vizualization panel
 */
public class GraphView extends JPanel {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";

    Visualization visualization;

    public GraphView(Graph g, String labelFiledName, String tooltipFieldName) {
        super(new BorderLayout());

        // create a new, empty visualization for our data
        visualization = new Visualization();

        // --------------------------------------------------------------------
        // set up the renderers

        LabelRenderer tr = new LabelRenderer();
        tr.setRoundedCorner(6, 6);
        visualization.setRendererFactory(new DefaultRendererFactory(tr));

        // --------------------------------------------------------------------
        // register the data with a visualization

        // adds graph to visualization and sets renderer label field
        setGraph(g, labelFiledName);

        // fix selected focus nodes
        TupleSet focusGroup = visualization.getGroup(Visualization.FOCUS_ITEMS);
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
                for (int i = 0; i < rem.length; ++i)
                    ((VisualItem) rem[i]).setFixed(false);
                for (int i = 0; i < add.length; ++i) {
                    ((VisualItem) add[i]).setFixed(false);
                    ((VisualItem) add[i]).setFixed(true);
                }
                if (ts.getTupleCount() == 0) {
                    ts.addTuple(rem[0]);
                    ((VisualItem) rem[0]).setFixed(false);
                }
                visualization.run("draw");
            }
        });


        // --------------------------------------------------------------------
        // create actions to process the visual data

        int hops = 130;
        final GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);

        ColorAction fill = new ColorAction(nodes, VisualItem.FILLCOLOR, ColorLib.rgb(200, 200, 255)); new Color(200, 200, 255);
        fill.add(VisualItem.FIXED, ColorLib.rgb(255, 100, 100));       new Color(255, 100, 100);
        fill.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255, 200, 125));   new Color(255, 200, 125);
        DataColorAction fill2 = new DataColorAction(nodes, "type",  Constants.NOMINAL, VisualItem.FILLCOLOR, ColorLib.getCategoryPalette(15));
        fill2.add(VisualItem.FIXED, ColorLib.rgb(255, 100, 100));       new Color(255, 100, 100);
        fill2.add(VisualItem.HIGHLIGHT, ColorLib.rgb(255, 200, 125));   new Color(255, 200, 125);

        ActionList draw = new ActionList();
        draw.add(filter);
        draw.add(fill2);
        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));

        ActionList animate = new ActionList(Activity.INFINITY);
        animate.add(new ForceDirectedLayout(graph));
        animate.add(fill2);
        animate.add(new RepaintAction());

        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        visualization.putAction("draw", draw);
        visualization.putAction("layout", animate);

        visualization.runAfter("draw", "layout");


        // --------------------------------------------------------------------
        // set up a display to show the visualization

        Display display = new Display(visualization);
        display.setSize(700, 700);
        display.pan(350, 350);
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
        display.addControlListener(new ToolTipControl(tooltipFieldName));

        // overview display
//        Display overview = new Display(vis);
//        overview.setSize(290,290);
//        overview.addItemBoundsListener(new FitOverviewListener());

        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // --------------------------------------------------------------------
        // launch the visualization
        JForcePanel fpanel = getForcePanel(animate, filter, hops);


        // create a new JSplitPane to present the interface
        JSplitPane split = new JSplitPane();
        split.setLeftComponent(fpanel);
        split.setRightComponent(display);
        split.setOneTouchExpandable(true);
        split.setContinuousLayout(false);
        split.setDividerLocation(300);

        // now we run our action list
        visualization.run("draw");

        add(split);
    }

    /**
     * create a panel for editing force values
     */
    private JForcePanel getForcePanel(ActionList animate, final GraphDistanceFilter filter, int hops) {
        ForceSimulator fsim = ((ForceDirectedLayout) animate.get(0)).getForceSimulator();
        JForcePanel fpanel = new JForcePanel(fsim);

//        JPanel opanel = new JPanel();
//        opanel.setBorder(BorderFactory.createTitledBorder("Overview"));
//        opanel.setBackground(Color.WHITE);
//        opanel.add(overview);

        final JValueSlider slider = new JValueSlider("Distance", 0, hops, hops);
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                filter.setDistance(slider.getValue().intValue());
                visualization.run("draw");
            }
        });
        slider.setBackground(Color.WHITE);
        slider.setPreferredSize(new Dimension(300, 30));
        slider.setMaximumSize(new Dimension(300, 30));

        Box cf = new Box(BoxLayout.Y_AXIS);
        cf.add(slider);
        cf.setBorder(BorderFactory.createTitledBorder("Connectivity Filter"));
        fpanel.add(cf);

        //fpanel.add(opanel);

        fpanel.add(Box.createVerticalGlue());
        return fpanel;
    }

    public Visualization getVisualization() {
        return visualization;
    }

    public void setGraph(Graph g, String labelFieldName) {
        // update labeling
        DefaultRendererFactory drf = (DefaultRendererFactory)
            visualization.getRendererFactory();
        ((LabelRenderer) drf.getDefaultRenderer()).setTextField(labelFieldName);

        // update graph
        visualization.removeGroup(graph);
        VisualGraph vg = visualization.addGraph(graph, g);
        visualization.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.FALSE);

        if(vg.getNodeCount()>0) {
            VisualItem f = (VisualItem) vg.getNode(0);
            visualization.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
            f.setFixed(false);
        }
    }


    // ------------------------------------------------------------------------

    public static class FitOverviewListener implements ItemBoundsListener {
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        private Rectangle2D m_temp = new Rectangle2D.Double();
        private double m_d = 15;

        public void itemBoundsChanged(Display d) {
            d.getItemBounds(m_temp);
            GraphicsLib.expand(m_temp, 25 / d.getScale());

            double dd = m_d / d.getScale();
            double xd = Math.abs(m_temp.getMinX() - m_bounds.getMinX());
            double yd = Math.abs(m_temp.getMinY() - m_bounds.getMinY());
            double wd = Math.abs(m_temp.getWidth() - m_bounds.getWidth());
            double hd = Math.abs(m_temp.getHeight() - m_bounds.getHeight());
            if (xd > dd || yd > dd || wd > dd || hd > dd) {
                m_bounds.setFrame(m_temp);
                DisplayLib.fitViewToBounds(d, m_bounds, 0);
            }
        }
    }

} // end of class GraphView
