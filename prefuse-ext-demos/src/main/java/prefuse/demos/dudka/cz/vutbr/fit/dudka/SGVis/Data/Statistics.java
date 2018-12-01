package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

/**
 * Simple structured type holding common storage statistics.
 */
public class Statistics {

	private int hosts;
	private int urls;
	private int rels;

	/**
	 * Simple constructor.
	 * @param hosts Total count of hosts managed by storage.
	 * @param urls Total count of URLs managed by storage.
	 * @param rels Total count of relations managed by storage.
	 */
	public Statistics(int hosts, int urls, int rels) {
		this.hosts = hosts;
		this.urls = urls;
		this.rels = rels;
	}

	/**
	 * @return Returns total count of hosts managed by storage.
	 */
	public int getHosts() {
		return hosts;
	}

	/**
	 * @return Returns total count of relations managed by storage.
	 */
	public int getRels() {
		return rels;
	}

	/**
	 * @return Returns total count of URLs managed by storage.
	 */
	public int getUrls() {
		return urls;
	}
}
