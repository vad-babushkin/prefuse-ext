//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package prefuse;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.net.URL;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class PrefuseAppLauncher extends JFrame {
	private PrefuseAppLauncher.AppDescription[] desc;
	private Action launcher = new PrefuseAppLauncher.LaunchAction();

	public static void main(String[] args) {
		setLookAndFeel();
		PrefuseAppLauncher.AppDescription[] desc = getAppDescriptions();
		JFrame f = new PrefuseAppLauncher(desc);
		f.pack();
		f.setVisible(true);
	}

	public static PrefuseAppLauncher.AppDescription[] getAppDescriptions() {
		PrefuseAppLauncher.AppDescription[] desc = new PrefuseAppLauncher.AppDescription[]{new PrefuseAppLauncher.AppDescription("Radial Graph Explorer", "radial.png", "prefuse.radialexplorer.RadialGraphExplorer", "/friendster.xml"), new PrefuseAppLauncher.AppDescription("Graph Editor", "editor.png", "prefuse.grapheditor.GraphEditor", (String)null), new PrefuseAppLauncher.AppDescription("Data Mountain", "datamountain.png", "prefuse.datamountain.DataMountain", "/data.xml"), new PrefuseAppLauncher.AppDescription("Hyperbolic Tree", "hyperbolic.png", "prefuse.hyperbolictree.HyperbolicTree", "/chitest.hdir")};
		return desc;
	}

	public static final void setLookAndFeel() {
		try {
			String laf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(laf);
		} catch (Exception var1) {
			;
		}

	}

	public PrefuseAppLauncher(PrefuseAppLauncher.AppDescription[] desc) {
		super("prefuse application launcher");
		this.desc = desc;
		this.initFrame();
		this.setDefaultCloseOperation(3);
	}

	public void initFrame() {
		Container c = this.getContentPane();
		c.setLayout(new BoxLayout(c, 1));
		int numcols = 2;

		for(int i = 0; i < this.desc.length; i += numcols) {
			c.add(Box.createVerticalStrut(5));
			c.add(Box.createVerticalGlue());
			Box b = new Box(0);

			for(int j = 0; j < numcols && i + j < this.desc.length; ++j) {
				b.add(Box.createHorizontalStrut(5));
				b.add(Box.createHorizontalGlue());
				JButton but = new JButton("An Application");
				but.setFont(new Font("Verdana", 1, 18));
				but.putClientProperty("app", this.desc[i + j]);
				but.setAction(this.launcher);
				but.setText(this.desc[i + j].name);
				if (this.desc[i + j].image != null) {
					URL url = this.getClass().getResource(this.desc[i + j].image);
					but.setIcon(new ImageIcon(url));
				}

				but.setVerticalTextPosition(3);
				but.setHorizontalTextPosition(0);
				b.add(but);
			}

			b.add(Box.createHorizontalStrut(5));
			b.add(Box.createHorizontalGlue());
			c.add(b);
		}

		c.add(Box.createVerticalStrut(5));
		c.add(Box.createVerticalGlue());
		this.validate();
	}

	public static class AppDescription {
		public static final int FRAME = 0;
		public static final int APP = 1;
		public int type;
		public String name;
		public String arg;
		public String image;
		public String classname;

		public AppDescription(int type, String name, String image, String classname, String arg) {
			this.type = type;
			this.name = name;
			this.image = image;
			this.classname = classname;
			this.arg = arg;
		}

		public AppDescription(String name, String image, String classname, String arg) {
			this(0, name, image, classname, arg);
		}

		public void launch() {
			if (this.type == 0) {
				try {
					Object o;
					if (this.arg == null) {
						o = Class.forName(this.classname).newInstance();
					} else {
						Constructor c = Class.forName(this.classname).getConstructor(String.class);
						o = c.newInstance(this.arg);
					}

					if (o instanceof JFrame) {
						JFrame f = (JFrame)o;
						f.setDefaultCloseOperation(1);
					}
				} catch (Exception var5) {
					var5.printStackTrace();
					JOptionPane.showMessageDialog((Component)null, "Sorry, could not load application!", "Error", 0);
				}
			} else {
				try {
					Runtime rt = Runtime.getRuntime();
					rt.exec(this.classname);
				} catch (Exception var3) {
					var3.printStackTrace();
					JOptionPane.showMessageDialog((Component)null, "Sorry, could not load application!", "Error", 0);
				}
			}

		}
	}

	public class LaunchAction extends AbstractAction {
		public LaunchAction() {
		}

		public void actionPerformed(ActionEvent e) {
			JComponent jc = (JComponent)e.getSource();
			PrefuseAppLauncher.AppDescription d = (PrefuseAppLauncher.AppDescription)jc.getClientProperty("app");
			d.launch();
		}
	}
}
