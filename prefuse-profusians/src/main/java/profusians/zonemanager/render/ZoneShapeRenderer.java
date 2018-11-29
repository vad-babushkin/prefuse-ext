package profusians.zonemanager.render;

import java.awt.Shape;

import prefuse.render.ShapeRenderer;
import prefuse.visual.VisualItem;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.Zone;

/**
 * Shape renderer which is responible for associating the proper shape to the
 * respecitve zone.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek </a>
 * 
 */
public class ZoneShapeRenderer extends ShapeRenderer {

    private ZoneManager m_zManager;

    /**
         * Create a new ZoneShapeRenderer
         * 
         * @param zoneManager
         *                the zonemanager this shape renderer is associated to
         */
    public ZoneShapeRenderer( ZoneManager zoneManager) {
	super();
	m_zManager = zoneManager;
    }

    protected Shape getRawShape( VisualItem item) {

	 int zoneNumber = item.getInt("zoneNumber");

	 Zone aZone = m_zManager.getZone(zoneNumber);

	return aZone.getRawShape(item.getEndX(), item.getEndY());
    }
}
