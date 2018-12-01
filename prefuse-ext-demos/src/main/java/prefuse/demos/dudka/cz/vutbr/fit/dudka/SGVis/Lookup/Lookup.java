package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup;

/**
 * @package cz.vutbr.fit.dudka.SGVis.Lookup
 * Set of classes used for Google's Social Graph API lookup.
 * @author Kamil Dudka <xdudka00@stud.fit.vutbr.cz>
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class stands for
 * <a href="http://code.google.com/apis/socialgraph/docs/api.html">
 * Social Graph API lookup</a> lookup.
 */
public class Lookup {
	private SimpleDownloader downloader = new SimpleDownloader();
	private String urlBase;
	
	/**
	 * Create Lookup object (there is no communication at this time).
	 * @param urlBase URL of lookup server as string.
	 */
	public Lookup(String urlBase) {
		this.urlBase = urlBase;
	}
	
	/**
	 * Returns URL of lookup server as string.
	 */
	public String getUrlBase() {
		return urlBase;
	}
	
	/**
	 * Set URL of lookup server.
	 * @param urlBase URL of lookup server.
	 */
	public void setUrlBase(String urlBase) {
		this.urlBase = urlBase;
	}
	
	/**
	 * Perform lookup - blocking operation.
	 * @param rq Lookup request.
	 * @return Returns lookup response text.
	 * @throws MalformedURLException Invalid URL in request and/or lookup server's address.
	 * @throws IOException Network problem, ...
	 */
	public String lookup(Request rq) throws MalformedURLException, IOException {
		URL rqUrl = this.buildUrl(rq);
		return downloader.download(rqUrl);
	}
	
	private URL buildUrl(Request rq) throws MalformedURLException {
		String[] q = rq.getQ();
		if (q.length==0)
			throw new RuntimeException("Empty lookup");
		
		// TODO: handle URL strings containing query
		// (encode/decode URL)
		StringBuffer urlString =
			new StringBuffer(this.urlBase+"?q=");
		for (int i=0; i<q.length; i++)
			urlString.append((i+1>=q.length)?
				q[i]:
				q[i]+",");
		urlString.append("&edo="+(rq.edo?1:0));
		urlString.append("&edi="+(rq.edi?1:0));
		urlString.append("&fme="+(rq.fme?1:0));
		return new URL(urlString.toString());
	}
}
