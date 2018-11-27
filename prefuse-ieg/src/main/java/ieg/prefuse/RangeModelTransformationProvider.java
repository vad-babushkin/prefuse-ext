package ieg.prefuse;

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
public interface RangeModelTransformationProvider {

	/**
	 * @return
	 */
	int[] getAxes();

	/**
	 * @param iAx
	 * @return
	 */
	ValuedRangeModel getRangeModel(int axis);

	/**
	 * @param iAx
	 * @return
	 */
	Double getMinPosition(int iAx);

	/**
	 * @param iAx
	 * @return
	 */
	Double getMaxPosition(int iAx);

}
