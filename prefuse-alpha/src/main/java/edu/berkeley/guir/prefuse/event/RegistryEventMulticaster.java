package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.VisualItem;

import java.util.EventListener;

public class RegistryEventMulticaster
		extends EventMulticaster
		implements ItemRegistryListener {
	public static ItemRegistryListener add(ItemRegistryListener paramItemRegistryListener1, ItemRegistryListener paramItemRegistryListener2) {
		return (ItemRegistryListener) addInternal(paramItemRegistryListener1, paramItemRegistryListener2);
	}

	public static ItemRegistryListener remove(ItemRegistryListener paramItemRegistryListener1, ItemRegistryListener paramItemRegistryListener2) {
		return (ItemRegistryListener) removeInternal(paramItemRegistryListener1, paramItemRegistryListener2);
	}

	public void registryItemAdded(VisualItem paramVisualItem) {
		((ItemRegistryListener) this.a).registryItemAdded(paramVisualItem);
		((ItemRegistryListener) this.b).registryItemAdded(paramVisualItem);
	}

	public void registryItemRemoved(VisualItem paramVisualItem) {
		((ItemRegistryListener) this.a).registryItemRemoved(paramVisualItem);
		((ItemRegistryListener) this.b).registryItemRemoved(paramVisualItem);
	}

	protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) {
		if (paramEventListener1 == null) {
			return paramEventListener2;
		}
		if (paramEventListener2 == null) {
			return paramEventListener1;
		}
		return new RegistryEventMulticaster(paramEventListener1, paramEventListener2);
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

	protected RegistryEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
		super(paramEventListener1, paramEventListener2);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/RegistryEventMulticaster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */