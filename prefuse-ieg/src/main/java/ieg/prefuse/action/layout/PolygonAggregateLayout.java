package ieg.prefuse.action.layout;

import java.util.Iterator;

import prefuse.action.ItemAction;
import prefuse.data.expression.Predicate;
import prefuse.render.PolygonRenderer;
import prefuse.visual.AggregateItem;
import prefuse.visual.VisualItem;

/**
 * Creates a polygon based on the coordinates of the {@link VisualItem}s in each
 * {@link AggregateItem}. The polygon can be rendered with the
 * {@link PolygonRenderer}.
 * 
 * This assumes that items in the group are {@link AggregateItem}s and have the
 * non-default field {@link VisualItem#POLYGON}.
 * 
 * @author Rind (based on work by Atanasov and Schindler)
 */
public class PolygonAggregateLayout extends ItemAction {

    /**
     * Creates a polygon based on the coordinates of the {@link VisualItem}s in
     * each {@link AggregateItem}.
     * 
     * @param group
     *            the name of the aggregate group
     */
    public PolygonAggregateLayout(String group) {
        super(group);
    }

    public PolygonAggregateLayout(String group, Predicate filter) {
        super(group, filter);
    }

    @Override
    public void process(VisualItem item, double frac) {

        AggregateItem ai = (AggregateItem) item;
        float[] poly = (float[]) ai.get(VisualItem.POLYGON);

        if (poly == null || poly.length * 2 != ai.getAggregateSize()) {
            poly = new float[ai.getAggregateSize() * 2];
            ai.set(VisualItem.POLYGON, poly);
        }

        int i = 0;
        @SuppressWarnings("unchecked")
        Iterator<VisualItem> visualItems = ai.items();
        while (visualItems.hasNext()) {
            VisualItem vi = visualItems.next();
            poly[i] = (float) vi.getX();
            i++;
            poly[i] = (float) vi.getY();
            i++;
        }
    }

}
