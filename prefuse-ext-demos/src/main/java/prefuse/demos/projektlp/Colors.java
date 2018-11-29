package prefuse.demos.projektlp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Colors extends JPanel implements ActionListener
{
	protected JColorChooser tcc;
	private JFrame frame = null;
	private JButton cButton = null;
	private JComponent newContentPane = null;
	int r, g, b = 0;
	private JButton button = null;
	
	public Colors(JButton buttonRight)
	{
		super(new BorderLayout());
		this.button = buttonRight;
		cButton = new JButton("Select Color");
		cButton.addActionListener(this);
		add(cButton, BorderLayout.NORTH);
		
        //Set up color chooser for setting text color
        tcc = new JColorChooser();
        //tcc.getSelectionModel().addChangeListener(this);
        tcc.setBorder(BorderFactory.createTitledBorder("Colors"));
        add(tcc, BorderLayout.CENTER);
        
        frame = new JFrame("ColorChooserDemo");
        newContentPane = this;
        frame.setContentPane(newContentPane);
        //Display the window.
        frame.pack();
        frame.setVisible(true);

	}
	
	/*public void stateChanged(ChangeEvent arg0) 
	{
		Color newColor = tcc.getColor();
		System.out.println(;
	}*/

	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getActionCommand().equals("Select Color"))
		{
			this.r = tcc.getColor().getRed();
			this.g = tcc.getColor().getGreen();
			this.b = tcc.getColor().getBlue();
			this.button.setBackground(new Color(this.r, this.g, this.b));
			frame.dispose();
		}	
	}
}
