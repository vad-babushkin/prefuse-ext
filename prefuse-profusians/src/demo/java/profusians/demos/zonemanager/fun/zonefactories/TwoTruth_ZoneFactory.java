package profusians.demos.zonemanager.fun.zonefactories;

import profusians.demos.zonemanager.fun.zones.TwoTruthZone;
import profusians.zonemanager.zone.DefaultZoneFactory;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class TwoTruth_ZoneFactory extends DefaultZoneFactory {

    public TwoTruth_ZoneFactory() {

    }

    public Zone getZone(ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {

	if (zAttributes.getZoneName().compareToIgnoreCase("yellow zone") == 0) {
	    return new TwoTruthZone(m_vis, m_fsim, zShape, zColors,
		    zAttributes, 100, 100);
	} else {
	    return new TwoTruthZone(m_vis, m_fsim, zShape, zColors,
		    zAttributes, -100, -100);
	}
    }

}
