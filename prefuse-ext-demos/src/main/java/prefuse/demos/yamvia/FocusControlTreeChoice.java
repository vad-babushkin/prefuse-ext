package prefuse.demos.yamvia;

import java.awt.event.MouseEvent;

import prefuse.controls.FocusControl;
import prefuse.data.Node;
import prefuse.visual.VisualItem;


// Our Control on the focus event
public class FocusControlTreeChoice extends FocusControl  
{ 
	/** 
	* Creates a new IVIPIFocusControl that changes the focus to another item 
	* when that item is clicked once. 
	*/ 
	public FocusControlTreeChoice() {
		// We surcharge the super focus control to perform the action on click
		this(1, "filter"); 
	} 
	 
	/** 
	* Creates a new IVIPIFocusControl that changes the focus to another item 
	* when that item is clicked once. 
	* @param focusGroup the name of the focus group to use 
	*/ 
	public FocusControlTreeChoice(String focusGroup) 
	{
		super(focusGroup); 
	} 
	 
	/** 
	* Creates a new IVIPIFocusControl that changes the focus when an item is  
	* clicked the specified number of times. A click value of zero indicates 
	* that the focus should be changed in response to mouse-over events. 
	* @param clicks the number of clicks needed to switch the focus. 
	*/ 
	public FocusControlTreeChoice(int clicks) 
	{ 
		super(clicks); 
	} 
	 
	/** 
	* Creates a new IVIPIFocusControl that changes the focus when an item is  
	* clicked the specified number of times. A click value of zero indicates 
	* that the focus should be changed in response to mouse-over events. 
	* @param focusGroup the name of the focus group to use  
	* @param clicks the number of clicks needed to switch the focus. 
	*/ 
	public FocusControlTreeChoice(String focusGroup, int clicks) 
	{ 
		super(focusGroup, clicks); 
	} 
	 
	/** 
	* Creates a new IVIPIFocusControl that changes the focus when an item is  
	* clicked the specified number of times. A click value of zero indicates 
	* that the focus should be changed in response to mouse-over events. 
	* @param clicks the number of clicks needed to switch the focus. 
	* @param act an action run to upon focus change  
	*/ 
	public FocusControlTreeChoice(int clicks, String act) 
	{ 
		super(clicks, act); 
	} 
	 
	/** 
	* Creates a new IVIPIFocusControl that changes the focus when an item is  
	* clicked the specified number of times. A click value of zero indicates 
	* that the focus should be changed in response to mouse-over events. 
	* @param focusGroup the name of the focus group to use 
	* @param clicks the number of clicks needed to switch the focus. 
	* @param act an action run to upon focus change  
	*/
	public FocusControlTreeChoice(String focusGroup, int clicks, String act) 
	{ 
		super(focusGroup, clicks); 
	} 
	 
	/** 
	* @see prefuse.controls.Control#itemClicked(prefuse.visual.VisualItem, java.awt.event.MouseEvent) 
	*/ 
	public void itemClicked(VisualItem item, MouseEvent e)  
	{ 
	    if(!e.isControlDown()) 
	    { 
	    	Node node = (Node)item.getSourceTuple(); // Get the node clicked
	    	Ivipi.controler.newChoice(node);
	    	
			// Super CLICK
		    super.itemClicked(item, e);
		    
		    //ChoicePanel.tview.setTextEditor(tc)		    
		    //ChoicePanel.tview.editText(item, "name");
		    //Node n = ChoicePanel.copy_t.addChild(node); 
			//n.set("name", IvipiData.choiceMainCat[i]);
			
		    //ChoicePanel.startEditing(item);
		    //ChoicePanel.test(item);		    
	    } 
	} 

}
