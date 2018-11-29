/**
 * NodeColorVisualizer.java
 * Han Dong
 * July 16, 2009
 * 
 * Thread that computes and determines the color of the individual nodes
 */
package prefuse.demos.projektlp;

import java.util.ArrayList;
import java.util.Iterator;

import prefuse.Visualization;
import prefuse.util.ColorLib;
import prefuse.util.ui.JFastLabel;
import prefuse.visual.NodeItem;

@SuppressWarnings({ "unchecked", "unused"})
public class NodeColorVisualizer implements Runnable
{
	Visualization vis = null;
	JFastLabel stat = null;
	MainDisplay zone = null;
	ArrayList<Node> node_list = null;
	public static boolean interrupt;
	
	public NodeColorVisualizer(Visualization vis, MainDisplay zone, ArrayList<Node> node_list)
	{
		super();
		this.vis = vis;
		this.zone = zone;
		this.node_list = node_list;
	}
	
	/**
	 * Iterates over each node and sets its color, depending on its colorIndicator value
	 * which is oeo_traffic_bw_ratio + ooo_traffic_bw_ratio
	 */
	public void run()
	{
		double color = 0.0;

		//creates iterator
		Iterator nodeItems = vis.items("graph.nodes");
		
		NodeItem aNodeItem = null;
		for(int i = 0; i < 32; i ++)
		{
			//aNodeItem holds the node object
			aNodeItem = (NodeItem) nodeItems.next();

			//gets color indicator of a node
			color = node_list.get(i).getColor_indicator();

			//determines the color of the node based off the color indicator
			if(color >= 0 && color < 0.1)
			{
				aNodeItem.setFillColor(ColorLib.hex("#FF0000"));
			}
			else if(color >= 0.1 && color < 0.2)
			{
				aNodeItem.setFillColor(ColorLib.hex("#D80000"));
			}
			else if(color >= 0.2 && color < 0.3)
			{
				aNodeItem.setFillColor(ColorLib.hex("#C80000"));
			}
			else if(color >= 0.3 && color < 0.4)
			{
				aNodeItem.setFillColor(ColorLib.hex("#B80000"));
			}
			else if(color >= 0.4 && color < 0.5)
			{
				aNodeItem.setFillColor(ColorLib.hex("#A80000"));
			}
			else if(color >= 0.5 && color < 0.6)
			{
				aNodeItem.setFillColor(ColorLib.hex("#980000"));
			}
			else if(color >= 0.6 && color < 0.7)
			{
				aNodeItem.setFillColor(ColorLib.hex("#880000"));
			}
			else if(color >= 0.7 && color < 0.8)
			{
				aNodeItem.setFillColor(ColorLib.hex("#780000"));
			}
			else if(color >= 0.8 && color < 0.9)
			{
				aNodeItem.setFillColor(ColorLib.hex("#480000"));
			}
			else if(color >= 0.9 && color < 1)
			{
				aNodeItem.setFillColor(ColorLib.hex("#380000"));
			}
			else
			{
				aNodeItem.setFillColor(ColorLib.hex("#180000"));
			}
		}
		
	}	
}
