package prefuse.demos.projektlp;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class Slider extends JPanel implements ChangeListener
{
	private JSlider slider = null;
	
	public Slider()
	{
		setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Buffer Size 1MB - 10MB"),
                BorderFactory.createEmptyBorder(0,0,0,0)));

		slider = new JSlider(SwingConstants.HORIZONTAL, 1, 10, 1);
		slider.addChangeListener(this);
		
		slider.setMajorTickSpacing(2);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        //slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.BOLD, 10);
        slider.setFont(font);

		add(slider);
	}
	
	public void stateChanged(ChangeEvent e) 
	{
		JSlider source = (JSlider)e.getSource();
        if (!source.getValueIsAdjusting()) 
        {
        	synchronized(ProcessData.buffer)
        	{
        		ProcessData.BUFF_SIZE = (source.getValue()*source.getValue()) * 25000;
        	}
        }
	}

}
