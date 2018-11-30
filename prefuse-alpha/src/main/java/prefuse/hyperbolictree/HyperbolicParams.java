/*    */
package prefuse.hyperbolictree;
/*    */
/*    */

import java.awt.geom.Point2D;

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class HyperbolicParams
		/*    */ {
	/* 14 */   public double[] z = new double[2];
	/* 15 */   public double[] zo = new double[2];
	/*    */   public double weight;

	/*    */
	/*    */
	public static void multiply(double[] t, double[] z) {
		/* 19 */
		double tx = t[0];
		double ty = t[1];
		/* 20 */
		t[0] = (tx * z[0] - ty * z[1]);
		/* 21 */
		t[1] = (tx * z[1] + ty * z[0]);
		/*    */
	}

	/*    */
	/*    */
	public static void divide(double[] t, double[] z) {
		/* 25 */
		double d = z[0] * z[0] + z[1] * z[1];
		/* 26 */
		double tx = t[0];
		double ty = t[1];
		/* 27 */
		t[0] = ((tx * z[0] + ty * z[1]) / d);
		/* 28 */
		t[1] = ((ty * z[0] - tx * z[1]) / d);
		/*    */
	}

	/*    */
	/*    */
	/*    */
	public static void project(Point2D loc, double[] z, Point2D origin, Point2D max)
	/*    */ {
		/* 34 */
		double x = z[0] * max.getX() + origin.getX();
		/* 35 */
		double y = -(z[1] * max.getY()) + origin.getY();
		/* 36 */
		loc.setLocation(x, y);
		/*    */
	}

	/*    */
	/*    */
	public static void translate(double[] z, double[] t) {
		/* 40 */
		double dX = z[0] * t[0] + z[1] * t[1] + 1.0D;
		/* 41 */
		double dY = z[1] * t[0] - z[0] * t[1];
		/* 42 */
		double dd = dX * dX + dY * dY;
		/* 43 */
		double nX = z[0] + t[0];
		/* 44 */
		double nY = z[1] + t[1];
		/*    */
		/* 46 */
		z[0] = ((nX * dX + nY * dY) / dd);
		/* 47 */
		z[1] = ((nY * dX - nX * dY) / dd);
		/*    */
	}

	/*    */
	/*    */
	public static double angle(double[] z) {
		/* 51 */
		double a = Math.atan(z[1] / z[0]);
		/* 52 */
		return a + (z[1] < 0.0D ? 6.283185307179586D : z[0] < 0.0D ? 3.141592653589793D : 0.0D);
		/*    */
	}

	/*    */
	/*    */
	public static void setControlPoint(double[] e, double[] p, double[] n) {
		/* 56 */
		double da = 1.0D + p[0] * p[0] + p[1] * p[1];
		/* 57 */
		double db = 1.0D + n[0] * n[0] + n[1] * n[1];
		/* 58 */
		double dd = 2.0D * (p[0] * n[1] - n[0] * p[1]);
		/* 59 */
		double cx = (n[1] * da - p[1] * db) / dd;
		/* 60 */
		double cy = (p[0] * db - n[0] * da) / dd;
		/* 61 */
		double det = (n[0] - cx) * (p[1] - cy) - (p[0] - cx) * (n[1] - cy);
		/* 62 */
		double fa = p[1] * (p[1] - cy) - p[0] * (cx - p[0]);
		/* 63 */
		double fb = n[1] * (n[1] - cy) - n[0] * (cx - n[0]);
		/*    */
		/* 65 */
		e[0] = (((p[1] - cy) * fb - (n[1] - cy) * fa) / det);
		/* 66 */
		e[1] = (((cx - p[0]) * fb - (cx - n[0]) * fa) / det);
		/*    */
		/* 68 */
		if ((Double.isNaN(e[0])) || (Double.isNaN(e[1]))) {
			/* 69 */
			e[0] = ((p[0] + n[0]) / 2.0D);
			/* 70 */
			e[1] = ((p[1] + n[1]) / 2.0D);
			/*    */
		}
		/*    */
	}
	/*    */
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/prefuse/hyperbolictree/HyperbolicParams.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */