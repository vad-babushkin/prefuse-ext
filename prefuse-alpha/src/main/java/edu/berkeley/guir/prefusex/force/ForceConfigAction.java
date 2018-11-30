package edu.berkeley.guir.prefusex.force;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Brings up a dialog allowing users to configure a force simulation.
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
 */
public class ForceConfigAction extends AbstractAction {

    private JDialog dialog;
    
    public ForceConfigAction(JFrame frame, ForceSimulator fsim) {
        dialog = new JDialog(frame, false);
        dialog.setTitle("Configure Force Simulator");
        JPanel forcePanel = new ForcePanel(fsim);
        dialog.getContentPane().add(forcePanel);
        dialog.pack();
    } //
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        dialog.setVisible(true);
    } //

} // end of class ForceConfigAction
