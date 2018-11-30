package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

public class RotationControl
		extends ControlAdapter {
	private int xLast;
	private int yLast;
	private boolean repaint = true;

	public RotationControl() {
		this(true);
	}

	public RotationControl(boolean paramBoolean) {
		this.repaint = paramBoolean;
	}

	public void mousePressed(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			Display localDisplay = (Display) paramMouseEvent.getComponent();
			localDisplay.setCursor(Cursor.getPredefinedCursor(8));
			this.xLast = paramMouseEvent.getX();
			this.yLast = paramMouseEvent.getY();
		}
	}

	public void mouseDragged(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			Display localDisplay = (Display) paramMouseEvent.getComponent();
			double d1 = localDisplay.getScale();
			int i = paramMouseEvent.getX();
			int j = paramMouseEvent.getY();
			int k = i - this.xLast;
			int m = j - this.yLast;
			double d2 = k / 40.0D;
			AffineTransform localAffineTransform = localDisplay.getTransform();
			localAffineTransform.rotate(d2);
			try {
				localDisplay.setTransform(localAffineTransform);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
			this.yLast = j;
			this.xLast = i;
			if (this.repaint) {
				localDisplay.repaint();
			}
		}
	}

	public void mouseReleased(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			paramMouseEvent.getComponent().setCursor(Cursor.getDefaultCursor());
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/RotationControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */