package prefuse.demos.vocal;

import java.awt.geom.Point2D;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ItemAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * 
 * Visual representation of a ConceptTree
 * 
 * @author <code>fnaufel@gmail.com</code>
 * 
 */
public class ConceptTreeView extends Display implements VocalConstants {

	/**
	 * ConceptTree of which this is the visual representation
	 */
	ConceptTree tree;
	
	/**
	 * Visualization associated to this ConceptTreeView. Superclass Display already has a field called m_vis, but we use our own vis field as an alias.
	 */
	Visualization vis;
	
	/**
	 * Custom renderer for nodes of this ConceptTreeView
	 */
	ConceptTreeLabelRenderer labRenderer;

	/**
	 * Renderer for edges of this ConceptTreeView
	 */
	EdgeRenderer edgeRenderer;
	
	/**
	 * Renderer factory for this ConceptTreeView
	 */
	DefaultRendererFactory rendererFactory;
	
	/**
	 * Question: can the multiple Visualizations of multiple ConceptTreeView instances have groups
	 * named "tree"?  
	 *  
	 * @param t Concept tree to be represented
	 */
	public ConceptTreeView( ConceptTree t ) {
		
		super( new Visualization() );
		tree = t;
		vis = m_vis; // m_vis is a field in superclass Display
		
		vis.addTree( "tree", tree );
		
		// renderers
		labRenderer = new ConceptTreeLabelRenderer( this );
		edgeRenderer = new EdgeRenderer( Constants.EDGE_TYPE_CURVE );
		edgeRenderer.setVerticalAlignment1( Constants.CENTER );
		edgeRenderer.setVerticalAlignment2( Constants.CENTER );
		edgeRenderer.setHorizontalAlignment1( Constants.RIGHT );
		edgeRenderer.setHorizontalAlignment2( Constants.LEFT );
		
		rendererFactory = new DefaultRendererFactory( labRenderer );
		rendererFactory.add( new InGroupPredicate( "tree.edges" ), edgeRenderer );
        vis.setRendererFactory( rendererFactory );
		
        // colors 
        ItemAction nodeColor = new NodeColorAction( "tree.nodes" );
        ItemAction textColor = new TextColorAction( "tree.nodes" );
        ItemAction edgeColor = new EdgeColorAction( "tree.edges" );
        vis.putAction( "nodeColor", nodeColor );
        vis.putAction( "textColor", textColor );
        vis.putAction( "edgeColor", edgeColor );

        // create the tree layout action...
        NodeLinkTreeLayout treeLayout = 
        	new NodeLinkTreeLayout( "tree", 
        			VocalConfig.treeOrientation, 
        			VocalConfig.levelSpacing, 
        			VocalConfig.siblingSpacing, 
        			VocalConfig.subtreeSpacing );
        treeLayout.setLayoutAnchor( new Point2D.Double(25,300) );
        vis.putAction( "treeLayout", treeLayout );
        
        // initialize the display and add controls
        setSize(700,600);
        setItemSorter( new TreeDepthItemSorter() );
        addControlListener( new ZoomToFitControl() );
        addControlListener( new ZoomControl() );
        addControlListener( new PanControl() );
        
        // run actions
        vis.run( "treeLayout" );
        vis.run( "nodeColor" );
        vis.run( "textColor" );
        vis.run( "edgeColor" );
        
	}
	
	public ConceptTree getTree() {
		return tree;
	}

	public Visualization getVis() {
		return vis;
	}

	public EdgeRenderer getEdgeRenderer() {
		return edgeRenderer;
	}

	public ConceptTreeLabelRenderer getLabRenderer() {
		return labRenderer;
	}

	public DefaultRendererFactory getRendererFactory() {
		return rendererFactory;
	}

}
