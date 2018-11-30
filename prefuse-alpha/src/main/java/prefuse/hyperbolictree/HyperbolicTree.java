/*     */
package prefuse.hyperbolictree;
/*     */
/*     */

import edu.berkeley.guir.prefuse.*;
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
import edu.berkeley.guir.prefuse.render.*;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.util.ColorLib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.net.URL;

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
/*     */ public class HyperbolicTree
		/*     */ extends JFrame
		/*     */ {
	/*     */   public static final String TREE_CHI = "/chitest.hdir";
	/*     */   public static ItemRegistry registry;
	/*     */   public static Tree tree;
	/*     */   public static Display display;
	/*     */   public static HyperbolicTranslation translation;
	/*  56 */   public static ActivityMap actmap = new ActivityMap();
	/*     */
	/*  58 */   private static Font frameCountFont = new Font("SansSerif", 0, 14);

	/*     */
	/*     */
	public HyperbolicTree(String dataFile) {
		/*  61 */
		super("HyperbolicTree -- " + dataFile);
		/*     */
		try
			/*     */ {
			/*  64 */
			URL input = HyperbolicTree.class.getResource(dataFile);
			/*  65 */
			tree = new HDirTreeReader().loadTree(input);
			/*     */
			/*     */
			/*  68 */
			registry = new ItemRegistry(tree);
			/*  69 */
			registry.setItemComparator(new DOIItemComparator());
			/*  70 */
			display = new Display(registry);
			/*     */
			/*     */
			/*     */
			/*     */
			/*  75 */
			TextItemRenderer nodeRenderer = new TextItemRenderer();
			/*  76 */
			nodeRenderer.setRoundedCorner(8, 8);
			/*  77 */
			nodeRenderer.setMaxTextWidth(75);
			/*  78 */
			nodeRenderer.setAbbrevType(0);
			/*     */
			/*  80 */
			NullRenderer nodeRenderer2 = new NullRenderer();
			/*     */
			/*  82 */
			DefaultEdgeRenderer edgeRenderer = new DefaultEdgeRenderer()
					/*     */ {
				/*     */
				protected void getCurveControlPoints(EdgeItem eitem, Point2D[] cp, double x1, double y1, double x2, double y2)
				/*     */ {
					/*  86 */
					Point2D c = eitem.getLocation();
					/*  87 */
					cp[0].setLocation(c);
					/*  88 */
					cp[1].setLocation(c);
					/*     */
				}
				/*  90 */
			};
			/*  91 */
			edgeRenderer.setEdgeType(1);
			/*  92 */
			edgeRenderer.setRenderType(1);
			/*     */
			/*     */
			/*  95 */
			registry.setRendererFactory(new DemoRendererFactory(
					/*  96 */         nodeRenderer, nodeRenderer2, edgeRenderer));
			/*     */
			/*     */
			/*  99 */
			display.setSize(500, 460);
			/* 100 */
			display.setBackground(Color.WHITE);
			/* 101 */
			display.addControlListener(new DemoControl());
			/* 102 */
			TranslateControl dragger = new TranslateControl();
			/* 103 */
			display.addMouseListener(dragger);
			/* 104 */
			display.addMouseMotionListener(dragger);
			/*     */
			/*     */
			/* 107 */
			ActionList repaint = new ActionList(registry);
			/* 108 */
			repaint.add(new HyperbolicTreeMapper());
			/* 109 */
			repaint.add(new HyperbolicVisibilityFilter());
			/* 110 */
			repaint.add(new RepaintAction());
			/* 111 */
			actmap.put("repaint", repaint);
			/*     */
			/*     */
			/* 114 */
			ActionList filter = new ActionList(registry);
			/* 115 */
			filter.add(new TreeFilter());
			/* 116 */
			filter.add(new HyperbolicTreeLayout());
			/* 117 */
			filter.add(new DemoColorFunction());
			/* 118 */
			filter.add(repaint);
			/* 119 */
			actmap.put("filter", filter);
			/*     */
			/*     */
			/* 122 */
			ActionList translate = new ActionList(registry);
			/* 123 */
			translation = new HyperbolicTranslation();
			/* 124 */
			translate.add(translation);
			/* 125 */
			translate.add(repaint);
			/* 126 */
			actmap.put("translate", translate);
			/*     */
			/*     */
			/* 129 */
			ActionList animate = new ActionList(registry, 1000L, 20L);
			/* 130 */
			animate.setPacingFunction(new SlowInSlowOutPacer());
			/* 131 */
			animate.add(translate);
			/* 132 */
			actmap.put("animate", animate);
			/*     */
			/*     */
			/* 135 */
			ActionList endTranslate = new ActionList(registry);
			/* 136 */
			endTranslate.add(new HyperbolicTranslationEnd());
			/* 137 */
			actmap.put("endTranslate", endTranslate);
			/*     */
			/*     */
			/* 140 */
			setDefaultCloseOperation(3);
			/* 141 */
			getContentPane().add(display, "Center");
			/* 142 */
			pack();
			/* 143 */
			setVisible(true);
			/*     */
			/*     */
			/* 146 */
			registry.getDefaultFocusSet().set(tree.getRoot());
			/*     */
			/* 148 */
			actmap.scheduleNow("filter");
			/*     */
		} catch (Exception e) {
			/* 150 */
			e.printStackTrace();
			/*     */
		}
		/*     */
	}

	/*     */
	/*     */
	public static void main(String[] args) {
		/* 155 */
		String infile = "/chitest.hdir";
		/* 156 */
		if (args.length > 0)
			/* 157 */ infile = args[0];
		/* 158 */
		new HyperbolicTree(infile);
	}

	/*     */
	/*     */   public class TranslateControl extends MouseAdapter implements MouseMotionListener {
		public TranslateControl() {
		}

		/*     */
		/* 162 */ boolean drag = false;

		/*     */
		/* 164 */
		public void mousePressed(MouseEvent e) {
			HyperbolicTree.translation.setStartPoint(e.getX(), e.getY());
		}

		/*     */
		/*     */
		public void mouseDragged(MouseEvent e) {
			/* 167 */
			this.drag = true;
			/* 168 */
			HyperbolicTree.translation.setEndPoint(e.getX(), e.getY());
			/* 169 */
			HyperbolicTree.actmap.scheduleNow("translate");
			/*     */
		}

		/*     */
		/* 172 */
		public void mouseReleased(MouseEvent e) {
			if (this.drag) {
				/* 173 */
				HyperbolicTree.actmap.scheduleNow("endTranslate");
				/* 174 */
				this.drag = false;
				/*     */
			}
			/*     */
		}

		/*     */
		/*     */
		public void mouseMoved(MouseEvent e) {
		}
		/*     */
	}

	/*     */
	/*     */   public class DemoControl extends ControlAdapter {
		/*     */
		public DemoControl() {
		}

		/*     */
		/* 184 */
		public void itemEntered(VisualItem item, MouseEvent e) {
			e.getComponent().setCursor(
					/* 185 */         Cursor.getPredefinedCursor(12));
			/*     */
		}

		/*     */
		/* 188 */
		public void itemExited(VisualItem item, MouseEvent e) {
			e.getComponent().setCursor(Cursor.getDefaultCursor());
		}

		/*     */
		/*     */
		public void itemClicked(VisualItem item, MouseEvent e)
		/*     */ {
			/* 192 */
			int cc = e.getClickCount();
			/* 193 */
			if (((item instanceof NodeItem)) &&
					/* 194 */         (cc == 1)) {
				/* 195 */
				TreeNode node = (TreeNode) HyperbolicTree.registry.getEntity(item);
				/* 196 */
				if (node != null) {
					/* 197 */
					HyperbolicTree.translation.setStartPoint(e.getX(), e.getY());
					/* 198 */
					HyperbolicTree.translation.setEndPoint(e.getX(), e.getY());
					/* 199 */
					HyperbolicTree.actmap.scheduleNow("animate");
					/* 200 */
					HyperbolicTree.actmap.scheduleAfter("animate", "endTranslate");
					/*     */
				}
				/*     */
			}
			/*     */
		}
		/*     */
	}

	/*     */
	/*     */   public class DemoRendererFactory implements RendererFactory {
		/*     */     private Renderer nodeRenderer1;
		/*     */     private Renderer nodeRenderer2;
		/*     */     private Renderer edgeRenderer;

		/*     */
		/*     */
		public DemoRendererFactory(Renderer nr1, Renderer nr2, Renderer er) {
			/* 212 */
			this.nodeRenderer1 = nr1;
			/* 213 */
			this.nodeRenderer2 = nr2;
			/* 214 */
			this.edgeRenderer = er;
			/*     */
		}

		/*     */
		/* 217 */
		public Renderer getRenderer(VisualItem item) {
			if ((item instanceof NodeItem)) {
				/* 218 */
				NodeItem n = (NodeItem) item;
				/* 219 */
				NodeItem p = (NodeItem) n.getParent();
				/*     */
				/* 221 */
				double d = Double.MAX_VALUE;
				/*     */
				/* 223 */
				Point2D nl = n.getLocation();
				/* 224 */
				if (p != null) {
					/* 225 */
					d = Math.min(d, nl.distance(p.getLocation()));
					/* 226 */
					int idx = p.getChildIndex(n);
					/*     */
					/* 228 */
					if (idx > 0) {
						/* 229 */
						NodeItem b = (NodeItem) p.getChild(idx - 1);
						/* 230 */
						d = Math.min(d, nl.distance(b.getLocation()));
						/*     */
					}
					/* 232 */
					if (idx < p.getChildCount() - 1) {
						/* 233 */
						NodeItem b = (NodeItem) p.getChild(idx + 1);
						/* 234 */
						d = Math.min(d, nl.distance(b.getLocation()));
						/*     */
					}
					/*     */
				}
				/* 237 */
				if (n.getChildCount() > 0) {
					/* 238 */
					NodeItem c = (NodeItem) n.getChild(0);
					/* 239 */
					d = Math.min(d, nl.distance(c.getLocation()));
					/*     */
				}
				/*     */
				/* 242 */
				if (d > 15.0D) {
					/* 243 */
					return this.nodeRenderer1;
					/*     */
				}
				/* 245 */
				return this.nodeRenderer2;
				/*     */
			}
			/* 247 */
			if ((item instanceof EdgeItem)) {
				/* 248 */
				return this.edgeRenderer;
				/*     */
			}
			/* 250 */
			return null;
			/*     */
		}
		/*     */
	}

	/*     */
	/*     */   public class DemoColorFunction extends ColorFunction
			/*     */ {
		/* 256 */     private int thresh = 5;
		/* 257 */     private Color graphEdgeColor = Color.LIGHT_GRAY;
		/*     */     private Color[] nodeColors;
		/*     */     private Color[] edgeColors;

		/*     */
		/*     */
		public DemoColorFunction() {
			/* 262 */
			this.nodeColors = new Color[this.thresh];
			/* 263 */
			this.edgeColors = new Color[this.thresh];
			/* 264 */
			for (int i = 0; i < this.thresh; i++) {
				/* 265 */
				double frac = i / this.thresh;
				/* 266 */
				this.nodeColors[i] = ColorLib.getIntermediateColor(Color.RED, Color.BLACK, frac);
				/* 267 */
				this.edgeColors[i] = ColorLib.getIntermediateColor(Color.RED, Color.BLACK, frac);
				/*     */
			}
			/*     */
		}

		/*     */
		/*     */
		public Paint getFillColor(VisualItem item) {
			/* 272 */
			if ((item instanceof NodeItem))
				/* 273 */ return Color.WHITE;
			/* 274 */
			if ((item instanceof AggregateItem))
				/* 275 */ return Color.LIGHT_GRAY;
			/* 276 */
			if ((item instanceof EdgeItem)) {
				/* 277 */
				return getColor(item);
				/*     */
			}
			/* 279 */
			return Color.BLACK;
			/*     */
		}

		/*     */
		/*     */
		public Paint getColor(VisualItem item)
		/*     */ {
			/* 284 */
			if ((item instanceof NodeItem)) {
				/* 285 */
				int d = ((NodeItem) item).getDepth();
				/* 286 */
				return this.nodeColors[Math.min(d, this.thresh - 1)];
			}
			/* 287 */
			if ((item instanceof EdgeItem)) {
				/* 288 */
				EdgeItem e = (EdgeItem) item;
				/* 289 */
				if (e.isTreeEdge())
					/*     */ {
					/* 291 */
					int d1 = ((NodeItem) e.getFirstNode()).getDepth();
					/* 292 */
					int d2 = ((NodeItem) e.getSecondNode()).getDepth();
					/* 293 */
					int d = Math.max(d1, d2);
					/* 294 */
					return this.edgeColors[Math.min(d, this.thresh - 1)];
					/*     */
				}
				/* 296 */
				return this.graphEdgeColor;
				/*     */
			}
			/*     */
			/* 299 */
			return Color.BLACK;
			/*     */
		}
		/*     */
	}
	/*     */
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/prefuse/hyperbolictree/HyperbolicTree.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */