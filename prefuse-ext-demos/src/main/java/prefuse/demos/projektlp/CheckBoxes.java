package prefuse.demos.projektlp;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class CheckBoxes implements ItemListener
{
	JCheckBox[] nodeSetButtons;
	JCheckBox linkSetButtons;
    JCheckBox autoPause;
    
	String c;
	String[] c_array;
	int r,g,b;
	
	MainDisplay md = null;
	
    public CheckBoxes(ArrayList<String> nsl, ArrayList<String> lsl, MainDisplay mainDisplay)
    {
    	this.md = mainDisplay;
    	nodeSetButtons = new JCheckBox[nsl.size()];
    	for(int i = 0; i < nsl.size(); i ++)
    	{
    		c = MainDisplay.colorValues.get(i);
    		c_array = c.split(",");
    		r = Integer.parseInt(c_array[0].trim());
    		g = Integer.parseInt(c_array[1].trim());
    		b = Integer.parseInt(c_array[2].trim());

    		nodeSetButtons[i] = new JCheckBox(nsl.get(i));
    		nodeSetButtons[i].setBackground(new Color(r,g,b));
    		nodeSetButtons[i].setForeground(Color.WHITE);
    		nodeSetButtons[i].setSelected(false);
    		nodeSetButtons[i].addItemListener(this);
    	}
    	
    	autoPause = new JCheckBox("Auto Pause");
    	autoPause.setSelected(false);
    	autoPause.addItemListener(this);
    }

    public JCheckBox getAutoPause()
    {
    	return autoPause;
    }
    
	public JCheckBox[] getNSB()
	{
		return nodeSetButtons;
	}
	
	public JCheckBox getLSB()
	{
		return linkSetButtons;
	}
	
	public void itemSelected(String name)
	{
		for(int i = 0; i < MainDisplay.nodeList.size(); i ++)
		{
			MainDisplay.zoneManager.setZoneVisible("Node "+(i+1)+"_"+name, true);
		}
	}
		
	public void itemDeSelected(String name)
	{
		for(int i = 0; i < MainDisplay.nodeList.size(); i ++)
		{
			MainDisplay.zoneManager.setZoneVisible("Node "+(i+1)+"_"+name, false);
		}
	}
	
	public void wlSelected(String name)
	{
		for(int i = 0; i < MainDisplay.wavelengthList.size(); i ++)
		{
			MainDisplay.zoneManager.setZoneVisible("Edge "+i+"_"+name, true);
		}
	}
	
	public void wlDeSelected(String name)
	{
		for(int i = 0; i < MainDisplay.wavelengthList.size(); i ++)
		{
			MainDisplay.zoneManager.setZoneVisible("Edge "+i+"_"+name, false);
		}
	}
	
	public void itemStateChanged(ItemEvent e) 
    {
    	Object source = e.getItemSelectable();
    	
    	for(int i = 0; i < nodeSetButtons.length; i ++)
    	{
    		if(source == nodeSetButtons[i])
    		{
    			if(e.getStateChange() == ItemEvent.DESELECTED)
        		{
        			itemDeSelected(nodeSetButtons[i].getText());
        		}
        		else if(e.getStateChange() == ItemEvent.SELECTED)
        		{
        			itemSelected(nodeSetButtons[i].getText());
        		}
    		}
    	}
    	
    	if(source == linkSetButtons)
    	{
    		if(e.getStateChange() == ItemEvent.DESELECTED)
    		{
    			//System.out.println(linkSetButtons[i].getText());
    			//MainDisplay.linkCheckBox.remove(linkSetButtons.getText());
    			//System.out.println(MainDisplay.linkCheckBox.toString());
    		}
    		else if(e.getStateChange() == ItemEvent.SELECTED)
    		{
    			//System.out.println(linkSetButtons[i].getText());
    			//MainDisplay.linkCheckBox.add(linkSetButtons.getText());
    			//System.out.println(MainDisplay.linkCheckBox.toString());
    		}
    	}
    	
    	
    	if(source == autoPause)
    	{
    		if(e.getStateChange() == ItemEvent.SELECTED)
    		{
    			MainDisplay.autoPause = true;
    			System.out.println("selected");
    		}
    		else if(e.getStateChange() == ItemEvent.DESELECTED)
    		{
    			MainDisplay.autoPause = false;
    			System.out.println("deselected");
    		}
    	}
    	
    	md.refresh();
    }
	
	public static void main(String[] args)
	{

	}
}
