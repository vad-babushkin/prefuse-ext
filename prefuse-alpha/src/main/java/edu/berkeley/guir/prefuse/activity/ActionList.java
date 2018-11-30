package edu.berkeley.guir.prefuse.activity;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.Action;

import java.util.ArrayList;
import java.util.Iterator;

public class ActionList
		extends Activity
		implements Action {
	private ItemRegistry m_registry;
	private ArrayList m_actions = new ArrayList();

	public ActionList(ItemRegistry paramItemRegistry) {
		this(paramItemRegistry, 0L);
	}

	public ActionList(ItemRegistry paramItemRegistry, long paramLong) {
		this(paramItemRegistry, paramLong, 20L);
	}

	public ActionList(ItemRegistry paramItemRegistry, long paramLong1, long paramLong2) {
		this(paramItemRegistry, paramLong1, paramLong2, System.currentTimeMillis());
	}

	public ActionList(ItemRegistry paramItemRegistry, long paramLong1, long paramLong2, long paramLong3) {
		super(paramLong1, paramLong2, paramLong3);
		this.m_registry = paramItemRegistry;
	}

	public synchronized int size() {
		return this.m_actions.size();
	}

	public synchronized void add(Action paramAction) {
		this.m_actions.add(paramAction);
	}

	public synchronized void add(int paramInt, Action paramAction) {
		this.m_actions.add(paramInt, paramAction);
	}

	public synchronized Action get(int paramInt) {
		return (Action) this.m_actions.get(paramInt);
	}

	public synchronized boolean remove(Action paramAction) {
		return this.m_actions.remove(paramAction);
	}

	public synchronized Action remove(int paramInt) {
		return (Action) this.m_actions.remove(paramInt);
	}

	protected synchronized void run(long paramLong) {
		run(this.m_registry, getPace(paramLong));
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		synchronized (this.m_registry) {
			Iterator localIterator = this.m_actions.iterator();
			while (localIterator.hasNext()) {
				Action localAction = (Action) localIterator.next();
				if (localAction.isEnabled()) {
					localAction.run(this.m_registry, paramDouble);
				}
			}
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/activity/ActionList.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */