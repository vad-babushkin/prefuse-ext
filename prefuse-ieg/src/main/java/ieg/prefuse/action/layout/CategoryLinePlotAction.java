package ieg.prefuse.action.layout;

import java.util.Iterator;
import java.util.TreeMap;

import prefuse.data.Tuple;
import prefuse.data.expression.Predicate;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;

/**
 * Adds line plot segments between items of the same category. The segments are
 * saved in a {@link VisualTable} and store the {@link VisualItem}s of their
 * start and end points. The action needs to run only once; unless new points
 * are added.
 * 
 * <p>
 * Added: 2013-03-24 / AR (based on work by AR and TT)<br>
 * Modifications: 2012-0X-XX / XX / ...
 * </p>
 * 
 * @author Alexander Rind (based on work conducted in VisuExplore)
 * @see ieg.prefuse.action.layout.LinePlotLayout
 */
public class CategoryLinePlotAction extends LinePlotAction {

    protected String categoryField = null;
    protected Predicate missingValue = null;

    /**
     * Adds line plot segments between items of the same category.
     * 
     * @param group
     *            the new visual table containing line segments
     * @param source
     *            the visual table containing points
     * @param categoryField
     *            the field name by which lines are to be distinguished
     */
    public CategoryLinePlotAction(String group, String source,
            String categoryField) {
        super(group, source);
        this.categoryField = categoryField;
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void connectPoints(VisualTable lines, Iterator points) {
        // insert a column with category into line table (if not there yet)
        if (lines.getColumn(categoryField) == null) {
			Iterator tuples = m_vis.getGroup(m_src).tuples();
			if (tuples.hasNext()) {
				// we try to copy the column type from 1st row of source
				Tuple firstTuple = (Tuple) tuples.next();
				lines.addColumn(categoryField, firstTuple.getColumnType(categoryField));
			} else {
				// default to String
				lines.addColumn(categoryField, String.class);
			}
        }

        // remember previous node for each time series
        TreeMap<Object, VisualItem> prevTupleMap = new TreeMap<Object, VisualItem>();

        while (points.hasNext()) {
            VisualItem vi = (VisualItem) points.next();
            Object code = categoryField != null ? vi.get(categoryField)
                    : "default";

            if (missingValue != null && missingValue.getBoolean(vi)) {
                // skip this tuple and interrupt the line plot
                prevTupleMap.remove(code);
                continue;
            }

            // connect if possible
            if (prevTupleMap.containsKey(code)) {
                VisualItem line = lines.addItem();
                line.set("v1", prevTupleMap.get(code));
                line.set("v2", vi);
                line.setString(categoryField, String.valueOf(code));
            }

            // remember node for connection
            prevTupleMap.put(code, vi);
        }
    }

    public String getCategoryField() {
        return categoryField;
    }

    public void setCategoryField(String categoryField) {
        this.categoryField = categoryField;
    }

    public Predicate getMissingValue() {
        return missingValue;
    }

    public void setMissingValue(Predicate missingValue) {
        this.missingValue = missingValue;
    }
}
