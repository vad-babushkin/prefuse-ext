package profusians.zonemanager.util;

import java.util.HashMap;
import java.util.Iterator;

import prefuse.visual.NodeItem;
import profusians.zonemanager.zone.Zone;


/**
 * Utility class handling all the mappings required by the zonemanager class. For internal use only.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek </a>
 */

public class ZoneMap {
    private HashMap m_allZones;

    private HashMap m_itemToZoneNumber;

    private HashMap m_zoneNameToZoneNumber;

    private HashMap m_zoneNumberToZoneAggregateRow;

    public ZoneMap() {
	m_allZones = new HashMap();
	m_itemToZoneNumber = new HashMap();
	m_zoneNameToZoneNumber = new HashMap();
	m_zoneNumberToZoneAggregateRow = new HashMap();
    }

    public HashMap getZones() {
	return m_allZones;
    }
    
    public int[] getZoneNumbers() {
	int[] result = new int[getNumberOfZones()];
	
	int i=0;
	Iterator iter = getZoneIterator();
	
	while (iter.hasNext()) {
	    result[i++] = (((Zone)iter.next()).getNumber());
	}
	return result;
    }
    
    public Zone getZone(int zoneNumber) {
	return (Zone) m_allZones.get(new Integer(zoneNumber));
    }

    public int getZoneNumber(NodeItem ni) {
	Object zoneNumber = m_itemToZoneNumber.get(ni);
	if (zoneNumber == null) {
	    return -1;
	} else {
	    return ((Integer)zoneNumber).intValue();
	}
    }

    public int getZoneNumber(String zoneName) {
	Object zoneNumber = m_zoneNameToZoneNumber.get(zoneName);
	if (zoneNumber == null) {
	    return -1;
	}
	return ((Integer)zoneNumber).intValue();
    }
    
    public boolean contains(String zoneName) {
	return m_zoneNameToZoneNumber.keySet().contains(zoneName);
    }

    public void mapItemToZone(NodeItem aNodeItem,int zoneNumber) {
	m_itemToZoneNumber.put(aNodeItem,new Integer(zoneNumber));
    }
    
    public void removeZone(Zone aZone) {
	m_allZones.remove(new Integer(aZone.getNumber()));
	m_zoneNameToZoneNumber.remove(aZone.getName());
	m_zoneNumberToZoneAggregateRow.remove(new Integer(aZone.getNumber()));
    }
    
    public void removeItemMapping(NodeItem ni) {
	m_itemToZoneNumber.remove(ni);
    }
    

    public int getAggregateRow(int zoneNumber) {
	return ((Integer)m_zoneNumberToZoneAggregateRow.get(new Integer(zoneNumber))).intValue();
    }
    
    public void mapZoneNumberToAggregateRow(int zoneNumber,int aggregateRow) {
	m_zoneNumberToZoneAggregateRow.put(new Integer(zoneNumber), new Integer(aggregateRow));
    }



    public void mapZone(Zone aZone) {
	m_allZones.put(new Integer(aZone.getNumber()), aZone);
	m_zoneNameToZoneNumber.put(aZone.getName(),new Integer(aZone.getNumber()));
    }

    public Iterator getZoneIterator() {
	return m_allZones.values().iterator();
    }
    
    public Iterator getZoneNumberIterator() {
	return m_allZones.keySet().iterator();
    }
    
    public Iterator getNodeItemIterator() {
	return m_itemToZoneNumber.keySet().iterator();
    }

    public int getNumberOfZones() {
	return m_allZones.size();
    }
    
    public void clearItemMapping() {
	m_itemToZoneNumber.clear();
    }
}
