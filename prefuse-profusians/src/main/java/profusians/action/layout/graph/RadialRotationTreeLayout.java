package profusians.action.layout.graph;

import java.util.Iterator;

import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.data.Graph;
import prefuse.util.MathLib;
import prefuse.util.UpdateListener;
import prefuse.visual.NodeItem;

/**
 * Extension of the RadialTreeLayout from the prefuse library, which reacts on
 * rescaling of the display window by fitting the displayed radial tree nicely
 * into the given space.
 * 
 * In addition, its adds the column "rotation" to the tree nodes and fills this
 * field with the angle between the node and one of these three possible
 * choices:
 * 
 * RadialRotationTreeLayout.ROOT_ORIENTATION - the angle is calculated in
 * relation to the tree root RadialRotationTreeLayout.PARENT_ORIENTATION - the
 * angle is calculated in relation to the parent node
 * RadialRotationTreeLayout.CHILDREN_ORIENTATION - the angle is calculated in
 * relation to the children nodes
 * 
 * 
 * This field can then be used in combination with the RotationLabelRenderer of
 * the profusians package in order to draw labels according to this angle This
 * angle can also be scaled with the method setLabelAngleScale();
 * 
 * @author <a href="http://jheer.org">jeffrey heer</a>
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class RadialRotationTreeLayout extends RadialTreeLayout {

    public final static int ROOT_ORIENTATION = 1;

    public final static int PARENT_ORIENTATION = 2;

    public final static int CHILDREN_ORIENTATION = 3;

    private int m_orientation = ROOT_ORIENTATION;

    private double m_scaleX = 1.0, m_scaleY = 1.0;

    private double m_rotationScale = 1;

    private boolean m_checkRotationColumn = true;

    /**
         * Creates a new RadialRotationTreeLayout. Automatic scaling of the
         * layout size values to fit the layout bounds is enabled by default.
         * 
         * @param group
         *                the data group to process. This should resolve to
         *                either a Graph or Tree instance.
         */

    public RadialRotationTreeLayout( String group) {
	super(group);
    }

    /**
         * Creates a new RadialRotationTreeLayout. Automatic scaling of the
         * layout size values to fit the layout bounds is enabled by default.
         * 
         * @param group
         *                the data group to process. This should resolve to
         *                either a Graph or Tree instance.
         * @param orientation
         *                Possible choices:
         *                RadialRotationTreeLayout.ROOT_ORIENTATION - the angle
         *                is calculated in relation to the tree root
         *                RadialRotationTreeLayout.PARENT_ORIENTATION - the
         *                angle is calculated in relation to the parent node
         * 
         */

    public RadialRotationTreeLayout( String group, int orientation) {
	super(group);
	m_orientation = orientation;
    }

    /**
         * Creates a new RadialRotationTreeLayout. Automatic scaling of the
         * layout size values to fit the layout bounds is enabled by default.
         * 
         * @param group
         *                the data group to process. This should resolve to
         *                either a Graph or Tree instance.
         * @param labelAngleScale
         *                scales the angle in which the label is layout.
         *                (default 1);
         * 
         */

    public RadialRotationTreeLayout( String group,
	     double labelAngleScale) {
	super(group);
	m_rotationScale = labelAngleScale;
    }

    /**
         * Creates a new RadialRotationTreeLayout. Automatic scaling of the
         * layout size values to fit the layout bounds is enabled by default.
         * 
         * @param group
         *                the data group to process. This should resolve to
         *                either a Graph or Tree instance.
         * @param orientation
         *                Possible choices:
         *                RadialRotationTreeLayout.ROOT_ORIENTATION - the angle
         *                is calculated in relation to the tree root
         *                RadialRotationTreeLayout.PARENT_ORIENTATION - the
         *                angle is calculated in relation to the parent node
         * @param labelAngleScale
         *                scales the angle in which the label is layout.
         *                (default 1);
         * 
         */

    public RadialRotationTreeLayout( String group, int orientation,
	     double labelAngleScale) {
	super(group);
	m_orientation = orientation;
	m_rotationScale = labelAngleScale;
    }

    /**
         * Sets the orientation of the rotated node labels. Possible choices:
         * RadialRotationTreeLayout.ROOT_ORIENTATION - the angle is calculated
         * in relation to the tree root
         * RadialRotationTreeLayout.PARENT_ORIENTATION - the angle is calculated
         * in relation to the parent node
         * 
         * @param orientation
         */
    public void setRotationOrientation( int orientation) {
	m_orientation = orientation;
    }

    /**
         * Gets the label rotation orientation
         * 
         * @return the orientation
         */
    public int getRotationOrientation() {
	return m_orientation;
    }

    /**
         * Sets the label rotation scale, which scales the rotation angle
         * written into the rotation field accordingly.
         * 
         * @param scale
         *                the angle rotation scale
         */

    public void setLabelRotationScale( double scale) {
	m_rotationScale = scale;
    }

    /**
         * Gets the current angle rotation scale factor
         * 
         * @return the angle rotation scale factor
         */
    public double getLabelRotationScale() {
	return m_rotationScale;
    }

    protected void setPolarLocation( NodeItem n, NodeItem p,
	     double r, double t) {
	setX(n, p, m_origin.getX() + m_scaleX * r * Math.cos(t));
	setY(n, p, m_origin.getY() + m_scaleY * r * Math.sin(t));
    }

    public void run( double frac) {
	super.run(frac);

	if (m_rotationScale > 0) {
	     Graph g = (Graph) m_vis.getGroup(m_group);
	    if (m_checkRotationColumn) {
		g.addColumn("rotation", double.class);
		g.addColumn("startRotation", double.class);
		g.addColumn("endRotation", double.class);
		m_checkRotationColumn = false;
	    }
	     NodeItem ni = getLayoutRoot();
	    setRotation(ni, ni);
	}
    }

    protected void setRotation( NodeItem ni, NodeItem root) {

	NodeItem parentNode;
	double x = 0, y = 0;
	boolean noRotation = false;

	switch (m_orientation) {
	case ROOT_ORIENTATION:
	    x = root.getX();
	    y = root.getY();
	    break;
	case PARENT_ORIENTATION:
	    parentNode = (NodeItem) ni.getParent();
	    if (parentNode != null) {
		x = parentNode.getX();
		y = parentNode.getY();
	    } else {
		noRotation = true;
	    }
	    break;
	case CHILDREN_ORIENTATION:
	    parentNode = (NodeItem) ni.getParent();
	    if (parentNode != null) {
		 Iterator iter = ni.children();
		if (!iter.hasNext()) {
		    x = parentNode.getX();
		    y = parentNode.getY();
		} else {
		    int n = 0;
		    while (iter.hasNext()) {
			 NodeItem aChild = (NodeItem) iter.next();
			x += aChild.getX();
			y += aChild.getY();
			n++;
		    }
		    x /= n;
		    y /= n;
		}
	    } else {
		noRotation = true;
	    }
	    break;
	default:
	    throw new IllegalArgumentException(
		    "Unrecognized orientation value: " + m_orientation);
	}

	if (noRotation) {
	    setRotation(ni, 0);
	} else {
	    setRotation(ni, getRotationInDegree(Math.atan2(y - ni.getY(), x
		    - ni.getX())));
	}
	 Iterator childIter = ni.children();
	while (childIter.hasNext()) {
	    setRotation((NodeItem) childIter.next(), root);
	}
    }

    public UpdateListener getUpdateListener( String action) {
	return new MyUpdateListener(this, action);
    }

    /**
         * specifies the recale factors
         * 
         * @param x
         * @param y
         */
    public void setRescale( double x, double y) {

	 double min = x < y ? x : y;

	m_scaleX = x / min;
	m_scaleY = y / min;
    }

    private void setRotation( NodeItem ni, double rotation) {
	ni.setDouble("startRotation", ni.getDouble("rotation"));
	ni.setDouble("endRotation", rotation);
	ni.setDouble("rotation", rotation);
    }

    private double getRotationInDegree(double angle) {

	while (angle > MathLib.TWO_PI) {
	    angle -= MathLib.TWO_PI;
	}
	while (angle < 0) {
	    angle += MathLib.TWO_PI;
	}

	double degrees = Math.toDegrees(angle);

	if (degrees < 90) {
	    degrees *= m_rotationScale;
	} else if (degrees < 180) {
	    degrees = 180 - (180 - degrees) * m_rotationScale;
	} else if (degrees < 270) {
	    degrees = 180 + (degrees - 180) * m_rotationScale;
	} else {
	    degrees = 360 - (360 - degrees) * m_rotationScale;
	}

	return degrees;
    }

    private class MyUpdateListener extends UpdateListener {
	RadialRotationTreeLayout lay;

	String action;

	public MyUpdateListener( RadialRotationTreeLayout lay,
		 String action) {
	    super();
	    this.lay = lay;
	    this.action = action;
	}

	public void update( Object src) {
	}

	public void componentResized( java.awt.event.ComponentEvent e) {
	     double x = e.getComponent().getWidth();
	     double y = e.getComponent().getHeight();
	    lay.setRescale(x, y);
	    m_vis.run(action);
	}
    }
}
