package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Entity;

import java.util.EventObject;

public class FocusEvent
		extends EventObject {
	public static final Entity[] EMPTY = new Entity[0];
	public static final int FOCUS_ADDED = 0;
	public static final int FOCUS_REMOVED = 1;
	public static final int FOCUS_SET = 2;
	private Entity[] m_added;
	private Entity[] m_removed;
	private long m_when;
	private int m_type;

	public FocusEvent(FocusSet paramFocusSet, int paramInt, Entity[] paramArrayOfEntity1, Entity[] paramArrayOfEntity2) {
		super(paramFocusSet);
		if ((paramInt < 0) || (paramInt > 2)) {
			throw new IllegalArgumentException("Unrecognized event type:" + paramInt);
		}
		this.m_when = System.currentTimeMillis();
		this.m_type = paramInt;
		this.m_added = (paramArrayOfEntity1 == null ? EMPTY : paramArrayOfEntity1);
		this.m_removed = (paramArrayOfEntity2 == null ? EMPTY : paramArrayOfEntity2);
	}

	public long getWhen() {
		return this.m_when;
	}

	public int getEventType() {
		return this.m_type;
	}

	public FocusSet getFocusSet() {
		return (FocusSet) getSource();
	}

	public Entity[] getAddedFoci() {
		return this.m_added;
	}

	public Entity[] getRemovedFoci() {
		return this.m_removed;
	}

	public Entity getFirstAdded() {
		return this.m_added.length > 0 ? this.m_added[0] : null;
	}

	public Entity getFirstRemoved() {
		return this.m_removed.length > 0 ? this.m_removed[0] : null;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/FocusEvent.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */