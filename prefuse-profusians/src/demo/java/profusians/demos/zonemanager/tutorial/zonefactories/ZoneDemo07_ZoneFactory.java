package profusians.demos.zonemanager.tutorial.zonefactories;

import profusians.demos.zonemanager.tutorial.zones.ZoneDemo07_SpecialFlexiblePurpleZone;
import profusians.zonemanager.zone.DefaultZoneFactory;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class ZoneDemo07_ZoneFactory extends DefaultZoneFactory {

    public ZoneDemo07_ZoneFactory() {

    }

    public Zone getZone(ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {

	if (zAttributes.getZoneName().compareTo("purple zone") == 0) {

	    return new ZoneDemo07_SpecialFlexiblePurpleZone(m_vis, m_fsim,
		    zShape, zColors, zAttributes);

	} else {
	    return super.getZone(zShape, zColors, zAttributes);
	}

    }

}
