package prefuse.hyperbolictree;

import prefuse.Constants;
import prefuse.action.GroupAction;
import prefuse.visual.EdgeItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;

import java.util.Iterator;

public class HyperbolicVisibilityFilter extends GroupAction {
	private double m_thresh = 0.96D;

	public HyperbolicVisibilityFilter() {
	}

	@Override
	public void run(double frac) {

		Iterator items = m_vis.visibleItems(m_group);
		while (items.hasNext()) {
			VisualItem item = (VisualItem) items.next();
			item.setDOI(Constants.MINIMUM_DOI);
		}

//
//
//
//
//
//		Iterator iter = registry.getNodeItems(false);
//
//		while(iter.hasNext()) {
//			NodeItem item = (NodeItem)iter.next();
//			HyperbolicParams np = this.getParams(item);
//			double d = Math.sqrt(np.z[0] * np.z[0] + np.z[1] * np.z[1]);
//			item.setVisible(d < this.m_thresh);
//		}
//
//		iter = registry.getEdgeItems(false);
//
//		while(iter.hasNext()) {
//			EdgeItem item = (EdgeItem)iter.next();
//			NodeItem n = (NodeItem)item.getFirstNode();
//			HyperbolicParams np = this.getParams(n);
//			double d = Math.sqrt(np.z[0] * np.z[0] + np.z[1] * np.z[1]);
//			item.setVisible(d < this.m_thresh);
//		}
	}

//	public HyperbolicParams getParams(VisualItem item) {
//		return (HyperbolicParams)item.getVizAttribute("hyperbolicParams");
//	}
}
