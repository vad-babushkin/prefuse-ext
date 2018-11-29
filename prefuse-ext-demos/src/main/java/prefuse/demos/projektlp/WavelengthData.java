package prefuse.demos.projektlp;

import java.util.ArrayList;

import prefuse.visual.NodeItem;

public class WavelengthData 
{
	String name;
	double data;
	ArrayList<NodeItem> nodesAry;
	boolean max;
	int barGraphIndicator;
	
	public WavelengthData(String name)
	{
		this.name = name;
		data = 0.0;
		nodesAry = new ArrayList<NodeItem>();
		max = false;
		barGraphIndicator = 0;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setData(double data)
	{
		this.data = data;
	}
	
	public double getData()
	{
		return this.data;
	}
	
	public int getBarGraphIndicator()
	{
		return this.barGraphIndicator;
	}
	
	public NodeItem removeNode(int a)
	{
		return this.nodesAry.remove(a);
	}
	
	public void incrementIndicator()
	{
		this.barGraphIndicator ++;
	}
	
	public void decrementIndicator()
	{
		this.barGraphIndicator --;
	}
	public void addNode(NodeItem a)
	{
		this.nodesAry.add(a);
	}
	
	public boolean isReachedMax() 
	{
		return max;
	}

	public void setReachedMax(boolean b) 
	{
		max = b;
	}	
}
