//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.datamountain;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.assignment.SizeFunction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ActivityAdapter;
import edu.berkeley.guir.prefuse.event.ActivityListener;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
import edu.berkeley.guir.prefuse.util.BrowserLauncher;
import edu.berkeley.guir.prefusex.force.DragForce;
import edu.berkeley.guir.prefusex.force.ForceItem;
import edu.berkeley.guir.prefusex.force.ForceSimulator;
import edu.berkeley.guir.prefusex.force.NBodyForce;
import edu.berkeley.guir.prefusex.force.SpringForce;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.net.URL;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DataMountain extends JFrame {
	private ActionList forces;

	public DataMountain(String datafile) {
		super("DataMountain -- " + datafile);
		Graph g = null;

		try {
			URL dataurl = DataMountain.class.getResource(datafile);
			g = (new XMLGraphReader()).loadGraph(dataurl);
		} catch (Exception var16) {
			var16.printStackTrace();
			System.exit(1);
		}

		final ItemRegistry registry = new ItemRegistry(g);
		registry.setItemComparator(new DataMountain.DataMountainComparator());
		TextImageItemRenderer nodeRenderer = new TextImageItemRenderer() {
			BasicStroke m_stroke = new BasicStroke(2.0F);

			public BasicStroke getStroke(VisualItem item) {
				return this.m_stroke;
			}
		};
		nodeRenderer.setVerticalAlignment(1);
		nodeRenderer.setRenderType(1);
		nodeRenderer.setHorizontalPadding(0);
		nodeRenderer.setVerticalPadding(0);
		nodeRenderer.setMaxImageDimensions(100, 100);
		registry.setRendererFactory(new DefaultRendererFactory(nodeRenderer));
		ActionList init = new ActionList(registry);
		RandomLayout rl = new RandomLayout();
		init.add(new GraphFilter());
		init.add(rl);
		ActionList update = new ActionList(registry);
		update.add(new DataMountain.DataMountainSizeFunction());
		update.add(new ColorFunction());
		update.add(new RepaintAction());
		ForceSimulator fsim = new ForceSimulator();
		fsim.addForce(new NBodyForce(-0.4F, 25.0F, 0.9F));
		fsim.addForce(new SpringForce(8.0E-6F, 0.0F));
		fsim.addForce(new DragForce());
		final ForceDirectedLayout fl = new DataMountain.DataMountainForceLayout(fsim);
		ActivityListener fReset = new ActivityAdapter() {
			public void activityCancelled(Activity a) {
				fl.reset(registry);
			}
		};
		ActionList preforce = new ActionList(registry, 1000L);
		preforce.add(fl);
		preforce.addActivityListener(fReset);
		ActionList forces = new ActionList(registry, -1L);
		forces.add(fl);
		forces.add(update);
		forces.addActivityListener(fReset);
		Rectangle2D bounds = new Double(20.0D, 30.0D, 590.0D, 400.0D);
		rl.setLayoutBounds(bounds);
		fl.setLayoutBounds(bounds);
		Display display = new Display(registry);
		display.setSize(635, 450);
		display.addControlListener(new DataMountain.DataMountainControl(forces));
		this.setDefaultCloseOperation(3);
		this.getContentPane().add(display);
		this.pack();
		this.setVisible(true);
		nodeRenderer.getImageFactory().preloadImages(g.getNodes(), "image");
		init.runNow();
		update.runAfter(preforce);
		preforce.runNow();
	}

	public static void main(String[] args) {
		String infile = "/data.xml";
		if (args.length > 0) {
			infile = args[0];
		}

		new DataMountain(infile);
	}

	public class DataMountainComparator implements Comparator {
		public DataMountainComparator() {
		}

		public int compare(Object o1, Object o2) {
			double y1 = ((VisualItem)o1).getY();
			double y2 = ((VisualItem)o2).getY();
			return y1 > y2 ? 1 : (y1 < y2 ? -1 : 0);
		}
	}

	public class DataMountainSizeFunction extends SizeFunction {
		public DataMountainSizeFunction() {
		}

		public void run(ItemRegistry registry, double frac) {
			super.run(registry, frac);
			registry.touchNodeItems();
		}

		public double getSize(VisualItem item) {
			double y = item.getEndLocation().getY();
			return 0.2D + y / 1400.0D;
		}
	}

	public class DataMountainForceLayout extends ForceDirectedLayout {
		public DataMountainForceLayout(ForceSimulator fsim) {
			super(fsim, true, false);
		}

		public void reset(ItemRegistry registry) {
			Iterator iter = registry.getNodeItems();

			while(iter.hasNext()) {
				NodeItem nitem = (NodeItem)iter.next();
				ForceItem aitem = (ForceItem)nitem.getVizAttribute("anchorItem");
				if (aitem != null) {
					aitem.location[0] = (float)nitem.getEndLocation().getX();
					aitem.location[1] = (float)nitem.getEndLocation().getY();
				}
			}

			super.reset(registry);
		}

		protected void initSimulator(ItemRegistry registry, ForceSimulator fsim) {
			Iterator iter = registry.getNodeItems();

			while(iter.hasNext()) {
				NodeItem nitem = (NodeItem)iter.next();
				ForceItem fitem = (ForceItem)nitem.getVizAttribute("forceItem");
				if (fitem == null) {
					fitem = new ForceItem();
					nitem.setVizAttribute("forceItem", fitem);
				}

				fitem.location[0] = (float)nitem.getEndLocation().getX();
				fitem.location[1] = (float)nitem.getEndLocation().getY();
				ForceItem aitem = (ForceItem)nitem.getVizAttribute("anchorItem");
				if (aitem == null) {
					aitem = new ForceItem();
					nitem.setVizAttribute("anchorItem", aitem);
					aitem.location[0] = fitem.location[0];
					aitem.location[1] = fitem.location[1];
				}

				fsim.addItem(fitem);
				fsim.addSpring(fitem, aitem, 0.0F);
			}

		}
	}

	public class DataMountainControl extends ControlAdapter {
		public static final String URL = "http://www.amazon.com/exec/obidos/tg/detail/-/";
		private VisualItem activeItem;
		private Activity forces;
		private Point2D down = new java.awt.geom.Point2D.Double();
		private Point2D tmp = new java.awt.geom.Point2D.Double();
		private boolean wasFixed;
		private boolean dragged;
		private boolean repaint = false;

		public DataMountainControl(Activity forces) {
			this.forces = forces;
		}

		public void itemEntered(VisualItem item, MouseEvent e) {
			if (item instanceof NodeItem) {
				Display d = (Display)e.getSource();
				d.setCursor(Cursor.getPredefinedCursor(12));
				this.activeItem = item;
				this.wasFixed = item.isFixed();
			}
		}

		public void itemExited(VisualItem item, MouseEvent e) {
			if (item instanceof NodeItem) {
				if (this.activeItem == item) {
					this.activeItem = null;
					item.setFixed(this.wasFixed);
				}

				Display d = (Display)e.getSource();
				d.setCursor(Cursor.getDefaultCursor());
			}
		}

		public void itemPressed(VisualItem item, MouseEvent e) {
			if (item instanceof NodeItem) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					ItemRegistry registry = item.getItemRegistry();
					registry.getDefaultFocusSet().set(item.getEntity());
					item.setFixed(true);
					this.dragged = false;
					Display d = (Display)e.getComponent();
					this.down = d.getAbsoluteCoordinate(e.getPoint(), this.down);
					this.forces.runNow();
				}
			}
		}

		public void itemReleased(VisualItem item, MouseEvent e) {
			if (item instanceof NodeItem) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (this.dragged) {
						this.activeItem = null;
						item.setFixed(this.wasFixed);
						this.dragged = false;
					}

					ItemRegistry registry = item.getItemRegistry();
					registry.getDefaultFocusSet().clear();
					this.forces.cancel();
				}
			}
		}

		public void itemClicked(VisualItem item, MouseEvent e) {
			if (item instanceof NodeItem) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.getClickCount() == 2) {
						String id = item.getAttribute("id");
						BrowserLauncher.showDocument("http://www.amazon.com/exec/obidos/tg/detail/-/" + id);
					}

				}
			}
		}

		public void itemDragged(VisualItem item, MouseEvent e) {
			if (item instanceof NodeItem) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					this.dragged = true;
					Display d = (Display)e.getComponent();
					this.tmp = d.getAbsoluteCoordinate(e.getPoint(), this.tmp);
					double dx = this.tmp.getX() - this.down.getX();
					double dy = this.tmp.getY() - this.down.getY();
					Point2D p = item.getLocation();
					item.updateLocation(p.getX() + dx, p.getY() + dy);
					item.setLocation(p.getX() + dx, p.getY() + dy);
					this.down.setLocation(this.tmp);
					if (this.repaint) {
						item.getItemRegistry().repaint();
					}

				}
			}
		}
	}
}
