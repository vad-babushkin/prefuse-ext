package prefuse.demos.projektlp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Main extends JPanel implements ActionListener
{
	private JButton fileButton = null;
	private JLabel fileLabel = null;
	private JButton[] colorButtons = null;
	private String file_location = null;
	private JPanel topP = null;
	private JPanel bottomP = null;
	private JFileChooser fc = null;
	private DataSets ds = null;
	
	public Main()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setSize(800, 900);
		setMaximumSize(new Dimension(700, 800));
		
		fc = new JFileChooser();
		
		topP = new JPanel();
		topP.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Choose File Location "),
                BorderFactory.createEmptyBorder(0,0,0,0)));
		fileButton = new JButton("File");
		fileButton.addActionListener(this);
		fileLabel = new JLabel("");
		
		topP.add(fileButton);
		topP.add(fileLabel);
		
		bottomP = new JPanel();
		bottomP.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Node Colors "),
                BorderFactory.createEmptyBorder(0,0,0,0)));
		
		add(topP);
		//add(bottomP);
	}
	
	@SuppressWarnings("unused")
	private void setFileLocation()
	{
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
		fileLabel.setText("File: "+this.file_location);
	}
	
	@SuppressWarnings("unused")
	private void setColorButtons()
	{
		NodeSet ns = ds.getNodeSet();
		int size = ns.getNSList2().size();
		int i;
		
		colorButtons = new JButton[size];
		
		for(i = 0; i < size; i ++)
		{
			colorButtons[i] = new JButton(ns.getNSList2().get(i).toString());
		}
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{/*
		if(arg0.getActionCommand().equals("File"))
		{
			setFileLocation();
			ds = new DataSets();
			setColorButtons();
		}*/
	}
	
	public DataSets getDs() {
		return ds;
	}

	public static void main(String[] args) 
	{	
		FileChooser fc = new FileChooser();
		DataSets ds = new DataSets(fc.getFileLocation());
		@SuppressWarnings("unused")
		ColorChooser cc = new ColorChooser(fc.getFileLocation(), ds);
	}
}
