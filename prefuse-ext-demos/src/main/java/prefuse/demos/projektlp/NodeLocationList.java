package prefuse.demos.projektlp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodeLocationList 
{
	ArrayList<NodeLocation> nodeLocationAry = null;
	
	public NodeLocationList(String s)
	{
		//initializes an arraylist to hold all current node location
		nodeLocationAry = new ArrayList<NodeLocation>();
		readNodeLocation(s);
	}
	
	/**
	 * opens ZoneNodeLocation.txt to get data of where the nodes
	 * should be located and saves it in the nodeLocationAry arraylist
	 */
	private void readNodeLocation(String s)
	{
		FileInputStream in = null;
        Scanner scan = null;
        
        //tries to open file to read
        try
        {
        	if(s.equals("node"))
        	{
        		in = new FileInputStream("ZoneNodeLocation.txt");
        	}
        	else if(s.equals("edge"))
        	{
        		in = new FileInputStream("ZoneEdgeLocation.txt");
        	}
        	scan = new Scanner(in);
        }
        catch (IOException e)
        {
        	System.out.println("ZoneNodeLocation.txt");
        }
        
        //regex that parses input strings
        String ptrStr = "(.*)\\)(.*), (.*)";
        Pattern p = Pattern.compile(ptrStr);
		Matcher m = null;
		
		//reads in data and parses
		while(scan.hasNextLine())
		{	
			String str = scan.nextLine();
			m = p.matcher(str);
			boolean bool = m.find();
			if(bool)
			{
				int x = Integer.parseInt(m.group(2).trim());
				int y = Integer.parseInt(m.group(3).trim());
				nodeLocationAry.add(new NodeLocation(x, y));
				System.out.println("NodeLocationList: "+x+" "+y);
			}
		}
	}
	
	/**
	 * returns the nodeLocation arraylist instance
	 * @return
	 */
	public ArrayList<NodeLocation> getNodeLocationAry()
	{
		return nodeLocationAry;
	}
	
	public int getSize()
	{
		return nodeLocationAry.size();
	}
	
	public int getXCoor(int i)
	{
		return nodeLocationAry.get(i).getX();
	}
	
	public int getYCoor(int i)
	{
		return nodeLocationAry.get(i).getY();
	}
}
