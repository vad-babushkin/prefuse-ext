package profusians.demos.zonemanager.tutorial.zones;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.zone.RectangularZone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class ZoneDemo10_RectangularZoneFlexibleItems extends RectangularZone {

    public ZoneDemo10_RectangularZoneFlexibleItems(Visualization vis,
	    ForceSimulator fsim, ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {
	super(vis, fsim, zShape, zColors, zAttributes);
    }

    public double getUpdateItemSize() {
	return Math.min(3, 10. / getNumberOfItems());
    }
}
