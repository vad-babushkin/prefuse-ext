package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.focus.DefaultFocusSet;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Entity;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class MultiSelectFocusControl
		extends ControlAdapter {
	private static final Object DEFAULT_FOCUS_KEY = "selection";
	private final ItemRegistry registry;
	private final Object focusKey;

	public MultiSelectFocusControl(ItemRegistry paramItemRegistry) {
		this(paramItemRegistry, DEFAULT_FOCUS_KEY);
	}

	public MultiSelectFocusControl(ItemRegistry paramItemRegistry, Object paramObject) {
		this.registry = paramItemRegistry;
		this.focusKey = paramObject;
		paramItemRegistry.getFocusManager().putFocusSet(paramObject, new DefaultFocusSet());
	}

	public void itemClicked(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (((paramVisualItem instanceof NodeItem)) && (SwingUtilities.isLeftMouseButton(paramMouseEvent))) {
			FocusManager localFocusManager = this.registry.getFocusManager();
			FocusSet localFocusSet = localFocusManager.getFocusSet(this.focusKey);
			Entity localEntity = paramVisualItem.getEntity();
			if (paramMouseEvent.isShiftDown()) {
				if (localFocusSet.contains(localEntity)) {
					localFocusSet.remove(localEntity);
				} else {
					localFocusSet.add(localEntity);
				}
			} else if (!localFocusManager.isFocus(this.focusKey, localEntity)) {
				localFocusSet.set(localEntity);
			}
			this.registry.touch(paramVisualItem.getItemClass());
		}
	}

	public void mouseClicked(MouseEvent paramMouseEvent) {
		this.registry.getFocusManager().getFocusSet(this.focusKey).clear();
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/MultiSelectFocusControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */