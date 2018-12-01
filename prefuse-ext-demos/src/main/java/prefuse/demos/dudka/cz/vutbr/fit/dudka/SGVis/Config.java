package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis;

import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup.Lookup;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup.Request;

/**
 * This class holds common application's configuration.
 * Design pattern singleton. This class is not thread-safe.
 */
public class Config {
	/**
	 * Application name
	 */
	public static final String APP_NAME = "SGVis";
	
	/**
	 * Default lookup server
	 */
	public static final String DEF_LOOKUP_SERVER = "http://socialgraph.apis.google.com/lookup";
	
	/**
	 * Default address to lookup for
	 */
	public static final String DEF_LOOKUP_FOR = "http://linkedin.com";

	/**
	 * Maximal count of concurrent lookups performed in background.
	 */
	public static final int MAX_CONCURRENT_LOOKUPS = 3;
	
	/**
	 * "Return edges out from returned nodes" lookup parameter.
	 */
	public static boolean DEF_LOOKUP_RQ_EDO = true;

	/**
	 * "Return edges in to returned nodes." lookup parameter.
	 */
	public static boolean DEF_LOOKUP_RQ_EDI = true;
	
	/**
	 * "Follow me links, also returning reachable nodes." lookup parameter.
	 */
	public static boolean DEF_LOOKUP_RQ_FME = true;
	
	/**
	 * Node count warrning's threshold
	 */
	public static int DEF_NODE_COUNT_WARN = 80;
	
	/**
	 * @return Returns initialized Lookup object.
	 */
	public static Lookup createLookup() {
		return getInstance().createLookupPrivate();
	}
	
	/**
	 * Creates Request object for given address. 
	 * @param q Address to lookup.
	 * @return Returns initialized Request object.
	 */
	public static Request createRequest(String q) {
		return createRequest(new String[]{q});
	}
	
	/**
	 * Creates Request object for given array of address. 
	 * @param q Array of address to lookup.
	 * @return Returns initialized Request object.
	 */
	public static Request createRequest(String[] q) {
		return getInstance().createRequestPrivate(q);
	}
	
	/**
	 * @return Returns node count warrning's threshold.
	 */
	public static int getNodeCountWarn() {
		return getInstance().nodeCountWarn;
	}
	
	private static Config instance = null;
	private static Config getInstance() {
		if (null==instance)
			instance = new Config();
		return instance;
	}
	
	private String lookupServer;
	private boolean lookupRqEdo;
	private boolean lookupRqEdi;
	private boolean lookupRqFme;
	private int nodeCountWarn;
	private Config() {
		this.lookupServer = DEF_LOOKUP_SERVER;
		this.lookupRqEdo = DEF_LOOKUP_RQ_EDO;
		this.lookupRqEdi = DEF_LOOKUP_RQ_EDI;
		this.lookupRqFme = DEF_LOOKUP_RQ_FME;
		this.nodeCountWarn = DEF_NODE_COUNT_WARN;
	}
	
	private Lookup createLookupPrivate() {
		return new Lookup(this.lookupServer);
	}
	private Request createRequestPrivate(String[] q) {
		return new Request(q,
				this.lookupRqEdo,
				this.lookupRqEdi,
				this.lookupRqFme);
	}
}
