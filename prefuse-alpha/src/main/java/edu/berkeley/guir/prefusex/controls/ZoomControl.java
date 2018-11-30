package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class ZoomControl
		extends ControlAdapter {
	private int yLast;
	private Point2D down = new Point2D.Float();
	private boolean repaint = true;
	private double minScale = 1.0E-4D;
	private double maxScale = 75.0D;

	public ZoomControl() {
		this(true);
	}

	public ZoomControl(boolean paramBoolean) {
		this.repaint = paramBoolean;
	}

	public void mousePressed(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isRightMouseButton(paramMouseEvent)) {
			Display localDisplay = (Display) paramMouseEvent.getComponent();
			localDisplay.setCursor(Cursor.getPredefinedCursor(8));
			localDisplay.getAbsoluteCoordinate(paramMouseEvent.getPoint(), this.down);
			this.yLast = paramMouseEvent.getY();
		}
	}

	public void mouseDragged(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isRightMouseButton(paramMouseEvent)) {
			Display localDisplay = (Display) paramMouseEvent.getComponent();
			double d1 = localDisplay.getScale();
			int i = paramMouseEvent.getX();
			int j = paramMouseEvent.getY();
			int k = j - this.yLast;
			double d2 = 1.0D + k / 100.0D;
			double d3 = d1 * d2;
			if (d3 < this.minScale) {
				d2 = this.minScale / d1;
				localDisplay.setCursor(Cursor.getPredefinedCursor(3));
			} else if (d3 > this.maxScale) {
				d2 = this.maxScale / d1;
				localDisplay.setCursor(Cursor.getPredefinedCursor(3));
			} else {
				localDisplay.setCursor(Cursor.getPredefinedCursor(8));
			}
			localDisplay.zoomAbs(this.down, d2);
			this.yLast = j;
			if (this.repaint) {
				localDisplay.repaint();
			}
		}
	}

	public void mouseReleased(MouseEvent paramMouseEvent) {
		if (SwingUtilities.isRightMouseButton(paramMouseEvent)) {
			paramMouseEvent.getComponent().setCursor(Cursor.getDefaultCursor());
		}
	}

	public double getMaxScale() {
		return this.maxScale;
	}

	public void setMaxScale(double paramDouble) {
		this.maxScale = paramDouble;
	}

	public double getMinScale() {
		return this.minScale;
	}

	public void setMinScale(double paramDouble) {
		this.minScale = paramDouble;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/ZoomControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */