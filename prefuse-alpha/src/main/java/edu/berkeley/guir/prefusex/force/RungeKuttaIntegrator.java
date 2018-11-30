package edu.berkeley.guir.prefusex.force;

import java.util.Iterator;

public class RungeKuttaIntegrator
		implements Integrator {
	public void integrate(ForceSimulator paramForceSimulator, long paramLong) {
		float f1 = paramForceSimulator.getSpeedLimit();
		Iterator localIterator = paramForceSimulator.getItems();
		ForceItem localForceItem;
		float f5;
		float[][] arrayOfFloat1;
		float[][] arrayOfFloat2;
		while (localIterator.hasNext()) {
			localForceItem = (ForceItem) localIterator.next();
			f5 = (float) paramLong / localForceItem.mass;
			arrayOfFloat1 = localForceItem.k;
			arrayOfFloat2 = localForceItem.l;
			localForceItem.plocation[0] = localForceItem.location[0];
			localForceItem.plocation[1] = localForceItem.location[1];
			arrayOfFloat1[0][0] = ((float) paramLong * localForceItem.velocity[0]);
			arrayOfFloat1[0][1] = ((float) paramLong * localForceItem.velocity[1]);
			arrayOfFloat2[0][0] = (f5 * localForceItem.force[0]);
			arrayOfFloat2[0][1] = (f5 * localForceItem.force[1]);
			localForceItem.location[0] += 0.5F * arrayOfFloat1[0][0];
			localForceItem.location[1] += 0.5F * arrayOfFloat1[0][1];
		}
		paramForceSimulator.accumulate();
		localIterator = paramForceSimulator.getItems();
		float f2;
		float f3;
		float f4;
		while (localIterator.hasNext()) {
			localForceItem = (ForceItem) localIterator.next();
			f5 = (float) paramLong / localForceItem.mass;
			arrayOfFloat1 = localForceItem.k;
			arrayOfFloat2 = localForceItem.l;
			f2 = localForceItem.velocity[0] + 0.5F * arrayOfFloat2[0][0];
			f3 = localForceItem.velocity[1] + 0.5F * arrayOfFloat2[0][1];
			f4 = (float) Math.sqrt(f2 * f2 + f3 * f3);
			if (f4 > f1) {
				f2 = f1 * f2 / f4;
				f3 = f1 * f3 / f4;
			}
			arrayOfFloat1[1][0] = ((float) paramLong * f2);
			arrayOfFloat1[1][1] = ((float) paramLong * f3);
			arrayOfFloat2[1][0] = (f5 * localForceItem.force[0]);
			arrayOfFloat2[1][1] = (f5 * localForceItem.force[1]);
			localForceItem.location[0] = (localForceItem.plocation[0] + 0.5F * arrayOfFloat1[1][0]);
			localForceItem.location[1] = (localForceItem.plocation[1] + 0.5F * arrayOfFloat1[1][1]);
		}
		paramForceSimulator.accumulate();
		localIterator = paramForceSimulator.getItems();
		while (localIterator.hasNext()) {
			localForceItem = (ForceItem) localIterator.next();
			f5 = (float) paramLong / localForceItem.mass;
			arrayOfFloat1 = localForceItem.k;
			arrayOfFloat2 = localForceItem.l;
			f2 = localForceItem.velocity[0] + 0.5F * arrayOfFloat2[1][0];
			f3 = localForceItem.velocity[1] + 0.5F * arrayOfFloat2[1][1];
			f4 = (float) Math.sqrt(f2 * f2 + f3 * f3);
			if (f4 > f1) {
				f2 = f1 * f2 / f4;
				f3 = f1 * f3 / f4;
			}
			arrayOfFloat1[2][0] = ((float) paramLong * f2);
			arrayOfFloat1[2][1] = ((float) paramLong * f3);
			arrayOfFloat2[2][0] = (f5 * localForceItem.force[0]);
			arrayOfFloat2[2][1] = (f5 * localForceItem.force[1]);
			localForceItem.location[0] = (localForceItem.plocation[0] + 0.5F * arrayOfFloat1[2][0]);
			localForceItem.location[1] = (localForceItem.plocation[1] + 0.5F * arrayOfFloat1[2][1]);
		}
		paramForceSimulator.accumulate();
		localIterator = paramForceSimulator.getItems();
		while (localIterator.hasNext()) {
			localForceItem = (ForceItem) localIterator.next();
			f5 = (float) paramLong / localForceItem.mass;
			arrayOfFloat1 = localForceItem.k;
			arrayOfFloat2 = localForceItem.l;
			float[] arrayOfFloat = localForceItem.plocation;
			f2 = localForceItem.velocity[0] + arrayOfFloat2[2][0];
			f3 = localForceItem.velocity[1] + arrayOfFloat2[2][1];
			f4 = (float) Math.sqrt(f2 * f2 + f3 * f3);
			if (f4 > f1) {
				f2 = f1 * f2 / f4;
				f3 = f1 * f3 / f4;
			}
			arrayOfFloat1[3][0] = ((float) paramLong * f2);
			arrayOfFloat1[3][1] = ((float) paramLong * f3);
			arrayOfFloat2[3][0] = (f5 * localForceItem.force[0]);
			arrayOfFloat2[3][1] = (f5 * localForceItem.force[1]);
			localForceItem.location[0] = (arrayOfFloat[0] + (arrayOfFloat1[0][0] + arrayOfFloat1[3][0]) / 6.0F + (arrayOfFloat1[1][0] + arrayOfFloat1[2][0]) / 3.0F);
			localForceItem.location[1] = (arrayOfFloat[1] + (arrayOfFloat1[0][1] + arrayOfFloat1[3][1]) / 6.0F + (arrayOfFloat1[1][1] + arrayOfFloat1[2][1]) / 3.0F);
			f2 = (arrayOfFloat2[0][0] + arrayOfFloat2[3][0]) / 6.0F + (arrayOfFloat2[1][0] + arrayOfFloat2[2][0]) / 3.0F;
			f3 = (arrayOfFloat2[0][1] + arrayOfFloat2[3][1]) / 6.0F + (arrayOfFloat2[1][1] + arrayOfFloat2[2][1]) / 3.0F;
			f4 = (float) Math.sqrt(f2 * f2 + f3 * f3);
			if (f4 > f1) {
				f2 = f1 * f2 / f4;
				f3 = f1 * f3 / f4;
			}
			localForceItem.velocity[0] += f2;
			localForceItem.velocity[1] += f3;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/RungeKuttaIntegrator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */