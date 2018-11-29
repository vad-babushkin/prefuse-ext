package profusians.controls;

import java.awt.event.MouseEvent;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.controls.FocusControl;
import prefuse.data.tuple.TupleSet;

/**
 * Extension of the FocusControl of the prefuse library, which enables a focus
 * deselect when a click on the display background occurs
 * 
 * Based on a solution posted in the prefuse forum by Nazri and Ashwin:
 * http://sourceforge.net/forum/forum.php?thread_id=1737242&forum_id=343013
 * 
 */

public class FocusSelectDeselectControl extends FocusControl {

    /**
         * Creates a new FocusSelectDeselectControl that changes the focus to
         * another item when that item is clicked once. All focused items become
         * unfocused if one click on the display background occurs.
         */

    public FocusSelectDeselectControl() {
	this(1);
    }

    /**
         * Creates a new FocusControl that changes the focus when an item is
         * clicked the specified number of times. All focused items become
         * unfocused if the background display is clicked the specified number
         * of times. A click value of zero indicates that the focus should be
         * changed in response to mouse-over events.
         * 
         * @param clicks
         *                the number of clicks needed to switch the focus.
         */

    public FocusSelectDeselectControl( int clicks) {
	super(clicks);
    }

    public void mouseClicked( MouseEvent e) {
	if ((e.getButton() != MouseEvent.BUTTON1)
		|| (e.getClickCount() != ccount)) {
	    return;
	}
	 Visualization vis = ((Display) e.getSource()).getVisualization();

	this.curFocus = null;
	 TupleSet ts = vis.getFocusGroup(Visualization.FOCUS_ITEMS);
	ts.clear();
	if (activity != null) {
	    vis.run(activity);
	}

    }

} // end of class FocusSelectDeselectControl
