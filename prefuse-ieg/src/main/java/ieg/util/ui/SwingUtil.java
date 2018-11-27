package ieg.util.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.Border;

/**
 * some swing helpers from
 * http://www.javapractices.com/topic/TopicAction.do?Id=152
 * 
 * @author Hirondelle Systems (code snippets used on BSD license)
 */
public abstract class SwingUtil {

	/**
	 * <P>
	 * The Java Look and Feel Design Guidelines recommend that components be
	 * spaced using this scheme :
	 * <ul>
	 * <li>place <tt>6*N</tt> pixels between items
	 * <li>but, place <tt>6*N-1</tt> pixels between items if there is one white
	 * border present
	 * </ul>
	 * 
	 * The <tt>XXX_SPACE(S)</tt> and {@link #STANDARD_BORDER} items follow the
	 * second scheme, and use <tt>6*N-1</tt> pixel spacings (since this is the
	 * more common case).
	 */
	public static final int ONE_SPACE = 5;
	public static final int TWO_SPACES = 11;
	public static final int THREE_SPACES = 17;
	public static final int STANDARD_BORDER = ONE_SPACE;

	/**
	 * Return a border of dimensions recommended by the Java Look and Feel
	 * Design Guidelines, suitable for many common cases.
	 * 
	 *<P>
	 * Each side of the border has size {@link UiConsts#STANDARD_BORDER}.
	 */
	public static Border getStandardBorder() {
		return BorderFactory.createEmptyBorder(SwingUtil.STANDARD_BORDER,
				SwingUtil.STANDARD_BORDER, SwingUtil.STANDARD_BORDER,
				SwingUtil.STANDARD_BORDER);
	}

	public static JComponent getCommandRowWithLabel(JLabel label, JComponent... buttons) {
		java.util.List<JComponent> aButtons = Arrays.asList(buttons);
		equalizeSizes(aButtons);
		JPanel panel = new JPanel();
		LayoutManager layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder(SwingUtil.THREE_SPACES, 
				SwingUtil.THREE_SPACES, 0, 0));
		panel.add(Box.createHorizontalGlue());

		Iterator<JComponent> buttonsIter = aButtons.iterator();
		while (buttonsIter.hasNext()) {
			panel.add(buttonsIter.next());
			if (buttonsIter.hasNext()) {
				panel.add(Box.createHorizontalStrut(SwingUtil.ONE_SPACE));
			}
		}
		
		JPanel parent = new JPanel(new BorderLayout());
		parent.add(panel, BorderLayout.EAST);
		parent.add(label, BorderLayout.CENTER);
		
		return parent;
	}

	/**
	 * Make a horizontal row of buttons of equal size, which are equally spaced,
	 * and aligned on the right.
	 */
	public static JComponent getCommandRow(JComponent... aButtons) {
		return getCommandRow(Arrays.asList(aButtons));
	}

	/**
	 * Make a horizontal row of buttons of equal size, which are equally spaced,
	 * and aligned on the right.
	 * 
	 * <P>
	 * The returned component has border spacing only on the top (of the size
	 * recommended by the Look and Feel Design Guidelines). All other spacing
	 * must be applied elsewhere ; usually, this will only mean that the
	 * dialog's top-level panel should use {@link #getStandardBorder}.
	 * 
	 * @param aButtons
	 *            contains the buttons to be placed in a row.
	 */
	public static JComponent getCommandRow(java.util.List<JComponent> aButtons) {
		equalizeSizes(aButtons);
		JPanel panel = new JPanel();
		LayoutManager layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder(SwingUtil.THREE_SPACES, 
				SwingUtil.THREE_SPACES, 0, 0));
		panel.add(Box.createHorizontalGlue());

		Iterator<JComponent> buttonsIter = aButtons.iterator();
		while (buttonsIter.hasNext()) {
			panel.add(buttonsIter.next());
			if (buttonsIter.hasNext()) {
				panel.add(Box.createHorizontalStrut(SwingUtil.ONE_SPACE));
			}
		}
		return panel;
	}

	/**
	 * Sets the items in <tt>aComponents</tt> to the same size.
	 * 
	 * <P>
	 * Sets each component's preferred and maximum sizes. The actual size is
	 * determined by the layout manager, which adjusts for locale-specific
	 * strings and customized fonts. (See this <a
	 * href="http://java.sun.com/products/jlf/ed2/samcode/prefere.html">Sun
	 * doc</a> for more information.)
	 * 
	 * @param aComponents
	 *            items whose sizes are to be equalized
	 */
	public static void equalizeSizes(java.util.List<JComponent> aComponents) {
		Dimension targetSize = new Dimension(0, 0);
		for (JComponent comp : aComponents) {
			Dimension compSize = comp.getPreferredSize();
			double width = Math.max(targetSize.getWidth(), compSize.getWidth());
			double height = Math.max(targetSize.getHeight(), compSize
					.getHeight());
			targetSize.setSize(width, height);
		}
		setSizes(aComponents, targetSize);
	}

	private static void setSizes(java.util.List<JComponent> aComponents,
			Dimension aDimension) {
		Iterator<JComponent> compsIter = aComponents.iterator();
		while (compsIter.hasNext()) {
			JComponent comp = (JComponent) compsIter.next();
			comp.setPreferredSize((Dimension) aDimension.clone());
			comp.setMaximumSize((Dimension) aDimension.clone());
		}
	}

	/**
	 * Force the escape key to call the same action as pressing the Cancel
	 * button.
	 * 
	 * <P>
	 * The <tt>Escape</tt> key does not always work (for example, when a
	 * <tt>JTable</tt> row has the focus)
	 */
	public static void addCancelByEscapeKey(JDialog fDialog,
			AbstractAction cancelAction) {
		String CANCEL_ACTION_KEY = "CANCEL_ACTION_KEY";
		int noModifiers = 0;
		KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
				noModifiers, false);
		InputMap inputMap = fDialog.getRootPane().getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(escapeKey, CANCEL_ACTION_KEY);
		fDialog.getRootPane().getActionMap().put(CANCEL_ACTION_KEY,
				cancelAction);
	}

    /**
     * adds the mouse listener to the component and all its children. This is
     * useful when you want to catch events on the ends of a scroll bar.
     * 
     * @param comp
     *            a GUI component (e.g., a scroll bar)
     * @param listener
     *            a mouse listener
     */
    public static void registerWithAllChildren(Component comp,
            MouseListener listener) {
        comp.addMouseListener(listener);

        if (comp instanceof Container)
            for (Component child : ((Container) comp).getComponents())
                registerWithAllChildren(child, listener);
    }
}
