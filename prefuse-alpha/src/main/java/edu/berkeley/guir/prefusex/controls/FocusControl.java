package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.*;
import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.graph.Entity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class FocusControl
		extends ControlAdapter {
	private Object focusSetKey = "default";
	protected int ccount;
	protected Class[] itemTypes = {NodeItem.class};
	protected Activity activity = null;
	protected Entity curFocus = null;

	public FocusControl() {
		this(1);
	}

	public FocusControl(Activity paramActivity) {
		this(1);
		this.activity = paramActivity;
	}

	public FocusControl(int paramInt) {
		this.ccount = paramInt;
	}

	public FocusControl(int paramInt, Activity paramActivity) {
		this.ccount = paramInt;
		this.activity = paramActivity;
	}

	public FocusControl(int paramInt, Class[] paramArrayOfClass) {
		this.ccount = paramInt;
		setFocusItemTypes(paramArrayOfClass);
	}

	public FocusControl(int paramInt, Object paramObject) {
		this.ccount = paramInt;
		this.focusSetKey = paramObject;
	}

	public FocusControl(int paramInt, Activity paramActivity, Object paramObject) {
		this.ccount = paramInt;
		this.activity = paramActivity;
		this.focusSetKey = paramObject;
	}

	public FocusControl(int paramInt, Object paramObject, Class[] paramArrayOfClass) {
		this.ccount = paramInt;
		this.focusSetKey = paramObject;
		setFocusItemTypes(paramArrayOfClass);
	}

	public void setFocusItemTypes(Class[] paramArrayOfClass) {
		for (int i = 0; i < paramArrayOfClass.length; i++) {
			if (!isVisualItem(paramArrayOfClass[i])) {
				throw new IllegalArgumentException("All types must be of type VisualItem");
			}
		}
		this.itemTypes = ((Class[]) paramArrayOfClass.clone());
	}

	protected boolean isVisualItem(Class paramClass) {
		while ((paramClass != null) && (!VisualItem.class.equals(paramClass))) {
			paramClass = paramClass.getSuperclass();
		}
		return paramClass != null;
	}

	protected boolean isAllowedType(VisualItem paramVisualItem) {
		for (int i = 0; i < this.itemTypes.length; i++) {
			if (this.itemTypes[i].isInstance(paramVisualItem)) {
				return true;
			}
		}
		return false;
	}

	public void itemEntered(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (isAllowedType(paramVisualItem)) {
			Display localDisplay = (Display) paramMouseEvent.getSource();
			localDisplay.setCursor(Cursor.getPredefinedCursor(12));
			if (this.ccount == 0) {
				this.curFocus = paramVisualItem.getEntity();
				ItemRegistry localItemRegistry = paramVisualItem.getItemRegistry();
				FocusManager localFocusManager = localItemRegistry.getFocusManager();
				FocusSet localFocusSet = localFocusManager.getFocusSet(this.focusSetKey);
				localFocusSet.set(paramVisualItem.getEntity());
				localItemRegistry.touch(paramVisualItem.getItemClass());
				runActivity();
			}
		}
	}

	public void itemExited(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (isAllowedType(paramVisualItem)) {
			Display localDisplay = (Display) paramMouseEvent.getSource();
			localDisplay.setCursor(Cursor.getDefaultCursor());
			if (this.ccount == 0) {
				this.curFocus = null;
				ItemRegistry localItemRegistry = paramVisualItem.getItemRegistry();
				FocusManager localFocusManager = localItemRegistry.getFocusManager();
				FocusSet localFocusSet = localFocusManager.getFocusSet(this.focusSetKey);
				localFocusSet.remove(paramVisualItem.getEntity());
				localItemRegistry.touch(paramVisualItem.getItemClass());
				runActivity();
			}
		}
	}

	public void itemClicked(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if ((isAllowedType(paramVisualItem)) && (this.ccount > 0) && (SwingUtilities.isLeftMouseButton(paramMouseEvent)) && (paramMouseEvent.getClickCount() == this.ccount)) {
			Entity localEntity = paramVisualItem.getEntity();
			if (localEntity != this.curFocus) {
				this.curFocus = localEntity;
				ItemRegistry localItemRegistry = paramVisualItem.getItemRegistry();
				FocusManager localFocusManager = localItemRegistry.getFocusManager();
				FocusSet localFocusSet = localFocusManager.getFocusSet(this.focusSetKey);
				localFocusSet.set(paramVisualItem.getEntity());
				localItemRegistry.touch(paramVisualItem.getItemClass());
				runActivity();
			}
		}
	}

	private void runActivity() {
		if (this.activity != null) {
			this.activity.runNow();
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/FocusControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */