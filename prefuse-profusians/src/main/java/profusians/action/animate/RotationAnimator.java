package profusians.action.animate;

import prefuse.action.ItemAction;
import prefuse.visual.VisualItem;

/**
 * Animator that interpolates between two rotation angles.
 * 
 * The usage of this animator requires that items contain the double fields
 * rotation, startRotation and endRotation and that these fields are maintained
 * by the used layout accordingly.
 * 
 * Its interpolation policy is designed to work with the RotationLabelRenderer
 * by Christopher Collins.
 * 
 * Between four rotation animation styles can be choosen:
 * 
 * RotationAnimator.STRAIGHT_ROTATION the node rotates the shortest way to the
 * new rotation angle (default) RotationAnimator.LOOP_ROTATION an extra loop is
 * included on the way to the new rotation angle
 * RotationAnimator.DOUBLELOOP_ROTATION two extra loops are included on the way
 * to the new rotation angle RotationAnimator.TRIPLELOOP_ROTATION three extra
 * loops are included on the way to the new rotation angle
 * 
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */
public class RotationAnimator extends ItemAction {

    public static final int STRAIGHT_ROTATION = 1;

    public static final int SINGLELOOP_ROTATION = 2;

    public  static  final int DOUBLELOOP_ROTATION = 3;

    public  static  final int TRIPLELOOP_ROTATION = 4;

    private int m_rotationStyle;

    private int m_rotationFactor;

    /**
         * Create a new RotationAnimator that processes all data groups.
         */
    public RotationAnimator() {
	super();
    }

    /**
         * Create a new RotationAnimator that processes the specified group.
         * 
         * @param group
         *                the data group to process.
         */

    public RotationAnimator( String group) {
	super(group);
    }

    /**
         * Create a new RotationAnimator that processes all data groups
         * according the given rotation style.
         * 
         * @param rotationStyle
         *                the rotation style to be used by this animator The
         *                following rotation animation styles are available:
         *                RotationAnimator.STRAIGHT_ROTATION the node rotates
         *                the shortest way to the new rotation angle (default)
         *                RotationAnimator.SINGLELOOP_ROTATION one extra loop is
         *                included on the way to the new rotation angle
         *                RotationAnimator.DOUBLELOOP_ROTATION two extra loops
         *                are included on the way to the new rotation angle
         *                RotationAnimator.TRIPLELOOP_ROTATION three extra loops
         *                are included on the way to the new rotation angle
         * 
         */
    public RotationAnimator( int rotationStyle) {
	super();
	m_rotationStyle = rotationStyle;
    }

    /**
         * Create a new RotationAnimator that processes the specified group.
         * 
         * @param group
         *                the data group to process.
         * @param rotationStyle
         *                the rotation style to be used by this animator The
         *                following rotation animation styles are available:
         *                RotationAnimator.STRAIGHT_ROTATION the node rotates
         *                the shortest way to the new rotation angle (default)
         *                RotationAnimator.SINGLELOOP_ROTATION one extra loop is
         *                included on the way to the new rotation angle
         *                RotationAnimator.DOUBLELOOP_ROTATION two extra loops
         *                are included on the way to the new rotation angle
         *                RotationAnimator.TRIPLELOOP_ROTATION three extra loops
         *                are included on the way to the new rotation angle
         */

    public RotationAnimator( String group, int rotationStyle) {
	super(group);
	m_rotationStyle = rotationStyle;
    }

    public void setRotationStyle( int style) {
	m_rotationStyle = style;
	switch (style) {
	case STRAIGHT_ROTATION:
	    m_rotationFactor = 0;
	    break;
	case SINGLELOOP_ROTATION:
	    m_rotationFactor = 1;
	    break;
	case DOUBLELOOP_ROTATION:
	    m_rotationFactor = 2;
	    break;
	case TRIPLELOOP_ROTATION:
	    m_rotationFactor = 3;
	    break;

	default:
	    throw new IllegalArgumentException(
		    "Unrecognized rotation style value: " + style);

	}
    }

    public int getRotationStyle() {
	return m_rotationStyle;
    }

    /**
         * @see prefuse.action.ItemAction#process(prefuse.visual.VisualItem,
         *      double)
         */
    public void process( VisualItem item, double frac) {
	try {
	     double sr = item.getDouble("startRotation") % 180;
	     double er = item.getDouble("endRotation") % 180;

	    if (Math.abs(sr - er) < 90) {
		item
			.setDouble("rotation",
				(sr + frac * (er - sr) + m_rotationFactor
					* frac * 180) % 180);
	    } else {
		 double distance = 180 - Math.abs(sr - er);
		 int direction = (sr > er) ? 1 : -1;
		item.setDouble("rotation", (180 + sr + direction * frac
			* distance + m_rotationFactor * frac * 180) % 180);
	    }
	} catch ( Exception e) {
	    System.out
		    .println("Problem while setting rotation angle in RotationAnimator class "
			    + e.getMessage());
	}
    }

} // end of class RotationAnimator
