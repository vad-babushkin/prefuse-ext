package edu.berkeley.guir.prefuse.activity;

import java.util.HashMap;

public class ActivityMap {
	private HashMap m_map = new HashMap();
	private ActivityMap m_parent;

	public ActivityMap() {
		this(null);
	}

	public ActivityMap(ActivityMap paramActivityMap) {
		this.m_parent = paramActivityMap;
	}

	public void clear() {
		this.m_map.clear();
	}

	public int size() {
		return this.m_map.size();
	}

	public Activity get(Object paramObject) {
		Activity localActivity = (Activity) this.m_map.get(paramObject);
		return (localActivity == null) && (this.m_parent != null) ? this.m_parent.get(paramObject) : localActivity;
	}

	public Activity schedule(Object paramObject) {
		Activity localActivity = get(paramObject);
		if (localActivity != null) {
			ActivityManager.schedule(localActivity);
		}
		return localActivity;
	}

	public Activity scheduleAt(Object paramObject, long paramLong) {
		Activity localActivity = get(paramObject);
		if (localActivity != null) {
			ActivityManager.scheduleAt(localActivity, paramLong);
		}
		return localActivity;
	}

	public Activity scheduleNow(Object paramObject) {
		Activity localActivity = get(paramObject);
		if (localActivity != null) {
			ActivityManager.scheduleNow(localActivity);
		}
		return localActivity;
	}

	public Activity scheduleAfter(Object paramObject1, Object paramObject2) {
		Activity localActivity1 = get(paramObject1);
		Activity localActivity2 = get(paramObject2);
		if ((localActivity1 != null) && (localActivity2 != null)) {
			ActivityManager.scheduleAfter(localActivity1, localActivity2);
		}
		return localActivity2;
	}

	public Activity cancel(Object paramObject) {
		Activity localActivity = get(paramObject);
		if (localActivity != null) {
			localActivity.cancel();
		}
		return localActivity;
	}

	public Activity put(Object paramObject, Activity paramActivity) {
		this.m_map.put(paramObject, paramActivity);
		return paramActivity;
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

	public void setParent(ActivityMap paramActivityMap) {
		this.m_parent = paramActivityMap;
	}

	public ActivityMap getParent() {
		return this.m_parent;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/activity/ActivityMap.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */