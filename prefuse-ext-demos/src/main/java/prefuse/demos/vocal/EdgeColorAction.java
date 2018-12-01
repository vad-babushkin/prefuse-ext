package prefuse.demos.vocal;

import prefuse.action.assignment.ColorAction;
import prefuse.visual.VisualItem;

public class EdgeColorAction extends ColorAction {
	
	public EdgeColorAction( String group ) {
		super( group, VisualItem.STROKECOLOR );
	}
	
	public int getColor(VisualItem item) {
		return VocalConfig.edgeColor;
	}

}
