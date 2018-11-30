package edu.berkeley.guir.prefuse.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A library of useful geometry routines for computing the intersection
 * of different shapes.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class GeometryLib {

	public static final int NO_INTERSECTION = 0;
	public static final int COINCIDENT      = -1;
	public static final int PARALLEL        = -2;

	public static int intersectLineLine(Line2D a, Line2D b, Point2D intersect) {
		double a1x = a.getX1(), a1y = a.getY1();
		double a2x = a.getX2(), a2y = a.getY2();
		double b1x = b.getX1(), b1y = b.getY1();
		double b2x = b.getX2(), b2y = b.getY2();
		return intersectLineLine(a1x,a1y,a2x,a2y,b1x,b1y,b2x,b2y,intersect);
	} //
	
	public static int intersectLineLine(double a1x, double a1y, double a2x,
		double a2y, double b1x, double b1y, double b2x, double b2y, 
		Point2D intersect)
	{
		double ua_t = (b2x-b1x)*(a1y-b1y)-(b2y-b1y)*(a1x-b1x);
		double ub_t = (a2x-a1x)*(a1y-b1y)-(a2y-a1y)*(a1x-b1x);
		double u_b  = (b2y-b1y)*(a2x-a1x)-(b2x-b1x)*(a2y-a1y);

		if ( u_b != 0 ) {
			double ua = ua_t / u_b;
			double ub = ub_t / u_b;

			if ( 0 <= ua && ua <= 1 && 0 <= ub && ub <= 1 ) {
				intersect.setLocation(a1x+ua*(a2x-a1x), a1y+ua*(a2y-a1y));
				return 1;
			} else {
				return NO_INTERSECTION;
			}
		} else {
			return ( ua_t == 0 || ub_t == 0 ? COINCIDENT : PARALLEL );
		}
	} //

	public static int intersectLineRectangle(Point2D a1, Point2D a2, Rectangle2D r, Point2D[] pts) {
		double a1x = a1.getX(), a1y = a1.getY();
		double a2x = a2.getX(), a2y = a2.getY();
		double mxx = r.getMaxX(), mxy = r.getMaxY();
		double mnx = r.getMinX(), mny = r.getMinY();
		
		if ( pts[0] == null ) pts[0] = new Point2D.Double();
		if ( pts[1] == null ) pts[1] = new Point2D.Double();
		
		int result, i = 0;
		if ( intersectLineLine(mnx,mny,mxx,mny,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		if ( intersectLineLine(mxx,mny,mxx,mxy,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		if ( i == 2 ) return i;
		if ( intersectLineLine(mxx,mxy,mnx,mxy,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		if ( i == 2 ) return i;
		if ( intersectLineLine(mnx,mxy,mnx,mny,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		return i;
	} //

	public static int intersectLineRectangle(Line2D l, Rectangle2D r, Point2D[] pts) {
		double a1x = l.getX1(), a1y = l.getY1();
		double a2x = l.getX2(), a2y = l.getY2();
		double mxx = r.getMaxX(), mxy = r.getMaxY();
		double mnx = r.getMinX(), mny = r.getMinY();
		
		if ( pts[0] == null ) pts[0] = new Point2D.Double();
		if ( pts[1] == null ) pts[1] = new Point2D.Double();
		
		int result, i = 0;
		if ( intersectLineLine(mnx,mny,mxx,mny,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		if ( intersectLineLine(mxx,mny,mxx,mxy,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		if ( i == 2 ) return i;
		if ( intersectLineLine(mxx,mxy,mnx,mxy,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		if ( i == 2 ) return i;
		if ( intersectLineLine(mnx,mxy,mnx,mny,a1x,a1y,a2x,a2y,pts[i]) > 0 ) i++;
		return i;
	} //
	
	/**
	 * Computes the 2D convex hull of a set of points using Graham's
	 * scanning algorithm. The algorithm has been implemented as described
	 * in Cormen, Leiserson, and Rivest's Introduction to Algorithms.
	 * 
	 * The running time of this algorithm is O(n log n), where n is
	 * the number of input points.
	 * 
	 * @param pts
	 * @return
	 */
	public static double[] convexHull(double[] pts, int len) {
	    if (len < 6) {
	        throw new IllegalArgumentException(
	                "Input must have at least 3 points");
	    }
	    
	    int i0 = 0;
	    // find the starting ref point
	    for ( int i=2; i < len; i += 2 ) {
	        if ( pts[i+1] < pts[i0+1] ) {
	            i0 = i;
	        } else if ( pts[i+1] == pts[i0+1] ) {
	            i0 = (pts[i] < pts[i0] ? i : i0);
	        }
	    }
	    
	    // calculate polar angles from ref point and sort
	    float[] angles = new float[len/2 - 1];
	    int[]   idx    = new int[len/2 - 1];
	    for ( int i=0, j=0; i < len; i+=2 ) {
	        if ( i == i0 ) continue;
	        angles[j] = (float)Math.atan2(pts[i+1]-pts[i0+1], pts[i]-pts[i0]);
	        idx[j] = i;
	        j += 1;
	    }
	    ArrayLib.sort(angles,idx);
	    
	    // toss out duplicated angles
	    float angle = angles[0];
	    int ti = 0;
	    for ( int i=1; i<angles.length; i++ ) {
	        if ( angle == angles[i] ) {
	            double d1 = Math.sqrt(pts[i]*pts[i]   + pts[i+1]*pts[i+1]);
	            double d2 = Math.sqrt(pts[ti]*pts[ti] + pts[ti+1]*pts[ti+1]);
	            if ( d1 >= d2 ) {
	                idx[i] = -1;
	            } else {
	                idx[ti] = -1;
	                angle = angles[i];
	                ti = i;
	            }
	        } else {
	            angle = angles[i];
	            ti = i;
	        }
	    }
	    
	    // initialize our stack
	    int sp = 0;
	    int[] stack = new int[len/2];
	    stack[sp++] = i0;
	    int j = 0;
	    for ( int k=0; k<2; j++ ) {
	        if ( idx[j] != -1 ) {
	            stack[sp++] = idx[j];
	            k++;
	        }
	    }
	    
	    // do graham's scan
	    for ( ; j < idx.length; j++ ) {
	        if ( idx[j] == -1 ) continue; // skip tossed out points
	        while ( isNonLeft(i0, stack[sp-2], stack[sp-1], idx[j], pts) ) {
	            sp--;
	        }
	        stack[sp++] = idx[j];
	    }
	    
	    // construct the hull
	    double hull[] = new double[sp*2];
	    for ( int i=0; i<sp; i++ ) {
	        hull[2*i]  = pts[stack[i]];
	        hull[2*i+1] = pts[stack[i]+1];
	    }
	    return hull;
	} //

	private static boolean isNonLeft(int i0, int i1, int i2, int i3, double[] pts) {
	    double l1, l2, l4, l5, l6, angle1, angle2, angle;

	    l1 = Math.sqrt(Math.pow(pts[i2+1]-pts[i1+1],2) + Math.pow(pts[i2]-pts[i1],2));
	    l2 = Math.sqrt(Math.pow(pts[i3+1]-pts[i2+1],2) + Math.pow(pts[i3]-pts[i2],2));
	    l4 = Math.sqrt(Math.pow(pts[i3+1]-pts[i0+1],2) + Math.pow(pts[i3]-pts[i0],2));
	    l5 = Math.sqrt(Math.pow(pts[i1+1]-pts[i0+1],2) + Math.pow(pts[i1]-pts[i0],2));
	    l6 = Math.sqrt(Math.pow(pts[i2+1]-pts[i0+1],2) + Math.pow(pts[i2]-pts[i0],2));

	    angle1 = Math.acos( ( (l2*l2)+(l6*l6)-(l4*l4) ) / (2*l2*l6) );
	    angle2 = Math.acos( ( (l6*l6)+(l1*l1)-(l5*l5) ) / (2*l6*l1) );

	    angle = (Math.PI - angle1) - angle2;

	    if(angle <= 0.0) {
	        return(true);
	    } else {
	        return(false);
	    }
	} //
	
	public static double[] centroid(double pts[], int len) {
	    double[] c = new double[] {0, 0};
	    for ( int i=0; i < len; i+=2 ) {
	        c[0] += pts[i];
	        c[1] += pts[i+1];
	    }
	    c[0] /= len/2;
	    c[1] /= len/2;
	    return c;
	} //
	
	public static void growPolygon(double pts[], int len, double amt) {
	    double[] c = centroid(pts, len);
	    for ( int i=0; i < len; i+=2 ) {
	        double vx = pts[i]-c[0];
	        double vy = pts[i+1]-c[1];
	        double norm = Math.sqrt(vx*vx+vy*vy);
	        pts[i] += amt*vx/norm;
	        pts[i+1] += amt*vy/norm;
	    }
	} //
	
} // end of class GeometryLib
