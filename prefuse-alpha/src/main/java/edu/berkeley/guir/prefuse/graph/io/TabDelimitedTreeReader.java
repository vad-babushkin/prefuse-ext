//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import edu.berkeley.guir.prefuse.graph.DefaultTreeNode;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

public class TabDelimitedTreeReader extends AbstractTreeReader {
	public static final String COMMENT = "#";

	public TabDelimitedTreeReader() {
	}

	public Tree loadTree(InputStream var1) throws IOException {
		BufferedReader var2 = new BufferedReader(new InputStreamReader(var1));
		String var4 = null;
		String var5 = null;
		int var6 = 0;
		boolean var7 = false;
		ArrayList var8 = new ArrayList();
		HashMap var9 = new HashMap();
		DefaultTreeNode var10 = null;
		int var11 = 0;

		String var3;
		String var25;
		TreeNode var26;
		while((var3 = var2.readLine()) != null) {
			++var6;

			try {
				if (!var3.startsWith("#")) {
					if (!var7) {
						StringTokenizer var21 = new StringTokenizer(var3);

						while(var21.hasMoreTokens()) {
							String var23 = var21.nextToken();
							var23 = var23.substring(0, var23.length() - 1);
							var8.add(var23);
						}

						var4 = (String)var8.get(0);
						var5 = (String)var8.get(1);
						var7 = true;
					} else {
						DefaultTreeNode var12 = new DefaultTreeNode();
						String[] var13 = var3.split("\t");

						for(int var14 = 0; var14 < var13.length; ++var14) {
							var12.setAttribute((String)var8.get(var14), var13[var14]);
						}

						var12.setAttribute("Key", String.valueOf(var11++));
						if (var9.containsKey(var4)) {
							var25 = "[" + var12.getAttribute(var4) + "]";
							throw new IllegalStateException("Found duplicate node label: " + var25 + " line " + var6);
						}

						var9.put(var12.getAttribute(var4), var12);
						var25 = var12.getAttribute(var5);
						if (var25.equals("")) {
							if (var10 != null) {
								String var15 = "[" + var12.getAttribute(var4) + "]";
								throw new IllegalStateException("Found multiple tree roots: " + var15 + " line " + var6);
							}

							var10 = var12;
						} else if (var9.containsKey(var25)) {
							var26 = (TreeNode)var9.get(var25);
							var26.addChild(new DefaultEdge(var26, var12));
						}
					}
				}
			} catch (NullPointerException var19) {
				System.err.println(var19 + " :: line " + var6);
				var19.printStackTrace();
			} catch (IllegalStateException var20) {
				System.err.println(var20);
			}
		}

		var2.close();
		Iterator var22 = var9.values().iterator();

		while(var22.hasNext()) {
			try {
				TreeNode var24 = (TreeNode)var22.next();
				if (var24.getParent() == null && var24 != var10) {
					var25 = var24.getAttribute(var5);
					var26 = (TreeNode)var9.get(var25);
					if (var26 == null) {
						String var16 = "[" + var24.getAttribute(var4) + ", " + var25 + "]";
						throw new IllegalStateException("Found parentless node: " + var16);
					}

					var26.addChild(new DefaultEdge(var26, var24));
				}
			} catch (NullPointerException var17) {
				var17.printStackTrace();
			} catch (IllegalStateException var18) {
				System.err.println(var18);
			}
		}

		System.out.println("Read in tree with " + (var10.getDescendantCount() + 1) + " nodes.");
		return new DefaultTree(var10);
	}
}
