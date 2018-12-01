package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

import java.util.HashMap;

/**
 * Associative array for Relation objects.
 * This is not simple key-value map. There can be multiple
 * values for single key object.
 */
public class RelationMap<KEY> {
	private HashMap<KEY, RelationSet> map;
	
	public RelationMap() {
		map = new HashMap<KEY, RelationSet>();
	}
	
	/**
	 * Deep copy constructor.
	 */
	public RelationMap(RelationMap<KEY> ref) {
		map = new HashMap<KEY, RelationSet>(ref.map);
	}
	
	/**
	 * Associative access to managed items.
	 * @param key Key object to look for.
	 * @return Returns all items associated with key as RelationSet.
	 */
	public RelationSet get(Object key) {
		RelationSet rset = map.get(key);
		if (null==rset)
			rset = new RelationSet();
		return rset;
	}
	
	/**
	 * Add item to set associated with given key.
	 * @param key Key to associate with.
	 * @param relation Relation object to add to set.
	 */
	public void addToSet(KEY key, Relation relation)
	{
		RelationSet rl = this.get(key);
		rl.add(relation);
		map.put(key, rl);
	}
	
	/**
	 * Deep clone method.
	 */
	public RelationMap<KEY> clone() {
		return new RelationMap<KEY>(this);
	}
}
