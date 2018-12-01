package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Lookup;

import java.net.MalformedURLException;
import java.net.URL;

import prefuse.demos.dudka.org.json.JSONArray;
import prefuse.demos.dudka.org.json.JSONException;
import prefuse.demos.dudka.org.json.JSONObject;

import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.Relation;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.RelationStorage;

/**
 * This class stands for
 * <a href="http://code.google.com/apis/socialgraph/docs/api.html">
 * Social Graph API lookup</a> lookup response.
 * @see Lookup
 */
public class Response {
	private JSONObject jsObj;
	
	/**
	 * Create object from response text.
	 * @param responseText Text of response - mostly returned by Lookup object.
	 * @throws JSONException Parse error while reading response text.
	 */
	public Response(String responseText) throws JSONException {
		jsObj = new JSONObject(responseText);
	}
	
	/**
	 * Add response data to desired storage.
	 * @param storage Storage to add data to.
	 * @throws JSONException Parse error while reading response text.
	 * @throws MalformedURLException Invalid URL in response data.
	 */
	public void addTo(RelationStorage storage) throws JSONException, MalformedURLException {
		ResponseParser parser = new ResponseParser(storage);
		parser.parse(jsObj);
	}
	
	private class ResponseParser {
		private RelationStorage storage;
		public ResponseParser(RelationStorage storage) {
			this.storage = storage;
		}
		@SuppressWarnings("unchecked")
		public void parse(JSONObject response) throws JSONException, MalformedURLException {
			JSONObject nodes = response.getJSONObject("nodes");
			java.util.Iterator iter = nodes.keys();
			while (iter.hasNext()) {
				String hostName = (String) iter.next();
				this.handleHost(
						hostName,
						nodes.getJSONObject(hostName));
			}
		}
		private void handleHost(
				String hostName,
				JSONObject hostData
				) throws JSONException, MalformedURLException
		{
			this.handleHostData(hostName, hostData, false);
			this.handleHostData(hostName, hostData, true);
		}
		private void handleHostData(
				String hostName,
				JSONObject hostData,
				boolean backLink
				) throws JSONException, MalformedURLException
		{
			String nodeName = backLink?
					"nodes_referenced_by":
					"nodes_referenced";
			if (hostData.has(nodeName)) {
				JSONObject refs = hostData.getJSONObject(nodeName);
				java.util.Iterator iter = refs.keys();
				while (iter.hasNext()) {
					String refName = (String) iter.next();
					this.handleReference(
							hostName,
							refName,
							refs.getJSONObject(refName),
							backLink);
				}
			}
		}
		private void handleReference(
				String hostName,
				String refName,
				JSONObject refData,
				boolean backLink
				) throws JSONException, MalformedURLException
		{
			JSONArray types = refData.getJSONArray("types");
			for(int i=0; i<types.length(); i++)
				this.handleRelation(
						backLink?refName:hostName,
						backLink?hostName:refName,
						types.getString(i)
						);
		}
		private void handleRelation(
				String from,
				String to,
				String edgeType
				) throws MalformedURLException
		{
			URL fromUrl = new URL(from);
			URL toUrl = new URL(to);
			Relation relation = new Relation(fromUrl, toUrl, edgeType);
			storage.add(relation);
		}
	}
}
