package prefuse.demos.tangwing;

import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.visual.EdgeItem;


public class MyForceDirectedLayout extends ForceDirectedLayout{

	public MyForceDirectedLayout(String graph) {
		super(graph);
		// TODO Auto-generated constructor stub
	}
	
	public MyForceDirectedLayout(String graph, boolean enforceBounds)
	{
		super(graph, enforceBounds);
	}

	@Override
	protected float getSpringLength(EdgeItem e)
	{
		float l = (e.canGet("length", float.class))?
				e.getFloat("length"):100f;
		//l = super.getSpringLength(e);
		//System.out.println(l);
		return l;
	}
}
