package edu.berkeley.guir.prefusex.force;

public abstract interface Force {
	public abstract void init(ForceSimulator paramForceSimulator);

	public abstract int getParameterCount();

	public abstract float getParameter(int paramInt);

	public abstract String getParameterName(int paramInt);

	public abstract void setParameter(int paramInt, float paramFloat);

	public abstract boolean isSpringForce();

	public abstract boolean isItemForce();

	public abstract void getForce(ForceItem paramForceItem);

	public abstract void getForce(Spring paramSpring);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/Force.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */