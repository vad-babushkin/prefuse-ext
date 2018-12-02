package prefuse.demos.idot.util;

import prefuse.demos.idot.Config;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import prefuse.Constants;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.StringLib;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * A renderer for rendering text on multiple lines. Lines in input should
 * be separated by a literal "\n", i.e. not the newline character '\n'. 
 **/
public class MultilineTextItemRenderer extends LabelRenderer {	
	/** text lines should be centered */
	public final static int CENTER = 0;
	
	/** text lines should be aligned to the left */
	public final static int LEFT = 1;
	
	/** text lines should be aligned to the right */
	public final static int RIGHT = 2;
	
	/**
	 * A regular expression for matching a literal \n in an XML file
	 */ 
	protected String lineDelimiter = "\\\\n";
	
	/** the horizontal alignment of text lines */
	protected int textAlign;

	/** whether we should use peripheries around shapes or not */
	private boolean usePeripheries;

	private RectangularShape rectangleBounds;
	private RectangularShape ellipseBounds;
	
	/**
	 * Creates a renderer for rendering text on multiple lines. The lines
	 * are aligned to the left by default
	 */
	public MultilineTextItemRenderer() {
		this(LEFT);
	}
	
	/**
	 * Creates a renderer for rendering text on multiple lines. 
	 * 
	 * @param textAlign horizontal alignment of text, either CENTER, LEFT or RIGHT
	 */
	public MultilineTextItemRenderer(int textAlign) {
		this.textAlign = textAlign;

		ellipseBounds = new Ellipse2D.Double();		
		enablePeripheries();
		rectangleBounds = m_bbox;
	}
	
	/**
	 * Enable the use of peripheries around shapes. After this method has
	 * been called, the "peripheries" attributes of each node to be rendered
	 * will be checked and peripheries will be drawn accordingly
	 */
	public void enablePeripheries() {
		usePeripheries = true;
		m_bbox = new PeripherieShape(m_bbox, 0);
		ellipseBounds = new PeripherieShape(ellipseBounds, 0);
	}
	
	/**
	 * Returns the horizontal alignment of text
	 * 
	 * @return the alignment of text
	 */
	public int getTextAlign() {
		return textAlign;
	}
	
	/**
	 * Sets the horizontal alignment of text
	 * 
	 * @param textAlign the alignment of text, either CENTER, LEFT or RIGHT
	 */
	public void setTextAlign(int textAlign) {
		this.textAlign = textAlign;
	}

	/**
	 * Returns the regular expression used to break text into lines
	 * @return the regular expression used to break text into lines
	 */
	public String getLineDelimiter() {
		return lineDelimiter;		
	}

	/**
	 * Sets the regular expression for splitting text into multiple lines
	 * @param lineDelimiter the regular expression for the line delimiter
	 */
	public void setLineDelimiter(String lineDelimiter) {
		this.lineDelimiter = lineDelimiter;
	}

	/**
	 * Returns the value of the label attribute splitted into multiple lines
	 * 
	 * @param item the item to get the label for
	 * @return the label of the item splitted into a string array
	 */	
	protected String[] getTextLines(VisualItem item) {
		String[] lines;
		String s = (String)item.getString(m_labelName);
		if(s == null) return null;
		
		lines = s.split(lineDelimiter);

		// abbreviate if necessary
		if ( m_maxTextWidth > -1 ) {
		    Font font = item.getFont();
		    if ( font == null ) { font = m_font; }
		    FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(font);
		    		    
		    for(int i=0; i<lines.length; i++) {
		    	if ( fm.stringWidth(lines[i]) > m_maxTextWidth ) {
		    		lines[i] = StringLib.abbreviate(lines[i], fm, m_maxTextWidth);			
		    	}
		    }
		}
		return lines;
	} //    

	
	protected Shape getRawShape(VisualItem item) {
		/* apparently this happens occasionally while loading a new graph */
		if(!(item instanceof NodeItem))
			return null;
		
		checkShapeAndPeripheries(item);
		
		float minWidth=0, minHeight=0;
		if(item.canGetFloat("width")) {
			minWidth = (float)(item.getFloat("width")*Config.DPI);
		}
		if(item.canGetFloat("height")) {
			minHeight = (float)(item.getFloat("height")*Config.DPI);
		}
		
		double w = minWidth;
		double h = minHeight;

		m_font = item.getFont();
		// make renderer size-aware
		double size = item.getSize();
		if ( size != 1 )
			m_font = FontLib.getFont(m_font.getName(), m_font.getStyle(),
					size*m_font.getSize());
		
		// only do more complicated calculations if the item size isn't fixed
		if(!(item.canGetBoolean("fixedsize") && item.getBoolean("fixedsize"))) {
			String[] s = getTextLines(item);
			if ( s == null ) { s = new String[] { "" }; }

			int lines = s.length;

			FontMetrics fm = DEFAULT_GRAPHICS.getFontMetrics(m_font);
			h = lines*fm.getHeight() + size*2*m_vertBorder;

			int maxline = Integer.MIN_VALUE;
			for(int i=0; i<lines; i++) {
				int currline = fm.stringWidth(s[i]);
				maxline = maxline > currline ? maxline : currline;
			}

			w = maxline + size*2*m_horizBorder;
			// avoid diminishing nodes with empty labels
			if(w < h && maxline == 0) 
				w = h;

			if(item.getString("shape").equalsIgnoreCase("circle")) {
				w = w > h ? w : h;
				h = w;
			}
		}

		getAlignedPoint(m_pt, item, w, h, m_xAlign, m_yAlign);
		m_bbox.setFrame(m_pt.getX(),m_pt.getY(),w,h);
		return m_bbox;
	} //
	
	public void render(Graphics2D g, VisualItem item) {
		checkShapeAndPeripheries(item);
		
        Shape shape = getShape(item);
        if ( shape != null ) {
            super.drawShape(g, item, shape);
        
            // now render the text
			String[] s = getTextLines(item);
			if ( s != null ) {
				Rectangle2D r;
				if(shape instanceof PeripherieShape) {
					r = ((PeripherieShape)shape).getInnerBounds2D();
				} else {
					r = shape.getBounds2D();
				}
				
				g.setPaint(ColorLib.getColor(item.getTextColor()));
				g.setFont(m_font);
				FontMetrics fm = g.getFontMetrics();
                double size = item.getSize();
                double x = r.getX() + size*m_horizBorder;
                double boxwidth = r.getWidth() - 2*size*m_horizBorder;
            	double basey = r.getCenterY() - s.length*fm.getHeight()/2.;      			
                
                for(int i=0; i<s.length; i++) {
                	double y = basey + i*fm.getHeight() + size*m_vertBorder;
                	double indent = 0;
                	if(textAlign == CENTER)                		
                		indent = (boxwidth - fm.stringWidth(s[i]))/2;
                	else if(textAlign == RIGHT)
                		indent = boxwidth - fm.stringWidth(s[i]);
                	
                	g.drawString(s[i], (float)(x+indent), (float)y+fm.getAscent());
                	
                }			                			
			}
		}
	} //

	/**
	 * Updates the current shape of the renderer based on the item
	 * to be rendered. 
	 * @param item
	 */
	private void checkShapeAndPeripheries(VisualItem item) {
		switch(item.getShape()) {
		case Constants.SHAPE_ELLIPSE:
			m_bbox = ellipseBounds;
			break;
		case Constants.SHAPE_RECTANGLE:
			m_bbox = rectangleBounds;
			break;
		default:
			if (Config.print) System.out.println("todo: shape " + item.getShape() + " unknown");
			m_bbox = rectangleBounds;
		}
		
		if(usePeripheries && item.canGetInt("peripheries")) {
			((PeripherieShape)m_bbox).setPeripheries(item.getInt("peripheries")-1);
		}
	}
}
