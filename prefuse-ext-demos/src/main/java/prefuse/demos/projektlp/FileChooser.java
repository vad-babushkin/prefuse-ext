package prefuse.demos.projektlp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class FileChooser extends JPanel
{
	private String file_location = "";
	JFileChooser fc = null;
	
	public FileChooser()
	{
		fc = new JFileChooser();
		if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fc.getSelectedFile();
			System.out.println(file.getPath());
            this.file_location = file.getAbsolutePath();

            try
            {
            	// Create file 
            	FileWriter fstream = new FileWriter("FileLocation.txt");
            	BufferedWriter out = new BufferedWriter(fstream);
            	out.write(this.file_location);
            	out.close();
            }
            catch (Exception e)
            {
            	System.err.println("Error: " + e.getMessage());
            }
		}
		else
		{
			try
			{
				FileInputStream fstream = new FileInputStream("FileLocation.txt");
				
				// Get the object of DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				
				//Read File Line By Line
				while ((strLine = br.readLine()) != null)   
				{
					// Print the content on the console
					System.out.println (strLine);
					this.file_location = strLine;
				}
				//Close the input stream
				in.close();
			}
			catch (Exception e)
			{
            	System.err.println("Error: " + e.getMessage());
			}
		}
	}
	
	public String getFileLocation()
	{
		return file_location;
	}
}
