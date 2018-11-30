package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.util.FontLib;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;

public class TextItemRenderer
		extends ShapeRenderer {
	public static final int ALIGNMENT_LEFT = 0;
	public static final int ALIGNMENT_RIGHT = 1;
	public static final int ALIGNMENT_CENTER = 2;
	public static final int ALIGNMENT_BOTTOM = 1;
	public static final int ALIGNMENT_TOP = 0;
	protected String m_labelName = "label";
	protected int m_xAlign = 2;
	protected int m_yAlign = 2;
	protected int m_horizBorder = 3;
	protected int m_vertBorder = 0;
	protected int m_maxTextWidth = -1;
	protected int m_abbrevType = 3;
	protected StringAbbreviator m_abbrev = StringAbbreviator.getInstance();
	protected RectangularShape m_textBox = new Rectangle2D.Float();
	protected Font m_font = new Font("SansSerif", 0, 10);
	protected Point2D m_tmpPoint = new Point2D.Float();

	public void setFont(Font paramFont) {
		this.m_font = paramFont;
	}

	public void setRoundedCorner(int paramInt1, int paramInt2) {
		if (((paramInt1 == 0) || (paramInt2 == 0)) && (!(this.m_textBox instanceof Rectangle2D))) {
			this.m_textBox = new Rectangle2D.Float();
		} else {
			if (!(this.m_textBox instanceof RoundRectangle2D)) {
				this.m_textBox = new RoundRectangle2D.Float();
			}
			((RoundRectangle2D) this.m_textBox).setRoundRect(0.0D, 0.0D, 10.0D, 10.0D, paramInt1, paramInt2);
		}
	}

	public String getTextAttributeName() {
		return this.m_labelName;
	}

	public void setTextAttributeName(String paramString) {
		this.m_labelName = paramString;
	}

	public void setMaxTextWidth(int paramInt) {
		this.m_maxTextWidth = paramInt;
	}

	public void setAbbrevType(int paramInt) {
		this.m_abbrevType = paramInt;
	}

	protected String getText(VisualItem paramVisualItem) {
		String str = paramVisualItem.getAttribute(this.m_labelName);
		if (this.m_maxTextWidth > -1) {
			Font localFont = paramVisualItem.getFont();
			if (localFont == null) {
				localFont = this.m_font;
			}
			FontMetrics localFontMetrics = Renderer.DEFAULT_GRAPHICS.getFontMetrics(localFont);
			if (localFontMetrics.stringWidth(str) > this.m_maxTextWidth) {
				str = this.m_abbrev.abbreviate(str, this.m_abbrevType, localFontMetrics, this.m_maxTextWidth);
			}
		}
		return str;
	}

	protected boolean isHyperlink(VisualItem paramVisualItem) {
		Boolean localBoolean = (Boolean) paramVisualItem.getVizAttribute(this.m_labelName + "_LINK");
		return (localBoolean != null) && (Boolean.TRUE.equals(localBoolean));
	}

	protected Shape getRawShape(VisualItem paramVisualItem) {
		String str = getText(paramVisualItem);
		if (str == null) {
			str = "";
		}
		this.m_font = paramVisualItem.getFont();
		double d1 = paramVisualItem.getSize();
		if (d1 != 1.0D) {
			this.m_font = FontLib.getFont(this.m_font.getName(), this.m_font.getStyle(), (int) Math.round(d1 * this.m_font.getSize()));
		}
		FontMetrics localFontMetrics = Renderer.DEFAULT_GRAPHICS.getFontMetrics(this.m_font);
		double d2 = localFontMetrics.getHeight() + d1 * 2.0D * this.m_vertBorder;
		double d3 = localFontMetrics.stringWidth(str) + d1 * 2.0D * this.m_horizBorder;
		getAlignedPoint(this.m_tmpPoint, paramVisualItem, d3, d2, this.m_xAlign, this.m_yAlign);
		this.m_textBox.setFrame(this.m_tmpPoint.getX(), this.m_tmpPoint.getY(), d3, d2);
		return this.m_textBox;
	}

	protected static void getAlignedPoint(Point2D paramPoint2D, VisualItem paramVisualItem, double paramDouble1, double paramDouble2, int paramInt1, int paramInt2) {
		double d1 = paramVisualItem.getX();
		double d2 = paramVisualItem.getY();
		if (paramInt1 == 2) {
			d1 -= paramDouble1 / 2.0D;
		} else if (paramInt1 == 1) {
			d1 -= paramDouble1;
		}
		if (paramInt2 == 2) {
			d2 -= paramDouble2 / 2.0D;
		} else if (paramInt2 == 1) {
			d2 -= paramDouble2;
		}
		paramPoint2D.setLocation(d1, d2);
	}

	public void render(Graphics2D paramGraphics2D, VisualItem paramVisualItem) {
		Shape localShape = getShape(paramVisualItem);
		if (localShape != null) {
			super.drawShape(paramGraphics2D, paramVisualItem, localShape);
			String str = getText(paramVisualItem);
			if (str != null) {
				Rectangle2D localRectangle2D = localShape.getBounds2D();
				paramGraphics2D.setPaint(paramVisualItem.getColor());
				paramGraphics2D.setFont(this.m_font);
				FontMetrics localFontMetrics = paramGraphics2D.getFontMetrics();
				double d1 = paramVisualItem.getSize();
				double d2 = localRectangle2D.getX() + d1 * this.m_horizBorder;
				double d3 = localRectangle2D.getY() + d1 * this.m_vertBorder;
				paramGraphics2D.drawString(str, (float) d2, (float) d3 + localFontMetrics.getAscent());
				if (isHyperlink(paramVisualItem)) {
					int i = (int) Math.round(d2);
					int j = (int) Math.round(d3);
					paramGraphics2D.drawLine(i, j, i + localFontMetrics.stringWidth(str), j + localFontMetrics.getHeight() - 1);
				}
			}
		}
	}

	public int getHorizontalAlignment() {
		return this.m_xAlign;
	}

	public int getVerticalAlignment() {
		return this.m_yAlign;
	}

	public void setHorizontalAlignment(int paramInt) {
		this.m_xAlign = paramInt;
	}

	public void setVerticalAlignment(int paramInt) {
		this.m_yAlign = paramInt;
	}

	public int getHorizontalPadding() {
		return this.m_horizBorder;
	}

	public void setHorizontalPadding(int paramInt) {
		this.m_horizBorder = paramInt;
	}

	public int getVerticalPadding() {
		return this.m_vertBorder;
	}

	public void setVerticalPadding(int paramInt) {
		this.m_vertBorder = paramInt;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/TextItemRenderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */