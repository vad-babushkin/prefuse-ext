package ieg.prefuse;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.layout.AxisLayout;
import prefuse.activity.Activity;
import prefuse.util.ui.ValuedRangeModel;

/**
 * 
 * 
 * <p>
 * Added:          / TL<br>
 * Modifications: 
 * </p>
 * 
 * @author Tim Lammarsch
 *
 */
public class RangeModelTransformationDisplay extends Display {

	protected double[] scale = new double[Constants.AXIS_COUNT];
	protected String[] relevantActionLists;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3993382454989162781L;
	
	/**
	 * @param vis
	 */
	public RangeModelTransformationDisplay(Visualization vis,String[] relevantActionLists) {
		super(vis);
		this.relevantActionLists = relevantActionLists;
		for(int i=0; i<Constants.AXIS_COUNT; i++)
			scale[i] = 1.0;
	}

	public synchronized void zoom(final Point2D p, double scale) {
		allAxesZoom(p,scale,false);
	}

	public synchronized void zoomAbs(final Point2D p, double scale) {
		allAxesZoom(p,scale,true);
	}
	
	public synchronized void allAxesZoom(final Point2D p, double scale,boolean absolute) {
		ArrayList<Integer> allAxes = new ArrayList<Integer>();
		allAxes.add(Constants.X_AXIS);
		allAxes.add(Constants.Y_AXIS);
		baseZoom(p,scale,allAxes,absolute);
	}
	
	public synchronized void findRelevantParameters(ArrayList<Integer> axes,ArrayList<ValuedRangeModel> rangeModels,
			ArrayList<Integer> axisTypes,ArrayList<Double> minPositions,ArrayList<Double> maxPositions) {

		for(String iKey : relevantActionLists) {
			Activity iAc = m_vis.getAction(iKey);
			if (iAc instanceof ActionList) {
				for (int i=0; i<((ActionList)iAc).size(); i++) {
					Action iAc2  = ((ActionList)iAc).get(i);
					if(iAc2 instanceof AxisLayout && axes.contains(((AxisLayout)iAc2).getAxis())) {
						if (!rangeModels.contains(((AxisLayout)iAc2).getRangeModel())) {
							rangeModels.add(((AxisLayout)iAc2).getRangeModel());
							axisTypes.add(((AxisLayout)iAc2).getAxis());
							minPositions.add(((AxisLayout)iAc2).getAxis() == Constants.X_AXIS ? ((AxisLayout)iAc2).getLayoutBounds().getMinX() : ((AxisLayout)iAc2).getLayoutBounds().getMinY());
							maxPositions.add(((AxisLayout)iAc2).getAxis() == Constants.X_AXIS ? ((AxisLayout)iAc2).getLayoutBounds().getMaxX() : ((AxisLayout)iAc2).getLayoutBounds().getMaxY());
						}
					} else if(iAc2 instanceof RangeModelTransformationProvider) {
						for(int iAx : ((RangeModelTransformationProvider)iAc2).getAxes()) {
							if (!rangeModels.contains(((RangeModelTransformationProvider)iAc2).getRangeModel(iAx))) {
								rangeModels.add(((RangeModelTransformationProvider)iAc2).getRangeModel(iAx));
								axisTypes.add(iAx);
								minPositions.add(((RangeModelTransformationProvider)iAc2).getMinPosition(iAx));
								maxPositions.add(((RangeModelTransformationProvider)iAc2).getMaxPosition(iAx));
							}
						}
					}
				}
			}				
		}
	}
	
	public synchronized void baseZoom(final Point2D p, double scale,ArrayList<Integer> axes,boolean absolute) {		
		ArrayList<ValuedRangeModel> rangeModels = new ArrayList<ValuedRangeModel>();
		ArrayList<Integer> axisTypes = new ArrayList<Integer>();
		ArrayList<Double> minPositions = new ArrayList<Double>();
		ArrayList<Double> maxPositions = new ArrayList<Double>();
		
		findRelevantParameters(axes,rangeModels,axisTypes,minPositions,maxPositions);
		
		for(int i=0; i<rangeModels.size(); i++) {
			ValuedRangeModel iModel = rangeModels.get(i);
			double zoomFocus = axisTypes.get(i) == Constants.X_AXIS ? p.getX() : p.getY();
			zoomFocus -= minPositions.get(i);
			zoomFocus /= (maxPositions.get(i)-minPositions.get(i));
			zoomFocus *= iModel.getExtent();
			zoomFocus += iModel.getValue();
			int newValue = (int)Math.round(zoomFocus - iModel.getExtent() * 0.5 / scale);
			int newExtent = (int)Math.round(iModel.getExtent() / scale);
			if(newValue + iModel.getExtent() < 10000 ) {
				iModel.setValue(newValue);
				iModel.setExtent(newExtent);
			} else {
				iModel.setExtent(newExtent);				
				iModel.setValue(newValue);
			}
		}	
		
		for(int i=0; i<axes.size(); i++) {
			if (absolute)
				this.scale[axes.get(i)] = scale;
			else
				this.scale[axes.get(i)] *= scale;
		}
		
		m_clip.invalidate();
		for(String iKey : relevantActionLists) {
			Activity iAc = m_vis.getAction(iKey);
			iAc.run();
		}		
	}
	
    public double getScale() {
        return scale[Constants.X_AXIS];
    }

	public synchronized void pan(double dx, double dy) {
		ArrayList<Integer> allAxes = new ArrayList<Integer>();
		allAxes.add(Constants.X_AXIS);
		allAxes.add(Constants.Y_AXIS);
		basePan(dx,dy,allAxes);
	}
    
    public synchronized void basePan(double dx, double dy,ArrayList<Integer> axes) {
		ArrayList<ValuedRangeModel> rangeModels = new ArrayList<ValuedRangeModel>();
		ArrayList<Integer> axisTypes = new ArrayList<Integer>();
		ArrayList<Double> minPositions = new ArrayList<Double>();
		ArrayList<Double> maxPositions = new ArrayList<Double>();
		
		findRelevantParameters(axes,rangeModels,axisTypes,minPositions,maxPositions);
    	
		for(int i=0; i<rangeModels.size(); i++) {
			ValuedRangeModel iModel = rangeModels.get(i);
			double factor = axisTypes.get(i) == Constants.X_AXIS ? dx : dy;
			factor /= (maxPositions.get(i) - minPositions.get(i));
			int newValue = Math.max(0,Math.min(10000-iModel.getExtent(),(int)Math.round(iModel.getValue()-factor*10000.0)));
			iModel.setValue(newValue);
		}
		
		m_clip.invalidate();
		for(String iKey : relevantActionLists) {
			Activity iAc = m_vis.getAction(iKey);
			iAc.run();
		}		
    }
}
