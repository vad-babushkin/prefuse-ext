package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public abstract class ShapeRenderer
		implements Renderer {
	public static final int RENDER_TYPE_NONE = 0;
	public static final int RENDER_TYPE_DRAW = 1;
	public static final int RENDER_TYPE_FILL = 2;
	public static final int RENDER_TYPE_DRAW_AND_FILL = 3;
	private int m_renderType = 3;
	protected AffineTransform m_transform = new AffineTransform();

	public void render(Graphics2D paramGraphics2D, VisualItem paramVisualItem) {
		Shape localShape = getShape(paramVisualItem);
		if (localShape != null) {
			drawShape(paramGraphics2D, paramVisualItem, localShape);
		}
	}

	protected void drawShape(Graphics2D paramGraphics2D, VisualItem paramVisualItem, Shape paramShape) {
		Paint localPaint1 = paramVisualItem.getColor();
		Paint localPaint2 = paramVisualItem.getFillColor();
		Stroke localStroke = paramGraphics2D.getStroke();
		BasicStroke localBasicStroke = getStroke(paramVisualItem);
		if (localBasicStroke != null) {
			paramGraphics2D.setStroke(localBasicStroke);
		}
		switch (getRenderType(paramVisualItem)) {
			case 1:
				paramGraphics2D.setPaint(localPaint1);
				paramGraphics2D.draw(paramShape);
				break;
			case 2:
				paramGraphics2D.setPaint(localPaint2);
				paramGraphics2D.fill(paramShape);
				break;
			case 3:
				paramGraphics2D.setPaint(localPaint2);
				paramGraphics2D.fill(paramShape);
				paramGraphics2D.setPaint(localPaint1);
				paramGraphics2D.draw(paramShape);
		}
		paramGraphics2D.setStroke(localStroke);
	}

	public Shape getShape(VisualItem paramVisualItem) {
		AffineTransform localAffineTransform = getTransform(paramVisualItem);
		Shape localShape = getRawShape(paramVisualItem);
		return localAffineTransform == null ? localShape : localAffineTransform.createTransformedShape(localShape);
	}

	protected abstract Shape getRawShape(VisualItem paramVisualItem);

	protected BasicStroke getStroke(VisualItem paramVisualItem) {
		return null;
	}

	protected AffineTransform getTransform(VisualItem paramVisualItem) {
		return null;
	}

	public int getRenderType(VisualItem paramVisualItem) {
		return this.m_renderType;
	}

	public void setRenderType(int paramInt) {
		if ((paramInt < 0) || (paramInt > 3)) {
			throw new IllegalArgumentException("Unrecognized render type.");
		}
		this.m_renderType = paramInt;
	}

	public boolean locatePoint(Point2D paramPoint2D, VisualItem paramVisualItem) {
		Shape localShape = getShape(paramVisualItem);
		return localShape != null ? localShape.contains(paramPoint2D) : false;
	}

	public Rectangle2D getBoundsRef(VisualItem paramVisualItem) {
		Shape localShape = getShape(paramVisualItem);
		if (localShape == null) {
			return new Rectangle(-1, -1, 0, 0);
		}
		Rectangle2D localRectangle2D = localShape.getBounds2D();
		BasicStroke localBasicStroke = getStroke(paramVisualItem);
		if (localBasicStroke != null) {
			double d1 = localBasicStroke.getLineWidth();
			double d2 = d1 / 2.0D;
			localRectangle2D.setFrame(localRectangle2D.getX() - d2, localRectangle2D.getY() - d2, localRectangle2D.getWidth() + d1, localRectangle2D.getHeight() + d1);
		}
		return localRectangle2D;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/ShapeRenderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */