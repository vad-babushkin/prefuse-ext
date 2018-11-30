package edu.berkeley.guir.prefuse.action;

import edu.berkeley.guir.prefuse.ItemRegistry;

public abstract interface Action {
	public abstract void run(ItemRegistry paramItemRegistry, double paramDouble);

	public abstract boolean isEnabled();

	public abstract void setEnabled(boolean paramBoolean);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/Action.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */