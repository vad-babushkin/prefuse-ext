//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.hyperbolictree;

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import java.util.Iterator;

public class HyperbolicTreeLayout extends TreeLayout {
	private double m_length = 0.3D;

	public HyperbolicTreeLayout() {
	}

	public NodeItem getLayoutRoot(ItemRegistry registry) {
		NodeItem r = super.getLayoutRoot();
		if (r != null) {
			return r;
		} else {
			DefaultTree t = (DefaultTree)registry.getGraph();
			return registry.getNodeItem(t.getRoot());
		}
	}

	public void run(ItemRegistry registry, double frac) {
		NodeItem n = this.getLayoutRoot(registry);
		this.calcWeight(n);
		this.layout(n, 0.0D, 3.141592653589793D, this.m_length);
	}

	private double calcWeight(NodeItem n) {
		HyperbolicParams np = this.getParams(n);
		double w = 0.0D;

		NodeItem c;
		for(Iterator iter = n.getChildren(); iter.hasNext(); w += this.calcWeight(c)) {
			c = (NodeItem)iter.next();
		}

		np.weight = w;
		return Math.max(1.0D, n.getSize()) + (w != 0.0D ? Math.log(w) : 0.0D);
	}

	private void layout(NodeItem n, double angle, double width, double length) {
		HyperbolicParams np = this.getParams(n);
		NodeItem p = (NodeItem)n.getParent();
		double l2;
		double startAngle;
		if (p != null) {
			HyperbolicParams pp = this.getParams(p);
			np.z[0] = length * Math.cos(angle);
			np.z[1] = length * Math.sin(angle);
			HyperbolicParams.translate(np.z, pp.z);
			np.zo[0] = np.z[0];
			np.zo[1] = np.z[1];
			if (n.getChildCount() > 0) {
				double[] a = new double[]{Math.cos(angle), Math.sin(angle)};
				double[] nz = new double[]{-np.z[0], -np.z[1]};
				HyperbolicParams.translate(a, pp.z);
				HyperbolicParams.translate(a, nz);
				angle = HyperbolicParams.angle(a);
				l2 = Math.cos(width);
				startAngle = 1.0D + length * length;
				double B = 2.0D * length;
				width = Math.acos((startAngle * l2 - B) / (startAngle - B * l2));
			}
		}

		int numChildren = n.getChildCount();
		if (numChildren != 0) {
			double l1 = 0.95D - this.m_length;
			l2 = Math.cos(62.83185307179586D / (2.0D * (double)numChildren + 38.0D));
			length = this.m_length + l1 * l2;
			startAngle = angle - width;
			Iterator childIter = n.getChildren();

			while(childIter.hasNext()) {
				NodeItem c = (NodeItem)childIter.next();
				HyperbolicParams cp = this.getParams(c);
				double cweight = Math.max(1.0D, c.getSize()) + (cp.weight != 0.0D ? Math.log(cp.weight) : 0.0D);
				double cwidth = width * (cweight / np.weight);
				double cangle = startAngle + cwidth;
				this.layout(c, cangle, cwidth, length);
				startAngle += 2.0D * cwidth;
				EdgeItem e = (EdgeItem)n.getEdge(c);
				HyperbolicParams ep = this.getParams(e);
				HyperbolicParams.setControlPoint(ep.z, np.z, cp.z);
			}

		}
	}

	private HyperbolicParams getParams(VisualItem n) {
		HyperbolicParams np = (HyperbolicParams)n.getVizAttribute("hyperbolicParams");
		if (np == null) {
			np = new HyperbolicParams();
			n.setVizAttribute("hyperbolicParams", np);
		}

		return np;
	}
}
