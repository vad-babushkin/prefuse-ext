package prefuse.demos.fajran.ubuntupkg;

import java.util.Iterator;

import prefuse.Constants;
import prefuse.action.Action;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.util.PrefuseLib;
import prefuse.visual.VisualItem;

public class FilterAction extends Action {

	public void run(double frac) {
		
		VisualItem res = null;
		
		Iterator ii = m_vis.items("graph.nodes");
		while (ii.hasNext()) {
			VisualItem item = (VisualItem)ii.next();
			String pkg = item.get("pkg").toString();
			if ("apt".equals(pkg)) {
				res = item;
			}
			
			item.setDOI(Constants.MINIMUM_DOI);
			PrefuseLib.updateVisible(item, false);
		}
		
		ii = m_vis.items("graph.edges");
		while (ii.hasNext()) {
			VisualItem item = (VisualItem)ii.next();
			item.setDOI(Constants.MINIMUM_DOI);
			PrefuseLib.updateVisible(item, false);
		}
		
		if (res != null) {
			res.setDOI(0);
			PrefuseLib.updateVisible(res, true);
			
			boolean repeat = true;
			
			while (repeat) {
				repeat = false;
			
				Node n = (Node)res;
				ii = n.outNeighbors();
				while (ii.hasNext()) {
					VisualItem item = (VisualItem)ii.next();
					item.setDOI(0);
					PrefuseLib.updateVisible(item, true);
				}
				
				ii = n.outEdges();
				while (ii.hasNext()) {
					VisualItem item = (VisualItem)ii.next();
					item.setDOI(0);
					PrefuseLib.updateVisible(item, true);
				}
			}
		}
	}

}
