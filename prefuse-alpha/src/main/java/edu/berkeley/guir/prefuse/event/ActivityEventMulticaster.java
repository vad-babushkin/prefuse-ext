package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.activity.Activity;

import java.util.EventListener;

public class ActivityEventMulticaster
		extends EventMulticaster
		implements ActivityListener {
	public static ActivityListener add(ActivityListener paramActivityListener1, ActivityListener paramActivityListener2) {
		return (ActivityListener) addInternal(paramActivityListener1, paramActivityListener2);
	}

	public static ActivityListener remove(ActivityListener paramActivityListener1, ActivityListener paramActivityListener2) {
		return (ActivityListener) removeInternal(paramActivityListener1, paramActivityListener2);
	}

	public void activityScheduled(Activity paramActivity) {
		((ActivityListener) this.a).activityScheduled(paramActivity);
		((ActivityListener) this.b).activityScheduled(paramActivity);
	}

	public void activityStarted(Activity paramActivity) {
		((ActivityListener) this.a).activityStarted(paramActivity);
		((ActivityListener) this.b).activityStarted(paramActivity);
	}

	public void activityStepped(Activity paramActivity) {
		((ActivityListener) this.a).activityStepped(paramActivity);
		((ActivityListener) this.b).activityStepped(paramActivity);
	}

	public void activityFinished(Activity paramActivity) {
		((ActivityListener) this.a).activityFinished(paramActivity);
		((ActivityListener) this.b).activityFinished(paramActivity);
	}

	public void activityCancelled(Activity paramActivity) {
		((ActivityListener) this.a).activityCancelled(paramActivity);
		((ActivityListener) this.b).activityCancelled(paramActivity);
	}

	protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) {
		if (paramEventListener1 == null) {
			return paramEventListener2;
		}
		if (paramEventListener2 == null) {
			return paramEventListener1;
		}
		return new ActivityEventMulticaster(paramEventListener1, paramEventListener2);
	}

	protected EventListener remove(EventListener paramEventListener) {
		if (paramEventListener == this.a) {
			return this.b;
		}
		if (paramEventListener == this.b) {
			return this.a;
		}
		EventListener localEventListener1 = removeInternal(this.a, paramEventListener);
		EventListener localEventListener2 = removeInternal(this.b, paramEventListener);
		if ((localEventListener1 == this.a) && (localEventListener2 == this.b)) {
			return this;
		}
		return addInternal(localEventListener1, localEventListener2);
	}

	protected ActivityEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
		super(paramEventListener1, paramEventListener2);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/ActivityEventMulticaster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */