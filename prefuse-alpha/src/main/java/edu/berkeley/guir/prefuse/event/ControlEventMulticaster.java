package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.VisualItem;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.EventListener;

public class ControlEventMulticaster
		extends EventMulticaster
		implements ControlListener {
	public static ControlListener add(ControlListener paramControlListener1, ControlListener paramControlListener2) {
		return (ControlListener) addInternal(paramControlListener1, paramControlListener2);
	}

	public static ControlListener remove(ControlListener paramControlListener1, ControlListener paramControlListener2) {
		return (ControlListener) removeInternal(paramControlListener1, paramControlListener2);
	}

	public void itemDragged(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		((ControlListener) this.a).itemDragged(paramVisualItem, paramMouseEvent);
		((ControlListener) this.b).itemDragged(paramVisualItem, paramMouseEvent);
	}

	public void itemMoved(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		((ControlListener) this.a).itemMoved(paramVisualItem, paramMouseEvent);
		((ControlListener) this.b).itemMoved(paramVisualItem, paramMouseEvent);
	}

	public void itemWheelMoved(VisualItem paramVisualItem, MouseWheelEvent paramMouseWheelEvent) {
		((ControlListener) this.a).itemWheelMoved(paramVisualItem, paramMouseWheelEvent);
		((ControlListener) this.b).itemWheelMoved(paramVisualItem, paramMouseWheelEvent);
	}

	public void itemClicked(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		((ControlListener) this.a).itemClicked(paramVisualItem, paramMouseEvent);
		((ControlListener) this.b).itemClicked(paramVisualItem, paramMouseEvent);
	}

	public void itemPressed(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		((ControlListener) this.a).itemPressed(paramVisualItem, paramMouseEvent);
		((ControlListener) this.b).itemPressed(paramVisualItem, paramMouseEvent);
	}

	public void itemReleased(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		((ControlListener) this.a).itemReleased(paramVisualItem, paramMouseEvent);
		((ControlListener) this.b).itemReleased(paramVisualItem, paramMouseEvent);
	}

	public void itemEntered(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		((ControlListener) this.a).itemEntered(paramVisualItem, paramMouseEvent);
		((ControlListener) this.b).itemEntered(paramVisualItem, paramMouseEvent);
	}

	public void itemExited(VisualItem paramVisualItem, MouseEvent paramMouseEvent) {
		((ControlListener) this.a).itemExited(paramVisualItem, paramMouseEvent);
		((ControlListener) this.b).itemExited(paramVisualItem, paramMouseEvent);
	}

	public void itemKeyPressed(VisualItem paramVisualItem, KeyEvent paramKeyEvent) {
		((ControlListener) this.a).itemKeyPressed(paramVisualItem, paramKeyEvent);
		((ControlListener) this.b).itemKeyPressed(paramVisualItem, paramKeyEvent);
	}

	public void itemKeyReleased(VisualItem paramVisualItem, KeyEvent paramKeyEvent) {
		((ControlListener) this.a).itemKeyReleased(paramVisualItem, paramKeyEvent);
		((ControlListener) this.b).itemKeyReleased(paramVisualItem, paramKeyEvent);
	}

	public void itemKeyTyped(VisualItem paramVisualItem, KeyEvent paramKeyEvent) {
		((ControlListener) this.a).itemKeyTyped(paramVisualItem, paramKeyEvent);
		((ControlListener) this.b).itemKeyTyped(paramVisualItem, paramKeyEvent);
	}

	public void mouseEntered(MouseEvent paramMouseEvent) {
		((ControlListener) this.a).mouseEntered(paramMouseEvent);
		((ControlListener) this.b).mouseEntered(paramMouseEvent);
	}

	public void mouseExited(MouseEvent paramMouseEvent) {
		((ControlListener) this.a).mouseExited(paramMouseEvent);
		((ControlListener) this.b).mouseExited(paramMouseEvent);
	}

	public void mousePressed(MouseEvent paramMouseEvent) {
		((ControlListener) this.a).mousePressed(paramMouseEvent);
		((ControlListener) this.b).mousePressed(paramMouseEvent);
	}

	public void mouseReleased(MouseEvent paramMouseEvent) {
		((ControlListener) this.a).mouseReleased(paramMouseEvent);
		((ControlListener) this.b).mouseReleased(paramMouseEvent);
	}

	public void mouseClicked(MouseEvent paramMouseEvent) {
		((ControlListener) this.a).mouseClicked(paramMouseEvent);
		((ControlListener) this.b).mouseClicked(paramMouseEvent);
	}

	public void mouseDragged(MouseEvent paramMouseEvent) {
		((ControlListener) this.a).mouseDragged(paramMouseEvent);
		((ControlListener) this.b).mouseDragged(paramMouseEvent);
	}

	public void mouseMoved(MouseEvent paramMouseEvent) {
		((ControlListener) this.a).mouseMoved(paramMouseEvent);
		((ControlListener) this.b).mouseMoved(paramMouseEvent);
	}

	public void mouseWheelMoved(MouseWheelEvent paramMouseWheelEvent) {
		((ControlListener) this.a).mouseWheelMoved(paramMouseWheelEvent);
		((ControlListener) this.b).mouseWheelMoved(paramMouseWheelEvent);
	}

	public void keyPressed(KeyEvent paramKeyEvent) {
		((ControlListener) this.a).keyPressed(paramKeyEvent);
		((ControlListener) this.b).keyPressed(paramKeyEvent);
	}

	public void keyReleased(KeyEvent paramKeyEvent) {
		((ControlListener) this.a).keyReleased(paramKeyEvent);
		((ControlListener) this.b).keyReleased(paramKeyEvent);
	}

	public void keyTyped(KeyEvent paramKeyEvent) {
		((ControlListener) this.a).keyTyped(paramKeyEvent);
		((ControlListener) this.b).keyTyped(paramKeyEvent);
	}

	protected static EventListener addInternal(EventListener paramEventListener1, EventListener paramEventListener2) {
		if (paramEventListener1 == null) {
			return paramEventListener2;
		}
		if (paramEventListener2 == null) {
			return paramEventListener1;
		}
		return new ControlEventMulticaster(paramEventListener1, paramEventListener2);
	}

	protected EventListener remove(EventListener paramEventListener) {
		if (paramEventListener == this.a) {
			return this.b;
		}
		if (paramEventListener == this.b) {
			return this.a;
		}
		EventListener localEventListener1 = removeInternal(this.a, paramEventListener);
		EventListener localEventListener2 = removeInternal(this.b, paramEventListener);
		if ((localEventListener1 == this.a) && (localEventListener2 == this.b)) {
			return this;
		}
		return addInternal(localEventListener1, localEventListener2);
	}

	protected ControlEventMulticaster(EventListener paramEventListener1, EventListener paramEventListener2) {
		super(paramEventListener1, paramEventListener2);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/ControlEventMulticaster.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */