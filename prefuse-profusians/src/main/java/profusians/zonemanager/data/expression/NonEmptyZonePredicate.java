package profusians.zonemanager.data.expression;

import prefuse.data.Tuple;
import prefuse.data.expression.AbstractPredicate;
import prefuse.visual.AggregateItem;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.Zone;

public class NonEmptyZonePredicate extends AbstractPredicate {
    ZoneManager m_zoneManager;

    public NonEmptyZonePredicate( ZoneManager zManager) {
	m_zoneManager = zManager;
    }

    public boolean getBoolean( Tuple t) {

	 int zoneNumber = ((AggregateItem) t).getInt("zoneNumber");
	 Zone aZone = m_zoneManager.getZone(zoneNumber);
	if (aZone.getNumberOfItems() > 0) {
	    return true;
	} else {
	    return false;

	}
    }
}
