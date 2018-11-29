package profusians.zonemanager.zone.shape;

import java.awt.Graphics2D;
import java.awt.Shape;

import prefuse.Display;
import prefuse.visual.NodeItem;

public interface ZoneShape {

    public String getZoneType();

    public Class getZoneClass();

    /**
         * Checks if the zone shape contains this node item
         * 
         * @param aItem
         *                the node item to be checked
         * @return true if the zone contains the node item, false otherwise
         */

    public boolean contains(NodeItem aItem);

    public boolean containsAtStart(NodeItem aItem);

    public boolean containsAtEnd(NodeItem aItem);

    public float getCenterX();

    public float getCenterY();

    public float getStartCenterX();

    public float getStartCenterY();

    public float getEndCenterX();

    public float getEndCenterY();

    public void updateStartValues();

    public void updateEndValues();

    public void animateShape(double frac);

    public void setCenterX(float centerX);

    public void setCenterY(float centerY);

    public void drawBorder(Display d, Graphics2D g, int borderColor);

    public Shape getRawShape(double centerX, double centerY);

    public Object clone();

}
