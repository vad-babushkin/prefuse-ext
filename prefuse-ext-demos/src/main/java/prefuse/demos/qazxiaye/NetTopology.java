package prefuse.demos.qazxiaye;/*
 * Copyright @ Ye XIA <qazxiaye@126.com>
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.controls.ControlAdapter;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.demos.qazxiaye.DeviceGenerator;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.Renderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

public class NetTopology extends JPanel {
    static final String GRAPH = "graph";
    static final String AGGR = "aggregates";
    static final String NODES = "graph.nodes";
    static final String EDGES = "graph.edges";

    static Graph graph;
    static Visualization vis;
    static Display display;

    public NetTopology() {
        super();
        setVisible(true);

        graph = DeviceGenerator.getGraph();
        vis = DeviceGenerator.getVis();

        setUpRenderers();
        setUpActions();
        setUpDisplay();

        add(display);

        vis.run("layout");
        vis.run("color");

        NetUpdater updater = new NetUpdater();
        updater.start();
    }

    private void setUpRenderers() {
        LabelRenderer nodeRenderer = new LabelRenderer("name", "img");

        nodeRenderer.setMaxImageDimensions(60, 60);
        try {
            BufferedImage image = ImageIO.read(new File("prefuse-ext-demos/src/main/resources/qazxiaye/images/device/Box.png"));
            nodeRenderer.getImageFactory().addImage("Box", image);

            image = ImageIO.read(new File("prefuse-ext-demos/src/main/resources/qazxiaye/images/device/BS.png"));
            nodeRenderer.getImageFactory().addImage("BS", image);

            image = ImageIO.read(new File("prefuse-ext-demos/src/main/resources/qazxiaye/images/device/Cloud.png"));
            nodeRenderer.getImageFactory().addImage("Cloud", image);

            image = ImageIO.read(new File("prefuse-ext-demos/src/main/resources/qazxiaye/images/device/Mobile.png"));
            nodeRenderer.getImageFactory().addImage("Mobile", image);

            image = ImageIO.read(new File("prefuse-ext-demos/src/main/resources/qazxiaye/images/device/PC.png"));
            nodeRenderer.getImageFactory().addImage("PC", image);

            image = ImageIO.read(new File("prefuse-ext-demos/src/main/resources/qazxiaye/images/device/POP_high.png"));
            nodeRenderer.getImageFactory().addImage("POP_high", image);

            image = ImageIO.read(new File("prefuse-ext-demos/src/main/resources/qazxiaye/images/device/POP_low.png"));
            nodeRenderer.getImageFactory().addImage("POP_low", image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        nodeRenderer.setImagePosition(Constants.TOP);

        EdgeRenderer edgeRenderer = new EdgeRenderer();
        edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
        edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
        edgeRenderer.setVerticalAlignment1(Constants.CENTER);
        edgeRenderer.setVerticalAlignment2(Constants.CENTER);
        edgeRenderer.setDefaultLineWidth(3.5);

        Renderer polyRenderer = new PolygonRenderer(Constants.POLY_TYPE_CURVE);
        ((PolygonRenderer) polyRenderer).setCurveSlack(0.15f);

        DefaultRendererFactory rendererFactory = new DefaultRendererFactory(nodeRenderer, edgeRenderer);
        rendererFactory.add("ingroup('aggregates')", polyRenderer);

        vis.setRendererFactory(rendererFactory);
    }

    private void setUpActions() {
        ActionList layout = new ActionList();
        layout.add(new NetLayout("graph", Constants.ORIENT_TOP_BOTTOM, 20, 50, 50));

        ColorAction text = new ColorAction(NODES, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0));
        text.add("_hover", ColorLib.rgb(255, 0, 0));

        ColorAction edges = new EdgeColorAction(EDGES, VisualItem.STROKECOLOR);
        // ColorAction edges = new ColorAction(EDGES, VisualItem.STROKECOLOR,
        // ColorLib.gray(200));

        ColorAction aggStroke = new ColorAction(AGGR, VisualItem.STROKECOLOR, ColorLib.gray(200));
        aggStroke.add("_hover", ColorLib.rgb(255, 100, 100));

        int[] palette = new int[] { ColorLib.rgba(255, 200, 200, 150), ColorLib.rgba(200, 255, 200, 150),
                ColorLib.rgba(200, 200, 255, 150) };
        ColorAction aggFill = new DataColorAction(AGGR, "id", Constants.NOMINAL, VisualItem.FILLCOLOR, palette);

        ActionList color = new ActionList();
        color.add(text);
        color.add(edges);
        color.add(aggStroke);
        color.add(aggFill);
        color.add(new AggregateLayout(AGGR));
        color.add(new RepaintAction());

        ActionList hover = new ActionList();
        hover.add(text);
        hover.add(aggStroke);
        hover.add(new RepaintAction());

        vis.putAction("layout", layout);
        vis.putAction("color", color);
        vis.putAction("hover", hover);
    }

    private void setUpDisplay() {
        display = new Display(vis);
        display.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        display.setHighQuality(true);

        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new AggregateDragControl());

        display.setCustomToolTip(new javax.swing.JToolTip());
        display.addControlListener(new HoverToolTip());
    }

    class NetUpdater extends Thread {
        @Override
        public void run() {
            while (true) {
                DeviceGenerator.UpdateMobiles();

                vis.run("layout");
                vis.run("color");

                // new ExportDisplayAction(display).actionPerformed(null);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class NetLayout extends NodeLinkTreeLayout {
        int marginY = 100;
        int marginX = 200;

        public NetLayout(String arg0, int arg1, double arg2, double arg3, double arg4) {
            super(arg0, arg1, arg2, arg3, arg4);
        }

        @Override
        public void run(double frac) {
            setLayoutRoot((NodeItem) vis.getVisualItem(NODES, graph.getNode(0)));

            super.run(frac);

            double rootX = getLayoutRoot().getX();
            double rootY = getLayoutRoot().getY();

            int cloudPadding = 0;
            int pop0Padding = 0;

            double top, bottom, left, right;
            left = rootX;
            right = rootX;
            top = rootY;
            bottom = rootY;

            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                VisualItem vItem = (VisualItem) iter.next();

                if (vItem.getGroup().equals(NODES)) {
                    NodeItem nItem = (NodeItem) vItem;

                    Integer i = (Integer) nItem.get("level");
                    vItem.setY(rootY + marginY * i);

                    if (i == 0) // pop level 0
                    {
                        vItem.setX(rootX + pop0Padding);

                        pop0Padding = UpdatePadding(pop0Padding);
                    } else if (i == -1) // cloud
                    {
                        vItem.setX(rootX + cloudPadding);

                        cloudPadding = UpdatePadding(cloudPadding);
                    }

                    left = Math.min(left, vItem.getX());
                    right = Math.max(right, vItem.getX());
                    top = Math.min(top, vItem.getY());
                    bottom = Math.max(bottom, vItem.getY());
                }
            }

            display.panToAbs(new Point2D.Double((left + right) / 2, (top + bottom) / 2));
        }

        private int UpdatePadding(int in) {
            int result;

            if (in <= 0) {
                result = marginX - in;
            } else {
                result = -in;
            }

            return result;
        }
    }

    public static void main(String[] argv) {
        new DeviceGenerator("prefuse-ext-demos/src/main/resources/qazxiaye/devices.xml");

        JFrame f = new JFrame();
        f.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
        f.add(new NetTopology());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
}

class HoverToolTip extends ControlAdapter {
    @Override
    public void itemEntered(VisualItem item, MouseEvent e) {
        String content = "<html>";

        if (item instanceof NodeItem) {
            content += "device : " + item.get("name");
            content += "<br>speed : " + item.get("speed");
            content += "<br>core nb : " + item.get("core");

            if (item.get("img").equals("Cloud")) {
                content += "<br>" + "host nb : " + item.get("clusterNb");
            }
        } else if (item instanceof EdgeItem) {
            content += "" + item.get("link");
        } else if (item instanceof AggregateItem) {
            content += "house " + item.get("id");
        } else {
            return;
        }

        content += "</html>";

        Visualization v = item.getVisualization();
        Display d = v.getDisplay(0);
        d.setToolTipText(content);
    }

    @Override
    public void itemExited(VisualItem item, MouseEvent e) {
        Visualization v = item.getVisualization();
        Display d = v.getDisplay(0);
        d.setToolTipText(null);
    }
}

class EdgeColorAction extends ColorAction {
    final int orange = ColorLib.rgb(255, 128, 0);
    final int red = ColorLib.rgb(255, 100, 100);
    final int green = ColorLib.rgb(0, 200, 0);
    final int none = ColorLib.gray(100);

    public EdgeColorAction(String group, String field) {
        super(group, field);
    }

    @Override
    public int getColor(VisualItem item) {
        if (item instanceof EdgeItem) {
            int i = (Integer) item.get("state");

            if (i == 0) {
                return green;
            } else if (i == 1) {
                return orange;
            } else {
                return red;
            }
        }

        return none;
    }
}

// class AggregateLayout modified from prefuse AggregateDemo
class AggregateLayout extends Layout {
    private final int m_margin = 5; // convex hull pixel margin
    private double[] m_pts; // buffer for computing convex hulls

    public AggregateLayout(String aggrGroup) {
        super(aggrGroup);
    }

    @Override
    public void run(double frac) {
        AggregateTable aggr = (AggregateTable) m_vis.getGroup(m_group);
        // do we have any to process?
        int num = aggr.getTupleCount();
        if (num == 0) {
            return;
        }

        // update buffers
        int maxsz = 0;
        for (Iterator aggrs = aggr.tuples(); aggrs.hasNext();) {
            maxsz = Math.max(maxsz, 4 * 2 * ((AggregateItem) aggrs.next()).getAggregateSize());
        }
        if (m_pts == null || maxsz > m_pts.length) {
            m_pts = new double[maxsz];
        }

        // compute and assign convex hull for each aggregate
        Iterator aggrs = m_vis.visibleItems(m_group);
        while (aggrs.hasNext()) {
            AggregateItem aitem = (AggregateItem) aggrs.next();

            int idx = 0;
            if (aitem.getAggregateSize() == 0) {
                continue;
            }
            VisualItem item = null;
            Iterator iter = aitem.items();
            while (iter.hasNext()) {
                item = (VisualItem) iter.next();
                if (item.isVisible()) {
                    addPoint(m_pts, idx, item, m_margin);
                    idx += 2 * 4;
                }
            }
            // if no aggregates are visible, do nothing
            if (idx == 0) {
                continue;
            }

            // compute convex hull
            double[] nhull = GraphicsLib.convexHull(m_pts, idx);

            // prepare viz attribute array
            float[] fhull = (float[]) aitem.get(VisualItem.POLYGON);
            if (fhull == null || fhull.length < nhull.length) {
                fhull = new float[nhull.length];
            } else if (fhull.length > nhull.length) {
                fhull[nhull.length] = Float.NaN;
            }

            // copy hull values
            for (int j = 0; j < nhull.length; j++) {
                fhull[j] = (float) nhull[j];
            }
            aitem.set(VisualItem.POLYGON, fhull);
            aitem.setValidated(false); // force invalidation
        }
    }

    private void addPoint(double[] pts, int idx, VisualItem item, int growth) {
        Rectangle2D b = item.getBounds();
        double minX = (b.getMinX()) - growth, minY = (b.getMinY()) - growth;
        double maxX = (b.getMaxX()) + growth, maxY = (b.getMaxY()) + growth;
        pts[idx] = minX;
        pts[idx + 1] = minY;
        pts[idx + 2] = minX;
        pts[idx + 3] = maxY;
        pts[idx + 4] = maxX;
        pts[idx + 5] = minY;
        pts[idx + 6] = maxX;
        pts[idx + 7] = maxY;
    }
}
