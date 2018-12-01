package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis;

/**
 * @package cz.vutbr.fit.dudka.SGVis
 * Main package containing application's main window and main() function.
 * There is also Config class holding application's configuration.
 * @author Kamil Dudka <xdudka00@stud.fit.vutbr.cz>
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Config;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data.Statistics;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual.GraphDisplay;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual.GraphView;
import prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual.LookupWorker;

/**
 * Application main window.
 * This class also contains application's main() function.
 */
public class Main extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JMenuBar jJMenuBar = null;
	private JMenu visMenu = null;
	private JMenuItem menuItemZootmToFit = null;
	private JMenuItem menuItemNewVis = null;
	private JToolBar statusBar = null;
	private JLabel statusText = null;
	final private GraphView gview;
	private JMenuItem menuItemStatistics = null;
	private JMenu helpMenu = null;
	private JMenuItem menuItemHelp = null;

	/**
	 * @param gview View object to work with.
	 */
	private Main(GraphView gview) {
		super();
		this.gview = gview;
		initialize();
		gview.addStatusListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				GraphView gv = (GraphView) e.getSource();
				statusText.setText(gv.getStatus());
				statusText.setForeground(
						gv.isStatusError()?
								Color.RED:
								Color.BLACK
						);
				getStatusBar().invalidate();
			} 
		});
        getJContentPane().add(new GraphDisplay(gview));
        this.pack();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	/**
	 * This method initializes this.
	 * Auto-generated method by VE. 
	 * @return void
	 */
	private void initialize() {
		this.setSize(1024, 768);
		this.setJMenuBar(getJJMenuBar());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(480, 320));
		this.setContentPane(getJContentPane());
		this.setTitle(Config.APP_NAME);
	}

	/**
	 * This method initializes jContentPane.
	 * Auto-generated method by VE. 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getStatusBar(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jJMenuBar.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getVisMenu());
			jJMenuBar.add(getHelpMenu());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenu.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JMenu	
	 */
	private JMenu getVisMenu() {
		if (visMenu == null) {
			visMenu = new JMenu();
			visMenu.setText("Visualization");
			visMenu.setMnemonic(KeyEvent.VK_V);
			visMenu.add(getMenuItemNewVis());
			visMenu.addSeparator();
			visMenu.add(getMenuItemZootmToFit());
			visMenu.add(getMenuItemStatistics());
		}
		return visMenu;
	}

	/**
	 * This method initializes jMenuItem.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMenuItemZootmToFit() {
		if (menuItemZootmToFit == null) {
			menuItemZootmToFit = new JMenuItem();
			menuItemZootmToFit.setText("Zoom to fit");
			menuItemZootmToFit.setMnemonic(KeyEvent.VK_Z);
			menuItemZootmToFit.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					gview.zoomToFit();
				}
			});
		}
		return menuItemZootmToFit;
	}

	/**
	 * This method initializes jMenuItem1.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMenuItemNewVis() {
		final Component obj = this;
		if (menuItemNewVis == null) {
			menuItemNewVis = new JMenuItem();
			menuItemNewVis.setText("New visualization");
			menuItemNewVis.setMnemonic(KeyEvent.VK_N);
			menuItemNewVis.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (0!=gview.getNodeCount() &&
							JOptionPane.OK_OPTION != JOptionPane.showConfirmDialog(
								obj,
								"This will erase current visualization.",
								Config.APP_NAME,
								JOptionPane.OK_CANCEL_OPTION))
						return;
					String urlString = JOptionPane.showInputDialog(
							obj,
							"Type URL to look for:",
							/*Config.APP_NAME,
							JOptionPane.QUESTION_MESSAGE,
							null, null,*/
							Config.DEF_LOOKUP_FOR);
					if (null==urlString)
						return;
					try {
						URL url = new URL(urlString);
						gview.clear();
						gview.lookup(url);
					} catch (MalformedURLException e1) {
						JOptionPane.showMessageDialog(
								obj,
								"Invalid URL",
								Config.APP_NAME,
								JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}
				}
			});
		}
		return menuItemNewVis;
	}

	/**
	 * This method initializes jToolBar.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getStatusBar() {
		if (statusBar == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setRows(1);
			statusText = new JLabel();
			statusText.setText(" ");
			statusText.setFont(new Font("Dialog", Font.BOLD, 18));
			statusBar = new JToolBar();
			statusBar.setLayout(gridLayout);
			statusBar.setFloatable(false);
			statusBar.add(statusText);
		}
		return statusBar;
	}

	/**
	 * This method initializes menuItemStatistics.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMenuItemStatistics() {
		final Component obj = this;
		if (menuItemStatistics == null) {
			menuItemStatistics = new JMenuItem();
			menuItemStatistics.setText("Statistics");
			menuItemStatistics.setMnemonic(KeyEvent.VK_S);
			menuItemStatistics.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JPanel panel = new JPanel(new GridLayout(3,0));
					panel.add(createGlobalStats());
					panel.add(createViewStats());
					panel.add(createLookupStats());
					JOptionPane pane = new JOptionPane(panel);
					JDialog dialog = pane.createDialog(obj, Config.APP_NAME + " - statistics");
					dialog.setVisible(true);
				}
				private JPanel createGlobalStats() {
					final int FIELD_CNT = 3;
					String names[] = new String[FIELD_CNT];
					String values[] = new String[FIELD_CNT];
					Statistics stats = gview.getStorage().getStatistics();
					names[0] = "Hosts";		values[0] = String.valueOf(stats.getHosts());
					names[1] = "URLs";		values[1] = String.valueOf(stats.getUrls());
					names[2] = "Relations";	values[2] = String.valueOf(stats.getRels());
					return createBox(names, values, "Downloaded data statistics");
				}
				private JPanel createViewStats() {
					final int FIELD_CNT = 3;
					String names[] = new String[FIELD_CNT];
					String values[] = new String[FIELD_CNT];
					names[0] = "Nodes";		values[0] = String.valueOf(gview.getNodeCount());
					names[1] = "Edges";		values[1] = String.valueOf(gview.getEdgeCount());
					names[2] = null;		values[2] = " ";
					return createBox(names, values, "View statistics");
				}
				private JPanel createLookupStats() {
					final int FIELD_CNT = 3;
					String names[] = new String[FIELD_CNT];
					String values[] = new String[FIELD_CNT];
					//names[0] = "Total lookups";		values[0] = String.valueOf(LookupWorker.getTotalCount());
					names[0] = "Pending";	values[0] = String.valueOf(LookupWorker.getActiveCount());
					names[1] = "Finished";	values[1] = String.valueOf(LookupWorker.getSuccessCount());
					names[2] = "Failed";	values[2] = String.valueOf(LookupWorker.getFailedCount());
					return createBox(names, values, "Lookup statistics");
				}
				private JPanel createBox(String[] names, String[] values, String title) {
					final int CNT = Math.min(names.length,values.length);
					JPanel panel = new JPanel(new GridLayout(CNT,2));
					//panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
					panel.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
					for(int i=0; i<CNT; i++) {
						String nameText = names[i];
						if (null==nameText)
							nameText = "";
						else
							nameText += ":";
						JLabel name = new JLabel(nameText);
						panel.add(name);
						JLabel value = new JLabel(values[i]);
						value.setHorizontalAlignment(JLabel.RIGHT);
						panel.add(value);
					}
					return panel;
				}
			});
		}
		return menuItemStatistics;
	}

	/**
	 * This method initializes helpMenu.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.add(getMenuItemHelp());
			helpMenu.setMnemonic(KeyEvent.VK_H);
		}
		return helpMenu;
	}

	/**
	 * This method initializes menuItemHelp.	
	 * Auto-generated method by VE. 
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMenuItemHelp() {
		final Component obj = this;
		if (menuItemHelp == null) {
			menuItemHelp = new JMenuItem();
			menuItemHelp.setText("Quick help");
			menuItemHelp.setMnemonic(KeyEvent.VK_H);
			menuItemHelp.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JPanel panel = new JPanel(new GridLayout(2,0));
					panel.add(createNodeHelp());
					panel.add(createVisHelp());
					JOptionPane pane = new JOptionPane(panel);
					JDialog dialog = pane.createDialog(obj, Config.APP_NAME + " - quick help");
					dialog.setModal(false);
					dialog.setVisible(true);
				}
				private JPanel createNodeHelp() {
					final int FIELD_CNT = 3;
					String names[] = new String[FIELD_CNT];
					String values[] = new String[FIELD_CNT];
					names[0] = "Arrange visualiztion";	values[0] = "mouse left click on node";
					names[1] = "Move node";				values[1] = "drag&drop (mouse left button)";
					names[2] = "Node options";			values[2] = "mouse right click on node";
					return createBox(names, values, "Node oprations");
				}
				private JPanel createVisHelp() {
					final int FIELD_CNT = 3;
					String names[] = new String[FIELD_CNT];
					String values[] = new String[FIELD_CNT];
					names[0] = "Move visualization";	values[0] = "drag&drop (mouse left button not over node)";
					names[1] = "Zoom";					values[1] = "mouse wheel";
					names[2] = "Zoom to fit";			values[2] = "mouse right click (not over node)";
					return createBox(names, values, "Visualization operations");
				}
				private JPanel createBox(String[] names, String[] values, String title) {
					final int CNT = Math.min(names.length,values.length);
					JPanel panel = new JPanel(new GridLayout(CNT,2));
					panel.setBorder(BorderFactory.createTitledBorder(null, title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Dialog", Font.BOLD, 12), new Color(51, 51, 51)));
					for(int i=0; i<CNT; i++) {
						String nameText = names[i];
						panel.add(new JLabel(nameText));
						panel.add(new JLabel(values[i]));
					}
					return panel;
				}
			});
		}
		return menuItemHelp;
	}

	/**
	 * Application entry point.
	 * @param args Command-line arguments are ignored.
	 */
	public static void main(String[] args) {
		try {
			GraphView gv = new GraphView();
			Main mainFrame = new Main(gv);
			mainFrame.setVisible(true);
			gv.lookup(new URL(Config.DEF_LOOKUP_FOR));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
