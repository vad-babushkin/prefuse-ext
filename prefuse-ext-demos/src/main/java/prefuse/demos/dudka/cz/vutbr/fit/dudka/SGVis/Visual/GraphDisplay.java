package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual;

import java.awt.geom.Rectangle2D;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.GroupAction;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.HoverActionControl;
import prefuse.controls.NeighborHighlightControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.visual.sort.TreeDepthItemSorter;

/**
 * Swing widget for <a href="http://code.google.com/apis/socialgraph/docs/api.html">
 * Social Graph API lookup</a> visualization using
 * <a href="http://prefuse.org/">Prefuse</a> Library.
 */
public class GraphDisplay extends Display {
	private static final long serialVersionUID = 1L;

	/**
	 * Create swing widget for desired view.
	 * @param view View to display inside widget.
	 */
	public GraphDisplay(GraphView view) {
		super(view.getVisualization());

        // initialize the display
        setSize(600,600);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new DragControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new PanControl());
        addControlListener(new FocusControl(1, "filter"));
        addControlListener(new NeighborHighlightControl("repaint"));
        addControlListener(new HoverActionControl("repaint"));
        ZoomToFitControl zoomToFit = new ZoomToFitControl();
        zoomToFit.setZoomOverItem(false);
        addControlListener(zoomToFit);
        GraphNodeContextMenu nodeContextMenu = new GraphNodeContextMenu(view);
        addControlListener(nodeContextMenu.getControlListener());

        // Add "zoom to fit" as action
        m_vis.putAction("zoomToFit", new ZoomToFitAction("tree", this));
        
        // Make "zoom to fit" default
        m_vis.alwaysRunAfter("animate", "zoomToFit");
	}

	/**
	 * Private "zoom to fit" action's implementation
	 * Note: copy-pasted from Prefuse demos
	 */
	private class ZoomToFitAction extends GroupAction {
		private Display display;
		public ZoomToFitAction(String graphGroup, Display display) {
			super(graphGroup);
			this.display = display;
		}
		public void run(double frac) {
			Visualization vis = display.getVisualization();
			Rectangle2D bounds = vis.getBounds(Visualization.ALL_ITEMS);
			GraphicsLib.expand(bounds, 50 + (int)(1/display.getScale()));
			DisplayLib.fitViewToBounds(display, bounds, 500);
		}
	}

}
