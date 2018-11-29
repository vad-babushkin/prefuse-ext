package profusians.demos.zonemanager.tutorial.zonefactories;

import profusians.demos.zonemanager.tutorial.zones.ZoneDemo10_CircularZoneFlexibleItems;
import profusians.demos.zonemanager.tutorial.zones.ZoneDemo10_RectangularZoneFlexibleItems;
import profusians.zonemanager.zone.DefaultZoneFactory;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class ZoneDemo10_ZoneFactory extends DefaultZoneFactory {

    public ZoneDemo10_ZoneFactory() {
    }

    public Zone getZone(ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {

	if (zAttributes.hasFlexibleItems() == true) {
	    if (zShape.getZoneType().compareTo("CIRCULAR") == 0) {
		return new ZoneDemo10_CircularZoneFlexibleItems(m_vis, m_fsim,
			zShape, zColors, zAttributes);
	    } else {
		return new ZoneDemo10_RectangularZoneFlexibleItems(m_vis,
			m_fsim, zShape, zColors, zAttributes);
	    }
	} else {
	    return super.getZone(zShape, zColors, zAttributes);
	}

    }

}
