package profusians.zonemanager.util.display;

import java.awt.Graphics2D;

import prefuse.Display;
import prefuse.util.display.PaintListener;
import profusians.zonemanager.ZoneManager;

/**
 * A paintlistener responsible for drawing of zone borders.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class ZoneBorderDrawing implements PaintListener {

    private ZoneManager m_zoneManager;

    /**
         * Creates a new ZoneBorderDrawing listener
         * 
         * @param zManager
         *                the zone manager which handle the zones which borders
         *                should be drawn
         */
    public ZoneBorderDrawing( ZoneManager zManager) {
	m_zoneManager = zManager;
    }

    public void prePaint( Display d, Graphics2D g) {
    }

    public void postPaint( Display d, Graphics2D g) {
	m_zoneManager.drawAllBorders(d, g);
    }
}
