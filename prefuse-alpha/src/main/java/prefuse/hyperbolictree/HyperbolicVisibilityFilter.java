/*    */
package prefuse.hyperbolictree;
/*    */
/*    */

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;

import java.util.Iterator;

/*    */
/*    */
/*    */
/*    */
/*    */

/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */
/*    */ public class HyperbolicVisibilityFilter
		/*    */ extends AbstractAction
		/*    */ {
	/* 19 */   private double m_thresh = 0.96D;

	/*    */
	/*    */
	public void run(ItemRegistry registry, double frac) {
		/* 22 */
		Iterator iter = registry.getNodeItems(false);
		/* 23 */
		while (iter.hasNext()) {
			/* 24 */
			NodeItem item = (NodeItem) iter.next();
			/* 25 */
			HyperbolicParams np = getParams(item);
			/* 26 */
			double d = Math.sqrt(np.z[0] * np.z[0] + np.z[1] * np.z[1]);
			/* 27 */
			item.setVisible(d < this.m_thresh);
			/*    */
		}
		/* 29 */
		iter = registry.getEdgeItems(false);
		/* 30 */
		while (iter.hasNext()) {
			/* 31 */
			EdgeItem item = (EdgeItem) iter.next();
			/* 32 */
			NodeItem n = (NodeItem) item.getFirstNode();
			/* 33 */
			HyperbolicParams np = getParams(n);
			/* 34 */
			double d = Math.sqrt(np.z[0] * np.z[0] + np.z[1] * np.z[1]);
			/* 35 */
			item.setVisible(d < this.m_thresh);
			/*    */
		}
		/*    */
	}

	/*    */
	/*    */
	public HyperbolicParams getParams(VisualItem item) {
		/* 40 */
		return (HyperbolicParams) item.getVizAttribute("hyperbolicParams");
		/*    */
	}
	/*    */
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/prefuse/hyperbolictree/HyperbolicVisibilityFilter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */