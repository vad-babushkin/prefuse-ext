package ieg.prefuse.renderer;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.VisualItem;

/**
 * <p>
 * Added: 2012-06-14 / AR<br>
 * Modifications: 2012-0X-XX / XX / ...
 * </p>
 * 
 * @author Alexander Rind 
 */
public class RectangleRenderer extends AbstractShapeRenderer {

    public static final String WIDTH = "_width";
    public static final String HEIGHT = "_height";

    Rectangle2D rect = new Rectangle2D.Double();

    @Override
    protected Shape getRawShape(VisualItem item) {
        rect.setFrame(item.getX(), item.getY(), item.getDouble(WIDTH),
                item.getDouble(HEIGHT));
        return rect;
    }

}
