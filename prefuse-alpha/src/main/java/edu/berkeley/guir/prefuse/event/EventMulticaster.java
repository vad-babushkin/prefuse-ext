package edu.berkeley.guir.prefuse.event;

import java.lang.reflect.Array;
import java.util.EventListener;

public abstract class EventMulticaster
		implements EventListener {
	protected final EventListener a;
	protected final EventListener b;

	protected static EventListener removeInternal(EventListener paramEventListener1, EventListener paramEventListener2) {
		if ((paramEventListener1 == paramEventListener2) || (paramEventListener1 == null)) {
			return null;
		}
		if ((paramEventListener1 instanceof EventMulticaster)) {
			return ((EventMulticaster) paramEventListener1).remove(paramEventListener2);
		}
		return paramEventListener1;
	}

	protected EventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
		this.a = paramEventListener1;
		this.b = paramEventListener2;
	}

	protected abstract EventListener remove(EventListener paramEventListener);

	private static int getListenerCount(EventListener paramEventListener) {
		if ((paramEventListener instanceof EventMulticaster)) {
			EventMulticaster localEventMulticaster = (EventMulticaster) paramEventListener;
			return getListenerCount(localEventMulticaster.a) + getListenerCount(localEventMulticaster.b);
		}
		return paramEventListener == null ? 0 : 1;
	}

	private static int populateListenerArray(EventListener[] paramArrayOfEventListener, EventListener paramEventListener, int paramInt) {
		if ((paramEventListener instanceof EventMulticaster)) {
			EventMulticaster localEventMulticaster = (EventMulticaster) paramEventListener;
			int i = populateListenerArray(paramArrayOfEventListener, localEventMulticaster.a, paramInt);
			return populateListenerArray(paramArrayOfEventListener, localEventMulticaster.b, i);
		}
		if (paramEventListener != null) {
			paramArrayOfEventListener[paramInt] = paramEventListener;
			return paramInt + 1;
		}
		return paramInt;
	}

	public static EventListener[] getListeners(EventListener paramEventListener, Class paramClass) {
		int i = getListenerCount(paramEventListener);
		EventListener[] arrayOfEventListener = (EventListener[]) Array.newInstance(paramClass, i);
		populateListenerArray(arrayOfEventListener, paramEventListener, 0);
		return arrayOfEventListener;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/EventMulticaster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */