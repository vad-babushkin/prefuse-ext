package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Set of Relation object.
 * Each Realtion object can be only once in container. 
 */
public class RelationSet
	implements
		Cloneable,
		IContainer<Relation>
{
	private HashSet<Relation> set;
	public RelationSet() {
		set = new HashSet<Relation>();
	}
	
	/**
	 * Deep copy constructor.
	 * @param ref Other container object to copy from.
	 */
	public RelationSet(RelationSet ref) {
		set = new HashSet<Relation>(ref.set);
	}
	
	/**
	 * Deep clone method.
	 */
	public RelationSet clone() {
		return new RelationSet(this);
	}
	public void add(Relation relation) {
		set.add(relation);
	}
	public Iterator<Relation> iterator() {
		return set.iterator();
	}
	
	/**
	 * @return Returns count if item managed by container.
	 */
	public int size() {
		return set.size();
	}
}
