package prefuse.demos.projektlp;

import java.util.Iterator;

import prefuse.visual.NodeItem;

@SuppressWarnings("unchecked")
public abstract class CalculateGraphs 
{
	Iterator nodeItems;
	NodeItem aNodeItem = null;
	NodeItem node = null;
	
	public CalculateGraphs(Iterator nodeItems)
	{
		this.nodeItems = nodeItems;
	}
	
	/**
	 * Adds a node to the barGraph zones to increase its height
	 * @param i
	 */
	abstract void addNode(int i, String name);
	
	/**
	 * removes a node from the barGraph zones to decrease its height
	 * @param i
	 */
	abstract void removeNode(int i);
	
	/*void addWavelengthNode(int i, String name)
	{
		//add node
		aNodeItem = (NodeItem) nodeItems.next();
		//wvList.get(i).addNode(aNodeItem);
		MainDisplay.zoneManager.addItemToZoneAndCatch(aNodeItem, name);
	}
	
	void removeWavelengthNode(int i)
	{
		//remove node
		//node = wvList.get(i).removeNode();
		MainDisplay.zoneManager.removeItemFromZone(node);
		//MainDisplay.zoneManager.add
	}*/
	
	void calculateGraph(int i, int numNode, int barGraphIndicator, String name)
	{
		if(barGraphIndicator > numNode)
		{
			for(int j = 0; j < (barGraphIndicator - numNode); j ++)
			{
				removeNode(i);
			}
		}
		else if(barGraphIndicator < numNode)
		{
			for(int j = 0; j < (numNode - barGraphIndicator); j ++)
			{
				addNode(i, name);
			}
		}
	}
	
	/*void calculateWavelengthGraph(int i, int numNode, int barGraphIndicator, String name)
	{
		//System.out.println(i+" "+numNode+" "+barGraphIndicator+" "+name);
		if(barGraphIndicator > numNode)
		{
			for(int j = 0; j < (barGraphIndicator - numNode); j ++)
			{
				//System.out.println(j);
				removeWavelengthNode(i);
			}
		}
		else if(barGraphIndicator < numNode)
		{
			for(int j = 0; j < (numNode - barGraphIndicator); j ++)
			{
				//System.out.println(j);
				addWavelengthNode(i, name);
			}
		}
	}*/
}
