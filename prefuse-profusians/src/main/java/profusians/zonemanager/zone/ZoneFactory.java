package profusians.zonemanager.zone;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * The ZoneFactory is responsible for providing the proper Zone instance for the
 * given input parameter.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */
public interface ZoneFactory {

    public Zone getZone(ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes);

    public void setForceSimulator(ForceSimulator fsim);

    public void setVisualization(Visualization vis);

} // end of interface ZoneFactory
