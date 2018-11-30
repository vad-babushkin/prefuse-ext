package edu.berkeley.guir.prefusex.controls;

import java.awt.event.MouseEvent;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

/**
 * Enables tooltip display for items based on mouse hover.
 *  
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ToolTipControl extends ControlAdapter {

    private String label;
    
    /**
     * Default constructor. Uses "label" as the attribute to use for
     * the tooltip text.
     */
    public ToolTipControl() {
        this("label");
    } //
    
    /**
     * Constructor with specified label attribute.
     * @param labelAttr the attribute name to use for the tooltip text
     */
    public ToolTipControl(String labelAttr) {
        label = labelAttr;
    } //
    
    public void itemEntered(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setToolTipText(item.getAttribute(label));
    } //
    
    public void itemExited(VisualItem item, MouseEvent e) {
        Display d = (Display)e.getSource();
        d.setToolTipText(null);
    } //
    
} // end of class ToolTipControl
