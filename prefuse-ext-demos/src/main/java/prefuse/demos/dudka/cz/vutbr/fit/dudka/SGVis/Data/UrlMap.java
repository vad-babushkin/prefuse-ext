package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

import java.net.URL;
import java.util.HashMap;

/**
 * Associative array for URL objects.
 * This is not simple key-value map. There can be multiple
 * values for single key object.
 */
public class UrlMap<KEY> {
	private HashMap<KEY, UrlSet> map;
	
	public UrlMap() {
		map = new HashMap<KEY, UrlSet>();
	}
	
	/**
	 * Deep copy constructor.
	 * @param ref Other UrlMap object to copy data from.
	 */
	public UrlMap(UrlMap<KEY> ref) {
		map = new HashMap<KEY, UrlSet>(ref.map);
	}
	
	/**
	 * Deep clone method.
	 */
	public UrlMap<KEY> clone() {
		return new UrlMap<KEY>(this);
	}

	/**
	 * Associative access to managed items.
	 * @param key Key object to look for.
	 * @return Returns all items associated with key as UrlSet.
	 */
	public UrlSet get(Object key) {
		UrlSet uset = map.get(key);
		if (null==uset)
			uset = new UrlSet();
		return uset;
	}
	
	/**
	 * Add item to set associated with given key.
	 * @param key Key to associate with.
	 * @param url URL object to add to set.
	 */
	public void addToSet(KEY key, URL url) {
		UrlSet rl = this.get(key);
		rl.add(url);
		map.put(key, rl);
	}
	
	/**
	 * @return Returns set of keys.
	 */
	public Iterable<KEY> getKeys() {
		return map.keySet();
	}
}
