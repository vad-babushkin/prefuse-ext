package edu.berkeley.guir.prefusex.force;

public abstract class AbstractForce
		implements Force {
	protected float[] params;

	public void init(ForceSimulator paramForceSimulator) {
	}

	public int getParameterCount() {
		return this.params == null ? 0 : this.params.length;
	}

	public float getParameter(int paramInt) {
		if ((paramInt < 0) || (this.params == null) || (paramInt >= this.params.length)) {
			throw new IndexOutOfBoundsException();
		}
		return this.params[paramInt];
	}

	public String getParameterName(int paramInt) {
		String[] arrayOfString = getParameterNames();
		if ((paramInt < 0) || (arrayOfString == null) || (paramInt >= arrayOfString.length)) {
			throw new IndexOutOfBoundsException();
		}
		return arrayOfString[paramInt];
	}

	public void setParameter(int paramInt, float paramFloat) {
		if ((paramInt < 0) || (this.params == null) || (paramInt >= this.params.length)) {
			throw new IndexOutOfBoundsException();
		}
		this.params[paramInt] = paramFloat;
	}

	protected abstract String[] getParameterNames();

	public boolean isItemForce() {
		return false;
	}

	public boolean isSpringForce() {
		return false;
	}

	public void getForce(ForceItem paramForceItem) {
		throw new UnsupportedOperationException("This class does not support this operation");
	}

	public void getForce(Spring paramSpring) {
		throw new UnsupportedOperationException("This class does not support this operation");
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/AbstractForce.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */