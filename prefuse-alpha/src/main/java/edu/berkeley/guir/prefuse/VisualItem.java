package edu.berkeley.guir.prefuse;

import edu.berkeley.guir.prefuse.graph.Entity;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.util.FontLib;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

public abstract class VisualItem
		implements Entity {
	protected ItemRegistry m_registry;
	protected String m_itemClass;
	protected Entity m_entity;
	protected int m_dirty;
	protected boolean m_visible;
	protected boolean m_newlyVisible;
	protected Map m_attrs = new HashMap(5, 0.9F);
	protected double m_doi;
	protected Point2D m_location = new Point2D.Float(0F, 0F);
	protected Point2D m_startLocation = new Point2D.Float(0F, 0F);
	protected Point2D m_endLocation = new Point2D.Float(0F, 0F);
	protected Paint m_color;
	protected Paint m_startColor;
	protected Paint m_endColor;
	protected Paint m_fillColor;
	protected Paint m_startFillColor;
	protected Paint m_endFillColor;
	protected double m_size;
	protected double m_startSize;
	protected double m_endSize;
	protected Font m_startFont;
	protected Font m_font;
	protected Font m_endFont;
	protected boolean m_fixed = false;
	protected boolean m_highlight = false;

	public String toString() {
		return "VisualItem{" + this.m_entity + "}";
	}

	public void init(ItemRegistry paramItemRegistry, String paramString, Entity paramEntity) {
		this.m_itemClass = paramString;
		this.m_registry = paramItemRegistry;
		this.m_entity = paramEntity;
		this.m_dirty = 0;
		this.m_visible = false;
		this.m_newlyVisible = false;
		this.m_doi = -2.147483648E9D;
		initAttributes();
	}

	protected void initAttributes() {
		this.m_attrs.clear();
		this.m_location.setLocation(0F, 0F);
		this.m_startLocation.setLocation(0F, 0F);
		this.m_endLocation.setLocation(0F, 0F);
		this.m_color = Color.BLACK;
		this.m_startColor = Color.BLACK;
		this.m_endColor = Color.BLACK;
		this.m_fillColor = Color.LIGHT_GRAY;
		this.m_startFillColor = Color.LIGHT_GRAY;
		this.m_endFillColor = Color.LIGHT_GRAY;
		this.m_size = 1.0D;
		this.m_startSize = 1.0D;
		this.m_endSize = 1.0D;
		this.m_startFont = FontLib.getFont("SansSerif", 0, 10);
		this.m_font = this.m_startFont;
		this.m_endFont = this.m_startFont;
	}

	public void clear() {
		this.m_registry = null;
		this.m_entity = null;
		initAttributes();
	}

	public ItemRegistry getItemRegistry() {
		return this.m_registry;
	}

	public String getItemClass() {
		return this.m_itemClass;
	}

	public Entity getEntity() {
		return this.m_entity;
	}

	public String getAttribute(String paramString) {
		if (this.m_entity == null) {
			throw new IllegalStateException("This item has no assigned entity.");
		}
		return this.m_entity.getAttribute(paramString);
	}

	public void setAttribute(String paramString1, String paramString2) {
		if (this.m_entity == null) {
			throw new IllegalStateException("This item has no assigned entity.");
		}
		this.m_entity.setAttribute(paramString1, paramString2);
	}

	public Map getAttributes() {
		return this.m_entity.getAttributes();
	}

	public void setAttributes(Map paramMap) {
		this.m_entity.setAttributes(paramMap);
	}

	public void clearAttributes() {
		this.m_entity.clearAttributes();
	}

	public Object getVizAttribute(String paramString) {
		return this.m_attrs.get(paramString);
	}

	public void setVizAttribute(String paramString, Object paramObject) {
		this.m_attrs.put(paramString, paramObject);
	}

	public void removeVizAttribute(String paramString) {
		this.m_attrs.remove(paramString);
	}

	public void updateVizAttribute(String paramString1, String paramString2, String paramString3, Object paramObject) {
		Object localObject = getVizAttribute(paramString1);
		setVizAttribute(paramString2, localObject);
		setVizAttribute(paramString3, paramObject);
	}

	public void touch() {
		this.m_dirty = 0;
	}

	public int getDirty() {
		return this.m_dirty;
	}

	public void setDirty(int paramInt) {
		this.m_dirty = paramInt;
	}

	public boolean isNewlyVisible() {
		return this.m_newlyVisible;
	}

	public boolean isVisible() {
		return this.m_visible;
	}

	public void setVisible(boolean paramBoolean) {
		this.m_newlyVisible = ((!this.m_visible) && (paramBoolean));
		this.m_visible = paramBoolean;
	}

	public boolean isFocus() {
		FocusManager localFocusManager = this.m_registry.getFocusManager();
		return localFocusManager.isFocus(this.m_entity);
	}

	public Renderer getRenderer() {
		try {
			return this.m_registry.getRendererFactory().getRenderer(this);
		} catch (Exception localException) {
			System.out.println("processing reclaimed item!!! -- " + getClass().getName());
		}
		return null;
	}

	public boolean locatePoint(Point2D paramPoint2D) {
		return getRenderer().locatePoint(paramPoint2D, this);
	}

	public Rectangle2D getBounds() {
		return getRenderer().getBoundsRef(this);
	}

	public boolean isFixed() {
		return this.m_fixed;
	}

	public void setFixed(boolean paramBoolean) {
		this.m_fixed = paramBoolean;
	}

	public boolean isHighlighted() {
		return this.m_highlight;
	}

	public void setHighlighted(boolean paramBoolean) {
		this.m_highlight = paramBoolean;
	}

	public double getDOI() {
		return this.m_doi;
	}

	public void setDOI(double paramDouble) {
		this.m_doi = paramDouble;
	}

	public Point2D getStartLocation() {
		return this.m_startLocation;
	}

	public Point2D getEndLocation() {
		return this.m_endLocation;
	}

	public Point2D getLocation() {
		return this.m_location;
	}

	public void setLocation(Point2D paramPoint2D) {
		this.m_location.setLocation(paramPoint2D);
	}

	public void setLocation(double paramDouble1, double paramDouble2) {
		this.m_location.setLocation(paramDouble1, paramDouble2);
	}

	public void setStartLocation(Point2D paramPoint2D) {
		this.m_startLocation.setLocation(paramPoint2D);
	}

	public void setStartLocation(double paramDouble1, double paramDouble2) {
		this.m_startLocation.setLocation(paramDouble1, paramDouble2);
	}

	public void setEndLocation(Point2D paramPoint2D) {
		this.m_endLocation.setLocation(paramPoint2D);
	}

	public void setEndLocation(double paramDouble1, double paramDouble2) {
		this.m_endLocation.setLocation(paramDouble1, paramDouble2);
	}

	public void updateLocation(Point2D paramPoint2D) {
		this.m_startLocation.setLocation(this.m_location);
		this.m_endLocation.setLocation(paramPoint2D);
	}

	public void updateLocation(double paramDouble1, double paramDouble2) {
		this.m_startLocation.setLocation(this.m_location);
		this.m_endLocation.setLocation(paramDouble1, paramDouble2);
	}

	public double getX() {
		return this.m_location.getX();
	}

	public double getY() {
		return this.m_location.getY();
	}

	public Paint getStartColor() {
		return this.m_startColor;
	}

	public Paint getEndColor() {
		return this.m_endColor;
	}

	public Paint getColor() {
		return this.m_color;
	}

	public void setColor(Paint paramPaint) {
		this.m_color = paramPaint;
	}

	public void updateColor(Paint paramPaint) {
		this.m_startColor = this.m_color;
		this.m_endColor = paramPaint;
	}

	public Paint getStartFillColor() {
		return this.m_startFillColor;
	}

	public Paint getEndFillColor() {
		return this.m_endFillColor;
	}

	public Paint getFillColor() {
		return this.m_fillColor;
	}

	public void setFillColor(Paint paramPaint) {
		this.m_fillColor = paramPaint;
	}

	public void updateFillColor(Paint paramPaint) {
		this.m_startFillColor = this.m_fillColor;
		this.m_endFillColor = paramPaint;
	}

	public double getStartSize() {
		return this.m_startSize;
	}

	public double getEndSize() {
		return this.m_endSize;
	}

	public double getSize() {
		return this.m_size;
	}

	public void setSize(double paramDouble) {
		this.m_size = paramDouble;
	}

	public void setStartSize(double paramDouble) {
		this.m_startSize = paramDouble;
	}

	public void setEndSize(double paramDouble) {
		this.m_endSize = paramDouble;
	}

	public void updateSize(double paramDouble) {
		this.m_startSize = this.m_size;
		this.m_endSize = paramDouble;
	}

	public Font getStartFont() {
		return this.m_startFont;
	}

	public void setStartFont(Font paramFont) {
		this.m_startFont = paramFont;
	}

	public Font getFont() {
		return this.m_font;
	}

	public void setFont(Font paramFont) {
		this.m_font = paramFont;
	}

	public Font getEndFont() {
		return this.m_endFont;
	}

	public void setEndFont(Font paramFont) {
		this.m_endFont = paramFont;
	}

	public void updateFont(Font paramFont) {
		this.m_startFont = this.m_font;
		this.m_endFont = paramFont;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/VisualItem.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */