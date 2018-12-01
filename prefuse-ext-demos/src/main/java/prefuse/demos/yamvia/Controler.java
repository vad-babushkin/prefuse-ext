package prefuse.demos.yamvia;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import prefuse.data.Node;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.util.ui.*;

import prefuse.Display;
import prefuse.visual.VisualItem;
import java.util.regex.*;

import java.awt.BorderLayout;

public class Controler 
{	
    // Choice of the user
    public static List<Node> choosedNodes = new ArrayList();
	public static boolean yearIsChoose = false;
	private Vector choice = new Vector ();
    private IvipiData iData;
    
    
	public Controler ()
	{
		iData = new IvipiData ();
	}
	
	private void showMenu (Node node)
	{
		for (int i = 0; i<IvipiData.choiceMainCat.length; i++)
		{
			if (IvipiData.choiceMainCat[i]=="YEARS" && yearIsChoose) continue; // Years is choose, we don't add year to the possibl choice 					
			Node n = ChoicePanel.copy_t.addChild(node); 
			n.set("name", IvipiData.choiceMainCat[i]);
		}
	}
	
	private void showMenuActor (Node node)
	{
		Vector<String> tmp = new Vector ();
		tmp.add("Search");
		tmp.add("Most Popular");
		tmp.add("All 20 Firsts");
		ChoicePanel.updateDynamicNode (node, tmp);
	}
	
	private void showSearchPerson (Node node, String role)
	{
		Vector result = new Vector ();
		String msg = "Tape Which actor are your searching";
		String retour = "";
		
		do {
			if (result.size()>30) msg ="More than 30 results for your entry, please try again";
			retour = JOptionPane.showInputDialog(Ivipi.choicePanel,
					msg,
					"Search Actor",
					JOptionPane.QUESTION_MESSAGE);
			if (retour != "")
				result = this.iData.getPersons (this.choice, role, retour, 10);
		}while (retour != "" && result.size()>30);
		
		if (result.size()<30 && retour != "") 
			ChoicePanel.updateDynamicNode (node, result);
	}
	
	private void showFirstPersons (Node node, String role, int top)
	{
		Vector result = new Vector ();
		result = this.iData.getPersons (this.choice, role, top);
		ChoicePanel.updateDynamicNode (node, result);
	}
	
	private void showGenre (Node node)
	{
		Vector result = new Vector ();
		result = this.iData.getGenres (this.choice);
		ChoicePanel.updateDynamicNode (node, result);
	}
	
	// New Choice
    public void newChoice (Node node)
    {
    	String nodeValue = node.getString("name");
    	int nodeDeep = ChoicePanel.copy_t.getDepth(node.getRow()); 

		System.out.println ("value "+nodeValue+"- deep "+nodeDeep+" - choide "+choice.size());
		
    	while (choice.size() > 0 && choice.size() >= nodeDeep)
    	{
    		System.out.println ("remove");
    		choice.removeElementAt(choice.size()-1);
			
    		// We remove also on the treemap
    		ChoicePanel.deleteNode (choosedNodes.get(choosedNodes.size()-1));
    		choosedNodes.remove(choosedNodes.get(choosedNodes.size()-1));
    		
			// We remove the choice about the year, the user can choose an other year
			if (nodeValue == "YEARS")
				yearIsChoose = false;
    	}
		
		// Add the clicked node to the list of choice
		if (nodeDeep > 0)
		{
			choice.add(new String(nodeValue));
			choosedNodes.add(node);
		}
 
		
		// Main Menu
		if (nodeValue == "ACTORS")
		{
			// Show Menu for Actors
			showMenuActor (node);
		}
		else if (nodeValue == "ACTRESS")
		{
			// Show Menu for Actors
			showMenuActor (node);
		}
		else if (nodeValue == "DIRECTORS")
		{
			// Show Menu for Actors
			showMenuActor (node);
		}
		else if (nodeValue == "CINEMATOGRAPHERS")
		{
			// Show Menu for Actors
			showMenuActor (node);
		}
		else if (nodeValue == "YEARS")
		{
			// Show Menu for Actors
			showMenuActor (node);
		}
		else if (nodeValue == "GENRES")
		{
			// Show Genre
			showGenre (node);
		}
		
		// Not in main menu
		else
		{
			String parentNodeValue = this.choice.get(this.choice.size()-2).toString();
			System.out.println ("PARENT NODE VALUE : "+parentNodeValue);
			
			// Sub menu
			if (nodeValue == "Search")
			{
				// Show The first 30 Persons available for this session
				if (parentNodeValue.compareTo(new String("ACTORS")) == 0)
					showSearchPerson (node, "actors");
				else if (parentNodeValue.compareTo(new String("ACTRESS")) == 0)
					showSearchPerson (node, "actress");
				else if (parentNodeValue.compareTo(new String("CINEMATOGRAPHERS")) == 0)
					showSearchPerson (node, "cinematographers");
				else if (parentNodeValue.compareTo(new String("DIRECTORS")) == 0)
					showSearchPerson (node, "directors");
				// Actor
				
			}
			else if (nodeValue == "Most Popular")
			{
				// Show Most Popular Actors
			}
			else if (nodeValue == "All 20 Firsts")
			{
				// Show The first 30 Persons available for this session
				if (parentNodeValue.compareTo(new String("ACTORS")) == 0)
					showFirstPersons (node, "actors", 10);
				else if (parentNodeValue.compareTo(new String("ACTRESS")) == 0)
					showFirstPersons (node, "actress", 10);
				else if (parentNodeValue.compareTo(new String("CINEMATOGRAPHERS")) == 0)
					showFirstPersons (node, "cinematographers", 10);
				else if (parentNodeValue.compareTo(new String("DIRECTORS")) == 0)
					showFirstPersons (node, "directors", 10);
			}
			// Not in submenu
			else
			{
				// On doit être ds le cas d'un actor ou d'une actrice
				showMenu (node);
				String queryMovies = this.iData.getQueryMovies (this.choice);
				try
				{
					DataPlot.t = null;
					DataPlot.t = DataPlot.db.getData(DataPlot.t, queryMovies, "mname");
					DataPlot.db.loadData(DataPlot.t, queryMovies, "mname");
				}catch (Exception e)
				{}
				//Ivipi.dataShit.m_vis.run("update");
				
				//Ivipi.dataShit.m_vis.removeGroup("mname");
				
				//Predicate p = (Predicate)
		        //ExpressionParser.parse("[mrating] >= 0"); 
		        //vt = vis.addTable(group, t, p);
		        
				Ivipi.dataPlot.m_vis.reset();
				Ivipi.dataPlot.vt = null;
				Ivipi.dataPlot.vt = Ivipi.dataPlot.m_vis.addTable("table", DataPlot.t);
				Ivipi.dataPlot.m_vis.run("update");
				Ivipi.dataPlot.repaint();
				//Ivipi.dataShit.m_vis.repaint();
				/*Ivipi.mainPanel.remove(Ivipi.dataShit);
				Ivipi.dataShit = null;
				Ivipi.dataShit = new DataShit (queryMovies);
				Ivipi.mainPanel.add(Ivipi.dataShit, BorderLayout.CENTER);
				Ivipi.dataShit.setVisible(true);
				Ivipi.mainPanel.repaint();*/
			}
		}

		// Feedback
		System.out.println("LISTE NOEUDS : "+choice);
    }
    
}

/*Pattern aToZ = Pattern.compile("^[A-Z]$");
Matcher fit = aToZ.matcher (nodeValue);

if (fit.matches())
{
	System.out.println ("TEST A TO Z");
	Vector result = this.iData.getActors (this.choice);
	ChoicePanel.updateDynamicNode (node, result);
}
else if (nodeValue == "YEARS")
{
	yearIsChoose = true; // We remove this choice for the resting choices table
	//ChoicePanel.updateDynamicNode (node, ivipi);
}	
else if (nodeValue == "ACTORS")
{
	Vector<String> tmp = new Vector ();
	tmp.add("Search An Actor");
	tmp.add("Most Popular");
	tmp.add("All 30 Firsts");
	ChoicePanel.updateDynamicNode (node, tmp);
}
else if (nodeValue == "All 30 Firsts")
{
	Vector result = new Vector ();
	result = this.iData.getActors (this.choice, 'm', 30);
	ChoicePanel.updateDynamicNode (node, result);
}
else if (nodeValue == "Search An Actor")
{
	
	//Vector result = this.iData.getActors(this.choice);
	//if (result.size() > 100)
	//{
		//node.set("name", data.get(i));
	//	System.out.println (node.canSet("name", javax.swing.text.JTextComponent.class));
	//}
	//ChoicePanel.updateDynamicNode (node, result);
	
	//Vector result = this.iData.getAToZActors (this.choice, 'm');
	//ChoicePanel.updateDynamicNode (node, result);
	//((Display)Ivipi.choicePanel).editText(item, "mname");
	
	
}	
else if (nodeValue == "ACTRESS")
{
	//ChoicePanel.updateDynamicNode (node, actress);
}	
else if (nodeValue == "GENRES")
{
	//ChoicePanel.updateDynamicNode (node, genres);
}
else // SHOW CAT
{	
	for (int i = 0; i<IvipiData.choiceMainCat.length; i++)
	{
		if (IvipiData.choiceMainCat[i]=="YEARS" && yearIsChoose) continue; // Years is choose, we don't add year to the possibl choice 					
		Node n = ChoicePanel.copy_t.addChild(node); 
		n.set("name", IvipiData.choiceMainCat[i]);
	}
}
*/