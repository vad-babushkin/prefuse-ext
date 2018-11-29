package prefuse.demos.projektlp;

import java.awt.Color;
import java.util.Iterator;

import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;
import profusians.zonemanager.zone.colors.ZoneColors;

public class WavelengthChart implements Runnable
{
	NodeItem aNodeItem;
	@SuppressWarnings("unchecked")
	Iterator nodeItems;
	double data;
	int i, j;
	String name;
	
	@SuppressWarnings("unchecked")
	public WavelengthChart(Iterator nodeItems, String name, double data, int i, int j)
	{
		this.nodeItems = nodeItems;
		this.name = name;
		this.data = data;
		this.i = i;
		this.j = j;
	}
	
	void addNode(String n) 
	{
		//add node
		synchronized(this)
		{
			this.aNodeItem = (NodeItem) this.nodeItems.next();
			MainDisplay.wavelengthList.get(this.i).wdList.get(this.j).addNode(this.aNodeItem);
			MainDisplay.zoneManager.addItemToZoneAndCatch(this.aNodeItem, n);
			MainDisplay.wavelengthList.get(this.i).wdList.get(this.j).incrementIndicator();
		}
	}

	void removeNode() 
	{
		//remove node
		NodeItem temp = null;
		int x =  MainDisplay.wavelengthList.get(this.i).wdList.get(this.j).getBarGraphIndicator()-1;
		try
		{
			temp = MainDisplay.wavelengthList.get(this.i).wdList.get(this.j).removeNode(x);
			MainDisplay.wavelengthList.get(this.i).wdList.get(this.j).decrementIndicator();
		}
		catch (Exception e)
		{
			System.out.println("ASKHAKLSJ EXCEPTION");
		}
		MainDisplay.zoneManager.removeItemFromZone(temp);
	}
	
	void calculateGraph(int numNode, int barGraphIndicator, String n)
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
				addNode(n);
			}
		}
	}
	
	public void run() 
	{
		String n = "Edge "+i+"_"+name;
		int barGraphIndicator = MainDisplay.wavelengthList.get(i).wdList.get(j).getBarGraphIndicator();
		
		if(data > 0 && data < 0.2)
		{
			//System.out.println("At data > 0 && data < 0.1");
			calculateGraph(1, barGraphIndicator, n);
		}
		else if(data >= 0.2 && data < 0.4)
		{
			//System.out.println("At data >= 0.1 && data < 0.2");
			calculateGraph(2, barGraphIndicator, n);
		}
		else if(data >= 0.4 && data < 0.6)
		{
			//System.out.println("At data >= 0.2 && data < 0.3");
			calculateGraph(3, barGraphIndicator, n);	
		}
		else if(data >= 0.6 && data < 0.8)
		{
			//System.out.println("At data >= 0.3 && data < 0.4");
			calculateGraph(4, barGraphIndicator, n);	
		}
		else if(data >= 0.8 && data < 1.0)
		{
			calculateGraph(5, barGraphIndicator, n);
		}
		else if(data == 1.0)
		{
			if(!MainDisplay.wavelengthList.get(i).wdList.get(j).isReachedMax())
			{
				MainDisplay.wavelengthList.get(i).wdList.get(j).setReachedMax(true);
				MainDisplay.zoneManager.getZone(n).setColors(new ZoneColors(ColorLib.color(Color.BLACK), ColorLib.color(Color.BLACK)));
				//MainDisplay.changeColor();
			}
		}
	}
}
