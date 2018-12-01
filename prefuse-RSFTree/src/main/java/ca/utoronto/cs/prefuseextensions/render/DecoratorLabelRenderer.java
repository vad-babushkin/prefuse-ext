package ca.utoronto.cs.prefuseextensions.render;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


import prefuse.render.LabelRenderer;
import prefuse.visual.DecoratorItem;
import prefuse.visual.VisualItem;

/**
 * Renders a decorator item if the specified label will fit within the bounds of the 
 * decorated node.
 * 
 * @version 1.1
 * @author <a href="http://www.cs.utoronto.ca/~ccollins">Christopher Collins</a>
 */
public class DecoratorLabelRenderer extends RotationLabelRenderer {
    
    boolean restrictToBounds;
    double minimumRenderSize;
    
    public DecoratorLabelRenderer() {
        super();
    }

    /**
     * An extension of RotationLabelRenderer that only displays labels if they fit within
     * node boundaries.  Labels that are too large are omitted.
     * 
     * @param string the label string to try
     * @param restrictToBounds
     */
    public DecoratorLabelRenderer(String string, boolean restrictToBounds, double minimumRenderSize) {
        super(string);
        this.restrictToBounds = restrictToBounds;
        this.minimumRenderSize = minimumRenderSize;
    }
    
    /**
     * An extension of RotationLabelRenderer that only displays labels if they fit within
     * node boundaries.  Labels that are too large are omitted.
     * 
     * @param string the label string to try
     * @param restrictToBounds
     */
    public DecoratorLabelRenderer(String string, boolean restrictToBounds) {
        super(string);
        this.restrictToBounds = restrictToBounds;
        this.minimumRenderSize = 8.0;
    }

    /**
     * Only render labels that fit within their assigned shape.
     */
    public void render(Graphics2D g, VisualItem item) {
        DecoratorItem dItem = (DecoratorItem) item;
        Rectangle2D itemBounds = dItem.getDecoratedItem().getBounds();
        if (((itemBounds.getWidth() > getRawShape(item).getBounds2D().getWidth()) ||
           (itemBounds.getHeight() > getRawShape(item).getBounds2D().getHeight()) ||
           (!restrictToBounds)) && (g.getFontMetrics(item.getFont()).getHeight() * Math.sqrt(g.getTransform().getDeterminant()) > minimumRenderSize))
            super.render(g, item);
    }
}
