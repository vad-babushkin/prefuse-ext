/*     */
package prefuse.hyperbolictree;
/*     */
/*     */

import edu.berkeley.guir.prefuse.*;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import edu.berkeley.guir.prefuse.graph.DefaultTree;

import java.awt.geom.Rectangle2D;
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
/*     */
/*     */
/*     */ public class HyperbolicTranslation
		/*     */ extends TreeLayout
		/*     */ {
	/*  23 */   private double[] p = new double[2];
	/*  24 */   private double[] o = new double[2];
	/*     */
	/*  26 */   private double[] zo = new double[2];
	/*  27 */   private double[] zs = new double[2];
	/*  28 */   private double[] ze = new double[2];
	/*     */
	/*  30 */   private double[] origin = new double[2];
	/*  31 */   private double[] max = new double[2];
	/*  32 */   private int[] ps = new int[2];
	/*  33 */   private int[] pe = new int[2];

	/*     */
	/*     */
	public void setStartPoint(int x, int y) {
		/*  36 */
		this.ps[0] = x;
		this.ps[1] = y;
		/*     */
	}

	/*     */
	/*     */
	public void setEndPoint(int x, int y) {
		/*  40 */
		this.pe[0] = x;
		this.pe[1] = y;
		/*     */
	}

	/*     */
	/*     */
	private void projection(double[] z, int[] p) {
		/*  44 */
		z[0] = ((p[0] - this.origin[0]) / this.max[0]);
		/*  45 */
		z[1] = (-((p[1] - this.origin[1]) / this.max[1]));
		/*  46 */
		double mag = z[0] * z[0] + z[1] * z[1];
		/*  47 */
		double limit = 0.85D;
		/*  48 */
		if (mag >= limit)
			/*     */ {
			/*  50 */
			mag = Math.sqrt(mag) / Math.sqrt(limit);
			/*  51 */
			z[0] /= mag;
			/*  52 */
			z[1] /= mag;
			/*     */
		}
		/*     */
	}

	/*     */
	/*     */
	public Rectangle2D getLayoutBounds(ItemRegistry registry) {
		/*  57 */
		Rectangle2D r = super.getLayoutBounds();
		/*  58 */
		if (r != null) {
			/*  59 */
			return r;
			/*     */
		}
		/*  61 */
		r = new Rectangle2D.Double();
		/*  62 */
		if (registry != null) {
			/*  63 */
			Display d = registry.getDisplay(0);
			/*  64 */
			r.setFrame(0.0D, 0.0D, d.getWidth(), d.getHeight());
			/*     */
		}
		/*  66 */
		return r;
		/*     */
	}

	/*     */
	/*     */
	public void run(ItemRegistry registry, double frac) {
		/*  70 */
		Rectangle2D b = getLayoutBounds(registry);
		/*  71 */
		this.origin[0] = (b.getWidth() / 2.0D);
		this.origin[1] = (b.getHeight() / 2.0D);
		/*  72 */
		this.max[0] = (b.getWidth() / 2.0D);
		this.max[1] = (b.getHeight() / 2.0D);
		/*     */
		/*  74 */
		projection(this.zs, this.ps);
		/*  75 */
		projection(this.ze, this.pe);
		/*  76 */
		this.ze[0] -= frac * this.ze[0];
		/*  77 */
		this.ze[1] -= frac * this.ze[1];
		/*     */
		/*  79 */
		DefaultTree t = (DefaultTree) registry.getGraph();
		/*  80 */
		NodeItem r = registry.getNodeItem(t.getRoot());
		/*  81 */
		HyperbolicParams rp = getParams(r);
		/*  82 */
		this.zo[0] = rp.zo[0];
		/*  83 */
		this.zo[1] = rp.zo[1];
		/*     */
		/*  85 */
		if (!computeTransform(this.zo, this.zs, this.ze, this.p, this.o)) {
			/*  86 */
			return;
			/*     */
		}
		/*     */
		/*  89 */
		Iterator nodeIter = registry.getNodeItems(false);
		/*  90 */
		while (nodeIter.hasNext()) {
			/*  91 */
			NodeItem n = (NodeItem) nodeIter.next();
			/*  92 */
			HyperbolicParams np = getParams(n);
			/*  93 */
			if (np != null) {
				/*  94 */
				np.z[0] = np.zo[0];
				/*  95 */
				np.z[1] = np.zo[1];
				/*  96 */
				transform(np.z, this.p, this.o);
				/*     */
			}
			/*     */
		}
		/*     */
		/* 100 */
		Iterator edgeIter = registry.getEdgeItems(false);
		/* 101 */
		while (edgeIter.hasNext()) {
			/* 102 */
			EdgeItem e = (EdgeItem) edgeIter.next();
			/* 103 */
			NodeItem n = (NodeItem) e.getFirstNode();
			/* 104 */
			NodeItem p = (NodeItem) e.getSecondNode();
			/* 105 */
			if (n.getParent() != p) {
				/* 106 */
				NodeItem tmp = n;
				n = p;
				p = tmp;
				/*     */
			}
			/* 108 */
			if (n.getParent() == p)
				/*     */ {
				/*     */
				/*     */
				/* 112 */
				HyperbolicParams ep = getParams(e);
				/* 113 */
				HyperbolicParams np = getParams(n);
				/* 114 */
				HyperbolicParams pp = getParams(p);
				/* 115 */
				HyperbolicParams.setControlPoint(ep.z, pp.z, np.z);
				/*     */
			}
			/*     */
		}
		/*     */
	}

	/*     */
	/* 120 */
	private HyperbolicParams getParams(VisualItem item) {
		return (HyperbolicParams) item.getVizAttribute("hyperbolicParams");
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
	private static boolean computeTransform(double[] zo, double[] zs, double[] ze, double[] p, double[] o)
	/*     */ {
		/* 131 */
		zo[0] = (-zo[0]);
		/* 132 */
		zo[1] = (-zo[1]);
		/* 133 */
		double[] zs2 = {zs[0], zs[1]};
		/* 134 */
		HyperbolicParams.translate(zs2, zo);
		/*     */
		/* 136 */
		double de = ze[0] * ze[0] + ze[1] * ze[1];
		/* 137 */
		double ds = zs2[0] * zs2[0] + zs2[1] * zs2[1];
		/* 138 */
		double dd = 1.0D - de * ds;
		/* 139 */
		double[] t = new double[2];
		/* 140 */
		t[0] = ((ze[0] * (1.0D - ds) - zs2[0] * (1.0D - de)) / dd);
		/* 141 */
		t[1] = ((ze[1] * (1.0D - ds) - zs2[1] * (1.0D - de)) / dd);
		/*     */
		/* 143 */
		if (t[0] * t[0] + t[1] * t[1] < 1.0D) {
			/* 144 */
			compose(zo, t, p, o);
			/* 145 */
			return true;
			/*     */
		}
		/* 147 */
		System.err.println(t[0] * t[0] + t[1] * t[1] + ": not valid");
		/* 148 */
		return false;
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
	private static void compose(double[] z1, double[] z2, double[] p, double[] o)
	/*     */ {
		/* 159 */
		z1[0] += z2[0];
		/* 160 */
		z1[1] += z2[1];
		/*     */
		/* 162 */
		double[] d = {z2[0], z2[1]};
		/* 163 */
		d[1] = (-d[1]);
		/* 164 */
		HyperbolicParams.multiply(d, z1);
		/* 165 */
		d[0] += 1.0D;
		/* 166 */
		HyperbolicParams.divide(p, d);
		/*     */
		/* 168 */
		o[0] = z1[0];
		/* 169 */
		o[1] = (-z1[1]);
		/* 170 */
		HyperbolicParams.multiply(o, z2);
		/* 171 */
		o[0] += 1.0D;
		/* 172 */
		HyperbolicParams.divide(o, d);
		/*     */
	}

	/*     */
	/*     */
	private static void transform(double[] z, double[] p, double[] o) {
		/* 176 */
		double[] z0 = {z[0], z[1]};
		/* 177 */
		HyperbolicParams.multiply(z, o);
		/* 178 */
		z[0] += p[0];
		/* 179 */
		z[1] += p[1];
		/*     */
		/* 181 */
		double[] d = {p[0], p[1]};
		/* 182 */
		d[1] = (-d[1]);
		/* 183 */
		HyperbolicParams.multiply(d, z0);
		/* 184 */
		HyperbolicParams.multiply(d, o);
		/* 185 */
		d[0] += 1.0D;
		/*     */
		/* 187 */
		HyperbolicParams.divide(z, d);
		/*     */
	}
	/*     */
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/prefuse/hyperbolictree/HyperbolicTranslation.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */