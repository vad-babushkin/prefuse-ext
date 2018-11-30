package edu.berkeley.guir.prefusex.force;

/**
 * Wrapper class for force simulation variables associated with a
 * graph item instance.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ForceItem {
    public ForceItem() {
        mass = 1.0f;
        force = new float[] { 0.f, 0.f };
        velocity = new float[] { 0.f, 0.f };
        location = new float[] { 0.f, 0.f };
        plocation = new float[] { 0.f, 0.f };
        k = new float[4][2];
        l = new float[4][2];
    } //
    public float   mass;
    public float[] force;
    public float[] velocity;
    public float[] location;
    public float[] plocation; // stores the previous location
    public float[][] k; // temp variables for Runge-Kutta integration
    public float[][] l; // temp variables for Runge-Kutta integration
    
    public static final boolean isValid(ForceItem item) {
        return
          !( Float.isNaN(item.location[0])  || Float.isNaN(item.location[1])  || 
             Float.isNaN(item.plocation[0]) || Float.isNaN(item.plocation[1]) ||
             Float.isNaN(item.velocity[0])  || Float.isNaN(item.velocity[1])  ||
             Float.isNaN(item.force[0])     || Float.isNaN(item.force[1]) );
    } //
    
} // end of class ForceItem
