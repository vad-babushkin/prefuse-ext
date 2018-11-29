package prefuse.demos.projektlp;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class NodeMiniWindow extends JFrame implements Runnable
{
	JFrame mainFrm;
	JPanel panel;
	JPanel panelLeft;
	JPanel panelRight;
    JLabel[] labelLeft;
    JLabel[] labelRight;
    Double counter;
    ArrayList<NodeData> ndList;
    char c;
    
	public NodeMiniWindow(int ele, char c, int x, int y)
	{
		counter = 0.0;
		mainFrm = new JFrame("Node "+(ele+1));
		panel = new JPanel();
		panelLeft = new JPanel();
		panelRight = new JPanel();
		mainFrm.setSize(new Dimension(250, 110));
		panel.setPreferredSize(new Dimension(250, 110));
		mainFrm.setAlwaysOnTop(true);
		mainFrm.setLocation(x, y);
		ndList = MainDisplay.nodeList.get(ele).getNDList();
		this.c = c;
		
		if(this.c == 'E')
		{
			panelLeft.setLayout(new GridLayout(ndList.size(), 1));
			panelRight.setLayout(new GridLayout(ndList.size(), 1));

			labelLeft = new JLabel[ndList.size()];
			labelRight = new JLabel[ndList.size()];

			for(int i = 0; i < ndList.size(); i ++)
			{
				labelLeft[i] = new JLabel(ndList.get(i).getName()+":  ");
				labelRight[i] = new JLabel("0");
				panelLeft.add(labelLeft[i]);
				panelRight.add(labelRight[i]);
			}
		}
		else if(this.c == 'N')
		{
			panelLeft.setLayout(new GridLayout(ndList.size()-1, 1));
			panelRight.setLayout(new GridLayout(ndList.size()-1, 1));

			labelLeft = new JLabel[ndList.size()-1];
			labelRight = new JLabel[ndList.size()-1];
			
			for(int i = 1; i < ndList.size(); i ++)
			{
				labelLeft[i-1] = new JLabel(ndList.get(i).getName()+":  ");
				labelRight[i-1] = new JLabel("0");
				panelLeft.add(labelLeft[i-1]);
				panelRight.add(labelRight[i-1]);
			}	
		}
		
		panel.add(panelLeft);
		panel.add(panelRight);
		mainFrm.getContentPane().add(panel);
		mainFrm.pack();
        mainFrm.setVisible(true);
	}

	public void run() 
	{
		if(this.c == 'E')
		{
			for(int i = 0; i < ndList.size(); i ++)
			{
				counter = ndList.get(i).getData();
				labelRight[i].setText(counter.toString());
			}
		}
		else if(this.c == 'N')
		{
			for(int i = 1; i < ndList.size(); i ++)
			{
				counter = ndList.get(i).getData();
				labelRight[i-1].setText(counter.toString());
			}
		}
	}
}
