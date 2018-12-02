package prefuse.hyperbolictree;

import java.awt.geom.Point2D;

public class HyperbolicParams {
	public double[] z = new double[2];
	public double[] zo = new double[2];
	public double weight;

	public HyperbolicParams() {
	}

	public static void multiply(double[] t, double[] z) {
		double tx = t[0];
		double ty = t[1];
		t[0] = tx * z[0] - ty * z[1];
		t[1] = tx * z[1] + ty * z[0];
	}

	public static void divide(double[] t, double[] z) {
		double d = z[0] * z[0] + z[1] * z[1];
		double tx = t[0];
		double ty = t[1];
		t[0] = (tx * z[0] + ty * z[1]) / d;
		t[1] = (ty * z[0] - tx * z[1]) / d;
	}

	public static void project(Point2D loc, double[] z, Point2D origin, Point2D max) {
		double x = z[0] * max.getX() + origin.getX();
		double y = -(z[1] * max.getY()) + origin.getY();
		loc.setLocation(x, y);
	}

	public static void translate(double[] z, double[] t) {
		double dX = z[0] * t[0] + z[1] * t[1] + 1.0D;
		double dY = z[1] * t[0] - z[0] * t[1];
		double dd = dX * dX + dY * dY;
		double nX = z[0] + t[0];
		double nY = z[1] + t[1];
		z[0] = (nX * dX + nY * dY) / dd;
		z[1] = (nY * dX - nX * dY) / dd;
	}

	public static double angle(double[] z) {
		double a = Math.atan(z[1] / z[0]);
		return a + (z[0] < 0.0D ? 3.141592653589793D : (z[1] < 0.0D ? 6.283185307179586D : 0.0D));
	}

	public static void setControlPoint(double[] e, double[] p, double[] n) {
		double da = 1.0D + p[0] * p[0] + p[1] * p[1];
		double db = 1.0D + n[0] * n[0] + n[1] * n[1];
		double dd = 2.0D * (p[0] * n[1] - n[0] * p[1]);
		double cx = (n[1] * da - p[1] * db) / dd;
		double cy = (p[0] * db - n[0] * da) / dd;
		double det = (n[0] - cx) * (p[1] - cy) - (p[0] - cx) * (n[1] - cy);
		double fa = p[1] * (p[1] - cy) - p[0] * (cx - p[0]);
		double fb = n[1] * (n[1] - cy) - n[0] * (cx - n[0]);
		e[0] = ((p[1] - cy) * fb - (n[1] - cy) * fa) / det;
		e[1] = ((cx - p[0]) * fb - (cx - n[0]) * fa) / det;
		if (Double.isNaN(e[0]) || Double.isNaN(e[1])) {
			e[0] = (p[0] + n[0]) / 2.0D;
			e[1] = (p[1] + n[1]) / 2.0D;
		}

	}
}
