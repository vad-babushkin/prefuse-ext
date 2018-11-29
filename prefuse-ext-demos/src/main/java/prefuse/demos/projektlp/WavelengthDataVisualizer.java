package prefuse.demos.projektlp;

import java.util.ArrayList;
import java.util.Iterator;

import prefuse.data.Graph;
import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;


public class WavelengthDataVisualizer implements Runnable
{
	Graph edgeGraph;
	Double data;
	ArrayList<WavelengthData> wdList = null;
	ArrayList<NodeItem> niList = null;
	MainDisplay md = null;
	@SuppressWarnings("unchecked")
	Iterator nodeItems = null;
	
	public WavelengthDataVisualizer(Graph edgeGraph, MainDisplay mainDisplay) 
	{
		this.edgeGraph = edgeGraph;
		data = 0.0;
		this.md = mainDisplay;
		nodeItems = md.getVis().items("edgeGraph.nodes");
		this.niList = new ArrayList<NodeItem>();
		for(int i = 0; i < MainDisplay.wavelengthList.size(); i ++)
		{
			niList.add((NodeItem) nodeItems.next());
			niList.get(i).setTextColor(ColorLib.gray(0));
		}
		
		
	}

	public void run() 
	{
		int i;
			
		//wavelengthList represents each edge in graph, it consists of
		//source & destination along with a arraylist of wavelengthdata
		//with consists of each data field and its data e.g.
		//bw-usage-ratio : 0.002
		for(i = 0; i < MainDisplay.wavelengthList.size(); i ++)
		{			
			//gets the list of wavelengthdata for each edge
			wdList = MainDisplay.wavelengthList.get(i).wdList;
			data = wdList.get(0).getData();
			
			if(data == 2.0)
			{
				//System.out.println(i+" "+data);
				new WavelengthColorVisualizer(i, "max", nodeItems, niList).run();
			}
			else
			{
				new WavelengthColorVisualizer(i, "min", nodeItems, niList).run();
			}
			
			try
			{
				synchronized(this)
				{
					edgeGraph.getNode(i).set("name", data.toString());
				}
			}
			catch (Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		md.refresh();
	}
}
