package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup;

/**
 * This class stands for
 * <a href="http://code.google.com/apis/socialgraph/docs/api.html">
 * Social Graph API lookup</a> lookup request.
 * Value object type.
 * @see Lookup
 */
public class Request {
	protected String[] q;		///< Array of address to lookup for
	protected boolean edo;	///< "Return edges out from returned nodes" lookup parameter
	protected boolean edi;	///< "Return edges in to returned nodes." lookup parameter.
	protected boolean fme;	///< "Follow me links, also returning reachable nodes." lookup parameter.
	
	/**
	 * Simple constructor.
	 * @param q Array of address to lookup for
	 * @param edo "Return edges out from returned nodes" lookup parameter
	 * @param edi "Return edges in to returned nodes." lookup parameter.
	 * @param fme "Follow me links, also returning reachable nodes." lookup parameter.
	 */
	public Request(String[] q, boolean edo, boolean edi, boolean fme) {
		this.q = q;
		this.edo = edo;
		this.edi = edi;
		this.fme = fme;
	}
	
	/**
	 * Simple constructor.
	 * @param q Address to lookup for
	 * @param edo "Return edges out from returned nodes" lookup parameter
	 * @param edi "Return edges in to returned nodes." lookup parameter.
	 * @param fme "Follow me links, also returning reachable nodes." lookup parameter.
	 */
	public Request(String q, boolean edo, boolean edi, boolean fme) {
		this(new String[]{q}, edo, edi, false);
	}
	
	public String[] getQ() {
		return q;
	}
	public void setQ(String[] q) {
		this.q = q;
	}
	public boolean isEdo() {
		return edo;
	}
	public void setEdo(boolean edo) {
		this.edo = edo;
	}
	public boolean isEdi() {
		return edi;
	}
	public void setEdi(boolean edi) {
		this.edi = edi;
	}
	public boolean isFme() {
		return fme;
	}
	public void setFme(boolean fme) {
		this.fme = fme;
	}
}
