package ieg.prefuse.renderer;

import java.awt.Shape;

import prefuse.render.ShapeRenderer;

/**
 * <p>
 * Added:          / TL<br>
 * Modifications:
 * </p>
 *
 * @author Tim Lammarsch
 */
public class ExtendedShapeRenderer extends ShapeRenderer {

	public static final int SHAPE_LEFT_BRACKET = 256;
	public static final int SHAPE_RIGHT_BRACKET = 257;

	public ExtendedShapeRenderer() {
		super();
	}

	/**
	 * Creates a new ShapeRenderer with given base size.
	 *
	 * @param size the base size in pixels
	 */
	public ExtendedShapeRenderer(int size) {
		super(size);
	}

	/* (non-Javadoc)
	 * @see prefuse.render.ShapeRenderer#extendedShape(int, double, double, double, double)
	 */
	@Override
	protected Shape extendedShape(int stype, double x, double y, double width, double height) {
		switch (stype) {
			case SHAPE_LEFT_BRACKET:
				return leftBracket(x, y, width, height);
			case SHAPE_RIGHT_BRACKET:
				return rightBracket(x, y, width, height);
			default:
				throw new IllegalStateException("Unknown shape type: " + stype);
		}
	}

	private Shape leftBracket(double x, double y, double width, double height) {
		m_path.reset();
		m_path.moveTo(x + width, y);
		m_path.lineTo(x, y);
		m_path.lineTo(x, y + height);
		m_path.lineTo(x + width, y + height);
		return m_path;
	}

	private Shape rightBracket(double x, double y, double width, double height) {
		m_path.reset();
		m_path.moveTo(x, y);
		m_path.lineTo(x + width, y);
		m_path.lineTo(x + width, y + height);
		m_path.lineTo(x, y + height);
		return m_path;
	}
}
