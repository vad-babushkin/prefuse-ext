package prefuse.demos.projektlp;

import java.util.ArrayList;
import prefuse.visual.NodeItem;
import java.util.concurrent.atomic.AtomicInteger;

public class NodeData 
{
	double data;
	String name; 
	ArrayList<NodeItem> nodesAry;
	boolean max;
	private AtomicInteger barGraphIndicator;
	
	public NodeData(String name)
	{
		this.data = 0.0;
		this.name = name;
		this.nodesAry = new ArrayList<NodeItem>();
		this.max = false;
		this.barGraphIndicator = new AtomicInteger(0);
	}
	
	public void addNode(NodeItem a)
	{
		if(nodesAry.size() == 10)
		{
			//do nothing
		}
		else
		{
			this.nodesAry.add(a);
		}
	}
	
	public String toString() {
		return "NodeData [barGraphIndicator=" + barGraphIndicator + ", data="
				+ data + ", name=" + name + "]";
	}

	public NodeItem removeNode(int a)
	{
		NodeItem ni = null;
		try
		{
			ni = this.nodesAry.remove(a);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage()+ " a: "+a +" nodesAry size: "+nodesAry.size());
			System.out.println(toString());
		}
		return ni;
	}
	
	public void incrementIndicator()
	{
		if(this.barGraphIndicator.get() >= 10)
		{
			System.out.println("barGraphIndicator > 10?!?!");
			this.barGraphIndicator.set(10);
		}
		else
		{
			this.barGraphIndicator.incrementAndGet();
		}
	}
	
	public int decrementIndicator()
	{
		if(this.barGraphIndicator.get() < 0)
		{
			System.out.println("barGraphIndicator < 0?!?!");
			this.barGraphIndicator.set(0);
		}
		else
		{
			this.barGraphIndicator.decrementAndGet();
		}
		return this.barGraphIndicator.get();
	}
	
	public double getData() {
		return data;
	}

	public void setData(double data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<NodeItem> getNodesAry() {
		return nodesAry;
	}

	public void setNodesAry(ArrayList<NodeItem> nodesAry) {
		this.nodesAry = nodesAry;
	}

	public boolean isMax() {
		return max;
	}

	public void setMax(boolean max) {
		this.max = max;
	}

	public int getBarGraphIndicator() {
		return barGraphIndicator.get();
	}
}
