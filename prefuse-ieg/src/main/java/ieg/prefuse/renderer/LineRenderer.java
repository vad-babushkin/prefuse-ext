package ieg.prefuse.renderer;

import java.awt.Shape;
import java.awt.geom.Line2D;

import prefuse.render.AbstractShapeRenderer;
import prefuse.visual.VisualItem;

// TODO subclass with bezier curves (cp. EdgeRenderer)
/**
 * <p>
 * Added: 2012-06-14 / AR (based on LineChart example by Jeff Heer)<br>
 * Modifications: 2012-0X-XX / XX / ...
 * </p>
 * 
 * @author Alexander Rind (based on LineChart example by Jeff Heer)
 */
public class LineRenderer extends AbstractShapeRenderer {

    private Line2D m_line = new Line2D.Double();

    // create Shape that draws a line
    @Override
    protected Shape getRawShape(VisualItem item) {
        m_line.setLine(item.getX(), item.getY(), item.getDouble(VisualItem.X2),
                item.getDouble(VisualItem.Y2));
        return m_line;
    }
}
