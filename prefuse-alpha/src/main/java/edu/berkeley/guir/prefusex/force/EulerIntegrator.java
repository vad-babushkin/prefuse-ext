package edu.berkeley.guir.prefusex.force;

import java.util.Iterator;

public class EulerIntegrator
		implements Integrator {
	public void integrate(ForceSimulator paramForceSimulator, long paramLong) {
		float f1 = paramForceSimulator.getSpeedLimit();
		Iterator localIterator = paramForceSimulator.getItems();
		while (localIterator.hasNext()) {
			ForceItem localForceItem = (ForceItem) localIterator.next();
			localForceItem.location[0] += (float) paramLong * localForceItem.velocity[0];
			localForceItem.location[1] += (float) paramLong * localForceItem.velocity[1];
			float f2 = (float) paramLong / localForceItem.mass;
			localForceItem.velocity[0] += f2 * localForceItem.force[0];
			localForceItem.velocity[1] += f2 * localForceItem.force[1];
			float f3 = localForceItem.velocity[0];
			float f4 = localForceItem.velocity[1];
			float f5 = (float) Math.sqrt(f3 * f3 + f4 * f4);
			if (f5 > f1) {
				localForceItem.velocity[0] = (f1 * f3 / f5);
				localForceItem.velocity[1] = (f1 * f4 / f5);
			}
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/EulerIntegrator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */