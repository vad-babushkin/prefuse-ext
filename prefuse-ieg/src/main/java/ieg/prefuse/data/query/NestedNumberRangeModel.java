package ieg.prefuse.data.query;

import prefuse.data.query.NumberRangeModel;

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
public class NestedNumberRangeModel extends NumberRangeModel {

    private static final long serialVersionUID = 1L;
    
    protected Number[] min;
	protected Number[] max;
	
	/**
	 * 
	 */
	public NestedNumberRangeModel(Number lo, Number hi,Number[] min, Number[] max) {
		super(lo,hi,lo,hi);
		
		this.min = min;
		this.max = max;
		
		recalculateMinMax();
	}
	
	/**
	 * 
	 */
	private void recalculateMinMax() {
		double lmin = 1;
		double lmax = 1;
		for(int i=0; i<min.length; i++) {
			lmin *= min[i].doubleValue();
			lmax *= max[i].doubleValue();
		}
		setValueRange((Number)getLowValue(), (Number)getHighValue(), lmin, lmax);
	}

	/**
	 * @return the min
	 */
	public Number[] getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Number[] min) {
		this.min = min;
		recalculateMinMax();
	}

	/**
	 * @return the max
	 */
	public Number[] getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Number[] max) {
		this.max = max;
		recalculateMinMax();
	}
}
