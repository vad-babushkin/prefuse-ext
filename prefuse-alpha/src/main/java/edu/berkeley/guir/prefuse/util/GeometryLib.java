package edu.berkeley.guir.prefuse.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class GeometryLib {
	public static final int NO_INTERSECTION = 0;
	public static final int COINCIDENT = -1;
	public static final int PARALLEL = -2;

	public static int intersectLineLine(Line2D paramLine2D1, Line2D paramLine2D2, Point2D paramPoint2D) {
		double d1 = paramLine2D1.getX1();
		double d2 = paramLine2D1.getY1();
		double d3 = paramLine2D1.getX2();
		double d4 = paramLine2D1.getY2();
		double d5 = paramLine2D2.getX1();
		double d6 = paramLine2D2.getY1();
		double d7 = paramLine2D2.getX2();
		double d8 = paramLine2D2.getY2();
		return intersectLineLine(d1, d2, d3, d4, d5, d6, d7, d8, paramPoint2D);
	}

	public static int intersectLineLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, double paramDouble7, double paramDouble8, Point2D paramPoint2D) {
		double d1 = (paramDouble7 - paramDouble5) * (paramDouble2 - paramDouble6) - (paramDouble8 - paramDouble6) * (paramDouble1 - paramDouble5);
		double d2 = (paramDouble3 - paramDouble1) * (paramDouble2 - paramDouble6) - (paramDouble4 - paramDouble2) * (paramDouble1 - paramDouble5);
		double d3 = (paramDouble8 - paramDouble6) * (paramDouble3 - paramDouble1) - (paramDouble7 - paramDouble5) * (paramDouble4 - paramDouble2);
		if (d3 != 0.0D) {
			double d4 = d1 / d3;
			double d5 = d2 / d3;
			if ((0.0D <= d4) && (d4 <= 1.0D) && (0.0D <= d5) && (d5 <= 1.0D)) {
				paramPoint2D.setLocation(paramDouble1 + d4 * (paramDouble3 - paramDouble1), paramDouble2 + d4 * (paramDouble4 - paramDouble2));
				return 1;
			}
			return 0;
		}
		return (d1 == 0.0D) || (d2 == 0.0D) ? -1 : -2;
	}

	public static int intersectLineRectangle(Point2D paramPoint2D1, Point2D paramPoint2D2, Rectangle2D paramRectangle2D, Point2D[] paramArrayOfPoint2D) {
		double d1 = paramPoint2D1.getX();
		double d2 = paramPoint2D1.getY();
		double d3 = paramPoint2D2.getX();
		double d4 = paramPoint2D2.getY();
		double d5 = paramRectangle2D.getMaxX();
		double d6 = paramRectangle2D.getMaxY();
		double d7 = paramRectangle2D.getMinX();
		double d8 = paramRectangle2D.getMinY();
		if (paramArrayOfPoint2D[0] == null) {
			paramArrayOfPoint2D[0] = new Point2D.Double();
		}
		if (paramArrayOfPoint2D[1] == null) {
			paramArrayOfPoint2D[1] = new Point2D.Double();
		}
		int i = 0;
		if (intersectLineLine(d7, d8, d5, d8, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		if (intersectLineLine(d5, d8, d5, d6, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(d5, d6, d7, d6, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(d7, d6, d7, d8, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		return i;
	}

	public static int intersectLineRectangle(Line2D paramLine2D, Rectangle2D paramRectangle2D, Point2D[] paramArrayOfPoint2D) {
		double d1 = paramLine2D.getX1();
		double d2 = paramLine2D.getY1();
		double d3 = paramLine2D.getX2();
		double d4 = paramLine2D.getY2();
		double d5 = paramRectangle2D.getMaxX();
		double d6 = paramRectangle2D.getMaxY();
		double d7 = paramRectangle2D.getMinX();
		double d8 = paramRectangle2D.getMinY();
		if (paramArrayOfPoint2D[0] == null) {
			paramArrayOfPoint2D[0] = new Point2D.Double();
		}
		if (paramArrayOfPoint2D[1] == null) {
			paramArrayOfPoint2D[1] = new Point2D.Double();
		}
		int i = 0;
		if (intersectLineLine(d7, d8, d5, d8, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		if (intersectLineLine(d5, d8, d5, d6, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(d5, d6, d7, d6, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		if (i == 2) {
			return i;
		}
		if (intersectLineLine(d7, d6, d7, d8, d1, d2, d3, d4, paramArrayOfPoint2D[i]) > 0) {
			i++;
		}
		return i;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/GeometryLib.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */