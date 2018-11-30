//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.graph.Entity;
import java.awt.Container;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class PrefuseEventLogger implements ControlListener, ItemRegistryListener, FocusListener, ComponentListener {
	public static final int LOG_INTERFACE = 1;
	public static final int LOG_FOCUS = 2;
	public static final int LOG_REGISTRY = 4;
	public static final int LOG_ALL = 7;
	public static final String ITEM_DRAGGED = "ITEM-DRAGGED";
	public static final String ITEM_MOVED = "ITEM-MOVED";
	public static final String ITEM_WHEEL_MOVED = "ITEM-WHEEL-MOVED";
	public static final String ITEM_CLICKED = "ITEM-CLICKED";
	public static final String ITEM_PRESSED = "ITEM-PRESSED";
	public static final String ITEM_RELEASED = "ITEM-RELEASED";
	public static final String ITEM_ENTERED = "ITEM-ENTERED";
	public static final String ITEM_EXITED = "ITEM-EXITED";
	public static final String ITEM_KEY_PRESSED = "ITEM-KEY-PRESSED";
	public static final String ITEM_KEY_RELEASED = "ITEM-KEY-RELEASED";
	public static final String ITEM_KEY_TYPED = "ITEM-KEY-TYPED";
	public static final String MOUSE_ENTERED = "MOUSE-ENTERED";
	public static final String MOUSE_EXITED = "MOUSE-EXITED";
	public static final String MOUSE_PRESSED = "MOUSE-PRESSED";
	public static final String MOUSE_RELEASED = "MOUSE-RELEASED";
	public static final String MOUSE_CLICKED = "MOUSE-CLICKED";
	public static final String MOUSE_DRAGGED = "MOUSE-DRAGGED";
	public static final String MOUSE_MOVED = "MOUSE-MOVED";
	public static final String MOUSE_WHEEL_MOVED = "MOUSE-WHEEL-MOVED";
	public static final String KEY_PRESSED = "KEY-PRESSED";
	public static final String KEY_RELEASED = "KEY-RELEASED";
	public static final String KEY_TYPED = "KEY-TYPED";
	public static final String FOCUS_CHANGED = "FOCUS-CHANGED";
	public static final String REGISTRY_ITEM_ADDED = "REGISTRY-ITEM-ADDED";
	public static final String REGISTRY_ITEM_REMOVED = "REGISTRY-ITEM-REMOVED";
	public static final String WINDOW_POSITION = "WINDOW-POSITION";
	private ItemRegistry m_registry;
	private Display m_display;
	private String m_label;
	private boolean m_logging;
	private int m_state;
	private PrintStream m_out;

	public PrefuseEventLogger(ItemRegistry var1, Display var2, int var3, String var4) {
		this.m_registry = var1;
		this.m_display = var2;
		this.m_state = var3;
		this.m_label = var4;
		this.m_logging = false;
	}

	public PrefuseEventLogger(ItemRegistry var1, int var2, String var3) {
		this(var1, (Display)null, var2, var3);
	}

	public PrefuseEventLogger(Display var1, String var2) {
		this((ItemRegistry)null, var1, 1, var2);
	}

	public synchronized void start(String var1) throws FileNotFoundException {
		if (this.m_logging) {
			throw new IllegalStateException("Can't start an already running logger!");
		} else {
			this.m_out = new PrintStream(new BufferedOutputStream(new FileOutputStream(var1)));
			this.m_logging = true;
			if (this.m_display != null && (this.m_state & 1) > 0) {
				this.m_display.addControlListener(this);
			}

			if (this.m_registry != null && (this.m_state & 2) > 0) {
				this.m_registry.getDefaultFocusSet().addFocusListener(this);
			}

			if (this.m_registry != null && (this.m_state & 4) > 0) {
				this.m_registry.addItemRegistryListener(this);
			}

			Container var2;
			for(var2 = this.m_display.getParent(); var2 != null && !(var2 instanceof Window); var2 = var2.getParent()) {
				;
			}

			if (var2 != null) {
				var2.addComponentListener(this);
			}

			Point var3 = this.m_display.getLocationOnScreen();
			this.log("WINDOW-POSITION\t(" + var3.x + "," + var3.y + ")");
		}
	}

	public synchronized void stop() {
		if (this.m_logging) {
			if (this.m_display != null && (this.m_state & 1) > 0) {
				this.m_display.removeControlListener(this);
			}

			if (this.m_registry != null && (this.m_state & 2) > 0) {
				this.m_registry.getDefaultFocusSet().removeFocusListener(this);
			}

			if (this.m_registry != null && (this.m_state & 4) > 0) {
				this.m_registry.removeItemRegistryListener(this);
			}

			Container var1;
			for(var1 = this.m_display.getParent(); var1 != null && !(var1 instanceof Window); var1 = var1.getParent()) {
				;
			}

			if (var1 != null) {
				var1.removeComponentListener(this);
			}

			this.m_out.flush();
			this.m_out.close();
			this.m_logging = false;
		}
	}

	public synchronized boolean isRunning() {
		return this.m_logging;
	}

	public void log(String var1) {
		if (!this.m_logging) {
			throw new IllegalStateException("Logger isn't running!");
		} else {
			this.m_out.println(System.currentTimeMillis() + "\t" + var1);
		}
	}

	public void log(long var1, String var3) {
		if (!this.m_logging) {
			throw new IllegalStateException("Logger isn't running!");
		} else {
			this.m_out.println(var1 + "\t" + var3);
		}
	}

	public void logMouseEvent(String var1, MouseEvent var2) {
		var1 = var1 + "\t[id=" + var2.getID() + ",x=" + var2.getX() + ",y=" + var2.getY() + ",button=" + var2.getButton() + ",clickCount=" + var2.getClickCount() + ",modifiers=" + var2.getModifiers() + "]";
		this.log(var2.getWhen(), var1);
	}

	public void logMouseWheelEvent(String var1, MouseWheelEvent var2) {
		var1 = var1 + "\t[id=" + var2.getID() + ",x=" + var2.getX() + ",y=" + var2.getY() + ",button=" + var2.getButton() + ",clickCount=" + var2.getClickCount() + ",modifiers=" + var2.getModifiers() + ",scrollType=" + var2.getScrollType() + ",scrollAmount=" + var2.getScrollAmount() + ",wheelRotation=" + var2.getWheelRotation() + "]";
		this.log(var2.getWhen(), var1);
	}

	public void logKeyEvent(String var1, KeyEvent var2) {
		var1 = var1 + "\t[id=" + var2.getID() + ",keyCode=" + var2.getKeyCode() + ",keyChar=" + var2.getKeyChar() + ",modifiers=" + var2.getModifiers() + ",keyText=" + KeyEvent.getKeyText(var2.getKeyCode()) + "]";
		this.log(var2.getWhen(), var1);
	}

	public void logFocusEvent(FocusEvent var1) {
		Entity[] var2 = null;
		StringBuffer var4 = new StringBuffer("FOCUS-CHANGED");
		var4.append("\t[");
		var2 = var1.getAddedFoci();

		int var3;
		for(var3 = 0; var3 < var2.length; ++var3) {
			if (var3 > 0) {
				var4.append(",");
			}

			var4.append(this.getEntityString(var2[var3]));
		}

		var4.append("]\t[");
		var2 = var1.getRemovedFoci();

		for(var3 = 0; var3 < var2.length; ++var3) {
			if (var3 > 0) {
				var4.append(",");
			}

			var4.append(this.getEntityString(var2[var3]));
		}

		var4.append("]");
		this.log(var1.getWhen(), var4.toString());
	}

	protected String getEntityString(Entity var1) {
		return var1 == null ? "NULL" : var1.getAttribute(this.m_label);
	}

	protected String getItemString(VisualItem var1) {
		return var1 == null ? "NULL" : this.getEntityString(var1.getEntity());
	}

	public void componentHidden(ComponentEvent var1) {
	}

	public void componentMoved(ComponentEvent var1) {
		Point var2 = this.m_display.getLocationOnScreen();
		this.log("WINDOW-POSITION\t(" + var2.x + "," + var2.y + ")");
	}

	public void componentResized(ComponentEvent var1) {
	}

	public void componentShown(ComponentEvent var1) {
	}

	public void itemDragged(VisualItem var1, MouseEvent var2) {
		this.logMouseEvent("ITEM-DRAGGED\t" + this.getItemString(var1), var2);
	}

	public void itemMoved(VisualItem var1, MouseEvent var2) {
		this.logMouseEvent("ITEM-MOVED\t" + this.getItemString(var1), var2);
	}

	public void itemWheelMoved(VisualItem var1, MouseWheelEvent var2) {
		this.logMouseWheelEvent("ITEM-WHEEL-MOVED\t" + this.getItemString(var1), var2);
	}

	public void itemClicked(VisualItem var1, MouseEvent var2) {
		this.logMouseEvent("ITEM-CLICKED\t" + this.getItemString(var1), var2);
	}

	public void itemPressed(VisualItem var1, MouseEvent var2) {
		this.logMouseEvent("ITEM-PRESSED\t" + this.getItemString(var1), var2);
	}

	public void itemReleased(VisualItem var1, MouseEvent var2) {
		this.logMouseEvent("ITEM-RELEASED\t" + this.getItemString(var1), var2);
	}

	public void itemEntered(VisualItem var1, MouseEvent var2) {
		this.logMouseEvent("ITEM-ENTERED\t" + this.getItemString(var1), var2);
	}

	public void itemExited(VisualItem var1, MouseEvent var2) {
		this.logMouseEvent("ITEM-EXITED\t" + this.getItemString(var1), var2);
	}

	public void itemKeyPressed(VisualItem var1, KeyEvent var2) {
		this.logKeyEvent("ITEM-KEY-PRESSED\t" + this.getItemString(var1), var2);
	}

	public void itemKeyReleased(VisualItem var1, KeyEvent var2) {
		this.logKeyEvent("ITEM-KEY-RELEASED\t" + this.getItemString(var1), var2);
	}

	public void itemKeyTyped(VisualItem var1, KeyEvent var2) {
		this.logKeyEvent("ITEM-KEY-TYPED\t" + this.getItemString(var1), var2);
	}

	public void mouseEntered(MouseEvent var1) {
		this.logMouseEvent("MOUSE-ENTERED", var1);
	}

	public void mouseExited(MouseEvent var1) {
		this.logMouseEvent("MOUSE-EXITED", var1);
	}

	public void mousePressed(MouseEvent var1) {
		this.logMouseEvent("MOUSE-PRESSED", var1);
	}

	public void mouseReleased(MouseEvent var1) {
		this.logMouseEvent("MOUSE-RELEASED", var1);
	}

	public void mouseClicked(MouseEvent var1) {
		this.logMouseEvent("MOUSE-CLICKED", var1);
	}

	public void mouseDragged(MouseEvent var1) {
		this.logMouseEvent("MOUSE-DRAGGED", var1);
	}

	public void mouseMoved(MouseEvent var1) {
		this.logMouseEvent("MOUSE-MOVED", var1);
	}

	public void mouseWheelMoved(MouseWheelEvent var1) {
		this.logMouseWheelEvent("ITEM-WHEEL-MOVED", var1);
	}

	public void keyPressed(KeyEvent var1) {
		this.logKeyEvent("KEY-PRESSED", var1);
	}

	public void keyReleased(KeyEvent var1) {
		this.logKeyEvent("KEY-RELEASED", var1);
	}

	public void keyTyped(KeyEvent var1) {
		this.logKeyEvent("KEY-TYPED", var1);
	}

	public void registryItemAdded(VisualItem var1) {
		this.log("REGISTRY-ITEM-ADDED\t" + var1.getItemClass() + "\t" + var1.getAttribute(this.m_label));
	}

	public void registryItemRemoved(VisualItem var1) {
		this.log("REGISTRY-ITEM-REMOVED\t" + var1.getItemClass() + "\t" + var1.getAttribute(this.m_label));
	}

	public void focusChanged(FocusEvent var1) {
		this.logFocusEvent(var1);
	}
}
