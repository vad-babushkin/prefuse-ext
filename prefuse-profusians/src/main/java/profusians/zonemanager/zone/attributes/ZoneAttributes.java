package profusians.zonemanager.zone.attributes;

import java.util.HashSet;

import profusians.zonemanager.zone.Zone;


public class ZoneAttributes {

    static int m_lastZoneNumber = 0;

    static HashSet m_givenZoneNames;

    protected String m_zName;

    protected int m_zNumber;

    protected boolean m_flexibleZone = false;

    protected boolean m_flexibleItems = false;

    protected float m_gravConstant = Zone.getDefaultGravConstant();

    protected String m_info = "";

    public ZoneAttributes() {
	this("", false, false, Zone.getDefaultGravConstant(), "");
    }

    public ZoneAttributes( String name) {
	this(name, false, false, Zone.getDefaultGravConstant(), "");
    }

    public ZoneAttributes( float gravConst) {
	this("", false, false, gravConst, "");
    }

    public ZoneAttributes( String name, float gravConst) {
	this(name, false, false, gravConst, "");
    }

    public ZoneAttributes(String name, boolean flexibleZone,
	     boolean flexibleItems, float gravConstant, String info) {

	if (name == null) {
	    name = "";
	}
	if (info == null) {
	    info = "";
	}

	name = name.trim();

	if (name.length() == 0) {
	    name = createZoneName();
	}
	m_zName = name;
	m_zNumber = m_lastZoneNumber++;

	m_flexibleZone = flexibleZone;
	m_flexibleItems = flexibleItems;
	m_gravConstant = gravConstant;
	m_info = info;

    }

    public int getZoneNumber() {
	return m_zNumber;
    }

    public String getZoneName() {
	return m_zName;
    }

    public void setZoneName( String name) {
	m_zName = name;
    }

    public boolean isFlexible() {
	return m_flexibleZone;
    }

    public void setFlexibleZone( boolean value) {
	m_flexibleZone = value;
    }

    public boolean hasFlexibleItems() {
	return m_flexibleItems;
    }

    public void setFlexibleItems( boolean value) {
	m_flexibleItems = value;
    }

    public float getGravConst() {
	return m_gravConstant;
    }

    public void setGravConst( float gravConstant) {
	m_gravConstant = gravConstant;
    }

    public String getInfo() {
	return m_info;
    }

    public void setInfo( String info) {
	m_info = info;
    }

    public String createZoneName() {
	String name = "";

	if (m_givenZoneNames == null) {
	    m_givenZoneNames = new HashSet();
	}

	do {
	    name = "zone " + System.currentTimeMillis();
	} while (m_givenZoneNames.contains(name));

	m_givenZoneNames.add(name);

	return name;
    }

}
