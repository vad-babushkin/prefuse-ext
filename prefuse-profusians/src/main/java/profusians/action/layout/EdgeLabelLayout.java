package profusians.action.layout;

import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import prefuse.Visualization;
import prefuse.action.layout.Layout;
import prefuse.data.Schema;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.visual.DecoratorItem;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

public class EdgeLabelLayout extends Layout {

    private static final String EDGE_DECORATORS = "edgeDeco";

    private static final int textColor = ColorLib.rgba(100, 100, 100, 200);

    private static final Font textFont = FontLib.getFont("Tahoma", 11);

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     String field) {
	this(vis, drf, edges, field, textColor, textFont);
    }

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     LabelRenderer renderer) {
	this(vis, drf, edges, renderer, textColor, textFont);
    }

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     String field, int color) {
	this(vis, drf, edges, field, color, textFont);
    }

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     LabelRenderer renderer, int color) {
	this(vis, drf, edges, renderer, color, textFont);
    }

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     String field, Font font) {
	this(vis, drf, edges, field, textColor, font);
    }

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     LabelRenderer renderer, Font font) {
	this(vis, drf, edges, renderer, textColor, font);
    }

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     String field, int color, Font font) {
	this(vis, drf, edges, new LabelRenderer(field), color, font);
    }

    public EdgeLabelLayout( Visualization vis,
	     DefaultRendererFactory drf, String edges,
	     LabelRenderer renderer, int color, Font font) {
	super(EDGE_DECORATORS);

	setVisualization(vis);

	 Schema EDGE_DECORATOR_SCHEMA = PrefuseLib.getVisualItemSchema();

	EDGE_DECORATOR_SCHEMA.setDefault(VisualItem.INTERACTIVE, false); // noninteractive
	EDGE_DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, textColor);
	EDGE_DECORATOR_SCHEMA.setDefault(VisualItem.FONT, textFont);
	m_vis.addDecorators(EDGE_DECORATORS, edges, EDGE_DECORATOR_SCHEMA);

	drf.add(new InGroupPredicate(EDGE_DECORATORS), renderer);

    }

    public void run( double frac) {
	 Iterator iter = m_vis.items(m_group);
	while (iter.hasNext()) {
	     DecoratorItem decorator = (DecoratorItem) iter.next();
	     EdgeItem decoratedEdgeItem = (EdgeItem) decorator
		    .getDecoratedItem();

	    if (decoratedEdgeItem.isVisible()) {
		decorator.setVisible(true);
		 Rectangle2D bounds = decoratedEdgeItem.getBounds();
		setX(decorator, null, getX(decoratedEdgeItem, bounds));
		setY(decorator, null, getY(decoratedEdgeItem, bounds));
	    } else {
		decorator.setVisible(false);
	    }
	}

    }

    protected double getX( VisualItem decoratedItem,
	     Rectangle2D bounds) {
	return bounds.getCenterX();
    }

    protected double getY( VisualItem decoratedItem,
	     Rectangle2D bounds) {
	return bounds.getCenterY();
    }

}
