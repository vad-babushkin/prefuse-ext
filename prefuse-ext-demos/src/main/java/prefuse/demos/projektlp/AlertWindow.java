package prefuse.demos.projektlp;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class AlertWindow extends JFrame implements Runnable
{
	JTextArea textArea;
	JPanel panel = null;
	JScrollPane spane;
	String s;
	
	public AlertWindow(String s)
	{
		this.setTitle("Alert");
		this.setLocation(600, 400);
		this.setSize(400, 300);
		this.s = s;
		
		panel = new JPanel();
		textArea = new JTextArea(s);
		spane = new JScrollPane(textArea);
		spane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spane.setPreferredSize(new Dimension(400, 155));
		spane.setMinimumSize(new Dimension(10, 10));

	    
	    //panel.add(textArea);
	    panel.add(spane);
		this.add(panel);
		this.pack();
		
		showWindow();
	}
	
	private void showWindow()
	{
        this.setVisible(true);
	}
	
	public void closeWindow()
	{
		this.setVisible(false);
	}

	public void setText(long d)
	{
		textArea.setText(this.s+"\n"+d);
	}
	
	public void run() 
	{
		this.showWindow();
	}
}
