package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;

import java.util.Comparator;

public class DOIItemComparator
		implements Comparator {
	public int compare(Object paramObject1, Object paramObject2) {
		if ((!(paramObject1 instanceof VisualItem)) || (!(paramObject2 instanceof VisualItem))) {
			throw new IllegalArgumentException();
		}
		VisualItem localVisualItem1 = (VisualItem) paramObject1;
		VisualItem localVisualItem2 = (VisualItem) paramObject2;
		double d3;
		if ((localVisualItem1 instanceof NodeItem)) {
			if ((localVisualItem2 instanceof NodeItem)) {
				double d1 = ((NodeItem) localVisualItem1).getDOI();
				d3 = ((NodeItem) localVisualItem2).getDOI();
				return d1 == d3 ? 0 : d1 > d3 ? 1 : -1;
			}
			return 1;
		}
		if ((localVisualItem2 instanceof NodeItem)) {
			return -1;
		}
		if ((localVisualItem1 instanceof EdgeItem)) {
			if ((localVisualItem2 instanceof EdgeItem)) {
				EdgeItem localEdgeItem1 = (EdgeItem) localVisualItem1;
				EdgeItem localEdgeItem2 = (EdgeItem) localVisualItem2;
				d3 = ((NodeItem) localEdgeItem1.getFirstNode()).getDOI();
				double d4 = ((NodeItem) localEdgeItem2.getFirstNode()).getDOI();
				double d5 = ((NodeItem) localEdgeItem1.getSecondNode()).getDOI();
				double d6 = ((NodeItem) localEdgeItem2.getSecondNode()).getDOI();
				double d7 = Math.max(d3, d5);
				double d8 = Math.max(d4, d6);
				return d7 == d8 ? 0 : d7 > d8 ? 1 : -1;
			}
			return 1;
		}
		if ((localVisualItem2 instanceof EdgeItem)) {
			return -1;
		}
		if ((localVisualItem1 instanceof AggregateItem)) {
			if ((localVisualItem2 instanceof AggregateItem)) {
				double d2 = ((AggregateItem) localVisualItem1).getNodeItem().getDOI();
				d3 = ((AggregateItem) localVisualItem2).getNodeItem().getDOI();
				return d2 == d3 ? 0 : d2 > d3 ? 1 : -1;
			}
			return 1;
		}
		if ((localVisualItem2 instanceof AggregateItem)) {
			return -1;
		}
		return 0;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/DOIItemComparator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */