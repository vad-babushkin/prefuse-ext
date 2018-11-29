package profusians.controls;

import java.awt.event.MouseEvent;

import prefuse.controls.DragControl;
import prefuse.data.expression.Predicate;
import prefuse.visual.VisualItem;

public class PredicateDragControl extends DragControl {

    Predicate m_predicate;

    /**
         * Creates a new drag control that issues repaint requests as an item is
         * dragged.
         * 
         * @param pred
         *                the predicate which determines if a item should be
         *                dragged
         */
    public PredicateDragControl( Predicate pred) {
	m_predicate = pred;
    }

    /**
         * Creates a new drag control that optionally issues repaint requests as
         * an item is dragged.
         * 
         * @param repaint
         *                indicates whether or not repaint requests are issued
         *                as drag events occur. This can be set to false if
         *                other activities (for example, a continuously running
         *                force simulation) are already issuing repaint events.
         * 
         * @param pred
         *                the predicate which determines if a item should be
         *                dragged
         */
    public PredicateDragControl( boolean repaint, Predicate pred) {
	m_predicate = pred;
	this.repaint = repaint;
    }

    /**
         * Creates a new drag control that optionally issues repaint requests as
         * an item is dragged.
         * 
         * @param repaint
         *                indicates whether or not repaint requests are issued
         *                as drag events occur. This can be set to false if
         *                other activities (for example, a continuously running
         *                force simulation) are already issuing repaint events.
         * @param fixOnMouseOver
         *                indicates if object positions should become fixed
         *                (made stationary) when the mouse pointer is over an
         *                item.
         * @param pred
         *                the predicate which determines if a item should be
         *                dragged
         */
    public PredicateDragControl( boolean repaint,
	     boolean fixOnMouseOver, Predicate pred) {

	m_predicate = pred;
	this.repaint = repaint;
	super.setFixPositionOnMouseOver(fixOnMouseOver);

    }

    /**
         * Creates a new drag control that invokes an action upon drag events.
         * 
         * @param action
         *                the action to run when drag events occur.
         * @param pred
         *                the predicate which determines if a item should be
         *                dragged
         */
    public PredicateDragControl( String action, Predicate pred) {
	m_predicate = pred;
	this.repaint = false;
	this.action = action;
    }

    public void itemDragged( VisualItem item, MouseEvent e) {
	if (m_predicate.get(item) == Boolean.TRUE) {
	    super.itemDragged(item, e);
	}
    }
}
