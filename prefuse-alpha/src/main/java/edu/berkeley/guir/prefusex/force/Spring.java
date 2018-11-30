package edu.berkeley.guir.prefusex.force;

import java.util.ArrayList;

public class Spring {
	private static SpringFactory s_factory = new SpringFactory();
	public ForceItem item1;
	public ForceItem item2;
	public float length;
	public float coeff;

	public static SpringFactory getFactory() {
		return s_factory;
	}

	public Spring(ForceItem paramForceItem1, ForceItem paramForceItem2, float paramFloat1, float paramFloat2) {
		this.item1 = paramForceItem1;
		this.item2 = paramForceItem2;
		this.coeff = paramFloat1;
		this.length = paramFloat2;
	}

	public static final class SpringFactory {
		private int maxSprings = 10000;
		private ArrayList springs = new ArrayList();

		public Spring getSpring(ForceItem paramForceItem1, ForceItem paramForceItem2, float paramFloat1, float paramFloat2) {
			if (this.springs.size() > 0) {
				Spring localSpring = (Spring) this.springs.remove(this.springs.size() - 1);
				localSpring.item1 = paramForceItem1;
				localSpring.item2 = paramForceItem2;
				localSpring.coeff = paramFloat1;
				localSpring.length = paramFloat2;
				return localSpring;
			}
			return new Spring(paramForceItem1, paramForceItem2, paramFloat1, paramFloat2);
		}

		public void reclaim(Spring paramSpring) {
			paramSpring.item1 = null;
			paramSpring.item2 = null;
			if (this.springs.size() < this.maxSprings) {
				this.springs.add(paramSpring);
			}
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/Spring.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */