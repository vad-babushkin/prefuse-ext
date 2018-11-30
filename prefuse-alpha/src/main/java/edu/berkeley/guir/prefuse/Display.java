//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse;

import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.event.ControlEventMulticaster;
import edu.berkeley.guir.prefuse.event.ControlListener;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.util.ColorLib;
import edu.berkeley.guir.prefuse.util.FontLib;
import edu.berkeley.guir.prefuse.util.display.Clip;
import edu.berkeley.guir.prefuse.util.display.ExportDisplayAction;
import edu.berkeley.guir.prefuse.util.display.ToolTipManager;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

public class Display extends JComponent {
	protected ItemRegistry m_registry;
	protected ControlListener m_listener;
	protected BufferedImage m_offscreen;
	protected Clip m_clip;
	protected boolean m_showDebug;
	protected boolean m_repaint;
	protected boolean m_highQuality;
	protected AffineTransform m_transform;
	protected AffineTransform m_itransform;
	protected Display.TransformActivity m_transact;
	protected Point2D m_tmpPoint;
	protected double frameRate;
	protected int nframes;
	private int sampleInterval;
	private long mark;
	private JTextComponent m_editor;
	private boolean m_editing;
	private VisualItem m_editItem;
	private String m_editAttribute;
	private ToolTipManager m_ttipManager;

	public Display() {
		this((ItemRegistry)null);
	}

	public Display(ItemRegistry var1) {
		this.m_clip = new Clip();
		this.m_showDebug = false;
		this.m_repaint = false;
		this.m_highQuality = false;
		this.m_transform = new AffineTransform();
		this.m_itransform = new AffineTransform();
		this.m_transact = new Display.TransformActivity();
		this.m_tmpPoint = new Double();
		this.nframes = 0;
		this.sampleInterval = 10;
		this.mark = -1L;
		this.setDoubleBuffered(false);
		this.setBackground(Color.WHITE);
		this.m_editing = false;
		this.m_editor = new JTextField();
		this.m_editor.setBorder((Border)null);
		this.m_editor.setVisible(false);
		this.add(this.m_editor);
		Display.InputEventCapturer var2 = new Display.InputEventCapturer();
		this.addMouseListener(var2);
		this.addMouseMotionListener(var2);
		this.addMouseWheelListener(var2);
		this.addKeyListener(var2);
		this.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent var1) {
				Display.this.m_showDebug = !Display.this.m_showDebug;
			}
		}, "debug info", KeyStroke.getKeyStroke("ctrl D"), 0);

		try {
			this.registerKeyboardAction(new ExportDisplayAction(this), "export display", KeyStroke.getKeyStroke("ctrl E"), 0);
		} catch (SecurityException var4) {
			;
		}

		this.setItemRegistry(var1);
		this.setSize(400, 400);
	}

	public void setDebug(boolean var1) {
		this.m_showDebug = var1;
	}

	public boolean getDebug() {
		return this.m_showDebug;
	}

	public void setUseCustomTooltips(boolean var1) {
		String var2;
		if (var1 && this.m_ttipManager == null) {
			this.m_ttipManager = new ToolTipManager(this);
			var2 = super.getToolTipText();
			super.setToolTipText((String)null);
			this.m_ttipManager.setToolTipText(var2);
			this.addMouseMotionListener(this.m_ttipManager);
		} else if (!var1 && this.m_ttipManager != null) {
			this.removeMouseMotionListener(this.m_ttipManager);
			var2 = this.m_ttipManager.getToolTipText();
			this.m_ttipManager.setToolTipText((String)null);
			super.setToolTipText(var2);
			this.m_ttipManager = null;
		}

	}

	public ToolTipManager getToolTipManager() {
		return this.m_ttipManager;
	}

	public void setToolTipText(String var1) {
		if (this.m_ttipManager != null) {
			this.m_ttipManager.setToolTipText(var1);
		} else {
			super.setToolTipText(var1);
		}

	}

	public void setSize(int var1, int var2) {
		this.m_offscreen = null;
		this.setPreferredSize(new Dimension(var1, var2));
		super.setSize(var1, var2);
	}

	public void setSize(Dimension var1) {
		this.m_offscreen = null;
		this.setPreferredSize(var1);
		super.setSize(var1);
	}

	public void reshape(int var1, int var2, int var3, int var4) {
		this.m_offscreen = null;
		super.reshape(var1, var2, var3, var4);
	}

	public void setFont(Font var1) {
		super.setFont(var1);
		this.m_editor.setFont(var1);
	}

	public void setHighQuality(boolean var1) {
		this.m_highQuality = var1;
	}

	public boolean isHighQuality() {
		return this.m_highQuality;
	}

	public ItemRegistry getRegistry() {
		return this.m_registry;
	}

	public void setItemRegistry(ItemRegistry var1) {
		if (this.m_registry != var1) {
			if (this.m_registry != null) {
				this.m_registry.removeDisplay(this);
			}

			this.m_registry = var1;
			if (var1 != null) {
				this.m_registry.addDisplay(this);
			}

		}
	}

	public void setTransform(AffineTransform var1) throws NoninvertibleTransformException {
		this.m_transform = var1;
		this.m_itransform = this.m_transform.createInverse();
	}

	public AffineTransform getTransform() {
		return this.m_transform;
	}

	public AffineTransform getInverseTransform() {
		return this.m_itransform;
	}

	public Point2D getAbsoluteCoordinate(Point2D var1, Point2D var2) {
		return this.m_itransform.transform(var1, var2);
	}

	public double getScale() {
		return this.m_transform.getScaleX();
	}

	public double getDisplayX() {
		return -this.m_transform.getTranslateX();
	}

	public double getDisplayY() {
		return -this.m_transform.getTranslateY();
	}

	public void pan(double var1, double var3) {
		double var5 = var1 / this.m_transform.getScaleX();
		double var7 = var3 / this.m_transform.getScaleY();
		this.panAbs(var5, var7);
	}

	public void panAbs(double var1, double var3) {
		this.m_transform.translate(var1, var3);

		try {
			this.m_itransform = this.m_transform.createInverse();
		} catch (Exception var6) {
			;
		}

	}

	public void panTo(Point2D var1) {
		this.m_itransform.transform(var1, this.m_tmpPoint);
		this.panToAbs(this.m_tmpPoint);
	}

	public void panToAbs(Point2D var1) {
		double var2 = var1.getX();
		var2 = java.lang.Double.isNaN(var2) ? 0.0D : var2;
		double var4 = var1.getY();
		var4 = java.lang.Double.isNaN(var4) ? 0.0D : var4;
		double var6 = (double)this.getWidth() / (2.0D * this.m_transform.getScaleX());
		double var8 = (double)this.getHeight() / (2.0D * this.m_transform.getScaleY());
		double var10 = var6 - var2 - this.m_transform.getTranslateX();
		double var12 = var8 - var4 - this.m_transform.getTranslateY();
		this.m_transform.translate(var10, var12);

		try {
			this.m_itransform = this.m_transform.createInverse();
		} catch (Exception var15) {
			;
		}

	}

	public void zoom(Point2D var1, double var2) {
		this.m_itransform.transform(var1, this.m_tmpPoint);
		this.zoomAbs(this.m_tmpPoint, var2);
	}

	public void zoomAbs(Point2D var1, double var2) {
		double var4 = var1.getX();
		double var6 = var1.getY();
		this.m_transform.translate(var4, var6);
		this.m_transform.scale(var2, var2);
		this.m_transform.translate(-var4, -var6);

		try {
			this.m_itransform = this.m_transform.createInverse();
		} catch (Exception var9) {
			;
		}

	}

	public void animatePan(double var1, double var3, long var5) {
		double var7 = var1 / this.m_transform.getScaleX();
		double var9 = var3 / this.m_transform.getScaleY();
		this.animatePanAbs(var7, var9, var5);
	}

	public void animatePanAbs(double var1, double var3, long var5) {
		this.m_transact.pan(var1, var3, var5);
	}

	public void animatePanTo(Point2D var1, long var2) {
		Double var4 = new Double();
		this.m_itransform.transform(var1, var4);
		this.animatePanToAbs(var4, var2);
	}

	public void animatePanToAbs(Point2D var1, long var2) {
		this.m_tmpPoint.setLocation(0.0D, 0.0D);
		this.m_itransform.transform(this.m_tmpPoint, this.m_tmpPoint);
		double var4 = var1.getX();
		var4 = java.lang.Double.isNaN(var4) ? 0.0D : var4;
		double var6 = var1.getY();
		var6 = java.lang.Double.isNaN(var6) ? 0.0D : var6;
		double var8 = (double)this.getWidth() / (2.0D * this.m_transform.getScaleX());
		double var10 = (double)this.getHeight() / (2.0D * this.m_transform.getScaleY());
		double var12 = var8 - var4 + this.m_tmpPoint.getX();
		double var14 = var10 - var6 + this.m_tmpPoint.getY();
		this.animatePanAbs(var12, var14, var2);
	}

	public void animateZoom(Point2D var1, double var2, long var4) {
		Double var6 = new Double();
		this.m_itransform.transform(var1, var6);
		this.animateZoomAbs(var6, var2, var4);
	}

	public void animateZoomAbs(Point2D var1, double var2, long var4) {
		this.m_transact.zoom(var1, var2, var4);
	}

	public BufferedImage getOffscreenBuffer() {
		return this.m_offscreen;
	}

	protected BufferedImage getNewOffscreenBuffer() {
		return (BufferedImage)this.createImage(this.getSize().width, this.getSize().height);
	}

	public boolean saveImage(OutputStream var1, String var2, double var3) {
		try {
			Dimension var5 = new Dimension((int)(var3 * (double)this.getWidth()), (int)(var3 * (double)this.getHeight()));
			BufferedImage var6 = (BufferedImage)this.createImage(var5.width, var5.height);
			Graphics2D var7 = (Graphics2D)var6.getGraphics();
			Double var8 = new Double(0.0D, 0.0D);
			this.zoom(var8, var3);
			this.paintDisplay(var7, var5);
			this.zoom(var8, 1.0D / var3);
			ImageIO.write(var6, var2, var1);
			return true;
		} catch (Exception var9) {
			var9.printStackTrace();
			return false;
		}
	}

	public void update(Graphics var1) {
		this.paint(var1);
	}

	public void repaint() {
		if (!this.m_repaint) {
			this.m_repaint = true;
			super.repaint();
		}

	}

	protected void paintBufferToScreen(Graphics var1) {
		byte var2 = 0;
		byte var3 = 0;
		BufferedImage var4 = this.m_offscreen;
		var1.drawImage(var4, var2, var3, (ImageObserver)null);
	}

	public void repaintImmediate() {
		Graphics var1 = this.getGraphics();
		if (var1 != null && this.m_offscreen != null) {
			this.paintBufferToScreen(var1);
		}

	}

	protected void prepareGraphics(Graphics2D var1) {
		if (this.m_transform != null) {
			var1.setTransform(this.m_transform);
		}

		this.setRenderingHints(var1);
	}

	protected void setRenderingHints(Graphics2D var1) {
		if (this.m_highQuality) {
			var1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			var1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		var1.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		var1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	}

	protected String getDebugString() {
		float var1 = (float)Math.round(this.frameRate * 100.0D) / 100.0F;
		Runtime var2 = Runtime.getRuntime();
		long var3 = var2.totalMemory() / 1000000L;
		long var5 = var2.maxMemory() / 1000000L;
		StringBuffer var7 = new StringBuffer();
		var7.append("frame rate: ").append(var1).append("fps - ");
		var7.append(this.m_registry.size()).append(" items (");
		var7.append(this.m_registry.size("node"));
		var7.append(" nodes, ");
		var7.append(this.m_registry.size("edge"));
		var7.append(" edges) fonts(").append(FontLib.getCacheMissCount());
		var7.append(") colors(");
		var7.append(ColorLib.getCacheMissCount()).append(')');
		var7.append(" mem(");
		var7.append(var3).append("M / ");
		var7.append(var5).append("M)");
		return var7.toString();
	}

	protected void prePaint(Graphics2D var1) {
	}

	protected void postPaint(Graphics2D var1) {
	}

	public void paintComponent(Graphics var1) {
		if (this.m_offscreen == null) {
			this.m_offscreen = this.getNewOffscreenBuffer();
		}

		Graphics2D var2 = (Graphics2D)this.m_offscreen.getGraphics();
		this.paintDisplay(var2, this.getSize());
		this.paintBufferToScreen(var1);
		var2.dispose();
		this.m_repaint = false;
		++this.nframes;
		if (this.mark < 0L) {
			this.mark = System.currentTimeMillis();
			this.nframes = 0;
		} else if (this.nframes == this.sampleInterval) {
			long var3 = System.currentTimeMillis();
			this.frameRate = 1000.0D * (double)this.nframes / (double)(var3 - this.mark);
			this.mark = var3;
			this.nframes = 0;
		}

	}

	public void paintDisplay(Graphics2D var1, Dimension var2) {
		var1.setColor(this.getBackground());
		var1.fillRect(0, 0, var2.width, var2.height);
		if (this.m_showDebug) {
			var1.setFont(this.getFont());
			var1.setColor(this.getForeground());
			var1.drawString(this.getDebugString(), 5, 15);
		}

		this.prepareGraphics(var1);
		this.prePaint(var1);
		var1.setColor(Color.BLACK);
		ItemRegistry var3 = this.m_registry;
		synchronized(this.m_registry) {
			this.m_clip.setClip(0.0D, 0.0D, (double)var2.width, (double)var2.height);
			this.m_clip.transform(this.m_itransform);
			Iterator var4 = this.m_registry.getItems();

			while(true) {
				if (!var4.hasNext()) {
					break;
				}

				VisualItem var5 = (VisualItem)var4.next();
				Renderer var6 = var5.getRenderer();
				Rectangle2D var7 = var6.getBoundsRef(var5);
				if (this.m_clip.intersects(var7)) {
					var6.render(var1, var5);
				}
			}
		}

		this.postPaint(var1);
	}

	public void clearRegion(Rectangle var1) {
		Graphics2D var2 = (Graphics2D)this.m_offscreen.getGraphics();
		if (var2 != null) {
			var2.setColor(this.getBackground());
			var2.fillRect(var1.x, var1.y, var1.width, var1.height);
		}

	}

	public void drawItem(VisualItem var1) {
		Graphics2D var2 = (Graphics2D)this.m_offscreen.getGraphics();
		if (var2 != null) {
			this.prepareGraphics(var2);
			var1.getRenderer().render(var2, var1);
		}

	}

	public void addControlListener(ControlListener var1) {
		this.m_listener = ControlEventMulticaster.add(this.m_listener, var1);
	}

	public void removeControlListener(ControlListener var1) {
		this.m_listener = ControlEventMulticaster.remove(this.m_listener, var1);
	}

	public VisualItem findItem(Point var1) {
		Object var2 = this.m_itransform == null ? var1 : this.m_itransform.transform(var1, this.m_tmpPoint);
		ItemRegistry var3 = this.m_registry;
		synchronized(this.m_registry) {
			Iterator var4 = this.m_registry.getItemsReversed();

			VisualItem var5;
			Renderer var6;
			do {
				if (!var4.hasNext()) {
					return null;
				}

				var5 = (VisualItem)var4.next();
				var6 = var5.getRenderer();
			} while(var6 == null || !var6.locatePoint((Point2D)var2, var5));

			return var5;
		}
	}

	public JTextComponent getTextEditor() {
		return this.m_editor;
	}

	public void setTextEditor(JTextComponent var1) {
		this.remove(this.m_editor);
		this.m_editor = var1;
		this.add(this.m_editor, 1);
	}

	public void editText(VisualItem var1, String var2) {
		if (this.m_editing) {
			this.stopEditing();
		}

		Rectangle2D var3 = var1.getBounds();
		Rectangle var4 = this.m_transform.createTransformedShape(var3).getBounds();
		if (this.m_editor instanceof JTextArea) {
			var4.y -= 2;
			var4.width += 22;
			var4.height += 2;
		} else {
			var4.x += 3;
			++var4.y;
			var4.width -= 5;
			var4.height -= 2;
		}

		Font var5 = this.getFont();
		int var6 = (int)Math.round((double)var5.getSize() * this.m_transform.getScaleX());
		Font var7 = new Font(var5.getFontName(), var5.getStyle(), var6);
		this.m_editor.setFont(var7);
		this.editText(var1, var2, var4);
	}

	public void editText(VisualItem var1, String var2, Rectangle var3) {
		if (this.m_editing) {
			this.stopEditing();
		}

		String var4 = var1.getAttribute(var2);
		this.m_editItem = var1;
		this.m_editAttribute = var2;
		Paint var5 = var1.getColor();
		Paint var6 = var1.getFillColor();
		if (var5 instanceof Color) {
			this.m_editor.setForeground((Color)var5);
		}

		if (var6 instanceof Color) {
			this.m_editor.setBackground((Color)var6);
		}

		this.editText(var4, var3);
	}

	public void editText(String var1, Rectangle var2) {
		if (this.m_editing) {
			this.stopEditing();
		}

		this.m_editing = true;
		this.m_editor.setBounds(var2.x, var2.y, var2.width, var2.height);
		this.m_editor.setText(var1);
		this.m_editor.setVisible(true);
		this.m_editor.setCaretPosition(var1.length());
		this.m_editor.requestFocus();
	}

	public void stopEditing() {
		this.m_editor.setVisible(false);
		if (this.m_editItem != null) {
			String var1 = this.m_editor.getText();
			this.m_editItem.setAttribute(this.m_editAttribute, var1);
			this.m_editItem = null;
			this.m_editAttribute = null;
			this.m_editor.setBackground((Color)null);
			this.m_editor.setForeground((Color)null);
		}

		this.m_editing = false;
	}

	public class InputEventCapturer implements MouseMotionListener, MouseWheelListener, MouseListener, KeyListener {
		private VisualItem activeVI = null;
		private boolean mouseDown = false;
		private boolean itemDrag = false;

		public InputEventCapturer() {
		}

		public void mouseDragged(MouseEvent var1) {
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemDragged(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.mouseDragged(var1);
			}

		}

		public void mouseMoved(MouseEvent var1) {
			boolean var2 = false;
			VisualItem var3 = Display.this.findItem(var1.getPoint());
			if (Display.this.m_listener != null && this.activeVI != null && this.activeVI != var3) {
				Display.this.m_listener.itemExited(this.activeVI, var1);
				var2 = true;
			}

			if (Display.this.m_listener != null && var3 != null && var3 != this.activeVI) {
				Display.this.m_listener.itemEntered(var3, var1);
				var2 = true;
			}

			this.activeVI = var3;
			if (!var2) {
				if (Display.this.m_listener != null && var3 != null && var3 == this.activeVI) {
					Display.this.m_listener.itemMoved(var3, var1);
				}

				if (Display.this.m_listener != null && var3 == null) {
					Display.this.m_listener.mouseMoved(var1);
				}

			}
		}

		public void mouseWheelMoved(MouseWheelEvent var1) {
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemWheelMoved(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.mouseWheelMoved(var1);
			}

		}

		public void mouseClicked(MouseEvent var1) {
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemClicked(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.mouseClicked(var1);
			}

		}

		public void mousePressed(MouseEvent var1) {
			this.mouseDown = true;
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemPressed(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.mousePressed(var1);
			}

		}

		public void mouseReleased(MouseEvent var1) {
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemReleased(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.mouseReleased(var1);
			}

			if (Display.this.m_listener != null && this.activeVI != null && this.mouseDown && this.isOffComponent(var1)) {
				Display.this.m_listener.itemExited(this.activeVI, var1);
				this.activeVI = null;
			}

			this.mouseDown = false;
		}

		public void mouseEntered(MouseEvent var1) {
			if (Display.this.m_listener != null) {
				Display.this.m_listener.mouseEntered(var1);
			}

		}

		public void mouseExited(MouseEvent var1) {
			if (Display.this.m_listener != null && !this.mouseDown && this.activeVI != null) {
				Display.this.m_listener.itemExited(this.activeVI, var1);
				this.activeVI = null;
			}

			if (Display.this.m_listener != null) {
				Display.this.m_listener.mouseExited(var1);
			}

		}

		public void keyPressed(KeyEvent var1) {
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemKeyPressed(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.keyPressed(var1);
			}

		}

		public void keyReleased(KeyEvent var1) {
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemKeyReleased(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.keyReleased(var1);
			}

		}

		public void keyTyped(KeyEvent var1) {
			if (Display.this.m_listener != null && this.activeVI != null) {
				Display.this.m_listener.itemKeyTyped(this.activeVI, var1);
			} else if (Display.this.m_listener != null) {
				Display.this.m_listener.keyTyped(var1);
			}

		}

		private boolean isOffComponent(MouseEvent var1) {
			int var2 = var1.getX();
			int var3 = var1.getY();
			return var2 < 0 || var2 > Display.this.getWidth() || var3 < 0 || var3 > Display.this.getHeight();
		}
	}

	private class TransformActivity extends Activity {
		private double[] src = new double[6];
		private double[] dst = new double[6];
		private AffineTransform m_at = new AffineTransform();

		public TransformActivity() {
			super(2000L, 20L, 0L);
			this.setPacingFunction(new SlowInSlowOutPacer());
		}

		private AffineTransform getTransform() {
			if (this.isScheduled()) {
				this.m_at.setTransform(this.dst[0], this.dst[1], this.dst[2], this.dst[3], this.dst[4], this.dst[5]);
			} else {
				this.m_at.setTransform(Display.this.m_transform);
			}

			return this.m_at;
		}

		public void pan(double var1, double var3, long var5) {
			AffineTransform var7 = this.getTransform();
			this.cancel();
			this.setDuration(var5);
			var7.translate(var1, var3);
			var7.getMatrix(this.dst);
			Display.this.m_transform.getMatrix(this.src);
			this.runNow();
		}

		public void zoom(Point2D var1, double var2, long var4) {
			AffineTransform var6 = this.getTransform();
			this.cancel();
			this.setDuration(var4);
			double var7 = var1.getX();
			double var9 = var1.getY();
			var6.translate(var7, var9);
			var6.scale(var2, var2);
			var6.translate(-var7, -var9);
			var6.getMatrix(this.dst);
			Display.this.m_transform.getMatrix(this.src);
			this.runNow();
		}

		protected void run(long var1) {
			double var3 = this.getPace(var1);
			Display.this.m_transform.setTransform(this.src[0] + var3 * (this.dst[0] - this.src[0]), this.src[1] + var3 * (this.dst[1] - this.src[1]), this.src[2] + var3 * (this.dst[2] - this.src[2]), this.src[3] + var3 * (this.dst[3] - this.src[3]), this.src[4] + var3 * (this.dst[4] - this.src[4]), this.src[5] + var3 * (this.dst[5] - this.src[5]));

			try {
				Display.this.m_itransform = Display.this.m_transform.createInverse();
			} catch (Exception var6) {
				;
			}

			Display.this.repaint();
		}
	}
}
