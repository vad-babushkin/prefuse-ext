package prefuse.demos.projektlp;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ProgressBar extends JPanel implements Runnable
{
	private JLabel progress;
	private String progressData;
	
	public ProgressBar()
	{
		progress = new JLabel();
		progressData = "";
		
		setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Current Index "),
                BorderFactory.createEmptyBorder(0,0,0,0)));

		add(progress);
	}
	
	public void setProgressData(long index)
	{		
		progressData = String.valueOf(index);
	}

	public void run() 
	{
		progress.setText(progressData+" / 170000");
	}
}
