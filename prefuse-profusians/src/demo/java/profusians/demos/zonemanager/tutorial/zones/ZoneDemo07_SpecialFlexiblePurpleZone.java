package profusians.demos.zonemanager.tutorial.zones;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.zone.RectangularZone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

public class ZoneDemo07_SpecialFlexiblePurpleZone extends RectangularZone {

    public ZoneDemo07_SpecialFlexiblePurpleZone(Visualization vis,
	    ForceSimulator fsim, ZoneShape zShape, ZoneColors zColors,
	    ZoneAttributes zAttributes) {

	super(vis, fsim, zShape, zColors, zAttributes);
    }

    public float getUpdateCenterX() {
	return getInitialCenterX() + getInitialWidth() * getNumberOfItems() / 2;
    }

    public int getUpdateWidth() {
	return getInitialWidth() * getNumberOfItems();
    }

    public int getUpdateHeight() {
	return getInitialHeight();
    }

}
