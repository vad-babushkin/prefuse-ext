package prefuse.demos.idot.util;


import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import prefuse.demos.idot.Config;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.EdgeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

/**
 * A renderer for drawing the transitions as curves.
 */
public class TransitionRenderer extends EdgeRenderer {
	
	/**
	 * When two points on a curve on opposite sides of rectangle bounds
	 * are closer to each other than the square root of this value, the
	 * curve is assumed to be flat between the points and the arrow head
	 * for the transition is oriented between the points
	 */
	private static final int INTERSECT_THRESHOLD = 25;

	/**
	 * The name of the attribute holding the control point coordinates.
	 */
	public static final String COORDS = "coords";
	
	/**
	 * The (almost) b-spline shape that is used for drawing the transitions.
	 */
	NearBSpline bspline = new NearBSpline();
	
	/**
	 * Renders the transition
	 * 
	 * @param g  the Graphics2D object to draw the transition to
	 * @param item  the item to render
	 */	
	public void render(Graphics2D g, VisualItem item) {
		// copied from ShapeRenderer, can't call super.render 
		Shape shape = getShape(item);
		if (shape != null)
			drawShape(g, item, shape);
		
		VisualItem targetNodeItem = (VisualItem)((EdgeItem)item).getTargetItem();
        //Rectangle2D r = targetNodeItem.getBounds();
		Shape r = 
			((AbstractShapeRenderer)targetNodeItem.getRenderer()).getShape(targetNodeItem);

        /** index of the starting point of the part of the curve that crosses
         * the boundary of the target node */
        int startpointi=0;
        Point2D start, end;
        
        // if there are control points for the curve, use them, otherwise
        // just use the locations of the nodes as start and end points
        if(item.canGet(COORDS, double[].class) && item.get(COORDS) != null) {
        	double[] c = (double[]) item.get(COORDS);
        	int n = c.length;
        	end = new Point2D.Double(c[n-2], c[n-1]);
        	start = new Point2D.Double(c[n-4], c[n-3]);
        	startpointi = n/2-2;
        	int i = n-4;
        	
        	// try to find the two control points that are closest to the target
        	// node border but on opposite sides - this is for aligning the 
        	// arrow head correctly
        	while(r.contains(start) && i > 0) {
        		i-=2;
        		end.setLocation(start);
        		start.setLocation(c[i], c[i+1]);
        		startpointi--;
        	}
        	
        	// show control points for debugging
        	// for(i=0; i<n; i+=2) {
        	// 	g.drawOval((int) c[i]-3, (int) c[i+1]-3, 7, 7);
        	// }
        } else {
        	start = new Point2D.Double(
        		((NodeItem)((EdgeItem)item).getSourceItem()).getX(),
        		((NodeItem)((EdgeItem)item).getSourceItem()).getY());
        	end = new Point2D.Double(
            		((NodeItem)((EdgeItem)item).getTargetItem()).getX(),
            		((NodeItem)((EdgeItem)item).getTargetItem()).getY());
        }
        

        boolean done = false;
        if(r instanceof PeripherieShape) {
        	Shape s = ((PeripherieShape)r).getOutermostShape();
        	if(s instanceof Ellipse2D) {
            	int i = getCurveShapeIntersection(shape, (Ellipse2D) s, m_isctPoints, start, end, startpointi);
            	if ( i > 0 ) {
            		end = m_isctPoints[0];
            	}
            	
            	done = true;            	
        	}
        } 
        if(!done) {
        	Rectangle2D r2 = r.getBounds2D();
        	int i = getCurveShapeIntersection(shape, r2, m_isctPoints, start, end, startpointi);
        	if ( i > 0 ) {
        		end = m_isctPoints[0];
        	}
        }
        
        // orient and draw the arrow head
		AffineTransform at = getArrowTrans(start, end, getLineWidth(item));
        Shape arrowHead = at.createTransformedShape(m_arrowHead);
		g.setPaint(ColorLib.getColor(item.getFillColor()));
		g.fill(arrowHead);			
	}
	
	/**
	 * Returns the shape of the item
	 * 
	 * @param item the item to return the shape for
	 * @return the shape of the item
	 */
	protected Shape getRawShape(VisualItem item) {
				
		EdgeItem   edge = (EdgeItem)item;
		VisualItem item1 = (VisualItem)edge.getSourceNode();
		VisualItem item2 = (VisualItem)edge.getTargetNode();
						
		getAlignedPoint(m_tmpPoints[0], item1.getBounds(),
				m_xAlign1, m_yAlign1);
		getAlignedPoint(m_tmpPoints[1], item2.getBounds(),
				m_xAlign2, m_yAlign2);
		
		double n1x = m_tmpPoints[0].getX();
		double n1y = m_tmpPoints[0].getY();
		double n2x = m_tmpPoints[1].getX();
		double n2y = m_tmpPoints[1].getY();
		m_curWidth = (float) (m_width * getLineWidth(item));
		
		double coords[] = null;

		if(item.canGet(COORDS, double[].class))
			coords = (double[]) item.get((COORDS));
		if(coords != null) {
			bspline.setLine(coords);
		} else {
			bspline.setLine(n1x, n1y, n2x, n2y);
		}
		
		return bspline;
	}


	/**
	 * Calculates the intersection point of the curve and a shape that is either
	 * an ellipse or a rectangle.
	 * This only works when these assumptions are true:
	 *     - the shape is a NearBSpline
	 *     - the end of the curve is inside the rectangle ro ellipse
	 *     - the other end of the last segment of the curve is not in the 
	 *       rectangle or ellipse
	 *     
	 * @param shape the curve 
	 * @param targetShape the rectangle (Rectangle2D) or ellipse (Ellipse2D)
	 * @param points point[0] will contain the point of intersection (if found)
	 * @param start assumed point outside the rectangle (may get overwritten)
	 * @param end   assumed point inside the rectangle
	 * @param segmentStartIndex the index of the segment (pair of control points)
	 *     that is assumed to cross the rectangle boundary
	 * @return the number of intersections found (should be 1)
	 */
	private int getCurveShapeIntersection(Shape shape, Shape targetShape, Point2D[] points, 
			Point2D start, Point2D end, int segmentStartIndex) {

		if(!(shape instanceof NearBSpline)) {
			System.err.println("unknown shape");
			return -1;
		}

		NearBSpline spline = (NearBSpline) shape;
		// using rectangular bounds with an ellipse might be risky, 
		// but it seems to work reasonably well
		Rectangle2D bounds = targetShape.getBounds2D();
		
		Shape lastSegment = spline.getSegment(segmentStartIndex); 

		// the segment might be a line, a cubic curve or a quadratic curve
		// try to find the intersection point in every case
		if(lastSegment instanceof Line2D) {
			return intersectLineShape(start, end, targetShape, points);

		} else if(lastSegment instanceof CubicCurve2D) {
			if(((CubicCurve2D)lastSegment).intersects(bounds)) {
				CubicCurve2D.Double cubic = (CubicCurve2D.Double) lastSegment;
				double p0x = cubic.x1, p1x = cubic.ctrlx1, p2x = cubic.ctrlx2, p3x = cubic.x2;
				double p0y = cubic.y1, p1y = cubic.ctrly1, p2y = cubic.ctrly2, p3y = cubic.y2;
				
				double r0x, r0y, r1x, r1y, r2x, r2y, r3x, r3y, s0x, s0y, s1x, s1y, s2x, s2y, s3x, s3y;
				int rounds = 1;
				while(true) {
					// divide P0, P1, P2, P3 to R0, R1, R2, R3 && S0, S1, S2, S3
					r0x = p0x; r0y = p0y;
					s3x = p3x; s3y = p3y;
					r1x = (p0x+p1x)/2f; r1y = (p0y+p1y)/2f;
					s2x = (p2x+p3x)/2f; s2y = (p2y+p3y)/2f;
					r2x = r1x/2f+(p1x+p2x)/4f; r2y = r1y/2f+(p1y+p2y)/4f;
					s1x = (p1x+p2x)/4f+s2x/2f; s1y = (p1y+p2y)/4f+s2y/2f;
					r3x = (r2x+s1x)/2f; r3y = (r2y+s1y)/2f;
					s0x = r3x; s0y = r3y;
					
					// select one of the subcurves
					if(targetShape.contains(s0x, s0y)) {
						p0x = r0x; p1x = r1x; p2x = r2x; p3x = r3x;
						p0y = r0y; p1y = r1y; p2y = r2y; p3y = r3y;
						
						if(Config.print && targetShape.contains(r0x, r0y))
							System.err.println("both end points swallowed!");
					} else {
						p0x = s0x; p1x = s1x; p2x = s2x; p3x = s3x;
						p0y = s0y; p1y = s1y; p2y = s2y; p3y = s3y;
						
						if(!targetShape.contains(s3x, s3y))
							System.err.println("end not in rectangle!");
					}
					
					// assumption: p0 is not in r, p3 is
					
					if(Point2D.distanceSq(p0x, p0y, p3x, p3y) < INTERSECT_THRESHOLD) {
						// if (Config.print) System.out.println("cubic: did " + rounds + " iterations");
						start.setLocation(p0x, p0y); // angle head direction
						end.setLocation(p3x, p3y);
						return intersectLineShape(start, end, targetShape, points);
					}
					rounds++;
				}	
			} else			
				return intersectLineShape(start, end, targetShape, points);
			
			
		} else if(lastSegment instanceof QuadCurve2D) {
			if(((QuadCurve2D.Float)lastSegment).intersects(bounds)) {
				QuadCurve2D.Float quad = (QuadCurve2D.Float) lastSegment;
				float p0x = quad.x1, p1x = quad.ctrlx, p2x = quad.x2;
				float p0y = quad.y1, p1y = quad.ctrly, p2y = quad.y2;
				
				float r0x, r0y, r1x, r1y, r2x, r2y, s1x, s1y, s2x, s2y; // s0x, s0y;
				int rounds = 1;				
				while(true) {
					// divide P0, P1, P2 to R0, R1, R2 && S0, S1, S2
					r0x = p0x; r0y = p0y;
					r1x = (p0x+p1x)/2f; r1y = (p0y+p1y)/2f;
					s1x = (p1x+p2x)/2f; s1y = (p1y+p2y)/2f;
					r2x = (r1x+s1x)/2f; r2y = (r1y+s1y)/2f; // = s0x, s0y
					s2x = p2x; s2y = p2y;
					
					// select one of the subcurves
					if(targetShape.contains(r2x, r2y)) {
						p0x = r0x; p1x = r1x; p2x = r2x;
						p0y = r0y; p1y = r1y; p2y = r2y;				
					} else {
						p0x = r2x; p1x = s1x; p2x = s2x;
						p0y = r2y; p1y = s1y; p2y = s2y;										
					}
					
					// assumption: p0 is not in r, p2 is
					
					if(Point2D.distanceSq(p0x, p0y, p2x, p2y) < INTERSECT_THRESHOLD) {
						// if (Config.print) System.out.println("quad: did " + rounds + " iterations");
						start.setLocation(p0x, p0y); // angle head direction
						return intersectLineShape(new Point2D.Float(p0x, p0y), 
								new Point2D.Float(p2x, p2y), targetShape, points);
					}
					rounds++;
				}
				
			} else
				return intersectLineShape(start, end, targetShape, points);
		} else
			return intersectLineShape(start, end, targetShape, points);
	}

	/**
	 * Caluclates the intersection point between a line segment and a shape,
	 * which is either a rectangle or an ellipse
	 * 
	 * @param start
	 * @param end
	 * @param shape
	 * @param points
	 * @return the number of intersection points found
	 */
	private int intersectLineShape(Point2D start, 
			Point2D end, Shape shape, Point2D[] points) {

		if(shape instanceof Rectangle2D) {
			return GraphicsLib.intersectLineRectangle(
					start, end, (Rectangle2D) shape, points);
		}
		
		Ellipse2D ellipse = (Ellipse2D) shape;
		
		double[] mu = new double[2];
		double widthScale = ellipse.getWidth();
		double heightScale = ellipse.getHeight();
		
		Point2D center = new Point2D.Double(ellipse.getCenterX(), ellipse.getCenterY());
		start.setLocation((start.getX()-center.getX())/widthScale, 
				(start.getY()-center.getY())/heightScale);
		end.setLocation((end.getX()-center.getX())/widthScale, 
				(end.getY()-center.getY())/heightScale);

		
		int n = intersectLineCircle(start, end, 1./2., mu);
		
		if(n > 0) {
			double dx = end.getX() - start.getX();
			double dy = end.getY() - start.getY();
			
			double x = start.getX() + mu[0]*dx;
			double y = start.getY() + mu[0]*dy;
			
			points[0].setLocation(
					x*widthScale+center.getX(), y*heightScale+center.getY());
			if(n > 1) {
				x = start.getX() + mu[1]*dx;
				y = start.getY() + mu[1]*dy;
				
				points[1].setLocation(
						x*widthScale+center.getX(), y*heightScale+center.getY());
			}
		}

		start.setLocation(start.getX()*widthScale +center.getX(), 
				start.getY()*heightScale + center.getY());
		end.setLocation(end.getX()*widthScale +center.getX(), 
				end.getY()*heightScale + center.getY());
		
		// check that intersections points are between start and end?
		// we will *not* check the validity of points[1] if
		// points[0] is valid, because the caller should
		// always use the first point (at least in this
		// class it does
		
		while(n>0) {
			boolean outx = start.getX() < points[0].getX();
			outx ^= points[0].getX() < end.getX();
			
			boolean outy = start.getY() < points[0].getY();
			outy ^= points[0].getY() < end.getY();
			
			if(outx || outy) {
				//System.err.println("point was not between start and end");
				points[0].setLocation(points[1]);
				n--;
			} else {
				break;
			}
		}
				
		return n;
	}


	/**
	 * Calculates the intersections point(s) between a line
	 * and circle, if there exist any.
	 *  
	 *  The line is defined by two points, <code>p1</code> and 
	 *  <code>p2</code>, and the circle is defined by the
	 *  radius <code>r</code>. The circle is always centered
	 *  at (0,0). 
	 *  
	 *  There are potentially two points of intersection given by:
	 *  <pre>
	 *  p = p1 + mu[0] (p2 - p1)
	 *  p = p1 + mu[1] (p2 - p1)
	 *  </pre>
	 *  
	 *  @param p1  start point of the line segment
	 *  @param p2    end point of the line segment
	 *  @param r       radius of the circle
	 *  @param mu      the resulting coefficients will be placed
	 *                 in this array (see formula above)
	 *                 
	 *  @return the number of intersection points (0, 1 or 2)
	 */
	private int intersectLineCircle(Point2D p1, Point2D p2, double r, double[] mu) {
		double a, b, c;
		double bb4ac;

		double dx = p2.getX() - p1.getX();
		double dy = p2.getY() - p1.getY();

		a = dx * dx + dy * dy;
		b = 2 * (dx * p1.getX() + dy * p1.getY());
		c = p1.getX() * p1.getX() + p1.getY() * p1.getY();		
		c -= r * r;		
		bb4ac = b * b - 4 * a * c;
		
		// bb4ac < 0 ?  no intersection
		// bb4ac == 0 ? 1 intersection point
		// bb4ac > 0 ?  2 intersection points
		
		if(bb4ac < 0) {
			return 0;
		}

		mu[0] = (-b - Math.sqrt(bb4ac)) / (2 * a);
		mu[1] = (-b + Math.sqrt(bb4ac)) / (2 * a);

		return (bb4ac == 0) ? 1 : 2;
	}

}