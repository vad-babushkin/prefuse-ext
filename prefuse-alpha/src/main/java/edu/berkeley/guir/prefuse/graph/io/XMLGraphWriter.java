//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class XMLGraphWriter extends AbstractGraphWriter {
	public static final String NODE = "node";
	public static final String EDGE = "edge";
	public static final String ATT = "att";
	public static final String ID = "id";
	public static final String LABEL = "label";
	public static final String SOURCE = "source";
	public static final String TARGET = "target";
	public static final String WEIGHT = "weight";
	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String VALUE = "value";
	public static final String LIST = "list";
	public static final String GRAPH = "graph";
	public static final String DIRECTED = "directed";
	public static final String[] NODE_ATTR = new String[]{"id", "label", "weight"};
	public static final String[] EDGE_ATTR = new String[]{"label", "weight"};

	public XMLGraphWriter() {
	}

	public void writeGraph(Graph var1, OutputStream var2) throws IOException {
		PrintWriter var3 = new PrintWriter(new BufferedOutputStream(var2));
		this.assignIDs(var1);
		this.printGraph(var3, var1);
		var3.flush();
	}

	protected void assignIDs(Graph var1) {
		Set var2 = this.initializeIDs(var1);
		int var3 = 0;
		Iterator var5 = var1.getNodes();

		while(true) {
			Node var6;
			do {
				if (!var5.hasNext()) {
					return;
				}

				var6 = (Node)var5.next();
			} while(var6.getAttribute("id") != null);

			String var4;
			do {
				++var3;
				var4 = String.valueOf(var3);
			} while(var2.contains(var4));

			var6.setAttribute("id", var4);
		}
	}

	private Set initializeIDs(Graph var1) {
		HashSet var2 = new HashSet(var1.getNodeCount() / 2);
		Iterator var4 = var1.getNodes();

		while(var4.hasNext()) {
			Node var5 = (Node)var4.next();
			String var3;
			if ((var3 = var5.getAttribute("id")) != null) {
				var2.add(var3);
			}
		}

		return var2;
	}

	private void printGraph(PrintWriter var1, Graph var2) {
		int var3 = var2.isDirected() ? 1 : 0;
		var1.println("<!-- prefuse graph writer :: " + new Date() + " -->");
		var1.println("<graph directed=\"" + var3 + "\">");
		var1.println("  <!-- nodes -->");
		Iterator var4 = var2.getNodes();

		while(var4.hasNext()) {
			Node var5 = (Node)var4.next();
			this.printNode(var1, var5);
		}

		var1.println("  <!-- edges -->");
		Iterator var7 = var2.getEdges();

		while(var7.hasNext()) {
			Edge var6 = (Edge)var7.next();
			this.printEdge(var1, var6);
		}

		var1.println("</graph>");
	}

	private void printNode(PrintWriter var1, Node var2) {
		var1.print("  <node");

		for(int var3 = 0; var3 < NODE_ATTR.length; ++var3) {
			String var4 = NODE_ATTR[var3];
			String var5 = var2.getAttribute(var4);
			if (var5 != null) {
				var1.print(" " + var4 + "=\"" + var5 + "\"");
			}
		}

		var1.print(">");
		Map var8 = var2.getAttributes();
		Iterator var9 = var8.keySet().iterator();
		boolean var10 = false;

		while(var9.hasNext()) {
			String var6 = (String)var9.next();
			if (!this.contains(NODE_ATTR, var6)) {
				String var7 = (String)var8.get(var6);
				if (!var10) {
					var1.println();
					var10 = true;
				}

				this.printAttr(var1, var6, var7);
			}
		}

		var1.println("  </node>");
	}

	private void printEdge(PrintWriter var1, Edge var2) {
		String var3 = var2.getFirstNode().getAttribute("id");
		String var4 = var2.getSecondNode().getAttribute("id");
		var1.print("  <edge");
		var1.print(" source=\"" + var3 + "\"");
		var1.print(" target=\"" + var4 + "\"");

		for(int var5 = 0; var5 < EDGE_ATTR.length; ++var5) {
			String var6 = EDGE_ATTR[var5];
			String var7 = var2.getAttribute(var6);
			if (var7 != null) {
				var1.print(" " + var6 + "=\"" + var7 + "\"");
			}
		}

		var1.print(">");
		Map var10 = var2.getAttributes();
		Iterator var11 = var10.keySet().iterator();
		boolean var12 = false;

		while(var11.hasNext()) {
			String var8 = (String)var11.next();
			if (!this.contains(EDGE_ATTR, var8)) {
				String var9 = (String)var10.get(var8);
				if (!var12) {
					var1.println();
					var12 = true;
				}

				this.printAttr(var1, var8, var9);
			}
		}

		var1.println("  </edge>");
	}

	private void printAttr(PrintWriter var1, String var2, String var3) {
		var1.println("    <att name=\"" + var2 + "\" " + "value" + "=\"" + var3 + "\"/>");
	}

	private boolean contains(String[] var1, String var2) {
		for(int var3 = 0; var3 < var1.length; ++var3) {
			if (var1[var3].equals(var2)) {
				return true;
			}
		}

		return false;
	}
}
