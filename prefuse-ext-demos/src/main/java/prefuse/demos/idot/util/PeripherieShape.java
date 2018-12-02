package prefuse.demos.idot.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * A shape used to add "peripheries" to another shape.
 * A new PeriphehieShape is constructed by giving as parameters
 * a base shape, and the number of peripheries to add.
 * The {@link #getPathIterator(AffineTransform)} method for this
 * class returns an iterator, which consists of the iterator for
 * the base shape as well of the iterators for the shapes that can be
 * formed by constructing {@link #peripheries} copies of the base shape
 * with an gradially increasing size.
 */
public class PeripherieShape extends RectangularShape {
	/** the base shape for this shape */
	protected RectangularShape baseShape;
	
	/** 
	 * the biggest shape that can be formed from the base shape by
	 * adding the peripheries around it
	 */
	protected RectangularShape bigShape;
	
	/** the number of peripheries added to the base shape */
	protected int peripheries;
	
	/** 
	 * the value added to the x coordinate of the previous shape when 
	 * constructing a new peripherie around the previous one
	 */
	protected int dx = -5;
	
	/** 
	 * the value added to the y coordinate of the previous shape when 
	 * constructing a new peripherie around the previous one
	 */
	protected int dy = -5;
	
	/** 
	 * the value added to the width of the previous shape when 
	 * constructing a new peripherie around the previous one
	 */
	protected int dwidth = 10;
	
	/** 
	 * the value added to the height of the previous shape when 
	 * constructing a new peripherie around the previous one
	 */
	protected int dheight = 10;
	
	/**
	 * Creates a new shape that uses tha given shape as the base
	 * and has <code>peripheries</code> additional peripheries around it
	 * @param base
	 * @param peripheries
	 */
	public PeripherieShape(RectangularShape base, int peripheries) {
		this.peripheries = peripheries;
		this.baseShape = base;
		try {
			bigShape = base.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		updateBigShape();
	}

	/**
	 * Updates th enumber of peripheries for this shape
	 * @param peripheries
	 */
	public void setPeripheries(int peripheries) {
		this.peripheries = peripheries;
		updateBigShape();
	}
	
	private void updateBigShape() {
		double x = baseShape.getX() + peripheries*dx;
		double y = baseShape.getY() + peripheries*dy;
		double width = baseShape.getWidth() + peripheries*dwidth;
		double height = baseShape.getHeight() + peripheries*dheight;
		
		bigShape.setFrame(x, y, width, height);
	}
	
	@Override
	public double getHeight() {
		return bigShape.getHeight();
	}

	@Override
	public double getWidth() {
		return bigShape.getWidth();
	}

	@Override
	public double getX() {
		return bigShape.getX();
	}

	@Override
	public double getY() {
		return bigShape.getY();
	}

	@Override
	public boolean isEmpty() {
		return bigShape.isEmpty();
	}

	@Override
	public void setFrame(double x, double y, double w, double h) {
		baseShape.setFrame(x, y, w, h);
		updateBigShape();
	}

	public boolean intersects(double x, double y, double w, double h) {
		return bigShape.intersects(x, y, w, h);
	}

	public boolean contains(double x, double y) {
		return bigShape.contains(x, y);
	}

	public boolean contains(double x, double y, double w, double h) {
		return bigShape.contains(x, y, w, h);
	}

	/**
	 * Returns the bounds of the original shape extended
	 * with the additional peripheries.
	 * @return the bounds of the extended shape
	 */
	public Rectangle2D getBounds2D() {		
		return bigShape.getBounds2D();
	}

	/**
	 * Returns the bounds of the original shape.
	 * @return the bounds of the original shape
	 */
	public Rectangle2D getInnerBounds2D() {
		return baseShape.getBounds2D();
	}

	/**
	 * Returns a new PathIterator that traverses the path of
	 * the original shape as well as the paths of the peripheries
	 */
	public PathIterator getPathIterator(AffineTransform at) {		
		return new PeripherieIterator(at);
	}

	/** 
	 * Returns the shape formed by adding the maximum number of peripheries
	 * to the base shape 
	 */
	public RectangularShape getOutermostShape() {
		return bigShape;
	}

	/**
	 * Class for traversing the paths of the original shape as well as
	 * the paths of the peripheries
	 */
	private class PeripherieIterator implements PathIterator {
		PathIterator currentIterator;
		RectangularShape currentShape;
		AffineTransform aTransform;
		int currentPeripherie;
		
		PeripherieIterator(AffineTransform at) {
			currentShape = (RectangularShape) baseShape.clone();
			currentPeripherie = 0;
			currentIterator = currentShape.getPathIterator(at);
			aTransform = at;
		}
		
		public int currentSegment(float[] coords) {
			return currentIterator.currentSegment(coords);
		}

		public int currentSegment(double[] coords) {
			return currentIterator.currentSegment(coords);
		}

		public int getWindingRule() {
			return currentIterator.getWindingRule();
		}

		public boolean isDone() {
			return currentIterator.isDone() && currentPeripherie >= peripheries;
		}

		public void next() {
			currentIterator.next();
			
			if(currentIterator.isDone() && currentPeripherie < peripheries) {
				currentPeripherie++;
				double x = currentShape.getX() + dx;
				double y = currentShape.getY() + dy;
				double width = currentShape.getWidth() + dwidth;
				double height = currentShape.getHeight() + dheight;
				
				currentShape.setFrame(x, y, width, height);
				currentIterator = currentShape.getPathIterator(aTransform);
			}			
		}	
	}
}
