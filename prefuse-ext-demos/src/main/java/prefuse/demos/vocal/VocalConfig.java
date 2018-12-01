package prefuse.demos.vocal;

import java.awt.Color;

import prefuse.Constants;
import prefuse.util.ColorLib;

/**
 * 
 * Default configuration values for VoCAL
 * 
 * @author <code>fnaufel@gmail.com</code>
 * @version 2007-12-02
 *
 */
public class VocalConfig {

	/**
	 * Arc width of rounded corners of node labels of concept trees
	 */
	static public int nodeLabelRoundedArcWidth = 8;
	
	/**
	 * Arc height of rounded corners of node labels of concept trees
	 */
	static public int nodeLabelRoundedArcHeight = 8;
	
	/**
	 * Horizontal alignment of text in node labels of concept trees 
	 */
	static public int nodeLabelHorizontalTextAlignment = prefuse.Constants.LEFT;
	
	/**
	 * Type of node shape drawn in concept tree: outline or fill (or none or both) 
	 */
	static public int nodeLabelRenderType = prefuse.render.AbstractShapeRenderer.RENDER_TYPE_FILL;
	
	/**
	 * Horizontal alignment of node labels of concept trees with respect to their x, y coordinates.
	 */
	static public int nodeLabelHorizontalAlignment = prefuse.Constants.LEFT;
	
	/**
	 * Horizontal padding in pixels between content and border of node labels of concept trees
	 */
	static public int nodeLabelHorizontalPadding = 5;
	
	/**
	 * Vertical padding in pixels between content and border of node labels of concept trees
	 */
	static public int nodeLabelVerticalPadding = 5;
	
	/**
	 * Orientation of concept tree
	 */
	static public int treeOrientation = Constants.ORIENT_LEFT_RIGHT;
	
	/**
	 * Spacing to maintain between depth levels of the tree
	 */
	static public double levelSpacing = 20;
	
	/**
	 * Spacing to maintain between sibling nodes
	 */
	static public double siblingSpacing = 20;
	
	/**
	 * Spacing to maintain between neighboring subtrees
	 */
	static public double subtreeSpacing = 5;
	
	/**
	 * Background color of main panel of UI  
	 */
	static public Color mainPanelBackgroundColor = Color.WHITE;

	/**
	 * Foreground color of main panel of UI  
	 */
	static public Color mainPanelForegroundColor = Color.BLACK;
	
	/**
	 * Fill color of named class nodes
	 */
	static public int namedClassNodeColor = ColorLib.color( Color.orange );
	
	/**
	 * Fill color of not nodes
	 */
	static public int notNodeColor = ColorLib.color( Color.pink );
	
	/**
	 * Fill color of or nodes
	 */
	static public int orNodeColor = ColorLib.color( Color.green );

	/**
	 * Fill color of and nodes
	 */
	static public int andNodeColor = ColorLib.color( Color.lightGray );

	/**
	 * Fill color of only nodes
	 */
	static public int onlyNodeColor = ColorLib.color( Color.yellow );
	
	/**
	 * Fill color of some nodes
	 */
	static public int someNodeColor = ColorLib.color( Color.cyan );

	/**
	 * Fill color of unknown nodes
	 */
	static public int unknownNodeColor = ColorLib.color( Color.magenta );
	
	/**
	 * Text color of node labels
	 */
	static public int nodeTextColor = ColorLib.color( Color.BLACK );
	
	/**
	 * Edge color
	 */
	static public int edgeColor = ColorLib.rgb( 200, 200, 200 );
	
}
