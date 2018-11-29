package prefuse.demos.projektlp;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class EdgeMiniWindow extends JFrame implements Runnable
{
	JFrame mainFrm;
	JPanel panel;
	JPanel panelLeft;
	JPanel panelRight;
    JLabel[] labelLeft;
    JLabel[] labelRight;
    Double counter;
    ArrayList<WavelengthData> wvList;
    
    public EdgeMiniWindow(int ele, int x, int y)
    {
    	counter = 0.0;
		mainFrm = new JFrame("Edge");
		panel = new JPanel();
		panelLeft = new JPanel();
		panelRight = new JPanel();
		//mainFrm.setSize(new Dimension(250, 110));
		panel.setPreferredSize(new Dimension(170, 30));
		mainFrm.setAlwaysOnTop(true);
		mainFrm.setLocation(x, y);
		wvList = MainDisplay.wavelengthList.get(ele).getWdList();
		
		panelLeft.setLayout(new GridLayout(wvList.size()-1, 1));
		panelRight.setLayout(new GridLayout(wvList.size()-1, 1));

		labelLeft = new JLabel[wvList.size()-1];
		labelRight = new JLabel[wvList.size()-1];

		for(int i = 1; i < wvList.size(); i ++)
		{
			labelLeft[i-1] = new JLabel(wvList.get(i).getName()+":  ");
			labelRight[i-1] = new JLabel("0");
			panelLeft.add(labelLeft[i-1]);
			panelRight.add(labelRight[i-1]);
		}
		
		panel.add(panelLeft);
		panel.add(panelRight);
		mainFrm.getContentPane().add(panel);
		mainFrm.pack();
        mainFrm.setVisible(true);
    }
    
    public void run()
    {
    	for(int i = 1; i < wvList.size(); i ++)
		{
			counter = wvList.get(i).getData();
			labelRight[i-1].setText(counter.toString());
		}
    }
}
