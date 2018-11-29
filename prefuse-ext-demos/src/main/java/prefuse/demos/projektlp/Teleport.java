package prefuse.demos.projektlp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Teleport extends JPanel implements ActionListener
{
	private JPanel main;
	private JPanel buttonP;
	private JTextField txtF;
	private JButton button;
	private JButton button2;
	
	MainDisplay md;
	AlertWindow aw;
	
	ProcessTeleport pt;
	JumpBackProcess jbp;
	
	Buttons b;
	long index = 0;
	int offsetCounter = 1;
	
	ExecutorService threadExecutor = null;
	
	public Teleport(MainDisplay md, Buttons buttons)
	{
		this.md = md;
		this.b = buttons;
		threadExecutor = Executors.newFixedThreadPool(2);
		aw = null;
		
		main = new JPanel();
		buttonP = new JPanel();
		buttonP.setLayout(new BoxLayout(buttonP, BoxLayout.Y_AXIS));
		
		txtF = new JTextField(10);
		
		button = new JButton("Go To Index");
		button.addActionListener(this);
		
		button2 = new JButton("Jump to prior hop");
		button2.addActionListener(this);
		
		buttonP.add(button);
		buttonP.add(button2);
		
		main.add(txtF);
		main.add(buttonP);
		
		setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(" Jump to Specific Index "),
                BorderFactory.createEmptyBorder(0,0,0,0)));
		add(main);
	}
	
	public void actionPerformed(ActionEvent e) 
	{
		
		if(e.getActionCommand().equals("Go To Index"))
		{
			if(txtF.getText().isEmpty())
			{
				aw = new AlertWindow("Please enter a number from 0.00 - 100.00.");
				this.threadExecutor.execute(aw);
			}
			else if(!b.getPause().isSelected())
			{
				aw = new AlertWindow("Please press the \"Pause\" button.");
				this.threadExecutor.execute(aw);
			}
			else
			{
				aw = new AlertWindow("Processing...");
				pt = new ProcessTeleport(md, b, Double.parseDouble(txtF.getText()), aw);
				this.threadExecutor.execute(aw);
				this.threadExecutor.execute(pt);
			}
		}
		else if(e.getActionCommand().equals("Jump to prior hop"))
		{
			jbp = new JumpBackProcess(md, b);
			this.threadExecutor.execute(jbp);
		}
	}
}
