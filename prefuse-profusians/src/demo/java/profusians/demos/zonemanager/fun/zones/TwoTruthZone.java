package profusians.demos.zonemanager.fun.zones;

import java.awt.Polygon;

import prefuse.Visualization;
import prefuse.util.force.ForceSimulator;
import profusians.zonemanager.zone.PolygonalZone;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.PolygonalZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

public class TwoTruthZone extends PolygonalZone {

    Polygon m_zeroPolygon;

    int m_numberOfPoints;

    public TwoTruthZone(Visualization vis, ForceSimulator fsim,
	    ZoneShape zShape, ZoneColors zColors, ZoneAttributes zAttributes,
	    int voidCenterX, int voidCenterY) {

	super(vis, fsim, zShape, zColors, zAttributes);

	m_numberOfPoints = ((PolygonalZoneShape) zShape).getNumberOfPoints();

	initZeroPolygon(voidCenterX, voidCenterY);

    }

    public Polygon getUpdatePolygon() {
	if (getNumberOfItems() == 0) {
	    return m_zeroPolygon;
	}
	return ((PolygonalZoneShape) m_initialZoneShape).getPolygon();
    }

    private void initZeroPolygon(int x, int y) {
	int[] zeroXpoints = new int[m_numberOfPoints];
	int[] zeroYpoints = new int[m_numberOfPoints];

	for (int i = 0; i < m_numberOfPoints; i++) {
	    zeroXpoints[i] = x;
	    zeroYpoints[i] = y;
	}
	m_zeroPolygon = new Polygon(zeroXpoints, zeroYpoints, m_numberOfPoints);
    }

}
