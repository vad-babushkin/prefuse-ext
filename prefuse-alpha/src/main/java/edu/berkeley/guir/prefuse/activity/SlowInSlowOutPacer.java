package edu.berkeley.guir.prefuse.activity;

public class SlowInSlowOutPacer
		implements Pacer {
	public double pace(double paramDouble) {
		return (paramDouble == 0.0D) || (paramDouble == 1.0D) ? paramDouble : sigmoid(paramDouble);
	}

	private double sigmoid(double paramDouble) {
		paramDouble = 12.0D * paramDouble - 6.0D;
		return 1.0D / (1.0D + Math.exp(-1.0D * paramDouble));
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/activity/SlowInSlowOutPacer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */