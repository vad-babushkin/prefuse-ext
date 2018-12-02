/**
 * 
 */
package vocal;

import prefuse.action.assignment.ColorAction;
import prefuse.visual.VisualItem;

/**
 * @author fnaufel
 *
 */
public class TextColorAction extends ColorAction {

	public TextColorAction(String group) {
		super( group, VisualItem.TEXTCOLOR );
	}
	
	public int getColor(VisualItem item) {
		return VocalConfig.nodeTextColor;
	}


}
