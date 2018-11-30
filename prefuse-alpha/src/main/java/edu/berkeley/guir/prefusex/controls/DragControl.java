package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class DragControl
		extends ControlAdapter {
	private VisualItem activeItem;
	protected Activity update;
	protected Point2D down = new Point2D.Double();
	protected Point2D tmp = new Point2D.Double();
	protected boolean dragged;
	private boolean wasFixed;
	private boolean fixOnMouseOver;
	protected boolean repaint = true;

	public DragControl() {
	}

	public DragControl(boolean paramBoolean) {
		this.repaint = paramBoolean;
	}

	public DragControl(boolean paramBoolean1, boolean paramBoolean2) {
		this.repaint = paramBoolean1;
		this.fixOnMouseOver = paramBoolean2;
	}

	public DragControl(Activity paramActivity) {
		this.repaint = false;
		this.update = paramActivity;
	}

	public void setFixPositionOnMouseOver(boolean paramBoolean) {
		this.fixOnMouseOver = paramBoolean;
	}

	public void itemEntered(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		Display localDisplay = (Display) paramMouseEvent.getSource();
		localDisplay.setCursor(Cursor.getPredefinedCursor(12));
		this.activeItem = paramVisualItem;
		this.wasFixed = paramVisualItem.isFixed();
		if (this.fixOnMouseOver) {
			paramVisualItem.setFixed(true);
		}
	}

	public void itemExited(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		if (this.activeItem == paramVisualItem) {
			this.activeItem = null;
			paramVisualItem.setFixed(this.wasFixed);
		}
		Display localDisplay = (Display) paramMouseEvent.getSource();
		localDisplay.setCursor(Cursor.getDefaultCursor());
	}

	public void itemPressed(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			return;
		}
		paramVisualItem.setFixed(true);
		this.dragged = false;
		Display localDisplay = (Display) paramMouseEvent.getComponent();
		this.down = localDisplay.getAbsoluteCoordinate(paramMouseEvent.getPoint(), this.down);
	}

	public void itemReleased(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			return;
		}
		if (this.dragged) {
			this.activeItem = null;
			paramVisualItem.setFixed(this.wasFixed);
			this.dragged = false;
		}
	}

	public void itemDragged(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		if (!(paramVisualItem instanceof NodeItem)) {
			return;
		}
		if (!SwingUtilities.isLeftMouseButton(paramMouseEvent)) {
			return;
		}
		this.dragged = true;
		Display localDisplay = (Display) paramMouseEvent.getComponent();
		this.tmp = localDisplay.getAbsoluteCoordinate(paramMouseEvent.getPoint(), this.tmp);
		double d1 = this.tmp.getX() - this.down.getX();
		double d2 = this.tmp.getY() - this.down.getY();
		Point2D localPoint2D = paramVisualItem.getLocation();
		paramVisualItem.updateLocation(localPoint2D.getX() + d1, localPoint2D.getY() + d2);
		paramVisualItem.setLocation(localPoint2D.getX() + d1, localPoint2D.getY() + d2);
		this.down.setLocation(this.tmp);
		if (this.repaint) {
			paramVisualItem.getItemRegistry().repaint();
		}
		if (this.update != null) {
			this.update.runNow();
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/DragControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */