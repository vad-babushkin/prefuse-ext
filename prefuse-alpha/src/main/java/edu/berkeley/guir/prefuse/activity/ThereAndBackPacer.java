package edu.berkeley.guir.prefuse.activity;

public class ThereAndBackPacer
		implements Pacer {
	public double pace(double paramDouble) {
		return 2.0D * (paramDouble <= 0.5D ? paramDouble : 1.0D - paramDouble);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/activity/ThereAndBackPacer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */