package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Set of URL object.
 * Each URL object can be only once in container. 
 */
public class UrlSet
	implements
		Cloneable,
		IContainer<URL>
{
	private HashSet<URL> set;
	
	public UrlSet() {
		set = new HashSet<URL>();
	}
	
	/**
	 * Deep copy constructor.
	 * @param ref Other container object to copy from.
	 */
	public UrlSet(UrlSet ref) {
		set = new HashSet<URL>(ref.set);
	}
	
	/**
	 * Deep clone method.
	 */
	public UrlSet clone() {
		return new UrlSet(this);
	}

	public void add(URL url) {
		set.add(url);
	}

	public Iterator<URL> iterator() {
		return set.iterator();
	}
	
	/**
	 * @return Returns count if item managed by container.
	 */
	public int size() {
		return set.size();
	}
}
