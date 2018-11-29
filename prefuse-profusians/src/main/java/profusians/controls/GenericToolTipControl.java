package profusians.controls;

import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

/**
 * Basic generic tooltip control to display multiple field informations
 * 
 * @author <a href="http://goosebumps4all.net"> Martin Dudek </a>
 * 
 */

public class GenericToolTipControl extends ControlAdapter {

    private static final int DISMISSDELAY = Integer.MAX_VALUE;

    private String[] m_fields;

    private String[] m_texts;

    private StringBuffer m_sbuf;

    private int m_maxWidth;

    /**
         * Creates a new GenericToolTipControl
         * 
         * @param texts
         *                An array of the texts which should be displayed on the
         *                left side of the tooltip before the field values as
         *                explanations
         * @param fields
         *                an array of item field names which contains the
         *                informations to be displayed
         * @param maxWidth
         *                the maximum widht of the tooltip
         */

    public GenericToolTipControl( String[] texts, String[] fields,
	     int maxWidth) {
	this(texts, fields, maxWidth, 476);
    }

    /**
         * Creates a new GenericToolTipControl
         * 
         * @param texts
         *                An array of the texts which should be displayed on the
         *                left side of the tooltip before the field values as
         *                explanations
         * @param fields
         *                an array of item field names which contains the
         *                informations to be displayed
         * @param maxWidth
         *                the maximum widht of the tooltip
         * @param initialDelay
         *                the number of milliseconds to delay (after the cursor
         *                has paused) before displaying the tooltip
         */

    public GenericToolTipControl( String[] texts, String[] fields,
	     int maxWidth, int initialDelay) {
	m_texts = texts;
	m_fields = fields;
	m_sbuf = new StringBuffer();

	m_maxWidth = maxWidth;
	ToolTipManager.sharedInstance().setInitialDelay(initialDelay);

    }

    public void itemEntered( VisualItem vi, MouseEvent e) {

	String value;

	ToolTipManager.sharedInstance().setDismissDelay(DISMISSDELAY);

	m_sbuf.delete(0, m_sbuf.length());

	m_sbuf.append("<html><table>");

	boolean someInfo = false;

	for (int i = 0; i < m_fields.length; i++) {
	    if (vi.canGetString(m_fields[i])) {
		value = vi.getString(m_fields[i]);
		if ((value != null) && (value.length() > 0)) {
		    m_sbuf.append("<tr valign='top'><td><b>");
		    m_sbuf.append(m_texts[i] + "</b></td><td width="
			    + m_maxWidth + ">");
		    m_sbuf.append(value);
		    someInfo = true;
		}
		m_sbuf.append("</td></tr>");
	    }
	}

	if (someInfo) {
	    m_sbuf.append("</table>");
	    m_sbuf
		    .append("<hr size=1 width=\"97%\"><div align=\"right\"><font color=\"#999999\">esc to close&nbsp;</font></div>");
	    m_sbuf.append("</html>");

	     Display disp = (Display) e.getSource(); // the display
	    // on which
	    // the event occured
	    disp.setToolTipText(m_sbuf.toString());
	}
    }

    public void itemExited( VisualItem item, MouseEvent e) {
	 Display disp = (Display) e.getSource();
	disp.setToolTipText(null);
    }

} // end of class GenericToolTipControl
