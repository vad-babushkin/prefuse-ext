package edu.berkeley.guir.prefuse.render;

import java.awt.Shape;
import java.awt.geom.GeneralPath;

import edu.berkeley.guir.prefuse.VisualItem;

/**
 * Renders a polygon. Polygon points must be assigned prior to rendering,
 * binding an array of float values (alternating x,y value) to the "polygon"
 * viz attribute. For example, create an array pts of polygon points and then
 * use item.setVizAttribute("polygon", pts).
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class PolygonRenderer extends ShapeRenderer {

    public static final int EDGE_LINE  = 0;
    public static final int EDGE_QUAD  = 1;
    public static final int EDGE_CUBIC = 2;
    
    private int     edgeType = EDGE_LINE;
    private float   controlFrac = 0.10f;
    
    public PolygonRenderer() {
        this(EDGE_LINE);
    } //
    
    public PolygonRenderer(int edgeType) {
        this.edgeType = edgeType;
    } //

    public int getEdgeType() {
        return edgeType;
    } //
    
    public void setEdgeType(int edgeType) {
        this.edgeType = edgeType;
    } //
    
    /**
     * @see edu.berkeley.guir.prefuse.render.ShapeRenderer#getRawShape(edu.berkeley.guir.prefuse.VisualItem)
     */
    protected Shape getRawShape(VisualItem item) {
        float[] poly = (float[])item.getVizAttribute("polygon");
        float x = (float)item.getX();
        float y = (float)item.getY();
        
        GeneralPath path = new GeneralPath();
        path.moveTo(x+poly[0],y+poly[1]);
        if ( edgeType == EDGE_LINE ) {
            for ( int i=2; i<poly.length; i+=2 ) {
                path.lineTo(x+poly[i],y+poly[i+1]);
            }
        } else {
            // first calculate the centroid of the polygon
            float cx = 0f, cy = 0f;
            float[] centroid = (float[])item.getVizAttribute("centroid");
            if ( centroid != null && centroid.length == 2 ) {
                cx = centroid[0];
                cy = centroid[1];
            } else {
	            for ( int k=0; k<poly.length; k+=2 ) {
	                cx += poly[k];
	                cy += poly[k+1];
	            }
	            cx = cx/((float)(poly.length/2)) + x;
	            cy = cy/((float)(poly.length/2)) + y;
            }
            
            if ( edgeType == EDGE_CUBIC ) {
	            // now go around computing control points
	            int i;
	            float frac = controlFrac;
	            float amp;
	            for ( i=2; i<poly.length; i+=2 ) {
	                float x1 = x+poly[i-2], y1 = y+poly[i-1];
	                float x2 = x+poly[i],   y2 = y+poly[i+1];
	                amp = frac*(float)Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	                
	                float vx = x1-cx, vy = y1-cy;
	                float norm = (float)Math.sqrt(vx*vx+vy*vy);
	                float p1x = x1 - amp*vy/norm;
	                float p1y = y1 + amp*vx/norm;
	                
	                vx = x2-cx; vy = y2-cy;
	                norm = (float)Math.sqrt(vx*vx+vy*vy);
	                float p2x = x2 + amp*vy/norm;
	                float p2y = y2 - amp*vx/norm;
	                
	                path.curveTo(p1x,p1y,p2x,p2y,x2,y2);
	            }
	            
	            float x1 = x+poly[i-2], y1 = y+poly[i-1];
	            float x2 = x+poly[0],   y2 = y+poly[1];
	            amp = frac*(float)Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	            
	            float vx = x1-cx, vy = y1-cy;
	            float norm = (float)Math.sqrt(vx*vx+vy*vy);
	            float p1x = x1 - amp*vy/norm;
	            float p1y = y1 + amp*vx/norm;
	            
	            vx = x2-cx; vy = y2-cy;
	            norm = (float)Math.sqrt(vx*vx+vy*vy);
	            float p2x = x2 + amp*vy/norm;
	            float p2y = y2 - amp*vx/norm;
	            
	            path.curveTo(p1x,p1y,p2x,p2y,x2,y2);
            } else if ( edgeType == EDGE_QUAD ) {
	            // now go around computing control points
	            float frac = controlFrac;
	            int i;
	            float amp;
	            for ( i=2; i<poly.length; i+=2 ) {
	                float x1 = x+poly[i-2], y1 = y+poly[i-1];
	                float x2 = x+poly[i],   y2 = y+poly[i+1];
	                amp = frac*(float)Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	                
	                float mx = x + x1 + (x2-x1)/2f;
	                float my = y + y1 + (y2-y1)/2f;
	                
	                float vx = mx-cx, vy = my-cy;
	                float norm = (float)Math.sqrt(vx*vx+vy*vy);
	                float px = mx + amp*vx/norm;
	                float py = my + amp*vy/norm;
	                path.quadTo(px,py,x2,y2);
	                
//	                float mx = x + poly[i-2] + (poly[i]-poly[i-2])/2f;
//	                float my = y + poly[i-1] + (poly[i+1]-poly[i-1])/2f;
//	                float px = mx + frac*(mx-cx);
//	                float py = my + frac*(my-cy);
//	                path.quadTo(px,py,x+poly[i],y+poly[i+1]);
	            }
	            
	            float x1 = x+poly[i-2], y1 = y+poly[i-1];
	            float x2 = x+poly[0],   y2 = y+poly[1];
	            amp = frac*(float)Math.sqrt(Math.pow(x2-x1,2)+Math.pow(y2-y1,2));
	            
	            float mx = x + x1 + (x2-x1)/2f;
                float my = y + y1 + (y2-y1)/2f;
                
                float vx = mx-cx, vy = my-cy;
                float norm = (float)Math.sqrt(vx*vx+vy*vy);
                float px = mx + amp*vx/norm;
                float py = my + amp*vy/norm;
                path.quadTo(px,py,x2,y2);
	            
	            // close off the loop
//	            float mx = x + poly[i-2] + (poly[0]-poly[i-2])/2f;
//	            float my = y + poly[i-1] + (poly[1]-poly[i-1])/2f;
//	            float px = mx + frac*(mx-cx);
//	            float py = my + frac*(my-cy);
//	            path.quadTo(px,py,x+poly[0],y+poly[1]);
            }
        }
        path.closePath();
        return path;
    } //

} // end of class PolygonRenderer
