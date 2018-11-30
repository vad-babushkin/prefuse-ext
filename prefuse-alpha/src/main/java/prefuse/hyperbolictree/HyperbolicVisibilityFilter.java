//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.hyperbolictree;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import java.util.Iterator;

public class HyperbolicVisibilityFilter extends AbstractAction {
	private double m_thresh = 0.96D;

	public HyperbolicVisibilityFilter() {
	}

	public void run(ItemRegistry registry, double frac) {
		Iterator iter = registry.getNodeItems(false);

		while(iter.hasNext()) {
			NodeItem item = (NodeItem)iter.next();
			HyperbolicParams np = this.getParams(item);
			double d = Math.sqrt(np.z[0] * np.z[0] + np.z[1] * np.z[1]);
			item.setVisible(d < this.m_thresh);
		}

		iter = registry.getEdgeItems(false);

		while(iter.hasNext()) {
			EdgeItem item = (EdgeItem)iter.next();
			NodeItem n = (NodeItem)item.getFirstNode();
			HyperbolicParams np = this.getParams(n);
			double d = Math.sqrt(np.z[0] * np.z[0] + np.z[1] * np.z[1]);
			item.setVisible(d < this.m_thresh);
		}

	}

	public HyperbolicParams getParams(VisualItem item) {
		return (HyperbolicParams)item.getVizAttribute("hyperbolicParams");
	}
}
