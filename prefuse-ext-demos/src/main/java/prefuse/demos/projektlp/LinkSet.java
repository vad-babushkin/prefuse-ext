package prefuse.demos.projektlp;

import java.util.ArrayList;

public class LinkSet extends BuildDataSets
{
	private ArrayList<String> lsList = null;
	private ArrayList<String> lsList2 = null;
	private String lsData[] = null;
	private String linkRegex = "\\[link\\] (.*)";
	
	public LinkSet(String file)
	{
		super(file);
		lsData = super.readData(linkRegex);
		lsList = super.parseData(lsData);
		lsList2 = new ArrayList<String>();
		for(int i = 2; i < lsList.size(); i++)
		{
			lsList2.add(lsList.get(i));
		}
	}
	
	public ArrayList<String> getLSList()
	{
		return lsList;
	}
	
	public ArrayList<String> getLSList2()
	{
		return lsList2;
	}
	
	public static void main(String[] args)
	{
		LinkSet ls = new LinkSet("");
		
		System.out.println("lsList:");
		for(int i = 0; i < ls.lsList.size(); i ++)
		{
			System.out.println(ls.lsList.get(i));
		}
		
		System.out.println("\n\nlsList2:");
		for(int i = 0; i < ls.lsList2.size(); i ++)
		{
			System.out.println(ls.lsList2.get(i));
		}
	}
}
