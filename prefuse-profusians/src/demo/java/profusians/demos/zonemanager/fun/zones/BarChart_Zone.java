package profusians.demos.zonemanager.fun.zones;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.zone.RectangularZone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class BarChart_Zone extends RectangularZone {

    public BarChart_Zone(Visualization vis, ForceSimulator fsim,
	    ZoneShape zShape, ZoneColors zColors, ZoneAttributes zAttributes) {

	super(vis, fsim, zShape, zColors, zAttributes);
    }

    public float getUpdateCenterY() {
	return getInitialCenterY() - getInitialHeight() * getNumberOfItems()
		/ 2;
    }

    public int getUpdateWidth() {
	return getInitialWidth();
    }

    public int getUpdateHeight() {
	return getInitialHeight() * getNumberOfItems();
    }

}
