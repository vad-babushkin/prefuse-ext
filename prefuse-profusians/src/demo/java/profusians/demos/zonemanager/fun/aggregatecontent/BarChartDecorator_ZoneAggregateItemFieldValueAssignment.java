package profusians.demos.zonemanager.fun.aggregatecontent;

import prefuse.visual.AggregateItem;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.aggregate.DefaultZoneAggregateItemFieldValueAssignment;

public class BarChartDecorator_ZoneAggregateItemFieldValueAssignment  extends DefaultZoneAggregateItemFieldValueAssignment {

    protected void fillAdditionalFields(AggregateItem ai,Zone aZone) {
	ai.setDouble("rotation", -30);
    }
}
