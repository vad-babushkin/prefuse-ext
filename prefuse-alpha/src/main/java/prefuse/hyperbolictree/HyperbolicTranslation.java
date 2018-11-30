//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.hyperbolictree;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.graph.DefaultTree;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.Iterator;

public class HyperbolicTranslation extends TreeLayout {
	private double[] p = new double[2];
	private double[] o = new double[2];
	private double[] zo = new double[2];
	private double[] zs = new double[2];
	private double[] ze = new double[2];
	private double[] origin = new double[2];
	private double[] max = new double[2];
	private int[] ps = new int[2];
	private int[] pe = new int[2];

	public HyperbolicTranslation() {
	}

	public void setStartPoint(int x, int y) {
		this.ps[0] = x;
		this.ps[1] = y;
	}

	public void setEndPoint(int x, int y) {
		this.pe[0] = x;
		this.pe[1] = y;
	}

	private void projection(double[] z, int[] p) {
		z[0] = ((double)p[0] - this.origin[0]) / this.max[0];
		z[1] = -(((double)p[1] - this.origin[1]) / this.max[1]);
		double mag = z[0] * z[0] + z[1] * z[1];
		double limit = 0.85D;
		if (mag >= limit) {
			mag = Math.sqrt(mag) / Math.sqrt(limit);
			z[0] /= mag;
			z[1] /= mag;
		}

	}

	public Rectangle2D getLayoutBounds(ItemRegistry registry) {
		Rectangle2D r = super.getLayoutBounds();
		if (r != null) {
			return r;
		} else {
			r = new Rectangle2D.Double();
			if (registry != null) {
				Display d = registry.getDisplay(0);
				r.setFrame(0.0D, 0.0D, (double)d.getWidth(), (double)d.getHeight());
			}

			return r;
		}
	}

	public void run(ItemRegistry registry, double frac) {
		Rectangle2D b = this.getLayoutBounds(registry);
		this.origin[0] = b.getWidth() / 2.0D;
		this.origin[1] = b.getHeight() / 2.0D;
		this.max[0] = b.getWidth() / 2.0D;
		this.max[1] = b.getHeight() / 2.0D;
		this.projection(this.zs, this.ps);
		this.projection(this.ze, this.pe);
		this.ze[0] -= frac * this.ze[0];
		this.ze[1] -= frac * this.ze[1];
		DefaultTree t = (DefaultTree)registry.getGraph();
		NodeItem r = registry.getNodeItem(t.getRoot());
		HyperbolicParams rp = this.getParams(r);
		this.zo[0] = rp.zo[0];
		this.zo[1] = rp.zo[1];
		if (computeTransform(this.zo, this.zs, this.ze, this.p, this.o)) {
			Iterator nodeIter = registry.getNodeItems(false);

			while(nodeIter.hasNext()) {
				NodeItem n = (NodeItem)nodeIter.next();
				HyperbolicParams np = this.getParams(n);
				if (np != null) {
					np.z[0] = np.zo[0];
					np.z[1] = np.zo[1];
					transform(np.z, this.p, this.o);
				}
			}

			Iterator edgeIter = registry.getEdgeItems(false);

			while(edgeIter.hasNext()) {
				EdgeItem e = (EdgeItem)edgeIter.next();
				NodeItem n = (NodeItem)e.getFirstNode();
				NodeItem p = (NodeItem)e.getSecondNode();
				if (n.getParent() != p) {
					NodeItem tmp = n;
					n = p;
					p = tmp;
				}

				if (n.getParent() == p) {
					HyperbolicParams ep = this.getParams(e);
					HyperbolicParams np = this.getParams(n);
					HyperbolicParams pp = this.getParams(p);
					HyperbolicParams.setControlPoint(ep.z, pp.z, np.z);
				}
			}

		}
	}

	private HyperbolicParams getParams(VisualItem item) {
		return (HyperbolicParams)item.getVizAttribute("hyperbolicParams");
	}

	private static boolean computeTransform(double[] zo, double[] zs, double[] ze, double[] p, double[] o) {
		zo[0] = -zo[0];
		zo[1] = -zo[1];
		double[] zs2 = new double[]{zs[0], zs[1]};
		HyperbolicParams.translate(zs2, zo);
		double de = ze[0] * ze[0] + ze[1] * ze[1];
		double ds = zs2[0] * zs2[0] + zs2[1] * zs2[1];
		double dd = 1.0D - de * ds;
		double[] t = new double[]{(ze[0] * (1.0D - ds) - zs2[0] * (1.0D - de)) / dd, (ze[1] * (1.0D - ds) - zs2[1] * (1.0D - de)) / dd};
		if (t[0] * t[0] + t[1] * t[1] < 1.0D) {
			compose(zo, t, p, o);
			return true;
		} else {
			System.err.println(t[0] * t[0] + t[1] * t[1] + ": not valid");
			return false;
		}
	}

	private static void compose(double[] z1, double[] z2, double[] p, double[] o) {
		p[0] = z1[0] + z2[0];
		p[1] = z1[1] + z2[1];
		double[] d = new double[]{z2[0], z2[1]};
		d[1] = -d[1];
		HyperbolicParams.multiply(d, z1);
		++d[0];
		HyperbolicParams.divide(p, d);
		o[0] = z1[0];
		o[1] = -z1[1];
		HyperbolicParams.multiply(o, z2);
		++o[0];
		HyperbolicParams.divide(o, d);
	}

	private static void transform(double[] z, double[] p, double[] o) {
		double[] z0 = new double[]{z[0], z[1]};
		HyperbolicParams.multiply(z, o);
		z[0] += p[0];
		z[1] += p[1];
		double[] d = new double[]{p[0], p[1]};
		d[1] = -d[1];
		HyperbolicParams.multiply(d, z0);
		HyperbolicParams.multiply(d, o);
		++d[0];
		HyperbolicParams.divide(z, d);
	}
}
