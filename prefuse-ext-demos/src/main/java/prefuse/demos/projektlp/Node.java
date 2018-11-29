/**
 * Node.java
 * Han Dong
 * July 16, 2009
 * 
 * Class that holds information of the node, acessors and getters to retrieve info
 */
package prefuse.demos.projektlp;

import java.util.ArrayList;

public class Node 
{
	double color_indicator;
	ArrayList<NodeData> ndList;
	
	public Node(ArrayList<String> nsList2) 
	{
		this.color_indicator = 0.0;
		this.ndList = new ArrayList<NodeData>();
		
		for(int i = 0; i < nsList2.size(); i ++)
		{
			ndList.add(new NodeData(nsList2.get(i)));
		}
	}
	
	public ArrayList<NodeData> getNDList()
	{
		return this.ndList;
	}
	
	public void setData(double data, String name)
	{
		for(int i = 0; i < this.ndList.size(); i ++)
		{
			if(this.ndList.get(i).getName().equals(name))
			{
				this.ndList.get(i).setData(data);
				i = this.ndList.size();
			}
		}
	}
	
	public void setColor_indicator(double color_indicator) 
	{
		this.color_indicator = color_indicator;
	}
	
	public double getColor_indicator()
	{
		return this.color_indicator;
	}
}

