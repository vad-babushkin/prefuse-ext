/*    */
package prefuse.hyperbolictree;
/*    */
/*    */

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
/*    */
/*    */
/*    */
/*    */
/*    */ public class HyperbolicTreeMapper
		/*    */ extends TreeLayout
		/*    */ {
	/* 23 */   private Point2D m_max = new Point2D.Float();

	/*    */
	/*    */
	public Point2D getLayoutAnchor(ItemRegistry registry) {
		/* 26 */
		Point2D anchor = super.getLayoutAnchor();
		/* 27 */
		if (anchor != null) {
			/* 28 */
			return anchor;
			/*    */
		}
		/* 30 */
		double x = 0.0D;
		double y = 0.0D;
		/* 31 */
		if (registry != null) {
			/* 32 */
			Display d = registry.getDisplay(0);
			/* 33 */
			x = d.getWidth() / 2;
			y = d.getHeight() / 2;
			/*    */
		}
		/* 35 */
		return new Point2D.Double(x, y);
		/*    */
	}

	/*    */
	/*    */
	public Rectangle2D getLayoutBounds(ItemRegistry registry) {
		/* 39 */
		Rectangle2D r = super.getLayoutBounds();
		/* 40 */
		if (r != null) {
			/* 41 */
			return r;
			/*    */
		}
		/* 43 */
		r = new Rectangle2D.Double();
		/* 44 */
		if (registry != null) {
			/* 45 */
			Display d = registry.getDisplay(0);
			/* 46 */
			r.setFrame(0.0D, 0.0D, d.getWidth(), d.getHeight());
			/*    */
		}
		/* 48 */
		return r;
		/*    */
	}

	/*    */
	/*    */
	public void run(ItemRegistry registry, double frac) {
		/* 52 */
		Rectangle2D b = getLayoutBounds(registry);
		/* 53 */
		Point2D anchor = getLayoutAnchor(registry);
		/* 54 */
		this.m_max.setLocation(b.getWidth() / 2.0D, b.getHeight() / 2.0D);
		/*    */
		/* 56 */
		Iterator itemIter = registry.getItems(false);
		/* 57 */
		while (itemIter.hasNext()) {
			/* 58 */
			VisualItem item = (VisualItem) itemIter.next();
			/* 59 */
			HyperbolicParams hp =
					/* 60 */         (HyperbolicParams) item.getVizAttribute("hyperbolicParams");
			/* 61 */
			if (hp != null) {
				/* 62 */
				HyperbolicParams.project(
						/* 63 */           item.getLocation(), hp.z, anchor, this.m_max);
				/*    */
			}
			/*    */
		}
		/*    */
	}
	/*    */
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/prefuse/hyperbolictree/HyperbolicTreeMapper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */