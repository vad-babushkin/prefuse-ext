//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package edu.berkeley.guir.prefuse.util;

import edu.berkeley.guir.prefuse.FocusManager;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.event.ItemRegistryListener;
import edu.berkeley.guir.prefuse.focus.FocusSet;
import edu.berkeley.guir.prefuse.focus.KeywordSearchFocusSet;
import edu.berkeley.guir.prefuse.graph.Entity;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class KeywordSearchPanel extends JPanel implements DocumentListener, ActionListener {
	private KeywordSearchFocusSet searcher;
	private FocusSet focus;
	private JTextField queryF = new JTextField(15);
	private JLabel resultL = new JLabel();
	private JLabel matchL = new JLabel();
	private JLabel searchL = new JLabel("search >> ");
	private KeywordSearchPanel.IconButton upArrow = new KeywordSearchPanel.IconButton(new KeywordSearchPanel.ArrowIcon(0), new KeywordSearchPanel.ArrowIcon(1));
	private KeywordSearchPanel.IconButton downArrow = new KeywordSearchPanel.IconButton(new KeywordSearchPanel.ArrowIcon(2), new KeywordSearchPanel.ArrowIcon(3));
	private String[] searchAttr;
	private Entity[] m_results;
	private int m_curResult;

	public KeywordSearchPanel(String[] var1, ItemRegistry var2) {
		this.searchAttr = var1;
		FocusManager var3 = var2.getFocusManager();
		this.focus = var3.getDefaultFocusSet();
		FocusSet var4 = var2.getFocusManager().getFocusSet("search");
		if (var4 != null) {
			if (!(var4 instanceof KeywordSearchFocusSet)) {
				throw new IllegalStateException("Search focus set not instance of KeywordSearchFocusSet!");
			}

			this.searcher = (KeywordSearchFocusSet)var4;
		} else {
			this.searcher = new KeywordSearchFocusSet();
			var3.putFocusSet("search", this.searcher);
		}

		this.init(var2);
	}

	public KeywordSearchPanel(String[] var1, ItemRegistry var2, KeywordSearchFocusSet var3, FocusSet var4) {
		this.searchAttr = var1;
		this.searcher = var3;
		this.focus = var4;
		this.init(var2);
	}

	private void init(ItemRegistry var1) {
		var1.addItemRegistryListener(new ItemRegistryListener() {
			public void registryItemAdded(VisualItem var1) {
				if (var1 instanceof NodeItem) {
					for(int var2 = 0; var2 < KeywordSearchPanel.this.searchAttr.length; ++var2) {
						KeywordSearchPanel.this.searcher.index(var1.getEntity(), KeywordSearchPanel.this.searchAttr[var2]);
					}

					KeywordSearchPanel.this.searchUpdate();
				}
			}

			public void registryItemRemoved(VisualItem var1) {
				if (var1 instanceof NodeItem) {
					for(int var2 = 0; var2 < KeywordSearchPanel.this.searchAttr.length; ++var2) {
						KeywordSearchPanel.this.searcher.remove(var1.getEntity(), KeywordSearchPanel.this.searchAttr[var2]);
					}

					KeywordSearchPanel.this.searchUpdate();
				}
			}
		});
		this.queryF.getDocument().addDocumentListener(this);
		this.queryF.setMaximumSize(new Dimension(100, 20));
		this.upArrow.addActionListener(this);
		this.upArrow.setEnabled(false);
		this.downArrow.addActionListener(this);
		this.downArrow.setEnabled(false);
		this.matchL.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent var1) {
				if (KeywordSearchPanel.this.matchL.getText().length() > 0) {
					KeywordSearchPanel.this.matchL.setCursor(new Cursor(12));
				}

			}

			public void mouseExited(MouseEvent var1) {
				if (KeywordSearchPanel.this.matchL.getText().length() > 0) {
					KeywordSearchPanel.this.matchL.setCursor(new Cursor(0));
				}

			}

			public void mouseClicked(MouseEvent var1) {
				if (KeywordSearchPanel.this.matchL.getText().length() > 0) {
					KeywordSearchPanel.this.focus.set(KeywordSearchPanel.this.m_results[KeywordSearchPanel.this.m_curResult]);
				}

			}
		});
		this.setBackground(Color.WHITE);
		this.initUI();
	}

	private void initUI() {
		this.setLayout(new BoxLayout(this, 0));
		Box var1 = new Box(0);
		var1.add(this.resultL);
		var1.add(Box.createHorizontalStrut(5));
		var1.add(Box.createHorizontalGlue());
		var1.add(this.matchL);
		var1.add(Box.createHorizontalStrut(5));
		var1.add(this.downArrow);
		var1.add(this.upArrow);
		var1.add(Box.createHorizontalStrut(5));
		var1.add(this.searchL);
		var1.add(this.queryF);
		this.add(var1);
	}

	private void searchUpdate() {
		String var1 = this.queryF.getText();
		if (var1.length() == 0) {
			this.searcher.clear();
			this.resultL.setText("");
			this.matchL.setText("");
			this.downArrow.setEnabled(false);
			this.upArrow.setEnabled(false);
			this.m_results = null;
		} else {
			this.searcher.search(var1);
			int var2 = this.searcher.size();
			this.resultL.setText(var2 + " match" + (var2 == 1 ? "" : "es"));
			this.m_results = new Entity[var2];
			Iterator var3 = this.searcher.iterator();

			for(int var4 = 0; var3.hasNext(); ++var4) {
				this.m_results[var4] = (Entity)var3.next();
			}

			if (var2 > 0) {
				String var5 = "name";
				this.matchL.setText("1/" + var2 + ": " + this.m_results[0].getAttribute(var5));
				this.downArrow.setEnabled(true);
				this.upArrow.setEnabled(true);
			} else {
				this.matchL.setText("");
				this.downArrow.setEnabled(false);
				this.upArrow.setEnabled(false);
			}

			this.m_curResult = 0;
		}

		this.validate();
	}

	public void setBackground(Color var1) {
		super.setBackground(var1);
		if (this.queryF != null) {
			this.queryF.setBackground(var1);
		}

		if (this.resultL != null) {
			this.resultL.setBackground(var1);
		}

		if (this.matchL != null) {
			this.matchL.setBackground(var1);
		}

		if (this.searchL != null) {
			this.searchL.setBackground(var1);
		}

		if (this.upArrow != null) {
			this.upArrow.setBackground(var1);
		}

		if (this.downArrow != null) {
			this.downArrow.setBackground(var1);
		}

	}

	public void setForeground(Color var1) {
		super.setForeground(var1);
		if (this.queryF != null) {
			this.queryF.setForeground(var1);
			this.queryF.setCaretColor(var1);
		}

		if (this.resultL != null) {
			this.resultL.setForeground(var1);
		}

		if (this.matchL != null) {
			this.matchL.setForeground(var1);
		}

		if (this.searchL != null) {
			this.searchL.setForeground(var1);
		}

		if (this.upArrow != null) {
			this.upArrow.setForeground(var1);
		}

		if (this.downArrow != null) {
			this.downArrow.setForeground(var1);
		}

	}

	public void changedUpdate(DocumentEvent var1) {
		this.searchUpdate();
	}

	public void insertUpdate(DocumentEvent var1) {
		this.searchUpdate();
	}

	public void removeUpdate(DocumentEvent var1) {
		this.searchUpdate();
	}

	public void actionPerformed(ActionEvent var1) {
		if (this.matchL.getText().length() != 0) {
			if (var1.getSource() == this.downArrow) {
				this.m_curResult = (this.m_curResult + 1) % this.m_results.length;
			} else if (var1.getSource() == this.upArrow) {
				this.m_curResult = (this.m_curResult - 1) % this.m_results.length;
				if (this.m_curResult < 0) {
					this.m_curResult += this.m_results.length;
				}
			}

			String var2 = "name";
			this.matchL.setText(this.m_curResult + 1 + "/" + this.m_results.length + ": " + this.m_results[this.m_curResult].getAttribute(var2));
			this.validate();
			this.repaint();
		}
	}

	public class ArrowIcon implements Icon {
		public static final int UP = 0;
		public static final int UP_DEPRESSED = 1;
		public static final int DOWN = 2;
		public static final int DOWN_DEPRESSED = 3;
		public static final int DISABLED = 4;
		private int type;

		public ArrowIcon(int var2) {
			this.type = var2;
		}

		public int getIconHeight() {
			return 11;
		}

		public int getIconWidth() {
			return 11;
		}

		public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
			if (this.type < 4) {
				Polygon var5 = new Polygon();
				int var6 = this.getIconWidth();
				int var7 = this.getIconHeight();
				if (this.type < 2) {
					var5.addPoint(var3, var4 + var7 - 1);
					var5.addPoint(var3 + var6 - 1, var4 + var7 - 1);
					var5.addPoint(var3 + (var6 - 1) / 2, var4);
					var5.addPoint(var3, var4 + var7);
				} else {
					var5.addPoint(var3, var4);
					var5.addPoint(var3 + var6 - 1, var4);
					var5.addPoint(var3 + (var6 - 1) / 2, var4 + var7 - 1);
					var5.addPoint(var3, var4);
				}

				var2.setColor(this.type % 2 != 0 ? Color.LIGHT_GRAY : KeywordSearchPanel.this.getForeground());
				var2.fillPolygon(var5);
				var2.setColor(Color.BLACK);
				var2.drawPolygon(var5);
			}
		}
	}

	public class IconButton extends JButton {
		public IconButton(Icon var2, Icon var3) {
			super(var2);
			if (var2.getIconWidth() == var3.getIconWidth() && var3.getIconHeight() == var3.getIconHeight()) {
				this.setPressedIcon(var3);
				this.setDisabledIcon(KeywordSearchPanel.this.new ArrowIcon(4));
				this.setBorderPainted(false);
				this.setFocusPainted(false);
				this.setBackground(this.getBackground());
				Insets var4 = this.getMargin();
				var4.left = 0;
				var4.right = 0;
				this.setMargin(var4);
			} else {
				throw new IllegalArgumentException("Icons must have matching dimensions");
			}
		}
	}
}
