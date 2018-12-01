package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Simple container for Relation objects with linear access to it.
 */
public class RelationList	implements
	Cloneable,
	IContainer<Relation>
{
	private ArrayList<Relation> list;
	
	public RelationList() {
		 list =	new ArrayList<Relation>();
	}
	
	/**
	 * Deep copy constructor.
	 * @param ref
	 */
	public RelationList(RelationList ref) {
		list = new ArrayList<Relation>(ref.list);
	}
	
	public Iterator<Relation> iterator() {
		return list.iterator();
	}
	
	public void add(Relation relation) {
		list.add(relation);
	}

	/**
	 * Add other iterable container to itself.
	 * @param container Container to add.
	 */
	public void add(IContainer<Relation> container) {
		for (Relation rel: container)
			this.add(rel);
	}
	
	/**
	 * Deep clone method.
	 */
	public RelationList clone() throws CloneNotSupportedException {
		return new RelationList(this);
	}
}
