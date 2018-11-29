package profusians.zonemanager;

import java.awt.Graphics2D;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.Layout;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.PolygonRenderer;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import profusians.zonemanager.layout.ZoneAggregateLayout;
import profusians.zonemanager.layout.ZoneConvexHullAggregateLayout;
import profusians.zonemanager.render.ZoneShapeRenderer;
import profusians.zonemanager.util.ZoneMap;
import profusians.zonemanager.zone.DefaultZoneFactory;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.zone.ZoneFactory;
import profusians.zonemanager.zone.aggregate.DefaultZoneAggregateItemFieldValueAssignment;
import profusians.zonemanager.zone.aggregate.ZoneAggregateItemFieldValueAssignment;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.io.ZoneFileHandler;
import profusians.zonemanager.zone.shape.DefaultZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * 
 * Central class of the zonemanager package, managing user defined zones and
 * items associated with this zones.
 * 
 * Check the demos coming along with package and the documentation to learn how
 * to use this class.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek </a>
 * @version beta
 */

public class ZoneManager {

    public static final String ZONEAGGREGATES = "zone aggregates";

    public static final int DEFAULTZONERENDERER = 1;

    public static final int CONVEXHULLZONERENDERER = 2;

    
    protected  ZoneMap m_zoneMap;
    
    protected Visualization m_vis;

    protected ForceSimulator m_fsim;

    protected AggregateTable m_zoneAggregateTable;

    private boolean m_interactiveAggregates = false;

    protected ZoneFactory m_zoneFactory;
    
    private ZoneAggregateItemFieldValueAssignment m_aggregateItemFieldAssignment;

    /**
     * Creates a new zonemanager instance associated with the given
     * visualization
     * 
     * @param vis
     *                the visualization for which this instance of
     *                zonemanager should act
     */

    public ZoneManager( Visualization vis, ForceSimulator fsim) {

	
	m_vis = vis;

	m_fsim = fsim;
	
	m_zoneMap = new ZoneMap();

	setZoneFactory(new DefaultZoneFactory());

	m_zoneAggregateTable = m_vis.addAggregates(ZONEAGGREGATES);

	m_zoneAggregateTable.addColumn("zoneName", String.class);
	m_zoneAggregateTable.addColumn("zoneInfo", String.class);
	m_zoneAggregateTable.addColumn("zoneNumber", int.class);
	m_zoneAggregateTable.addColumn("zoneType", String.class);
	m_zoneAggregateTable.addColumn("zoneClass", Class.class);

	m_zoneAggregateTable.addColumn(VisualItem.POLYGON, float[].class); // for convex hull zone layout
	
	m_aggregateItemFieldAssignment = new DefaultZoneAggregateItemFieldValueAssignment();

    }

    /**
     * Adds further columns to the zone aggregate table
     * 
     * @param name
     *                the data field name for the column
     * @param type
     *                the data type, as a Java Class, for the column
     */

    public void addColumnToZoneAggregateTable( String name,
	    Class type) {
	m_zoneAggregateTable.addColumn(name, type);
    }

    /**
     * Add the item to this zone. If the item previously belonged to another
     * zone managed by this zone manager instance, it will be removed
     * automatically from that zone.
     * 
     * @param aNodeItem
     *                the item to be added
     * @param zoneNumber
     *                the number of the zone to which the item should be
     *                added
     */

    public void addItemToZone( NodeItem aNodeItem, int zoneNumber) {

	removeItemFromZone(aNodeItem);
	
	Zone theZone = m_zoneMap.getZone(zoneNumber); 
	    
	if (theZone == null) {
	    return;
	}
	theZone.addItem(aNodeItem);
	
	m_zoneMap.mapItemToZone(aNodeItem, zoneNumber);
	
	AggregateItem at = (AggregateItem) m_zoneAggregateTable
	.getItem(m_zoneMap.getAggregateRow(zoneNumber));
	at.addItem(aNodeItem);

    }

    /**
     * adds the item to this zone
     * 
     * @param aNodeItem
     *                the item to be added
     * @param name
     *                the name of the zone to which the item should be added
     */

    public void addItemToZone( NodeItem aNodeItem, String name) {
	addItemToZone(aNodeItem, getZoneNumber(name));
    }

    /**
     * add the item to this zone and catches is, should be means ensured
     * that the item is placed within the borders of the zone
     * 
     * @param aNodeItem
     *                the item to be added
     * @param zoneNumber
     *                the number of the zone to which the item should be
     *                added
     */

    public void addItemToZoneAndCatch( NodeItem aNodeItem,
	    int zoneNumber) {

	if (zoneNumber == getZoneNumber(aNodeItem)) {
	    return;
	}
	removeItemFromZone(aNodeItem);
	addItemToZone(aNodeItem, zoneNumber);

	Zone theZone = m_zoneMap.getZone(zoneNumber);
	theZone.catchItem(aNodeItem);

    }

    /**
     * adds the item to this zone and catches is, should be means ensured
     * that the item is placed within the borders of the zone
     * 
     * @param aNodeItem
     *                the item to be added
     * @param name
     *                the name of the zone to which the item should be added
     */

    public void addItemToZoneAndCatch( NodeItem aNodeItem,
	    String name) {
	addItemToZoneAndCatch(aNodeItem, getZoneNumber(name));
    }

    /**
     * Adds the given zone to zonemanager
     * 
     * @param aZone
     *                the zone to be added.
     * @return the number of the zone
     */

    public int addZone( Zone aZone) {

	m_zoneMap.mapZone(aZone);

	createAggregateItem(aZone);

	return aZone.getNumber();
    }

    /**
     * Adding the color mapping responsible for the colors of the zone to
     * the given color action.
     * 
     * @param aFill
     *                the color action to which the zone item color mapping
     *                should be added
     * @return the given color action enriched with the new color mapping
     *         for the zone
     */

    public ColorAction addZoneAggregateColorMapping( ColorAction aFill) {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    aFill.add(getZoneAggregatePredicate(aZone), aZone.getColors()
		    .getFillColor());
	}
	return aFill;
    }

    /**
     * Adding the color mapping responsible for the colors of the items
     * belonging to one zone to the given color action.
     * 
     * @param nFill
     *                the color action to which the zone item color mapping
     *                should be added
     * @return the given color action enriched with the new color mapping
     *         for the zone items
     */

    public ColorAction addZoneItemColorMapping( ColorAction nFill) {

	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    nFill.add(getZoneFocusGroupPredicate(aZone), aZone.getColors()
		    .getItemColor());
	}
	return nFill;
    }

    /**
     * adds the default zone renderer responsible for drawing the zones to
     * the given default renderer factory
     * 
     * @param drf
     *                the default renderer factory to which the zone
     *                renderer should be added
     */

    public void addZoneRenderer( DefaultRendererFactory drf) {
	drf.add("ingroup('" + ZoneManager.ZONEAGGREGATES + "')",
		new ZoneShapeRenderer(this));
	positionZoneAggregates();
    }

    /**
     * adds the zone renderer of the given layout type to the given default
     * renderer factory
     * 
     * @param drf
     *                the default renderer factory to which the zone
     *                renderer should be added
     * @param types
     *                The zone renderer type Possible types at the moment:
     *                ZoneManager.DEFAULTZONERENDERER default renderer
     *                drawing the zones according tho their shape
     *                ZoneManager.CONVEXHULLZONERENDERER renderer drawing a
     *                convex hull around the nodes within the zone (as done
     *                in the AggreagteDemo of the prefuse demos)
     * 
     * 
     */

    public void addZoneRenderer( DefaultRendererFactory drf, int type)

    {
	if (type == ZoneManager.CONVEXHULLZONERENDERER) {
	    drf.add("ingroup('" + ZoneManager.ZONEAGGREGATES + "')",
		    new PolygonRenderer(Constants.POLY_TYPE_CURVE));
	} else {
	    drf.add("ingroup('" + ZoneManager.ZONEAGGREGATES + "')",
		    new ZoneShapeRenderer(this));
	}
	positionZoneAggregates();
    }

    // -----------

    /**
     * adds the zones specified in this zone xml file to the zonemanager
     * 
     * @param fr
     *                filereader reading the xml file
     * @return true if the file could be red successfully, otherwise false
     */

    public boolean addZonesFromFile( FileReader fr) {

	boolean result = ZoneFileHandler.addZonesFromFile(this, fr);
	return result;
    }

    /**
     * adds the zones specified in this zone xml file to the zonemanager
     * 
     * @param location
     *                the location of the zone xml file (path and name)
     * @return true if the file could be red successfully, otherwise false
     */

    public boolean addZonesFromFile( String location) {

	boolean result = ZoneFileHandler
	.addZonesFromFile(this, location);
	return result;
    }

    /**
     * adds the zones specified in this zone xml file URL to the zonemanager
     * 
     * @param fr
     *                filereader reading the xml file
     * @return true if the file could be red successfully, otherwise false
     */

    public boolean addZonesFromUrl( URL zoneXmlUrl) {
	boolean result = ZoneFileHandler.addZonesFromUrl(this,
		zoneXmlUrl);
	return result;
    }

    /**
     * catches all items of all zones
     * 
     */

    public void catchAll() {
	catchAll(true);
    }

    /**
     * Catches all items of all zones
     * 
     * @param onlyIfOutSide
     *                if true, only items outside the zone borders are
     *                catched; otherwise also items within their zones are
     *                catched, which means arranged close to the center of
     *                the zone
     */

    public void catchAll( boolean onlyIfOutSide) {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    catchAllItemsOfZone(aZone.getNumber(), onlyIfOutSide);
	}

    }

    /**
     * Catches all items of this zone
     * 
     * @param zoneNumber
     *                the number of the zone
     */

    public void catchAllItemsOfZone( int zoneNumber) {
	catchAllItemsOfZone(zoneNumber, true);
    }

    /**
     * Catches all items of this zone
     * 
     * @param zoneNumber
     *                the number of the zone
     * @param onlyIfOutSide
     *                if true, only items outside the zone borders are
     *                catched; otherwise also items within their zones are
     *                catched, which means arranged close to the center of
     *                the zone
     */

    public void catchAllItemsOfZone( int zoneNumber,
	    boolean onlyIfOutSide) {

	Zone theZone = m_zoneMap.getZone(zoneNumber);
	theZone.catchAllItems(onlyIfOutSide);

    }

    /**
     * Catches all items of this zone
     * 
     * @param name
     *                the name of the zone
     */

    public void catchAllItemsOfZone( String name) {
	catchAllItemsOfZone(name, true);
    }

    /**
     * Catches all items of this zone
     * 
     * @param name
     *                the name of the zone
     * @param onlyIfOutSide
     *                if true, only items outside the zone borders are
     *                catched; otherwise also items within their zones are
     *                catched, which means arranged close to the center of
     *                the zone
     */

    public void catchAllItemsOfZone( String name,
	    boolean onlyIfOutSide) {
	catchAllItemsOfZone(getZoneNumber(name), onlyIfOutSide);
    }

    private void createAggregateItem(Zone aZone) {
	
	AggregateItem aAItem = (AggregateItem) m_zoneAggregateTable
	.addItem();
	
	m_zoneMap.mapZoneNumberToAggregateRow(aZone.getNumber(),aAItem.getRow());

	m_aggregateItemFieldAssignment.fillFields(aAItem, aZone);
	
	aAItem.setInteractive(false);

	fillAdditionalFieldsOfZoneAggregate(aAItem);

    }

    /**
     * Creates the zone specified through the zone shape and adds this zone
     * to zonemanager
     * 
     * @param zShape
     *                the shape of the zone
     * @return the number of the newly created zone
     */

    public int createAndAddZone( ZoneShape zShape) {
	ZoneColors zColors = new ZoneColors();
	ZoneAttributes zAttributes = new ZoneAttributes(DefaultZoneShape
		.getDefaultGravConst(zShape));
	return createAndAddZone(zShape, zColors, zAttributes);
    }

    /**
     * Creates the zone specified through the zone shape and attributes.
     * This newly created zone is then added to zonemanager.
     * 
     * @param zShape
     *                the shape of the zone
     * @param zAttributes
     *                the attributes of the zone
     * @return the number of the zone
     */
    public int createAndAddZone( ZoneShape zShape,
	    ZoneAttributes zAttributes) {
	ZoneColors zColors = new ZoneColors();
	return createAndAddZone(zShape, zColors, zAttributes);
    }

    /**
     * Creates the zone specified through the zone shape and colors. This
     * newly created zone is then added to zonemanager.
     * 
     * @param zShape
     *                the shape of the zone
     * @param zColors
     *                the colors of the zone
     * @return the number of the newly created zone
     */

    public int createAndAddZone( ZoneShape zShape, ZoneColors zColors) {
	ZoneAttributes zAttributes = new ZoneAttributes(DefaultZoneShape
		.getDefaultGravConst(zShape));
	return createAndAddZone(zShape, zColors, zAttributes);
    }

    /**
     * Creates the zone specified through the zone shape, colors and
     * attributes. This newly created zone is then added to zonemanager.
     * 
     * @param zShape
     *                the shape of the zone
     * @param zColors
     *                the colors of the zone
     * @param zAttributes
     *                the attributes of the zone
     * @return the number of the zone
     */

    public int createAndAddZone( ZoneShape zShape,
	    ZoneColors zColors, ZoneAttributes zAttributes) {

	Zone aZone = m_zoneFactory.getZone(zShape, zColors, zAttributes);

	return addZone(aZone);

    }

    /**
     * triggers the drawing of all borders of all zones
     * 
     * @param d
     *                the display on which the zone broders should be drawn
     * @param g
     *                the Graphics context for the Display
     */

    public void drawAllBorders( Display d, Graphics2D g) {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    aZone.drawBorder(d, g);
	}

    }

    /**
     * This method is called when a new zone is added to the zone manager.
     * The default columns zoneName, zoneType and zoneNumber are already
     * filled The additional column zoneInfo and further columns, which had
     * been added through the method addColumnsToAggrTable() can be filled
     * by overriding this method.
     */

    public void fillAdditionalFieldsOfZoneAggregate( AggregateItem aAitem) {

    }

    /**
     * 
     * @return the force simulator used by zonemanager
     */
    public ForceSimulator getForceSimulator() {
	return m_fsim;

    }

    /**
     * get the number of zones which are at the moment managed by this
     * zonemanager instance
     * 
     * @return the number of zones
     */

    public int getNumberOfZones() {
	return m_zoneMap.getNumberOfZones();
    }

    /**
     * get this zone
     * 
     * @param zoneNumber
     *                the number of the zone
     * @return the zone
     */

    public Zone getZone( int zoneNumber) {

	return m_zoneMap.getZone(zoneNumber);
    }

    /**
     * get this zone
     * 
     * @param name
     *                the name of the zone
     * @return the zone
     */

    public Zone getZone( String name) {
	return getZone(getZoneNumber(name));
    }

    /**
     * returns the aggregate item which was created for this zone
     * 
     * @param zoneNumber
     *                the number of the zone
     * @return the aggregate item belonging to the given zone
     */

    public AggregateItem getZoneAggregateItemOfZone( int zoneNumber) {
	return (AggregateItem) m_zoneAggregateTable.getItem(m_zoneMap.getAggregateRow(zoneNumber));
    }

    /**
     * returns the aggregate item which was created for this zone
     * 
     * @param name
     *                the name of the zone
     * @return the aggregate item belonging to the given zone
     */
    public AggregateItem getZoneAggregateItemOfZone( String name) {
	return getZoneAggregateItemOfZone(getZoneNumber(name));
    }

    /**
     * Returns a predicate indicating if a zone aggregate belongs to this
     * zone
     * 
     * @param aZone
     *                the zone
     * @return the zone aggregate predicate
     */

    public Predicate getZoneAggregatePredicate( Zone aZone) {
	return ExpressionParser.predicate("ingroup('"
		+ ZoneManager.ZONEAGGREGATES + "') and [zoneName] = \""
		+ aZone.getName() + "\"");
    }

    /**
     * For each zone an aggregate is created by zonemanager. This aggregate
     * is used to realize the drawing of the zones, but can also be used in
     * addition for other purposes
     * 
     * @return the aggreate table containing all the zone aggregates
     */
    public AggregateTable getZoneAggregateTable() {
	return m_zoneAggregateTable;
    }

    /**
     * Get the color action responsible for the color mapping of the zone
     * drawing
     * 
     * @return the color action for the zone drawing
     */
    public ColorAction getZoneColorAction() {
	ColorAction nFill = new ColorAction(ZoneManager.ZONEAGGREGATES,
		VisualItem.FILLCOLOR);
	return addZoneAggregateColorMapping(nFill);

    }

    /**
     * 
     * @return the zone factory used by this {@link ZoneManager} instance
     */
    public ZoneFactory getZoneFactory() {
	return m_zoneFactory;
    }
    
    /**
     * Sets the zone factory to be used by this {@link ZoneManager} instance
     * 
     * @param zoneFactory
     */

    public void setZoneFactory( ZoneFactory zoneFactory) {
	m_zoneFactory = zoneFactory;
	m_zoneFactory.setForceSimulator(m_fsim);
	m_zoneFactory.setVisualization(m_vis);
    }

    public void setZoneAggregateItemFieldValueAssignment(ZoneAggregateItemFieldValueAssignment fieldAssignment) {
	m_aggregateItemFieldAssignment = fieldAssignment;
    }
    
    public ZoneAggregateItemFieldValueAssignment getZoneAggregateItemFieldValueAssignment() {
	return m_aggregateItemFieldAssignment;
    }

    /**
     * Returns a predicate which indicates the belonging of an node item to the
     * focus group of this zone
     * 
     * @param aZone
     *                the zone
     * @return the zone focus group predicate
     */

    public Predicate getZoneFocusGroupPredicate( Zone aZone) {
	return ExpressionParser.predicate("ingroup('" + aZone.getName() + "')");
    }

    /**
     * Get the default layout responsible for adjusting the position and size of the zone
     * aggregates, which are used to draw the zones if requested
     * 
     * @return the zone layout
     */

    public Layout getZoneLayout() {
	return getZoneLayout(ZoneManager.DEFAULTZONERENDERER);

    }

    /**
     * gets the default zone layout
     * 
     * @param animate
     *                it true, changes of flexible zones are animated (only
     *                meaningful for finite actions)
     * @return the default zone layout
     */

    public Layout getZoneLayout( boolean animate) {
	return new ZoneAggregateLayout(this, animate);
    }

    public Layout getZoneLayout( boolean animate, boolean drawIfEmpty) {
	return new ZoneAggregateLayout(this, animate, drawIfEmpty);
    }

    /**
     * Get the layout belonging the specified renderer type responsible for
     * adjustment of the zone aggregates etc
     * 
     * 
     * @param types
     *                The type of the zone rendering Possible types at the
     *                moment: ZoneManager.DEFAULTZONERENDERER default
     *                renderer drawing the zones according tho their shape
     *                ZoneManager.CONVEXHULLZONERENDERER renderer drawing a
     *                convex hull around the nodes within the zone (as done
     *                in the AggreagteDemo of the prefuse demos)
     * 
     * @return the zone layout
     */

    public Layout getZoneLayout( int type) {
	if (type == ZoneManager.CONVEXHULLZONERENDERER) {
	    return new ZoneConvexHullAggregateLayout(this,
		    ZoneManager.ZONEAGGREGATES);
	} else {
	    return new ZoneAggregateLayout(this);
	}
    }

    /**
     * get the number of the zone this item belongs to
     * 
     * @param ni
     *                the node item
     * @return the number of the zone if any, otherwise -1
     */

    public int getZoneNumber( NodeItem ni) {
	return m_zoneMap.getZoneNumber(ni);
    }

    /**
     * get the number of this zone
     * 
     * @param name
     *                the name of the zone
     * @return
     */
    public int getZoneNumber( String name) {
	return m_zoneMap.getZoneNumber(name);
    }

    /**
     * get a list of all zones currently managed by this zonemanager
     * 
     * @return a list of all zones managed by this zonemanager instance
     */

    public HashMap getZones() {
	return m_zoneMap.getZones();
    }
    
    /**
     * Returns all numbers of the zones managed by zone manager
     * @return
     */
    public int[] getZoneNumbers() {
	return m_zoneMap.getZoneNumbers();
	
    }

    /**
     * Checks if the zone aggregates are active
     * 
     * @return true if the aggregates are active, false otherwise
     */

    public boolean hasInteractiveAggregates() {
	return m_interactiveAggregates;
    }

    

    private void positionZoneAggregates() {

	AggregateTable aggr = getZoneAggregateTable();

	if (aggr != null) {
	    for ( Iterator aggrs = aggr.tuples(); aggrs.hasNext();) {
		AggregateItem aAitem = (AggregateItem) aggrs.next();
		int zoneNumber = aAitem.getInt("zoneNumber");
		Zone aZone = getZone(zoneNumber);
		aZone.positionZoneAggregate(aAitem);
	    }
	}

    }

    /**
     * Recalculates all flexible zones and flexible items within managed by
     * this zonemanager instance.
     */

    public void recalculateFlexibility() {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    recalculateFlexibility(((Zone)iter.next()).getNumber());
	}

    }

    /**
     * Recalculates the flexibility of the zone specified by the given zone
     * number.
     * 
     * @param zoneNumber
     *                the zone number of the zone
     */

    public void recalculateFlexibility( int zoneNumber) {
	getZone(zoneNumber).recalculateFlexibility();
    }

    /**
     * Recalculates the flexibility of the zone specified by the given zone
     * name.
     * 
     * @param zoneName
     *                of th ename of the zone
     */

    public void recalculateFlexibility( String zoneName) {
	getZone(zoneName).recalculateFlexibility();
    }

    /**
     * removes all items from all zones
     * 
     */

    public void removeAllItems() {
	
	Iterator iter = m_zoneMap.getZoneNumberIterator();
	
	ArrayList sl = new ArrayList();
	
	while (iter.hasNext()) {
	    sl.add(iter.next());
	}
	
	Collections.sort(sl); 
	
	/**
	 * A non sorted collections leads sometimes to a runtime error, which
	 * shouldn't be the case of course. Not clear yet where the bug is ...
	 * 
	 */
	
	Iterator sortedIter = sl.iterator();
	while (sortedIter.hasNext()) {
	    removeAllItemsFromZone(((Integer)sortedIter.next()).intValue(), false);
	}
	
	m_zoneMap.clearItemMapping();

    }

    /**
     * removes all items for this zone
     * 
     * @param zoneNumber
     *                the number of the zone
     */

    public void removeAllItemsFromZone( int zoneNumber) {
	removeAllItemsFromZone(zoneNumber, true);
    }

   

    private void removeAllItemsFromZone( int zoneNumber,
	    boolean clearMemberList) {
	Zone theZone = (Zone) m_zoneMap.getZone(zoneNumber);
	theZone.removeAllItems();

	if (clearMemberList) {
	    List toBeRemoved = new ArrayList();
	    for ( Iterator i = m_zoneMap.getNodeItemIterator(); i
	    .hasNext();) {
		NodeItem aNodeItem = (NodeItem) i.next();
		if (m_zoneMap.getZoneNumber(aNodeItem) == zoneNumber) {
		    toBeRemoved.add(aNodeItem);
		}

	    }
	    for (int i = 0; i < toBeRemoved.size(); i++) {
		m_zoneMap.removeItemMapping((NodeItem)toBeRemoved.get(i));
	    }
	}

	AggregateItem at = (AggregateItem) m_zoneAggregateTable
	.getItem(m_zoneMap.getAggregateRow(zoneNumber));
	at.removeAllItems();
	
    }

    /**
     * removes all item for this zone
     * 
     * @param name
     *                the name of this zone
     */

    public void removeAllItemsFromZone( String name) {
	removeAllItemsFromZone(getZoneNumber(name));
    }

    /**
     * Removes all zones from zone manager
     * 
     */

    public void removeAllZones() {
	ArrayList allZones = new ArrayList();
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    allZones.add(aZone);
	}
	Iterator zoneIter = allZones.iterator();
	while (zoneIter.hasNext()) {
	    removeZone((Zone) zoneIter.next());
	}
    }

    /**
     * remove this item from its zone.
     * 
     * @param aNodeItem
     *                the item to be removed
     */

    public void removeItemFromZone( NodeItem aNodeItem) {

	int zoneNumber = getZoneNumber(aNodeItem);
	if (zoneNumber == -1) {
	    return;
	}

	Zone theZone = m_zoneMap.getZone(zoneNumber);
	if (theZone != null) {
	    theZone.removeItem(aNodeItem);
	}

	AggregateItem at = (AggregateItem) m_zoneAggregateTable
	.getItem(m_zoneMap.getAggregateRow(zoneNumber));
	at.removeItem(aNodeItem);

    }

    /**
     * Removes the zone with the given zone number from zone manager
     * 
     * @param zoneNumber
     *                the number of the zone
     * @return true, if the zone was managed by zone manager, otherwise
     *         false
     */
    public boolean removeZone( int zoneNumber) {
	return removeZone(getZone(zoneNumber));
    }

    /**
     * Removes the zone with the given zone name from zone manager
     * 
     * @param zoneName
     *                the name of the zone
     * @return true, if the zone was managed by zone manager, otherwise
     *         false
     */
    public boolean removeZone( String zoneName) {
	return removeZone(getZone(zoneName));
    }

    /**
     * Removes the given zone from zone manager
     * 
     * @param aZone
     *                the zone to be removed
     * @return true, if the zone was managed by zone manager, otherwise
     *         false
     */

    public boolean removeZone( Zone aZone) {
	String zoneName = aZone.getName();

	if (!m_zoneMap.contains(zoneName)) {
	    return false;
	}
	int zoneNumber = aZone.getNumber();

	aZone.setActive(false); // removes the wall forces from the force
	// simulator

	removeAllItemsFromZone(zoneNumber);
	m_zoneAggregateTable.removeRow(m_zoneMap.getAggregateRow(zoneNumber));
	
	m_zoneMap.removeZone(aZone);

	return true;
    }

    /**
     * set all empty zones invisible
     * 
     */

    public void setAllEmptyZonesInvisible() {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    zoneVisibilityHelper(aZone.getNumber(), false, true);
	}
    }

    /**
     * set all non empty zones visible
     * 
     */
    public void setAllNonEmptyZonesVisible() {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    zoneVisibilityHelper(aZone.getNumber(), true, true);
	}
    }

    /**
     * set the active state of all zones
     * 
     * @param active
     *                if true, all zones are set active, otherwise inactive
     */

    public void setAllZonesActive( boolean active) {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    setZoneActive(aZone.getNumber(),
		    active);
	}
    }

    /**
     * set all zones visibility
     * 
     * @param value
     *                true to make the zone visible, false otherwise.
     */

    public void setAllZonesVisible( boolean value) {
	Iterator iter = m_zoneMap.getZoneIterator();
	while (iter.hasNext()) {
	    Zone aZone = (Zone) iter.next();
	    zoneVisibilityHelper(aZone.getNumber(), value, false);
	}
    }

    /**
     * set the force simulator which should be used to add/remove zoneforces
     * 
     * @param fsim
     */
    public void setForceSimulator( ForceSimulator fsim) {
	m_fsim = fsim;
    }

    /**
     * set the active state of this zones
     * 
     * @param active
     *                if true, this zone are is active, otherwise inactive
     */

    public void setZoneActive( int zoneNumber, boolean active) {
	Zone theZone = m_zoneMap.getZone(zoneNumber);

	theZone.setActive(active);
    }

    /**
     * set the active state of this zones
     * 
     * @param active
     *                if true, this zone are is active, otherwise inactive
     */

    public void setZoneActive( String zoneName, boolean active) {
	setZoneActive(getZoneNumber(zoneName), active);
    }

    /**
     * create a zone name including the current time informations
     * 
     * @return the newly created zone name
     */

    /**
     * Sets the zone aggregates active/interactive
     * 
     * @param interactive
     *                if true all zone aggregaets are set active, otherwise
     *                all are set inactive
     */

    public void setZoneAggregatesInteractive( boolean interactive) {
	AggregateTable aggr = getZoneAggregateTable();

	if (aggr != null) {
	    m_interactiveAggregates = interactive;
	    for ( Iterator aggrs = aggr.tuples(); aggrs.hasNext();) {
		AggregateItem aAitem = (AggregateItem) aggrs.next();
		aAitem.setInteractive(interactive);
	    }
	} else {
	    m_interactiveAggregates = false;
	}
    }

    
    /**
     * set this zone invisible if it is empty
     * 
     * @param zoneNumber
     *                the number of the zone the aggreate belongs to
     */

    public void setZoneInvisibleIfEmpty( int zoneNumber) {
	zoneVisibilityHelper(zoneNumber, false, true);
    }

    /**
     * set this zone invisible if it is empty
     * 
     * @param name
     *                the name of the zone
     */

    public void setZoneInvisibleIfEmpty( String name) {
	zoneVisibilityHelper(getZoneNumber(name), false, true);
    }

    // private stuff

    /**
     * set this zone visiblity
     * 
     * @param zoneNumber
     *                the number of the zone
     * @param value
     *                true to make the item visible, false otherwise
     */
    public void setZoneVisible( int zoneNumber, boolean value) {
	zoneVisibilityHelper(zoneNumber, value, false);
    }

    /**
     * set this zone visiblity
     * 
     * @param name
     *                the name of the zone
     * @param value
     *                true to make the item visible, false otherwise
     */
    public void setZoneVisible( String name, boolean value) {
	zoneVisibilityHelper(getZoneNumber(name), value, false);
    }

    /**
     * set this zone visible if it is not empty
     * 
     * @param zoneNumber
     *                the number of the zone the aggregate belongs to
     */

    public void setZoneVisibleIfNonEmpty( int zoneNumber) {
	zoneVisibilityHelper(zoneNumber, true, true);
    }

    /**
     * set this zone visible if it is not empty
     * 
     * @param name
     *                the name of the zone the aggregate belongs to
     */

    public void setZoneVisibleIfNonEmpty( String name) {
	zoneVisibilityHelper(getZoneNumber(name), true, true);
    }

    private void zoneVisibilityHelper( int zoneNumber, boolean visible,
	    boolean checkIfEmptyNonEmpty) {
	AggregateItem aItem = (AggregateItem) m_zoneAggregateTable
	.getItem(m_zoneMap.getAggregateRow(zoneNumber));
	if (checkIfEmptyNonEmpty) {
	    if (visible && (aItem.getAggregateSize() == 0)) {
		return;
	    } else if ((!visible) && (aItem.getAggregateSize() > 0)) {
		return;
	    }
	}
	aItem.setVisible(visible);
    }    
}
