package prefuse.demos.projektlp;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class NetworkDataVisualizer extends JPanel implements Runnable
{
	JTextArea textArea;
	private String networkData;

	public NetworkDataVisualizer() 
	{
		textArea = new JTextArea();
		this.networkData = "";
		initializeGUI();
	}

	private void initializeGUI()
	{
		setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Network Data "),
                BorderFactory.createEmptyBorder(0,0,0,0)));
		add(textArea);
	}

	public void setData(StringBuffer networkData)
	{
		this.networkData = networkData.toString();
	}
	
	public void run() 
	{
		textArea.setText(networkData);
	}
}
