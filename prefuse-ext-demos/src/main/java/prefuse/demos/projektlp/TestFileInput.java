package prefuse.demos.projektlp;

import java.io.BufferedReader;
import java.io.FileReader;

public class TestFileInput 
{
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		FileReader in;
		BufferedReader br;
		String strLine;
		String []temp;
		int i;
		
		try 
		{
			in = new FileReader("testing.txt");
	        br = new BufferedReader(in);
	        
	        while ((strLine = br.readLine()) != null)   
	        {
	        	strLine = strLine.replaceAll(", |,", " ");
	        	System.out.println(strLine);
	        	temp = strLine.split(" ");
	        	if(temp[0].equals("[network]"))
	        	{
	        		for(i = 0; i < temp.length; i++)
	        		{
	        			System.out.println(i+": "+temp[i]);
	        		}
	        	}
	        	/*else if(temp[0].equals("[node]"))
	        	{
	        		for(i = 0; i < temp.length; i++)
	        		{
	        			System.out.println(i+": "+temp[i]);
	        		}
	        	}*/
	        }
	        in.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}
