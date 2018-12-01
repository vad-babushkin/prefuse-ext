package ca.utoronto.cs.prefuseextensions.render;

import java.awt.Shape;

import ca.utoronto.cs.prefuseextensions.geom.Sector2D;

import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.VisualItem;

/**
 * Renders a Sector2D shape for a node.
 * Assumes the columns innerRadius, outerRadius, startAngle, and angleExtent are already in the VisualItem schema.  
 * They are added by StarburstLayout, but if that layout isn't being used, they need to be added and set in another way.
 * 
 * @version 1.0
 * @author <a href="http://www.cs.utoronto.ca/~ccollins">Christopher Collins</a>
 */
public class SectorRenderer extends AbstractShapeRenderer {
        
    /**
     * The shape instance to use for rendering.  prefuse rendering recommends a single shape 
     * instance reused to minimize instantiation/garbage collection.
     */
	private Sector2D sector2D = new Sector2D();
    
    /**
     * Renderer for Sector2D nodes (wedges from an annulus).
     */
	public SectorRenderer() {
        super();
    }

    /**
     * Using the paramters stored in the VisualItem, set the position and size of the sector2d.
     * Assumes the columns innerRadius, outerRadius, startAngle, and angleExtent are already in the VisualItem schema.  
     * They are added by StarburstLayout, but if that layout isn't being used, they need to be added and set in another way.
     * 
     * @param item the VisualItem to set the shape for
     * @return the shape for the given visual item
     */ 
    protected Shape getRawShape(VisualItem item) {
        double x = item.getX();
        if ( Double.isNaN(x) || Double.isInfinite(x) )
            x = 0;
        double y = item.getY();
        if ( Double.isNaN(y) || Double.isInfinite(y) )
            y = 0;
        
        sector2D.setSectorByCenter(x, y, 
                item.getDouble("innerRadius"), 
                item.getDouble("outerRadius"),
                item.getDouble("startAngle"), 
                item.getDouble("angleExtent"));
        return sector2D;
    }
}
