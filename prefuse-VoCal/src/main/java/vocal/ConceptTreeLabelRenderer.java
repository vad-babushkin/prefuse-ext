package vocal;

import java.util.Iterator;

import prefuse.data.Node;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

/**
 *
 * Custom renderer for nodes of ConceptTreeViews
 *
 * @author <code>fnaufel@gmail.com</code>
 * @version 2007-12-02
 *
 */
public class ConceptTreeLabelRenderer extends LabelRenderer implements VocalConstants {

	ConceptTreeView treeview;

	public ConceptTreeLabelRenderer( ConceptTreeView tv ) {

		treeview = tv;

		// Create and fill new column with node text
		createLabelTextColumn();

		// TODO: turn into VocalConfig field
		setRenderType( AbstractShapeRenderer.RENDER_TYPE_FILL );
		setRoundedCorner( VocalConfig.nodeLabelRoundedArcWidth, VocalConfig.nodeLabelRoundedArcHeight );
		setHorizontalTextAlignment( VocalConfig.nodeLabelHorizontalTextAlignment );
		setRenderType( VocalConfig.nodeLabelRenderType );
        setHorizontalAlignment( VocalConfig.nodeLabelHorizontalAlignment );
        setHorizontalPadding( VocalConfig.nodeLabelHorizontalPadding );
        setVerticalPadding( VocalConfig.nodeLabelVerticalPadding );

	}

	protected void createLabelTextColumn() {

		try {
			treeview.getTree().addColumn( "labelText", java.lang.Class.forName( "java.lang.String" ) );
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		for( Iterator i = treeview.getTree().nodes(); i.hasNext(); ){

			Node item = (Node) i.next();

			String s = "";

			if( item.getString( "contents" ).equals( "namedClass" ) )
				s = item.getString( "conceptNames" );
			else
			if( item.getString( "contents" ).equals( "not" ) ) {
				if( item.getString( "connection" ) == "edges" )
					s = "NOT";
				else
					s = "NOT " + item.getString( "conceptNames" );
			}
			else
			if( item.getString( "contents" ).equals( "or" ) ) {
				if( item.getString( "connection" ).equals( "edges" ) )
					s = "OR";
				else
					s = item.getString( "conceptNames" ).replace( ",", "\nOR " );
			}
			else
	    	if( item.getString( "contents" ).equals( "and" ) ) {
				if( item.getString( "connection" ).equals( "edges" ) )
					s = "AND";
				else
					s = item.getString( "conceptNames" ).replace( ",", "\nAND " );
			}
	    	else
	    	if( item.getString( "contents" ).equals( "only" ) ) {
	    		s = item.getString( "roleNames" ) + " ONLY";
	    	}
	    	else
			if( item.getString( "contents" ).equals( "some" ) ) {
	    		s = item.getString( "roleNames" ) + " SOME";
	    	}
			else
			if( item.getString( "contents" ).equals( "onlysome" ) ) {
	    		s = item.getString( "roleNames" ) + " ONLYSOME";
	    	}
	    	if( item.getString( "contents" ).equals( "max" ) ) {
	    		s = item.getString( "roleNames" ) + " MAX " + item.getString( "numbers" );
	    	}
			else
			if( item.getString( "contents" ).equals( "min" ) ) {
	        	s = item.getString( "roleNames" ) + " MIN " + item.getString( "numbers" );
	        }
			else
			if( item.getString( "contents" ).equals( "exactly" ) ) {
	        	s = item.getString( "roleNames" ) + " EXACTLY " + item.getString( "numbers" );
	        }
			else
			if( item.getString( "contents" ).equals( "oneOf" ) ) {
				s = item.getString( "conceptNames" ).replace( ",", ",\n" );
			}

	    	item.set( "labelText", s );
		}

	}

	protected String getText( VisualItem item ) {

		return (String) item.get( "labelText" );

	}

}
