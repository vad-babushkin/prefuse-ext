package edu.berkeley.guir.prefuse.event;

import java.util.EventListener;

public abstract interface FocusListener
		extends EventListener {
	public abstract void focusChanged(FocusEvent paramFocusEvent);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/FocusListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */