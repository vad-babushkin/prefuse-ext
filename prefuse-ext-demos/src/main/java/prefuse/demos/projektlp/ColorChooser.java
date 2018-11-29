package prefuse.demos.projektlp;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ColorChooser extends JPanel implements ActionListener
{
	String file_location = null;
	NodeSet ns = null;
	int size = 0;
	
	JPanel main = null;
	JPanel left = null;
	JPanel right = null;
	JLabel[] labelLeft = null;
    JButton[] buttonRight = null;
    JButton startButton = null;
    DataSets ds = null;
    JFrame frame = null;
    ArrayList<String> colorValues = null;
    
	public ColorChooser(String file_location, DataSets ds)
	{
		this.file_location = file_location;
		this.ds = ds;
		this.ns = ds.getNodeSet();
		this.size = ns.getNSList2().size();
		colorValues = new ArrayList<String>();
		int i = 0;
		String s = null;
		String[] s2 = null;
		readColorValues();
		
		main = new JPanel();
		JPanel left = new JPanel(new GridLayout(size, 1));
		JPanel right = new JPanel(new GridLayout(size, 1));
		labelLeft = new JLabel[size];
		buttonRight = new JButton[size];
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		
		for(i = 0; i < size; i ++)
		{
			labelLeft[i] = new JLabel(ns.getNSList2().get(i).toString());
			System.out.println("i "+ns.getNSList2().get(i));
			buttonRight[i] = new JButton(ns.getNSList2().get(i).toString());
			s = colorValues.get(i);
			s2 = s.split(",");
			buttonRight[i].setBackground(new Color(Integer.parseInt(s2[0]),Integer.parseInt(s2[1]),Integer.parseInt(s2[2])));
			buttonRight[i].addActionListener(this);
			
			left.add(labelLeft[i]);
			right.add(buttonRight[i]);
		}
		//main.add(left);
		main.add(right);
		main.add(startButton);
		
		//Create and set up the window.
        frame = new JFrame("Color Chooser");
        frame.setLocation(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Add content to the window.
        frame.add(main);
        //Display the window.
        frame.pack();
        frame.setVisible(true);
	}

	private void readColorValues()
	{
		FileInputStream colorStream;
		String strLine;
		BufferedReader br;
		
		try 
		{
			colorStream = new FileInputStream("Colors.txt");
			DataInputStream in = new DataInputStream(colorStream);
	        br = new BufferedReader(new InputStreamReader(in));
	        
	        while ((strLine = br.readLine()) != null)   
	        {
	            colorValues.add(strLine);
	        }
	        in.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getActionCommand().equals("Start"))
		{
			try
			{
				// Create file 
				FileWriter fstream = new FileWriter("Colors.txt");
				BufferedWriter out = new BufferedWriter(fstream);
				for(int i = 0; i < this.size; i ++)
				{
					String s = buttonRight[i].getBackground().getRed()+","+
					buttonRight[i].getBackground().getGreen()+","+
					buttonRight[i].getBackground().getBlue();
					
					out.write(s);
					out.newLine();
				}
				out.close();
			}
			catch (Exception e)
			{
				System.err.println("Error: " + e.getMessage());
			}
	            
			@SuppressWarnings("unused")
			MainDisplay md = new MainDisplay(this.file_location, this.ds);
			this.frame.dispose();
		}
		else if(arg0.getActionCommand().compareTo("Start") != 0)
		{
			for(int i = 0; i < size; i ++)
			{
				if(buttonRight[i].getText().equals(arg0.getActionCommand()))
				{
					@SuppressWarnings("unused")
					Colors c = new Colors(buttonRight[i]);
				}
			}
		}
	}
}
