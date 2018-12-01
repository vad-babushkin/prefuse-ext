package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual;

/**
 * @package cz.vutbr.fit.dudka.SGVis.Visual
 * Set of classes for <a href="http://code.google.com/apis/socialgraph/docs/api.html">
 * Social Graph API lookup</a> visualization using
 * <a href="http://prefuse.org/">Prefuse</a> Library.
 * @author Kamil Dudka <xdudka00@stud.fit.vutbr.cz>
 */

import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.event.ChangeListener;

import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.PolarLocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.expression.ColumnExpression;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.collections.IntIterator;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Config;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.Relation;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.RelationStorage;

/**
 * Complex class for <a href="http://code.google.com/apis/socialgraph/docs/api.html">
 * Social Graph API lookup</a> visualization using
 * <a href="http://prefuse.org/">Prefuse</a> Library.
 * 
 * This class holds visualization-related data. To obtain swing widget
 * use GraphDisplay class.
 */
public class GraphView {

	private RelationStorage storage;
	private Table table;
	private Graph graph;
	private NodeTable nodeTable;
	private Visualization vis;
	private StatusManager status;
	private HashSet<String> expandedHosts;

	public GraphView() {
		vis = new Visualization();
		status = new StatusManager(this);
		storage = new RelationStorage();
		nodeTable = new NodeTable();
		expandedHosts = new HashSet<String>();

		table = new Table();
		table.addColumn("text", String.class);
		table.addColumn("type", String.class);
		graph = new Graph(table,true);

		// -- set up visualization --
		vis.add("tree", graph);
		vis.setInteractive("tree.edges", null, false);

		// -- set up renderers --
		LabelRenderer nodeRenderer = new LabelRenderer("text");
		nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
		nodeRenderer.setHorizontalAlignment(Constants.CENTER);
		nodeRenderer.setRoundedCorner(8,8);
		EdgeRenderer edgeRenderer = new EdgeRenderer(
				Constants.EDGE_TYPE_LINE,
				Constants.EDGE_ARROW_FORWARD
		);
		edgeRenderer.setArrowHeadSize(6, 10);
		DefaultRendererFactory rf = new DefaultRendererFactory(nodeRenderer);
		rf.add(new InGroupPredicate("tree.edges"), edgeRenderer);
		vis.setRendererFactory(rf);

		// -- set up processing actions --

		// colors
		ItemAction nodeColor = new NodeColorAction("tree.nodes");
		ItemAction textColor = new TextColorAction("tree.nodes");
		vis.putAction("textColor", textColor);

		ColorAction edgeColor = new ColorAction("tree.edges",
				VisualItem.STROKECOLOR, ColorLib.rgb(200,200,200));

		ColorAction arrowColor = new ColorAction("tree.edges",
				VisualItem.FILLCOLOR, ColorLib.rgb(100,100,100));


		FontAction fonts = new FontAction("tree.nodes", 
				FontLib.getFont("Tahoma", 10));
		//fonts.add(new InGroupPredicate("_focus_"), FontLib.getFont("Tahoma", 11));

		// recolor
		ActionList recolor = new ActionList();
		recolor.add(nodeColor);
		recolor.add(textColor);
		vis.putAction("recolor", recolor);

		// repaint
		ActionList repaint = new ActionList();
		repaint.add(recolor);
		repaint.add(new RepaintAction());
		vis.putAction("repaint", repaint);

		// animate paint change
		ActionList animatePaint = new ActionList(400);
		animatePaint.add(new ColorAnimator("tree.nodes"));
		animatePaint.add(new RepaintAction());
		vis.putAction("animatePaint", animatePaint);

		// create the tree layout action
		NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout("tree");
		vis.putAction("treeLayout", treeLayout);
		CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout("tree");
		vis.putAction("subLayout", subLayout);

		// create the filtering and layout
		ActionList filter = new ActionList();
		filter.add(new TreeRootAction("tree"));
		filter.add(fonts);
		filter.add(treeLayout);
		filter.add(subLayout);
		filter.add(textColor);
		filter.add(nodeColor);
		filter.add(edgeColor);
		filter.add(arrowColor);
		vis.putAction("filter", filter);

		// animated transition
		ActionList animate = new ActionList(1250);
		animate.setPacingFunction(new SlowInSlowOutPacer());
		animate.add(new QualityControlAnimator());
		animate.add(new VisibilityAnimator("tree"));
		animate.add(new PolarLocationAnimator("tree.nodes", "linear"));
		animate.add(new ColorAnimator("tree.nodes"));
		animate.add(new RepaintAction());
		vis.putAction("animate", animate);
		vis.alwaysRunAfter("filter", "animate");
	}

	/**
	 * Returns reference to internal visualization object.
	 * Use only if you know what are you doing.
	 */
	public Visualization getVisualization() {
		return vis;
	}

	/**
	 * Returns edge iterator of internal visualization object.
	 * Use only if you know what are you doing.
	 */
	public IntIterator edgeRows(int row) {
		return graph.edgeRows(row);
	}

	/**
	 * Returns reference to global storage.
	 * This storage may contain also not currently visible data.
	 */
	public RelationStorage getStorage() {
		return storage;
	}
	
	/**
	 * Returns count of nodes in current view.
	 */
	public int getNodeCount() {
		return graph.getNodeCount();
	}
	
	/**
	 * Returns count of edges in current view.
	 */
	public int getEdgeCount() {
		return graph.getEdgeCount();
	}
	
	/**
	 * Clear all - storage, view, ...
	 * And also kill all pending lookups.
	 */
	public void clear() {
		LookupWorker.killAll();
		vis.cancel("animatePaint");
		graph.clear();
		table.clear();
		storage = new RelationStorage();
		nodeTable.clear();
		expandedHosts.clear();
		vis.run("repaint");
	}

	private void updateVisual() {
		vis.cancel("animatePaint");
		final String errText = "Too many nodes opened. Try to collapse some hosts, if you experience long latency."; 
		if (graph.getNodeCount()>Config.getNodeCountWarn())
			status.showErrror(errText);
		else if (status.getStatus().equals(errText))
			status.showStatus(" ");
		vis.run("filter");
	}

	/**
	 * Remove node from view.
	 * Note: the node is NOT removed from storage.
	 * @param node ID of node to remove from view.
	 */
	public void removeNode(int node) {
		removeNodePrivate(node);
		vis.run("repaint");
	}

	private void removeNodePrivate(int node) {
		nodeTable.remove(node);
		graph.removeNode(node);
	}

	private void removeGroup(String host) {
		if (nodeTable.contains(host)) {
			this.removeNodePrivate(nodeTable.getId(host));
		} else {
			for (URL url: storage.getUrls(host)) {
				String urlString = url.toString();
				if (nodeTable.contains(urlString)) {
					this.removeNodePrivate(nodeTable.getId(urlString));
				}
			}
		}
		this.expandedHosts.remove(host);
	}


	private void addGroup(String host, boolean collapsed) {
		if (collapsed) {
			if (nodeTable.contains(host))
				return;

			int row = table.addRow();
			table.setString(row, 0, host);
			table.setString(row, 1, "G");
			nodeTable.add(host, row);

			for (URL url: storage.getUrls(host)) {
				this.addNodeEdges(url);
			}
		} else {
			for (URL url: storage.getUrls(host)) {
				String urlString = url.toString();
				if (nodeTable.contains(urlString))
					continue;

				int row = table.addRow();
				table.setString(row, 0, urlString);
				table.setString(row, 1, "U");
				nodeTable.add(urlString, row);

				this.addNodeEdges(url);
			}
			this.expandedHosts.add(host);
		}
	}

	private void addNodeEdges(URL url) {
		for (Relation rel: storage.getAllIncidents(url)) {
			// Obtain host-only and full url strings
			String fromUrl = rel.from.toString();
			String fromHost = RelationStorage.urlToHost(rel.from);
			String toUrl = rel.to.toString();
			String toHost = RelationStorage.urlToHost(rel.to);

			// check source node
			int s;
			if (nodeTable.contains(fromUrl))
				s = nodeTable.getId(fromUrl);
			else if (nodeTable.contains(fromHost))
				s = nodeTable.getId(fromHost);
			else
				continue;

			// check target node
			int t;
			if (nodeTable.contains(toUrl))
				t = nodeTable.getId(toUrl);
			else if (nodeTable.contains(toHost))
				t = nodeTable.getId(toHost);
			else
				continue;

			if (s!=t) {
				graph.addEdge(s,t);
			}
		}
	}

	/**
	 * Collapse all URL nodes to one (grouped by host) node.
	 * @param host Host name identifing the group of URLs.
	 */
	public void collapseHost(String host) {
		vis.cancel("animatePaint");
		this.removeGroup(host);
		this.addGroup(host, true);
		updateVisual();
	}

	/**
	 * Expand all URL nodes from grouped host node.
	 * @param host Host name to expand.
	 */
	public void expandHost(String host) {
		vis.cancel("animatePaint");
		this.removeGroup(host);
		this.addGroup(host, false);
		updateVisual();
	}

	/**
	 * Zoom and center current view to fit all nodes into visible part of display.
	 */
	public void zoomToFit() {
		vis.cancel("animatePaint");
		vis.run("zoomToFit");
	}

	/**
	 * Initiate new lookup for relations.
	 * @param url Address to lookup for.
	 */
	public void lookup(URL url) {
		status.setStatus("Looking for " + url.toString());
		LookupWorker.run(url, this);
	}

	/**
	 * This package-private method is called when lookup is finished. 
	 * @param current Temporary storage to add to global storage.
	 * @param url URL of lookup request.
	 */
	void handleResponse(RelationStorage current, URL url) {
		//setStatus("--- LookupWorker succesfully finished");
		storage.add(current);
		//setStatus("--- Response added to global storage");
		int count = graph.getNodeCount();
		boolean bEmpty = true;
		for(String host: current.getHosts()) {
			//setStatus("--- Adding group: " + host);
			this.addGroup(host, !expandedHosts.contains(host));
			bEmpty = false;
		}
		
		if (bEmpty)
			status.showStatus("Empty lookup for "+url.toString());
		else
			status.showStatus("Lookup complete for "+url.toString());

		if (count != graph.getNodeCount())
			this.updateVisual();
	}

	/**
	 * Add status observer to list.
	 * @param listener Object which wants to know about status changes.
	 */
	public void addStatusListener(ChangeListener listener) {
		status.addListener(listener);
	}
	
	/**
	 * Display text in statusbar and undisplay it after an amount of time.
	 * @param text Text to display in statusbar.
	 */
	public void showStatus(String text) {
		status.showStatus(text);
	}
	
	/**
	 * Returns current status message.
	 */
	public String getStatus() {
		return status.getStatus();
	}
	
	/**
	 * Returns true if status message is an error message.
	 */
	public boolean isStatusError() {
		return status.isStatusError();
	}
	
	/**
	 * This package-private method is called when lookup fails. 
	 * @param url URL of lookup request.
	 */
	void lookupError(URL url) {
		status.showErrror("Lookup failed for "+url.toString());
	}
	
	
	// ------------------------------------------------------------------------

	/**
	 * Switch the root of the tree by requesting a new spanning tree
	 * at the desired root
	 */
	private class TreeRootAction extends GroupAction {
		public TreeRootAction(String graphGroup) {
			super(graphGroup);
		}
		public void run(double frac) {
			TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
			if ( focus==null || focus.getTupleCount() == 0 )
				return;
			Graph g = (Graph)m_vis.getGroup(m_group);
			Node f = null;
			Iterator tuples = focus.tuples();
			while (tuples.hasNext() && !g.containsTuple(f=(Node)tuples.next()))
				f = null;
			if ( f == null )
				return;
			g.getSpanningTree(f);
		}
	}

	/**
	 * Set node fill colors
	 */
	private class NodeColorAction extends ColorAction {
		public NodeColorAction(String group) {
			super(group, VisualItem.FILLCOLOR, ColorLib.rgba(255,255,255,0));
			add(new ColumnExpression("_hover"), ColorLib.rgb(210,210,210));
			add(new ColumnExpression("_highlight"), ColorLib.rgb(210,210,210));
		}

	} // end of inner class NodeColorAction

	/**
	 * Set node text colors
	 */
	private class TextColorAction extends ColorAction {
		public TextColorAction(String group) {
			super(group, VisualItem.TEXTCOLOR, ColorLib.gray(0));
			add(new ColumnExpression("_hover"), ColorLib.rgb(255,0,0));
		}
	} // end of inner class TextColorAction

} // end of class RadialGraphView
