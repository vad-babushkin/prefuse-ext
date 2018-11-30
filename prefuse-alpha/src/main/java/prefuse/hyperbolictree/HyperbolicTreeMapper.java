//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.hyperbolictree;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.assignment.TreeLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.util.Iterator;

public class HyperbolicTreeMapper extends TreeLayout {
	private Point2D m_max = new Float();

	public HyperbolicTreeMapper() {
	}

	public Point2D getLayoutAnchor(ItemRegistry registry) {
		Point2D anchor = super.getLayoutAnchor();
		if (anchor != null) {
			return anchor;
		} else {
			double x = 0.0D;
			double y = 0.0D;
			if (registry != null) {
				Display d = registry.getDisplay(0);
				x = (double)(d.getWidth() / 2);
				y = (double)(d.getHeight() / 2);
			}

			return new Double(x, y);
		}
	}

	public Rectangle2D getLayoutBounds(ItemRegistry registry) {
		Rectangle2D r = super.getLayoutBounds();
		if (r != null) {
			return r;
		} else {
			r = new java.awt.geom.Rectangle2D.Double();
			if (registry != null) {
				Display d = registry.getDisplay(0);
				r.setFrame(0.0D, 0.0D, (double)d.getWidth(), (double)d.getHeight());
			}

			return r;
		}
	}

	public void run(ItemRegistry registry, double frac) {
		Rectangle2D b = this.getLayoutBounds(registry);
		Point2D anchor = this.getLayoutAnchor(registry);
		this.m_max.setLocation(b.getWidth() / 2.0D, b.getHeight() / 2.0D);
		Iterator itemIter = registry.getItems(false);

		while(itemIter.hasNext()) {
			VisualItem item = (VisualItem)itemIter.next();
			HyperbolicParams hp = (HyperbolicParams)item.getVizAttribute("hyperbolicParams");
			if (hp != null) {
				HyperbolicParams.project(item.getLocation(), hp.z, anchor, this.m_max);
			}
		}
	}
}
