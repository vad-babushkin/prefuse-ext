package edu.berkeley.guir.prefuse.focus;

import edu.berkeley.guir.prefuse.event.FocusListener;
import edu.berkeley.guir.prefuse.graph.Entity;

import java.util.Collection;
import java.util.Iterator;

public abstract interface FocusSet {
	public abstract void addFocusListener(FocusListener paramFocusListener);

	public abstract void removeFocusListener(FocusListener paramFocusListener);

	public abstract void add(Entity paramEntity);

	public abstract void add(Collection paramCollection);

	public abstract void remove(Entity paramEntity);

	public abstract void remove(Collection paramCollection);

	public abstract void set(Entity paramEntity);

	public abstract void set(Collection paramCollection);

	public abstract void clear();

	public abstract Iterator iterator();

	public abstract int size();

	public abstract boolean contains(Entity paramEntity);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/focus/FocusSet.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */