package profusians.zonemanager.zone.shape;

import profusians.zonemanager.zone.Zone;

abstract public class DefaultZoneShape {

    protected float m_centerX;

    protected float m_centerY;

    protected float m_startCenterX = 0;

    protected float m_startCenterY = 0;

    protected float m_endCenterX = 0;

    protected float m_endCenterY = 0;

    /**
         * Returns the x position of the center of the zone
         * 
         * @return the x position of the center
         */

    public float getCenterX() {
	return m_centerX;
    }

    /**
         * Returns the y position of the center of the zone
         * 
         * @return the y position of the center
         */

    public float getCenterY() {
	return m_centerY;
    }

    public float getStartCenterX() {
	return m_startCenterX;
    }

    public float getStartCenterY() {
	return m_startCenterY;
    }

    public float getEndCenterX() {
	return m_endCenterX;
    }

    public float getEndCenterY() {
	return m_endCenterY;
    }

    /**
         * Sets the x position of the center of the zone
         * 
         * @param centerX
         *                the x position
         */
    public void setCenterX( float centerX) {
	m_centerX = centerX;
    }

    public void setStartCenterX( float startCenterX) {
	m_startCenterX = startCenterX;
    }

    public void setEndCenterX( float endCenterX) {
	m_endCenterX = endCenterX;
    }

    /**
         * Sets the y position of the center of the zone
         * 
         * @param centerY
         *                the y position
         */

    public void setCenterY( float centerY) {
	m_centerY = centerY;
    }

    public void setStartCenterY( float startCenterY) {
	m_startCenterY = startCenterY;
    }

    public void setEndCenterY( float endCenterY) {
	m_endCenterY = endCenterY;
    }

    public void updateStartValues() {

	m_startCenterX = m_centerX;
	m_startCenterY = m_centerY;
    }

    public void updateEndValues() {
	m_endCenterX = m_centerX;
	m_endCenterY = m_centerY;
    }

    public void animateShape( double frac) {
	m_centerX = (float) ((1 - frac) * m_startCenterX + frac * m_endCenterX);
	m_centerY = (float) ((1 - frac) * m_startCenterY + frac * m_endCenterY);
    }

    public static float getDefaultGravConst( ZoneShape zShape) {

	 Class zoneClass = zShape.getZoneClass();
	try {
	    return ((Float) (zoneClass
		    .getMethod("getDefaultGravConstant", null).invoke(null,
		    null))).floatValue();
	} catch ( Exception e) {
	    System.out.println("Unknown zone class " + e.getMessage());
	    return Zone.getDefaultGravConstant();
	}

    }

}
