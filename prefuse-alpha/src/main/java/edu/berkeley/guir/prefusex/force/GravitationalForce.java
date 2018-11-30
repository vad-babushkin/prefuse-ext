package edu.berkeley.guir.prefusex.force;

public class GravitationalForce
		extends AbstractForce {
	private static final String[] pnames = {"GravitationalConstant", "Direction"};
	public static final int GRAVITATIONAL_CONST = 0;
	public static final int DIRECTION = 1;
	public static final float DEFAULT_FORCE_CONSTANT = 1.0E-4F;
	public static final float DEFAULT_DIRECTION = -90.0F;

	public GravitationalForce(float paramFloat1, float paramFloat2) {
		this.params = new float[]{paramFloat1, paramFloat2};
	}

	public GravitationalForce() {
		this(1.0E-4F, -90.0F);
	}

	public boolean isItemForce() {
		return true;
	}

	protected String[] getParameterNames() {
		return pnames;
	}

	public void getForce(ForceItem paramForceItem) {
		float f1 = (float) Math.toRadians(-this.params[1]);
		float f2 = this.params[0] * paramForceItem.mass;
		int tmp30_29 = 0;
		float[] tmp30_26 = paramForceItem.force;
		tmp30_26[tmp30_29] = ((float) (tmp30_26[tmp30_29] + Math.cos(f1) * f2));
		int tmp49_48 = 1;
		float[] tmp49_45 = paramForceItem.force;
		tmp49_45[tmp49_48] = ((float) (tmp49_45[tmp49_48] + Math.sin(f1) * f2));
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/GravitationalForce.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */