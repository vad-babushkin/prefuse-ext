package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.VisualItem;

import java.util.EventListener;

public abstract interface ItemRegistryListener
		extends EventListener {
	public abstract void registryItemAdded(VisualItem paramVisualItem);

	public abstract void registryItemRemoved(VisualItem paramVisualItem);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/ItemRegistryListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */