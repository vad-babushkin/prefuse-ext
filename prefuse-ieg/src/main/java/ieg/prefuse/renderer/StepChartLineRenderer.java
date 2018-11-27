package ieg.prefuse.renderer;

import java.awt.Shape;
import java.awt.geom.Path2D;

import prefuse.visual.VisualItem;

/**
 * <p>
 * Added: 2012-06-14 / AR<br>
 * Modifications: 2012-0X-XX / XX / ...
 * </p>
 * 
 * @author Alexander Rind 
 */
public class StepChartLineRenderer extends LineRenderer {

    private Path2D path = new Path2D.Double();

    @Override
    protected Shape getRawShape(VisualItem item) {
        path.reset();
        path.moveTo(item.getX(), item.getY());
        path.lineTo(item.getDouble(VisualItem.X2), item.getY());
        path.lineTo(item.getDouble(VisualItem.X2), item.getDouble(VisualItem.Y2));
        return path;
    }
}
