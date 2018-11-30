package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.activity.Activity;

import java.util.EventListener;

public abstract interface ActivityListener
		extends EventListener {
	public abstract void activityScheduled(Activity paramActivity);

	public abstract void activityStarted(Activity paramActivity);

	public abstract void activityStepped(Activity paramActivity);

	public abstract void activityFinished(Activity paramActivity);

	public abstract void activityCancelled(Activity paramActivity);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/ActivityListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */