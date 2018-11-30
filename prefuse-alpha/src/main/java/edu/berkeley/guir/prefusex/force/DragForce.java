package edu.berkeley.guir.prefusex.force;

public class DragForce
		extends AbstractForce {
	private static String[] pnames = {"DragCoefficient"};
	public static final float DEFAULT_DRAG_COEFF = -0.01F;
	public static final int DRAG_COEFF = 0;

	public DragForce(float paramFloat) {
		this.params = new float[]{paramFloat};
	}

	public DragForce() {
		this(-0.01F);
	}

	public boolean isItemForce() {
		return true;
	}

	protected String[] getParameterNames() {
		return pnames;
	}

	public void getForce(ForceItem paramForceItem) {
		paramForceItem.force[0] += this.params[0] * paramForceItem.velocity[0];
		paramForceItem.force[1] += this.params[0] * paramForceItem.velocity[1];
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/DragForce.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */