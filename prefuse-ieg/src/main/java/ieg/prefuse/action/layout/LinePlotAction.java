package ieg.prefuse.action.layout;

import java.util.Iterator;

import prefuse.action.GroupAction;
import prefuse.data.Schema;
import prefuse.data.expression.Predicate;
import prefuse.data.tuple.TupleSet;
import prefuse.data.util.Sort;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;

/**
 * Adds line plot segments to the visualization. The segments are saved in a
 * {@link VisualTable} and store the {@link VisualItem}s of their start and end
 * points. The action needs to run only once; unless new points are added.
 * 
 * <p>
 * Added: 2012-06-14 / AR (based on LineChart example by Jeff Heer)<br>
 * Modifications: 2012-0X-XX / XX / ...
 * </p>
 * 
 * @author Alexander Rind (based on LineChart example by Jeff Heer)
 * @see ieg.prefuse.action.layout.LinePlotLayout
 */
public class LinePlotAction extends GroupAction {
    
    protected String m_src;
    
    protected String m_sortField = null;
    
    protected Predicate m_filter = null;

    public LinePlotAction(String group, String source) {
        super(group);
        m_src = source;
    }

    public LinePlotAction(String group, String source, String sortField) {
        super(group);
        m_src = source;
        m_sortField = sortField;
    }

    @Override
    public void run(double frac) {
        // get visual table of my visualization
        VisualTable lines = getTable();

        @SuppressWarnings("rawtypes")
        Iterator points = getSortedPoints();
        
        // add line segments
        connectPoints(lines, points);
    }
    
    @SuppressWarnings("rawtypes")
    protected void connectPoints(VisualTable lines, Iterator points) {
        for ( VisualItem v1=null, v2; points.hasNext(); v1=v2 ) {
            v2 = (VisualItem)points.next();
            if ( v1 != null ) {
                VisualItem line = lines.addItem();
                line.set("v1", v1);
                line.set("v2", v2);
            }
        }
    }
    
    @SuppressWarnings("rawtypes")
    protected Iterator getSortedPoints() {
        Iterator points;
        if (m_sortField != null) {
            Sort sort = new Sort(new String[] {m_sortField});
            points = m_vis.getGroup(m_src).tuples(m_filter, sort);
        } else {
            points = m_vis.items(m_src, m_filter);
        }
        return points;
    }

    /**
     * Create a new table for representing line segments.
     */
    protected VisualTable getTable() {
        TupleSet ts = m_vis.getGroup(m_group);
        if ( ts == null ) {
            Schema lineSchema = PrefuseLib.getVisualItemSchema();
            lineSchema.addColumn(VisualItem.X2, double.class);
            lineSchema.addColumn(VisualItem.Y2, double.class);
            lineSchema.addColumn("v1", VisualItem.class);
            lineSchema.addColumn("v2", VisualItem.class);
            
            VisualTable vt = m_vis.addTable(m_group, lineSchema);
            return vt;
        } else if ( ts instanceof VisualTable ) {
            // empty table
            ts.clear();
            return (VisualTable)ts;
        } else {
            throw new IllegalStateException(
                "Group already exists, not being used for line segments");
        }
    }
    
    public Predicate getFilter() {
        return m_filter;
    }

    public void setFilter(Predicate filter) {
        this.m_filter = filter;
    }
}
