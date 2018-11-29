package profusians.controls;

import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.DragControl;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

/**
 * Extension of the original DragControl which drags all focused items around
 * 
 * @author <a href="http://goosebumps4all.net"> martin dudek </a>
 * 
 */
public class MultipleDragControl extends DragControl {

    public void itemPressed( VisualItem item, MouseEvent e) {
	super.itemPressed(item, e);
	addToFocusGroup(item, e);
    }

    public void itemClicked( VisualItem item, MouseEvent e) {
	super.itemPressed(item, e);
	addToFocusGroup(item, e);
    }

    public void itemDragged( VisualItem item, MouseEvent e) {
	if (!SwingUtilities.isLeftMouseButton(e)) {
	    return;
	}

	 Visualization vis = item.getVisualization();

	 TupleSet focusSet = vis.getGroup("_focus_");

	dragged = true;
	 Display d = (Display) e.getComponent();

	d.getAbsoluteCoordinate(e.getPoint(), temp);
	 double dx = temp.getX() - down.getX();
	 double dy = temp.getY() - down.getY();

	if (e.isControlDown()) {
	     Iterator iter = focusSet.tuples();
	    while (iter.hasNext()) {
		moveIt((VisualItem) iter.next(), dx, dy);
	    }
	} else {
	    moveIt(item, dx, dy);
	}
	down.setLocation(temp);
	if (repaint) {
	    vis.repaint();
	}

	if (action != null) {
	    d.getVisualization().run(action);
	}
    }

    private void addToFocusGroup( VisualItem vi, MouseEvent e) {
	 TupleSet ts = vi.getVisualization().getGroup("_focus_");
	if (!e.isControlDown()) {
	    ts.clear();
	}
	ts.addTuple(vi);
    }

    private void moveIt( VisualItem vi, double dx, double dy) {

	 double x = vi.getX();
	 double y = vi.getY();

	vi.setStartX(x);
	vi.setStartY(y);
	vi.setX(x + dx);
	vi.setY(y + dy);
	vi.setEndX(x + dx);
	vi.setEndY(y + dy);
    }

} // end of class MultipleDragControl

