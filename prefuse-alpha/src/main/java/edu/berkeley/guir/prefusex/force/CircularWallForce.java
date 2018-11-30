package edu.berkeley.guir.prefusex.force;

public class CircularWallForce
		extends AbstractForce {
	private static String[] pnames = {"GravitationalConstant"};
	public static final float DEFAULT_GRAV_CONSTANT = -0.1F;
	public static final int GRAVITATIONAL_CONST = 0;
	private float x;
	private float y;
	private float r;

	public CircularWallForce(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
		this.params = new float[]{paramFloat1};
		this.x = paramFloat2;
		this.y = paramFloat3;
		this.r = paramFloat4;
	}

	public CircularWallForce(float paramFloat1, float paramFloat2, float paramFloat3) {
		this(-0.1F, paramFloat1, paramFloat2, paramFloat3);
	}

	public boolean isItemForce() {
		return true;
	}

	protected String[] getParameterNames() {
		return pnames;
	}

	public void getForce(ForceItem paramForceItem) {
		float[] arrayOfFloat = paramForceItem.location;
		float f1 = this.x - arrayOfFloat[0];
		float f2 = this.y - arrayOfFloat[1];
		float f3 = (float) Math.sqrt(f1 * f1 + f2 * f2);
		float f4 = this.r - f3;
		float f5 = f4 > 0.0F ? -1.0F : 1.0F;
		float f6 = f5 * this.params[0] * paramForceItem.mass / (f4 * f4);
		if (f3 == 0.0D) {
			f1 = ((float) Math.random() - 0.5F) / 50.0F;
			f2 = ((float) Math.random() - 0.5F) / 50.0F;
			f3 = (float) Math.sqrt(f1 * f1 + f2 * f2);
		}
		paramForceItem.force[0] += f6 * f1 / f3;
		paramForceItem.force[1] += f6 * f2 / f3;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/CircularWallForce.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */