package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual;

import java.net.URL;
import java.util.HashSet;

import javax.swing.SwingWorker;

import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Config;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.RelationStorage;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup.Lookup;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup.Request;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup.Response;

/**
 * Background Lookup using Java 1.6+ swing worker.
 */
public class LookupWorker extends SwingWorker<Void, Void> {
	private URL url;
	private RelationStorage storage;
	private boolean downloadOk;
	private GraphView caller;

	private static HashSet<LookupWorker> set = new HashSet<LookupWorker>();
	private static int cntTotal = 0;
	private static int cntSuccess = 0;
	private static int cntFailed = 0;
	
	/**
	 * Initiate background lookup.
	 * @param url URL to lookup for.
	 * @param caller Reference to caller object.
	 */
	public static synchronized void run(URL url, GraphView caller) {
		LookupWorker worker = new LookupWorker(url, caller);
		set.add(worker);
		cntTotal++;
		worker.execute();
	}
	
	/**
	 * Kill all unfinished lookups.
	 */
	public static synchronized void killAll() {
		HashSet<LookupWorker> setClone = new HashSet<LookupWorker>(set);
		for (LookupWorker worker: setClone)
			worker.cancel(true);
		set.clear();
	}
	
	/**
	 * Returns total count of lookups.
	 */
	public static synchronized int getTotalCount() {
		return cntTotal;
	}
	
	/**
	 * Returns count of active (unfinished) lookups.
	 */
	public static synchronized int getActiveCount() {
		return set.size();
	}
	
	/**
	 * Returns count of successfully finished lookups.
	 */
	public static synchronized int getSuccessCount() {
		return cntSuccess; 
	}
	
	/**
	 * Returns count of failed lookups.
	 */
	public static synchronized int getFailedCount() {
		return cntFailed;
	}

	private static synchronized void removeFromSet(LookupWorker worker) {
		set.remove(worker);
	}

	private LookupWorker(URL url, GraphView caller) {
		super();
		this.url = url;
		this.storage = new RelationStorage();
		this.caller = caller;
		this.downloadOk = false;
	}

	/**
	 * Background thread's behavior.
	 */
	@Override
	protected Void doInBackground() throws Exception {
		try {
			Lookup lookup = Config.createLookup();
			Request rq = Config.createRequest(url.toString());
			String responseText = lookup.lookup(rq);
			Response response = new Response(responseText);
			response.addTo(this.storage);
			this.downloadOk = true;
		}
		catch (Exception e) {
			this.downloadOk = false;
		}
		return null;
	}

	/**
	 * This code is processed in Swing event-dispatch thread.
	 */
	@Override
	protected void done() {
		if (downloadOk) {
			caller.handleResponse(this.storage, url);
			cntSuccess++;
		} else {
			caller.lookupError(url);
			cntFailed++;
		}
		removeFromSet(this);
	}
}
