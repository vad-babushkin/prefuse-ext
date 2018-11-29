package profusians.util.display;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.layout.Layout;
import prefuse.util.display.PaintListener;
import prefuse.visual.NodeItem;

/**
 * Abstract PaintListener class which counts the number of visible items on the
 * display. Extensions of this class can have access to the current number of
 * items on the the display by overriding the abstract countUpdate() method.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

abstract public class VisibleItemsOnDisplayCounter implements PaintListener {

    private String m_nodeGroupName;

    private Layout m_layout;

    boolean m_onlyCompleteOnDisplay;

    int m_idleRoundsBetweenCounts = 1;

    int m_count = 0;

    int m_round = 0;

    public VisibleItemsOnDisplayCounter() {

    }

    public VisibleItemsOnDisplayCounter( Layout lay,
	     String nodeGroupName) {
	this(lay, nodeGroupName, false);
    }

    public VisibleItemsOnDisplayCounter( Layout lay,
	     String nodeGroupName, boolean onlyCompleteOnDisplay) {
	this(lay, nodeGroupName, onlyCompleteOnDisplay, 1);
    }

    public VisibleItemsOnDisplayCounter( Layout lay,
	     String nodeGroupName, boolean onlyCompleteOnDisplay,
	     int freq) {
	m_layout = lay;
	m_nodeGroupName = nodeGroupName;
	m_onlyCompleteOnDisplay = onlyCompleteOnDisplay;
	m_idleRoundsBetweenCounts = freq;
    }

    public void init( Layout lay, String nodeGroupName) {
	m_layout = lay;
	m_nodeGroupName = nodeGroupName;
    }

    public void setLayout( Layout lay) {
	m_layout = lay;
    }

    public Layout getLayout() {
	return m_layout;
    }

    public void setNodeGroupName( String nodeGroupName) {
	m_nodeGroupName = nodeGroupName;
    }

    public String getNodeGroupName() {
	return m_nodeGroupName;
    }

    public void setOnlyCompleteOnDisplay( boolean onlyCompleteOnDisplay) {
	m_onlyCompleteOnDisplay = onlyCompleteOnDisplay;
    }

    public boolean getOnlyCompleteOnDisplay() {
	return m_onlyCompleteOnDisplay;
    }

    /**
         * Sets the number of rounds the counter stays idle before updating the
         * count information.
         * 
         * A mind blowing example of method naming skills displayed within the
         * profusinas library.
         * 
         * @param rounds
         *                the number of idle rounds
         */

    public void setIdleRoundsBetweenCounts( int rounds) {
	m_idleRoundsBetweenCounts = rounds;
    }

    /**
         * Gets the number of rounds the counter stays idle before updating the
         * count informations.
         * 
         * @return the number of idle rounds between counts
         */
    public int setIdleRoundsBetweenCounts() {
	return m_idleRoundsBetweenCounts;
    }

    public void prePaint( Display d, Graphics2D g) {

    }

    public void postPaint( Display d, Graphics2D g) {

	if (m_round % m_idleRoundsBetweenCounts == 0) {
	     Visualization vis = d.getVisualization();
	     Iterator items = vis.visibleItems(m_nodeGroupName);

	     Rectangle2D bounds = m_layout.getLayoutBounds();

	    m_count = 0;
	    while (items.hasNext()) {
		 NodeItem aItem = (NodeItem) items.next();
		if (m_onlyCompleteOnDisplay) {
		    if (bounds.contains(aItem.getBounds())) {
			m_count++;
		    }
		} else {
		    if (!bounds.createIntersection(aItem.getBounds()).isEmpty()) {
			m_count++;
		    }
		}
	    }
	}
	countUpdate(d, g, m_count, (m_round++)
		% (m_idleRoundsBetweenCounts + 1) == 0);
    }

    /**
         * Override this method to get access to the current items on display
         * informations.
         * 
         * @param d
         *                the display
         * @param g
         *                the Graphics context for the Display
         * @param count
         *                the result of the last count
         * @param realUpdate
         *                if true, the count is the actual value, otherwise the
         *                value from the last count (if the roundsPerCount
         *                factor is higher than 1, see the method
         *                setRoundsPerCount())
         */
    public abstract void countUpdate(Display d, Graphics2D g, int count,
	    boolean realUpdate);

}
