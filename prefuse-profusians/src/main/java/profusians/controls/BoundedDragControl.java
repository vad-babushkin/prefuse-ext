package profusians.controls;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import prefuse.Display;
import prefuse.action.layout.Layout;
import prefuse.controls.DragControl;
import prefuse.data.event.TableListener;
import prefuse.visual.VisualItem;

/**
 * Variation of the original prefuse DragControl, which restricts dragging to
 * the bounds of the display The used layout has to be specified so that the
 * bounds in which dragging should happen can be calculated. This control acts
 * like the original DragControl if the layout isn't set or explicitly set null.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class BoundedDragControl extends DragControl implements TableListener {

    protected Layout m_layout = null;

    /**
         * Create a new BoundedDragControl
         * 
         * @param lay
         *                the associated layout
         */

    public BoundedDragControl( Layout lay) {
	super();
	m_layout = lay;
    }

    public void setLayout( Layout lay) {
	m_layout = lay;
    }

    public void itemDragged( VisualItem item, MouseEvent e) {

	if (!SwingUtilities.isLeftMouseButton(e)) {
	    return;
	}

	dragged = true;
	 Display d = (Display) e.getComponent();
	d.getAbsoluteCoordinate(e.getPoint(), temp);

	double dx = temp.getX() - down.getX();
	double dy = temp.getY() - down.getY();
	 double x = item.getX();
	 double y = item.getY();

	if (m_layout != null) {
	     Rectangle2D bounds = m_layout.getLayoutBounds();
	    double x1 = 0, x2 = 0, y1 = 0, y2 = 0;
	    if (bounds != null) {
		x1 = bounds.getMinX();
		y1 = bounds.getMinY();
		x2 = bounds.getMaxX();
		y2 = bounds.getMaxY();
	    }

	    if (bounds != null) {
		 Rectangle2D b = item.getBounds();
		 double hw = b.getWidth() / 2;
		 double hh = b.getHeight() / 2;
		if ((x + hw > x2) || (temp.getX() + hw > x2)) {
		    dx = x2 - hw - x;
		}
		if ((x - hw < x1) || (temp.getX() - hw < x1)) {
		    dx = x1 + hw - x;
		}
		if ((y + hh > y2) || (temp.getY() + hh > y2)) {
		    dy = y2 - hh - y;
		}
		if ((y - hh < y1) || (temp.getY() - hh < y1)) {
		    dy = y1 + hh - y;
		}
	    }
	}

	item.setStartX(x);
	item.setStartY(y);
	item.setX(x + dx);
	item.setY(y + dy);
	item.setEndX(x + dx);
	item.setEndY(y + dy);

	if (repaint) {
	    item.getVisualization().repaint();
	}

	down.setLocation(temp);
	if (action != null) {
	    d.getVisualization().run(action);
	}
    }

} // end of class DragControl
