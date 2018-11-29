package profusians.demos.zonemanager.tutorial.zonefactories;

import profusians.demos.zonemanager.tutorial.zones.ZoneDemo07_SpecialFlexiblePurpleZone;
import profusians.demos.zonemanager.tutorial.zones.ZoneDemo08_SpecialFlexibleBlueZone;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.DefaultZoneFactory;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class ZoneDemo08_ZoneFactory extends DefaultZoneFactory {

    ZoneManager m_zManager;

    public ZoneDemo08_ZoneFactory(ZoneManager zManager) {
	m_zManager = zManager;
    }

    public Zone getZone(ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {

	if (zAttributes.getZoneName().compareTo("purple zone") == 0) {

	    return new ZoneDemo07_SpecialFlexiblePurpleZone(m_vis, m_fsim,
		    zShape, zColors, zAttributes);

	} else if (zAttributes.getInfo().compareTo("very special zone") == 0) {
	    return new ZoneDemo08_SpecialFlexibleBlueZone(m_zManager, m_vis,
		    m_fsim, zShape, zColors, zAttributes);
	} else {
	    return super.getZone(zShape, zColors, zAttributes);
	}

    }

}
