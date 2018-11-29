package prefuse.demos.projektlp;

import java.text.DecimalFormat;
import java.util.Iterator;
import prefuse.visual.NodeItem;

@SuppressWarnings("unchecked")
public class NodeChart implements Runnable
{
	NodeItem aNodeItem;
	Iterator nodeItems;
	double data;
	int i, j;
	String name;
	DecimalFormat twoDForm = null;
	MainDisplay md;
	
	public NodeChart(Iterator nodeItems, double data, int i, int j, String name, MainDisplay md) 
	{
		this.nodeItems = nodeItems;
		this.data = data;
		this.i = i;
		this.j = j;
		this.name = name;
		this.twoDForm = new DecimalFormat("#.#");
		this.md = md;
	}
	
	public void addNode() 
	{
		MainDisplay.nodeList.get(this.i).ndList.get(this.j).incrementIndicator();


		this.aNodeItem = (NodeItem) this.nodeItems.next();
		MainDisplay.nodeList.get(this.i).ndList.get(this.j).addNode(this.aNodeItem);
		MainDisplay.zoneManager.addItemToZoneAndCatch(this.aNodeItem, this.name);

		
		
		//add node
		/*try
		{*/

			
		/*}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(data+" "+i+" "+j+" "+name+"    add node: "+e.getMessage());
		}*/
	}

	public void removeNode() 
	{
		//remove node
		NodeItem temp = null;
		int indicator;
		
		indicator = MainDisplay.nodeList.get(this.i).ndList.get(this.j).decrementIndicator();
		temp = MainDisplay.nodeList.get(this.i).ndList.get(this.j).removeNode(indicator);
		MainDisplay.zoneManager.removeItemFromZone(temp);

		/*
		try
		{*/
			
			
/*		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println(data+" "+i+" "+j+" "+name+"   remove node: " + e.getMessage());
		}*/
	}
	
	void calculateGraph(int numNode, int barGraphIndicator)
	{
		if(barGraphIndicator > numNode)
		{
			for(int j = 0; j < (barGraphIndicator - numNode); j ++)
			{
				removeNode();
			}
		}
		else if(barGraphIndicator < numNode)
		{
			for(int j = 0; j < (numNode - barGraphIndicator); j ++)
			{
				addNode();
			}
		}
		else if(barGraphIndicator == numNode)
		{
			// do nothing
		}
	}
	
	private int calculateNumNodes()
	{
		int numOfNodes = (int)(Double.valueOf(twoDForm.format(data))*10);
		if(data > 0.01 && data < 0.1)
		{
			numOfNodes = 1;
		}
		else if(numOfNodes > 10)
		{
			numOfNodes = 10;
		}
		return numOfNodes;
	}
	
	public void run() 
	{
		
		String tempName = this.name;
		this.name = "Node "+(this.i+1)+"_"+this.name;
		int barGraphIndicator, numOfNodes = 0;

		synchronized(MainDisplay.nodeList)
		{
			barGraphIndicator = MainDisplay.nodeList.get(this.i).ndList.get(this.j).getBarGraphIndicator();
		}

		if(tempName.equals("total-requested-bw-per-node"))
		{
			numOfNodes = (int)(this.data/10/4);
			if(numOfNodes > 10)
			{
				numOfNodes = 10;
			}
		}
		else if(tempName.equals("node-oeo-port-util") && this.data > 0.9 && md.getColorChanged(i) == false)
		{
			md.changeColor(this.name, i);
			numOfNodes = calculateNumNodes();
		}
		else if(tempName.equals("node-oeo-port-util") && this.data < 0.9 && md.getColorChanged(i) == true)
		{
			md.defaultColor(this.name, i);
			numOfNodes = calculateNumNodes();
		}
		else
		{
			numOfNodes = calculateNumNodes();
		}
		
		synchronized(MainDisplay.nodeList)
		{
			calculateGraph(numOfNodes, barGraphIndicator);
		}
	}
}
