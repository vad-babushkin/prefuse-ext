package edu.berkeley.guir.prefuse.util.display;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.graph.Node;

/**
 * DisplayLib
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DisplayLib {

    private DisplayLib() {
        // don't instantiate
    } //
    
    public static Rectangle2D getNodeBounds(
        ItemRegistry registry, double margin, Rectangle2D b)
    {
        b.setFrame(Double.NaN,Double.NaN,Double.NaN,Double.NaN);
        synchronized ( registry ) {
	        Iterator iter = registry.getNodeItems();
	        if ( iter.hasNext() ) {
	            NodeItem nitem = (NodeItem)iter.next();
	            Rectangle2D nb = nitem.getBounds();
	            b.setFrame(nb);
	        }
	        while ( iter.hasNext() ) {   
	            NodeItem nitem = (NodeItem)iter.next();
	            Rectangle2D nb = nitem.getBounds();
	            double x1 = (nb.getMinX()<b.getMinX() ? nb.getMinX() : b.getMinX());
	            double x2 = (nb.getMaxX()>b.getMaxX() ? nb.getMaxX() : b.getMaxX());
	            double y1 = (nb.getMinY()<b.getMinY() ? nb.getMinY() : b.getMinY());
	            double y2 = (nb.getMaxY()>b.getMaxY() ? nb.getMaxY() : b.getMaxY());
	            b.setFrame(x1,y1,x2-x1,y2-y1);
	        }
        }
        b.setFrame(b.getMinX()-margin,b.getMinY()-margin,
                   b.getWidth()+2*margin,b.getHeight()+2*margin);
        return b;
    } //
    
    public static Rectangle2D getNodeBounds(
            ItemRegistry registry, double margin)
    {
        Rectangle2D b = new Rectangle2D.Double();
        return getNodeBounds(registry, margin, b);
    } //
    
    public static Point2D getCentroid(
        ItemRegistry registry, Iterator iter, Point2D p)
    {
        double cx = 0, cy = 0;
        int count = 0;
        
        while ( iter.hasNext() ) {
            Node n = (Node)iter.next();
            NodeItem nitem = null;
            if ( n instanceof NodeItem ) {
                nitem = (NodeItem)n;
            } else {
                nitem = registry.getNodeItem(n);
            }
            if ( nitem == null ) {
                continue;
            }
            double x = nitem.getX(), y = nitem.getY();
            if ( !(Double.isInfinite(x) || Double.isNaN(x)) &&
                 !(Double.isInfinite(y) || Double.isNaN(y)) )
            {
                cx += x;
                cy += y;
                count++;
            }
        }
        if ( count > 0 ) {
            cx /= count;
            cy /= count;
        }
        p.setLocation(cx, cy);
        return p;
    } //
    
    public static Point2D getCentroid(
            ItemRegistry registry, Iterator iter)
    {
        return getCentroid(registry, iter, new Point2D.Double());
    } //
    
    public static void fitViewToBounds(Display display, Rectangle2D bounds)
    {
        fitViewToBounds(display, bounds, null);
    }
    
    public static void fitViewToBounds(
        Display display, Rectangle2D bounds, Point2D center)
    {
        // init variables
        double w = display.getWidth(), h = display.getHeight();
        double cx = (center==null? bounds.getCenterX() : center.getX());
        double cy = (center==null? bounds.getCenterY() : center.getY());
        
        // compute half-widths of final bounding box around
        // the desired center point
        double wb = Math.max(cx-bounds.getMinX(),
                			 bounds.getMaxX()-cx);
        double hb = Math.max(cy-bounds.getMinY(),
   			 				 bounds.getMaxY()-cy);
        
        // compute scale factor
        //  - figure out if z or y dimension takes priority
        //  - then balance against the current scale factor
        double scale = Math.min(w/(2*wb),h/(2*hb)) / display.getScale();

        // animate to new display settings
        if ( center == null )
            center = new Point2D.Double(cx,cy);
        display.animatePanAndZoomToAbs(center,scale,2000);
    } //
    
} // end of class DisplayLib
