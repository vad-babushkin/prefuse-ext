package profusians.demos.zonemanager.fun.zonefactories;

import profusians.demos.zonemanager.fun.zones.TrafficLights_Zone;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.DefaultZoneFactory;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class TrafficLights_ZoneFactory extends DefaultZoneFactory {

    ZoneManager m_zom;

    public TrafficLights_ZoneFactory(ZoneManager zom) {
	m_zom = zom;
    }

    public Zone getZone(ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {

	if (zAttributes.getZoneName().compareToIgnoreCase("red zone") == 0) {
	    return new TrafficLights_Zone(m_zom, m_vis, m_fsim, zShape,
		    zColors, zAttributes,false);
	} else if (zAttributes.getZoneName().compareToIgnoreCase("green zone") == 0) {
	    return new TrafficLights_Zone(m_zom, m_vis, m_fsim, zShape,
		    zColors, zAttributes,true);
	} else {
	    return super.getZone(zShape, zColors, zAttributes);
	}
    }

}
