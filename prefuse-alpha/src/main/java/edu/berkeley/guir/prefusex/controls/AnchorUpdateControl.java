package edu.berkeley.guir.prefusex.controls;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.action.assignment.Layout;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.event.ControlAdapter;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class AnchorUpdateControl
		extends ControlAdapter {
	private Layout[] m_layouts;
	private Activity m_activity;
	private Point2D m_tmp = new Point2D.Float();

	public AnchorUpdateControl(Layout paramLayout) {
		this(paramLayout, null);
	}

	public AnchorUpdateControl(Layout paramLayout, Activity paramActivity) {
		this(new Layout[]{paramLayout}, paramActivity);
	}

	public AnchorUpdateControl(Layout[] paramArrayOfLayout, Activity paramActivity) {
		this.m_layouts = ((Layout[]) paramArrayOfLayout.clone());
		this.m_activity = paramActivity;
	}

	public void mouseExited(MouseEvent paramMouseEvent) {
		for (int i = 0; i < this.m_layouts.length; i++) {
			this.m_layouts[i].setLayoutAnchor(null);
		}
		if (this.m_activity != null) {
			this.m_activity.runNow();
		}
	}

	public void mouseMoved(MouseEvent paramMouseEvent) {
		moveEvent(paramMouseEvent);
	}

	public void mouseDragged(MouseEvent paramMouseEvent) {
		moveEvent(paramMouseEvent);
	}

	public void moveEvent(MouseEvent paramMouseEvent) {
		Display localDisplay = (Display) paramMouseEvent.getSource();
		localDisplay.getAbsoluteCoordinate(paramMouseEvent.getPoint(), this.m_tmp);
		for (int i = 0; i < this.m_layouts.length; i++) {
			this.m_layouts[i].setLayoutAnchor(this.m_tmp);
		}
		if (this.m_activity != null) {
			this.m_activity.runNow();
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/controls/AnchorUpdateControl.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */