package edu.berkeley.guir.prefuse;

import edu.berkeley.guir.prefuse.focus.DefaultFocusSet;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Entity;

import java.util.HashMap;
import java.util.Iterator;

public class FocusManager {
	public static final String DEFAULT_KEY = "default";
	public static final String SELECTION_KEY = "selection";
	public static final String SEARCH_KEY = "search";
	public static final String HOVER_KEY = "hover";
	private HashMap m_focusSets = new HashMap();

	public FocusManager() {
		setDefaultFocusSet(new DefaultFocusSet());
	}

	public FocusSet getFocusSet(Object paramObject) {
		return (FocusSet) this.m_focusSets.get(paramObject);
	}

	public void putFocusSet(Object paramObject, FocusSet paramFocusSet) {
		this.m_focusSets.put(paramObject, paramFocusSet);
	}

	public FocusSet getDefaultFocusSet() {
		return (FocusSet) this.m_focusSets.get("default");
	}

	public void setDefaultFocusSet(FocusSet paramFocusSet) {
		this.m_focusSets.put("default", paramFocusSet);
	}

	public Iterator getFocusSetIterator() {
		return this.m_focusSets.values().iterator();
	}

	public boolean isFocus(Object paramObject, Entity paramEntity) {
		FocusSet localFocusSet = getFocusSet(paramObject);
		return localFocusSet == null ? false : localFocusSet.contains(paramEntity);
	}

	public boolean isFocus(Entity paramEntity) {
		Iterator localIterator = this.m_focusSets.keySet().iterator();
		while (localIterator.hasNext()) {
			FocusSet localFocusSet = (FocusSet) this.m_focusSets.get(localIterator.next());
			if (localFocusSet.contains(paramEntity)) {
				return true;
			}
		}
		return false;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/FocusManager.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */