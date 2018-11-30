//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.hyperbolictree;

import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import java.util.Iterator;

public class HyperbolicTranslationEnd extends AbstractAction {
	public HyperbolicTranslationEnd() {
	}

	public void run(ItemRegistry registry, double frac) {
		Iterator nodeIter = registry.getNodeItems(false);

		while(nodeIter.hasNext()) {
			NodeItem n = (NodeItem)nodeIter.next();
			HyperbolicParams np = this.getParams(n);
			if (np != null) {
				np.zo[0] = np.z[0];
				np.zo[1] = np.z[1];
			}
		}

	}

	private HyperbolicParams getParams(VisualItem n) {
		return (HyperbolicParams)n.getVizAttribute("hyperbolicParams");
	}
}
