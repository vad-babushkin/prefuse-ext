/**
 * BarGraphVisualizer.java
 * Han Dong
 * July 16, 2009
 * 
 * Thread that calculates the size of the barGraph and how it should be displayed
 */
package prefuse.demos.projektlp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import profusians.zonemanager.ZoneManager;

@SuppressWarnings({ "unchecked", "unused"})
public class NodeGraphVisualizer implements Runnable
{
	MainDisplay zone = null;
	Iterator nodeItems = null;
	ArrayList<NodeData> ndList;
	NodeChart[][] a;
	ExecutorService threadExecutor;
	int nodeListSize;
	double network_blocking;
	ProcessData pd;
	Buttons b;
	int i, j, t;
	MainDisplay md;

	/**
	 * Constructor that initializes instance variables
	 * @param md 
	 * @param pd 
	 */
	public NodeGraphVisualizer(Iterator nodeItems, Buttons b, MainDisplay md)
	{
		//iterator
		this.nodeItems = nodeItems;
		nodeListSize = MainDisplay.nodeList.size();
		this.b = b;
		this.ndList = MainDisplay.nodeList.get(0).getNDList();
		a = new NodeChart[this.nodeListSize][this.ndList.size()];
		threadExecutor = Executors.newFixedThreadPool(this.nodeListSize*this.ndList.size());
		this.md = md;
	}
	
	public void setBlocking(double nb)
	{
		this.network_blocking = nb;
		setData();
	}
	
	private void setData()
	{	
		for(i = 0; i < nodeListSize; i ++)
		{
			this.ndList = MainDisplay.nodeList.get(i).getNDList();
			for(j = 0; j < this.ndList.size(); j ++)
			{	
				try
				{
					a[i][j] = new NodeChart(this.nodeItems, ndList.get(j).getData(), i, j, 
							ndList.get(j).getName(), this.md);
				}
				catch (Exception e)
				{
					System.out.println("i: "+i+" j: "+j);
				}
			}
		}
	}
	
	public void run()
	{			
		for(i = 0; i < this.nodeListSize; i ++)
		{
			for(j = 0; j < this.ndList.size(); j ++)
			{
				threadExecutor.execute(a[i][j]);
			}
		}
	}
}
