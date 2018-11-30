/*     */
package prefuse.hyperbolictree;
/*     */
/*     */

import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.graph.DefaultTree;

import java.util.Iterator;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */ public class HyperbolicTreeLayout
		/*     */ extends TreeLayout
		/*     */ {
	/*  21 */   private double m_length = 0.3D;

	/*     */
	/*     */
	public NodeItem getLayoutRoot(ItemRegistry registry) {
		/*  24 */
		NodeItem r = super.getLayoutRoot();
		/*  25 */
		if (r != null)
			/*  26 */ return r;
		/*  27 */
		DefaultTree t = (DefaultTree) registry.getGraph();
		/*  28 */
		return registry.getNodeItem(t.getRoot());
		/*     */
	}

	/*     */
	/*     */
	public void run(ItemRegistry registry, double frac) {
		/*  32 */
		NodeItem n = getLayoutRoot(registry);
		/*  33 */
		calcWeight(n);
		/*  34 */
		layout(n, 0.0D, 3.141592653589793D, this.m_length);
		/*     */
	}

	/*     */
	/*     */
	private double calcWeight(NodeItem n) {
		/*  38 */
		HyperbolicParams np = getParams(n);
		/*  39 */
		double w = 0.0D;
		/*  40 */
		Iterator iter = n.getChildren();
		/*  41 */
		while (iter.hasNext()) {
			/*  42 */
			NodeItem c = (NodeItem) iter.next();
			/*  43 */
			w += calcWeight(c);
			/*     */
		}
		/*  45 */
		np.weight = w;
		/*  46 */
		return Math.max(1.0D, n.getSize()) + (w != 0.0D ? Math.log(w) : 0.0D);
		/*     */
	}

	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	/*     */
	private void layout(NodeItem n, double angle, double width, double length)
	/*     */ {
		/*  60 */
		HyperbolicParams np = getParams(n);
		/*  61 */
		NodeItem p = (NodeItem) n.getParent();
		/*     */
		/*  63 */
		if (p != null) {
			/*  64 */
			HyperbolicParams pp = getParams(p);
			/*     */
			/*  66 */
			np.z[0] = (length * Math.cos(angle));
			/*  67 */
			np.z[1] = (length * Math.sin(angle));
			/*     */
			/*     */
			/*  70 */
			HyperbolicParams.translate(np.z, pp.z);
			/*  71 */
			np.zo[0] = np.z[0];
			/*  72 */
			np.zo[1] = np.z[1];
			/*     */
			/*  74 */
			if (n.getChildCount() > 0)
				/*     */ {
				/*     */
				/*  77 */
				double[] a = {Math.cos(angle), Math.sin(angle)};
				/*  78 */
				double[] nz = {-np.z[0], -np.z[1]};
				/*  79 */
				HyperbolicParams.translate(a, pp.z);
				/*  80 */
				HyperbolicParams.translate(a, nz);
				/*  81 */
				angle = HyperbolicParams.angle(a);
				/*     */
				/*     */
				/*     */
				/*     */
				/*  86 */
				double c = Math.cos(width);
				/*  87 */
				double A = 1.0D + length * length;
				/*  88 */
				double B = 2.0D * length;
				/*  89 */
				width = Math.acos((A * c - B) / (A - B * c));
				/*     */
			}
			/*     */
		}
		/*     */
		/*  93 */
		int numChildren = n.getChildCount();
		/*  94 */
		if (numChildren == 0) {
			/*  95 */
			return;
			/*     */
		}
		/*  97 */
		double l1 = 0.95D - this.m_length;
		/*  98 */
		double l2 = Math.cos(62.83185307179586D / (2.0D * numChildren + 38.0D));
		/*  99 */
		length = this.m_length + l1 * l2;
		/* 100 */
		double startAngle = angle - width;
		/*     */
		/* 102 */
		Iterator childIter = n.getChildren();
		/* 103 */
		while (childIter.hasNext())
			/*     */ {
			/* 105 */
			NodeItem c = (NodeItem) childIter.next();
			/* 106 */
			HyperbolicParams cp = getParams(c);
			/* 107 */
			double cweight = Math.max(1.0D, c.getSize()) + (
					/* 108 */         cp.weight != 0.0D ? Math.log(cp.weight) : 0.0D);
			/* 109 */
			double cwidth = width * (cweight / np.weight);
			/* 110 */
			double cangle = startAngle + cwidth;
			/* 111 */
			layout(c, cangle, cwidth, length);
			/* 112 */
			startAngle += 2.0D * cwidth;
			/*     */
			/*     */
			/* 115 */
			EdgeItem e = (EdgeItem) n.getEdge(c);
			/* 116 */
			HyperbolicParams ep = getParams(e);
			/* 117 */
			HyperbolicParams.setControlPoint(ep.z, np.z, cp.z);
			/*     */
		}
		/*     */
	}

	/*     */
	/*     */
	private HyperbolicParams getParams(VisualItem n) {
		/* 122 */
		HyperbolicParams np =
				/* 123 */       (HyperbolicParams) n.getVizAttribute("hyperbolicParams");
		/* 124 */
		if (np == null) {
			/* 125 */
			np = new HyperbolicParams();
			/* 126 */
			n.setVizAttribute("hyperbolicParams", np);
			/*     */
		}
		/* 128 */
		return np;
		/*     */
	}
	/*     */
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/prefuse/hyperbolictree/HyperbolicTreeLayout.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */