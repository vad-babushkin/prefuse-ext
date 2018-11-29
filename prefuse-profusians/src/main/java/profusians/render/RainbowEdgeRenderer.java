package profusians.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import prefuse.Constants;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.VisualItem;

/**
 * Multicolor edge renderer
 * 
 * The development of this edge renderer was caused by a post by Rythmic in the
 * source forge prefuse forum.
 * 
 * The essence and core of it is Rythmic's strategy of placing the beginning and
 * end points of the multiline edge.
 * 
 * Based on that this edge renderer extends the original class: + full multiple
 * line support + arrow heads are drawn in multiple colors + curved edges + the
 * edge width can be specified (or more precise the line width of one color)
 * 
 * Please report any problem you encounter with this edge renderer.
 * 
 * @author <a href="http://jheer.org">jeffrey heer (original edge renderer)</a>
 * @author rythmic (key insight provider)
 * @author <a href="http://goosebumps4all.net">martin dudek (the questionable
 *         rest)</a>
 */

public class RainbowEdgeRenderer extends SmoothEdgeRenderer {

    protected Polygon[] m_arrowRainbowHead;

    protected Shape[] m_curRainbowArrow;

    protected Line2D[] m_line;

    protected CubicCurve2D[] m_curve;

    private Color[] m_edgeColor;

    private int m_numberOfEdges;

    protected Point2D[] m_rainbowCtrlPoints[];

    double[] x1;

    double[] x2;

    double[] y1;

    double[] y2;

    public RainbowEdgeRenderer( Color[] edgeColor) {
	super();
	setup(edgeColor);
	updateArrowHeadRainbow(m_arrowWidth, m_arrowHeight);

    }

    public void render( Graphics2D g, VisualItem item) {
	// render the edge line

	getShape(item);

	for (int i = 0; i < m_numberOfEdges; i++) {

	    item.setStrokeColor(m_edgeColor[i].getRGB());
	    if (m_edgeType == prefuse.Constants.EDGE_TYPE_CURVE) {
		drawShape(g, item, m_curve[i]);
	    } else {
		drawShape(g, item, m_line[i]);
	    }

	}

	// render the edge arrow head, if appropriate

	for (int i = 0; i < m_numberOfEdges; i++) {
	    if (m_curRainbowArrow[i] != null) {

		g.setPaint(m_edgeColor[i]);
		g.fill(m_curRainbowArrow[i]);
	    }
	}
    }

    public void setArrowHeadSize( int width, int height) {
	m_arrowWidth = width;
	m_arrowHeight = height;
	updateArrowHeadRainbow(width, height);
    }

    public void setColors( Color[] edgeColor) {
	setup(edgeColor);
    }

    public Color[] getColors() {
	return m_edgeColor;
    }

    public int getNumberOfEdges() {
	return m_numberOfEdges;
    }

    public void setBounds( VisualItem item) { // TODO
	if (!m_manageBounds) {
	    return;
	}
	 Shape shape = getShape(item);
	if (shape == null) {
	    item.setBounds(item.getX(), item.getY(), 0, 0);

	} else {
	    GraphicsLib.setBounds(item, shape, getStroke(item));
	}

	if (m_curRainbowArrow[0] != null) {
	     Rectangle2D bbox = (Rectangle2D) item.get(VisualItem.BOUNDS);
	    for (int i = 0; i < m_numberOfEdges; i++) {
		Rectangle2D.union(bbox, m_curRainbowArrow[i].getBounds2D(),
			bbox);
	    }
	}
    }

    protected void updateArrowHeadRainbow( int w, int h) {

	if (m_arrowRainbowHead == null) {
	    m_arrowRainbowHead = new Polygon[m_numberOfEdges];
	    for (int i = 0; i < m_numberOfEdges; i++) {
		m_arrowRainbowHead[i] = new Polygon();
	    }

	} else {
	    for (int i = 0; i < m_numberOfEdges; i++) {
		m_arrowRainbowHead[i].reset();
	    }
	}

	 double rainbowHeadPieceWidth = (w * m_width) / m_numberOfEdges;

	for (int i = 0; i < m_numberOfEdges; i++) {
	    m_arrowRainbowHead[i].addPoint(0, 0);
	    m_arrowRainbowHead[i].addPoint((int) (w / 2.0 - i
		    * rainbowHeadPieceWidth), -h);
	    m_arrowRainbowHead[i].addPoint((int) (w / 2.0 - (i + 1)
		    * rainbowHeadPieceWidth), -h);
	    m_arrowRainbowHead[i].addPoint(0, 0);
	}
    }

    protected Shape getRawShape( VisualItem item) {
	 EdgeItem edge = (EdgeItem) item;
	 VisualItem item1 = edge.getSourceItem();
	 VisualItem item2 = edge.getTargetItem();

	 int type = m_edgeType;

	getAlignedPoint(m_tmpPoints[0], item1.getBounds(), m_xAlign1, m_yAlign1);
	getAlignedPoint(m_tmpPoints[1], item2.getBounds(), m_xAlign2, m_yAlign2);
	m_curWidth = (float) (m_width * getLineWidth(item));

	// create the arrow head, if needed
	 EdgeItem e = (EdgeItem) item;
	if (e.isDirected() && (m_edgeArrow != Constants.EDGE_ARROW_NONE)) {
	    // get starting and ending edge endpoints
	     boolean forward = (m_edgeArrow == Constants.EDGE_ARROW_FORWARD);
	    Point2D start = null, end = null;
	    start = m_tmpPoints[forward ? 0 : 1];
	    end = m_tmpPoints[forward ? 1 : 0];

	    // compute the intersection with the target bounding box
	     VisualItem dest = forward ? e.getTargetItem() : e
		    .getSourceItem();
	     int i = GraphicsLib.intersectLineRectangle(start, end, dest
		    .getBounds(), m_isctPoints);
	    if (i > 0) {
		end = m_isctPoints[0];
	    }

	    // create the arrow head shape
	     AffineTransform at = getArrowTrans(start, end, m_curWidth
		    * m_numberOfEdges * 0.5);

	    for (int j = 0; j < m_numberOfEdges; j++) {
		m_curRainbowArrow[j] = at
			.createTransformedShape(m_arrowRainbowHead[j]);

	    }

	    // update the endpoints for the edge shape
	    // need to bias this by arrow head size
	     Point2D lineEnd = m_tmpPoints[forward ? 1 : 0];

	    /*
                 * added +1 to ensure that all multiple edges are covered by the
                 * head Takes advantage of the fact that the arrow head is drawn
                 * after the edges and by that above them if intersection occurs
                 */
	    lineEnd.setLocation(0, -m_arrowHeight + 1);

	    at.transform(lineEnd, lineEnd);
	} else {

	    for (int j = 0; j < m_numberOfEdges; j++) {
		m_curRainbowArrow[j] = null;
	    }
	}

	// create the edge shape
	Shape shape = null;
	 double n1x = m_tmpPoints[0].getX();
	 double n1y = m_tmpPoints[0].getY();
	 double n2x = m_tmpPoints[1].getX();
	 double n2y = m_tmpPoints[1].getY();

	double c, radAngle, degAngle;

	// a = length of x-axis, b = length of y-axis
	double a, b;

	a = ((n1x - n2x));
	b = ((n1y - n2y));
	c = Math.sqrt((a * a) + (b * b));

	radAngle = Math.acos(((Math.abs(a)) / c));
	degAngle = radAngle * (180 / Math.PI);

	for (int i = 0; i < m_numberOfEdges; i++) {
	    x1[i] = n1x;
	    x2[i] = n2x;
	    y1[i] = n1y;
	    y2[i] = n2y;
	}

	getCurveControlPoints(edge, m_ctrlPoints, n1x, n1y, n2x, n2y);

	 double bx1 = m_ctrlPoints[0].getX();
	 double bx2 = m_ctrlPoints[1].getX();

	 double by1 = m_ctrlPoints[0].getY();
	 double by2 = m_ctrlPoints[1].getY();

	boolean xDirection;
	int flip;

	// a <= 0 -> target is left to source, or vertical if a == 0
	// b < 0 -> source is above target, or horizontal if b == 0

	flip = (a > b) ? -1 : 1;
	xDirection = (degAngle > 45) ? true : false;

	double offset = -flip * (m_numberOfEdges - 1) * m_width / 2;

	for (int i = 0; i < m_numberOfEdges; i++) {
	    if (xDirection) {
		x1[i] += offset;
		x2[i] += offset;

		if (m_edgeType == prefuse.Constants.EDGE_TYPE_CURVE) {
		    m_rainbowCtrlPoints[i][0].setLocation(bx1 + offset, by1);
		    m_rainbowCtrlPoints[i][1].setLocation(bx2 + offset, by2);
		}
	    } else {
		y1[i] += offset;
		y2[i] += offset;

		if (m_edgeType == prefuse.Constants.EDGE_TYPE_CURVE) {
		    m_rainbowCtrlPoints[i][0].setLocation(bx1, by1 + offset);
		    m_rainbowCtrlPoints[i][1].setLocation(bx2, by2 + offset);
		}
	    }
	    offset += flip * m_width;
	}

	switch (type) {
	case Constants.EDGE_TYPE_LINE:

	    m_line[0] = new Line2D.Double(x1[0], y1[0], x2[0], y2[0]);
	    Rectangle2D bbox = m_line[0].getBounds2D();
	    for (int i = 1; i < m_numberOfEdges; i++) {
		m_line[i] = new Line2D.Double(x1[i], y1[i], x2[i], y2[i]);
		Rectangle2D.union(bbox, m_line[i].getBounds2D(), bbox);
	    }
	    shape = bbox;
	    break;

	case Constants.EDGE_TYPE_CURVE:

	    m_curve[0] = new CubicCurve2D.Double(x1[0], y1[0],
		    m_rainbowCtrlPoints[0][0].getX(), m_rainbowCtrlPoints[0][0]
			    .getY(), m_rainbowCtrlPoints[0][1].getX(),
		    m_rainbowCtrlPoints[0][1].getY(), x2[0], y2[0]);
	    bbox = m_curve[0].getBounds2D();
	    for (int i = 1; i < m_numberOfEdges; i++) {
		m_curve[i] = new CubicCurve2D.Double(x1[i], y1[i],
			m_rainbowCtrlPoints[i][0].getX(),
			m_rainbowCtrlPoints[i][0].getY(),
			m_rainbowCtrlPoints[i][1].getX(),
			m_rainbowCtrlPoints[i][1].getY(), x2[i], y2[i]);
		Rectangle2D.union(bbox, m_curve[i].getBounds2D(), bbox);
	    }
	    shape = bbox;
	    break;

	default:
	    throw new IllegalStateException("Unknown edge type");
	}

	// return the edge shape
	return shape;
    }

    private void setup( Color[] edgeColor) {
	m_edgeColor = edgeColor;
	m_numberOfEdges = edgeColor.length;
	x1 = new double[m_numberOfEdges];
	x2 = new double[m_numberOfEdges];
	y1 = new double[m_numberOfEdges];
	y2 = new double[m_numberOfEdges];

	m_line = new Line2D[m_numberOfEdges];
	m_curve = new CubicCurve2D[m_numberOfEdges];
	m_curRainbowArrow = new Shape[m_numberOfEdges];

	m_rainbowCtrlPoints = new Point2D[m_numberOfEdges][2];

	for (int i = 0; i < m_numberOfEdges; i++) {
	    m_rainbowCtrlPoints[i][0] = new Point2D.Double();
	    m_rainbowCtrlPoints[i][1] = new Point2D.Double();
	}
    }

}
