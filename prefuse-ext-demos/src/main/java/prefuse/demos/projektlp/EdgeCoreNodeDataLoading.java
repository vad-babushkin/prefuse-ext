package prefuse.demos.projektlp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JPanel;


public class EdgeCoreNodeDataLoading extends JPanel implements ActionListener
{
	JPanel main = null;
	JPanel nodes[] = null;
	String f = null;
	
	public EdgeCoreNodeDataLoading(String file_location)
	{
		this.f = file_location;
		main = new JPanel();
		nodes = new JPanel[2];
	}
	
	public void actionPerformed(ActionEvent e) 
	{
	}
	
	public static void main(String[] args) 
	{
		
		try
		{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream("D:\\Documents and Settings\\Gundam\\Desktop\\URA Research\\Research Data\\April 27\\stats_load_8000-0.79.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine, strArray[];
			
			//Read File Line By Line
			while ((strLine = br.readLine()).isEmpty() == false)  
			{
				strArray = strLine.split(" ");
				if(strArray[0].equals("[node]"))
				{
					System.out.println(strArray[0]);
				}
			}
			//Close the input stream
			in.close();
		}
		catch (Exception e)
		{
			//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
