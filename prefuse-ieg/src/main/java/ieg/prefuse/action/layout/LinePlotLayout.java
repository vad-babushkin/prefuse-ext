package ieg.prefuse.action.layout;

import java.util.Iterator;

import prefuse.action.layout.Layout;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;

/**
 * <p>
 * Added: 2012-06-14 / AR (based on LineChart example by Jeff Heer)<br>
 * Modifications: 2012-0X-XX / XX / ...
 * </p>
 * 
 * @author Alexander Rind (based on LineChart example by Jeff Heer)
 */
public class LinePlotLayout extends Layout {
    
    public LinePlotLayout(String group) {
        super(group);
    }

    @Override
    public void run(double frac) {
        // get visual table of my visualization
        VisualTable lines = (VisualTable) m_vis.getGroup(m_group);

        // update x and y coordinates of lines
        @SuppressWarnings("rawtypes")
        Iterator items = lines.tuples();
        while ( items.hasNext() ) {
            VisualItem item = (VisualItem)items.next();
            VisualItem v1 = (VisualItem)item.get("v1");
            VisualItem v2 = (VisualItem)item.get("v2");
            if ( !(v1.isValid() && v2.isValid()) ) {
                lines.removeTuple(item);
            } else if ( v1.isVisible() && v2.isVisible() ) {
                item.setX(v1.getX());
                item.setY(v1.getY());
                item.setDouble(VisualItem.X2, v2.getX());
                item.setDouble(VisualItem.Y2, v2.getY());
                PrefuseLib.updateVisible(item, true);
            } else {
                PrefuseLib.updateVisible(item, false);
            }
        }
    }

}
