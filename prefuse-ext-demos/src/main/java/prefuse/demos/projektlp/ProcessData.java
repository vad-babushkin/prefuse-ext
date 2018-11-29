package prefuse.demos.projektlp;

import java.io.RandomAccessFile;

public class ProcessData implements Runnable
{	
	/*public static boolean isTeleport;
	public static String strIndex;
	public static int wavelengthCount;
	
	MainDisplay zs;
	public int ct = 0;
	public static int readRate = 1000000;
	public static long eof = 2148500000L;
	
	long counter = 0, index;
	int v, w, wavelength_count = 0;
	String rafData;
	StringBuffer networkData, trafficData;
	String[] y;
	String test = "\\[network\\] load=(.*?), network-blocking=(.*?), .*";
	Pattern ptrTest = Pattern.compile(test);
	Matcher mTest = null;
	public static RandomAccessFile raf;
	ArrayList<String> nsList, lsList;
	
	String bufString, strLine, name;
	String []strSplit, dataSplit, dataSplit2;
	boolean isBeginIndex = true, bool2 = false;
	int i, j, nodeNum;
	double data, networkBlocking;
	*/
	
	static volatile boolean pause = false;
	static RandomAccessFile raf;
	static int BUFF_SIZE = 25000, prev_blocking_num = 0, blocking_num;
	static byte[] buffer;
	static long index;
	
	String bufString, dataLine;
	String[] bufStringSplit, dataLineSplit, dataLineSplit2;
	StringBuffer networkData, trafficData;
	double networkBlocking;
	boolean isBeginIndex;
	
	MainDisplay md;
	
	ProcessData(MainDisplay md)
	{
		this.md = md;
		
		//super();
		/*zs = md;
		nsList = zs.getNodeSet().getNSList();
		lsList = zs.getLinkSet().getLSList();
		networkData = new StringBuffer();
		trafficData = new StringBuffer();*/
	}
	
	public synchronized void pausePD()
	{
		pause = true;
	}
	
	public synchronized void resumePD()
	{
		pause = false;
		this.notify();
	}
	
	public void readData()
	{
		int i, j, nodeNum = 0, wavelengthCount = 0;
		String name = "";
		double data = 0;
		
		try
		{
			//checks for pause
			if (pause) {
				synchronized(this) {
					while (pause)
						wait();
				}
			}

			//initializations
			buffer = new byte[BUFF_SIZE];
			isBeginIndex = true;
			networkData = new StringBuffer();
			trafficData = new StringBuffer();
			networkBlocking = 0;
			//System.out.println(ProcessData.raf.getFilePointer());
			//reads in data into buffer
			raf.read(buffer);
			bufString = new String(buffer);
			
			//splits data into newlines
			bufStringSplit = bufString.split("\n");
			
			for(i = 0; i < bufStringSplit.length; i ++)
			{
				dataLine = bufStringSplit[i].replaceAll(", |,", " ");
				dataLineSplit = dataLine.split(" ");
				
				if(dataLineSplit[0].equals("[index]"))
				{
					if(isBeginIndex == false)
					{
						break;
					}
					isBeginIndex = false;

					for(j = 1; j < dataLineSplit.length; j++)
					{
						dataLineSplit2 = dataLineSplit[j].split("=");
						index = (long) Double.parseDouble(dataLineSplit2[1]);
					}
				}
				else if(dataLineSplit[0].equals("[network]") && isBeginIndex == false)
		    	{
					for(j = 1; j < dataLineSplit.length; j++)
		    		{
						dataLineSplit2 = dataLineSplit[j].split("=");
						if(dataLineSplit2[0].equals("network-blocking"))
						{
							networkBlocking = Double.parseDouble(dataLineSplit2[1]);
						}
						else if(dataLineSplit2[0].equals("blocking-num"))
						{
							blocking_num = Integer.parseInt(dataLineSplit2[1]);
						}
						networkData.append(dataLineSplit[j]+"\n");
		    		}
		    	}
				else if(dataLineSplit[0].equals("[node]") && isBeginIndex == false)
				{
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
				else if(dataLineSplit[0].equals("[link]") && isBeginIndex == false)
				{

					for(j = 3; j < dataLineSplit.length; j++)
					{
						dataLineSplit2 = dataLineSplit[j].split("=");
						name = dataLineSplit2[0];
						data = Double.parseDouble(dataLineSplit2[1]);
						MainDisplay.wavelengthList.get(wavelengthCount).setData(data, name);
					}
					wavelengthCount++;
				}
			}
			networkData.append("delta-block-num="+(ProcessData.blocking_num - ProcessData.prev_blocking_num)+"\n");
			
			//calls startVisualizer to begin computation
			md.startVisualizer(networkBlocking, networkData, index, trafficData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void run()
	{	
		try
		{
			raf = new RandomAccessFile(MainDisplay.file_location, "r");
			while(true)
			{
				readData();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/*private void setSimulationData()
	{
		if(dataSplit[0].equals("[network]") && isBeginIndex == false)
    	{
			for(j = 1; j < dataSplit.length; j++)
    		{
				dataSplit2 = dataSplit[j].split("=");
				if(dataSplit2[0].equals("network-blocking"))
				{
					networkBlocking = Double.parseDouble(dataSplit2[1]);
				}
				networkData.append(dataSplit[j]+"\n");
    		}
    	}
		else if(dataSplit[0].equals("[node]") && isBeginIndex == false)
		{
			dataSplit2 = dataSplit[2].split("=");
			nodeNum = Integer.parseInt(dataSplit2[1]) - 1;

			for(j = 3; j < dataSplit.length; j++)
    		{
				dataSplit2 = dataSplit[j].split("=");
				name = dataSplit2[0];
				data = Double.parseDouble(dataSplit2[1]);
				MainDisplay.nodeList.get(nodeNum).setData(data, name);
    		}
		}
		else if(dataSplit[0].equals("[link]") && isBeginIndex == false)
		{
			try
			{
				for(j = 3; j < dataSplit.length; j++)
				{
					dataSplit2 = dataSplit[j].split("=");
					name = dataSplit2[0];
					data = Double.parseDouble(dataSplit2[1]);
					if(wavelengthCount > 51)
					{
						wavelengthCount = 0;
					}
					else
					{
						MainDisplay.wavelengthList.get(wavelengthCount).setData(data, name);
					}
				}
				wavelengthCount++;
			}
			catch(Exception e)
			{
				System.out.println("wv:"+wavelengthCount);
			}
		}
		else if(dataSplit[0].equals("[traffic]") && isBeginIndex == false)
		{
			trafficData.append(index+"\n");
			for(j = 1; j < dataSplit.length; j++)
    		{
				trafficData.append(dataSplit[j]+"\n");
    		}
		}
	}*/
	
	/*public void run()
	{
		try
		{
			raf = new RandomAccessFile(MainDisplay.file_location, "r");
			
			while(true)
			{
				if (pause) {
	                synchronized(this) {
	                    while (pause)
	                        wait();
	                }
	            }
				
				isBeginIndex = true;
				networkData = new StringBuffer();
				trafficData = new StringBuffer();
				wavelengthCount = 0;
				nodeNum = 0;
				networkBlocking = 0;
				
				if(isTeleport)
				{
					System.out.println(raf.getFilePointer());
					raf.seek(raf.getFilePointer()-ProcessData.BUFF_SIZE*2);
					buffer = new byte[ProcessData.BUFF_SIZE*2];
					System.out.println("\nteleport: ");
				}
				
				raf.read(buffer);
				bufString = new String(buffer);
				strSplit = bufString.split("\n");
				
				for(i = 0; i < strSplit.length; i ++)
				{
					strLine = strSplit[i].replaceAll(", |,", " ");
					dataSplit = strLine.split(" ");
			
					if(isTeleport)
					{
						if(dataSplit[0].equals("[index]"))
						{
							System.out.println(strLine);
							System.out.println(strIndex);
						}
						
						if(strLine.equals(strIndex))
						{
							if(!isBeginIndex)
							{
								break;
							}
							isBeginIndex = false;

							for(j = 1; j < dataSplit.length; j++)
							{
								dataSplit2 = dataSplit[j].split("=");
								index = (long) Double.parseDouble(dataSplit2[1]);
							}
						}
					}
					else
					{
						if(dataSplit[0].equals("[index]"))
						{
							if(!isBeginIndex)
							{
								break;
							}
							isBeginIndex = false;

							for(j = 1; j < dataSplit.length; j++)
							{
								dataSplit2 = dataSplit[j].split("=");
								index = (long) Double.parseDouble(dataSplit2[1]);
							}
						}
					}
					if(isBeginIndex == false) { setSimulationData(); }
				}
				//calls startVisualizer to begin computation
				zs.startVisualizer(networkBlocking, networkData, index, trafficData);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void teleport(String strIndex) 
	{
		ProcessData.strIndex = strIndex;
		isTeleport = true;
		zs.buttons.getResume().doClick();
		zs.buttons.getPause().doClick();
		isTeleport = false;
	}*/
	
	/*public void run()
	{
		try
		{
			FileInputStream in = new FileInputStream(MainDisplay.file_location);
			String bufString, strLine, name;
			String []strSplit, dataSplit, dataSplit2;
			boolean bool = true, bool2 = false;
			int i, j, wavelengthCount, nodeNum;
			double data, networkBlocking;
	        
			while(in.read(buffer) > 0)
			{
				//pause
				if(pause)
				{
					synchronized(this)
					{
						while(pause)
						{
							wait();
						}
					}
				}
				bufString = new String(buffer);
				strSplit = bufString.split("\n");
				
				bool = true;
				bool2 = false;
				networkData = new StringBuffer();
				trafficData = new StringBuffer("");
				wavelengthCount = 0;
				nodeNum = 0;
				networkBlocking = 0;
				
				for(i = 0; i < strSplit.length; i ++)
				{
					strLine = strSplit[i].replaceAll(", |,", " ");
					dataSplit = strLine.split(" ");
			
					if(dataSplit[0].equals("[index]"))
		        	{
						if(bool == false)
						{
							break;
						}
						bool = false;
						bool2 = true;
						
						for(j = 1; j < dataSplit.length; j++)
		        		{
							dataSplit2 = dataSplit[j].split("=");
							index = (long) Double.parseDouble(dataSplit2[1]);
		        		}
		        	}
					else if(dataSplit[0].equals("[network]") && bool2 == true)
		        	{
						for(j = 1; j < dataSplit.length; j++)
		        		{
							dataSplit2 = dataSplit[j].split("=");
							if(dataSplit2[0].equals("network-blocking"))
							{
								networkBlocking = Double.parseDouble(dataSplit2[1]);
							}
							networkData.append(dataSplit[j]+"\n");
		        		}
		        	}
					else if(dataSplit[0].equals("[node]") && bool2 == true)
					{
						dataSplit2 = dataSplit[2].split("=");
						nodeNum = Integer.parseInt(dataSplit2[1]) - 1;

						for(j = 3; j < dataSplit.length; j++)
		        		{
							dataSplit2 = dataSplit[j].split("=");
							name = dataSplit2[0];
							data = Double.parseDouble(dataSplit2[1]);
							MainDisplay.nodeList.get(nodeNum).setData(data, name);
		        		}
					}
					else if(dataSplit[0].equals("[link]") && bool2 == true)
					{
						for(j = 3; j < dataSplit.length; j++)
		        		{
							dataSplit2 = dataSplit[j].split("=");
							name = dataSplit2[0];
							data = Double.parseDouble(dataSplit2[1]);
							MainDisplay.wavelengthList.get(wavelengthCount).setData(data, name);
		        		}
						wavelengthCount++;
					}
					else if(dataSplit[0].equals("[traffic]") && bool2 == true)
					{
						trafficData.append(index+"\n");
						for(j = 1; j < dataSplit.length; j++)
		        		{
							trafficData.append(dataSplit[j]+"\n");
		        		}
					}
				}
				//calls startVisualizer to begin computation
				zs.startVisualizer(networkBlocking, networkData, index, trafficData);
	        }
	        in.close();
	        System.out.println("End of file reached.");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
	
	/*public void run()
	{        
		try
		{
			//accesses data to be read
			RandomAccessFile raf = new RandomAccessFile(MainDisplay.file_location, "r");

			while(true)
			{
				if (pause) {
	                synchronized(this) {
	                    while (pause)
	                        wait();
	                }
	            }

				counter += readRate;
				raf.seek(counter);
				rafData = raf.readUTF();
				bool = true;
				y = rafData.split("\n");
				
				for(i = 0; i < y.length; i ++)
				{
					mTest = ptrTest.matcher(y[i]);
					
					if(mTest.find() && bool)
					{	
						bool = false;
						wavelength_count = 0;
						for(j = i; j < y.length; j ++)
						{
							if(j > (y.length-10))
							{
								j = y.length;
							}
							else
							{	
								//System.out.println("HERE3");
								mTest = ptrTest.matcher(y[j+1]);
								if(mTest.find())
								{
									//System.out.println("HERE4");
									j = y.length;
									network_load = Double.parseDouble(mTest.group(1));
									network_blocking = Double.parseDouble(mTest.group(2));
								}
								else
								{
									//System.out.println(y[j]);
									zs.m = zs.ptrAry.matcher(y[j]);
									zs.m2 = zs.ptrAry2.matcher(y[j]);
									
									if(zs.m.find())
									{
										//System.out.println("HERE 5");
										
										id = (Integer.parseInt(zs.m.group(2)) - 1);
										
										for(v = 0; v < nsList.size(); v ++)
										{
											if(v < 2)
											{
											}
											else
											{
												data = Double.parseDouble(zs.m.group(v+1));
												name = nsList.get(v);
												MainDisplay.nodeList.get(id).setData(data, name);
												//System.out.println(name+" "+data);
											}
										}
										MainDisplay.nodeList.get(id).setColor_indicator(0.0);
									}

									if(zs.m2.find())
									{
										//System.out.println("HERE 6 ");
										
										for(w = 0; w < lsList.size(); w++)
										{
											if(w > 1)
											{
												data = Double.parseDouble(zs.m2.group(w+1));
												name = lsList.get(w);
												MainDisplay.wavelengthList.get(wavelength_count).setData(data, name);
												//System.out.println(name+" "+data);
											}
										}
										wavelength_count ++;
									}
								}
							}
						}
					}
				}
				
				DecimalFormat progress = new DecimalFormat("0.00");
				zs.getPb().setProgress(progress.format((double)counter/eof));
				
				//calls startVisualizer to begin computation
				zs.startVisualizer(network_load, network_blocking);
			}
		}
		catch(EOFException e)
		{
			System.out.println(e.getMessage());
			System.out.println("EOF: "+counter);
			AlertWindow eof = new AlertWindow("End of file reached.");
		} 
		catch (FileNotFoundException e) 
		{
			System.out.println("File not found: "+e.getMessage());
		} 
		catch (IOException e) 
		{
			System.out.println("IO Exception: "+e.getMessage());
		} 
		catch (InterruptedException e) 
		{
			System.out.println("resume...");
		}
	}*/
}