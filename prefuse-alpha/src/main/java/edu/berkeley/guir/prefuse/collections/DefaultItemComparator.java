package edu.berkeley.guir.prefuse.collections;

import java.util.Comparator;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.NodeItem;

/**
 * Comparator that sorts items based on type and focus status.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DefaultItemComparator implements Comparator {

    protected int score(VisualItem item) {
        int score = 0;
        if ( item instanceof AggregateItem ) {
            score += (1<<5);
        } else if ( item instanceof NodeItem ) {
            score += (1<<4);
        } else if ( item instanceof EdgeItem ) {
            score += (1<<3);
        }
        if ( item.isFocus() ) {
            score += (1<<2);
        }
        if ( item.isHighlighted() ) {
            score += (1<<1);
        }
        
        return score;
    } //
    
	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if ( !(o1 instanceof VisualItem && o2 instanceof VisualItem) ) {
			throw new IllegalArgumentException();
		}
		
		VisualItem item1 = (VisualItem)o1;
		VisualItem item2 = (VisualItem)o2;
        int score1 = score(item1);
        int score2 = score(item2);
		
		if ( item1 instanceof AggregateItem && item2 instanceof AggregateItem ) {
            int s1 = ((AggregateItem)item1).getAggregateSize();
            int s2 = ((AggregateItem)item2).getAggregateSize();
            if ( s1 < s2 )
                score1 += 1;
            else if ( s2 < s1 )
                score2 += 1;
        }
        return (score1<score2 ? -1 : (score1==score2 ? 0 : 1));
	} //

} // end of class DefaultItemComparator
