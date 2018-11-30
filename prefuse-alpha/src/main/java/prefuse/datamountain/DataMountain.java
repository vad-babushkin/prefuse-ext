package prefuse.datamountain;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.assignment.SizeFunction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ActivityAdapter;
import edu.berkeley.guir.prefuse.event.ActivityListener;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.ShapeRenderer;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
import edu.berkeley.guir.prefuse.util.BrowserLauncher;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceItem;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * 
 * Mar 24, 2004 - jheer - Created class
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> datamountain(AT)jheer.org
 */
public class DataMountain extends JFrame {

    private ActionList forces;
    
    public DataMountain(String datafile) {
        super("DataMountain -- "+datafile);
        
        Graph g = null;
        try {
            URL dataurl = DataMountain.class.getResource(datafile);
            g = (new XMLGraphReader()).loadGraph(dataurl);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        final ItemRegistry registry = new ItemRegistry(g);
        registry.setItemComparator(new DataMountainComparator());
        
        TextImageItemRenderer nodeRenderer = new TextImageItemRenderer() {
            BasicStroke m_stroke = new BasicStroke(2);
            public BasicStroke getStroke(VisualItem item) {
                return m_stroke;
            } //
        };
        nodeRenderer.setVerticalAlignment(
                TextImageItemRenderer.ALIGNMENT_BOTTOM);
        nodeRenderer.setRenderType(ShapeRenderer.RENDER_TYPE_DRAW);
        nodeRenderer.setHorizontalPadding(0);
        nodeRenderer.setVerticalPadding(0);
        nodeRenderer.setMaxImageDimensions(100,100);
        registry.setRendererFactory(new DefaultRendererFactory(nodeRenderer));
        
        ActionList init = new ActionList(registry);
        RandomLayout rl = new RandomLayout();
        init.add(new GraphFilter());
        init.add(rl);
        
        ActionList update = new ActionList(registry);
        update.add(new DataMountainSizeFunction());
        update.add(new ColorFunction());
        update.add(new RepaintAction());
        
        ForceSimulator fsim = new ForceSimulator();
        fsim.addForce(new NBodyForce(-0.4f, 25f, NBodyForce.DEFAULT_THETA));
        fsim.addForce(new SpringForce(8e-6f,0f));
        fsim.addForce(new DragForce());
        final ForceDirectedLayout fl = new DataMountainForceLayout(fsim);
        ActivityListener fReset = new ActivityAdapter() {
            public void activityCancelled(Activity a) {
                fl.reset(registry); 
             } //
        };
        
        // we run this to make sure the forces are stabilized
        ActionList preforce = new ActionList(registry,1000);
        preforce.add(fl);
        preforce.addActivityListener(fReset);
        
        // this will cause docs to move out of the way when dragging
        final ActionList forces = new ActionList(registry,-1);
        forces.add(fl);
        forces.add(update);
        forces.addActivityListener(fReset);
        
        Rectangle2D bounds = new Rectangle2D.Double(20,30,590,400);
        rl.setLayoutBounds(bounds);
        fl.setLayoutBounds(bounds);
        
        Display display = new Display(registry);
        display.setSize(635,450);
        display.addControlListener(new DataMountainControl(forces));
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(display);
        pack(); setVisible(true);
        
        // pre-load images
        nodeRenderer.getImageFactory().preloadImages(g.getNodes(),"image");
        
        // initialize and present the interface
        init.runNow();
        update.runAfter(preforce);
        preforce.runNow();
    } //
    
    public static void main(String[] args) {
        String infile = "/data.xml";
        if ( args.length > 0 )
            infile = args[0];
        new DataMountain(infile);
    } //
    
    public class DataMountainComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            double y1 = ((VisualItem)o1).getY(), y2 = ((VisualItem)o2).getY();
            return (y1>y2 ? 1 : (y1<y2 ? -1 : 0));
        } //
    } // end of inner class DataMountainComparator
    
    public class DataMountainSizeFunction extends SizeFunction {
        public void run(ItemRegistry registry, double frac) {
            super.run(registry,frac);
            registry.touchNodeItems();
        } //
        public double getSize(VisualItem item) {
            double y = item.getEndLocation().getY();
            return 0.2 + y/1400;
        } //
    } // end of inner class DataMountainSizeFunction
    
    public class DataMountainForceLayout extends ForceDirectedLayout {
        public DataMountainForceLayout(ForceSimulator fsim) {
            super(fsim,true,false);
        } //
        public void reset(ItemRegistry registry) {
            Iterator iter = registry.getNodeItems();
            while ( iter.hasNext() ) {
                NodeItem nitem = (NodeItem)iter.next();
                ForceItem aitem = (ForceItem)nitem.getVizAttribute("anchorItem");
                if ( aitem != null ) {
                    aitem.location[0] = (float)nitem.getEndLocation().getX();
                    aitem.location[1] = (float)nitem.getEndLocation().getY();
                }
            }
            super.reset(registry);
        } //
        protected void initSimulator(ItemRegistry registry, ForceSimulator fsim) {
            Iterator iter = registry.getNodeItems();
            while ( iter.hasNext() ) {
                NodeItem nitem = (NodeItem)iter.next();
                // get force item
                ForceItem fitem = (ForceItem)nitem.getVizAttribute("forceItem");
                if ( fitem == null ) {
                    fitem = new ForceItem();
                    nitem.setVizAttribute("forceItem", fitem);
                }
                fitem.location[0] = (float)nitem.getEndLocation().getX();
                fitem.location[1] = (float)nitem.getEndLocation().getY();
                
                // get spring anchor
                ForceItem aitem = (ForceItem)nitem.getVizAttribute("anchorItem");
                if ( aitem == null ) {
                    aitem = new ForceItem();
                    nitem.setVizAttribute("anchorItem", aitem);
                    aitem.location[0] = fitem.location[0];
                    aitem.location[1] = fitem.location[1];
                }
                
                fsim.addItem(fitem);
                fsim.addSpring(fitem, aitem, 0);
            }     
        } //        
    } // end of inner class DataMountainForceLayout
    
    public class DataMountainControl extends ControlAdapter {
        public static final String URL = "http://www.amazon.com/exec/obidos/tg/detail/-/";
        private VisualItem activeItem;
        private Activity forces;
        private Point2D down = new Point2D.Double();
        private Point2D tmp = new Point2D.Double();
        private boolean wasFixed, dragged;
        private boolean repaint = false;
        
        public DataMountainControl(Activity forces) {
            this.forces = forces;
        } //
        
        public void itemEntered(VisualItem item, MouseEvent e) {
            if (!(item instanceof NodeItem)) return;
            Display d = (Display)e.getSource();
            d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            activeItem = item;
            wasFixed = item.isFixed();
        } //
        
        public void itemExited(VisualItem item, MouseEvent e) {
            if (!(item instanceof NodeItem)) return;
            if ( activeItem == item ) {
                activeItem = null;
                item.setFixed(wasFixed);
            }
            Display d = (Display)e.getSource();
            d.setCursor(Cursor.getDefaultCursor());
        } //
        
        public void itemPressed(VisualItem item, MouseEvent e) {
            if (!(item instanceof NodeItem)) return;
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            
            // set the focus to the current node
            ItemRegistry registry = item.getItemRegistry();
            registry.getDefaultFocusSet().set(item.getEntity());
            
            item.setFixed(true);
            dragged = false;
            Display d = (Display)e.getComponent();
            down = d.getAbsoluteCoordinate(e.getPoint(), down);
            
            forces.runNow();
        } //
        
        public void itemReleased(VisualItem item, MouseEvent e) {
            if (!(item instanceof NodeItem)) return;
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            if ( dragged ) {
                activeItem = null;
                item.setFixed(wasFixed);
                dragged = false;
            }
            // clear the focus
            ItemRegistry registry = item.getItemRegistry();
            registry.getDefaultFocusSet().clear();
            
            forces.cancel();
        } //
        
        public void itemClicked(VisualItem item, MouseEvent e) {
            if (!(item instanceof NodeItem)) return;
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            if ( e.getClickCount() == 2 ) {
                String id = item.getAttribute("id");
                BrowserLauncher.showDocument(URL+id);
            }
        } //
        
        public void itemDragged(VisualItem item, MouseEvent e) {
            if (!(item instanceof NodeItem)) return;
            if (!SwingUtilities.isLeftMouseButton(e)) return;
            dragged = true;
            Display d = (Display)e.getComponent();
            tmp = d.getAbsoluteCoordinate(e.getPoint(), tmp);
            double dx = tmp.getX()-down.getX();
            double dy = tmp.getY()-down.getY();
            Point2D p = item.getLocation();
            item.updateLocation(p.getX()+dx,p.getY()+dy);
            item.setLocation(p.getX()+dx,p.getY()+dy);
            down.setLocation(tmp);
            if ( repaint )
                item.getItemRegistry().repaint();
        } //
    } // end of class DataMountainControl
    
} // end of class DataMountain
