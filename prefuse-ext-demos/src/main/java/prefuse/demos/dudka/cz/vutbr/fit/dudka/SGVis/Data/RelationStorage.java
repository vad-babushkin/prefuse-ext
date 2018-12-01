package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

/**
 * @package cz.vutbr.fit.dudka.SGVis.Data
 * Class holding application data. For complex data storage
 * use class RelationStorage. To implement new container
 * consider using of interface IContainer<T>.
 * @author Kamil Dudka <xdudka00@stud.fit.vutbr.cz>
 */

import java.net.URL;
import java.util.Iterator;

/**
 * Complex storage for Relation objects.
 * Simple (effective) lookups can be done on this storage.
 * Implemeted as set of containers with associative access.
 */
public class RelationStorage implements
	Cloneable,
	IContainer<Relation>
{
	private RelationSet all;
	private UrlSet urlSet;
	private UrlMap<String> urlToHostMap;
	private RelationMap<URL> fromMap;
	private RelationMap<URL> toMap;
	private RelationMap<String> edgeTypeMap;
	
	/**
	 * Create storage from list of Relation objects.
	 * @param relationList List of objects to create storage from.
	 * @return Returns filled storage object.
	 */
	public static RelationStorage fromRelationList(RelationList relationList) {
		RelationStorage relationSet = new RelationStorage();
		relationSet.add(relationList);
		return relationSet;
	}
	
	/**
	 * Convert URL address to host name.
	 * @param url URL adderess to convert.
	 * @return Returns string containing host name.
	 */
	public static String urlToHost(URL url) {
		return url.getHost();
	}
	
	public RelationStorage() {
		all = 	new RelationSet();
		urlSet = new UrlSet();
		urlToHostMap = new UrlMap<String>();
		fromMap = new RelationMap<URL>();
		toMap = new RelationMap<URL>();
		edgeTypeMap = new RelationMap<String>();
	}
	
	/**
	 * Deep copy constructor.
	 * @param ref Other storage object to copy from.
	 */
	public RelationStorage(RelationStorage ref) {
		all = 	new RelationSet(ref.all);
		urlSet = new UrlSet(ref.urlSet);
		urlToHostMap = new UrlMap<String>(ref.urlToHostMap);
		fromMap = new RelationMap<URL>(ref.fromMap);
		toMap = new RelationMap<URL>(ref.toMap);
		edgeTypeMap = new RelationMap<String>(ref.edgeTypeMap);
	}
	
	/**
	 * Deep clone method.
	 */
	public RelationStorage clone() throws CloneNotSupportedException {
		return new RelationStorage(this);
	}
	
	/**
	 * Add relation to storage
	 * @param relation Relation object to add to storage.
	 */
	public void add(Relation relation) {
		all.add(relation);
		addUrl(relation.from);
		addUrl(relation.to);
		fromMap.addToSet(relation.from, relation);
		toMap.addToSet(relation.to, relation);
		edgeTypeMap.addToSet(relation.edgeType, relation);
	}
	
	/**
	 * Add all relation to storage from given container.
	 * @param relationList Container to add relations from.
	 */
	public void add(Iterable<Relation> relationList) {
		for(Relation relation: relationList)
			this.add(relation);
	}
	
	/**
	 * Return container of relations, which has desired source URL.
	 * @param from Source URL of desired set of relations.
	 * @return Returns filled container with desired relations.
	 */
	public IContainer<Relation> getAllFrom(URL from) {
		return fromMap.get(from);
	}
	
	/**
	 * Return container of relations, which has desired target URL.
	 * @param to Target URL of desired set of relations.
	 * @return Returns filled container with desired relations.
	 */
	public IContainer<Relation> getAllTo(URL to) {
		return toMap.get(to);
	}
	
	/**
	 * Lookup for all relations incidenting with desired url.
	 * @param url Source/target URL of desired set of relations. 
	 * @return Returns filled container with desired raletions.
	 */
	public IContainer<Relation> getAllIncidents(URL url) {
		RelationList list = new RelationList();
		list.add(getAllFrom(url));
		list.add(getAllTo(url));
		return list;
	}
	
	/**
	 * Lookup for all relations of desired type.
	 * @param edgeType Type of desired set of relations.
	 * @return Returns filled container with desired relations.
	 */
	public IContainer<Relation> getAllOfEdgeType(String edgeType) {
		return edgeTypeMap.get(edgeType);
	}
	
	public Iterator<Relation> iterator() {
		return all.iterator();
	}
	
	/**
	 * @return Returns container containing all URLs managed by storage.
	 */
	public Iterable<URL> getUrls() {
		return urlSet;
	}
	
	/**
	 * Lookup for URLs belonging to desired host.
	 * @param host Host name to look for URLs.
	 * @return Returns filled container with desired set of relations.
	 */
	public Iterable<URL> getUrls(String host) {
		return urlToHostMap.get(host);
	}
	
	/**
	 * @return Returns container containing all hosts managed by storage.
	 */
	public Iterable<String> getHosts() {
		return urlToHostMap.getKeys();
	}
	
	/**
	 * @return Returns commons statistics for storage.
	 * @see Statistics
	 */
	public Statistics getStatistics() {
		int nHosts = 0;
		for (@SuppressWarnings("unused")
		String s:urlToHostMap.getKeys())
			nHosts++;
		return new Statistics(
				nHosts,
				urlSet.size(),
				all.size());
	}
	private void addUrl(URL url) {
		urlSet.add(url);
		urlToHostMap.addToSet(urlToHost(url), url);
	}
}
