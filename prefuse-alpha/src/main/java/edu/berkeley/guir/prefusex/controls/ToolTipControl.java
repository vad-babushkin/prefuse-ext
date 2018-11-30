package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import java.awt.event.MouseEvent;

public class ToolTipControl
		extends ControlAdapter {
	private String label;

	public ToolTipControl() {
		this("label");
	}

	public ToolTipControl(String paramString) {
		this.label = paramString;
	}

	public void itemEntered(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		Display localDisplay = (Display) paramMouseEvent.getSource();
		localDisplay.setToolTipText(paramVisualItem.getAttribute(this.label));
	}

	public void itemExited(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		Display localDisplay = (Display) paramMouseEvent.getSource();
		localDisplay.setToolTipText(null);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/ToolTipControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */