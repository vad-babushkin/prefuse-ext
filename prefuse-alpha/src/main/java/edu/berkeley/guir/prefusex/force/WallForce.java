package edu.berkeley.guir.prefusex.force;

import java.awt.geom.Line2D;

public class WallForce
		extends AbstractForce {
	private static String[] pnames = {"GravitationalConstant"};
	public static final float DEFAULT_GRAV_CONSTANT = -0.1F;
	public static final int GRAVITATIONAL_CONST = 0;
	private float x1;
	private float y1;
	private float x2;
	private float y2;
	private float dx;
	private float dy;

	public WallForce(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5) {
		this.params = new float[]{paramFloat1};
		this.x1 = paramFloat2;
		this.y1 = paramFloat3;
		this.x2 = paramFloat4;
		this.y2 = paramFloat5;
		this.dx = (paramFloat4 - paramFloat2);
		this.dy = (paramFloat5 - paramFloat3);
		float f = (float) Math.sqrt(this.dx * this.dx + this.dy * this.dy);
		if (this.dx != 0.0D) {
			this.dx /= f;
		}
		if (this.dy != 0.0D) {
			this.dy /= f;
		}
	}

	public WallForce(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
		this(-0.1F, paramFloat1, paramFloat2, paramFloat3, paramFloat4);
	}

	public boolean isItemForce() {
		return true;
	}

	protected String[] getParameterNames() {
		return pnames;
	}

	public void getForce(ForceItem paramForceItem) {
		float[] arrayOfFloat = paramForceItem.location;
		int i = Line2D.relativeCCW(this.x1, this.y1, this.x2, this.y2, arrayOfFloat[0], arrayOfFloat[1]);
		float f1 = (float) Line2D.ptSegDist(this.x1, this.y1, this.x2, this.y2, arrayOfFloat[0], arrayOfFloat[1]);
		if (f1 == 0.0D) {
			f1 = (float) Math.random() / 100.0F;
		}
		float f2 = this.params[0] * paramForceItem.mass / (f1 * f1 * f1);
		if ((arrayOfFloat[0] >= Math.min(this.x1, this.x2)) && (arrayOfFloat[0] <= Math.max(this.x1, this.x2))) {
			paramForceItem.force[1] += i * f2 * this.dx;
		}
		if ((arrayOfFloat[1] >= Math.min(this.y1, this.y2)) && (arrayOfFloat[1] <= Math.max(this.y1, this.y2))) {
			paramForceItem.force[0] += -1 * i * f2 * this.dy;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/WallForce.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */