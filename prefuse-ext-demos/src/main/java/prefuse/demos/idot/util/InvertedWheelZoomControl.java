package prefuse.demos.idot.util;

import java.awt.Point;
import java.awt.event.MouseWheelEvent;

import prefuse.Display;
import prefuse.controls.AbstractZoomControl;
import prefuse.visual.VisualItem;

/**
 * Zooms the display using the mouse scroll wheel, changing the scale of the
 * viewable region.
 *
 * Based on prefuse.controls.WheelZoomControl
 */
public class InvertedWheelZoomControl extends AbstractZoomControl {
	/**
	 * Amount to zoom for each wheel "click". Negative values make
	 * the up/away from user rotation lead to zoom in, and down/towards the user
	 * rotation to lead to zoom out. 
	 */
    public static final float scale = -0.1f;
	
    /** a temporary point used in calculations */
    private Point m_point = new Point();
    
    /**
     * @see prefuse.controls.Control#itemWheelMoved(prefuse.visual.VisualItem, java.awt.event.MouseWheelEvent)
     */
    public void itemWheelMoved(VisualItem item, MouseWheelEvent e) {
        if ( m_zoomOverItem )
            mouseWheelMoved(e);
    }
    
    /**
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        Display display = (Display)e.getComponent();
        m_point.x = e.getX(); // display.getWidth()/2;
        m_point.y = e.getY(); // display.getHeight()/2;
        zoom(display, m_point,
             1 + scale * e.getWheelRotation(), false);
    }
    
} // end of class InvertedWheelZoomControl
