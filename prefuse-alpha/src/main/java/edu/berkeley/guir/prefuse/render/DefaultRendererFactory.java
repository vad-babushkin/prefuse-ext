package edu.berkeley.guir.prefuse.render;

import java.util.HashMap;
import java.util.Map;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;

/**
 * Default factory from which to retrieve VisualItem renderers. Assumes only one
 * type of renderer each for NodeItems, EdgeItems, and AggregateItems.
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class DefaultRendererFactory implements RendererFactory {

    private Map itemClassMap;

	/**
	 * Default constructor. Assumes default renderers for each VisualItem type.
	 */
	public DefaultRendererFactory() {
		this(new DefaultNodeRenderer(),
		     new DefaultEdgeRenderer(),
		     null);
	} //
	
	/**
	 * Constructor.
	 * @param nodeRenderer the Renderer to use for NodeItems
	 */
	public DefaultRendererFactory(Renderer nodeRenderer)
	{
	    this(nodeRenderer, null, null);
	} //
	
	/**
	 * Constructor.
	 * @param nodeRenderer the Renderer to use for NodeItems
	 * @param edgeRenderer the Renderer to use for EdgeItems
	 */
	public DefaultRendererFactory(Renderer nodeRenderer, 
								  Renderer edgeRenderer)
	{
	    this(nodeRenderer, edgeRenderer, null);
	} //
	
	/**
	 * Constructor.
	 * @param nodeRenderer the Renderer to use for NodeItems
	 * @param edgeRenderer the Renderer to use for EdgeItems
	 * @param aggrRenderer the Renderer to use for AggregateItems
	 */
	public DefaultRendererFactory(Renderer nodeRenderer, 
								  Renderer edgeRenderer, 
								  Renderer aggrRenderer)
	{
	    itemClassMap = new HashMap();
	    if ( nodeRenderer != null ) {
	        itemClassMap.put(ItemRegistry.DEFAULT_NODE_CLASS, nodeRenderer);
	    }
	    if ( edgeRenderer != null ) {
	        itemClassMap.put(ItemRegistry.DEFAULT_EDGE_CLASS, edgeRenderer);
	    }
	    if ( aggrRenderer != null ) {
	        itemClassMap.put(ItemRegistry.DEFAULT_AGGR_CLASS, aggrRenderer);
	    }
	} //

	/**
	 * @see edu.berkeley.guir.prefuse.render.RendererFactory#getRenderer(edu.berkeley.guir.prefuse.VisualItem)
	 */
	public Renderer getRenderer(VisualItem item) {
	    return (Renderer)itemClassMap.get(item.getItemClass());
	} //
	
	public Renderer getRenderer(String itemClass) {
	    return (Renderer)itemClassMap.get(itemClass);
	} //
	
	public void addRenderer(String itemClass, Renderer renderer) {
	    itemClassMap.put(itemClass, renderer);
	} //
	
	public Renderer removeRenderer(String itemClass) {
	    return (Renderer)itemClassMap.remove(itemClass);
	} //
	
	/**
     * Returns the Renderer for AggregateItems
	 * @return the Renderer for AggregateItems
	 */
	public Renderer getAggregateRenderer() {
		return (Renderer)itemClassMap.get(ItemRegistry.DEFAULT_AGGR_CLASS);
	} //

	/**
     * Returns the Renderer for EdgeItems
	 * @return the Renderer for EdgeItems
	 */
	public Renderer getEdgeRenderer() {
	    return (Renderer)itemClassMap.get(ItemRegistry.DEFAULT_EDGE_CLASS);
	} //

	/**
     * Returns the Renderer for NodeItems
	 * @return the Renderer for NodeItems
	 */
	public Renderer getNodeRenderer() {
	    return (Renderer)itemClassMap.get(ItemRegistry.DEFAULT_NODE_CLASS);
	} //

	/**
     * Sets the Renderer for AggregateItems
	 * @param renderer the new Renderer for AggregateItems
	 */
	public void setAggregateRenderer(Renderer renderer) {
	    itemClassMap.put(ItemRegistry.DEFAULT_AGGR_CLASS, renderer);
	} //

	/**
     * Sets the Renderer for EdgeItems
	 * @param renderer the new Renderer for EdgeItems
	 */
	public void setEdgeRenderer(Renderer renderer) {
	    itemClassMap.put(ItemRegistry.DEFAULT_EDGE_CLASS, renderer);
	} //

	/**
     * Sets the Renderer for NodeItems
	 * @param renderer the new Renderer for NodeItems
	 */
	public void setNodeRenderer(Renderer renderer) {
	    itemClassMap.put(ItemRegistry.DEFAULT_NODE_CLASS, renderer);
	} //

} // end of class DefaultRendererFactory
