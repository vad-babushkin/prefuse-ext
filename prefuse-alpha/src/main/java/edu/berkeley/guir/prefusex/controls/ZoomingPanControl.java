//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;

public class ZoomingPanControl extends ControlAdapter {
	private int xDown;
	private int yDown;
	private double sDown;
	private boolean repaint;
	private boolean started;
	private Point mouseDown;
	private Point mouseCur;
	private Point mouseUp;
	private int dx;
	private int dy;
	private double pd;
	private double d;
	private double v0;
	private double d0;
	private double d1;
	private double s0;
	private ZoomingPanControl.UpdateActivity update;
	private ZoomingPanControl.FinishActivity finish;

	public ZoomingPanControl() {
		this(true);
	}

	public ZoomingPanControl(boolean var1) {
		this.repaint = true;
		this.started = false;
		this.pd = 0.0D;
		this.d = 0.0D;
		this.v0 = 75.0D;
		this.d0 = 50.0D;
		this.d1 = 400.0D;
		this.s0 = 0.1D;
		this.update = new ZoomingPanControl.UpdateActivity();
		this.finish = new ZoomingPanControl.FinishActivity();
		this.repaint = var1;
	}

	public void mousePressed(MouseEvent var1) {
		if (SwingUtilities.isLeftMouseButton(var1)) {
			Display var2 = (Display)var1.getComponent();
			var2.setCursor(Cursor.getPredefinedCursor(13));
			this.mouseDown = var1.getPoint();
			this.sDown = var2.getTransform().getScaleX();
		}

	}

	public void mouseDragged(MouseEvent var1) {
		if (SwingUtilities.isLeftMouseButton(var1)) {
			this.mouseCur = var1.getPoint();
			this.pd = this.d;
			this.dx = this.mouseCur.x - this.mouseDown.x;
			this.dy = this.mouseCur.y - this.mouseDown.y;
			this.d = Math.sqrt((double)(this.dx * this.dx + this.dy * this.dy));
			if (!this.started) {
				Display var2 = (Display)var1.getComponent();
				this.update.setDisplay(var2);
				this.update.runNow();
			}
		}

	}

	public void mouseReleased(MouseEvent var1) {
		if (SwingUtilities.isLeftMouseButton(var1)) {
			this.update.cancel();
			this.started = false;
			Display var2 = (Display)var1.getComponent();
			this.mouseUp = var1.getPoint();
			this.finish.setDisplay(var2);
			this.finish.runNow();
			var2.setCursor(Cursor.getDefaultCursor());
		}

	}

	private class FinishActivity extends Activity {
		private Display display;
		private double scale;

		public FinishActivity() {
			super(1500L, 15L, 0L);
			this.setPacingFunction(new SlowInSlowOutPacer());
		}

		public void setDisplay(Display var1) {
			this.display = var1;
			this.scale = var1.getTransform().getScaleX();
			double var2 = this.scale < 1.0D ? 1.0D / this.scale : this.scale;
			this.setDuration((long)(500.0D + 500.0D * Math.log(1.0D + var2)));
		}

		protected void run(long var1) {
			double var3 = this.getPace(var1);
			double var5 = this.display.getTransform().getScaleX();
			double var7 = (var3 + (1.0D - var3) * this.scale) / var5;
			this.display.zoom(ZoomingPanControl.this.mouseUp, var7);
			if (ZoomingPanControl.this.repaint) {
				this.display.repaint();
			}

		}
	}

	private class UpdateActivity extends Activity {
		private Display display;
		private long lastTime = 0L;

		public UpdateActivity() {
			super(-1L, 15L, 0L);
		}

		public void setDisplay(Display var1) {
			this.display = var1;
		}

		protected void run(long var1) {
			double var3 = this.display.getTransform().getScaleX();
			double var5;
			double var7;
			if (ZoomingPanControl.this.d <= ZoomingPanControl.this.d0) {
				var5 = 1.0D;
				var7 = ZoomingPanControl.this.v0 * (ZoomingPanControl.this.d / ZoomingPanControl.this.d0);
			} else {
				var5 = ZoomingPanControl.this.d >= ZoomingPanControl.this.d1 ? ZoomingPanControl.this.s0 : Math.pow(ZoomingPanControl.this.s0, (ZoomingPanControl.this.d - ZoomingPanControl.this.d0) / (ZoomingPanControl.this.d1 - ZoomingPanControl.this.d0));
				var7 = ZoomingPanControl.this.v0;
			}

			var5 /= var3;
			double var9 = var7 * (double)(var1 - this.lastTime) / 1000.0D;
			this.lastTime = var1;
			double var11 = -var9 * (double)ZoomingPanControl.this.dx / ZoomingPanControl.this.d;
			double var13 = -var9 * (double)ZoomingPanControl.this.dy / ZoomingPanControl.this.d;
			this.display.pan(var11, var13);
			if (var5 != 1.0D) {
				this.display.zoom(ZoomingPanControl.this.mouseCur, var5);
			}

			if (ZoomingPanControl.this.repaint) {
				this.display.repaint();
			}

		}
	}
}
