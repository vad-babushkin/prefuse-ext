package edu.berkeley.guir.prefuse.action.assignment;

import java.awt.Color;
import java.awt.Paint;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.AbstractAction;

/**
 * Simple ColorFunction which returns "black" for the draw color and
 * "gray" for the fill color when a color is requested. Subclasses 
 * should override the getColor() and getFillColor() methods to provide
 * custom color selection functions.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ColorFunction extends AbstractAction {
    
    protected Color defaultColor       = Color.BLACK;
    protected Color focusColor         = Color.RED;
    protected Color highlightColor     = Color.BLUE;
    
    protected Color defaultFillColor   = Color.WHITE;
    protected Color focusFillColor     = Color.WHITE;
    protected Color highlightFillColor = Color.WHITE;
    
	public void run(ItemRegistry registry, double frac) {
		Iterator itemIter = registry.getItems();
		while ( itemIter.hasNext() ) {
			VisualItem item = (VisualItem)itemIter.next();
            Paint c = getColor(item), o = item.getColor();
			if ( o == null ) item.setColor(getInitialColor(item));			
			item.updateColor(c);			
			item.setColor(c);
			
			c = getFillColor(item); o = item.getFillColor();
			if ( o == null ) item.setFillColor(getInitialFillColor(item));
			item.updateFillColor(c);			
			item.setFillColor(c);
		}
	} //

	protected Paint getInitialColor(VisualItem item) {
		return getColor(item);
	} //
	
	protected Paint getInitialFillColor(VisualItem item) {
		return getFillColor(item);
	} //

	public Paint getColor(VisualItem item) {
        if ( item.isFocus() ) {
            return focusColor;
        } else if ( item.isHighlighted() ) {
            return highlightColor;
        } else {
            return defaultColor;   
        }
	} //

	public Paint getFillColor(VisualItem item) {
	    if ( item.isFocus() ) {
            return focusFillColor;
        } else if ( item.isHighlighted() ) {
            return highlightFillColor;
        } else {
            return defaultFillColor;   
        }
	} //

    /**
     * @return Returns the defaultColor.
     */
    public Color getDefaultColor() {
        return defaultColor;
    } //
    
    /**
     * @param defaultColor The defaultColor to set.
     */
    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
    } //
    
    /**
     * @return Returns the defaultFillColor.
     */
    public Color getDefaultFillColor() {
        return defaultFillColor;
    } //
    
    /**
     * @param defaultFillColor The defaultFillColor to set.
     */
    public void setDefaultFillColor(Color defaultFillColor) {
        this.defaultFillColor = defaultFillColor;
    } //
    
    /**
     * @return Returns the focusColor.
     */
    public Color getFocusColor() {
        return focusColor;
    } //
    
    /**
     * @param focusColor The focusColor to set.
     */
    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
    } //
    
    /**
     * @return Returns the focusFillColor.
     */
    public Color getFocusFillColor() {
        return focusFillColor;
    } //
    
    /**
     * @param focusFillColor The focusFillColor to set.
     */
    public void setFocusFillColor(Color focusFillColor) {
        this.focusFillColor = focusFillColor;
    } //
    
    /**
     * @return Returns the highlightColor.
     */
    public Color getHighlightColor() {
        return highlightColor;
    } //
    
    /**
     * @param highlightColor The highlightColor to set.
     */
    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    } //
    
    /**
     * @return Returns the highlightFillColor.
     */
    public Color getHighlightFillColor() {
        return highlightFillColor;
    } //
    
    /**
     * @param highlightFillColor The highlightFillColor to set.
     */
    public void setHighlightFillColor(Color highlightFillColor) {
        this.highlightFillColor = highlightFillColor;
    } //
    
} // end of class ColorFunction
