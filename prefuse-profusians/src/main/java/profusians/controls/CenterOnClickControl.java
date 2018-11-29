package profusians.controls;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * Moves the clicked node item to the center of the display. The duration of
 * this animation can be specified, the default value is 1000 miliseconds,
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class CenterOnClickControl extends ControlAdapter {

    private int m_duration = 1000;

    /**
         * Creates a new CenterOnClickControl
         * 
         */

    public CenterOnClickControl() {
    }

    /**
         * Creates a new CenterOnClickControl
         * 
         * @param duration
         *                the duration of the animation
         */

    public CenterOnClickControl( int duration) {
	// TODO Auto-generated constructor stub
	m_duration = duration;
    }

    public void itemPressed( VisualItem item, MouseEvent e) {

	if (!SwingUtilities.isLeftMouseButton(e)) {
	    return;
	}

	if (item instanceof NodeItem) {
	    centerNode((Display) e.getComponent(), item);
	}

    }

    private void centerNode( Display dis, VisualItem item) {

	 double scale = dis.getScale();
	 double displayX = dis.getDisplayX();
	 double displayY = dis.getDisplayY();
	 double nodeX = item.getX() * scale;
	 double nodeY = item.getY() * scale;
	 double screenWidth = dis.getWidth();
	 double screenHeight = dis.getHeight();
	 double moveX = (nodeX * -1) + ((screenWidth / 2) + displayX);
	 double moveY = (nodeY * -1) + ((screenHeight / 2) + displayY);

	dis.animatePan(moveX, moveY, m_duration);
    }
}
