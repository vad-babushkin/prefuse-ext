package profusians.demos.zonemanager.fun.zones;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.CircularZone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class TrafficLights_Zone extends CircularZone {

    ZoneManager m_zoneManager;

    CircularZone neighborZone;
    
    int m_direction;

    public TrafficLights_Zone(ZoneManager zManager, Visualization vis,
	    ForceSimulator fsim, ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes,boolean upperLight) {

	super(vis, fsim, zShape, zColors, zAttributes);

	m_zoneManager = zManager;
	m_direction = upperLight ? -1 : 1;

    }

    public float getUpdateCenterY() {

	if (neighborZone == null) {
	    neighborZone = (CircularZone) m_zoneManager.getZone("yellow zone");
	}

	return neighborZone.getUpdateCenterY() + m_direction * neighborZone.getUpdateRadius()
		+ m_direction * (getUpdateRadius() + 1) ;
    }
}
