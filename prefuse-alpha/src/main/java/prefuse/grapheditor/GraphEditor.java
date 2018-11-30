package prefuse.grapheditor;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.EdgeItem;
import edu.berkeley.guir.prefuse.ItemRegistry;
import edu.berkeley.guir.prefuse.NodeItem;
import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.action.AbstractAction;
import edu.berkeley.guir.prefuse.action.ActionMap;
import edu.berkeley.guir.prefuse.action.RepaintAction;
import edu.berkeley.guir.prefuse.action.assignment.ColorFunction;
import edu.berkeley.guir.prefuse.action.assignment.FontFunction;
import edu.berkeley.guir.prefuse.action.filter.GraphFilter;
import edu.berkeley.guir.prefuse.activity.ActionList;
import edu.berkeley.guir.prefuse.activity.Activity;
import edu.berkeley.guir.prefuse.activity.ActivityMap;
import edu.berkeley.guir.prefuse.event.ActivityAdapter;
import edu.berkeley.guir.prefuse.event.ControlAdapter;
import edu.berkeley.guir.prefuse.graph.DefaultEdge;
import edu.berkeley.guir.prefuse.graph.DefaultGraph;
import edu.berkeley.guir.prefuse.graph.DefaultNode;
import edu.berkeley.guir.prefuse.graph.Edge;
import edu.berkeley.guir.prefuse.graph.Graph;
import edu.berkeley.guir.prefuse.graph.Node;
import edu.berkeley.guir.prefuse.graph.io.GraphReader;
import edu.berkeley.guir.prefuse.graph.io.GraphWriter;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphReader;
import edu.berkeley.guir.prefuse.graph.io.XMLGraphWriter;
import edu.berkeley.guir.prefuse.render.DefaultEdgeRenderer;
import edu.berkeley.guir.prefuse.render.DefaultRendererFactory;
import edu.berkeley.guir.prefuse.render.Renderer;
import edu.berkeley.guir.prefuse.render.TextImageItemRenderer;
import edu.berkeley.guir.prefusex.layout.CircleLayout;
import edu.berkeley.guir.prefusex.layout.ForceDirectedLayout;
import edu.berkeley.guir.prefusex.layout.FruchtermanReingoldLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

public class GraphEditor
		extends JFrame
{
	public static final int SMALL_FONT_SIZE = 10;
	public static final int MEDIUM_FONT_SIZE = 14;
	public static final int LARGE_FONT_SIZE = 20;
	public static final String OPEN = "Open";
	public static final String SAVE = "Save";
	public static final String SAVE_AS = "Save As...";
	public static final String EXIT = "Exit";
	public static final String RANDOM = "Random Layout";
	public static final String CIRCLE = "Circle Layout";
	public static final String FR = "Fruchterman-Reingold Layout";
	public static final String FORCE = "Force-Directed Layout";
	public static final String SMALL_FONT = "Small";
	public static final String MEDIUM_FONT = "Medium";
	public static final String LARGE_FONT = "Large";
	private JMenuItem saveItem;
	public static final String TITLE = "Graph Editor";
	public static final String DEFAULT_LABEL = "???";
	public static final String nameField = "label";
	public static final String idField = "id";
	private ItemRegistry registry;
	private Display display;
	private Graph g;
	private int fontSize = 10;
	private ActivityMap activityMap = new ActivityMap();
	private ActionMap actionMap = new ActionMap();
	private Font[] fonts = { new Font("SansSerif", 0, 10),
			new Font("SansSerif", 0, 14),
			new Font("SansSerif", 0, 20) };
	private Font curFont = this.fonts[0];

	public static void main(String[] argv)
	{
		new GraphEditor();
	}

	public GraphEditor()
	{
		super("Graph Editor");

		setLookAndFeel();
		try
		{
			this.g = new DefaultGraph(Collections.EMPTY_LIST, true);

			this.registry = new ItemRegistry(this.g);
			this.display = new Display();
			Controller controller = new Controller();

			Renderer nodeRenderer = new TextImageItemRenderer();
			Renderer edgeRenderer = new DefaultEdgeRenderer()
			{
				protected int getLineWidth(VisualItem item)
				{
					try
					{
						String wstr = item.getAttribute("weight");
						return Integer.parseInt(wstr);
					}
					catch (Exception e) {}
					return this.m_width;
				}
			};
			this.registry.setRendererFactory(new DefaultRendererFactory(
					nodeRenderer, edgeRenderer, null));

			this.display.setItemRegistry(this.registry);
			this.display.setSize(600, 600);
			this.display.setBackground(Color.WHITE);
			this.display.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
			this.display.setFont(this.curFont);
			this.display.getTextEditor().addKeyListener(controller);
			this.display.addControlListener(controller);

			ActionList filter = new ActionList(this.registry);
			filter.add(new GraphFilter());
			filter.add(this.actionMap.put("font", new FontFunction()
			{
				public Font getFont(VisualItem item)
				{
					return GraphEditor.this.curFont;
				}
			}));
			this.activityMap.put("filter", filter);

			ActionList update = new ActionList(this.registry);
			update.add(new AbstractAction()
			{
				public void run(ItemRegistry registry, double frac)
				{
					Iterator nodeIter = registry.getNodeItems();
					while (nodeIter.hasNext())
					{
						NodeItem item = (NodeItem)nodeIter.next();
						item.setAttribute("X", String.valueOf(item.getX()));
						item.setAttribute("Y", String.valueOf(item.getY()));
					}
				}
			});
			update.add(new ColorFunction()
			{
				public Paint getColor(VisualItem item)
				{
					return item.getColor();
				}

				public Paint getFillColor(VisualItem item)
				{
					if ((item instanceof EdgeItem)) {
						return Color.BLACK;
					}
					return item.getFillColor();
				}
			});
			update.add(new RepaintAction());
			this.activityMap.put("update", update);

			ActionList randomLayout = new ActionList(this.registry);
			randomLayout.add(this.actionMap.put("random", new RandomLayout()));
			randomLayout.add(update);
			this.activityMap.put("randomLayout", randomLayout);

			ActionList circleLayout = new ActionList(this.registry);
			circleLayout.add(this.actionMap.put("circle", new CircleLayout()));
			circleLayout.add(update);
			this.activityMap.put("circleLayout", circleLayout);

			ActionList frLayout = new ActionList(this.registry);
			frLayout.add(this.actionMap.put("fr", new FruchtermanReingoldLayout()));
			frLayout.add(update);
			this.activityMap.put("frLayout", frLayout);

			ActionList forceLayout = new ActionList(this.registry, -1L, 20L);
			forceLayout.add(this.actionMap.put("force", new ForceDirectedLayout(true)));
			forceLayout.add(update);
			forceLayout.addActivityListener(new ActivityAdapter()
			{
				public void activityFinished(Activity a)
				{
					((ForceDirectedLayout)GraphEditor.this.actionMap.get("force")).reset(GraphEditor.this.registry);
				}

				public void activityCancelled(Activity a)
				{
					((ForceDirectedLayout)GraphEditor.this.actionMap.get("force")).reset(GraphEditor.this.registry);
				}
			});
			this.activityMap.put("forceLayout", forceLayout);

			JMenuBar menubar = new JMenuBar();
			JMenu fileMenu = new JMenu("File");
			JMenu layoutMenu = new JMenu("Layout");
			JMenu fontMenu = new JMenu("Font");
			JMenuItem openItem = new JMenuItem("Open");
			this.saveItem = new JMenuItem("Save");
			JMenuItem saveAsItem = new JMenuItem("Save As...");
			JMenuItem exitItem = new JMenuItem("Exit");
			JMenuItem randomItem = new JMenuItem("Random Layout");
			JMenuItem circleItem = new JMenuItem("Circle Layout");
			JMenuItem frItem = new JMenuItem("Fruchterman-Reingold Layout");
			JMenuItem forceItem = new JCheckBoxMenuItem("Force-Directed Layout");

			JMenuItem smallItem = new JRadioButtonMenuItem("Small");
			JMenuItem mediumItem = new JRadioButtonMenuItem("Medium");
			JMenuItem largeItem = new JRadioButtonMenuItem("Large");

			ButtonGroup bg = new ButtonGroup();
			bg.add(smallItem);
			bg.add(mediumItem);
			bg.add(largeItem);
			smallItem.setSelected(true);

			openItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
			this.saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
			saveAsItem.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
			exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
			randomItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
			circleItem.setAccelerator(KeyStroke.getKeyStroke("ctrl C"));
			frItem.setAccelerator(KeyStroke.getKeyStroke("ctrl L"));
			forceItem.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
			smallItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
			mediumItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
			largeItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));

			openItem.setActionCommand("Open");
			this.saveItem.setActionCommand("Save");
			saveAsItem.setActionCommand("Save As...");
			exitItem.setActionCommand("Exit");
			randomItem.setActionCommand("Random Layout");
			circleItem.setActionCommand("Circle Layout");
			frItem.setActionCommand("Fruchterman-Reingold Layout");
			forceItem.setActionCommand("Force-Directed Layout");
			smallItem.setActionCommand("Small");
			mediumItem.setActionCommand("Medium");
			largeItem.setActionCommand("Large");

			openItem.addActionListener(controller);
			this.saveItem.addActionListener(controller);
			saveAsItem.addActionListener(controller);
			exitItem.addActionListener(controller);
			randomItem.addActionListener(controller);
			circleItem.addActionListener(controller);
			frItem.addActionListener(controller);
			forceItem.addActionListener(controller);
			smallItem.addActionListener(controller);
			mediumItem.addActionListener(controller);
			largeItem.addActionListener(controller);

			fileMenu.add(openItem);
			fileMenu.add(this.saveItem);
			fileMenu.add(saveAsItem);
			fileMenu.add(exitItem);

			layoutMenu.add(randomItem);
			layoutMenu.add(circleItem);
			layoutMenu.add(frItem);
			layoutMenu.add(forceItem);

			fontMenu.add(smallItem);
			fontMenu.add(mediumItem);
			fontMenu.add(largeItem);

			menubar.add(fileMenu);
			menubar.add(layoutMenu);
			menubar.add(fontMenu);

			setDefaultCloseOperation(3);
			setJMenuBar(menubar);
			getContentPane().add(this.display);
			pack();
			setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void setLookAndFeel()
	{
		try
		{
			String laf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(laf);
		}
		catch (Exception localException) {}
	}

	private void setLocations(Graph g)
	{
		Iterator nodeIter = g.getNodes();
		while (nodeIter.hasNext())
		{
			Node n = (Node)nodeIter.next();
			NodeItem item = this.registry.getNodeItem(n, true);
			item.setColor(Color.BLACK);
			item.setFillColor(Color.WHITE);
			try
			{
				double x = Double.parseDouble(n.getAttribute("X"));
				double y = Double.parseDouble(n.getAttribute("Y"));
				item.updateLocation(x, y);
				item.setLocation(x, y);
			}
			catch (Exception e)
			{
				System.err.println("!!");
			}
		}
	}

	class Controller
			extends ControlAdapter
			implements MouseListener, KeyListener, ActionListener
	{
		private int xDown;
		private int yDown;
		private int xCur;
		private int yCur;
		private boolean directed = true;
		private boolean drag = false;
		private boolean editing = false;
		private VisualItem activeItem;
		private VisualItem edgeItem;
		private boolean edited = false;
		private File saveFile = null;

		Controller() {}

		public void itemEntered(VisualItem item, MouseEvent e)
		{
			if ((item instanceof NodeItem)) {
				e.getComponent().setCursor(
						Cursor.getPredefinedCursor(12));
			}
		}

		public void itemExited(VisualItem item, MouseEvent e)
		{
			if ((item instanceof NodeItem)) {
				e.getComponent().setCursor(Cursor.getDefaultCursor());
			}
		}

		public void itemPressed(VisualItem item, MouseEvent e)
		{
			if ((item instanceof NodeItem))
			{
				this.xDown = e.getX();
				this.yDown = e.getY();
				item.setColor(Color.RED);
				item.setFillColor(Color.WHITE);
				GraphEditor.this.activityMap.scheduleNow("update");
				item.setFixed(true);
			}
		}

		public void itemReleased(VisualItem item, MouseEvent e)
		{
			if (!this.editing) {
				item.setFixed(false);
			}
			if (!(item instanceof NodeItem)) {
				return;
			}
			boolean update = false;
			if ((item instanceof NodeItem)) {
				if ((this.activeItem == null) && (!this.drag))
				{
					this.activeItem = item;
				}
				else if (this.activeItem == null)
				{
					item.setColor(Color.BLACK);
					item.setFillColor(Color.WHITE);
					update = true;
				}
				else if ((this.activeItem == item) && (!this.drag))
				{
					this.editing = true;
					this.activeItem.setFixed(true);
					GraphEditor.this.display.editText(item, "label");
					GraphEditor.this.display.getTextEditor().selectAll();
					setEdited(true);
					update = true;
				}
				else if (this.activeItem != item)
				{
					addEdge(this.activeItem, item);

					item.setColor(Color.BLACK);
					item.setFillColor(Color.WHITE);
					this.activeItem.setColor(Color.BLACK);
					this.activeItem.setFillColor(Color.WHITE);
					this.activeItem = null;
					update = true;
					GraphEditor.this.activityMap.scheduleNow("filter");
				}
			}
			this.drag = false;
			if (update) {
				GraphEditor.this.activityMap.scheduleNow("update");
			}
		}

		public void itemDragged(VisualItem item, MouseEvent e)
		{
			if (!(item instanceof NodeItem)) {
				return;
			}
			this.drag = true;
			Point2D p = item.getLocation();
			double x = p.getX() + e.getX() - this.xDown;
			double y = p.getY() + e.getY() - this.yDown;
			item.updateLocation(x, y);
			item.setLocation(x, y);
			GraphEditor.this.activityMap.scheduleNow("update");
			this.xDown = e.getX();
			this.yDown = e.getY();
			setEdited(true);
		}

		public void itemKeyTyped(VisualItem item, KeyEvent e)
		{
			if (e.getKeyChar() == '\b')
			{
				if (item == this.activeItem) {
					this.activeItem = null;
				}
				removeNode(item);
				GraphEditor.this.activityMap.scheduleNow("filter");
				GraphEditor.this.activityMap.scheduleNow("update");
				setEdited(true);
			}
		}

		public void mouseReleased(MouseEvent e)
		{
			boolean update = false;
			if (this.editing)
			{
				stopEditing();
				update = true;
			}
			if (this.activeItem != null)
			{
				this.activeItem.setColor(Color.BLACK);
				this.activeItem.setFillColor(Color.WHITE);
				this.activeItem = null;
				update = true;
			}
			boolean rightClick = (e.getModifiers() & 0x4) > 0;
			if (rightClick)
			{
				addNode(e.getX(), e.getY());
				setEdited(true);
				GraphEditor.this.activityMap.scheduleNow("filter");
				update = true;
			}
			if (update) {
				GraphEditor.this.activityMap.scheduleNow("update");
			}
		}

		public void mouseMoved(MouseEvent e)
		{
			if (!this.editing) {
				GraphEditor.this.display.requestFocus();
			}
			this.xCur = e.getX();
			this.yCur = e.getY();
		}

		public void keyPressed(KeyEvent e)
		{
			Object src = e.getSource();
			char c = e.getKeyChar();
			int modifiers = e.getModifiers();
			boolean modded = (modifiers &
					0xA) > 0;
			if ((Character.isLetterOrDigit(c)) && (!modded) &&
					(src == GraphEditor.this.display) && (this.activeItem == null))
			{
				VisualItem item = addNode(this.xCur, this.yCur);
				item.setAttribute("label", String.valueOf(c));
				this.editing = true;
				Rectangle r = item.getBounds().getBounds();
				r.width = 52;r.height += 2;
				r.x -= 1 + r.width / 2;r.y -= 1;
				this.activeItem = item;
				item.setFixed(true);
				GraphEditor.this.display.editText(item, "label", r);
				setEdited(true);
				GraphEditor.this.activityMap.scheduleNow("filter");
				GraphEditor.this.activityMap.scheduleNow("update");
			}
		}

		public void keyReleased(KeyEvent e)
		{
			Object src = e.getSource();
			if ((src == GraphEditor.this.display.getTextEditor()) &&
					(e.getKeyCode() == 10))
			{
				stopEditing();
				GraphEditor.this.activityMap.scheduleNow("update");
			}
		}

		private NodeItem addNode(int x, int y)
		{
			Node n = new DefaultNode();
			n.setAttribute("label", "???");
			GraphEditor.this.g.addNode(n);
			NodeItem item = GraphEditor.this.registry.getNodeItem(n, true);
			item.setColor(Color.BLACK);
			item.setFillColor(Color.WHITE);
			item.updateLocation(x, y);
			item.setLocation(x, y);
			return item;
		}

		private void addEdge(VisualItem item1, VisualItem item2)
		{
			Node n1 = (Node)item1.getEntity();
			Node n2 = (Node)item2.getEntity();
			if (n1.getIndex(n2) < 0)
			{
				Edge e = new DefaultEdge(n1, n2, this.directed);
				n1.addEdge(e);
				if (!this.directed) {
					n2.addEdge(e);
				}
			}
		}

		private void removeNode(VisualItem item)
		{
			Node n = (Node)item.getEntity();
			GraphEditor.this.g.removeNode(n);
		}

		private void stopEditing()
		{
			GraphEditor.this.display.stopEditing();
			if (this.activeItem != null)
			{
				this.activeItem.setColor(Color.BLACK);
				this.activeItem.setFillColor(Color.WHITE);
				this.activeItem.setFixed(false);
				this.activeItem = null;
			}
			this.editing = false;
		}

		public void actionPerformed(ActionEvent e)
		{
			boolean runFilterUpdate = false;
			String cmd = e.getActionCommand();
			if ("Open".equals(cmd))
			{
				JFileChooser chooser = new JFileChooser();
				if (chooser.showOpenDialog(GraphEditor.this.display) == 0)
				{
					File f = chooser.getSelectedFile();
					GraphReader gr = new XMLGraphReader();
					try
					{
						GraphEditor.this.g = gr.loadGraph(f);
						GraphEditor.this.registry.setGraph(GraphEditor.this.g);
						GraphEditor.this.setLocations(GraphEditor.this.g);
						GraphEditor.this.activityMap.scheduleNow("filter");
						GraphEditor.this.activityMap.scheduleNow("update");
						this.saveFile = f;
						setEdited(false);
					}
					catch (Exception ex)
					{
						JOptionPane.showMessageDialog(
								GraphEditor.this.display,
								"Sorry, an error occurred while loading the graph.",
								"Error Loading Graph",
								0);
						ex.printStackTrace();
					}
				}
			}
			else if ("Save".equals(cmd))
			{
				if (this.saveFile == null)
				{
					JFileChooser chooser = new JFileChooser();
					if (chooser.showSaveDialog(GraphEditor.this.display) == 0)
					{
						File f = chooser.getSelectedFile();
						save(f);
					}
				}
				else
				{
					save(this.saveFile);
				}
			}
			else if ("Save As...".equals(cmd))
			{
				JFileChooser chooser = new JFileChooser();
				if (chooser.showSaveDialog(GraphEditor.this.display) == 0)
				{
					File f = chooser.getSelectedFile();
					save(f);
				}
			}
			else if ("Exit".equals(cmd))
			{
				System.exit(0);
			}
			else if ("Random Layout".equals(cmd))
			{
				GraphEditor.this.activityMap.scheduleNow("randomLayout");
			}
			else if ("Circle Layout".equals(cmd))
			{
				GraphEditor.this.activityMap.scheduleNow("circleLayout");
			}
			else if ("Fruchterman-Reingold Layout".equals(cmd))
			{
				GraphEditor.this.activityMap.scheduleNow("frLayout");
			}
			else if ("Force-Directed Layout".equals(cmd))
			{
				JCheckBoxMenuItem cb = (JCheckBoxMenuItem)e.getSource();
				if (cb.getState()) {
					GraphEditor.this.activityMap.scheduleNow("forceLayout");
				} else {
					GraphEditor.this.activityMap.cancel("forceLayout");
				}
			}
			else if ("Small".equals(cmd))
			{
				GraphEditor.this.curFont = GraphEditor.this.fonts[0];
				GraphEditor.this.display.setFont(GraphEditor.this.curFont);
				runFilterUpdate = true;
			}
			else if ("Medium".equals(cmd))
			{
				GraphEditor.this.curFont = GraphEditor.this.fonts[1];
				GraphEditor.this.display.setFont(GraphEditor.this.curFont);
				runFilterUpdate = true;
			}
			else if ("Large".equals(cmd))
			{
				GraphEditor.this.curFont = GraphEditor.this.fonts[2];
				GraphEditor.this.display.setFont(GraphEditor.this.curFont);
				runFilterUpdate = true;
			}
			else
			{
				throw new IllegalStateException();
			}
			if (runFilterUpdate)
			{
				GraphEditor.this.activityMap.scheduleNow("filter");
				GraphEditor.this.activityMap.scheduleNow("update");
			}
		}

		private void save(File f)
		{
			GraphWriter gw = new XMLGraphWriter();
			try
			{
				gw.writeGraph(GraphEditor.this.g, f);
				this.saveFile = f;
				setEdited(false);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(
						GraphEditor.this.display,
						"Sorry, an error occurred while saving the graph.",
						"Error Saving Graph",
						0);
				ex.printStackTrace();
			}
		}

		private void setEdited(boolean s)
		{
			if (this.edited == s) {
				return;
			}
			this.edited = s;
			GraphEditor.this.saveItem.setEnabled(s);
			String titleString;
			if (this.saveFile == null) {
				titleString = "Graph Editor";
			} else {
				titleString =
						"Graph Editor - " + this.saveFile.getName() + (s ? "*" : "");
			}
			if (!titleString.equals(GraphEditor.this.getTitle())) {
				GraphEditor.this.setTitle(titleString);
			}
		}
	}
}
