package prefuse.demos.idot;

import prefuse.demos.idot.util.ExtensionFileFilter;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;
import prefuse.data.Graph;
import prefuse.util.display.ExportDisplayAction;


/**
 * State Diagram Visualizer application.
 * This program reads state diagrams produced by the SpinSpider tool and
 * saved in the dot format and displays the diagram 
 * interactively to the user, who can then perform different "filters"
 * on the diagram to gain deeper understanding of the operation of the
 * illustrated program or algorithm.
 * 
 * Originally based on a sample graph editor in the 
 * <a href="http://prefuse.org">prefuse</a> library made by 
 * <a href="http://jheer.org">Jeffrey Heer</a>.
 */
public class iDot extends JFrame {
    
	// menu item texts
	public static final String OPEN    = "Open";
	public static final String EXPORT  = "Export";
	public static final String EXIT    = "Exit";
	public static final String ABOUT = "About";
	public static final String HELP = "Help";
	public static final String REDO = "Redo";
	public static final String EDIT   = "Edit";
	public static final String SHOW_ALL      = "Show all";
	public static final String SHOW_OPENED   = "Show open";
    public static final JButton toolOpen = new JButton(OPEN);
    public static final JButton toolRedo = new JButton(REDO);
    public static final JButton toolEdit = new JButton(EDIT);
    public static final JButton toolShow = new JButton(SHOW_ALL);
    public static final JButton toolExport = new JButton(EXPORT);
    public static final JButton toolAbout = new JButton(ABOUT);
    public static final JButton toolHelp = new JButton(HELP);
    public static final JButton toolExit = new JButton(EXIT);
    public static final int OPENMN   = KeyEvent.VK_O;
    public static final int REDOMN   = KeyEvent.VK_R;
    public static final int EDITMN   = KeyEvent.VK_E;
    public static final int SHOWMN   = KeyEvent.VK_S;
    public static final int EXPORTMN   = KeyEvent.VK_P;
    public static final int HELPMN   = KeyEvent.VK_H;
    public static final int ABOUTMN   = KeyEvent.VK_A;
    public static final int EXITMN   = KeyEvent.VK_X;
    
	public static final int   HEIGHT = 700;
        public static final int   WIDTH = 900;
        public static final java.awt.Font font = 
                new java.awt.Font(Config.TEXTAREA_FONT_NAME, 
		java.awt.Font.BOLD, 12);
    
	/** where to look for files at first File->Open */
	public static final String INITIAL_PATH = 
		System.getProperty("user.dir") + File.separatorChar 
				+ "examples" + File.separatorChar;

	/**
	 * The component containing the actual view of the graph and methods
	 * for relevant operations on it
	 */
	private DotDisplay display;
	
	/**
	 * The standard dialog used for selecting the file to load
	 */
    private JFileChooser fileChooser;

	/**
	 * The file the graph was last loaded from
	 */
	private File saveFile = null;
	    
    /**
     * Additional information about the currently displayed graph. After
     * Layout->"Extract filtered graph" this variable tells which
     * file the graph was extracted from, which information is then shown
     * in the title bar.
     */
	private String  graphState = "empty";
    	
	/**
	 * Entry point of the program. If there are no
	 * command line parameters, an empty application window
	 * is opened. If there are command line parameters, they
	 * are considered to be names of files containing state
	 * diagrams to be loaded by the application. Every
	 * specified file will be opened in a separate window.
	 * 
	 * @param argv  the command line parameters
	 */
    public static void main(String argv[]) {
        if(argv.length > 0)
        	for(String filename : argv) 
        		new iDot().loadFile(new File(filename));
        else
        	new iDot();
    } //
    
    /**
     * Default constructor. Will initialize the view with
     * an empty graph.
     */
    public iDot() {    	
    	this(new Graph(true), null);
    }
    
    /**
     * A constructor specifying the graph to load to the program.
     * 
     * @param graph  the graph to be loaded initially
     * @param originOfGraph  an optional string providing additional information 
     *     about the graph to be shown in the title bar of the program
     */
	public iDot(Graph graph, String originOfGraph) {
          super();
          setLookAndFeel();
              fileChooser = new JFileChooser(INITIAL_PATH);		
              fileChooser.addChoosableFileFilter(new ExtensionFileFilter(
                              ".dot", "*.dot (DOT files)"));
              MenuController controller = new MenuController();		
              JToolBar toolBar = new JToolBar();
              display = new DotDisplay(graph);
              graphState = originOfGraph;
              updateTitle();      
              initToolBar(toolBar, controller);
/*        
		// initialize menus
		JMenuBar  menubar    = new JMenuBar();
		JMenu     fileMenu   = new JMenu("File");
		fileMenu.setMnemonic('F');
		JMenu     layoutMenu = new JMenu("Layout");
		layoutMenu.setMnemonic('L');
		JMenu     helpMenu   = new JMenu("Help");
		helpMenu.setMnemonic('H');
                
		// build menus from data to avoid repetitive coding
		Object[][] fileMenuItems = {
				// menu Text, mnemonic, short cut
				{ OPEN, KeyEvent.VK_O, "ctrl O" },
				// note: ctrl E is also defined in Display class
				// also ctrl D is defined for showing some debug information
				// Action
				{ createExportItem() },
				null, // separator
				{ EXIT, KeyEvent.VK_X, "ctrl X" }
		};
	
		buildMenu(fileMenu, fileMenuItems, controller);
		
		Object[][] layoutMenuItems = {
				{ REDO, KeyEvent.VK_R, "ctrl R" },
				{ EDIT, KeyEvent.VK_F, "ctrl F" }
		};
		
		buildMenu(layoutMenu, layoutMenuItems, controller);
		
		Object[][] helpMenuItems = {
                  // No help for now
		  // { HELP, KeyEvent.VK_H, "F1" },
				{ ABOUT, KeyEvent.VK_A, null },
		};
		
		buildMenu(helpMenu, helpMenuItems, controller);
		
		menubar.add(fileMenu);
		menubar.add(layoutMenu);
		menubar.add(helpMenu);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(menubar);
		getContentPane().add(display);
		pack();
		setSize(900, 700);
		setLocationRelativeTo(null);
		setVisible(true);		
*/		

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new java.awt.BorderLayout());
        contentPane.add(toolBar, java.awt.BorderLayout.NORTH);
        contentPane.add(display, java.awt.BorderLayout.CENTER);
        setContentPane(contentPane);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setFont(font);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null); 
        setVisible(true);

	} //

   void initToolButton(JToolBar toolBar, JButton item, int mnemonic, MenuController controller) {
        item.setFont(font);
        item.setMaximumSize(new java.awt.Dimension(100, 35));
        item.setBorder(new javax.swing.border.EtchedBorder());
        toolBar.add(item);
        item.setMnemonic(mnemonic);
        item.addActionListener(controller);
    }
    
    // Initialize toolbar
    void initToolBar(JToolBar toolBar, MenuController controller) {
        toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));
        toolBar.setPreferredSize(new java.awt.Dimension(900, 35));
        toolBar.setFloatable(false);
        toolBar.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLUE));
        initToolButton(toolBar, toolOpen, OPENMN, controller);
        initToolButton(toolBar, toolExport, EXPORTMN, controller);
        toolExport.addActionListener(new ExportDisplayAction(display));
        initToolButton(toolBar, toolShow, SHOWMN, controller);
        toolShow.addActionListener(DotDisplay.controller);
        initToolButton(toolBar, toolRedo, REDOMN, controller);
        initToolButton(toolBar, toolEdit, EDITMN, controller);
        initToolButton(toolBar, toolHelp, HELPMN, controller);
        initToolButton(toolBar, toolAbout,ABOUTMN, controller);
        initToolButton(toolBar, toolExit, EXITMN, controller);
    }

    /**
	 * Reads the menu item descriptions from the given array and adds
	 * the found/created menu items to the menu.
	 * 
	 * @param menu   where to add the menu items to
	 * @param items  
	 *   the array containing the menu items or the data needed
	 *   to create them. Items are added to the menu in the order
	 *   given. Each item in the array should be an array containing either:
	 *     - String menuText, Integer mnemonic, String accelerator
	 *     - JMenuItem menuItem, String accelerator
	 *     - null for menu separator
	 * @param listener
	 *   the listener for menu actions
	 */
         /*
	private void buildMenu(JMenu menu, Object[][] items, ActionListener listener) {
		for(Object[] data : items) {
			if(data == null)
				menu.addSeparator();
			else if(data[0] instanceof Action) {
				menu.add((Action) data[0]);
			} else if(data[0] instanceof JMenuItem) {
				JMenuItem menuItem = (JMenuItem) data[0];
				String acceleratorKeyStroke = (String) data[1];
				menu.add(menuItem);
				(menuItem).setAccelerator(KeyStroke.getKeyStroke(acceleratorKeyStroke));
				menuItem.addActionListener(listener);
			} else {
				String text = (String) data[0];
				int mnemonic = ((Integer) data[1]).intValue();
				String acceleratorKeyStroke = (String) data[2];
				
				JMenuItem menuItem = new JMenuItem(text, mnemonic);
				menuItem.setAccelerator(KeyStroke.getKeyStroke(acceleratorKeyStroke));
				menuItem.addActionListener(listener);
				menu.add(menuItem);
			}
		}
	}
*/	
	/**
	 * Creates an action for "export as image". The ExportDisplayAction
	 * class has almost everything needed, but some things need to be added.
	 *  
	 * @return an action for "export as image"
	 */
/*
         private Action createExportItem() {
		Action a = new ExportDisplayAction(display);
		a.putValue(Action.NAME, EXPORT);
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getAWTKeyStroke("ctrl typed E"));
		a.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_E);
		return a;
	}
*/
	/**
	 * Tries to set the application to use the native look and feel
	 */
	private static void setLookAndFeel() {
		try {
			String laf = UIManager.getSystemLookAndFeelClassName();				
			UIManager.setLookAndFeel(laf);	
		} catch ( Exception e ) { e.printStackTrace(); }
	} //
	
    
	/**
	 * Input controller for interacting with the application.
	 * This class is responsible for handling the menu actions
	 * for all menus.
	 */
	private class MenuController implements ActionListener {
		
		/** the frame of the dot editor window */
		DotEditorFrame editorFrame = null;
		
		// menu callbacks 

		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
                        boolean runFilterUpdate = false;
			String cmd = e.getActionCommand();
			if ( OPEN.equals(cmd) ) {				
				if ( fileChooser.showOpenDialog(display) == JFileChooser.APPROVE_OPTION ) {
					 File f = fileChooser.getSelectedFile();
					 runFilterUpdate = loadFile(f);
                                         toolShow.setText(SHOW_ALL);
					 if(editorFrame != null && editorFrame.isVisible()) {
						 editorFrame.reload(getFileName());
					 }
				} // - OPEN 
				
			} else if ( EXPORT.equals(cmd) ) {
			} else if ( SHOW_ALL.equals(cmd) ) {
			} else if ( SHOW_OPENED.equals(cmd) ) {
			} else if ( EXIT.equals(cmd) ) {
				System.exit(0);
				
			} else if (REDO.equals(cmd) ) {
				 display.runLayout();
				 
			} else if (EDIT.equals(cmd) ) {				
				if(editorFrame != null)
					editorFrame.dispose();
				editorFrame = new DotEditorFrame(iDot.this, display);
				editorFrame.setVisible(true);

            } else if(HELP.equals(cmd)) {
            	showHelp();

            } else if(ABOUT.equals(cmd)) {
            	showAbout();

            } else {
				throw new IllegalStateException("cmd " + cmd + " not handled");
			}
            if ( runFilterUpdate ) {
            	// will be true if the display needs updating
            	display.runFilterUpdate();
            }
		} //
		
	} // - MenuController

	/**
	 * Updates the title bar to show the name of the currently loaded graph
	 */
	private void updateTitle() {
		String titleString = Config.TITLE + " " + Config.VERSION;
		if ( saveFile != null ) {
                  titleString += " - " + saveFile.getName() + " ";
                  int n = 0;
                  if(display != null && display.getGraph() != null)
                          n = display.getGraph().getNodeCount();			
                  titleString += "[";
                  if(graphState != null) {				
                          titleString += graphState + ", ";
                  }
                  titleString += n + " states]";				
		}
		setTitle(titleString);
	} //		

	/** 
	 * Returns the file name of the currently opened graph
	 * 
	 * @return the file name
	 */
	public String getFileName() {
		return saveFile == null ? null : saveFile.getName();
	}
	
	
	/**
	 * Tries to save the given input string to a file. The file to be used is
	 * from the user using a standard JFileChooser dialog. The string to be
	 * saved is assumed to contain the contents of a DOT file.
	 *
	 * @param dotData  the string to be saved in a file
	 */
	public void save(String dotData) {
		
		if (fileChooser.showSaveDialog(display) == JFileChooser.APPROVE_OPTION ) {
			File f = fileChooser.getSelectedFile();

			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write(dotData /*display.getDotFileContents()*/);
				bw.close();
				if (Config.print) System.out.println("saved " + f.getName());
			} catch (Exception e) {
				JOptionPane.showMessageDialog(
						display,
						"Sorry, an error occurred while saving the graph.\n" +
						" ("+ e.getLocalizedMessage() + ")",
						"Error Saving Graph",
						JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Shows the help file.
	 */
	protected void showHelp() {
		showUrlFrame(Config.TITLE + " " + Config.VERSION + " - Help", 
				getClass().getResource("/help.txt"), 650, 560);
	}

	/**
	 * Shows some information about the program.
	 */
	protected void showAbout() {
		showUrlFrame(Config.TITLE + " " + Config.VERSION + " - About", 
				getClass().getResource("/about.txt"), 600, 450);
	}

	/**
	 * Loads a file from an URL and shows it in a non-editable window. If the 
	 * URL contains an HTML document then local hyperlinks in the document
	 * should work.
	 * 
	 * @param title the title of the new frame
	 * @param url   the URL of the file to load. Plain text files and html 
	 *     files are supported
	 * @param width   width of the frame to create
	 * @param height  height of the frame to create
	 */
	protected void showUrlFrame(String title, URL url, int width, int height) {
		 JFrame helpframe=new JFrame(title);
		 
		 helpframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		 try {
			 JEditorPane epane=new JEditorPane(url);
			 epane.setEditable(false);
                         epane.setFont(new java.awt.Font(Config.TEXTAREA_FONT_NAME, 
                           Config.TEXTAREA_FONT_STYLE, Config.TEXTAREA_FONT_SIZE));
    
			 helpframe.getContentPane().add(new JScrollPane(epane));
			 epane.setMargin(new Insets(10, 30, 10, 30));
			 epane.addHyperlinkListener(new HyperlinkListener() {
				 // from java api comments for JEditorPane: 
				 public void hyperlinkUpdate(HyperlinkEvent e) {
					 if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						 JEditorPane pane = (JEditorPane) e.getSource();
						 if (e instanceof HTMLFrameHyperlinkEvent) {
							 HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
							 HTMLDocument doc = (HTMLDocument)pane.getDocument();
							 doc.processHTMLFrameHyperlinkEvent(evt);
						 } else {
							 try {
								 pane.setPage(e.getURL());
							 } catch (Throwable t) {
								 t.printStackTrace();
							 }
						 }
					 }
				 }
			 });
		 } catch(IOException e) {
			 helpframe.getContentPane().add(new JLabel("Source file (" + 
					 url + ") couldn't be loaded: "+e.getMessage()));
		 }
		 helpframe.pack();
		 helpframe.setSize(width, height);
		 helpframe.setLocationRelativeTo(null);
		 helpframe.setVisible(true);
	}

	
	/**
	 * Tries to load the state diagram from the specified file. The file 
	 * is assumed to be in DOT format.
	 * After the file is loaded, a layout operation (using DOT) is attempted.
	 * 
	 * @param graphFile
	 * @return true, if the file was loaded successfully and the laying out
	 *         the graph using DOT completed without errors
	 */
	protected boolean loadFile(File graphFile) {
		boolean fileLoaded = false;
		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(graphFile)));
			
			long size = graphFile.length();
			if(size > 1024*1024)
				throw new IOException("File too big (> 1 MB): " + graphFile);
			
			StringBuilder sb = new StringBuilder((int) size);
			String line;			
			do {
				line = br.readLine();
				if(line != null) {
					sb.append(line).append('\n');
				}				
			} while(line != null);
			
			display.setDotFileContents(sb.toString());

			saveFile = graphFile;
			graphState = null;			
			if (Config.print) System.out.println("opened " + graphFile.getName());				

			display.runDOTLayout();
			updateTitle();
			fileLoaded = true;		
		} catch ( Exception ex ) {
			JOptionPane.showMessageDialog(
					display,
					"Sorry, an error occurred while loading the graph.\n" +
					"("+ ex.getLocalizedMessage() + ")",
					"Error Loading Graph",
					JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
		return fileLoaded;
	}

} // end of class iDot
