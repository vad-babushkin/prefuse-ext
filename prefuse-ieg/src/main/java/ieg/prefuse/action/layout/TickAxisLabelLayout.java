package ieg.prefuse.action.layout;

import java.awt.Insets;
import java.awt.geom.Rectangle2D;

import prefuse.Display;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;

/**
 * A variant of {@link AxisLabelLayout} that has only small tick marks instead
 * of grid lines.
 * 
 * @author Rind
 */
public class TickAxisLabelLayout extends AxisLabelLayout {

    private int width;

    /**
     * Create a new AxisLabelLayout layout that has only small tick marks
     * instead of grid lines.
     * 
     * @param group
     *            the data group of the axis lines and labels
     * @param layout
     *            an {@link AxisLayout} instance to model this layout after.
     * @param width
     *            length of the tick mark
     */
    public TickAxisLabelLayout(String group, AxisLayout layout, int width) {
        super(group, layout);
        this.width = width;
    }

    @Override
    public Rectangle2D getLayoutBounds() {
        if (m_bounds != null)
            return m_bounds;

        if (m_vis != null && m_vis.getDisplayCount() > 0) {
            Display d = m_vis.getDisplay(0);
            Insets i = m_margin ? m_insets : d.getInsets(m_insets);
            m_bpts[0] = i.left;
            m_bpts[1] = i.top;
            m_bpts[2] = width + i.left;
            m_bpts[3] = d.getHeight() - i.bottom;
            d.getInverseTransform().transform(m_bpts, 0, m_bpts, 0, 2);
            m_tmpb.setRect(m_bpts[0], m_bpts[1], m_bpts[2] - m_bpts[0],
                    m_bpts[3] - m_bpts[1]);
            return m_tmpb;
        } else {
            return null;
        }
    }
}
