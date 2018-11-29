package prefuse.demos.projektlp;

import java.util.ArrayList;
import java.util.Iterator;

import prefuse.util.ColorLib;
import prefuse.visual.NodeItem;

public class WavelengthColorVisualizer implements Runnable
{
	int i;
	String s;
	@SuppressWarnings("unchecked")
	Iterator nodeItems;
	ArrayList<NodeItem> niList = null;
	
	@SuppressWarnings("unchecked")
	public WavelengthColorVisualizer(int i, String s, Iterator nodeItems, ArrayList<NodeItem> niList) 
	{
		this.i = i;
		this.s = s;
		this.nodeItems = nodeItems;
		this.niList = niList;
	}

	public void run() 
	{
		if(s.equals("max"))
		{
			niList.get(i).setFillColor(ColorLib.hex("#FF3333"));
		}
		else if(s.equals("min"))
		{
			niList.get(i).setFillColor(ColorLib.gray(255));
		}
	}
}
