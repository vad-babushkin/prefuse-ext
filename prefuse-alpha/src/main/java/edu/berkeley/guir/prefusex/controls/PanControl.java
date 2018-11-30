package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class PanControl
		extends ControlAdapter {
	private int xDown;
	private int yDown;
	private boolean repaint = true;

	public PanControl() {
		this(true);
	}

	public PanControl(boolean paramBoolean) {
		this.repaint = paramBoolean;
	}

	public void mousePressed(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			paramMouseEvent.getComponent().setCursor(Cursor.getPredefinedCursor(13));
			this.xDown = paramMouseEvent.getX();
			this.yDown = paramMouseEvent.getY();
		}
	}

	public void mouseDragged(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			Display localDisplay = (Display) paramMouseEvent.getComponent();
			int i = paramMouseEvent.getX();
			int j = paramMouseEvent.getY();
			int k = i - this.xDown;
			int m = j - this.yDown;
			localDisplay.pan(k, m);
			this.xDown = i;
			this.yDown = j;
			if (this.repaint) {
				localDisplay.repaint();
			}
		}
	}

	public void mouseReleased(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			paramMouseEvent.getComponent().setCursor(Cursor.getDefaultCursor());
			this.xDown = -1;
			this.yDown = -1;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/PanControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */