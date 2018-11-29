package profusians.render;

import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;

/**
 * An extension of the LabelRenderer class of the prefuse library which enables
 * the specification of the maximal length of the label If the length of the
 * label text is longer than the maximal length, the label text is cut and
 * concluded with three dots ...
 * 
 * @author <a href="http://goosebumps4all.net"> Martin Dudek </a>
 * 
 */

public class LimitedLabelRenderer extends LabelRenderer {

    private int m_maxLength;

    private String m_textField;

    public LimitedLabelRenderer( String textField, int maxLength) {
	super.setTextField(textField);
	m_textField = textField;
	m_maxLength = maxLength;
    }

    public String getText( VisualItem vi) {
	 String text = vi.getString(m_textField);
	if (text.length() <= m_maxLength) {
	    return text;
	} else {
	    return text.substring(0, m_maxLength - 2) + "...";
	}

    }
}
