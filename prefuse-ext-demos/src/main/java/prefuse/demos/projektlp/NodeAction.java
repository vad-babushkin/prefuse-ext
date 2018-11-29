package prefuse.demos.projektlp;

import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

public class NodeAction extends ControlAdapter
{
	DecimalFormat fmt;  
	String string;
	NodeLocationList nll;
	private NodeMiniWindow mw;
	private EdgeMiniWindow emw;
	private final ScheduledExecutorService scheduler =
		Executors.newSingleThreadScheduledExecutor();
	
	public NodeAction(NodeLocationList nlocateList)
	{
		nll = nlocateList;
		string = "";
		fmt = new DecimalFormat("0.000"); 
	}
	
	@Override
	public void itemClicked(VisualItem item, MouseEvent e)
	{
		int i = item.getInt("num")-1;
		System.out.println(item.getEndX() +" "+ item.getEndY());
		String name = item.getString("name");
		//System.out.println("name: "+name+" Type of Node: "+name.charAt(0) + " i: "+i);
		
		if(name.charAt(0) == 'E' || name.charAt(0) == 'N')
		{
			mw = new NodeMiniWindow(i, name.charAt(0), nll.getXCoor(i), nll.getYCoor(i));
			scheduler.scheduleWithFixedDelay(mw, 0, 1, TimeUnit.SECONDS);
		}
		else
		{
			emw = new EdgeMiniWindow(i, e.getXOnScreen(), e.getYOnScreen());
			scheduler.scheduleWithFixedDelay(emw, 0, 1, TimeUnit.SECONDS);	
		}
	}
	
	/*public void itemClicked(int i)
	{
		mw = new MiniWindow(i, "", nll.getXCoor(i), nll.getYCoor(i));
		scheduler.scheduleWithFixedDelay(mw, 0, 1, TimeUnit.SECONDS);
	}*/
	
	/*public void itemEntered(VisualItem item, MouseEvent e) 
	{
        Display d = (Display)e.getSource();
        d.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        string = fmt.format(MainDisplay.nodeList.get((item.getInt("num")-1)).getColor_indicator());
        d.setToolTipText("(ooo+oeo)_traffic_bw_ratio: " + string);
    }
	
	public void itemExited(VisualItem item, MouseEvent e)
	{
		Display d = (Display)e.getSource();
        d.setToolTipText(null);
        d.setCursor(Cursor.getDefaultCursor());
	}*/
}
