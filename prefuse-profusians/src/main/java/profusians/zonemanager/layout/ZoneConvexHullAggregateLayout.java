package profusians.zonemanager.layout;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.util.GraphicsLib;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.Zone;

/**
 * Layout for zones that draws a convex hull around the zone items. The convex
 * hull implementaion is "taken" from the prefuse AggregateDemo class.
 * 
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */
public class ZoneConvexHullAggregateLayout extends Layout {

    private  int m_margin = 5; // convex hull pixel margin

    private double[] m_pts; // buffer for computing convex hulls

    ZoneManager m_zoneManager;

    /**
         * Creates a new ZoneConvexHullAggregateLayout
         * 
         * @param zManager
         *                the zone manager in use
         * @param aggrGroup
         *                the aggregate group to layout
         */

    public ZoneConvexHullAggregateLayout( ZoneManager zManager,
	     String aggrGroup) {
	super(aggrGroup);

	m_zoneManager = zManager;

    }

    public void run( double frac) {

	 AggregateTable aggr = (AggregateTable) m_vis.getGroup(m_group);

	// do we have any to process?
	 int num = aggr.getTupleCount();
	if (num == 0) {
	    return;
	}

	// update buffers
	int maxsz = 0;
	for ( Iterator aggrs = aggr.tuples(); aggrs.hasNext();) {
	     AggregateItem aitem = (AggregateItem) aggrs.next();
	    maxsz = Math.max(maxsz, 4 * 2 * aitem.getAggregateSize());
	}

	if ((m_pts == null) || (maxsz > m_pts.length)) {
	    m_pts = new double[maxsz];
	}

	// compute and assign convex hull for each aggregate

	 Iterator aggrs = m_vis.getGroup(m_group).tuples();

	while (aggrs.hasNext()) {

	     AggregateItem aitem = (AggregateItem) aggrs.next();

	    if (aitem.getAggregateSize() == 0) {
		continue;
	    }

	     Zone aZone = m_zoneManager
		    .getZone(aitem.getInt("zoneNumber"));

	    int idx = 0;

	    NodeItem item = null;
	     Iterator iter = aitem.items();

	    while (iter.hasNext()) {
		item = (NodeItem) iter.next();
		if (item.isVisible() && aZone.contains(item)) {
		    addPoint(m_pts, idx, item, m_margin);
		    idx += 2 * 4;
		}
	    }
	    // if no aggregates are visible, do nothing
	    if (idx == 0) {
		aitem.setVisible(false);
		continue;

	    }
	    aitem.setVisible(true);
	    // compute convex hull
	     double[] nhull = GraphicsLib.convexHull(m_pts, idx);

	    // prepare viz attribute array
	    float[] fhull = (float[]) aitem.get(VisualItem.POLYGON);
	    if ((fhull == null) || (fhull.length < nhull.length)) {
		fhull = new float[nhull.length];
	    } else if (fhull.length > nhull.length) {
		fhull[nhull.length] = Float.NaN;
	    }

	    // copy hull values
	    for (int j = 0; j < nhull.length; j++) {
		fhull[j] = (float) nhull[j];
	    }
	    aitem.set(VisualItem.POLYGON, fhull);
	    aitem.setValidated(false); // force invalidation

	}
    }

    private static void addPoint( double[] pts, int idx,
	     VisualItem item, int growth) {
	 Rectangle2D b = item.getBounds();
	 double minX = (b.getMinX()) - growth, minY = (b.getMinY())
		- growth;
	 double maxX = (b.getMaxX()) + growth, maxY = (b.getMaxY())
		+ growth;
	pts[idx] = minX;
	pts[idx + 1] = minY;
	pts[idx + 2] = minX;
	pts[idx + 3] = maxY;
	pts[idx + 4] = maxX;
	pts[idx + 5] = minY;
	pts[idx + 6] = maxX;
	pts[idx + 7] = maxY;
    }

} // end of class AggregateLayout

