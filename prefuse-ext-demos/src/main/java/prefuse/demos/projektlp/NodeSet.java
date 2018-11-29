package prefuse.demos.projektlp;

import java.util.ArrayList;

public class NodeSet extends BuildDataSets
{
	private ArrayList<String> nsList = null;
	private ArrayList<String> nsList2 = null;
	private String nsData[] = null;
	private String nodeRegex = "\\[node\\] (.*)";
	
	public NodeSet(String file)
	{
		super(file);
		nsData = super.readData(nodeRegex);
		nsList = super.parseData(nsData);
		nsList2 = new ArrayList<String>();
		for(int i = 0; i < nsList.size(); i++)
		{
			nsList2.add(nsList.get(i));
		}
		//nsList.remove(1);
		nsList2.remove(0);
		nsList2.remove(0);
		
	}
	
	public ArrayList<String> getNSList()
	{
		return nsList;
	}
	
	public ArrayList<String> getNSList2()
	{
		return nsList2;
	}
	
	public static void main(String[] args)
	{
		NodeSet ns = new NodeSet("");
		
		System.out.println("nsList:");
		for(int i = 0; i < ns.nsList.size(); i ++)
		{
			System.out.println(i+" "+ns.nsList.get(i));
		}
		
		System.out.println("\n\nnsList2:");
		for(int i = 0; i < ns.nsList2.size(); i ++)
		{
			System.out.println(i+" "+ns.nsList2.get(i));
		}
		
		//ptrStrAry = "\\[node\\] load=(.*?), node=(.*?), node-blocking=(.*?), node-oeo-port-util=(.*?), node-oeo-traffic-bw-ratio=(.*?), node-ooo-traffic-bw-ratio=(.*?), node-unused-bw-ratio=(.*)";

		//System.out.println(s.compareTo(ptrStrAry));
		//System.out.println(s);
		//System.out.println(ptrStrAry);
	}
}
