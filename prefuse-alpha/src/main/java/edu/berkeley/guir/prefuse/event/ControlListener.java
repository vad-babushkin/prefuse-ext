package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.VisualItem;

import java.awt.event.*;
import java.util.EventListener;

public abstract interface ControlListener
		extends EventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	public abstract void itemDragged(VisualItem paramVisualItem, MouseEvent paramMouseEvent);

	public abstract void itemMoved(VisualItem paramVisualItem, MouseEvent paramMouseEvent);

	public abstract void itemWheelMoved(VisualItem paramVisualItem, MouseWheelEvent paramMouseWheelEvent);

	public abstract void itemClicked(VisualItem paramVisualItem, MouseEvent paramMouseEvent);

	public abstract void itemPressed(VisualItem paramVisualItem, MouseEvent paramMouseEvent);

	public abstract void itemReleased(VisualItem paramVisualItem, MouseEvent paramMouseEvent);

	public abstract void itemEntered(VisualItem paramVisualItem, MouseEvent paramMouseEvent);

	public abstract void itemExited(VisualItem paramVisualItem, MouseEvent paramMouseEvent);

	public abstract void itemKeyPressed(VisualItem paramVisualItem, KeyEvent paramKeyEvent);

	public abstract void itemKeyReleased(VisualItem paramVisualItem, KeyEvent paramKeyEvent);

	public abstract void itemKeyTyped(VisualItem paramVisualItem, KeyEvent paramKeyEvent);

	public abstract void mouseEntered(MouseEvent paramMouseEvent);

	public abstract void mouseExited(MouseEvent paramMouseEvent);

	public abstract void mousePressed(MouseEvent paramMouseEvent);

	public abstract void mouseReleased(MouseEvent paramMouseEvent);

	public abstract void mouseClicked(MouseEvent paramMouseEvent);

	public abstract void mouseDragged(MouseEvent paramMouseEvent);

	public abstract void mouseMoved(MouseEvent paramMouseEvent);

	public abstract void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent);

	public abstract void keyPressed(KeyEvent paramKeyEvent);

	public abstract void keyReleased(KeyEvent paramKeyEvent);

	public abstract void keyTyped(KeyEvent paramKeyEvent);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/ControlListener.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */