package edu.berkeley.guir.prefusex.force;

public class ForceItem {
	public float mass = 1.0F;
	public float[] force = {0.0F, 0.0F};
	public float[] velocity = {0.0F, 0.0F};
	public float[] location = {0.0F, 0.0F};
	public float[] plocation = {0.0F, 0.0F};
	public float[][] k = new float[4][2];
	public float[][] l = new float[4][2];
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/ForceItem.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */