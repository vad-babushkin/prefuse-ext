package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class SubtreeDragControl
		extends ControlAdapter {
	private VisualItem activeItem;
	private Point2D down = new Point2D.Double();
	private Point2D tmp = new Point2D.Double();
	private boolean wasFixed;
	private boolean repaint = true;

	public SubtreeDragControl() {
	}

	public SubtreeDragControl(boolean paramBoolean) {
		this.repaint = paramBoolean;
	}

	public void itemEntered(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		Display localDisplay = (Display) paramMouseEvent.getSource();
		localDisplay.setCursor(Cursor.getPredefinedCursor(12));
	}

	public void itemExited(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		Display localDisplay = (Display) paramMouseEvent.getSource();
		localDisplay.setCursor(Cursor.getDefaultCursor());
	}

	public void itemPressed(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			return;
		}
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		Display localDisplay = (Display) paramMouseEvent.getComponent();
		this.down = localDisplay.getAbsoluteCoordinate(paramMouseEvent.getPoint(), this.down);
		this.activeItem = paramVisualItem;
		this.wasFixed = paramVisualItem.isFixed();
		paramVisualItem.setFixed(true);
	}

	public void itemReleased(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			return;
		}
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		this.activeItem = null;
		paramVisualItem.setFixed(this.wasFixed);
	}

	public void itemDragged(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			return;
		}
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		Display localDisplay = (Display) paramMouseEvent.getComponent();
		this.tmp = localDisplay.getAbsoluteCoordinate(paramMouseEvent.getPoint(), this.tmp);
		double d1 = this.tmp.getX() - this.down.getX();
		double d2 = this.tmp.getY() - this.down.getY();
		updateLocations((NodeItem) paramVisualItem, d1, d2);
		this.down.setLocation(this.tmp);
		if (this.repaint) {
			paramVisualItem.getItemRegistry().repaint();
		}
	}

	private void updateLocations(NodeItem paramNodeItem, double paramDouble1, double paramDouble2) {
		Point2D localPoint2D = paramNodeItem.getLocation();
		paramNodeItem.updateLocation(localPoint2D.getX() + paramDouble1, localPoint2D.getY() + paramDouble2);
		paramNodeItem.setLocation(localPoint2D.getX() + paramDouble1, localPoint2D.getY() + paramDouble2);
		for (int i = 0; i < paramNodeItem.getChildCount(); i++) {
			updateLocations((NodeItem) paramNodeItem.getChild(i), paramDouble1, paramDouble2);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/SubtreeDragControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */