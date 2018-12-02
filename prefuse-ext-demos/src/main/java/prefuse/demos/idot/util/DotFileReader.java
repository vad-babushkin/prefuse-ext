package prefuse.demos.idot.util;

import prefuse.demos.idot.Config;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.io.AbstractGraphReader;
import prefuse.data.io.DataIOException;
import prefuse.util.ColorLib;

/**
 * A class for reading simple dot files and building the corresponding graph.
 * Many of the advanced constructs, such as subgraph, are not recognized, and
 * might lead either into parsing error, ignorance, or erroneous behaviour.
 * <br>
 * Only a limited subset of attributes for nodes and edges are recognized.
 * The recognized attributes include those in the {@link #NODE_ATTRIBUTES}
 * and {@link #EDGE_ATTRIBUTES} arrays, as well as the "color" attribute.
 */
public class DotFileReader extends AbstractGraphReader {
	/** the known color names and the corresbonding color codes */
	protected static final HashMap<String,Color> KNOWN_COLORS;
	
	/** the node attribute names, types and default values */
	protected static final Object[][] NODE_ATTRIBUTES = new Object[][] {
		// name in dot, data type, default
		{ "label", 		String.class, 	null },
		{ "shape", 		String.class, 	"circle" },
		{ "style", 		String.class, 	"normal" },
		{ "fontname",  	String.class, 	"Helvetica" },
		{ "fontsize",  	int.class, 		"12" },
		{ "peripheries", int.class, 	"1" },
		{ "width",     	float.class, 	"0" },
		{ "height",    	float.class, 	"0" },
		{ "fixedsize", 	boolean.class, 	"false" },
		{ "pos",  		String.class, 	"" }
	};

	/** the edge attribute names, types and default values */
	protected static final Object[][] EDGE_ATTRIBUTES = new Object[][] {
		// name in dot, data type, default
		{ "label", 		String.class, 	null },
		{ "style", 		String.class, 	"normal" },
		{ "fontname",  	String.class, 	"Helvetica" },
		{ "fontsize",  	int.class, 		"12" },
		{ "pos",  		String.class, 	"" },
		{ "lp",  		String.class, 	null }
	};

	
	static {
		KNOWN_COLORS = new HashMap<String, Color>();
		KNOWN_COLORS.put("BLACK", Color.BLACK);
		KNOWN_COLORS.put("BLUE", Color.BLUE);
		KNOWN_COLORS.put("CYAN", Color.CYAN);
		KNOWN_COLORS.put("DARGGRAY", Color.DARK_GRAY);
		KNOWN_COLORS.put("GRAY", Color.GRAY);
		KNOWN_COLORS.put("GREEN", Color.GREEN);
		KNOWN_COLORS.put("LIGHTGRAY", Color.LIGHT_GRAY);
		KNOWN_COLORS.put("MAGENTA", Color.MAGENTA);
		KNOWN_COLORS.put("ORANGE", Color.ORANGE);
		KNOWN_COLORS.put("PINK", Color.PINK);
		KNOWN_COLORS.put("RED", Color.RED);
		KNOWN_COLORS.put("WHITE", Color.WHITE);
		KNOWN_COLORS.put("YELLOW", Color.YELLOW);	
	}
	
	
	/**
	 * The default values for nodes ("node"), edges ("edge"), and 
	 * the whole graph ("graph")
	 */
	protected HashMap<String, HashMap<String, String>> groupDefaults;
	
	/**
	 * Loads the graph from the specified input stream
	 * @param is the input stream to load the graph from
	 * @return the loaded graph
	 * 
	 * @throws IOException
	 */
	public Graph loadGraph(InputStream is) throws IOException {
		return loadGraph(new InputStreamReader(is));
	}

	/**
	 * Loads the graph from the specified input reader
	 * @param inputReader the input stream reader to load the graph from
	 * @return the loaded graph
	 * 
	 * @throws IOException
	 */
	public Graph loadGraph(Reader inputReader) throws IOException {
		Graph g;
		HashMap<String, Node> nodes = new HashMap<String, Node>();	
		groupDefaults = initializeDefaults();

		BufferedReader br = new BufferedReader(inputReader);

		String nodeSepInEdges = "->";

		// skip lines before the graph
		String l;
		do {
			l = br.readLine();				
		} while(l != null && ! l.matches(".*graph.*\\{.*"));

		if(l == null) 
			throw new IOException("no graph found in file");
		
		// load either a directed or undirected graph
		if(l.contains("digraph")) {
			g = new Graph(true);
		} else {
			g = new Graph(false);
			nodeSepInEdges = "--";
		}
		
		l = br.readLine();
		
		Pattern nodep = Pattern.compile("\\s*(\\S+)\\s+\\[(.*)].*");
		Pattern edgep = Pattern.compile("\\s*(\\S+)\\s+"+ nodeSepInEdges +"\\s+([^\\[ ;]+)(?:\\s+\\[(.*)])?.*");
		
		while(l != null && ! l.trim().equals("}")) {
			// if (Config.print) System.out.println("Checking line: " + l);
			Matcher m = nodep.matcher(l);
			
			if("".equals(l.trim()) || l.matches("\\s*//.*") ||
					 l.matches("\\s*/\\*([^*]|\\*[^/])*\\*/\\s*")) {

				// these check for empty and comment lines
				// note multiline /* ... */ style comments are not recognized					
				// just ignore the line
				
			} else if(m.matches()) {
				if(isNodeLabel(m.group(1))) {
					if (Config.print) System.out.println("line '" + l + "' looks like a node");
					String nodename = m.group(1);

					HashMap<String, String> attributes = parseAttributes(m.group(2));

					Node n = nodes.get(nodename);
					if(n == null) {
						n = g.addNode();
						nodes.put(nodename, n);
					}

					setNodeAttributes(n, attributes);
					
					addField(n.getTable(), "nodename", String.class, null);
					n.setString("nodename", nodename);
					
					// point shaped nodes shouldn't have labels
					if("point".equalsIgnoreCase(n.getString("shape")) 
							&& "\\N".equals(n.getString("label")))
						n.setString("label", "");
					
					// special node label \N should be replaced with the node name
					if("\\N".equals(n.getString("label")))
						n.setString("label", nodename);
										
				} else {
					if (Config.print) System.out.println("general attributes: " + l);
					String groupname = m.group(1).toLowerCase();
					String attributes = m.group(2);
					
					HashMap<String, String> attributeSet = parseAttributes(attributes);
					setGroupDefaults(groupname, attributeSet);					
				}
			} else {
				m = edgep.matcher(l);
				if(m.matches()) {
					if (Config.print) System.out.println("line '" + l + "' looks like an edge");
					String from = m.group(1);
					String to = m.group(2);
					
					//if (Config.print) System.out.println("edge: " + from + "->" + to + ".");					
										
					Node fromnode = nodes.get(from);
					Node tonode = nodes.get(to);
				
					if(fromnode == null) {
						fromnode = g.addNode();
						nodes.put(from, fromnode);
					}
					if(tonode == null) {
						tonode = g.addNode();
						nodes.put(to, tonode);
					}
										
					Edge e = g.addEdge(fromnode, tonode);

					HashMap<String, String>attributes = parseAttributes(m.group(3));
					setEdgeAttributes(e, attributes);
					
				} else {
					// it's probable that we are missing some data
					System.err.println("Warning, line ignored: " + l);
				}
			}
			
			l = br.readLine();
			
			// dot seems to break very long lines (about 150 chars)
			// to multiple lines with a single '\' at the end of continued lines
			// try to recombine them
			// if this doesn't work, some edges might appear as straight lines
			// though they should be curves
			while(l != null && l.endsWith("\\"))
				l = l.substring(0, l.length()-1) + br.readLine();
		}
		
		groupDefaults = null;
		return g;
	}

	/**
	 * Initializes the default values for known attributes
	 * @return
	 */
	private HashMap<String, HashMap<String, String>> initializeDefaults() {
		HashMap<String, HashMap<String, String>> defaults = 
			new HashMap<String, HashMap<String,String>>();
		
		HashMap<String, String> graphDefaults = new HashMap<String, String>();
		defaults.put("graph", graphDefaults);
		
		HashMap<String, String> nodeDefaults = new HashMap<String, String>();	
		for(Object[] o : NODE_ATTRIBUTES) {
			// name in dot, data type, default
			nodeDefaults.put((String) o[0], (String)o[2]);
		}
		
		defaults.put("node", nodeDefaults);
		
		HashMap<String, String> edgeDefaults = new HashMap<String, String>();
		for(Object[] o : EDGE_ATTRIBUTES) {
			// name in dot, data type, default
			edgeDefaults.put((String) o[0], (String)o[2]);
		}
		defaults.put("edge", edgeDefaults);
		
		return defaults;
	}

	/**
	 * Sets the default values for the whole graph, nodes or edges.
	 * If a certain node or edge doesn't have a value for a certain
	 * attrribute, the value is first looked up from the defaults set 
	 * through this method (which is called after reading a 
	 * "node [attr=value...]" or "edge [attr=value...]" line from the
	 * input file), and if not found there, then from the original
	 * defaults from the {@link #NODE_ATTRIBUTES} or 
	 * {@link #EDGE_ATTRIBUTES} arrays.
	 * 
	 * @param groupname "graph", "node", or "edge"
	 * @param attributes
	 */
	private void setGroupDefaults(String groupname, HashMap<String, String> attributes) {
		if (Config.print) System.out.println("setting defaults for all of: " + groupname);
		HashMap<String, String> defaults = groupDefaults.get(groupname);
		if(defaults != null) {
			defaults.putAll(attributes);
		} else {
			System.err.println("I don't know what to do with '" + groupname + "'");
		}
	}

	private String getGroupDefault(String groupName, String attribute) {
		//if (Config.print) System.out.println("default " + groupName  + "->" + attribute + ": '" 
		//		+ groupDefaults.get(groupName).get(attribute) + "'");
		return groupDefaults.get(groupName).get(attribute);
	}
	
	/**
	 * Sets the attribute values for a node
	 * 
	 * @param n
	 * @param attributes
	 */
	private void setNodeAttributes(Node n, HashMap<String, String> attributes) {
		for(Object[] o : NODE_ATTRIBUTES) {
			// name in dot, data type, default
			String nameInDot = (String) o[0];
			Object value = attributes.get(nameInDot);
			if(value == null) {
				value = getGroupDefault("node", nameInDot);
			}
			if(value == null) {
				value = o[2];
			}
			
			Object defaultValue = o[2];
			if(o[1].equals(int.class)) {
				defaultValue = Integer.parseInt((String)defaultValue);
			} else if(o[1].equals(float.class)) {
				defaultValue = Float.parseFloat((String)defaultValue);
			} else if(o[1].equals(boolean.class)) {
				defaultValue = Boolean.parseBoolean((String)defaultValue);
			}
			
			addField(n.getTable(), nameInDot, (Class)o[1], defaultValue);
			n.set(nameInDot, value);
		}
		
		String color = attributes.get("color");
		if(color == null) {
			color = getGroupDefault("node", "color");
		}
		
		addField(n.getTable(), "color", int.class, ColorLib.gray(0));
		if(color!=null) {
			Color c = getColor(color);
			n.setInt("color", ColorLib.color(c));
		} else {			
			// default is black
			n.setInt("color", ColorLib.gray(0)); 
		}
				
		// arrgh, quirks
		if(n.getFloat("width") > 0 
				&& n.getFloat("height") == 0
				&& n.getBoolean("fixedsize")) {
			n.setFloat("height", n.getFloat("width"));
		}		
	}

	/**
	 * Sets the attribute values for an edge
	 * 
	 * @param e
	 * @param attributes
	 */
	private void setEdgeAttributes(Edge e, HashMap<String, String> attributes) {
		for(Object[] o : EDGE_ATTRIBUTES) {
			// name in dot, data type, default
			String nameInDot = (String) o[0];
			Object value = attributes.get(nameInDot);
			if(value == null) {
				value = getGroupDefault("edge", nameInDot);
			}
			if(value == null) {
				value = o[2];
			}
			
			Object defaultValue = o[2];
			if(o[1].equals(int.class)) {
				defaultValue = Integer.parseInt((String)defaultValue);
			} else if(o[1].equals(float.class)) {
				defaultValue = Float.parseFloat((String)defaultValue);
			} else if(o[1].equals(boolean.class)) {
				defaultValue = Boolean.parseBoolean((String)defaultValue);
			}
			
			addField(e.getTable(), nameInDot, (Class)o[1], defaultValue);
			e.set(nameInDot, value);
		}
		
		String color = attributes.get("color");
		if(color == null) {
			color = getGroupDefault("edge", "color");
		}

		addField(e.getTable(), "color", int.class, ColorLib.gray(0));
		if(color!=null) {
			Color c = getColor(color);
			e.setInt("color", ColorLib.color(c));
		} else {
			// default is black
			e.setInt("color", ColorLib.gray(0)); 
		}	
	}
	
	/**
	 * Adds the field to a table if not already present
	 *  
	 * @param table  the table to add the field to
	 * @param field  the field to add
	 * @param type   the class of the field
	 */
	private void addField(Table table, String field, Class type, Object defaultValue) {
		if(!table.canSet(field, type)) {
			if(table.getColumnNumber(field) != -1) {
				System.err.println("field " + field + " has wrong type, fixing");
				table.removeColumn(field);
			}
			
			table.addColumn(field, type, defaultValue);
		}
	}

	
	/**
	 * Gets a color by name from the table of known colors.
	 * 
	 * @param color  the name of the oclor, e.g. "red"
	 * @return  the corresponding color, or Color.BLACK if not found
	 */
	private Color getColor(String color) {
		Color c = KNOWN_COLORS.get(color.toUpperCase());
		if(c == null) {
			System.err.println("Color " + color + " is unknown");
			c = Color.BLACK;
		}
		return c;
	}

	/**
	 * Returns true if the parameter isn't one of the known
	 * keywords for general graph, node or edge attributes
	 * 
	 * @param candidate
	 * @return true, if candidate is not one of "graph", "node" or "edge"
	 */
	private boolean isNodeLabel(String candidate) {		
		return 
			! candidate.equalsIgnoreCase("graph")
			&& ! candidate.equalsIgnoreCase("node")
			&& ! candidate.equalsIgnoreCase("edge");
	}

	/**
	 * Parses attribute-value pairs from the input string
	 *  
	 * @param attributestring the string containing the attributes 
	 * 							and their values
	 * @return  a HashMap containing the name-value pairs for attributes
	 * 			found in attributestring 
	 */
	private HashMap<String, String> parseAttributes(String attributestring) {		
		HashMap<String, String> attribs = new HashMap<String, String>();
		
		if(attributestring != null) {
			Pattern attrp = Pattern.compile("\\s*(\\S+?)\\s*=\\s*(?:(?:\"([^\"]*)\")|([^ ,]*)),?");

			//if (Config.print) System.out.println("parsing attributes from: '"+ attributestring + "'");

			Matcher m = attrp.matcher(attributestring);
			while(m.find()) {
				String attr = m.group(1);
				String value = m.group(2);
				if(value == null) {
					// value is not in quotes
					value = m.group(3);
				}

				attribs.put(attr, value);
			}

			for(String key : attribs.keySet()) {
				if (Config.print) System.out.println("found: " + key + "->" + attribs.get(key) + ".");
			}
		}

		return attribs;
	}

	@Override
	public Graph readGraph(InputStream is) throws DataIOException {
		try {
			return loadGraph(is);
		} catch (IOException e) {
			throw new DataIOException(e);
		}
	}
}
