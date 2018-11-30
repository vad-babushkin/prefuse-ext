package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.AggregateItem;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;

public class DefaultRendererFactory
		implements RendererFactory {
	private Renderer m_nodeRenderer;
	private Renderer m_edgeRenderer;
	private Renderer m_aggrRenderer;

	public DefaultRendererFactory() {
		this(new DefaultNodeRenderer(), new DefaultEdgeRenderer(), null);
	}

	public DefaultRendererFactory(Renderer paramRenderer) {
		this(paramRenderer, null, null);
	}

	public DefaultRendererFactory(Renderer paramRenderer1, Renderer paramRenderer2) {
		this(paramRenderer1, paramRenderer2, null);
	}

	public DefaultRendererFactory(Renderer paramRenderer1, Renderer paramRenderer2, Renderer paramRenderer3) {
		this.m_nodeRenderer = paramRenderer1;
		this.m_edgeRenderer = paramRenderer2;
		this.m_aggrRenderer = paramRenderer3;
	}

	public Renderer getRenderer(VisualItem paramVisualItem) {
		if ((paramVisualItem instanceof AggregateItem)) {
			return this.m_aggrRenderer;
		}
		if ((paramVisualItem instanceof NodeItem)) {
			return this.m_nodeRenderer;
		}
		if ((paramVisualItem instanceof EdgeItem)) {
			return this.m_edgeRenderer;
		}
		return null;
	}

	public Renderer getAggregateRenderer() {
		return this.m_aggrRenderer;
	}

	public Renderer getEdgeRenderer() {
		return this.m_edgeRenderer;
	}

	public Renderer getNodeRenderer() {
		return this.m_nodeRenderer;
	}

	public void setAggregateRenderer(Renderer paramRenderer) {
		this.m_aggrRenderer = paramRenderer;
	}

	public void setEdgeRenderer(Renderer paramRenderer) {
		this.m_edgeRenderer = paramRenderer;
	}

	public void setNodeRenderer(Renderer paramRenderer) {
		this.m_nodeRenderer = paramRenderer;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/DefaultRendererFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */