package prefuse.demos.projektlp;

import java.io.IOException;

public class ProcessTeleport implements Runnable
{
	MainDisplay md;
	Buttons b;
	double point;
	long index;
	AlertWindow aw;
	
	public ProcessTeleport(MainDisplay md, Buttons b, double d, AlertWindow aw) 
	{
		this.md = md;
		this.b = b;
		this.point = d;
		this.index = 0;
		this.aw = aw;
	}

	private boolean findData(double num)
	{
		int i = 0, j;
		byte[] b = new byte[ProcessData.BUFF_SIZE];
		String buffer = null, dataLine = null;
		String[] bufferSplit = null, dataLineSplit, dataLineSplit2;
		boolean isFound = false;

		try 
		{
			ProcessData.raf.read(b);
			buffer = new String(b);
			bufferSplit = buffer.split("\n");
			
			for(i = 0; i < bufferSplit.length; i++)
			{
				dataLine = bufferSplit[i].replaceAll(", |,", " ");
				dataLineSplit = dataLine.split(" ");
				
				if(dataLineSplit[0].equals("[index]"))
				{
					for(j = 1; j < dataLineSplit.length;)
					{
						dataLineSplit2 = dataLineSplit[j].split("=");
						index = (long) Double.parseDouble(dataLineSplit2[1]);
						this.aw.setText(index);
						if(index == num)
						{
							isFound = true;
							break;
						}
						else
						{
							break;
						}
					}
				}
			}
		} 
		catch (IOException e)
		{
			System.out.println("IO Exception");
		} 
		catch (ArrayIndexOutOfBoundsException e)
		{
			new AlertWindow("Cannot jump to index\n");
		}

		return isFound;
	}
	
	private void readData(double num)
	{
		int i, j, nodeNum = 0, wavelengthCount = 0;
		byte[] b = new byte[2*ProcessData.BUFF_SIZE];
		String buffer = null, dataLine = null;
		String[] bufferSplit, dataLineSplit, dataLineSplit2;
		StringBuffer networkData, trafficData;
		double networkBlocking;
		boolean isFound = false, bool = false;
		String name = "";
		double data = 0;
		long fp;
		
		try
		{
			fp = ProcessData.raf.getFilePointer();
			ProcessData.raf.seek(fp - ProcessData.BUFF_SIZE);
			networkData = new StringBuffer();
			trafficData = new StringBuffer();
			networkBlocking = 0;
			
			ProcessData.raf.read(b);
			buffer = new String(b);
			bufferSplit = buffer.split("\n");
			
			for(i = 0; i < bufferSplit.length; i++)
			{
				dataLine = bufferSplit[i].replaceAll(", |,", " ");
				dataLineSplit = dataLine.split(" ");

				if(dataLineSplit[0].equals("[index]"))
				{
					for(j = 1; j < dataLineSplit.length;)
					{
						dataLineSplit2 = dataLineSplit[j].split("=");
						index = (long) Double.parseDouble(dataLineSplit2[1]);
						
						if(index == num)
						{
							System.out.println(dataLine);
							ProcessData.index = index;
							isFound = true;
							bool = true;
							break;
						}
						else
						{
							bool = false;
							break;
						}
					}
				}
				else if(dataLineSplit[0].equals("[network]") && bool == true)
		    	{
					System.out.println(dataLine);

					for(j = 1; j < dataLineSplit.length; j++)
		    		{
						dataLineSplit2 = dataLineSplit[j].split("=");
						if(dataLineSplit2[0].equals("network-blocking"))
						{
							networkBlocking = Double.parseDouble(dataLineSplit2[1]);
						}
						else if(dataLineSplit2[0].equals("blocking-num"))
						{
							ProcessData.blocking_num = Integer.parseInt(dataLineSplit2[1]);
						}
						networkData.append(dataLineSplit[j]+"\n");
		    		}
		    	}
				else if(dataLineSplit[0].equals("[node]") && bool == true)
				{
					System.out.println(dataLine);

					dataLineSplit2 = dataLineSplit[2].split("=");
					nodeNum = Integer.parseInt(dataLineSplit2[1]) - 1;

					for(j = 3; j < dataLineSplit.length; j++)
		    		{
						dataLineSplit2 = dataLineSplit[j].split("=");
						name = dataLineSplit2[0];
						data = Double.parseDouble(dataLineSplit2[1]);
						MainDisplay.nodeList.get(nodeNum).setData(data, name);
		    		}
				}
				else if(dataLineSplit[0].equals("[link]") && bool == true)
				{
					System.out.println(dataLine);

					for(j = 3; j < dataLineSplit.length; j++)
					{
						dataLineSplit2 = dataLineSplit[j].split("=");
						name = dataLineSplit2[0];
						data = Double.parseDouble(dataLineSplit2[1]);
						MainDisplay.wavelengthList.get(wavelengthCount).setData(data, name);
					}
					wavelengthCount++;	
				}
				else if(dataLineSplit[0].equals("[traffic]") && bool == true)
				{
					System.out.println(dataLine);
				}
			}
			
			if(isFound == true)
			{
				//calls startVisualizer to begin computation
				md.startVisualizer(networkBlocking, networkData, ProcessData.index, trafficData);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void jumpToPoint(double num)
	{	
		long fp = 0;
		boolean found = false;		
		
		try 
		{		
			fp = ProcessData.raf.getFilePointer();
			while(found == false)
			{
				if(num > index)
				{
					ProcessData.raf.seek(fp +  3*ProcessData.BUFF_SIZE);
					found = findData(num);
				}
				else if(num <= index)
				{
					ProcessData.raf.seek(fp - 4*ProcessData.BUFF_SIZE);
					found = findData(num);
				}
				
				fp = ProcessData.raf.getFilePointer();
			}		
			System.out.println("found");
			readData(num);
		} 
		catch (IOException e) 
		{
			System.out.println("negative seek offset");
			ProcessData.BUFF_SIZE = 10000;
			jumpToPoint(num);
			//e.printStackTrace();
		}
	}
	
	public void run() 
	{
		jumpToPoint(this.point);
	}
}
