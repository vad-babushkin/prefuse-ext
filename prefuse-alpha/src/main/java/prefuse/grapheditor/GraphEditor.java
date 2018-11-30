package prefuse.grapheditor;

import java.awt.Color;
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
import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

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
import edu.berkeley.guir.prefuse.action.filter.TreeFilter;
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
import edu.berkeley.guir.prefusex.layout.RadialTreeLayout;
import edu.berkeley.guir.prefusex.layout.RandomLayout;

/**
 * GraphEditor Application, an editor for hand creating directed graphs
 *
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> - prefuse(AT)jheer.org
 */
public class GraphEditor extends JFrame {

    public static final int SMALL_FONT_SIZE  = 10;
    public static final int MEDIUM_FONT_SIZE = 14;
    public static final int LARGE_FONT_SIZE  = 20;
    
	public static final String NEW     = "New";
    public static final String OPEN    = "Open";
	public static final String SAVE    = "Save";
	public static final String SAVE_AS = "Save As...";
	public static final String EXIT    = "Exit";
	public static final String MANUAL  = "Manual Layout";
    public static final String RANDOM  = "Random Layout";
    public static final String CIRCLE  = "Circle Layout";
    public static final String RADIAL  = "Radial Layout";
    public static final String FR      = "Fruchterman-Reingold Layout";
    public static final String FORCE   = "Force-Directed Layout";
    public static final String SMALL_FONT  = "Small";
    public static final String MEDIUM_FONT = "Medium";
    public static final String LARGE_FONT  = "Large";

	private JMenuItem saveItem;

	public static final String TITLE = "Graph Editor";
	public static final String DEFAULT_LABEL = "???";

	public static final String nameField = "label";
	public static final String idField   = "id";
		
	private ItemRegistry registry;
	private Display display;
	private Graph g;
    private int fontSize = SMALL_FONT_SIZE;
    private String curLayout = MANUAL;
    private ActivityMap activityMap = new ActivityMap();
    private ActionMap   actionMap = new ActionMap();
    
    private DirectedDialog directedDialog = new DirectedDialog();
    private Font[] fonts = { new Font("SansSerif",Font.PLAIN,SMALL_FONT_SIZE),
                             new Font("SansSerif",Font.PLAIN,MEDIUM_FONT_SIZE),
                             new Font("SansSerif",Font.PLAIN,LARGE_FONT_SIZE)};
    private Font curFont = fonts[0];
    
    public static void main(String argv[]) {
        new GraphEditor();
    } //
    
	public GraphEditor() {
        super(TITLE);
        
		setLookAndFeel();
		try {
			g = new DefaultGraph(Collections.EMPTY_LIST, true);

			registry = new ItemRegistry(g);
            display  = new Display();
			Controller controller = new Controller();
			
			// initialize renderers
			Renderer nodeRenderer = new TextImageItemRenderer();
			Renderer edgeRenderer = new DefaultEdgeRenderer() {
				protected int getLineWidth(VisualItem item) {
					try {
						String wstr = item.getAttribute("weight");
						return Integer.parseInt(wstr);
					} catch ( Exception e ) {
						return m_width;
					}
				} //
			};
			registry.setRendererFactory(new DefaultRendererFactory(
				nodeRenderer, edgeRenderer, null));
			
			// initialize display
			display.setItemRegistry(registry);
			display.setSize(600,600);
			display.setBackground(Color.WHITE);
            display.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
            display.setFont(curFont);
            display.getTextEditor().addKeyListener(controller);
			display.addControlListener(controller);
			
			// initialize filter
            ActionList filter = new ActionList(registry);
            filter.add(new GraphFilter());
            filter.add(actionMap.put("font", new FontFunction() {
                public Font getFont(VisualItem item) {
                    return curFont;
                } //
            }));
            activityMap.put("filter", filter);
            
            ActionList update = new ActionList(registry);
            update.add(new AbstractAction() {
				public void run(ItemRegistry registry, double frac) {
					Iterator nodeIter = registry.getNodeItems();
					while ( nodeIter.hasNext() ) {
						NodeItem item = (NodeItem)nodeIter.next();
						item.setAttribute("X",String.valueOf(item.getX()));
						item.setAttribute("Y",String.valueOf(item.getY()));
					}
				} //
			});
            update.add(new ColorFunction() {
                public Paint getColor(VisualItem item) {
                    return item.getColor();
                } //
                public Paint getFillColor(VisualItem item) {
                    if ( item instanceof EdgeItem ) {
                        return Color.BLACK;
                    } else {
                        return item.getFillColor();
                    }
                } //
            });
            update.add(new RepaintAction());
            activityMap.put("update", update);
            
            ActionList randomLayout = new ActionList(registry);
            randomLayout.add(actionMap.put("random",new RandomLayout()));
            randomLayout.add(update);
            activityMap.put("randomLayout", randomLayout);
            
            ActionList circleLayout = new ActionList(registry);
            circleLayout.add(actionMap.put("circle",new CircleLayout()));
            circleLayout.add(update);
            activityMap.put("circleLayout", circleLayout);
            
            ActionList radialLayout = new ActionList(registry);
            radialLayout.add(new TreeFilter(true));
            radialLayout.add(actionMap.put("radial",new RadialTreeLayout()));
            radialLayout.add(update);
            activityMap.put("radialLayout", radialLayout);
            
            ActionList frLayout = new ActionList(registry);
            frLayout.add(actionMap.put("fr",new FruchtermanReingoldLayout()));
            frLayout.add(update);
            activityMap.put("frLayout", frLayout);
            
            ActionList forceLayout = new ActionList(registry,-1,20);
            forceLayout.add(actionMap.put("force",new ForceDirectedLayout(true)));
            forceLayout.add(update);
            forceLayout.addActivityListener(new ActivityAdapter() {
                public void activityFinished(Activity a) {
                    ((ForceDirectedLayout)actionMap.get("force")).reset(registry);
                } //
                public void activityCancelled(Activity a) {
                    ((ForceDirectedLayout)actionMap.get("force")).reset(registry);
                } //
            });
            activityMap.put("forceLayout", forceLayout);
			
			// initialize menus
			JMenuBar  menubar    = new JMenuBar();
			JMenu     fileMenu   = new JMenu("File");
            JMenu     layoutMenu = new JMenu("Layout");
            JMenu     fontMenu   = new JMenu("Font");
            JMenuItem newItem    = new JMenuItem(NEW);
			JMenuItem openItem   = new JMenuItem(OPEN);
				      saveItem   = new JMenuItem(SAVE);
			JMenuItem saveAsItem = new JMenuItem(SAVE_AS);
			JMenuItem exitItem   = new JMenuItem(EXIT);
			JMenuItem manualItem = new JCheckBoxMenuItem(MANUAL);
            JMenuItem randomItem = new JCheckBoxMenuItem(RANDOM);
            JMenuItem circleItem = new JCheckBoxMenuItem(CIRCLE);
            JMenuItem radialItem = new JCheckBoxMenuItem(RADIAL);
            JMenuItem frItem     = new JCheckBoxMenuItem(FR);
            JMenuItem forceItem  = new JCheckBoxMenuItem(FORCE);
            
            JMenuItem smallItem  = new JRadioButtonMenuItem(SMALL_FONT);
            JMenuItem mediumItem = new JRadioButtonMenuItem(MEDIUM_FONT);
            JMenuItem largeItem  = new JRadioButtonMenuItem(LARGE_FONT);
			
            ButtonGroup bg1 = new ButtonGroup();
            bg1.add(manualItem);
            bg1.add(randomItem);
            bg1.add(circleItem);
            bg1.add(radialItem);
            bg1.add(frItem);
            bg1.add(forceItem);
            manualItem.setSelected(true);
            
            ButtonGroup bg2 = new ButtonGroup();
            bg2.add(smallItem);
            bg2.add(mediumItem);
            bg2.add(largeItem);
            smallItem.setSelected(true);
            
            newItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
            openItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
            saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
            saveAsItem.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
            exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
            manualItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 0"));
            randomItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 1"));
            circleItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 2"));
            radialItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 3"));
            frItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 4"));
            forceItem.setAccelerator(KeyStroke.getKeyStroke("ctrl 5"));
            
            newItem.setActionCommand(NEW);
			openItem.setActionCommand(OPEN);
			saveItem.setActionCommand(SAVE);
			saveAsItem.setActionCommand(SAVE_AS);
			exitItem.setActionCommand(EXIT);
			manualItem.setActionCommand(MANUAL);
            randomItem.setActionCommand(RANDOM);
            circleItem.setActionCommand(CIRCLE);
            radialItem.setActionCommand(RADIAL);
            frItem.setActionCommand(FR);
            forceItem.setActionCommand(FORCE);
            smallItem.setActionCommand(SMALL_FONT);
            mediumItem.setActionCommand(MEDIUM_FONT);
            largeItem.setActionCommand(LARGE_FONT);
			
            newItem.addActionListener(controller);
			openItem.addActionListener(controller);
			saveItem.addActionListener(controller);
			saveAsItem.addActionListener(controller);
			exitItem.addActionListener(controller);
			manualItem.addActionListener(controller);
            randomItem.addActionListener(controller);
            circleItem.addActionListener(controller);
            radialItem.addActionListener(controller);
            frItem.addActionListener(controller);
            forceItem.addActionListener(controller);
            smallItem.addActionListener(controller);
            mediumItem.addActionListener(controller);
            largeItem.addActionListener(controller);
			
            fileMenu.add(newItem);
			fileMenu.add(openItem);
			fileMenu.add(saveItem);
			fileMenu.add(saveAsItem);
			fileMenu.add(exitItem);
            
            layoutMenu.add(randomItem);
            layoutMenu.add(circleItem);
            layoutMenu.add(radialItem);
            layoutMenu.add(frItem);
            layoutMenu.add(forceItem);
            
            fontMenu.add(smallItem);
            fontMenu.add(mediumItem);
            fontMenu.add(largeItem);
			
			menubar.add(fileMenu);
            menubar.add(layoutMenu);
            menubar.add(fontMenu);
			
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setJMenuBar(menubar);
			getContentPane().add(display);
			pack();
			setVisible(true);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //
	
	public static void setLookAndFeel() {
		try {
			String laf = UIManager.getSystemLookAndFeelClassName();				
			UIManager.setLookAndFeel(laf);	
		} catch ( Exception e ) {}
	} //
	
	private void setLocations(Graph g) {
		Iterator nodeIter = g.getNodes();
		while ( nodeIter.hasNext() ) {
			Node n = (Node)nodeIter.next();
			NodeItem item = registry.getNodeItem(n,true,false);
			item.setColor(Color.BLACK);
			item.setFillColor(Color.WHITE);
			try {
				double x = Double.parseDouble(n.getAttribute("X"));
				double y = Double.parseDouble(n.getAttribute("Y"));
                item.updateLocation(x,y);
				item.setLocation(x,y);
			} catch ( Exception e ) {
				System.err.println("!!");
			}
		}
	} //
    
	/**
	 * Input controller for interacting with the application.
	 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse(AT)jheer.org
	 */
	class Controller extends ControlAdapter 
		implements MouseListener, KeyListener, ActionListener
	{
		private int xDown, yDown, xCur, yCur;
		
		private boolean drag     = false;
		private boolean editing  = false;
		
		private VisualItem activeItem;
		private VisualItem edgeItem;
		
		private boolean edited   = false;
		private File    saveFile = null;
		
		public void itemEntered(VisualItem item, MouseEvent e) {
            if ( item instanceof NodeItem ) {
                e.getComponent().setCursor(
                        Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
		} //
		
		public void itemExited(VisualItem item, MouseEvent e) {
            if ( item instanceof NodeItem ) {
                e.getComponent().setCursor(Cursor.getDefaultCursor());
            }
		} //
		
		public void itemPressed(VisualItem item, MouseEvent e) {
			if ( !(item instanceof NodeItem) ) {
			    return;
			}
			if ( !e.isControlDown() )
			{
			    xDown = e.getX();
			    yDown = e.getY();
			    item.setColor(Color.RED);
			    item.setFillColor(Color.WHITE);
			    activityMap.runNow("update");
                item.setFixed(true);
            }
		} //
		
		public void itemReleased(VisualItem item, MouseEvent e) {
            if (!editing)
                item.setFixed(false);
            if ( !(item instanceof NodeItem) )
                return;
            
            if ( e.isControlDown() ) {
                registry.getDefaultFocusSet().set(item.getEntity());
                activityMap.runNow("filter");
                activityMap.runNow(curLayout);
                return;
            }
            
			boolean update = false;
			if ( activeItem == null && !drag ) {
				activeItem = item;
			} else if ( activeItem == null ) {
				item.setColor(Color.BLACK);
				item.setFillColor(Color.WHITE);
				update = true;
			} else if ( activeItem == item && !drag ) {
				editing = true;
                activeItem.setFixed(true);
				display.editText(item, nameField);
				display.getTextEditor().selectAll();
				setEdited(true);
				update = true;
			} else if ( activeItem != item ) {
				// add edge
				addEdge(activeItem, item);
			    item.setColor(Color.BLACK);
				item.setFillColor(Color.WHITE);
				deactivateItem();
				update = true;
                activityMap.runNow("filter");
			}
			drag = false;
            if ( update )
                activityMap.runNow("update");
		} //
		
		public void itemDragged(VisualItem item, MouseEvent e) {
            if ( !(item instanceof NodeItem) )
                return;
            
			drag = true;
            Point2D p = item.getLocation();
			double x = p.getX() + e.getX() - xDown;
			double y = p.getY() + e.getY() - yDown;
            item.updateLocation(x,y);
			item.setLocation(x,y);
            activityMap.runNow("update");
			xDown = e.getX();
			yDown = e.getY();
			setEdited(true);
		} //
		
		public void itemKeyTyped(VisualItem item, KeyEvent e) {
			if ( e.getKeyChar() == '\b' ) {				
				if (item == activeItem) activeItem = null;
				removeNode(item);
                activityMap.runNow("filter");
                activityMap.runNow("update");
				setEdited(true);
			}
		} //

		/**
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
			boolean update = false;
			if ( editing ) {
				stopEditing();
				update = true;
			}
			if ( activeItem != null ) {
				activeItem.setColor(Color.BLACK);
				activeItem.setFillColor(Color.WHITE);
				activeItem = null;
				update = true;
			}
			boolean rightClick = (e.getModifiers() & MouseEvent.BUTTON3_MASK) > 0;
			if ( rightClick ) {
				addNode(e.getX(), e.getY());
				setEdited(true);
                activityMap.runNow("filter");
				update = true;
			}
			if ( update ) {
                activityMap.runNow("update");
			}
		} //
		
		public void mouseMoved(MouseEvent e) {
			if ( !editing )
				display.requestFocus();
			xCur = e.getX();
			yCur = e.getY();
		} //

		public void keyPressed(KeyEvent e) {
			Object src = e.getSource();
			char c = e.getKeyChar();
            int modifiers = e.getModifiers();
            boolean modded = (modifiers & 
               (KeyEvent.ALT_MASK | KeyEvent.CTRL_MASK)) > 0; 
			if ( Character.isLetterOrDigit(c) && !modded && 
				src == display ) {
				VisualItem item = addNode(xCur, yCur);
				if ( activeItem != null ) {
				    addEdge(activeItem, item);
				    deactivateItem();
				}
				item.setAttribute(nameField,String.valueOf(c));
				editing = true;
				Rectangle r = item.getBounds().getBounds();
				r.width = 52; r.height += 2;
				r.x -= 1+r.width/2; r.y -= 1; 
                activeItem = item;
                item.setFixed(true);
				display.editText(item, nameField, r);
				setEdited(true);
                activityMap.runNow("filter");
                activityMap.runNow("update");
			}
		} //
		
		public void keyReleased(KeyEvent e) {
			Object src = e.getSource();
			if ( src == display.getTextEditor() && 
				e.getKeyCode() == KeyEvent.VK_ENTER ) {
				stopEditing();
                activityMap.runNow("update");
			}
		} //

		private void deactivateItem() {
			activeItem.setColor(Color.BLACK);
			activeItem.setFillColor(Color.WHITE);
			activeItem = null;
		} //
		
		private NodeItem addNode(int x, int y) {
			Node n = new DefaultNode();
			n.setAttribute(nameField, DEFAULT_LABEL);
			g.addNode(n);
			NodeItem item = registry.getNodeItem(n,true);
			item.setColor(Color.BLACK);
			item.setFillColor(Color.WHITE);
            item.updateLocation(x,y);
			item.setLocation(x,y);
			return item;
		} //
		
		private void addEdge(VisualItem item1, VisualItem item2) {
			Node n1 = (Node)item1.getEntity();
			Node n2 = (Node)item2.getEntity();
			if ( n1.getIndex(n2) < 0 ) {
				Edge e = new DefaultEdge(n1, n2, g.isDirected());
				g.addEdge(e);
			}
		} //
		
		private void removeNode(VisualItem item) {
            Node n = (Node)item.getEntity();
			g.removeNode(n);
		} //

		private void stopEditing() {
			display.stopEditing();
			if ( activeItem != null ) {
				activeItem.setColor(Color.BLACK);
				activeItem.setFillColor(Color.WHITE);
                activeItem.setFixed(false);
				activeItem = null;
			}
			editing = false;
		} //

		// == MENU CALLBACKS =====================================================

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
            boolean runFilterUpdate = false;
			String cmd = e.getActionCommand();
			if ( NEW.equals(cmd) ) {
			    boolean directed = directedDialog.showDialog();
			    g = new DefaultGraph(Collections.EMPTY_LIST, directed);
			    registry.setGraph(g);
			    saveFile = null;
                setEdited(false);
                runFilterUpdate = true;
			} else if ( OPEN.equals(cmd) ) {
				JFileChooser chooser = new JFileChooser();
				if ( chooser.showOpenDialog(display) == JFileChooser.APPROVE_OPTION ) {
					 File f = chooser.getSelectedFile();
					 GraphReader gr = new XMLGraphReader();
					 try {					 
						g = gr.loadGraph(f);
						registry.setGraph(g);
						setLocations(g);
						saveFile = f;
						setEdited(false);
						runFilterUpdate = true;
					 } catch ( Exception ex ) {
						JOptionPane.showMessageDialog(
							display,
							"Sorry, an error occurred while loading the graph.",
							"Error Loading Graph",
							JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					 }
				}
			} else if ( SAVE.equals(cmd) ) {
				if ( saveFile == null ) {
					JFileChooser chooser = new JFileChooser();
					if ( chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION ) {
						File f = chooser.getSelectedFile();
						save(f);
					}
				} else {
					save(saveFile);
				}
			} else if ( SAVE_AS.equals(cmd) ) {
				JFileChooser chooser = new JFileChooser();
				if ( chooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION ) {
					 File f = chooser.getSelectedFile();
					 save(f);
				}
			} else if ( EXIT.equals(cmd) ) {
				System.exit(0);
			} else if ( MANUAL.equals(cmd) ) {
			    curLayout = MANUAL;
            } else if ( RANDOM.equals(cmd) ) {
                doLayout("randomLayout");
            } else if ( CIRCLE.equals(cmd) ) {
                doLayout("circleLayout");
            } else if ( RADIAL.equals(cmd) ) {
                doLayout("radialLayout");
            } else if ( FR.equals(cmd) ) {
                doLayout("frLayout");
            } else if ( FORCE.equals(cmd) ) {
                doLayout("forceLayout");
            } else if ( SMALL_FONT.equals(cmd) ) {
                curFont = fonts[0];
                display.setFont(curFont);
                runFilterUpdate = true;
            } else if ( MEDIUM_FONT.equals(cmd) ) {
                curFont = fonts[1];
                display.setFont(curFont);
                runFilterUpdate = true;
            } else if ( LARGE_FONT.equals(cmd) ) {
                curFont = fonts[2];
                display.setFont(curFont);
                runFilterUpdate = true;
            } else {
				throw new IllegalStateException();
			}
            if ( runFilterUpdate ) {
                activityMap.runNow("filter");
                activityMap.runNow("update");
            }
		} //
		
		private void doLayout(String layout) {
		    curLayout = layout;
		    activityMap.cancel("forceLayout");
		    activityMap.runNow(layout);
		} //
		
		private void save(File f) {
			GraphWriter gw = new XMLGraphWriter();
			 try {					 
				gw.writeGraph(g, f);
				saveFile = f;
				setEdited(false);
			 } catch ( Exception ex ) {
				JOptionPane.showMessageDialog(
					display,
					"Sorry, an error occurred while saving the graph.",
					"Error Saving Graph",
					JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			 }
		} //
		
		private void setEdited(boolean s) {
			if ( edited == s ) return;
			edited = s;
			saveItem.setEnabled(s);
			String titleString;
			if ( saveFile == null ) {
				titleString = TITLE;
			} else {
				titleString = TITLE + " - " + saveFile.getName() +
								( s ? "*" : "" );
			}
			if ( !titleString.equals(getTitle()) )
				setTitle(titleString);
		} //
		
	} // end of inner class MouseOverControl

	class DirectedDialog extends JDialog implements ActionListener {
	    private boolean directed = true;
	    private JRadioButton dir;
	    private JRadioButton und;
	    private JButton ok;
	    
	    public DirectedDialog() {
	        super(GraphEditor.this, "New Graph", true);
	        Container c = getContentPane();
	        setLookAndFeel();
	        
	        dir = new JRadioButton("Directed");
	        und = new JRadioButton("Undirected");
	        
	        dir.setSelected(true);
	        dir.addActionListener(this);
	        und.addActionListener(this);
	        
	        ButtonGroup bg = new ButtonGroup();
	        bg.add(dir);
	        bg.add(und);
	        
	        ok = new JButton("OK");
	        ok.setHorizontalAlignment(JButton.RIGHT);
	        ok.addActionListener(this);
	        
	        Box okb = new Box(BoxLayout.X_AXIS);
	        okb.add(Box.createHorizontalGlue());
	        okb.add(ok);
	        okb.add(Box.createHorizontalGlue());
	        
	        JLabel label = new JLabel("What type of graph?");
	        
	        Box b = new Box(BoxLayout.Y_AXIS);
	        b.add(Box.createVerticalStrut(5));
	        b.add(label);
	        b.add(dir);
	        b.add(und);
	        b.add(okb);
	        b.add(Box.createVerticalStrut(5));
	        
	        Box d = new Box(BoxLayout.X_AXIS);
	        d.add(Box.createHorizontalStrut(10));
	        d.add(b);
	        d.add(Box.createHorizontalStrut(10));
	        
	        c.add(d);
	        c.setBackground(dir.getBackground());
	        this.pack();
	    } //

	    public boolean showDialog() {
	        dir.setSelected(true);
	        this.setVisible(true);
	        return directed;
	    }
	    
        public void actionPerformed(ActionEvent evt) {
            if ( evt.getSource() == ok ) {
                this.setVisible(false);
            } else {
                directed = (evt.getSource() == dir);
            }
        } //
	    
	} // end of inner class DirectedDialog
	
} // end of classs GraphEditor
