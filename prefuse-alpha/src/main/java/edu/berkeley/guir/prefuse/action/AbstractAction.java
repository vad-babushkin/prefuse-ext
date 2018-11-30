package edu.berkeley.guir.prefuse.action;

import edu.berkeley.guir.prefuse.ItemRegistry;

public abstract class AbstractAction
		implements Action {
	protected boolean m_enabled = true;

	public abstract void run(ItemRegistry paramItemRegistry, double paramDouble);

	public boolean isEnabled() {
		return this.m_enabled;
	}

	public void setEnabled(boolean paramBoolean) {
		this.m_enabled = paramBoolean;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/action/AbstractAction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */