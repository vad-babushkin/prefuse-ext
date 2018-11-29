package profusians.zonemanager.zone.aggregate;

import prefuse.visual.AggregateItem;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.shape.ZoneShape;


public class DefaultZoneAggregateItemFieldValueAssignment implements ZoneAggregateItemFieldValueAssignment{
    
    public DefaultZoneAggregateItemFieldValueAssignment() {

    }
    
    public final void fillFields(AggregateItem ai,Zone aZone) {
	fillDefaultFields(ai, aZone);
	fillAdditionalFields(ai, aZone);
    }
    private void fillDefaultFields(AggregateItem ai,Zone aZone) {
	
	ZoneShape zShape = aZone.getShape();
	ZoneAttributes zAttributes = aZone.getAttributes();
	
	ai.setString("zoneName", zAttributes.getZoneName());
	ai.setString("zoneInfo", zAttributes.getInfo());
	ai.setInt("zoneNumber", zAttributes.getZoneNumber());
	ai.setString("zoneType", zShape.getZoneType());
	ai.set("zoneClass", zShape.getZoneClass());
    }

    protected void fillAdditionalFields(AggregateItem ai,Zone aZone) {
	
    }
}
