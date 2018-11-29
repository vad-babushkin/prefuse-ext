package prefuse.demos.projektlp;

import java.util.ArrayList;

public class NetworkSet extends BuildDataSets
{
	private ArrayList<String> nwsList = null;
	private String nwsData[] = null;
	private String networkRegex = "\\[network\\] (.*)";

	public NetworkSet(String file)
	{
		super(file);
		nwsData = super.readData(networkRegex);
		nwsList = super.parseData(nwsData);
	}
	
	public void addData(String s)
	{
		nwsList.add(s);
	}
	
	public ArrayList<String> getNWSList()
	{
		return nwsList;
	}
	
	public static void main(String[] args)
	{
		NetworkSet nws = new NetworkSet("");
		for(int i = 0; i < nws.nwsList.size(); i ++)
		{
			System.out.println(nws.nwsList.get(i));
		}
	}
}
