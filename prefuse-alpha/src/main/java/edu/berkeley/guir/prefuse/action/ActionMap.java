package edu.berkeley.guir.prefuse.action;

import java.util.HashMap;

public class ActionMap {
	private HashMap m_map = new HashMap();
	private ActionMap m_parent;

	public ActionMap() {
		this(null);
	}

	public ActionMap(ActionMap paramActionMap) {
		this.m_parent = paramActionMap;
	}

	public void clear() {
		this.m_map.clear();
	}

	public int size() {
		return this.m_map.size();
	}

	public Action get(Object paramObject) {
		Action localAction = (Action) this.m_map.get(paramObject);
		return (localAction == null) && (this.m_parent != null) ? this.m_parent.get(paramObject) : localAction;
	}

	public Action put(Object paramObject, Action paramAction) {
		this.m_map.put(paramObject, paramAction);
		return paramAction;
	}

	public void remove(Object paramObject) {
		this.m_map.remove(paramObject);
	}

	public Object[] keys() {
		return this.m_map.keySet().toArray();
	}

	public Object[] allKeys() {
		Object[] arrayOfObject1 = this.m_map.keySet().toArray();
		if (this.m_parent != null) {
			Object[] arrayOfObject2 = this.m_parent.allKeys();
			if ((arrayOfObject2 != null) && (arrayOfObject2.length > 0)) {
				Object[] arrayOfObject3 = new Object[arrayOfObject1.length + arrayOfObject2.length];
				System.arraycopy(arrayOfObject1, 0, arrayOfObject3, 0, arrayOfObject1.length);
				System.arraycopy(arrayOfObject2, 0, arrayOfObject3, arrayOfObject1.length, arrayOfObject2.length);
				return arrayOfObject3;
			}
		}
		return arrayOfObject1;
	}

	public void setParent(ActionMap paramActionMap) {
		this.m_parent = paramActionMap;
	}

	public ActionMap getParent() {
		return this.m_parent;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/ActionMap.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */