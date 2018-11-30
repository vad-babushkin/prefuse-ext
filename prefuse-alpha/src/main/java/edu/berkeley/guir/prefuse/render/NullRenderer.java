package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class NullRenderer
		implements Renderer {
	Rectangle2D r = new Rectangle2D.Double(-1.0D, -1.0D, 0.0D, 0.0D);

	public void render(Graphics2D paramGraphics2D, VisualItem paramVisualItem) {
	}

	public boolean locatePoint(Point2D paramPoint2D, VisualItem paramVisualItem) {
		return false;
	}

	public Rectangle2D getBoundsRef(VisualItem paramVisualItem) {
		this.r.setRect(paramVisualItem.getX(), paramVisualItem.getY(), 0.0D, 0.0D);
		return this.r;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/NullRenderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */