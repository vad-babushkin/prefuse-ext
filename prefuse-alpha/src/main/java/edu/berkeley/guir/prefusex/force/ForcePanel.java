//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefusex.force;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ForcePanel extends JPanel {
	private ForcePanel.ForceConstantAction action = new ForcePanel.ForceConstantAction();
	private ForceSimulator fsim;

	public ForcePanel(ForceSimulator var1) {
		this.fsim = var1;
		this.setBackground(Color.WHITE);
		this.initUI();
	}

	private void initUI() {
		this.setLayout(new BoxLayout(this, 1));
		Force[] var1 = this.fsim.getForces();

		for (int var2 = 0; var2 < var1.length; ++var2) {
			Force var3 = var1[var2];
			Box var4 = new Box(1);

			for (int var5 = 0; var5 < var3.getParameterCount(); ++var5) {
				var4.add(this.createField(var3, var5));
			}

			String var6 = var3.getClass().getName();
			var6 = var6.substring(var6.lastIndexOf(".") + 1);
			var4.setBorder(BorderFactory.createTitledBorder(var6));
			this.add(var4);
		}

		this.add(Box.createVerticalGlue());
	}

	private Box createField(Force var1, int var2) {
		Box var3 = new Box(0);
		float var4 = var1.getParameter(var2);
		JLabel var5 = new JLabel(var1.getParameterName(var2));
		var5.setPreferredSize(new Dimension(100, 20));
		var5.setMaximumSize(new Dimension(100, 20));
		JTextField var6 = new JTextField(String.valueOf(var4));
		var6.setPreferredSize(new Dimension(200, 20));
		var6.setMaximumSize(new Dimension(200, 20));
		var6.putClientProperty("force", var1);
		var6.putClientProperty("param", new Integer(var2));
		var6.addActionListener(this.action);
		var3.add(var5);
		var3.add(Box.createHorizontalStrut(10));
		var3.add(Box.createHorizontalGlue());
		var3.add(var6);
		var3.setPreferredSize(new Dimension(300, 30));
		var3.setMaximumSize(new Dimension(300, 30));
		return var3;
	}

	private class ForceConstantAction extends AbstractAction {
		private ForceConstantAction() {
		}

		public void actionPerformed(ActionEvent var1) {
			JTextField var2 = (JTextField) var1.getSource();
			float var3 = Float.parseFloat(var2.getText());
			Force var4 = (Force) var2.getClientProperty("force");
			Integer var5 = (Integer) var2.getClientProperty("param");
			var4.setParameter(var5, var3);
		}
	}
}
