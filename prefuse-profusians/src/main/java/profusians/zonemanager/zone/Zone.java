package profusians.zonemanager.zone;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Iterator;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.data.tuple.TupleSet;
import prefuse.util.force.Force;
import prefuse.util.force.ForceSimulator;
import prefuse.visual.AggregateItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * 
 * Abstract base class for Zone implementations.
 * 
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */
abstract public class Zone {

    protected Visualization m_vis;

    protected static float m_defaultGravConstant = -1.11f;

    protected ZoneShape m_zoneShape;

    protected ZoneShape m_initialZoneShape;

    protected ZoneColors m_zColors;

    protected ZoneAttributes m_zAttributes;

    protected TupleSet m_items;

    protected ForceSimulator m_fsim;

    protected Force[] m_forces;

    protected boolean m_active = true;

    public Zone() {

    }

    /**
         * Create a new zone
         * 
         */

    public Zone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneColors zoneColors,
	     ZoneAttributes zoneAttributes) {
	this(vis, fsim, zoneShape, zoneColors, zoneAttributes, 1);
    }

    /**
         * Create a new zone
         * 
         * @param vis
         *                the visualization this zone is assoicated with
         * @param fsim
         *                the forcesimulator which handles the zone forces. This
         *                forcesimulator must be of the class
         *                ForceSimulatorRemovableForces of the the profusians
         *                package if the zone is flexible.
         * @param zoneShape
         *                the shape of the zone
         * @param zoneColors
         *                the colors of the zone
         * @param zoneAttributes
         *                the attributes of the zone
         * @param numberOfForces
         *                the number of forces used by this zone maintains (at
         *                the moment always 1)
         */

    public Zone( Visualization vis, ForceSimulator fsim,
	     ZoneShape zoneShape, ZoneColors zoneColors,
	     ZoneAttributes zoneAttributes, int numberOfForces) {

	m_vis = vis;

	m_zoneShape = zoneShape;
	m_initialZoneShape = (ZoneShape) zoneShape.clone();

	m_zColors = zoneColors;
	m_zAttributes = zoneAttributes;

	m_fsim = fsim;

	m_vis.addFocusGroup(zoneAttributes.getZoneName());
	m_items = m_vis.getFocusGroup(zoneAttributes.getZoneName());

	m_forces = new Force[numberOfForces];

	calculateForces();
	addForcesToFsim();

    }

    /**
         * This abstract method is called whenever a flexible zone has to be
         * updated and has to be implemented according the zone specifics.
         * 
         */

    abstract public void updateZone();

    /**
         * This abstract method calculates the walll forces of the zone
         * according to the current zone specifics. This method is called
         * initially when the zone is created and whenever a flexible zone
         * changes its position and/or size. This mehtod has to be implemented
         * according the zone specifics.
         * 
         * 
         */
    abstract public void calculateForces();

    /**
         * Returns the type of this zone
         * 
         * @return the zone type
         */

    abstract public String getType();

    /**
         * Checks if this nodeitem is placed with the zone borders. If the
         * onlyIfOutside parameter is false, only items outside the zone borders
         * are replaced newly within the zone borders, otherwise all items
         * regardless of their actual position. This abstract method has to be
         * overriden by subclasses.
         * 
         * 
         * @param aItem
         *                the item to be catched
         * @param onlyIfOutside
         *                if true only items outside the zone borders are
         *                replaced newly within the zone borders, otherwise all
         *                items regardless of their actual position
         */
    abstract public void catchItem(NodeItem aItem, boolean onlyIfOutside);

    abstract public void positionZoneAggregate(AggregateItem aAItem);

    abstract public void animateZoneAggregate(AggregateItem aAItem, double frac);

    public void updateStartValues() {
	m_zoneShape.updateStartValues();
    }

    public void updateEndValues() {
	m_zoneShape.updateEndValues();
    }

    /**
         * Checks if the zone contains this node item
         * 
         * @param aItem
         *                the node item to be checked
         * @return true if the zone contains the node item, false otherwise
         */

    public boolean contains( NodeItem aItem) {
	return m_zoneShape.contains(aItem);
    }

    /**
         * Returns the raw shape of the zone
         * 
         * @param x
         *                the x position of the zone
         * @param y
         *                the y position of the zone
         * @return the raw shape
         */

    public Shape getRawShape( double x, double y) {
	return m_zoneShape.getRawShape(x, y);
    }

    /**
         * Draws the border of the zone
         * 
         * @param d
         *                the Display about to paint itself
         * @param g
         *                the Graphics context for the Display
         */

    public void drawBorder( Display d, Graphics2D g) {
	m_zoneShape.drawBorder(d, g, m_zColors.getBorderColor());
    }

    /**
         * Returns the new size of flexible zone node item. By default this
         * method always return 1.0 . Overreide this method to immplement
         * flexible zone items.
         * 
         * @return the size
         */

    public double getUpdateItemSize() {
	return 1;
    }

    /**
         * Return the default gravitational constant used by the wall forces of
         * this zone
         * 
         * @return the default gravitational constant
         */

    public static float getDefaultGravConstant() {
	return m_defaultGravConstant;
    }

    /**
         * Returns the gravitational constant used by the wall forces of this
         * zone
         * 
         * @return the gravitational constant
         */

    public float getGravConstant() {
	return m_zAttributes.getGravConst();
    }

    /**
         * Sets the gravitational constant to be used by the wall forces of this
         * zone
         * 
         * @param gravConstant
         *                the gravitational constant
         */

    public void setGravConstant( float gravConstant) {
	m_zAttributes.setGravConst(gravConstant);
	calculateForces();
    }

    /**
         * Returns the shape of the zone
         * 
         * @return the zone shape
         */

    public ZoneShape getShape() {
	return m_zoneShape;
    }

    /**
         * Set the shape of the zone
         * 
         * @param zoneShape
         */

    public void setShape( ZoneShape zoneShape) {
	m_zoneShape = zoneShape;
	calculateForces();
    }

    /**
         * Returns the zone colors of the zone
         * 
         * @return the zone colors
         */

    public ZoneColors getColors() {
	return m_zColors;
    }

    /**
         * Sets the colors of the zone
         * 
         * @param zColors
         *                the colors of the zone
         */

    public void setColors( ZoneColors zColors) {
	m_zColors = zColors;
    }

    /**
         * Returns the attributes of the zone
         * 
         * @return the zone attributes
         */

    public ZoneAttributes getAttributes() {
	return m_zAttributes;
    }

    /**
         * Set the attributes of the zone
         * 
         * @param zoneAttributes
         */

    public void setAttributes( ZoneAttributes zoneAttributes) {
	m_zAttributes = zoneAttributes;
	calculateForces();
    }

    /**
         * Returns the number of the zone
         * 
         * @return the zone number
         */

    public int getNumber() {
	return m_zAttributes.getZoneNumber();
    }

    /**
         * Returns the name of the zone.
         * 
         * @return the zone name
         */

    public String getName() {
	return m_zAttributes.getZoneName();
    }

    /**
         * Sets the name of the zone.
         * 
         * @param name
         *                the zone name
         */
    public void setName( String name) {
	m_zAttributes.setZoneName(name);
    }

    /**
         * Returns the tuple set containing all items belonging to this zone
         * 
         * @return the tuple set
         */
    public TupleSet getAllItems() {
	return m_items;
    }

    /**
         * Returns the number of items contained in this zone
         * 
         * @return the number of items
         */

    public int getNumberOfItems() {
	return m_items.getTupleCount();
    }

    /**
         * Adds an node item to this zone.
         * 
         * @param ni
         *                the node item to be added
         */

    public void addItem( NodeItem ni) {
	m_items.addTuple(ni);

    }

    /**
         * Adds the nodeitems specified through the given iterator to the zone.
         * 
         * @param iter
         *                the iterator over the node items to be added
         */

    public void addItems( Iterator iter) {

	while (iter.hasNext()) {
	     Object o = iter.next();
	    if (o instanceof NodeItem) {
		 NodeItem aNodeItem = (NodeItem) o;
		m_items.addTuple(aNodeItem);
	    }
	}

    }

    /**
         * Removes all items from this zone.
         * 
         */
    public void removeAllItems() {
	if (m_zAttributes.hasFlexibleItems()) {
	    resetAllItems();
	}

	m_items.clear();

    }

    /**
         * Removes the specified item from the zone
         * 
         * @param ni
         *                the nodeitem to be removed
         */

    public void removeItem( NodeItem ni) {
	if (m_zAttributes.hasFlexibleItems()) {
	    resetItem(ni);
	}

	m_items.removeTuple(ni);

    }

    /**
         * Ensures that the node item is placed within the zone. If the zone
         * item's position is outside the zone border, it will be placed within
         * the borders.
         * 
         * @param ni
         *                the node item to be catched
         */
    public void catchItem( NodeItem ni) {
	catchItem(ni, true);
    }

    /**
         * Ensures that all node items belonging to this zone are placed within
         * the zone. If a zone item's position is outside the zone border, it
         * will be placed within the borders.
         * 
         * @param onlyIfOutside
         *                if true, only items outside the zone border will be
         *                placed newly inside the zone, otherwise all items
         *                regardless of their actual position
         */
    public void catchAllItems( boolean onlyIfOutside) {
	try {
	     Iterator iter = m_items.tuples();

	    while (iter.hasNext()) {
		 NodeItem aNodeItem = (NodeItem) iter.next();
		catchItem(aNodeItem, onlyIfOutside);
	    }
	} catch ( Exception ignore) {
	    // very occasionally a concurrent modification exception is
	    // thrown here
	    // we choose to ignore this and catch the items next round
	}

    }

    /**
         * Returns all the wallforces of the zone
         * 
         * @return the wallforces
         */
    public Force[] getForces() {
	return m_forces;
    }

    /**
         * Checks if the zone is flexible
         * 
         * @return true if the zone os flexible, otherwise false
         */
    public boolean isFlexible() {
	return m_zAttributes.isFlexible();
    }

    /**
         * Sets the flexibility attribute of the zone
         * 
         * @param value
         *                the flexibility attribute
         */
    public void setFlexible( boolean value) {
	m_zAttributes.setFlexibleZone(value);
    }

    /**
         * Checks of the zone items are flexible
         * 
         * @return true if the zone items are flexible, otherwise false
         */

    public boolean hasFlexibleItems() {
	return m_zAttributes.hasFlexibleItems();
    }

    /**
         * Sets the flexibility attribute of the zone items
         * 
         * @param value
         *                the flexibility attribute of the zone items
         */
    public void setItemsFlexible( boolean value) {
	m_zAttributes.setFlexibleItems(value);
    }

    /**
         * Returns the x position of the center of the zone
         * 
         * @return the inital x position
         */
    public float getCenterX() {
	return m_zoneShape.getCenterX();
    }

    /**
         * Returns the y position of the center of the zone
         * 
         * @return the initial y position
         */

    public float getCenterY() {
	return m_zoneShape.getCenterY();

    }

    /**
         * Returns the x position of the center that was given initially
         * 
         * @return the inital x position
         */
    public float getInitialCenterX() {
	return m_initialZoneShape.getCenterX();
    }

    /**
         * Returns the y position of the center that was givien initially
         * 
         * @return the initial y position
         */

    public float getInitialCenterY() {
	return m_initialZoneShape.getCenterY();
    }

    /**
         * Updates all flexible items of the zone. This method calls the
         * updateItem(Nodeitem) method of the zone class which has to be
         * overridden by a subclass of the zone in order to implement user
         * specific flexible node item updates
         * 
         */

    protected void updateAllFlexibleItems() {
	 Iterator iter = m_items.tuples();

	while (hasFlexibleItems() && iter.hasNext()) {
	    updateItem((NodeItem) iter.next());
	}
    }

    /**
         * Update this nodeitem
         * 
         * @param ni
         *                the nodeitem to be updated
         */

    protected void updateItem( NodeItem ni) {
	ni.setSize(getUpdateItemSize());
    }

    /**
         * Resests all flexible items of the zone. Per default the size of all
         * flexible node items is set to 1 by calling this method. Override the
         * method resetItem(Nodeitem) of the zone class in order to implement
         * different rescaling
         * 
         */

    protected void resetAllItems() {
	 Iterator iter = m_items.tuples();
	while (iter.hasNext()) {
	     NodeItem aItem = (NodeItem) iter.next();
	    resetItem(aItem);
	}

    }

    /**
         * Resets this nodeitem. Per default this method sets the size of this
         * nodeitem to 1. Override this method to implement a different
         * resacling.
         * 
         * @param ni
         */

    private void resetItem( NodeItem ni) {
	ni.setSize(1);
    }

    /**
         * Checks if the zone is active.
         * 
         * @return true if the zone is active, false otherwise
         */
    public boolean isActive() {
	return m_active;
    }

    /**
         * Sets the zone active/inactive. If a zone is set inactive, all its
         * zone forces are removed from its associated force simulator, IF set
         * active, they are added to the force simulator.
         * 
         * @param active
         *                if true, the zone is set active, inactive otherwise
         */

    public void setActive( boolean active) {
	m_active = active;
	if (active) {
	    removeForcesFromFsim();
	    addForcesToFsim();
	} else {
	    removeForcesFromFsim();
	}
    }

    /**
         * Recalculates the zone size and position if the zone is flexible In
         * addition flexible zone items are recalculated.
         * 
         */
    public void recalculateFlexibility() {
	if (isFlexible()) {
	    removeForcesFromFsim();
	    updateZone();
	    calculateForces();
	    addForcesToFsim();
	}

	if (hasFlexibleItems()) {
	    updateAllFlexibleItems();
	}
    }

    private void removeForcesFromFsim() {
	for (int i = 0; i < m_forces.length; i++) {
	    ((ForceSimulatorRemovableForces) m_fsim).removeForce(m_forces[i]);
	}
    }

    private void addForcesToFsim() {
	for (int i = 0; i < m_forces.length; i++) {
	    m_fsim.addForce(m_forces[i]);
	}
    }

    /**
         * Helper method ...
         * 
         * @param item
         * @param referrer
         * @param x
         */

    protected void setX( VisualItem item, VisualItem referrer,
	     double x) {
	double sx = item.getX();
	if (Double.isNaN(sx)) {
	    sx = (referrer != null ? referrer.getX() : x);
	}

	item.setStartX(sx);
	item.setEndX(x);

	item.setX(sx);
    }

    /**
         * Helper method ...
         * 
         * @param item
         * @param referrer
         * @param y
         */

    protected void setY( VisualItem item, VisualItem referrer,
	     double y) {
	double sy = item.getY();
	if (Double.isNaN(sy)) {
	    sy = (referrer != null ? referrer.getY() : y);
	}

	item.setStartY(sy);
	item.setEndY(y);

	item.setY(sy);
    }

}
