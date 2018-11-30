package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.util.GeometryLib;

import java.awt.*;
import java.awt.geom.*;

public class DefaultEdgeRenderer
		extends ShapeRenderer {
	public static final String EDGE_TYPE = "edgeType";
	protected static final double HALF_PI = 1.5707963267948966D;
	protected static final Polygon DEFAULT_ARROW_HEAD = new Polygon(new int[]{0, -4, 4, 0}, new int[]{0, -12, -12, 0}, 4);
	public static final int EDGE_TYPE_LINE = 0;
	public static final int EDGE_TYPE_CURVE = 1;
	public static final int WEIGHT_TYPE_NONE = 0;
	public static final int WEIGHT_TYPE_LINEAR = 1;
	public static final int WEIGHT_TYPE_LOG = 2;
	public static final int ALIGNMENT_LEFT = 0;
	public static final int ALIGNMENT_RIGHT = 1;
	public static final int ALIGNMENT_CENTER = 2;
	public static final int ALIGNMENT_BOTTOM = 1;
	public static final int ALIGNMENT_TOP = 0;
	protected Line2D m_line = new Line2D.Float();
	protected CubicCurve2D m_cubic = new CubicCurve2D.Float();
	protected int m_edgeType = 0;
	protected int m_weightType = 1;
	protected int m_xAlign1 = 2;
	protected int m_yAlign1 = 2;
	protected int m_xAlign2 = 2;
	protected int m_yAlign2 = 2;
	protected int m_width = 1;
	protected int m_curWidth = 1;
	protected Point2D[] m_tmpPoints = new Point2D[2];
	protected Point2D[] m_ctrlPoints = new Point2D[2];
	protected Point2D[] m_isctPoints = new Point2D[2];
	protected String m_weightLabel = "weight";
	protected boolean m_directed = false;
	protected Polygon m_arrowHead = DEFAULT_ARROW_HEAD;
	protected AffineTransform m_arrowTrans = new AffineTransform();

	public DefaultEdgeRenderer() {
		this.m_tmpPoints[0] = new Point2D.Float();
		this.m_tmpPoints[1] = new Point2D.Float();
		this.m_ctrlPoints[0] = new Point2D.Float();
		this.m_ctrlPoints[1] = new Point2D.Float();
		this.m_isctPoints[0] = new Point2D.Float();
		this.m_isctPoints[1] = new Point2D.Float();
	}

	public String getWeightAttributeName() {
		return this.m_weightLabel;
	}

	public void setWeightAttributeName(String paramString) {
		this.m_weightLabel = paramString;
	}

	public int getWeightType() {
		return this.m_weightType;
	}

	public void setWeightType(int paramInt) {
		this.m_weightType = paramInt;
	}

	public int getRenderType() {
		if (this.m_directed) {
			return 3;
		}
		return 1;
	}

	protected Shape getRawShape(VisualItem paramVisualItem) {
		EdgeItem localEdgeItem = (EdgeItem) paramVisualItem;
		VisualItem localVisualItem1 = (VisualItem) localEdgeItem.getFirstNode();
		VisualItem localVisualItem2 = (VisualItem) localEdgeItem.getSecondNode();
		String str = (String) localEdgeItem.getVizAttribute("edgeType");
		int i = this.m_edgeType;
		if (str != null) {
			try {
				i = Integer.parseInt(str);
			} catch (Exception localException) {
			}
		}
		getAlignedPoint(this.m_tmpPoints[0], localVisualItem1.getRenderer().getBoundsRef(localVisualItem1), this.m_xAlign1, this.m_yAlign1);
		getAlignedPoint(this.m_tmpPoints[1], localVisualItem2.getRenderer().getBoundsRef(localVisualItem2), this.m_xAlign2, this.m_yAlign2);
		double d1 = this.m_tmpPoints[0].getX();
		double d2 = this.m_tmpPoints[0].getY();
		double d3 = this.m_tmpPoints[1].getX();
		double d4 = this.m_tmpPoints[1].getY();
		this.m_curWidth = getLineWidth(paramVisualItem);
		switch (i) {
			case 0:
				this.m_line.setLine(d1, d2, d3, d4);
				return this.m_line;
			case 1:
				getCurveControlPoints(localEdgeItem, this.m_ctrlPoints, d1, d2, d3, d4);
				this.m_cubic.setCurve(d1, d2, this.m_ctrlPoints[0].getX(), this.m_ctrlPoints[0].getY(), this.m_ctrlPoints[1].getX(), this.m_ctrlPoints[1].getY(), d3, d4);
				return this.m_cubic;
		}
		throw new IllegalStateException("Unknown edge type");
	}

	public void render(Graphics2D paramGraphics2D, VisualItem paramVisualItem) {
		super.render(paramGraphics2D, paramVisualItem);
		EdgeItem localEdgeItem = (EdgeItem) paramVisualItem;
		if (localEdgeItem.isDirected()) {
			Point2D localPoint2D1 = null;
			Point2D localPoint2D2 = null;
			String str = (String) paramVisualItem.getVizAttribute("edgeType");
			int j = this.m_edgeType;
			if (str != null) {
				try {
					j = Integer.parseInt(str);
				} catch (Exception localException) {
				}
			}
			int i;
			switch (j) {
				case 0:
					localPoint2D1 = this.m_tmpPoints[0];
					localPoint2D2 = this.m_tmpPoints[1];
					i = this.m_width;
					break;
				case 1:
					localPoint2D1 = this.m_ctrlPoints[1];
					localPoint2D2 = this.m_tmpPoints[1];
					i = 1;
					break;
				default:
					throw new IllegalStateException("Unknown edge type.");
			}
			VisualItem localVisualItem = (VisualItem) localEdgeItem.getSecondNode();
			Rectangle2D localRectangle2D = localVisualItem.getBounds();
			int k = GeometryLib.intersectLineRectangle(localPoint2D1, localPoint2D2, localRectangle2D, this.m_isctPoints);
			if (k > 0) {
				localPoint2D2 = this.m_isctPoints[0];
			}
			AffineTransform localAffineTransform = getArrowTrans(localPoint2D1, localPoint2D2, i);
			Shape localShape = localAffineTransform.createTransformedShape(this.m_arrowHead);
			paramGraphics2D.setPaint(paramVisualItem.getFillColor());
			paramGraphics2D.fill(localShape);
		}
	}

	protected AffineTransform getArrowTrans(Point2D paramPoint2D1, Point2D paramPoint2D2, int paramInt) {
		this.m_arrowTrans.setToTranslation(paramPoint2D2.getX(), paramPoint2D2.getY());
		this.m_arrowTrans.rotate(-1.5707963267948966D + Math.atan2(paramPoint2D2.getY() - paramPoint2D1.getY(), paramPoint2D2.getX() - paramPoint2D1.getX()));
		if (paramInt > 1) {
			double d = 2.0D * (paramInt - 1) / 4.0D + 1.0D;
			this.m_arrowTrans.scale(d, d);
		}
		return this.m_arrowTrans;
	}

	protected AffineTransform getTransform(VisualItem paramVisualItem) {
		return null;
	}

	public boolean locatePoint(Point2D paramPoint2D, VisualItem paramVisualItem) {
		Shape localShape = getShape(paramVisualItem);
		if (localShape == null) {
			return false;
		}
		double d1 = Math.max(2, getLineWidth(paramVisualItem));
		double d2 = d1 / 2.0D;
		return localShape.intersects(paramPoint2D.getX() - d2, paramPoint2D.getY() - d2, d1, d1);
	}

	protected int getLineWidth(VisualItem paramVisualItem) {
		if (this.m_weightType == 0) {
			return this.m_width;
		}
		String str = paramVisualItem.getAttribute(this.m_weightLabel);
		if (str != null) {
			try {
				double d = Double.parseDouble(str);
				if (this.m_weightType == 1) {
					return (int) Math.round(d);
				}
				if (this.m_weightType == 2) {
					return Math.max(1, 1 + (int) Math.round(Math.log(d)));
				}
			} catch (Exception localException) {
				System.err.println("Weight value is not a valid number!");
				localException.printStackTrace();
			}
		}
		return this.m_width;
	}

	protected BasicStroke getStroke(VisualItem paramVisualItem) {
		return this.m_curWidth == 1 ? null : new BasicStroke(this.m_curWidth);
	}

	protected void getCurveControlPoints(EdgeItem paramEdgeItem, Point2D[] paramArrayOfPoint2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4) {
		double d1 = paramDouble3 - paramDouble1;
		double d2 = paramDouble4 - paramDouble2;
		paramArrayOfPoint2D[0].setLocation(paramDouble1 + 2.0D * d1 / 3.0D, paramDouble2);
		paramArrayOfPoint2D[1].setLocation(paramDouble3 - d1 / 8.0D, paramDouble4 - d2 / 8.0D);
	}

	protected static void getAlignedPoint(Point2D paramPoint2D, Rectangle2D paramRectangle2D, int paramInt1, int paramInt2) {
		double d1 = paramRectangle2D.getX();
		double d2 = paramRectangle2D.getY();
		double d3 = paramRectangle2D.getWidth();
		double d4 = paramRectangle2D.getHeight();
		if (paramInt1 == 2) {
			d1 += d3 / 2.0D;
		} else if (paramInt1 == 1) {
			d1 += d3;
		}
		if (paramInt2 == 2) {
			d2 += d4 / 2.0D;
		} else if (paramInt2 == 1) {
			d2 += d4;
		}
		paramPoint2D.setLocation(d1, d2);
	}

	public int getEdgeType() {
		return this.m_edgeType;
	}

	public void setEdgeType(int paramInt) {
		this.m_edgeType = paramInt;
	}

	public int getHorizontalAlignment1() {
		return this.m_xAlign1;
	}

	public int getVerticalAlignment1() {
		return this.m_yAlign1;
	}

	public int getHorizontalAlignment2() {
		return this.m_xAlign2;
	}

	public int getVerticalAlignment2() {
		return this.m_yAlign2;
	}

	public void setHorizontalAlignment1(int paramInt) {
		this.m_xAlign1 = paramInt;
	}

	public void setVerticalAlignment1(int paramInt) {
		this.m_yAlign1 = paramInt;
	}

	public void setHorizontalAlignment2(int paramInt) {
		this.m_xAlign2 = paramInt;
	}

	public void setVerticalAlignment2(int paramInt) {
		this.m_yAlign2 = paramInt;
	}

	public void setWidth(int paramInt) {
		this.m_width = paramInt;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/DefaultEdgeRenderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */