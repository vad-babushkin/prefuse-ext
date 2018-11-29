package profusians.render;

import java.awt.geom.Point2D;

import prefuse.render.EdgeRenderer;
import prefuse.visual.EdgeItem;

/**
 * This edge renderer class draws curved edges in a wave like manner than. In
 * addition it is symmetric, should mean x and y distances between beginning and
 * end point of the curve are treated equal (in difference to the extend class
 * which only takes x distance into account, loosly spoken) An effect of this is
 * that the edge is straight if x and y distances between beginning and end
 * point are equal. Don't miss to play around with the waveSize parameter.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 */

public class WaveEdgeRenderer extends EdgeRenderer {

    double waveSize = 1;

    public double getWaveSize() {
	return waveSize;
    }

    public void setWaveSize( double size) {
	waveSize = size;
    }

    protected void getCurveControlPoints( EdgeItem eitem,
	     Point2D[] cp, double x1, double y1,
	     double x2, double y2) {
	 double dx = x2 - x1, dy = y2 - y1;

	 double c = Math.sqrt((dx * dx) + (dy * dy));

	 double radAngle = Math.acos(((Math.abs(dx)) / c));
	 double degAngle = radAngle * (180 / Math.PI);

	 double wx = getWeight(90 - degAngle);
	 double wy = getWeight(degAngle);

	cp[0].setLocation(x1 + (1 + wx) * dx / 3, y1 + (1 + wy) * dy / 3);
	cp[1].setLocation(x2 - (1 + wx) * dx / 3, y2 - (1 + wy) * dy / 3);

    }

    private double getWeight( double x) {
	return waveSize * (1 - Math.min(Math.abs(3 - x / 22.5), 1));
    }
}
