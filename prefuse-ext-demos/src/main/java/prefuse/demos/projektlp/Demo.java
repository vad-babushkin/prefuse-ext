/*package visualization;

import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Demo implements Runnable
{
	MainDisplay zs;
	int i, j, v, w, id = 0, wavelength_count = 0;
	public static long counter = 0;
	boolean bool = true;
	double network_blocking = 0.0, network_load = 0.0;
	double data = 0.0;
	String rafData;
	String name = null;
	String[] y;
	
	public Demo(MainDisplay zs, double d)
	{
		this.zs = zs;
		Demo.counter = (long) (d*21485000);
	}

	public void run() 
	{
		String test = "\\[network\\] load=(.*?), network-blocking=(.*?), .*";
		Pattern ptrTest = Pattern.compile(test);
		Matcher mTest = null;

		ArrayList<String> nsList, lsList;
		nsList = zs.getNodeSet().getNSList();
		lsList = zs.getLinkSet().getLSList();

		try
		{
			//accesses data to be read
			RandomAccessFile raf = new RandomAccessFile(MainDisplay.file_location, "r");
			
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
			zs.getPb().setProgress(progress.format((double)counter/ProcessData.eof));

			//calls startVisualizer to begin computation
			zs.startVisualizer(network_load, network_blocking);

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.out.println("process data counter: "+counter);
		}
	}
}*/