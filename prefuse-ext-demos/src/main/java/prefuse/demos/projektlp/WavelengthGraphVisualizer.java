package prefuse.demos.projektlp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WavelengthGraphVisualizer implements Runnable
{
	@SuppressWarnings("unchecked")
	Iterator nodeItems = null;
	ArrayList<WavelengthData> wdList = null;
	WavelengthChart[] a = null;
	ExecutorService threadExecutor = null;
	
	@SuppressWarnings("unchecked")
	public WavelengthGraphVisualizer(Iterator nodeItems)
	{
		this.nodeItems = nodeItems;
	}

	public void run() 
	{
		for(int i = 0; i < MainDisplay.wavelengthList.size(); i ++)
		{
			this.wdList = MainDisplay.wavelengthList.get(i).wdList;
			a = new WavelengthChart[wdList.size()];
			for(int j = 0; j < a.length; j++)
			{
				a[j] = new WavelengthChart(this.nodeItems, wdList.get(j).getName(),
						wdList.get(j).getData(), i, j);
			}
			ExecutorService threadExecutor = Executors.newFixedThreadPool(a.length);
			for(int t = 0; t < a.length; t ++)
			{
				threadExecutor.execute(a[t]);
			}
	        threadExecutor.shutdown(); // shutdown worker threads
		}
		
		for(int i = 0; i < MainDisplay.wavelengthList.size(); i ++)
		{
			for(int j = 0; j < this.wdList.size(); j ++)
			{
				MainDisplay.zoneManager.recalculateFlexibility("Edge "+i+"_"+this.wdList.get(j).getName());
			}
		}
	}
}
