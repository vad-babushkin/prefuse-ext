package profusians.demos.zonemanager.fun.zonefactories;

import profusians.demos.zonemanager.fun.zones.BarChart_Zone;
import profusians.zonemanager.zone.DefaultZoneFactory;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class BarChartDecorator_ZoneFactory extends DefaultZoneFactory {

    public Zone getZone(ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {

	return new BarChart_Zone(m_vis, m_fsim, zShape, zColors, zAttributes);

    }
}
