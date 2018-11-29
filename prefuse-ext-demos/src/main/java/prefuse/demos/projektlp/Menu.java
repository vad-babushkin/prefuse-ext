/**
 * Menu.java
 * Han Dong
 * July 16, 2009
 * 
 * Sets up the menu of the display
 */
package prefuse.demos.projektlp;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import prefuse.Visualization;
import prefuse.visual.NodeItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;

@SuppressWarnings({ "unchecked", "unused"})
public class Menu 
{
	JMenuBar menuBar = null;
	JMenuItem saveAction = null;
	JMenuItem exitAction = null;
	JMenuItem saveEdgeAction = null;
	JMenuItem chooseDataAction = null;
	NodeLocationList nlocateList = null;
	JFileChooser fc = null;
	
	public Menu(NodeLocationList nlocateList)
	{
		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		saveAction  = new JMenuItem("Save Node Location");
		fileMenu.add(saveAction);
		saveEdgeAction = new JMenuItem("Save Edge Location");
		fileMenu.add(saveEdgeAction);
		chooseDataAction = new JMenuItem("Choose Data...");
		fileMenu.add(chooseDataAction);
		exitAction = new JMenuItem("Exit");
		fileMenu.add(exitAction);
		
		this.nlocateList = nlocateList;
	}
	
	public JMenuBar getMenuBar()
	{
		return menuBar;
	}
	
	public void setAction(final Visualization vis)
	{
		saveAction.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				Iterator nodeItems = vis.items("graph.nodes");
				NodeItem aNodeItem = null;

				try
				{
					FileWriter fstream = new FileWriter("ZoneNodeLocation.txt");
					BufferedWriter out = new BufferedWriter(fstream);
					for(int i = 0; i < nlocateList.getSize(); i ++)
					{
						aNodeItem = (NodeItem) nodeItems.next();
						out.write((i+1)+") "+(int)aNodeItem.getX()+", "+(int)aNodeItem.getY());
						out.newLine();
						System.out.println((i+1)+") "+(int)aNodeItem.getX()+", "+(int)aNodeItem.getY());
					}
					out.close();
					System.out.println("Saved node locations.");
				}
				catch(Exception e)
				{
					System.out.println("cannot write to file.");
				}
				
            }
		});
		
		exitAction.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				System.exit(0);
				System.out.println("program ended");
			}
		});
		
		saveEdgeAction.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				Iterator nodeItems = vis.items("edgeGraph.nodes");
				NodeItem aNodeItem = null;
				int i;
				
				try
				{
					FileWriter fstream = new FileWriter("ZoneEdgeLocation.txt");
					BufferedWriter out = new BufferedWriter(fstream);
					for(i = 0; i < MainDisplay.wavelengthList.size(); i ++)
					{
						aNodeItem = (NodeItem) nodeItems.next();
						out.write((i+1)+") "+(int)aNodeItem.getX()+", "+(int)aNodeItem.getY());
						out.newLine();
					}
					out.close();
					System.out.println("Saved edge locations.");
				}
				catch(Exception e)
				{
					System.out.println("cannot save edge location");
				}
			}
		});
		
		chooseDataAction.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				fc = new JFileChooser();
				if(fc.showOpenDialog(menuBar) == JFileChooser.APPROVE_OPTION)
				{
					File file = fc.getSelectedFile();
	                MainDisplay.file_location = file.getAbsolutePath();
				}
				else
				{
					System.out.println("Exited...");
				}
			}
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub

	}

}
