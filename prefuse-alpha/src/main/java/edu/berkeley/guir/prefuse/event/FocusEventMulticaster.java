package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

public class FocusEventMulticaster
		extends EventMulticaster
		implements FocusListener {
	public static FocusListener add(FocusListener paramFocusListener1, FocusListener paramFocusListener2) {
		return (FocusListener) addInternal(paramFocusListener1, paramFocusListener2);
	}

	public static FocusListener remove(FocusListener paramFocusListener1, FocusListener paramFocusListener2) {
		return (FocusListener) removeInternal(paramFocusListener1, paramFocusListener2);
	}

	protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) {
		if (paramEventListener1 == null) {
			return paramEventListener2;
		}
		if (paramEventListener2 == null) {
			return paramEventListener1;
		}
		return new FocusEventMulticaster(paramEventListener1, paramEventListener2);
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

	protected FocusEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
		super(paramEventListener1, paramEventListener2);
	}

	public void focusChanged(FocusEvent paramFocusEvent) {
		((FocusListener) this.a).focusChanged(paramFocusEvent);
		((FocusListener) this.b).focusChanged(paramFocusEvent);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/FocusEventMulticaster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */