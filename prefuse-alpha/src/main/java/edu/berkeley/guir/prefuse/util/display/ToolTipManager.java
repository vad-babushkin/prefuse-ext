package edu.berkeley.guir.prefuse.util.display;

import edu.berkeley.guir.prefuse.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class ToolTipManager
		implements MouseMotionListener {
	private Display m_display;
	private JComponent m_tooltip;
	private ToolTipTimer m_toolTipTimer;
	private boolean m_toolTipsEnabled;
	private long m_toolTipDelay = 2000L;

	public ToolTipManager(Display paramDisplay, JComponent paramJComponent) {
		this.m_display = paramDisplay;
		this.m_tooltip = paramJComponent;
		this.m_tooltip.setVisible(false);
		this.m_toolTipTimer = new ToolTipTimer();
		this.m_toolTipsEnabled = true;
		new Thread(this.m_toolTipTimer).start();
	}

	public ToolTipManager(Display paramDisplay) {
		this.m_display = paramDisplay;
		this.m_tooltip = new DefaultToolTipper();
		this.m_tooltip.setVisible(false);
		this.m_toolTipTimer = new ToolTipTimer();
		this.m_toolTipsEnabled = true;
		new Thread(this.m_toolTipTimer).start();
	}

	public void showToolTip(int paramInt1, int paramInt2) {
		this.m_toolTipTimer.show(paramInt1, paramInt2);
	}

	public void hideToolTip() {
		this.m_toolTipTimer.hide();
	}

	public void setToolTipComponent(JComponent paramJComponent) {
		this.m_display.remove(this.m_tooltip);
		this.m_tooltip = paramJComponent;
		this.m_display.add(this.m_tooltip, 0);
	}

	public JComponent getToolTipComponent() {
		return this.m_tooltip;
	}

	public long getToolTipDelay() {
		return this.m_toolTipDelay;
	}

	public void setToolTipDelay(long paramLong) {
		this.m_toolTipDelay = paramLong;
	}

	public boolean isToolTipsEnabled() {
		return this.m_toolTipsEnabled;
	}

	public void setToolTipsEnabled(boolean paramBoolean) {
		this.m_toolTipsEnabled = paramBoolean;
	}

	public String getToolTipText() {
		if ((this.m_tooltip instanceof DefaultToolTipper)) {
			return ((DefaultToolTipper) this.m_tooltip).getText();
		}
		throw new IllegalStateException();
	}

	public void setToolTipText(String paramString) {
		if ((this.m_tooltip instanceof DefaultToolTipper)) {
			((DefaultToolTipper) this.m_tooltip).setText(paramString);
		} else {
			throw new IllegalStateException();
		}
	}

	public void mouseDragged(MouseEvent paramMouseEvent) {
		moveEvent(paramMouseEvent);
	}

	public void mouseMoved(MouseEvent paramMouseEvent) {
		moveEvent(paramMouseEvent);
	}

	private void moveEvent(MouseEvent paramMouseEvent) {
		if (getToolTipText() != null) {
			showToolTip(paramMouseEvent.getX(), paramMouseEvent.getY());
		} else {
			hideToolTip();
		}
	}

	public class DefaultToolTipper
			extends JComponent {
		private String text = null;

		public DefaultToolTipper() {
			setBackground(new Color(255, 255, 225));
			setForeground(Color.BLACK);
		}

		public Dimension getPreferredSize() {
			if (this.text == null) {
				return new Dimension(0, 0);
			}
			Graphics localGraphics = ToolTipManager.this.m_display.getGraphics();
			FontMetrics localFontMetrics = localGraphics.getFontMetrics();
			String str = getText();
			int i = 8;
			if (str != null) {
				i += localFontMetrics.stringWidth(str);
			}
			int j = localFontMetrics.getHeight();
			return new Dimension(i, j);
		}

		public void paintComponent(Graphics paramGraphics) {
			Rectangle localRectangle = getBounds();
			paramGraphics.setColor(getBackground());
			paramGraphics.fillRect(0, 0, localRectangle.width - 1, localRectangle.height - 1);
			paramGraphics.setColor(getForeground());
			paramGraphics.drawRect(0, 0, localRectangle.width - 1, localRectangle.height - 1);
			FontMetrics localFontMetrics = paramGraphics.getFontMetrics();
			if (this.text != null) {
				paramGraphics.drawString(this.text, 4, localFontMetrics.getAscent());
			}
		}

		public String getText() {
			return this.text;
		}

		public void setText(String paramString) {
			this.text = paramString;
		}
	}

	public class ToolTipTimer
			implements Runnable {
		int DEFAULT_X_OFFSET = 15;
		int DEFAULT_Y_OFFSET = 10;
		boolean visible = false;
		boolean show = false;
		boolean squit = true;
		boolean hquit = true;
		boolean hide = false;
		int x;
		int y;

		public ToolTipTimer() {
		}

		public synchronized void show(int paramInt1, int paramInt2) {
			this.squit = false;
			this.show = true;
			this.hide = false;
			this.x = paramInt1;
			this.y = paramInt2;
			notifyAll();
		}

		public synchronized void hide() {
			this.squit = true;
			this.hide = true;
			this.show = false;
			notifyAll();
		}

		public void run() {
			synchronized (this) {
				do {
					if ((!this.visible) && (this.show)) {
						if (ToolTipManager.this.m_toolTipDelay >= 10L) {
							try {
								wait(ToolTipManager.this.m_toolTipDelay);
							} catch (Exception localException1) {
							}
						}
						if (!this.squit) {
							paint();
							this.visible = true;
							this.show = false;
						}
					} else if ((this.visible) && (this.show)) {
						paint();
						this.show = false;
					} else if ((this.visible) && (this.hide)) {
						try {
							wait(250L);
						} catch (Exception localException2) {
						}
						if (this.hide) {
							unpaint();
							this.visible = false;
							this.hide = false;
						}
					} else if (this.hide) {
						this.hide = false;
					}
				} while ((this.hide) || (this.show));
				try {
					wait();
				} catch (Exception localException3) {
				}
				this.squit = false;
			}
		}

		public void paint() {
			ToolTipManager.this.m_display.repaintImmediate();
			Dimension localDimension = ToolTipManager.this.m_tooltip.getPreferredSize();
			Graphics2D localGraphics2D = (Graphics2D) ToolTipManager.this.m_display.getGraphics();
			int i = this.x + this.DEFAULT_X_OFFSET;
			int j = this.y + this.DEFAULT_Y_OFFSET;
			if ((i + localDimension.getWidth() > ToolTipManager.this.m_display.getWidth()) && (localDimension.getWidth() < ToolTipManager.this.m_display.getWidth())) {
				i = this.x - (int) localDimension.getWidth() - this.DEFAULT_X_OFFSET;
			}
			if ((j + localDimension.getHeight() > ToolTipManager.this.m_display.getHeight()) && (localDimension.getHeight() < ToolTipManager.this.m_display.getHeight())) {
				j = this.y - (int) localDimension.getHeight() - this.DEFAULT_Y_OFFSET;
			}
			SwingUtilities.paintComponent(localGraphics2D, ToolTipManager.this.m_tooltip, ToolTipManager.this.m_display, i, j, localDimension.width, localDimension.height);
		}

		public void unpaint() {
			ToolTipManager.this.m_display.repaintImmediate();
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/display/ToolTipManager.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */