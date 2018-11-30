//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse;

import edu.berkeley.guir.prefuse.collections.CompositeItemIterator;
import edu.berkeley.guir.prefuse.collections.DefaultItemComparator;
import edu.berkeley.guir.prefuse.collections.VisibleItemIterator;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.event.ItemRegistryListener;
import edu.berkeley.guir.prefuse.event.RegistryEventMulticaster;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.RendererFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
	public static final String DEFAULT_NODE_CLASS = "node";
	public static final String DEFAULT_EDGE_CLASS = "edge";
	public static final String DEFAULT_AGGR_CLASS = "aggregate";
	public static final int DEFAULT_MAX_ITEMS = 10000;
	public static final int DEFAULT_MAX_DIRTY = 1;
	private Graph m_backingGraph;
	private Graph m_filteredGraph;
	private List m_displays;
	private FocusManager m_fmanager;
	private ItemFactory m_ifactory;
	private RendererFactory m_rfactory;
	private List m_entryList;
	private Map m_entryMap;
	private Map m_entityMap;
	private int m_size;
	private Comparator m_comparator;
	private ItemRegistryListener m_registryListener;
	private FocusListener m_focusListener;

	public ItemRegistry(Graph var1) {
		this(var1, true);
	}

	public ItemRegistry(Graph var1, boolean var2) {
		this.m_backingGraph = var1;
		this.m_displays = new ArrayList();
		this.m_fmanager = new FocusManager();

		try {
			this.m_ifactory = new ItemFactory();
			this.m_rfactory = new DefaultRendererFactory();
			this.m_entryList = new LinkedList();
			this.m_entryMap = new HashMap();
			this.m_entityMap = new HashMap();
			this.m_comparator = new DefaultItemComparator();
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		if (var2) {
			this.defaultInitialization();
		}

	}

	private synchronized void defaultInitialization() {
		addItemClass("node", NodeItem.class);
		addItemClass("edge", EdgeItem.class);
		addItemClass("aggregate", AggregateItem.class);
	}


	public synchronized void addItemClass(String var1, Class var2) {
		this.addItemClass(var1, var2, 1, 10000);
	}

	public synchronized void addItemClass(String var1, Class var2, int var3) {
		this.addItemClass(var1, var2, var3, 10000);
	}

	public synchronized void addItemClass(String var1, Class var2, int var3, int var4) {
		ItemRegistry.ItemEntry var5 = new ItemRegistry.ItemEntry(var1, var2, var3);
		this.m_entryList.add(var5);
		this.m_entryMap.put(var1, var5);
		this.m_ifactory.addItemClass(var1, var2, var4);
	}

	public synchronized Graph getGraph() {
		return this.m_backingGraph;
	}

	public synchronized void setGraph(Graph var1) {
		this.m_backingGraph = var1;
	}

	public synchronized Graph getFilteredGraph() {
		return this.m_filteredGraph;
	}

	public synchronized void setFilteredGraph(Graph var1) {
		this.m_filteredGraph = var1;
	}

	public synchronized void addDisplay(Display var1) {
		if (!this.m_displays.contains(var1)) {
			this.m_displays.add(var1);
		}

	}

	public synchronized boolean removeDisplay(Display var1) {
		boolean var2 = this.m_displays.remove(var1);
		if (var2) {
			var1.setItemRegistry((ItemRegistry)null);
		}

		return var2;
	}

	public synchronized Display getDisplay(int var1) {
		return (Display)this.m_displays.get(var1);
	}

	public synchronized int getDisplayCount() {
		return this.m_displays.size();
	}

	public synchronized void repaint() {
		Iterator var1 = this.m_displays.iterator();

		while(var1.hasNext()) {
			((Display)var1.next()).repaint();
		}

	}

	public synchronized List getDisplays() {
		ArrayList var1 = new ArrayList(this.m_displays.size());
		var1.addAll(this.m_displays);
		return var1;
	}

	public synchronized FocusManager getFocusManager() {
		return this.m_fmanager;
	}

	public synchronized FocusSet getDefaultFocusSet() {
		return this.m_fmanager.getDefaultFocusSet();
	}

	public synchronized RendererFactory getRendererFactory() {
		return this.m_rfactory;
	}

	public synchronized void setRendererFactory(RendererFactory var1) {
		this.m_rfactory = var1;
	}

	public synchronized Comparator getItemComparator() {
		return this.m_comparator;
	}

	public synchronized void setItemComparator(Comparator var1) {
		this.m_comparator = var1;
	}

	public synchronized int size(String var1) {
		ItemRegistry.ItemEntry var2 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var1);
		return var2 == null ? -1 : var2.itemList.size();
	}

	public synchronized int size() {
		return this.m_size;
	}

	public synchronized void garbageCollect(String var1) {
		ItemRegistry.ItemEntry var2 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var1);
		if (var2 != null) {
			this.garbageCollect(var2);
		} else {
			throw new IllegalArgumentException("The input string must be a recognized item class!");
		}
	}

	public synchronized void garbageCollect(ItemRegistry.ItemEntry var1) {
		var1.modified = true;
		Iterator var2 = var1.itemList.iterator();

		while(true) {
			while(var2.hasNext()) {
				VisualItem var3 = (VisualItem)var2.next();
				int var4 = var3.getDirty() + 1;
				var3.setDirty(var4);
				if (var1.maxDirty > -1 && var4 > var1.maxDirty) {
					var2.remove();
					this.removeItem(var1, var3, false);
				} else if (var4 > 1) {
					var3.setVisible(false);
				}
			}

			return;
		}
	}

	public synchronized void garbageCollectNodes() {
		this.garbageCollect("node");
	}

	public synchronized void garbageCollectEdges() {
		this.garbageCollect("edge");
	}

	public synchronized void garbageCollectAggregates() {
		this.garbageCollect("aggregate");
	}

	public synchronized void clear() {
		Iterator var1 = this.m_entryList.iterator();

		while(var1.hasNext()) {
			this.clear((ItemRegistry.ItemEntry)var1.next());
		}

	}

	private synchronized void clear(ItemRegistry.ItemEntry var1) {
		var1.modified = true;

		while(var1.itemList.size() > 0) {
			VisualItem var2 = (VisualItem)var1.itemList.get(0);
			this.removeItem(var1, var2, true);
		}

	}

	private void sortAll() {
		Iterator var1 = this.m_entryList.iterator();

		while(var1.hasNext()) {
			ItemRegistry.ItemEntry var2 = (ItemRegistry.ItemEntry)var1.next();
			if (var2.modified) {
				Collections.sort(var2.itemList, this.m_comparator);
				var2.modified = false;
			}
		}

	}

	public synchronized Iterator getItems(boolean var1) {
		this.sortAll();
		return new CompositeItemIterator(this.m_entryList, this.m_comparator, var1, false);
	}

	public synchronized Iterator getItems() {
		this.sortAll();
		return new CompositeItemIterator(this.m_entryList, this.m_comparator, true, false);
	}

	public synchronized Iterator getItemsReversed() {
		this.sortAll();
		return new CompositeItemIterator(this.m_entryList, this.m_comparator, true, true);
	}

	public synchronized Iterator getItems(String var1, boolean var2) {
		ItemRegistry.ItemEntry var3 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var1);
		if (var3 != null) {
			if (var3.modified) {
				Collections.sort(var3.itemList, this.m_comparator);
				var3.modified = false;
			}

			return (Iterator)(var2 ? new VisibleItemIterator(var3.itemList, false) : var3.itemList.iterator());
		} else {
			throw new IllegalArgumentException("The input string must be a recognized item class!");
		}
	}

	public synchronized void touch(String var1) {
		ItemRegistry.ItemEntry var2 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var1);
		if (var2 != null) {
			var2.modified = true;
		} else {
			throw new IllegalArgumentException("The input string must be a recognized item class!");
		}
	}

	public synchronized void touchNodeItems() {
		this.touch("node");
	}

	public synchronized void touchEdgeItems() {
		this.touch("edge");
	}

	public synchronized void touchAggregateItems() {
		this.touch("aggregate");
	}

	public synchronized Iterator getNodeItems() {
		return this.getItems("node", true);
	}

	public synchronized Iterator getNodeItems(boolean var1) {
		return this.getItems("node", var1);
	}

	public synchronized Iterator getEdgeItems() {
		return this.getItems("edge", true);
	}

	public synchronized Iterator getEdgeItems(boolean var1) {
		return this.getItems("edge", var1);
	}

	public synchronized Iterator getAggregateItems() {
		return this.getItems("aggregate", true);
	}

	public synchronized Iterator getAggregateItems(boolean var1) {
		return this.getItems("aggregate", var1);
	}

	public synchronized Entity getEntity(VisualItem var1) {
		Object var2 = this.m_entityMap.get(var1);
		if (var2 == null) {
			return null;
		} else {
			return var2 instanceof Entity ? (Entity)var2 : (Entity)((List)var2).get(0);
		}
	}

	public synchronized List getEntities(VisualItem var1) {
		Object var2 = this.m_entityMap.get(var1);
		Object var3;
		if (var2 instanceof Entity) {
			((List)(var3 = new LinkedList())).add(var2);
		} else {
			var3 = (List)var2;
		}

		return (List)var3;
	}

	public synchronized boolean isVisible(Node var1) {
		NodeItem var2;
		return (var2 = this.getNodeItem(var1)) != null && var2.isVisible();
	}

	public synchronized VisualItem getItem(String var1, Entity var2, boolean var3) {
		ItemRegistry.ItemEntry var4 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var1);
		if (var4 != null) {
			VisualItem var5 = (VisualItem)var4.itemMap.get(var2);
			if (!var3) {
				return var5;
			} else {
				if (var5 == null) {
					var5 = this.m_ifactory.getItem(var1);
					var5.init(this, var1, var2);
					this.addItem(var4, var2, var5);
				}

				if (var5 instanceof NodeItem) {
					((NodeItem)var5).removeAllNeighbors();
				}

				var5.setDirty(0);
				var5.setVisible(true);
				return var5;
			}
		} else {
			throw new IllegalArgumentException("The input string must be a recognized item class!");
		}
	}

	public synchronized NodeItem getNodeItem(Node var1) {
		return (NodeItem)this.getItem("node", var1, false);
	}

	public synchronized NodeItem getNodeItem(Node var1, boolean var2) {
		return (NodeItem)this.getItem("node", var1, var2);
	}

	public synchronized EdgeItem getEdgeItem(Edge var1) {
		return (EdgeItem)this.getItem("edge", var1, false);
	}

	public synchronized EdgeItem getEdgeItem(Edge var1, boolean var2) {
		return (EdgeItem)this.getItem("edge", var1, var2);
	}

	public synchronized AggregateItem getAggregateItem(Entity var1) {
		return (AggregateItem)this.getItem("aggregate", var1, false);
	}

	public synchronized AggregateItem getAggregateItem(Entity var1, boolean var2) {
		return (AggregateItem)this.getItem("aggregate", var1, var2);
	}

	public synchronized void addMapping(Entity var1, VisualItem var2) {
		String var3 = var2.getItemClass();
		ItemRegistry.ItemEntry var4 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var3);
		if (var4 != null) {
			this.addMapping(var4, var1, var2);
		} else {
			throw new IllegalArgumentException("The input string must be a recognized item class!");
		}
	}

	private synchronized void addMapping(ItemRegistry.ItemEntry var1, Entity var2, VisualItem var3) {
		var1.itemMap.put(var2, var3);
		if (this.m_entityMap.containsKey(var3)) {
			Object var4 = this.m_entityMap.get(var3);
			Object var5;
			if (var4 instanceof List) {
				var5 = (List)var4;
			} else {
				((List)(var5 = new LinkedList())).add(var4);
			}

			((List)var5).add(var2);
			this.m_entityMap.put(var3, var5);
		} else {
			this.m_entityMap.put(var3, var2);
		}

	}

	public synchronized void removeMappings(VisualItem var1) {
		ItemRegistry.ItemEntry var2 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var1.getItemClass());
		if (var2 != null) {
			this.removeMappings(var2, var1);
		} else {
			throw new IllegalArgumentException("Didn't recognize the item's item class.");
		}
	}

	private synchronized void removeMappings(ItemRegistry.ItemEntry var1, VisualItem var2) {
		if (this.m_entityMap.containsKey(var2)) {
			Object var3 = this.m_entityMap.get(var2);
			this.m_entityMap.remove(var2);
			if (var3 instanceof Entity) {
				var1.itemMap.remove(var3);
			} else {
				Iterator var4 = ((List)var3).iterator();

				while(var4.hasNext()) {
					var1.itemMap.remove(var4.next());
				}
			}
		}

	}

	private synchronized void addItem(ItemRegistry.ItemEntry var1, Entity var2, VisualItem var3) {
		this.addItem(var1, var3);
		this.addMapping(var1, var2, var3);
	}

	private synchronized void addItem(ItemRegistry.ItemEntry var1, VisualItem var2) {
		var1.itemList.add(var2);
		var1.modified = true;
		++this.m_size;
		if (this.m_registryListener != null) {
			this.m_registryListener.registryItemAdded(var2);
		}

	}

	private synchronized void removeItem(ItemRegistry.ItemEntry var1, VisualItem var2, boolean var3) {
		this.removeMappings(var1, var2);
		if (var3) {
			var1.itemList.remove(var2);
		}

		--this.m_size;
		if (this.m_registryListener != null) {
			this.m_registryListener.registryItemRemoved(var2);
		}

		this.m_ifactory.reclaim(var2);
	}

	public synchronized void removeItem(VisualItem var1) {
		ItemRegistry.ItemEntry var2 = (ItemRegistry.ItemEntry)this.m_entryMap.get(var1.getItemClass());
		if (var2 != null) {
			this.removeItem(var2, var1, true);
		} else {
			throw new IllegalArgumentException("Didn't recognize the item's item class.");
		}
	}

	public synchronized void addItemRegistryListener(ItemRegistryListener var1) {
		this.m_registryListener = RegistryEventMulticaster.add(this.m_registryListener, var1);
	}

	public synchronized void removeItemRegistryListener(ItemRegistryListener var1) {
		this.m_registryListener = RegistryEventMulticaster.remove(this.m_registryListener, var1);
	}

	public class ItemEntry {
		public boolean modified;
		public int maxDirty;
		public Class type;
		public String name;
		public List itemList;
		public Map itemMap;

		ItemEntry(String var2, Class var3, int var4) {
			try {
				this.name = var2;
				this.type = var3;
				this.itemList = new LinkedList();
				this.itemMap = new HashMap();
				this.modified = false;
				this.maxDirty = var4;
			} catch (Exception var6) {
				var6.printStackTrace();
			}

		}

		public List getItemList() {
			return this.itemList;
		}
	}
}
