//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.activity;

import edu.berkeley.guir.prefuse.event.ActivityAdapter;

import java.util.ArrayList;

public class ActivityManager extends Thread {
	private static ActivityManager s_instance;
	private ArrayList m_activities = new ArrayList();
	private ArrayList m_tmp = new ArrayList();
	private long m_nextTime = 9223372036854775807L;

	private static ActivityManager getInstance() {
		if (s_instance == null || !s_instance.isAlive()) {
			s_instance = new ActivityManager();
		}

		return s_instance;
	}

	private ActivityManager() {
		this.start();
	}

	static void schedule(Activity var0) {
		getInstance()._schedule(var0);
	}

	static void scheduleNow(Activity var0) {
		getInstance()._scheduleNow(var0);
	}

	static void scheduleAt(Activity var0, long var1) {
		getInstance()._scheduleAt(var0, var1);
	}

	static void scheduleAfter(Activity var0, Activity var1) {
		getInstance()._scheduleAfter(var0, var1);
	}

	static void alwaysScheduleAfter(Activity var0, Activity var1) {
		getInstance()._alwaysScheduleAfter(var0, var1);
	}

	static void removeActivity(Activity var0) {
		getInstance()._removeActivity(var0);
	}

	public static int activityCount() {
		return getInstance()._activityCount();
	}

	private synchronized void _schedule(Activity var1) {
		if (!var1.isScheduled()) {
			this.m_activities.add(var1);
			var1.setScheduled(true);
			long var2 = var1.getStartTime();
			if (var2 < this.m_nextTime) {
				this.m_nextTime = var2;
				this.notify();
			}

		}
	}

	private synchronized void _scheduleAt(Activity var1, long var2) {
		if (!var1.isScheduled()) {
			var1.setStartTime(var2);
			schedule(var1);
		}
	}

	private synchronized void _scheduleNow(Activity var1) {
		if (!var1.isScheduled()) {
			var1.setStartTime(System.currentTimeMillis());
			schedule(var1);
		}
	}

	private synchronized void _scheduleAfter(Activity var1, Activity var2) {
		var1.addActivityListener(new ActivityManager.ScheduleAfterActivity(var2, true));
	}

	private synchronized void _alwaysScheduleAfter(Activity var1, Activity var2) {
		var1.addActivityListener(new ActivityManager.ScheduleAfterActivity(var2, false));
	}

	private synchronized boolean _removeActivity(Activity var1) {
		boolean var2 = this.m_activities.remove(var1);
		if (var2) {
			var1.setScheduled(false);
			if (this.m_activities.size() == 0) {
				this.m_nextTime = 9223372036854775807L;
			}
		}

		return var2;
	}

	private synchronized int _activityCount() {
		return this.m_activities.size();
	}

	public void run() {
		while (true) {
			if (activityCount() <= 0) {
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (InterruptedException var13) {
					;
				}
			} else {
				long var1 = System.currentTimeMillis();
				long var3 = -1L;
				synchronized (this) {
					this.m_tmp.addAll(this.m_activities);
				}

				for (int var5 = 0; var5 < this.m_tmp.size(); ++var5) {
					Activity var6 = (Activity) this.m_tmp.get(var5);
					var3 = Math.max(var3, var6.runActivity(var1));
				}

				this.m_tmp.clear();
				if (var3 != -1L) {
					synchronized (this) {
						try {
							this.wait(var3);
						} catch (InterruptedException var9) {
							;
						}
					}
				}
			}
		}
	}

	public class ScheduleAfterActivity extends ActivityAdapter {
		Activity after;
		boolean remove;

		public ScheduleAfterActivity(Activity var2, boolean var3) {
			this.after = var2;
			this.remove = var3;
		}

		public void activityFinished(Activity var1) {
			if (this.remove) {
				var1.removeActivityListener(this);
			}

			ActivityManager.scheduleNow(this.after);
		}

		public void activityCancelled(Activity var1) {
			if (this.remove) {
				var1.removeActivityListener(this);
			}

		}
	}
}
