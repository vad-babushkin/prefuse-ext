package profusians.util.force;

import prefuse.util.force.AbstractForce;
import prefuse.util.force.ForceItem;
import prefuse.util.force.WallForce;

/**
 * Uses a gravitational force model to act as a rectangular "box". This is
 * achieved by defining four different wall forces which constitutes this
 * rectangular box
 * 
 * Can be used to construct boxes which either attract or repel items.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class RectangularWallForce extends AbstractForce {

    private static final String[] pnames = new String[] { "GravitationalConstant" };

    public static final float DEFAULT_GRAV_CONSTANT = -0.1f;

    public static final float DEFAULT_MIN_GRAV_CONSTANT = -1.0f;

    public static final float DEFAULT_MAX_GRAV_CONSTANT = 1.0f;

    public static final int GRAVITATIONAL_CONST = 0;

    private WallForce wf1, wf2, wf3, wf4;

    /**
         * Creates a new RactangularWallForce.
         * 
         * @param cx
         *                the x-coordinate of the center of the rectangle
         * @param cy
         *                the y-coordinate of the center of the rectangle
         * @param width -
         *                the width of the rectangle
         * @param length
         *                the length of the rectangle
         */

    public RectangularWallForce( float cx, float cy,
	     float width, float length) {
	this(DEFAULT_GRAV_CONSTANT, cx, cy, width, length);

    }

    /**
         * Creates a new RactangularWallForce.
         * 
         * @param gravConst
         *                the gravitational constant of the wall
         * @param cx
         *                the x-coordinate of the center of the rectangle
         * @param cy
         *                the y-coordinate of the center of the rectangle
         * @param width -
         *                the width of the rectangle
         * @param length
         *                the length of the rectangle
         */

    public RectangularWallForce( float gravConst, float cx,
	     float cy, float width, float length) {
	params = new float[] { gravConst };
	minValues = new float[] { DEFAULT_MIN_GRAV_CONSTANT };
	maxValues = new float[] { DEFAULT_MAX_GRAV_CONSTANT };

	 float x1 = cx - width / 2;
	 float y1 = cy - length / 2;
	 float x2 = cx + width / 2;
	 float y2 = y1;
	 float x3 = x1;
	 float y3 = cy + length / 2;
	 float x4 = x2;
	 float y4 = y3;

	wf1 = new WallForce(gravConst, x1, y1, x2, y2);
	wf2 = new WallForce(gravConst, x1, y1, x3, y3);
	wf3 = new WallForce(gravConst, x2, y2, x4, y4);
	wf4 = new WallForce(gravConst, x3, y3, x4, y4);

    }

    public boolean isItemForce() {
	return true;
    }

    protected String[] getParameterNames() {
	return pnames;
    }

    public void getForce( ForceItem item) {
	wf1.getForce(item);
	wf2.getForce(item);
	wf3.getForce(item);
	wf4.getForce(item);
    }

} // end of class RectangluarWallForce
