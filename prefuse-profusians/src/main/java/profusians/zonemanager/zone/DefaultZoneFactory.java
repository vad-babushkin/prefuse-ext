package profusians.zonemanager.zone;

import java.lang.reflect.Constructor;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class DefaultZoneFactory implements ZoneFactory {

    protected Visualization m_vis;

    protected ForceSimulator m_fsim;

    public DefaultZoneFactory() {

    }

    public Zone getZone( ZoneShape zoneShape, ZoneColors zoneColors,
	     ZoneAttributes zoneAttributes) {

	 Class ZoneClass = zoneShape.getZoneClass();

	try {
	     Constructor zoneClassConstructor = ZoneClass
		    .getConstructor(new Class[] { Visualization.class,
			    ForceSimulator.class, ZoneShape.class,
			    ZoneColors.class, ZoneAttributes.class });

	    return (Zone) zoneClassConstructor.newInstance(new Object[] {
		    m_vis, m_fsim, zoneShape, zoneColors, zoneAttributes });

	} catch ( Exception e) {
	    System.out.println("Problems while creating new zone "
		    + e.getMessage());
	    return null;
	}

    }

    public void setForceSimulator( ForceSimulator fsim) {
	m_fsim = fsim;
    }

    public void setVisualization( Visualization vis) {
	m_vis = vis;
    }

}
