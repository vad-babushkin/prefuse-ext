package prefuse.demos.idot.util;

import java.util.HashMap;

import prefuse.Constants;
import prefuse.action.assignment.ShapeAction;
import prefuse.visual.VisualItem;

public class ShapeDecoder extends ShapeAction {
	private String m_dataField;
	
	/**
	 * The known shape names and the corresponding shape constants
	 * used by Prefuse
	 */
	public static final HashMap<String,Integer> KNOWN_SHAPES;

	static {
		KNOWN_SHAPES = new HashMap<String, Integer>();
		KNOWN_SHAPES.put("box", Constants.SHAPE_RECTANGLE);
		KNOWN_SHAPES.put("circle", Constants.SHAPE_ELLIPSE);
		KNOWN_SHAPES.put("point", Constants.SHAPE_ELLIPSE);
	}

	/**
	 * Creates a new ShapeDecoder
	 * 
	 * @param dataField  the name of the field to get the shape name from
	 */
	public ShapeDecoder(String dataField) {
		m_dataField = dataField;
	}
	
	/**
     * Returns the data field used to encode shape values.
     * @return the data field that is mapped to shape values
     */
    public String getDataField() {
        return m_dataField;
    }
    
    /**
     * Set the data field used to encode shape values.
     * @param field the data field to map to shape values
     */
    public void setDataField(String field) {
        m_dataField = field;
    }

	@Override
	public int getShape(VisualItem item) {
		if(item.canGetString(m_dataField)) {
			String shape = item.getString(m_dataField).toLowerCase();
			Integer s = KNOWN_SHAPES.get(shape);
			if(s != null) {
				return s;
			}
		}
		return super.getShape(item);
	}
}
