package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract interface Renderer {
	public static final Graphics2D DEFAULT_GRAPHICS = (Graphics2D) new BufferedImage(1, 1, 2).getGraphics();

	public abstract void render(Graphics2D paramGraphics2D, VisualItem paramVisualItem);

	public abstract boolean locatePoint(Point2D paramPoint2D, VisualItem paramVisualItem);

	public abstract Rectangle2D getBoundsRef(VisualItem paramVisualItem);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/Renderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */