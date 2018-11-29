package profusians.zonemanager.layout;

import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.Zone;

/**
 * This class implements a basic zone layout, responsible for adjusting the size and
 * position of the zone aggregates used for the drawing of the zones according to
 * the data of the associated zones
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek </a>
 * 
 */

public class ZoneAggregateLayout extends Layout {

    private ZoneManager m_zoneManager;

    boolean m_drawIfEmpty;

    boolean m_animate;

    /**
         * Creates a new zone aggregate layout
         * 
         * @param zManager
         */

    public ZoneAggregateLayout( ZoneManager zManager) {
	this(zManager, false, true);
    }

    public ZoneAggregateLayout( ZoneManager zManager, boolean animate) {
	this(zManager, animate, true);
    }

    public ZoneAggregateLayout( ZoneManager zManager,
	     boolean animate, boolean drawIfEmpty) {
	super(ZoneManager.ZONEAGGREGATES);
	m_zoneManager = zManager;

	m_animate = animate;
	m_drawIfEmpty = drawIfEmpty;
    }

    public void run( double frac) {

	 AggregateTable aggr = m_zoneManager.getZoneAggregateTable();

	for ( Iterator aggrs = aggr.tuples(); aggrs.hasNext();) {
	     AggregateItem aAitem = (AggregateItem) aggrs.next();

	     int zoneNumber = aAitem.getInt("zoneNumber");

	     Zone itsZone = m_zoneManager.getZone(zoneNumber);

	    if (!m_drawIfEmpty) {
		if (itsZone.getNumberOfItems() == 0) {
		    aAitem.setVisible(false);
		    continue;
		} else {
		    aAitem.setVisible(true);
		}
	    }

	    if (itsZone.isFlexible() && m_animate) {
		itsZone.animateZoneAggregate(aAitem, pace(frac));
	    } else {
		itsZone.positionZoneAggregate(aAitem);
	    }

	}

    }

    /**
         * Pace function for the animation of changes of flexible zones.
         * Subclasses might override this method in order to implement
         * customized pace functions. By default, it returns the root of the
         * given fraction.
         * 
         * @param frac
         *                the fraction of this Action's duration that has
         *                elapsed.
         * @return the root of the given fraction
         */

    protected double pace( double frac) {
	return Math.sqrt(frac);
    }

} // end of class ZoneAggregateLayout
