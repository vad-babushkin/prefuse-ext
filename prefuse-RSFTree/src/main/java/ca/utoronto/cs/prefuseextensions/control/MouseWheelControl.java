package ca.utoronto.cs.prefuseextensions.control;

import java.awt.event.MouseWheelEvent;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

/**
 * Increases or decreases a specified field when the mouse wheel moves.
 * 
 * @version 1.0
 * @author <a href="http://www.cs.utoronto.ca/~ccollins">Christopher Collins</a>
 */
public class MouseWheelControl extends ControlAdapter {

	/**
	 * The action to run when the mouse wheel is moved.
	 */
	private String m_action; 
	
	/**
	 * The field to increment/decrement when the mouse wheel moves
	 */
	private String m_field;
	
	/**
	 * The upper bound for the value of m_field.
	 */
	private double m_max;
	
	/**
	 * The lower bound for the value of m_field.
	 */
	private double m_min;
	
	/**
	 * The increment/decrement of m_field per mouse wheel unit.
	 */
	private double m_increment;
	
	/**
	 * Create a new mouse wheel control to optionally manipulate a node parameter and optionally run an action 
	 * after each manipulation.  Uses default max, min, and increment. 
	 * 
	 * @param action the action to run when the mouse wheel moves
	 * @param field the field to manipulate when the mouse wheel moves
	 */
	public MouseWheelControl (String action, String field) {
		this(action, field, 0.2, Double.MAX_VALUE, 0.1);
	}
	
	/**
	 * @param action the action to run when the mouse wheel moves
	 * @param field the field to manipulate when the mouse wheel moves
	 */
	public MouseWheelControl (String action, String field, double min, double max, double increment) {
		super();
		m_field = field;
		m_action = action;
		m_min = min;
		m_max = max;
		m_increment = increment;
	}
	
	public void itemWheelMoved(VisualItem item, MouseWheelEvent e) {
		if (m_field != null) {
			if (e.getWheelRotation() < 0)
				item.setDouble(m_field, (item.getDouble(m_field) <= m_max ?	
					item.getDouble(m_field) + m_increment : m_max));
			else
				item.setDouble(m_field, (item.getDouble(m_field) > m_min ? item
					.getDouble(m_field) - m_increment: m_min));
		}
		if (m_action != null)
			item.getVisualization().run(m_action);
	}
}
