package ieg.prefuse.controls;

import java.awt.event.MouseEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import prefuse.Display;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;

/**
 * process items that the mouse drags over. However, this is not a rectangular 
 * brush. 
 * 
 * <p>
 * If subclasses overwrite event methods, they must call their super methods.
 * 
 * <p>
 * Performance is affected mainly by {@link Display#findItem(java.awt.Point)}.
 * For dragging events prefuse does not call this method and our approach calls
 * it only if a brush control was added. Thus, this approach is more efficient
 * than changing prefuse.
 * 
 * @author Rind
 */
public abstract class AbstractBrushControl extends ControlAdapter {

    private Set<VisualItem> items = new LinkedHashSet<VisualItem>();
    private boolean startOnBackground = true;
    private int m_button;

    /**
     * Create a new AbstractBrushControl with the left mouse button.
     */
    public AbstractBrushControl() {
        this(LEFT_MOUSE_BUTTON, true);
    }

    /**
     * Create a new AbstractBrushControl.
     * 
     * @param mouseButton
     *            the mouse button that should initiate a brush. One of
     *            {@link Control#LEFT_MOUSE_BUTTON},
     *            {@link Control#MIDDLE_MOUSE_BUTTON}, or
     *            {@link Control#RIGHT_MOUSE_BUTTON}.
     * @param startOnBackground
     *            if false, the brush control needs to be started on a visual
     *            item (useful if combined with a PanControl).
     */
    public AbstractBrushControl(int mouseButton, boolean startOnBackground) {
        this.startOnBackground = startOnBackground;
        this.m_button = mouseButton;
    }

    /**
     * Invoked when the mouse is dragged over a VisualItem that is not yet in
     * the item set.
     */
    public abstract void brushedItemAdded(VisualItem item, MouseEvent e);

    /**
     * Invoked when the brush is completed, i.e., the mouse button is released.
     * The item set might be empty.
     */
    public abstract void brushComplete(Set<VisualItem> items, MouseEvent e);

    public Set<VisualItem> getBrushedItems() {
        return items;
    }

    @Override
    public void itemPressed(VisualItem item, MouseEvent e) {
        pressed(item, e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (startOnBackground)
            pressed(null, e);
    }

    @Override
    public void itemReleased(VisualItem item, MouseEvent e) {
        released(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (startOnBackground)
            released(e);
    }

    @Override
    public void itemDragged(VisualItem item, MouseEvent e) {
        dragged(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (startOnBackground)
            dragged(e);
    }

    private void pressed(VisualItem item, MouseEvent e) {
        if (UILib.isButtonPressed(e, m_button)) {
            items.clear();
            if (item != null) {
                items.add(item);
                brushedItemAdded(item, e);
            }
        }
    }

    private void released(MouseEvent e) {
        if (UILib.isButtonPressed(e, m_button))
            brushComplete(items, e);
    }

    private void dragged(MouseEvent e) {
        if (UILib.isButtonPressed(e, m_button)) {
            Display d = (Display) e.getComponent();
            // potential to improve performance: check if over an added item 
            VisualItem vi = d.findItem(e.getPoint());
            if (vi != null)
                if (items.add(vi))
                    brushedItemAdded(vi, e);
        }
    }
}

/*
// alternative implementation with Swing events 
//      BrushControl brush = new BrushControl(); 
//      super.m_dataDisplay.addMouseMotionListener(brush);
//      super.m_dataDisplay.addMouseListener(brush);
public class BrushControl extends MouseAdapter {
    private Set<VisualItem> items = new HashSet<VisualItem>();
    @Override
    public void mousePressed(MouseEvent e) {
        items.clear();
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("brushed items: " + Arrays.toString(items.toArray()));
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        Display d = (Display) e.getComponent();
        VisualItem vi = d.findItem(e.getPoint());
        if (vi != null)
            if (items.add(vi))
                System.out.println("added to brush: " + vi);
    }
}
*/
