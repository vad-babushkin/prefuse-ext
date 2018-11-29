package profusians.demos.zonemanager.tutorial.zones;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.CircularZone;
import profusians.zonemanager.zone.RectangularZone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class ZoneDemo08_SpecialFlexibleBlueZone extends CircularZone {

    ZoneManager m_zManager;

    RectangularZone neighborZone;

    public ZoneDemo08_SpecialFlexibleBlueZone(ZoneManager zm,
	    Visualization vis, ForceSimulator fsim, ZoneShape zShape,
	    ZoneColors zColors, ZoneAttributes zAttributes) {

	super(vis, fsim, zShape, zColors, zAttributes);
	m_zManager = zm;
    }

    public float getUpdateCenterX() {

	if (neighborZone == null) {
	    neighborZone = (RectangularZone) m_zManager.getZone("purple zone");
	}

	return neighborZone.getUpdateCenterX() + neighborZone.getUpdateWidth()
		/ 2 + getUpdateRadius() + 1;
    }
}
