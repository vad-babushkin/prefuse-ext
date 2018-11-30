package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;

public abstract interface RendererFactory {
	public abstract Renderer getRenderer(VisualItem paramVisualItem);
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/RendererFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */