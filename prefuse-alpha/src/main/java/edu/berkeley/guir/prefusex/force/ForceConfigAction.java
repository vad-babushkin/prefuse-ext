package edu.berkeley.guir.prefusex.force;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ForceConfigAction
		extends AbstractAction {
	private JDialog dialog;

	public ForceConfigAction(JFrame paramJFrame, ForceSimulator paramForceSimulator) {
		this.dialog = new JDialog(paramJFrame, false);
		this.dialog.setTitle("Configure Force Simulator");
		ForcePanel localForcePanel = new ForcePanel(paramForceSimulator);
		this.dialog.getContentPane().add(localForcePanel);
		this.dialog.pack();
	}

	public void actionPerformed(ActionEvent paramActionEvent) {
		this.dialog.setVisible(true);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefusex/force/ForceConfigAction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */