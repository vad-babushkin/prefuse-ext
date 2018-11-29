package prefuse.demos.projektlp;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildDataSets 
{	
	String file;
	
	public BuildDataSets(String file)
	{
		this.file = file;
	}
	
	public String[] readData(String regex) 
	{	 
		RandomAccessFile raf;
		int counter = 400000;
		Pattern regexPtr = Pattern.compile(regex);
		Matcher matcher = null;
		String data[] = null;
		
		try 
		{
			raf = new RandomAccessFile(this.file, "r");			
	    	raf.seek(counter);
			String dataIns[] = raf.readUTF().split("\n");
		
			for(int i = 0; i < dataIns.length; i ++)
    		{
				matcher = regexPtr.matcher(dataIns[i]);
				if(matcher.find())
    			{	
					data = matcher.group(1).split(",");
					i = dataIns.length;
    			}
    		}		
			raf.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return data;		
	}

	public ArrayList<String> parseData(String[] data)
	{
		ArrayList<String> temp = new ArrayList<String>();
		String test = "(.*)=(.*)";
		Pattern ptrTest = Pattern.compile(test);
		Matcher mTest = null;
		
		for(int i = 0; i < data.length; i ++)
		{
			data[i] = data[i].trim();
			mTest = ptrTest.matcher(data[i]);
			if(mTest.find())
			{
				temp.add(mTest.group(1));
			}
		}
		
		return temp;
	}

	public static void main(String[] args)
	{
		BuildDataSets bds = new BuildDataSets("");
		String d[] = bds.readData("\\[node\\] (.*)");
		ArrayList<String> s = bds.parseData(d);
		for(int i = 0; i < s.size(); i ++)
		{
			System.out.println(s.get(i));
		}
	}
}
