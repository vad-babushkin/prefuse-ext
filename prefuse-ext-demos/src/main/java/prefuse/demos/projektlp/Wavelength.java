package prefuse.demos.projektlp;

import java.util.ArrayList;

public class Wavelength 
{
	int source;
	int destination;
	int midX;
	int midY;
	ArrayList<WavelengthData> wdList;
	
	/*
	double usage;
	int barGraphIndicator;
	ArrayList<NodeItem> nodesAry = null;
	boolean reachedMax;
	*/
	
	public Wavelength(int source, int destination, int midX, int midY, ArrayList<String> lsList2) 
	{
		this.source = source;
		this.destination = destination;
		this.midX = midX;
		this.midY = midY;
		wdList = new ArrayList<WavelengthData>();
		initializeWdList(lsList2);
		
		//this.usage = 0.0;
		//this.barGraphIndicator = 0;
		//this.reachedMax = false;
		//nodesAry = new ArrayList<NodeItem>();
	}

	
	public ArrayList<WavelengthData> getWdList() {
		return wdList;
	}


	public void setData(double data, String name)
	{
		for(int i = 0; i < wdList.size(); i ++)
		{
			if(wdList.get(i).getName().equals(name))
			{
				wdList.get(i).setData(data);
				i = this.wdList.size();
			}
		}
	}
	
	private void initializeWdList(ArrayList<String> lsList2)
	{
		for(int i = 0; i < lsList2.size(); i++)
		{
			wdList.add(new WavelengthData(lsList2.get(i)));
		}
	}
	
	/*
	public boolean isReachedMax() {
		return reachedMax;
	}

	public void setReachedMax(boolean reachedMax) {
		this.reachedMax = reachedMax;
	}

	public void addNode(NodeItem node)
	{
		nodesAry.add(node);
		barGraphIndicator ++;
	}
	
	public NodeItem removeNode()
	{
		NodeItem temp = null;
		try
		{
			temp = nodesAry.get(this.barGraphIndicator-1);
			nodesAry.remove(this.barGraphIndicator-1);
			barGraphIndicator --;	
		}
		catch (Exception e)
		{
			System.out.println("Exception: this.barGraphIndicator "+this.barGraphIndicator);
		}
		return temp;
	}*/
	
	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	/*public double getUsage() {
		return usage;
	}

	public void setUsage(double usage) {
		this.usage = usage;
	}*/

	public int getMidX() {
		return midX;
	}

	public void setMidX(int midX) {
		this.midX = midX;
	}

	public int getMidY() {
		return midY;
	}

	public void setMidY(int midY) {
		this.midY = midY;
	}
}
