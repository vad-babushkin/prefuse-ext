package edu.berkeley.guir.prefuse.action.filter;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.AbstractAction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class Filter
		extends AbstractAction {
	private Set m_classes = new HashSet(3);
	private boolean m_gc;

	public Filter(String paramString, boolean paramBoolean) {
		if (paramString != null) {
			this.m_classes.add(paramString);
		}
		this.m_gc = paramBoolean;
	}

	public Filter(String[] paramArrayOfString, boolean paramBoolean) {
		for (int i = 0; i < paramArrayOfString.length; i++) {
			if (paramArrayOfString[i] != null) {
				this.m_classes.add(paramArrayOfString[i]);
			}
		}
		this.m_gc = paramBoolean;
	}

	public boolean isGarbageCollectEnabled() {
		return this.m_gc;
	}

	public void setGarbageCollect(boolean paramBoolean) {
		this.m_gc = paramBoolean;
	}

	public String[] getItemClasses() {
		return (String[]) this.m_classes.toArray(new String[this.m_classes.size()]);
	}

	public void addItemClass(String paramString) {
		this.m_classes.add(paramString);
	}

	public void removeItemClass(String paramString) {
		this.m_classes.remove(paramString);
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		if (this.m_gc) {
			Iterator localIterator = this.m_classes.iterator();
			while (localIterator.hasNext()) {
				String str = (String) localIterator.next();
				paramItemRegistry.garbageCollect(str);
			}
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/filter/Filter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */