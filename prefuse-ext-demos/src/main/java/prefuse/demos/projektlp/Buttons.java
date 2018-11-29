package prefuse.demos.projektlp;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;

public class Buttons implements ActionListener
{
	JButton start = null;
	JButton resume = null;
	JButton pause = null;
	
	JButton scan = null;
	JButton refresh = null;
	
	ProcessData pd = null;
	MainDisplay md = null;
			
	public Buttons(ProcessData pd, MainDisplay mainDisplay)
	{
		start = new JButton("Start");
		resume = new JButton("Resume");
		pause = new JButton("Pause");
		scan = new JButton("Scan");
		refresh = new JButton("Refresh");
		
		start.addActionListener(this);
		resume.addActionListener(this);
		pause.addActionListener(this);
		scan.addActionListener(this);
		refresh.addActionListener(this);
		
		this.start.setBackground(Color.lightGray);
		this.resume.setBackground(Color.lightGray);
		this.pause.setBackground(Color.lightGray);
		
		this.pd = pd;
		this.md = mainDisplay;
	}
	
	public ProcessData getPd() {
		return pd;
	}

	public void setPd(ProcessData pd) {
		this.pd = pd;
	}

	public JButton getStart() {
		return start;
	}

	public JButton getScan()
	{
		return scan;
	}
	
	public JButton getResume() {
		return resume;
	}
	
	public JButton getPause() {
		return pause;
	}
	
	public JButton getRefresh()
	{
		return refresh;
	}
	
	@SuppressWarnings("unused")
	private void readWavelengthData()
	{
		FileInputStream in = null;
        Scanner scan = null;
        int source, destination;
        int midX, midY;
        int counter = 0;
        
        //tries to open file to read
        try
        {
        	in = new FileInputStream("NodeData.xml");
        	scan = new Scanner(in);
        }
        catch (IOException e)
        {
        	System.out.println("Cannot open NodeData.xml");
        }
        
        //regex that parses input strings
        String ptrStr = "<edge source=\"(.*?)\" target=\"(.*)\"></edge>";
        Pattern p = Pattern.compile(ptrStr);
		Matcher m = null;
		
		//reads in data and parses
		while(scan.hasNextLine())
		{	
			String str = scan.nextLine();
			
			m = p.matcher(str);
			
			if(m.find())
			{
				source = Integer.parseInt(m.group(1).trim());
				destination = Integer.parseInt(m.group(2).trim());
				
				midX = (md.nlocate_list.getXCoor(source - 1) + md.nlocate_list.getXCoor(destination - 1)) / 2;
				midY = (md.nlocate_list.getYCoor(source - 1) + md.nlocate_list.getYCoor(destination - 1)) / 2;
				
				MainDisplay.wavelengthList.set(counter, new Wavelength(source, destination, midX, midY, md.getLinkSet().getLSList2()));
				counter ++;
			}
		}
	}
	
	private void scanForBlock()
	{
		int i, j, prevblocking_num = 0;
		long index = 0;
		byte[] b = null;
		String trafficData = "";
		long fp = 0;
		@SuppressWarnings("unused")
		boolean found = false;
		String buffer = null, dataLine;
		String[] bufferSplit, dataLineSplit, dataLineSplit2;
		
		/*if(ProcessData.blocking_num <= 0)
		{
			System.out.println("invalid blocking number");
		}
		else
		{*/
			try 
			{
				fp = ProcessData.raf.getFilePointer();

				/*do
				{*/
					ProcessData.raf.seek(fp - 2*ProcessData.BUFF_SIZE);
					b = new byte[2*ProcessData.BUFF_SIZE];
					ProcessData.raf.read(b);

					buffer = new String(b);
					bufferSplit = buffer.split("\n");

					for(i = 0; i < bufferSplit.length; i++)
					{
						dataLine = bufferSplit[i].replaceAll(", |,", " ");
						dataLineSplit = dataLine.split(" ");

						if(dataLineSplit[0].equals("[index]"))
						{
							/*if(found == true)
							{
								break;
							}*/

							for(j = 1; j < dataLineSplit.length; j++)
							{
								dataLineSplit2 = dataLineSplit[j].split("=");
								index = (long) Double.parseDouble(dataLineSplit2[1]);
								System.out.println("index: "+index);
							}
						}
						else if(dataLineSplit[0].equals("[network]"))
						{
							for(j = 1; j < dataLineSplit.length; j++)
							{
								dataLineSplit2 = dataLineSplit[j].split("=");
								if(dataLineSplit2[0].equals("blocking-num"))
								{
									prevblocking_num = Integer.parseInt(dataLineSplit2[1]);
									System.out.println("block: "+prevblocking_num);
								}
							}
						}
						else if(dataLineSplit[0].equals("[traffic]"))
						{
							trafficData += "index: "+index+"   blocking-num: "+prevblocking_num+"\n";
							trafficData += dataLine+"\n\n";
							found = true;
						}
					}
					fp = ProcessData.raf.getFilePointer();

				/*} while(found == false);*/

				new AlertWindow(trafficData);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		//}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("Start"))
		{	
			this.start.setSelected(true);
			this.start.setBackground(Color.orange);
			//this.stop.setBackground(Color.lightGray);
			this.resume.setBackground(Color.lightGray);
			this.pause.setBackground(Color.lightGray);
			
			this.pd = new ProcessData(md);
			//this.md.resetData();

			for(int i = 0; i < MainDisplay.nodeList.size(); i ++)
			{
				MainDisplay.nodeList.set(i, new Node(md.getNodeSet().getNSList2()));
			}

			MainDisplay.zoneManager.removeAllItems();
			
			Executors.newFixedThreadPool(1).execute(pd);
			
		}
		else if(e.getActionCommand().equals("Pause"))
		{
			this.pause.setSelected(true);
			this.start.setBackground(Color.lightGray);
			//this.stop.setBackground(Color.lightGray);
			this.resume.setBackground(Color.lightGray);
			this.pause.setBackground(Color.orange);
			pd.pausePD();
			//pd.suspend();
		}
		else if(e.getActionCommand().equals("Resume"))
		{
			this.resume.setSelected(true);
			this.start.setBackground(Color.lightGray);
			//this.stop.setBackground(Color.lightGray);
			this.resume.setBackground(Color.orange);
			this.pause.setBackground(Color.lightGray);
			this.pd.resumePD();
			//this.pd.resume();
		}
		else if(e.getActionCommand().equals("Scan"))
		{
			scanForBlock();
		}
		else if(e.getActionCommand().equals("Refresh"))
		{
			md.test();
		}
	}
}
