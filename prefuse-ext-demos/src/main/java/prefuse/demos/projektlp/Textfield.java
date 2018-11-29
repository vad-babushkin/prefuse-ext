package prefuse.demos.projektlp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

public class Textfield implements ActionListener
{
	public static JTextField textField;

	public Textfield()
	{
		//textField = new JTextField(String.valueOf(ProcessData.readRate));
        textField.addActionListener(this);
	}
	
	public JTextField getTextField()
	{
		return Textfield.textField;
	}
	
	public void actionPerformed(ActionEvent evt) 
	{
		String text = textField.getText();
		System.out.println(text);
	}
}
