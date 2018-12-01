package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual;

import java.util.HashMap;

/**
 * String-to-ID map for nodes.
 * This class is not very useful at all and
 * should be removed later.
 */
class NodeTable {
	private HashMap<String,Integer> textToIdMap;
	private HashMap<Integer,String> idToTextMap;
	public NodeTable() {
		textToIdMap = new HashMap<String, Integer>();
		idToTextMap = new HashMap<Integer, String>();
	}
	public void add(String text, int id) {
		textToIdMap.put(text, id);
		idToTextMap.put(id, text);
	}
	public void remove(String text) {
		Integer id = (Integer) textToIdMap.get(text);
		assert (null!=id):"Invalid lookup";
		textToIdMap.remove(text);
		idToTextMap.remove(id);
	}
	public void remove(int id) {
		String text = (String) idToTextMap.get(id);
		assert (null!=text):"Invalid lookup";
		idToTextMap.remove(id);
		textToIdMap.remove(text);
	}
	public int getId(String text) {
		Integer id = (Integer) textToIdMap.get(text);
		assert (null!=id):"Invalid lookup";
		return id.intValue();
	}
	public boolean contains(String text) {
		Integer id = (Integer) textToIdMap.get(text);
		return (null!=id);
	}
	public void clear() {
		textToIdMap.clear();
		idToTextMap.clear();
	}
}
