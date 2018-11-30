//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.graph.external;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderListener;
import edu.berkeley.guir.prefuse.graph.event.GraphLoaderMulticaster;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

public abstract class GraphLoader implements Runnable {
	public static final int LOAD_NEIGHBORS = 0;
	public static final int LOAD_CHILDREN = 1;
	public static final int LOAD_PARENT = 2;
	protected List m_queue = new LinkedList();
	protected Graph m_graph;
	protected ItemRegistry m_registry;
	protected int m_maxSize = 5000;
	protected String m_keyField;
	protected LinkedHashMap m_cache;
	protected GraphLoaderListener m_listener;

	public GraphLoader(ItemRegistry var1, String var2) {
		this.m_cache = new LinkedHashMap(this.m_maxSize, 0.75F, true) {
			public boolean removeEldestEntry(Entry var1) {
				return GraphLoader.this.evict((ExternalEntity)var1.getValue());
			}
		};
		this.m_keyField = var2;
		this.m_registry = var1;
		this.m_graph = var1.getGraph();
		Thread var3 = new Thread(this);
		var3.setPriority(1);
		var3.start();
	}

	public void setMaximumCacheSize(int var1) {
		this.m_maxSize = var1;
	}

	public int getMaximumCacheSize() {
		return this.m_maxSize;
	}

	public void addGraphLoaderListener(GraphLoaderListener var1) {
		this.m_listener = GraphLoaderMulticaster.add(this.m_listener, var1);
	}

	public void removeGraphLoaderListener(GraphLoaderListener var1) {
		this.m_listener = GraphLoaderMulticaster.remove(this.m_listener, var1);
	}

	public void touch(ExternalEntity var1) {
		this.m_cache.get(var1.getAttribute(this.m_keyField));
	}

	public synchronized void loadNeighbors(ExternalNode var1) {
		this.submit(new GraphLoader.Job(0, var1));
	}

	public synchronized void loadChildren(ExternalTreeNode var1) {
		this.submit(new GraphLoader.Job(1, var1));
	}

	public synchronized void loadParent(ExternalTreeNode var1) {
		this.submit(new GraphLoader.Job(2, var1));
	}

	private synchronized void submit(GraphLoader.Job var1) {
		if (!this.m_queue.contains(var1)) {
			this.m_queue.add(var1);
			this.notifyAll();
		}

	}

	public boolean evict(ExternalEntity var1) {
		boolean var2 = this.m_cache.size() > this.m_maxSize && !this.m_registry.isVisible(var1);
		if (var2 && this.m_listener != null) {
			this.m_listener.entityUnloaded(this, var1);
		}

		if (var2) {
			var1.unload();
			this.m_graph.removeNode(var1);
		}

		return var2;
	}

	public void run() {
		while(true) {
			GraphLoader.Job var1 = this.getNextJob();
			if (var1 != null) {
				if (var1.type == 0) {
					ExternalNode var6 = (ExternalNode)var1.n;
					this.getNeighbors(var6);
					var6.setNeighborsLoaded(true);
				} else {
					ExternalTreeNode var2;
					if (var1.type == 1) {
						var2 = (ExternalTreeNode)var1.n;
						this.getChildren(var2);
						var2.setChildrenLoaded(true);
					} else if (var1.type == 2) {
						var2 = (ExternalTreeNode)var1.n;
						this.getParent(var2);
						var2.setParentLoaded(true);
					}
				}
			} else {
				try {
					synchronized(this) {
						this.wait();
					}
				} catch (InterruptedException var5) {
					;
				}
			}
		}
	}

	protected synchronized GraphLoader.Job getNextJob() {
		return this.m_queue.isEmpty() ? null : (GraphLoader.Job)this.m_queue.remove(0);
	}

	protected void foundNode(int var1, ExternalEntity var2, ExternalEntity var3, Edge var4) {
		boolean var5 = false;
		String var6 = var3.getAttribute(this.m_keyField);
		if (this.m_cache.containsKey(var6)) {
			var3 = (ExternalEntity)this.m_cache.get(var6);
			var5 = true;
		} else {
			this.m_cache.put(var6, var3);
		}

		var3.setLoader(this);
		if (var4 == null && var2 != null) {
			if (var1 == 2) {
				var4 = new DefaultEdge(var3, var2, this.m_graph.isDirected());
			} else {
				var4 = new DefaultEdge(var2, var3, this.m_graph.isDirected());
			}
		}

		ItemRegistry var7 = this.m_registry;
		synchronized(this.m_registry) {
			if (var1 == 0) {
				this.m_graph.addNode(var3);
				if (var2 != null) {
					this.m_graph.addEdge((Edge)var4);
				}
			} else if (var2 != null && (var1 == 2 || var1 == 1)) {
				((Tree)this.m_graph).addChild((Edge)var4);
				if (var1 == 1) {
					((ExternalTreeNode)var3).setParentLoaded(true);
				}
			}
		}

		if (this.m_listener != null && !var5) {
			this.m_listener.entityLoaded(this, var3);
		}

	}

	protected abstract void getNeighbors(ExternalNode var1);

	protected abstract void getChildren(ExternalTreeNode var1);

	protected abstract void getParent(ExternalTreeNode var1);

	public class Job {
		int type;
		ExternalEntity n;

		public Job(int var2, ExternalEntity var3) {
			this.type = var2;
			this.n = var3;
		}

		public boolean equals(Object var1) {
			if (!(var1 instanceof GraphLoader.Job)) {
				return false;
			} else {
				GraphLoader.Job var2 = (GraphLoader.Job)var1;
				return this.type == var2.type && this.n == var2.n;
			}
		}

		public int hashCode() {
			return this.type ^ this.n.hashCode();
		}
	}
}
