package ca.utoronto.cs.prefuseextensions.layout;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.action.assignment.FontAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.TreeLayout;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.tuple.TupleSet;
import prefuse.render.LabelRenderer;
import prefuse.util.ArrayLib;
import prefuse.util.FontLib;
import prefuse.util.MathLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * TreeLayout instance that computes a radial space filling layout, 
 * laying out subsequent depth levels of a tree on circles of progressively increasing radius.
 *  
 * Based on radial layout implementation for node-link diagrams by jeffrey heer.
 * 
 * @author <a href="http://www.cs.utoronto.ca/~ccollins">Christopher Collins</a>
 */
public class StarburstLayout extends TreeLayout {
    
    private static final int DEFAULT_RADIUS = 50;
    private static final int MARGIN = 30;
    private static final int INNER_ROOT_RADIUS_DEFAULT = 0;
    
    public enum WidthType { FIELD, CHILDCOUNT };
    protected String widthField;

    private int m_maxDepth = 0;
    private double innerRadiusRoot;
    private double radiusInc;
    private double layoutStartAngle, layoutEndAngle;
    private  boolean m_autoScale = true;
    private WidthType widthType = WidthType.CHILDCOUNT;
    
    /**
     * Flag to set the radius of the root node to have the diameter equal the  
     * radius of the of the other nodes (looks better).  Set to false to have
     * the radius equal the radius of the other nodes.
     */
    private boolean rootRadiusHalf = true;
    
    private Point2D m_origin;
    
    /**
     * Creates a new StarburstLayout. Automatic scaling of the radius
     * values to fit the layout bounds is enabled by default.
     * 
     * @param group the data group to process. This should resolve to
     * either a Graph or Tree instance.
     */
    public StarburstLayout(String group) {
        super(group);
        radiusInc = DEFAULT_RADIUS;
        innerRadiusRoot = INNER_ROOT_RADIUS_DEFAULT;
        layoutStartAngle = 0;
        layoutEndAngle = layoutStartAngle + MathLib.TWO_PI;
    }
    
    /**
     * Creates a new RadialTreeLayout using the specified radius increment
     * between levels of the layout. Automatic scaling of the radius values
     * is disabled by default.
     * @param group the data group to process. This should resolve to
     * either a Graph or Tree instance.
     * @param radiusIncrement the radius increment to use between subsequent rings
     * in the layout.
     */
    public StarburstLayout(String group, int radiusIncrement) {
        this(group);
        this.radiusInc = radiusIncrement;
        m_autoScale = false;
    }

    /**
     * Creates a new RadialTreeLayout using the specified radius increment
     * between levels of the layout. Automatic scaling of the radius values
     * is disabled by default.
     * @param group the data group to process. This should resolve to
     * either a Graph or Tree instance.
     * @param radiusIncrement the radius increment to use between subsequent rings
     * in the layout.
     * @param innerRadiusRoot the inner radius of the root node, set to non-zero to create an empty ring in center
     */
    public StarburstLayout(String group, int radiusIncrement, int innerRadiusRoot) {
        this(group);
        this.radiusInc = radiusIncrement;
        this.innerRadiusRoot = innerRadiusRoot;
        m_autoScale = false;
        rootRadiusHalf = false;
    }
    
    /**
     * Set the radius increment to use between concentric circles. Note
     * that this value is used only if auto-scaling is disabled.
     * @return the radius increment between subsequent rings of the layout
     * when auto-scaling is disabled
     */
    public double getRadiusIncrement() {
        return radiusInc;
    }
    
    /**
     * Set the radius increment to use between concentric circles. Note
     * that this value is used only if auto-scaling is disabled.
     * @param radiusIncrement the radius increment between subsequent rings of the layout
     * @see #setAutoScale(boolean)
     */
    public void setRadiusIncrement(double radiusIncrement) {
        radiusInc = radiusIncrement;
    }

    /**
     * Indicates if the layout automatically scales to fit the layout bounds.
     * @return true if auto-scaling is enabled, false otherwise
     */
    public boolean getAutoScale() {
        return m_autoScale;
    }
    
    /**
     * Set whether or not the layout should automatically scale itself
     * to fit the layout bounds.
     * @param s true to automatically scale to fit display, false otherwise
     */
    public void setAutoScale(boolean s) {
        m_autoScale = s;
    }

    /**
     * Set the width type, which defines how angular widths are divided among nodes.  Width can either be calculated based on 
     * the total number of children under a node (default behaviour) or can be based on scores assigned to the node within 
     * a field.  If using scores assigned in a field, the total angular width of the parent is divided among children proportional
     * to their normalized score (scores are normalized among all children of a given node).
     * 
     * @param widthType the new widthType to use for layout
     * @param widthField 
     * 		if widthType FIELD is selected, this is the name of the float data 
     * 		field from which to take the counts for calculating the width such that 
     * 		childValue/parentValue = fraction of parent angle assigned to child
     */
    public void setWidthType(WidthType widthType, String widthField) {
    	this.widthType = widthType;
    	this.widthField = widthField;
    }
    
    public WidthType getWidthType() {
    	return widthType;
    }
    
    /**
     * Constrains this layout to the specified angular sector
     * @param theta the starting angle, in radians
     * @param width the angular width, in radians
     */
    public void setAngularBounds(double theta, double width) {
        layoutStartAngle = theta;
        layoutEndAngle = theta+width;
    }

    /**
     * @see prefuse.action.Action#run(double)
     */
    public void run(double frac) {
        Graph g = (Graph)m_vis.getGroup(m_group);
        if (g.getTupleCount() == 0) return;
        
        initSchema(g.getNodes());
        
        m_origin = getLayoutAnchor();
        NodeItem root = getLayoutRoot();
 
        // calc relative widths and maximum tree depth
        // performs one pass over the tree
        m_maxDepth = 0;
        
        setSize(root);
        
        // calculate the angular width of each node
        calcAngularWidth(root, 1, layoutEndAngle-layoutStartAngle, 0);
        
        if ( m_autoScale ) setScale(getLayoutBounds());
        
        // perform the layout
        if ( m_maxDepth > 0 )
            layout(root, innerRadiusRoot + (rootRadiusHalf ? radiusInc/2 : radiusInc), layoutStartAngle, layoutEndAngle);
        
        // update properties of the root node if we are laying out a Sector
        if (root.canGetDouble("angleExtent")) {
            root.setDouble("innerRadius", innerRadiusRoot);
            // the root has half the radius of other levels
            root.setDouble("outerRadius", innerRadiusRoot + (rootRadiusHalf ? radiusInc / 2 : radiusInc));
        }
        setX(root, null, m_origin.getX());
        setY(root, null, m_origin.getY());
    }
    
    protected void setScale(Rectangle2D bounds) {
        double r = Math.min(bounds.getWidth(),bounds.getHeight())/2.0;
        if ( m_maxDepth > 0 )
            radiusInc = (r-MARGIN)/m_maxDepth;
    }

    /**
     * Recursive calculations of relative measures of the angular widths of each expanded subtree. 
     * This method also updates the start angle value for nodes to ensure proper ordering of nodes.
     * 
     * @param n the node to set
     * @param d the depth of the given node
     * @param width the angular width to assign to n and distribute among n's children
     * @param angle the start angle to set for node n
     */
    private void calcAngularWidth(NodeItem n, int d, double width, double angle) {
        if ( d > m_maxDepth ) m_maxDepth = d;       
        
        if ((double)n.getDouble("size") > 0) {
            Iterator childIter = n.children();
            double startAngle = angle;
            while ( childIter.hasNext() ) {
                NodeItem c = (NodeItem)childIter.next();
                // assign a fraction of the available span based on relative sizes
                double childAW = width * n.getDouble("angleFactor") * ((double)c.getDouble("size") / (double)n.getDouble("size"));
                calcAngularWidth(c, d+1, childAW, startAngle);
                startAngle += childAW;
            }
        }
        n.setDouble("angleExtent", width);
        n.setDouble("startAngle", angle);
    }
    
    /**
     * Recursively count the number of descendants for a given subtree.  Assign
     * this value to the Params.children.
     *  
     * @param n NodeItem rooting the subtree to descend.
     */
    private double setSize(NodeItem n) {
        double size = 0;
        // even non-expanded nodes should contribute to angular width 
        // to reveal presence of large hidden subtrees
        if (n.getChildCount() > 0) {
            Iterator childIterator = n.children();
            while (childIterator.hasNext()) {
                NodeItem c = (NodeItem) childIterator.next();
                size += setSize(c);
            }
        } else {
            // child counts as one node for CHILDCOUNT
        	if (widthType == WidthType.CHILDCOUNT)
            	size = 1;
        	// child counts as it's data value for FIELD
            if (widthType == WidthType.FIELD) 
            	size = n.getFloat(widthField);
        }
        if (n.canGetDouble("angleFactor")) {
            size = size * n.getDouble("angleFactor");
        }
        n.setDouble("size",size);
        return size;
    }
    
    private static final double normalize(double angle) {
        while ( angle > MathLib.TWO_PI ) {
            angle -= MathLib.TWO_PI;
        }
        while ( angle < 0 ) {
            angle += MathLib.TWO_PI;
        }
        return angle;
    }
    
    private void setInnerRadiusRoot(double innerRadiusRoot) {
    	this.innerRadiusRoot = innerRadiusRoot;
    }
    
    private Iterator sortedChildren(final NodeItem n) {
        double base = 0;
        // update base angle for node ordering
        NodeItem p = (NodeItem)n.getParent();
        if ( p != null ) {
            base = normalize(Math.atan2(p.getY()-n.getY(), p.getX()-n.getX()));
        }
        int cc = n.getChildCount();
        if ( cc == 0 ) return null;

        NodeItem c = (NodeItem)n.getFirstChild();
        
        // TODO: this is hacky and will break when filtering
        // how to know that a branch is newly expanded?
        // is there an alternative property we should check?
        if ( !c.isStartVisible() ) {
            // use natural ordering for previously invisible nodes
            return n.children();
        }
        
        double angle[] = new double[cc];
        final int idx[] = new int[cc];
        for ( int i=0; i<cc; ++i, c=(NodeItem)c.getNextSibling() ) {
            idx[i] = i;
            angle[i] = normalize(-base +
                Math.atan2(c.getY()-n.getY(), c.getX()-n.getX()));
        }
        ArrayLib.sort(angle, idx);
        
        // return iterator over sorted children
        return new Iterator() {
            int cur = 0;
            public Object next() {
                return n.getChild(idx[cur++]);
            }
            public boolean hasNext() {
                return cur < idx.length;
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    /**
     * Compute the layout.
     * @param n the root of the current subtree under consideration
     * @param r the radius, current distance from the center
     * @param theta1 the start (in radians) of this subtree's angular region
     * @param theta2 the end (in radians) of this subtree's angular region
     */
    protected void layout(NodeItem n, double r, double theta1, double theta2) {
        double dtheta  = (theta2-theta1);
        double width = n.getDouble("angleExtent");
        double cfrac, nfrac = 0.0;
        
        Iterator childIter = sortedChildren(n);
        while ( childIter != null && childIter.hasNext() ) {
            NodeItem c = (NodeItem)childIter.next();
            cfrac = c.getDouble("angleExtent") / width;
            if ( c.isExpanded() && c.getChildCount()>0 ) {
                layout(c, r+radiusInc, theta1 + nfrac*dtheta, 
                                         theta1 + (nfrac+cfrac)*dtheta);
            }
            
            // set the angular and radial bounds of the sector; assumes if we can
            // set angleExtent, the other members of the parameter set will also be settable
            // unlike standard RadialTreeLayout, we need to store these things for the node renderer (i.e. we aren't 
            // just setting the node position)
            if (c.canSetDouble("angleExtent")) {
                c.setDouble("angleExtent", cfrac * dtheta);
                //c.setDouble("startAngle", c]cp.startAngle);
                c.setDouble("innerRadius", r);
                c.setDouble("outerRadius", r+radiusInc);
            }
            
            // Sectors can be set by (x,y) or by polar coordinates; we use (x,y); polar technique commented out below
            //setPolarLocation(c, n, r, theta1 + nfrac*dtheta + cfrac*dtheta2);
            setX(c, n, m_origin.getX());
            setY(c, n, m_origin.getY());
            nfrac += cfrac;
        }
        
    }

    /**
     * Set the position of the given node, given in polar co-ordinates.
     * @param n the NodeItem to set the position
     * @param p the referrer parent NodeItem
     * @param r the radius
     * @param t the angle theta
     */
    protected void setPolarLocation(NodeItem n, NodeItem p, double r, double t) {
        setX(n, p, r*Math.cos(t));
        setY(n, p, r*Math.sin(t));
    }
    
    /**
     * Set to true to have the diameter of the root equal the radius increment of other nodes.
     */
    public void setRootRadiusHalf(boolean rootRadiusHalf) {
    	this.rootRadiusHalf = rootRadiusHalf;
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////
    ///// Parameters for the layout: stored as a set of parameters in a single column Schema
    ///////////////////////////////////////////////////////////////////////////////////////////
    
    /**
     * The schema for the parameters used by this layout.
     */
    public static final Schema PARAMS_SCHEMA = new Schema();
    static {
        // used by renderers and layout
    	PARAMS_SCHEMA.addColumn("size", double.class);
    	PARAMS_SCHEMA.addColumn("angleFactor", double.class, 1.0);
        PARAMS_SCHEMA.addColumn("startAngle", double.class); 
        PARAMS_SCHEMA.addColumn("angleExtent", double.class);
        PARAMS_SCHEMA.addColumn("innerRadius", double.class);
        PARAMS_SCHEMA.addColumn("outerRadius", double.class); 
    }
    
    protected void initSchema(TupleSet ts) {
        ts.addColumns(PARAMS_SCHEMA);
    }
    
	/**
	 * Set label positions. Labels are assumed to be DecoratorItem instances,
	 * decorating their respective nodes. The layout simply gets the bounds of
	 * the decorated node and assigns the label coordinates to the center of
	 * those bounds.
	 */
	public static class LabelLayout extends Layout {
		
		public LabelLayout(String group) {
			super(group);
		}

		public void run(double frac) {
			Iterator iter = m_vis.items(m_group);
			
			while (iter.hasNext()) {
				DecoratorItem item = (DecoratorItem) iter.next();
				VisualItem node = item.getDecoratedItem();
				Rectangle2D bounds = node.getBounds();
				double angle = 2*Math.PI - item.getDouble("startAngle") - 0.5 * item.getDouble("angleExtent");
				if (angle < 0) 
					System.err.println("< 0 angle specified for " + item.getString("label"));
				if ((item.getDouble("angleExtent") > Math.PI/6)
						&& (item.getDouble("innerRadius") != 0)) {
					// render arched
					item.setDouble("rotation", 0);
				} else {
					// 	render straight
					item.setDouble("rotation", Math.toDegrees(angle));
				}
				setX(item, null, bounds.getCenterX());
				setY(item, null, bounds.getCenterY());
			}
		}
	} // end of inner class LabelLayout

	public static class ScaleFontAction extends FontAction {
		static final double MINFONTHEIGHT = 6.0;
		static final double MAXFONTHEIGHT = 20.0;
		
		private String field;
		
		public ScaleFontAction(String group, String field) {
			super(group);
			this.field = field;
		}

		public double getArcHeight(VisualItem item) {
			// the outer-inner distance between rings minus 2 for borders
			if (item.getDouble("angleExtent") >= 2*Math.PI)
				return 2 * (item.getDouble("outerRadius")
						- item.getDouble("innerRadius") - 4);
			else 
				return (item.getDouble("outerRadius")
						- item.getDouble("innerRadius") - 4);
						
		}

		public double getArcWidth(VisualItem item) {
			// the chord length between two points at midpoint of circle
			double R = (item.getDouble("outerRadius") + item
					.getDouble("innerRadius")) / 2;
			if (item.getDouble("innerRadius") == 0) 
				return 2 * R; // (straight across the middle)
			else 
				return R * item.getDouble("angleExtent");
		}

		public Font getFont(VisualItem item) {
			DecoratorItem dItem = (DecoratorItem) item;
			Font currentFont = (Font) item.getSchema().getDefault(VisualItem.FONT);
			FontMetrics fm = LabelRenderer.DEFAULT_GRAPHICS.getFontMetrics(currentFont);
			if (item.getDouble("rotation") != 0) {
				// scale based on string width and difference between arc inner and outer radii
				double scaleFactor = getArcHeight(dItem.getDecoratedItem()) / fm.stringWidth(dItem.getString(field));
				// ensure scaled height doesn't exceed median arc width
				if (fm.getHeight() * scaleFactor > getArcWidth(dItem))
					scaleFactor = getArcWidth(dItem)/fm.getHeight();
				currentFont = FontLib.getFont(currentFont.getFontName(),
						currentFont.getStyle(), Math.min(currentFont.getSize() * scaleFactor, MAXFONTHEIGHT));
			} else {
				// scale based on string height and difference between arc inner and outer radii
				double scaleFactor = getArcHeight(dItem.getDecoratedItem()) / fm.getHeight();
				// ensure scaled height doesn't exceed median arc width
				if (fm.stringWidth(dItem.getString(field)) * scaleFactor > getArcWidth(dItem))
					scaleFactor = getArcWidth(dItem)/fm.stringWidth(dItem.getString(field));
				// scale is later refined by the renderer
				currentFont = FontLib.getFont(currentFont.getFontName(),
						currentFont.getStyle(), Math.min(currentFont.getSize() * scaleFactor, MAXFONTHEIGHT));
			}
			return currentFont;
		}
	}
} // end of class StarburstLayout
