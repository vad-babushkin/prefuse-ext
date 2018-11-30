package edu.berkeley.guir.prefuse.action.assignment;

import java.awt.Font;
import java.util.Iterator;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.util.FontLib;

/**
 * Simple <code>FontFunction</code> that blindly returns a null 
 * <code>Font</code> for all items. Subclasses should override the 
 * <code>getFont()</code> method to provide custom Font assignment
 * for VisualItems.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class FontFunction extends AbstractAction {

    protected Font defaultFont = FontLib.getFont("SansSerif",Font.PLAIN,10);
    
    public FontFunction() {
        // do nothing
    } //
    
    public FontFunction(Font defaultFont) {
        this.defaultFont = defaultFont;
    } //
    
	public void run(ItemRegistry registry, double frac) {
		Iterator itemIter = registry.getItems();
		while ( itemIter.hasNext() ) {
			VisualItem item = (VisualItem)itemIter.next();
			Font font = getFont(item);
			item.setFont(font);
		}
	} //
	
	public void setDefaultFont(Font f) {
	    defaultFont = f;
	} //
	
	/**
	 * Returns the Font to use for a given VisualItem. Subclasses should
	 * override this method to perform customized font assignment.
	 * @param item the VisualItem for which to get the Font
	 * @return the Font for the given item
	 */
	public Font getFont(VisualItem item) {
		return defaultFont;
	} //

} // end of class FontFunction
