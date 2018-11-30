package edu.berkeley.guir.prefuse.activity;

import edu.berkeley.guir.prefuse.event.ActivityEventMulticaster;
import edu.berkeley.guir.prefuse.event.ActivityListener;

import java.util.ArrayList;
import java.util.List;

public abstract class Activity {
	public static final long DEFAULT_STEP_TIME = 20L;
	private static final int SCHEDULED = 0;
	private static final int STARTED = 1;
	private static final int STEPPED = 2;
	private static final int FINISHED = 3;
	private static final int CANCELLED = 4;
	private boolean m_enabled = true;
	private Pacer m_pacer;
	private long m_startTime = -1L;
	private long m_duration = -1L;
	private long m_stepTime = -1L;
	private long m_nextTime = -1L;
	private boolean m_isRunning = false;
	private boolean m_isScheduled = false;
	private ActivityListener m_listener;
	private List m_tmp = new ArrayList();

	public Activity(long paramLong) {
		this(paramLong, 20L);
	}

	public Activity(long paramLong1, long paramLong2) {
		this(paramLong1, paramLong2, System.currentTimeMillis());
	}

	public Activity(long paramLong1, long paramLong2, long paramLong3) {
		this.m_startTime = paramLong3;
		this.m_nextTime = paramLong3;
		this.m_duration = paramLong1;
		this.m_stepTime = paramLong2;
	}

	public void run() {
		ActivityManager.schedule(this);
	}

	public void runNow() {
		ActivityManager.scheduleNow(this);
	}

	public void runAt(long paramLong) {
		ActivityManager.scheduleAt(this, paramLong);
	}

	public void runAfter(Activity paramActivity) {
		ActivityManager.scheduleAfter(paramActivity, this);
	}

	public void alwaysRunAfter(Activity paramActivity) {
		ActivityManager.alwaysScheduleAfter(paramActivity, this);
	}

	protected abstract void run(long paramLong);

	long runActivity(long paramLong) {
		if (paramLong < this.m_startTime) {
			return this.m_startTime - paramLong;
		}
		long l = paramLong - this.m_startTime;
		if ((this.m_duration == 0L) || (paramLong > getStopTime())) {
			if (!isRunning()) {
				setRunning(true);
				if (this.m_listener != null) {
					this.m_listener.activityStarted(this);
				}
			}
			if (this.m_enabled) {
				run(l);
				if (this.m_listener != null) {
					this.m_listener.activityStepped(this);
				}
			}
			setRunning(false);
			if (this.m_listener != null) {
				this.m_listener.activityFinished(this);
			}
			ActivityManager.removeActivity(this);
			return -1L;
		}
		if (paramLong >= this.m_nextTime) {
			if (!isRunning()) {
				setRunning(true);
				if (this.m_listener != null) {
					this.m_listener.activityStarted(this);
				}
			}
			if (this.m_enabled) {
				run(l);
				this.m_nextTime = (paramLong + this.m_stepTime);
				if (this.m_listener != null) {
					this.m_listener.activityStepped(this);
				}
			}
			this.m_nextTime = (paramLong + this.m_stepTime);
		}
		return this.m_stepTime;
	}

	public void cancel() {
		if (isScheduled()) {
			if (this.m_listener != null) {
				this.m_listener.activityCancelled(this);
			}
			ActivityManager.removeActivity(this);
		}
		setRunning(false);
	}

	public synchronized boolean isScheduled() {
		return this.m_isScheduled;
	}

	void setScheduled(boolean paramBoolean) {
		synchronized (this) {
			int i = (paramBoolean) && (!this.m_isScheduled) ? 1 : 0;
			this.m_isScheduled = paramBoolean;
		}
		if (this.m_listener != null) {
			this.m_listener.activityScheduled(this);
		}
	}

	protected synchronized void setRunning(boolean paramBoolean) {
		this.m_isRunning = paramBoolean;
	}

	public synchronized boolean isRunning() {
		return this.m_isRunning;
	}

	public void addActivityListener(ActivityListener paramActivityListener) {
		this.m_listener = ActivityEventMulticaster.add(this.m_listener, paramActivityListener);
	}

	public void removeActivityListener(ActivityListener paramActivityListener) {
		this.m_listener = ActivityEventMulticaster.remove(this.m_listener, paramActivityListener);
	}

	public double getPace(long paramLong) {
		long l = getDuration();
		double d = l == 0L ? 0.0D : paramLong / l;
		d = Math.min(1.0D, Math.max(0.0D, d));
		return this.m_pacer != null ? this.m_pacer.pace(d) : d;
	}

	public synchronized Pacer getPacingFunction() {
		return this.m_pacer;
	}

	public synchronized void setPacingFunction(Pacer paramPacer) {
		this.m_pacer = paramPacer;
	}

	public long getStopTime() {
		if (this.m_duration == -1L) {
			return Long.MAX_VALUE;
		}
		return this.m_startTime + this.m_duration;
	}

	public long getNextTime() {
		return this.m_nextTime;
	}

	public long getDuration() {
		return this.m_duration;
	}

	public void setDuration(long paramLong) {
		this.m_duration = paramLong;
	}

	public long getStartTime() {
		return this.m_startTime;
	}

	public void setStartTime(long paramLong) {
		this.m_startTime = paramLong;
	}

	public long getStepTime() {
		return this.m_stepTime;
	}

	public void setStepTime(long paramLong) {
		this.m_stepTime = paramLong;
	}

	public boolean isEnabled() {
		return this.m_enabled;
	}

	public void setEnabled(boolean paramBoolean) {
		this.m_enabled = paramBoolean;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/activity/Activity.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */