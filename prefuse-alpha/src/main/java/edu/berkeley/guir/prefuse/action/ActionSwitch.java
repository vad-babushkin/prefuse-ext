package edu.berkeley.guir.prefuse.action;

import edu.berkeley.guir.prefuse.ItemRegistry;

import java.util.ArrayList;

public class ActionSwitch
		extends AbstractAction {
	private ArrayList actions = new ArrayList();
	private int switchVal = 0;

	public ActionSwitch() {
	}

	public ActionSwitch(Action[] paramArrayOfAction, int paramInt) {
		this();
		for (int i = 0; i < paramArrayOfAction.length; i++) {
			this.actions.add(paramArrayOfAction[i]);
		}
		setSwitchValue(paramInt);
	}

	public void run(ItemRegistry paramItemRegistry, double paramDouble) {
		if (this.actions.size() > 0) {
			get(this.switchVal).run(paramItemRegistry, paramDouble);
		}
	}

	public Action get(int paramInt) {
		return (Action) this.actions.get(paramInt);
	}

	public void add(Action paramAction) {
		this.actions.add(paramAction);
	}

	public void add(int paramInt, Action paramAction) {
		this.actions.add(paramInt, paramAction);
	}

	public void set(int paramInt, Action paramAction) {
		this.actions.set(paramInt, paramAction);
	}

	public Action remove(int paramInt) {
		return (Action) this.actions.remove(paramInt);
	}

	public int size() {
		return this.actions.size();
	}

	public int getSwitchValue() {
		return this.switchVal;
	}

	public void setSwitchValue(int paramInt) {
		if ((paramInt < 0) || (paramInt >= this.actions.size())) {
			throw new IllegalArgumentException("Switch value out of legal range");
		}
		this.switchVal = paramInt;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/ActionSwitch.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */