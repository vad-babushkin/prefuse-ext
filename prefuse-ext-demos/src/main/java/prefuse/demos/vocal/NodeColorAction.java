/**
 * 
 */
package prefuse.demos.vocal;

import prefuse.action.assignment.ColorAction;
import prefuse.visual.VisualItem;

/**
 * 
 * Defines the fill colors of concept tree nodes, depending on their contents. 
 * 
 * @author fnaufel
 *
 */
public class NodeColorAction extends ColorAction {

	public NodeColorAction( String group ) {
        super( group, VisualItem.FILLCOLOR );
    }
	
	public int getColor(VisualItem item) {
		
    	if( item.getString( "contents" ).equals( "namedClass" ) ) return VocalConfig.namedClassNodeColor; 
    	
    	if( item.getString( "contents" ).equals( "not" ) ) return VocalConfig.notNodeColor;
    	
    	if( item.getString( "contents" ).equals( "or" ) ) return VocalConfig.orNodeColor;
    	
    	if( item.getString( "contents" ).equals( "and" ) ) return VocalConfig.andNodeColor;
    	
    	if( item.getString( "contents" ).equals( "only" ) ) return VocalConfig.onlyNodeColor;
    	
    	if( item.getString( "contents" ).equals( "some" ) ) return VocalConfig.someNodeColor;
    	
    	return VocalConfig.unknownNodeColor;
		
	}
	
}
