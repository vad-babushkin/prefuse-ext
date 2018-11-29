package profusians.zonemanager.zone.colors;

import java.awt.Color;

import prefuse.util.ColorLib;

/**
 * Class representing the colors associated to a zone
 * 
 * @author goose
 * 
 */
public class ZoneColors {
    int m_itemColor;

    int m_fillColor;

    int m_borderColor = ColorLib.color(Color.GRAY);

    public ZoneColors() {

    }

    public ZoneColors( int itemColor, int fillColor) {
	m_itemColor = itemColor;
	m_fillColor = fillColor;
    }

    public int getItemColor() {
	return m_itemColor;
    }

    public int getFillColor() {
	return m_fillColor;
    }

    public int getBorderColor() {
	return m_borderColor;
    }

    public void setItemColor( int itemColor) {
	m_itemColor = itemColor;
    }

    public void setFillColor( int zoneColor) {
	m_fillColor = zoneColor;
    }

    public void setBorderColor( int borderColor) {
	m_borderColor = borderColor;
    }

}
