package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.util.FontLib;
import edu.berkeley.guir.prefuse.util.StringAbbreviator;

import java.awt.*;
import java.awt.geom.*;

public class TextImageItemRenderer
		extends ShapeRenderer {
	public static final int ALIGNMENT_LEFT = 0;
	public static final int ALIGNMENT_RIGHT = 1;
	public static final int ALIGNMENT_CENTER = 2;
	public static final int ALIGNMENT_BOTTOM = 1;
	public static final int ALIGNMENT_TOP = 0;
	protected ImageFactory m_images = new ImageFactory();
	protected String m_labelName = "label";
	protected String m_imageName = "image";
	protected int m_xAlign = 2;
	protected int m_yAlign = 2;
	protected int m_horizBorder = 3;
	protected int m_vertBorder = 0;
	protected int m_imageMargin = 4;
	protected int m_maxTextWidth = -1;
	protected int m_abbrevType = 3;
	protected StringAbbreviator m_abbrev = StringAbbreviator.getInstance();
	protected double m_imageSize = 1.0D;
	protected Font m_font = new Font("SansSerif", 0, 10);
	protected RectangularShape m_imageBox = new Rectangle2D.Float();
	protected Point2D m_tmpPoint = new Point2D.Double();
	protected AffineTransform m_transform = new AffineTransform();

	public void setRoundedCorner(int paramInt1, int paramInt2) {
		if (((paramInt1 == 0) || (paramInt2 == 0)) && (!(this.m_imageBox instanceof Rectangle2D))) {
			this.m_imageBox = new Rectangle2D.Float();
		} else {
			if (!(this.m_imageBox instanceof RoundRectangle2D)) {
				this.m_imageBox = new RoundRectangle2D.Float();
			}
			((RoundRectangle2D) this.m_imageBox).setRoundRect(0.0D, 0.0D, 10.0D, 10.0D, paramInt1, paramInt2);
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

	public void setImageSize(double paramDouble) {
		this.m_imageSize = paramDouble;
	}

	public void setMaxImageDimensions(int paramInt1, int paramInt2) {
		this.m_images.setMaxImageDimensions(paramInt1, paramInt2);
	}

	public String getImageAttributeName() {
		return this.m_imageName;
	}

	public void setImageAttributeName(String paramString) {
		this.m_imageName = paramString;
	}

	protected String getImageLocation(VisualItem paramVisualItem) {
		return paramVisualItem.getAttribute(this.m_imageName);
	}

	protected Image getImage(VisualItem paramVisualItem) {
		String str = getImageLocation(paramVisualItem);
		return str == null ? null : this.m_images.getImage(str);
	}

	protected Shape getRawShape(VisualItem paramVisualItem) {
		double d1 = paramVisualItem.getSize();
		Image localImage = getImage(paramVisualItem);
		double d2 = d1 * this.m_imageSize;
		double d3 = localImage == null ? 0.0D : d2 * localImage.getHeight(null);
		double d4 = localImage == null ? 0.0D : d2 * localImage.getWidth(null);
		this.m_font = paramVisualItem.getFont();
		if (d1 != 1.0D) {
			this.m_font = FontLib.getFont(this.m_font.getName(), this.m_font.getStyle(), (int) Math.round(d1 * this.m_font.getSize()));
		}
		String str = getText(paramVisualItem);
		if (str == null) {
			str = "";
		}
		FontMetrics localFontMetrics = Renderer.DEFAULT_GRAPHICS.getFontMetrics(this.m_font);
		int i = localFontMetrics.getHeight();
		int j = localFontMetrics.stringWidth(str);
		double d5 = j + d4 + d1 * (2 * this.m_horizBorder + ((j > 0) && (d4 > 0.0D) ? this.m_imageMargin : 0));
		double d6 = Math.max(i, d3) + d1 * 2.0D * this.m_vertBorder;
		getAlignedPoint(this.m_tmpPoint, paramVisualItem, d5, d6, this.m_xAlign, this.m_yAlign);
		this.m_imageBox.setFrame(this.m_tmpPoint.getX(), this.m_tmpPoint.getY(), d5, d6);
		return this.m_imageBox;
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
		if (localShape == null) {
			return;
		}
		Paint localPaint1 = paramVisualItem.getColor();
		Paint localPaint2 = paramVisualItem.getFillColor();
		int i = getRenderType(paramVisualItem);
		if ((i == 2) || (i == 3)) {
			paramGraphics2D.setPaint(localPaint2);
			paramGraphics2D.fill(localShape);
		}
		String str = getText(paramVisualItem);
		Image localImage = getImage(paramVisualItem);
		if ((str == null) && (localImage == null)) {
			return;
		}
		Rectangle2D localRectangle2D = localShape.getBounds2D();
		double d1 = paramVisualItem.getSize();
		double d2 = localRectangle2D.getMinX() + d1 * this.m_horizBorder;
		Object localObject;
		double d3;
		if (localImage != null) {
			localObject = paramGraphics2D.getComposite();
			if ((localPaint2 instanceof Color)) {
				int j = ((Color) localPaint2).getAlpha();
				if (j < 255) {
					AlphaComposite localAlphaComposite = AlphaComposite.getInstance(10, j / 255.0F);
					paramGraphics2D.setComposite(localAlphaComposite);
				}
			}
			d3 = this.m_imageSize * d1;
			double d4 = d3 * localImage.getWidth(null);
			double d5 = d3 * localImage.getHeight(null);
			double d6 = localRectangle2D.getMinY() + (localRectangle2D.getHeight() - d5) / 2.0D;
			this.m_transform.setTransform(d3, 0.0D, 0.0D, d3, d2, d6);
			paramGraphics2D.drawImage(localImage, this.m_transform, null);
			d2 += d4 + (str != null ? d1 * this.m_imageMargin : 0.0D);
			paramGraphics2D.setComposite((Composite) localObject);
		}
		if (str != null) {
			paramGraphics2D.setPaint(localPaint1);
			paramGraphics2D.setFont(this.m_font);
			localObject = Renderer.DEFAULT_GRAPHICS.getFontMetrics(this.m_font);
			d3 = localRectangle2D.getY() + (localRectangle2D.getHeight() - ((FontMetrics) localObject).getHeight()) / 2.0D + ((FontMetrics) localObject).getAscent();
			paramGraphics2D.drawString(str, (float) d2, (float) d3);
		}
		if ((i == 1) || (i == 3)) {
			localObject = paramGraphics2D.getStroke();
			BasicStroke localBasicStroke = getStroke(paramVisualItem);
			if (localBasicStroke != null) {
				paramGraphics2D.setStroke(localBasicStroke);
			}
			paramGraphics2D.setPaint(localPaint1);
			paramGraphics2D.draw(localShape);
			paramGraphics2D.setStroke((Stroke) localObject);
		}
	}

	public ImageFactory getImageFactory() {
		return this.m_images;
	}

	public void setImageFactory(ImageFactory paramImageFactory) {
		this.m_images = paramImageFactory;
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

	public int getImageSpacing() {
		return this.m_imageMargin;
	}

	public void setImageSpacing(int paramInt) {
		this.m_imageMargin = paramInt;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/TextImageItemRenderer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */