package ieg.prefuse.renderer;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import prefuse.Constants;
import prefuse.action.assignment.SizeAction;
import prefuse.render.AbstractShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

/**
 * Renders a {@link VisualItem} with an interval as a rectangle (e.g., Gantt
 * chart, LifeLines).
 * 
 * <p> The renderer requires that both {@link VisualItem#SIZE} and
 * {@link VisualItem#SIZEY} are set; for example by a {@link SizeAction}.
 * 
 * </p>
 * TODO adapt javadoc The interval is determined by the {@link VisualItem}'s
 * coordinates (by default {@link VisualItem#X} and {@link VisualItem#getX()})
 * and the {@link IntervalBarRenderer}s maxX (see
 * {@link IntervalBarRenderer#IntervalBarRenderer(String)} and
 * {@link IntervalBarRenderer#getMaxXField()}) field. The rendered height is
 * determined by the {@link VisualItem}'s size field and the base size.
 * 
 * <p>
 * Added: 2012-06-13 / AR (based on work by Peter Weishapl)<br>
 * Modifications: 2012-06-13 / AR / no label; height set by size field; y axis<br>
 * 2013-06-12 / AR / width in VisualItem.SIZE instead of custom maxXField
 * </p>
 * 
 * @author Rind, peterw
 * @see IntervalLayout
 */
public class IntervalBarRenderer extends AbstractShapeRenderer {
    protected Rectangle2D bounds = new Rectangle2D.Double();

    private int m_axis = Constants.X_AXIS;

    private int m_baseSize = 10;

    /**
     * Create a {@link IntervalBarRenderer}. Uses the given text data field to
     * draw it's text label and the maxX data field to determine the interval to
     * be rendered, which is from {@link VisualItem}s x data field to the given
     * maxX data field.
     * 
     * @param textField
     *            the data field used for the text label
     * @param maxXField
     *            the data field used for the interval
     */
    public IntervalBarRenderer() {
    }

    public IntervalBarRenderer(int m_baseSize) {
        this.m_baseSize = m_baseSize;
    }

    /*
     * (non-Javadoc)
     * 
     * @see prefuse.render.AbstractShapeRenderer#render(java.awt.Graphics2D,
     * prefuse.visual.VisualItem)
     */
    public void render(Graphics2D g, VisualItem item) {
        renderBackground(g, item);
        renderText(g, item, (int) item.getX());
    }

    /**
     * Renders the background. Override this method to customize background
     * painting.
     * 
     * @param g
     *            graphics object
     * @param item
     *            the item to be rendered
     */
    protected void renderBackground(Graphics2D g, VisualItem item) {
        super.render(g, item);
    }

    /**
     * Renders the text of the given item at the items y and the given x
     * position. The text may exceed the items bounds.
     * 
     * @param g
     *            graphics object
     * @param item
     *            the item to be rendered
     * @param x
     *            the x position of the text
     */
    protected void renderText(Graphics2D g, VisualItem item, int x) {
        // do nothing; override if desired
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * prefuse.render.AbstractShapeRenderer#getRawShape(prefuse.visual.VisualItem
     * )
     */
    protected Shape getRawShape(VisualItem item) {
        double startX = item.getX();
        double startY = item.getY();
        
        // TODO consider to handle the drawRect width glitch globally 
        // fix glitch that a border of stroke width 1 appears right of the area
        float stroke = (ColorLib.alpha(item.getStrokeColor()) > 0
                && super.getRenderType(item) != RENDER_TYPE_FILL 
                && item.getStroke().getLineWidth() > 0) ? 1.0f : 0.0f;

        if (Constants.X_AXIS == m_axis) {
            double width = item.getSize() - stroke;
            double height = m_baseSize * item.getSizeY() - stroke;
            bounds.setFrame(startX, startY - height / 2, width, height);
        } else {
            double width = m_baseSize * item.getSize() - stroke;
            double height = item.getSizeY() - stroke;
            bounds.setFrame(startX - width / 2, startY, width, height);
        }

        return bounds;
    }

    /**
     * Sets the base size, in pixels, for shapes drawn by this renderer. The
     * base size is the width and height value used when a VisualItem's size
     * value is 1. The base size is scaled by the item's size value to arrive at
     * the final scale used for rendering.
     * 
     * @param size
     *            the base size in pixels
     */
    public void setBaseSize(int size) {
        m_baseSize = size;
    }

    /**
     * Returns the base size, in pixels, for shapes drawn by this renderer.
     * 
     * @return the base size in pixels
     */
    public int getBaseSize() {
        return m_baseSize;
    }

    /**
     * Return the axis type of this layout, either
     * {@link prefuse.Constants#X_AXIS} or {@link prefuse.Constants#Y_AXIS}.
     * 
     * @return the axis type of this layout.
     */
    public int getAxis() {
        return m_axis;
    }

    /**
     * Set the axis type of this layout.
     * 
     * @param axis
     *            the axis type to use for this layout, either
     *            {@link prefuse.Constants#X_AXIS} or
     *            {@link prefuse.Constants#Y_AXIS}.
     */
    public void setAxis(int axis) {
        if (axis < 0 || axis >= Constants.AXIS_COUNT)
            throw new IllegalArgumentException("Unrecognized axis value: "
                    + axis);
        m_axis = axis;
    }
}
