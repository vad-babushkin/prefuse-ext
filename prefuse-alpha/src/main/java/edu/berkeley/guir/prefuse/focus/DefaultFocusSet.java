//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.focus;

import edu.berkeley.guir.prefuse.event.FocusEvent;
import edu.berkeley.guir.prefuse.event.FocusEventMulticaster;
import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class DefaultFocusSet implements FocusSet {
	private Set m_foci = new LinkedHashSet();
	private ArrayList m_tmp = new ArrayList();
	private FocusListener m_listener;

	public DefaultFocusSet() {
	}

	public void addFocusListener(FocusListener var1) {
		this.m_listener = FocusEventMulticaster.add(this.m_listener, var1);
	}

	public void removeFocusListener(FocusListener var1) {
		this.m_listener = FocusEventMulticaster.remove(this.m_listener, var1);
	}

	public void add(Entity var1) {
		if (this.m_foci.add(var1) && this.m_listener != null) {
			this.m_listener.focusChanged(new FocusEvent(this, 0, new Entity[]{var1}, (Entity[])null));
		}

	}

	public void add(Collection var1) {
		Iterator var2 = var1.iterator();

		while(var2.hasNext()) {
			Object var3 = var2.next();
			if (!(var3 instanceof Entity)) {
				throw new IllegalArgumentException("All foci must be of type Entity");
			}

			if (this.m_foci.add(var3) && this.m_listener != null) {
				this.m_tmp.add(var3);
			}
		}

		if (this.m_listener != null && this.m_tmp.size() > 0) {
			Entity[] var4 = (Entity[])this.m_tmp.toArray(FocusEvent.EMPTY);
			this.m_listener.focusChanged(new FocusEvent(this, 0, var4, (Entity[])null));
		}

		this.m_tmp.clear();
	}

	public void remove(Entity var1) {
		if (this.m_foci.remove(var1) && this.m_listener != null) {
			this.m_listener.focusChanged(new FocusEvent(this, 1, (Entity[])null, new Entity[]{var1}));
		}

	}

	public void remove(Collection var1) {
		Iterator var2 = var1.iterator();

		while(var2.hasNext()) {
			Object var3 = var2.next();
			if (this.m_foci.remove(var3) && this.m_listener != null) {
				this.m_tmp.add(var3);
			}
		}

		if (this.m_listener != null && this.m_tmp.size() > 0) {
			Entity[] var4 = (Entity[])this.m_tmp.toArray(FocusEvent.EMPTY);
			this.m_listener.focusChanged(new FocusEvent(this, 1, (Entity[])null, var4));
		}

	}

	public void set(Entity var1) {
		Entity[] var2 = null;
		Entity[] var3 = null;
		if (this.m_foci.size() > 0 && this.m_listener != null) {
			var3 = (Entity[])this.m_foci.toArray(FocusEvent.EMPTY);
		}

		this.m_foci.clear();
		if (this.m_foci.add(var1) && this.m_listener != null) {
			var2 = new Entity[]{var1};
		}

		if (var2 != null || var3 != null) {
			this.m_listener.focusChanged(new FocusEvent(this, 2, var2, var3));
		}

	}

	public void set(Collection var1) {
		Iterator var2 = var1.iterator();

		while(var2.hasNext()) {
			Object var3 = var2.next();
			if (!(var3 instanceof Entity)) {
				throw new IllegalArgumentException("All foci must be of type Entity");
			}
		}

		Entity[] var6 = null;
		Entity[] var4 = null;
		if (this.m_listener != null && this.m_foci.size() > 0) {
			var4 = (Entity[])this.m_foci.toArray(FocusEvent.EMPTY);
		}

		this.m_foci.clear();
		var2 = var1.iterator();

		while(var2.hasNext()) {
			Entity var5 = (Entity)var2.next();
			if (this.m_foci.add(var5) && this.m_listener != null) {
				this.m_tmp.add(var5);
			}
		}

		if (this.m_listener != null && this.m_tmp.size() > 0) {
			var6 = (Entity[])this.m_tmp.toArray(FocusEvent.EMPTY);
			this.m_tmp.clear();
		}

		if (var6 != null || var4 != null) {
			this.m_listener.focusChanged(new FocusEvent(this, 2, var6, var4));
		}

	}

	public void clear() {
		Entity[] var1 = null;
		if (this.m_listener != null && this.m_foci.size() > 0) {
			var1 = (Entity[])this.m_foci.toArray(FocusEvent.EMPTY);
		}

		this.m_foci.clear();
		if (var1 != null) {
			this.m_listener.focusChanged(new FocusEvent(this, 2, (Entity[])null, var1));
		}

	}

	public Iterator iterator() {
		return this.m_foci.iterator();
	}

	public int size() {
		return this.m_foci.size();
	}

	public boolean contains(Entity var1) {
		return this.m_foci.contains(var1);
	}
}
