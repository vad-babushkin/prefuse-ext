package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;

import java.awt.*;
import java.util.ArrayList;

public class MultiLineTextItemRenderer
		extends TextItemRenderer {
	public static final int DEFAULT_MAXLINES = 1;
	protected ArrayList m_attrList = new ArrayList();

	public void addTextAttribute(String paramString) {
		addTextAttribute(paramString, 1, null);
	}

	public void addTextAttribute(String paramString, int paramInt) {
		this.m_attrList.add(new TextEntry(paramString, paramInt, null));
	}

	public void addTextAttribute(String paramString, int paramInt, Font paramFont) {
		this.m_attrList.add(new TextEntry(paramString, paramInt, paramFont));
	}

	protected String getText(VisualItem paramVisualItem) {
		throw new UnsupportedOperationException();
	}

	protected String getText(VisualItem paramVisualItem, int paramInt) {
		String str = ((TextEntry) this.m_attrList.get(paramInt)).name;
		return paramVisualItem.getAttribute(str);
	}

	public int getNumEntries() {
		return this.m_attrList.size();
	}

	protected int getMaxLines(int paramInt) {
		return ((TextEntry) this.m_attrList.get(paramInt)).maxlines;
	}

	protected Font getFont(VisualItem paramVisualItem, int paramInt) {
		Font localFont = ((TextEntry) this.m_attrList.get(paramInt)).font;
		if (localFont == null) {
			localFont = paramVisualItem.getFont();
		}
		if (localFont == null) {
			localFont = this.m_font;
		}
		return localFont;
	}

	protected Shape getRawShape(VisualItem paramVisualItem) {
		int i = 2 * this.m_horizBorder;
		int j = 2 * this.m_vertBorder;
		for (int k = 0; k < getNumEntries(); k++) {
			Font localFont = getFont(paramVisualItem, k);
			FontMetrics localFontMetrics = Renderer.DEFAULT_GRAPHICS.getFontMetrics(localFont);
			String str = getText(paramVisualItem, k);
			int m = getMaxLines(k);
			if (str != null) {
				j += localFontMetrics.getHeight();
				i = Math.max(i, localFontMetrics.stringWidth(str) + 2 * this.m_horizBorder);
			}
		}
		getAlignedPoint(this.m_tmpPoint, paramVisualItem, i, j, this.m_xAlign, this.m_yAlign);
		this.m_textBox.setFrame(this.m_tmpPoint.getX(), this.m_tmpPoint.getY(), i, j);
		return this.m_textBox;
	}

	public Rectangle getEntryBounds(VisualItem paramVisualItem, int paramInt) {
		int i = this.m_vertBorder;
		int j = 0;
		int k = 0;
		int m = 2 * this.m_horizBorder;
		int n = 2 * this.m_vertBorder;
		for (int i1 = 0; i1 <= paramInt; i1++) {
			Font localFont = getFont(paramVisualItem, i1);
			FontMetrics localFontMetrics = Renderer.DEFAULT_GRAPHICS.getFontMetrics(localFont);
			String str = getText(paramVisualItem, i1);
			int i2 = getMaxLines(i1);
			if (str != null) {
				n += localFontMetrics.getHeight();
				m = Math.max(m, localFontMetrics.stringWidth(str) + 2 * this.m_horizBorder);
				if (i1 < paramInt) {
					i += localFontMetrics.getHeight();
				} else if (i1 == paramInt) {
					j = localFontMetrics.stringWidth(str) + 2 * this.m_horizBorder;
					k = localFontMetrics.getHeight();
				}
			}
		}
		getAlignedPoint(this.m_tmpPoint, paramVisualItem, m, n, this.m_xAlign, this.m_yAlign);
		this.m_textBox.setFrame(this.m_tmpPoint.getX(), this.m_tmpPoint.getY() + i, j, k);
		return this.m_textBox.getBounds();
	}

	public void render(Graphics2D paramGraphics2D, VisualItem paramVisualItem) {
		Paint localPaint1 = paramVisualItem.getFillColor();
		Paint localPaint2 = paramVisualItem.getColor();
		Shape localShape = getShape(paramVisualItem);
		if (localShape != null) {
			switch (getRenderType(paramVisualItem)) {
				case 1:
					paramGraphics2D.setPaint(localPaint2);
					paramGraphics2D.draw(localShape);
					break;
				case 2:
					paramGraphics2D.setPaint(localPaint1);
					paramGraphics2D.fill(localShape);
					break;
				case 3:
					paramGraphics2D.setPaint(localPaint1);
					paramGraphics2D.fill(localShape);
					paramGraphics2D.setPaint(localPaint2);
					paramGraphics2D.draw(localShape);
			}
			Rectangle localRectangle1 = localShape.getBounds();
			int i = localRectangle1.y + this.m_vertBorder;
			for (int j = 0; j < getNumEntries(); j++) {
				Font localFont = getFont(paramVisualItem, j);
				FontMetrics localFontMetrics = Renderer.DEFAULT_GRAPHICS.getFontMetrics(localFont);
				String str = getText(paramVisualItem, j);
				int k = getMaxLines(j);
				if (str != null) {
					Color localColor = (Color) paramVisualItem.getVizAttribute("overlay_" + j);
					if (localColor != null) {
						paramGraphics2D.setColor(localColor);
						Rectangle localRectangle2 = new Rectangle(localRectangle1.x + this.m_horizBorder, i, localFontMetrics.stringWidth(str), localFontMetrics.getHeight());
						paramGraphics2D.fill(localRectangle2);
					}
					paramGraphics2D.setPaint(localPaint2);
					paramGraphics2D.setFont(localFont);
					paramGraphics2D.drawString(str, localRectangle1.x + this.m_horizBorder, i + localFontMetrics.getAscent());
					i += localFontMetrics.getHeight();
				}
			}
		}
	}

	protected class TextEntry {
		String name;
		int maxlines;
		Font font;

		public TextEntry(String paramString, int paramInt, Font paramFont) {
			this.name = paramString;
			this.maxlines = paramInt;
			this.font = paramFont;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/MultiLineTextItemRenderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */