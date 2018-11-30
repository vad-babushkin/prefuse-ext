package edu.berkeley.guir.prefusex.force;

public class SpringForce
		extends AbstractForce {
	private static String[] pnames = {"SpringCoefficient", "DefaultSpringLength"};
	public static final float DEFAULT_SPRING_COEFF = 1.0E-4F;
	public static final float DEFAULT_SPRING_LENGTH = 100.0F;
	public static final int SPRING_COEFF = 0;
	public static final int SPRING_LENGTH = 1;
	private ForceSimulator fsim;

	public SpringForce(float paramFloat1, float paramFloat2) {
		this.params = new float[]{paramFloat1, paramFloat2};
	}

	public SpringForce() {
		this(1.0E-4F, 100.0F);
	}

	public boolean isSpringForce() {
		return true;
	}

	protected String[] getParameterNames() {
		return pnames;
	}

	public void init(ForceSimulator paramForceSimulator) {
		this.fsim = paramForceSimulator;
	}

	public void getForce(Spring paramSpring) {
		ForceItem localForceItem1 = paramSpring.item1;
		ForceItem localForceItem2 = paramSpring.item2;
		float f1 = paramSpring.length < 0.0F ? this.params[1] : paramSpring.length;
		float f2 = localForceItem1.location[0];
		float f3 = localForceItem1.location[1];
		float f4 = localForceItem2.location[0];
		float f5 = localForceItem2.location[1];
		float f6 = f4 - f2;
		float f7 = f5 - f3;
		float f8 = (float) Math.sqrt(f6 * f6 + f7 * f7);
		if (f8 == 0.0D) {
			f6 = ((float) Math.random() - 0.5F) / 50.0F;
			f7 = ((float) Math.random() - 0.5F) / 50.0F;
			f8 = (float) Math.sqrt(f6 * f6 + f7 * f7);
		}
		float f9 = f8 - f1;
		float f10 = (paramSpring.coeff < 0.0F ? this.params[0] : paramSpring.coeff) * f9 / f8;
		localForceItem1.force[0] += f10 * f6;
		localForceItem1.force[1] += f10 * f7;
		localForceItem2.force[0] += -f10 * f6;
		localForceItem2.force[1] += -f10 * f7;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/SpringForce.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */