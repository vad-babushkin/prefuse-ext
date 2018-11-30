//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse.hyperbolictree;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.ActivityMap;
import edu.berkeley.guir.prefuse.activity.SlowInSlowOutPacer;
import edu.berkeley.guir.prefuse.collections.DOIItemComparator;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.Tree;
import edu.berkeley.guir.prefuse.graph.TreeNode;
import edu.berkeley.guir.prefuse.graph.io.HDirTreeReader;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.NullRenderer;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.RendererFactory;
import edu.berkeley.guir.prefuse.render.TextItemRenderer;
import edu.berkeley.guir.prefuse.util.ColorLib;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.net.URL;
import javax.swing.JFrame;

public class HyperbolicTree extends JFrame {
	public static final String TREE_CHI = "/chitest.hdir";
	public static ItemRegistry registry;
	public static Tree tree;
	public static Display display;
	public static HyperbolicTranslation translation;
	public static ActivityMap actmap = new ActivityMap();
	private static Font frameCountFont = new Font("SansSerif", 0, 14);

	public HyperbolicTree(String dataFile) {
		super("HyperbolicTree -- " + dataFile);

		try {
			URL input = HyperbolicTree.class.getResource(dataFile);
			tree = (new HDirTreeReader()).loadTree(input);
			registry = new ItemRegistry(tree);
			registry.setItemComparator(new DOIItemComparator());
			display = new Display(registry);
			TextItemRenderer nodeRenderer = new TextItemRenderer();
			nodeRenderer.setRoundedCorner(8, 8);
			nodeRenderer.setMaxTextWidth(75);
			nodeRenderer.setAbbrevType(0);
			NullRenderer nodeRenderer2 = new NullRenderer();
			DefaultEdgeRenderer edgeRenderer = new DefaultEdgeRenderer() {
				protected void getCurveControlPoints(EdgeItem eitem, Point2D[] cp, double x1, double y1, double x2, double y2) {
					Point2D c = eitem.getLocation();
					cp[0].setLocation(c);
					cp[1].setLocation(c);
				}
			};
			edgeRenderer.setEdgeType(1);
			edgeRenderer.setRenderType(1);
			registry.setRendererFactory(new HyperbolicTree.DemoRendererFactory(nodeRenderer, nodeRenderer2, edgeRenderer));
			display.setSize(500, 460);
			display.setBackground(Color.WHITE);
			display.addControlListener(new HyperbolicTree.DemoControl());
			HyperbolicTree.TranslateControl dragger = new HyperbolicTree.TranslateControl();
			display.addMouseListener(dragger);
			display.addMouseMotionListener(dragger);
			ActionList repaint = new ActionList(registry);
			repaint.add(new HyperbolicTreeMapper());
			repaint.add(new HyperbolicVisibilityFilter());
			repaint.add(new RepaintAction());
			actmap.put("repaint", repaint);
			ActionList filter = new ActionList(registry);
			filter.add(new TreeFilter());
			filter.add(new HyperbolicTreeLayout());
			filter.add(new HyperbolicTree.DemoColorFunction());
			filter.add(repaint);
			actmap.put("filter", filter);
			ActionList translate = new ActionList(registry);
			translation = new HyperbolicTranslation();
			translate.add(translation);
			translate.add(repaint);
			actmap.put("translate", translate);
			ActionList animate = new ActionList(registry, 1000L, 20L);
			animate.setPacingFunction(new SlowInSlowOutPacer());
			animate.add(translate);
			actmap.put("animate", animate);
			ActionList endTranslate = new ActionList(registry);
			endTranslate.add(new HyperbolicTranslationEnd());
			actmap.put("endTranslate", endTranslate);
			this.setDefaultCloseOperation(3);
			this.getContentPane().add(display, "Center");
			this.pack();
			this.setVisible(true);
			registry.getDefaultFocusSet().set(tree.getRoot());
			actmap.runNow("filter");
		} catch (Exception var13) {
			var13.printStackTrace();
		}

	}

	public static void main(String[] args) {
		String infile = "/chitest.hdir";
		if (args.length > 0) {
			infile = args[0];
		}

		new HyperbolicTree(infile);
	}

	public class TranslateControl extends MouseAdapter implements MouseMotionListener {
		boolean drag = false;

		public TranslateControl() {
		}

		public void mousePressed(MouseEvent e) {
			HyperbolicTree.translation.setStartPoint(e.getX(), e.getY());
		}

		public void mouseDragged(MouseEvent e) {
			this.drag = true;
			HyperbolicTree.translation.setEndPoint(e.getX(), e.getY());
			HyperbolicTree.actmap.runNow("translate");
		}

		public void mouseReleased(MouseEvent e) {
			if (this.drag) {
				HyperbolicTree.actmap.runNow("endTranslate");
				this.drag = false;
			}

		}

		public void mouseMoved(MouseEvent e) {
		}
	}

	public class DemoControl extends ControlAdapter {
		public DemoControl() {
		}

		public void itemEntered(VisualItem item, MouseEvent e) {
			e.getComponent().setCursor(Cursor.getPredefinedCursor(12));
		}

		public void itemExited(VisualItem item, MouseEvent e) {
			e.getComponent().setCursor(Cursor.getDefaultCursor());
		}

		public void itemClicked(VisualItem item, MouseEvent e) {
			int cc = e.getClickCount();
			if (item instanceof NodeItem && cc == 1) {
				TreeNode node = (TreeNode)HyperbolicTree.registry.getEntity(item);
				if (node != null) {
					HyperbolicTree.translation.setStartPoint(e.getX(), e.getY());
					HyperbolicTree.translation.setEndPoint(e.getX(), e.getY());
					HyperbolicTree.actmap.runNow("animate");
					HyperbolicTree.actmap.runAfter("animate", "endTranslate");
				}
			}

		}
	}

	public class DemoRendererFactory implements RendererFactory {
		private Renderer nodeRenderer1;
		private Renderer nodeRenderer2;
		private Renderer edgeRenderer;

		public DemoRendererFactory(Renderer nr1, Renderer nr2, Renderer er) {
			this.nodeRenderer1 = nr1;
			this.nodeRenderer2 = nr2;
			this.edgeRenderer = er;
		}

		public Renderer getRenderer(VisualItem item) {
			if (item instanceof NodeItem) {
				NodeItem n = (NodeItem)item;
				NodeItem p = (NodeItem)n.getParent();
				double d = 1.7976931348623157E308D;
				Point2D nl = n.getLocation();
				if (p != null) {
					d = Math.min(d, nl.distance(p.getLocation()));
					int idx = p.getChildIndex(n);
					NodeItem b;
					if (idx > 0) {
						b = (NodeItem)p.getChild(idx - 1);
						d = Math.min(d, nl.distance(b.getLocation()));
					}

					if (idx < p.getChildCount() - 1) {
						b = (NodeItem)p.getChild(idx + 1);
						d = Math.min(d, nl.distance(b.getLocation()));
					}
				}

				if (n.getChildCount() > 0) {
					NodeItem c = (NodeItem)n.getChild(0);
					d = Math.min(d, nl.distance(c.getLocation()));
				}

				return d > 15.0D ? this.nodeRenderer1 : this.nodeRenderer2;
			} else {
				return item instanceof EdgeItem ? this.edgeRenderer : null;
			}
		}
	}

	public class DemoColorFunction extends ColorFunction {
		private int thresh = 5;
		private Color graphEdgeColor;
		private Color[] nodeColors;
		private Color[] edgeColors;

		public DemoColorFunction() {
			this.graphEdgeColor = Color.LIGHT_GRAY;
			this.nodeColors = new Color[this.thresh];
			this.edgeColors = new Color[this.thresh];

			for(int i = 0; i < this.thresh; ++i) {
				double frac = (double)i / (double)this.thresh;
				this.nodeColors[i] = ColorLib.getIntermediateColor(Color.RED, Color.BLACK, frac);
				this.edgeColors[i] = ColorLib.getIntermediateColor(Color.RED, Color.BLACK, frac);
			}

		}

		public Paint getFillColor(VisualItem item) {
			if (item instanceof NodeItem) {
				return Color.WHITE;
			} else if (item instanceof AggregateItem) {
				return Color.LIGHT_GRAY;
			} else {
				return (Paint)(item instanceof EdgeItem ? this.getColor(item) : Color.BLACK);
			}
		}

		public Paint getColor(VisualItem item) {
			if (item instanceof NodeItem) {
				int d = ((NodeItem)item).getDepth();
				return this.nodeColors[Math.min(d, this.thresh - 1)];
			} else if (item instanceof EdgeItem) {
				EdgeItem e = (EdgeItem)item;
				if (e.isTreeEdge()) {
					int d1 = ((NodeItem)e.getFirstNode()).getDepth();
					int d2 = ((NodeItem)e.getSecondNode()).getDepth();
					int dx = Math.max(d1, d2);
					return this.edgeColors[Math.min(dx, this.thresh - 1)];
				} else {
					return this.graphEdgeColor;
				}
			} else {
				return Color.BLACK;
			}
		}
	}
}
