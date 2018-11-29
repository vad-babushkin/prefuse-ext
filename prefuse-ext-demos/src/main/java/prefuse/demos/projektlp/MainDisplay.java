/**
 * ZoneSetup2.java
 * Han Dong
 *
 * The main of the program that sets up the display and the other components.
 * Calls other classes to manage the displaying of the color data.
 *
 * TODO October 29, 2009	Fix Start, Stop, Resume, Pause. Throws exception after
 * stopping and starting application. May need to reset arrays and other data before
 * starting the application again.
 *
 */
package prefuse.demos.projektlp;

import java.awt.BorderLayout;
import java.awt.Color;
//import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import prefuse.util.ColorLib;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
//import profusians.zonemanager.BarChartDecorator_ZoneAggregateItemFieldValueAssignment;
//import profusians.zonemanager.BarChartDecorator_ZoneFactory;
import profusians.demos.zonemanager.fun.zonefactories.BarChartDecorator_ZoneFactory;
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.action.ZoneGuardAction;
//import profusians.zonemanager.util.ZoneBorderDrawing;
import profusians.zonemanager.zone.RectangularZone;
import profusians.zonemanager.zone.Zone;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.CircularZoneShape;
import profusians.zonemanager.zone.shape.RectangularZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

@SuppressWarnings({ "unchecked", "serial" , "unused"})
public class MainDisplay extends Display
{
	public static ZoneManager zoneManager;
	public static ArrayList<Node> nodeList = null;
	public static ArrayList<Wavelength> wavelengthList = null;
	public static boolean autoPause = false;
	public static String file_location = "";
	public static long resumeCounter = 0;
	public boolean[] colorChanged;

	Menu menu = null;
	Graph graph = null;
	Graph barGraph = null;
	Graph edgeGraph = null;

	ExecutorService threadExecutor = null;
	NodeColorVisualizer ncv = null;
	NodeGraphVisualizer ngv = null;
	WavelengthGraphVisualizer wgv = null;
	WavelengthDataVisualizer wdv = null;
	NetworkDataVisualizer ndv = null;

	Iterator nodeItems = null;

	NodeLocationList nlocate_list = null;
	NodeLocationList elocate_list = null;

	Buttons buttons = null;
	CheckBoxes checkBoxes = null;
	Slider slider = null;

    String ptrStrAry = null;
    String ptrStrAry2 = null;
    Pattern ptrAry = null;
    Pattern ptrAry2 = null;

	Matcher m = null;
	Matcher m2 = null;
	Iterator nodes = this.getItr();
	NodeItem aNodeItem = null;

	ProcessData pd = null;

	public DefaultRendererFactory rf = null;
	public ActionList colors = null;
	public ActionList catchThem = null;
	public ColorAction prev_aFill = null;
	public ColorAction curr_aFill = null;

	ExecutorService e = null;

	LinkSet linkSet = null;
	NetworkSet networkSet = null;
	NodeSet nodeSet = null;

	static ArrayList<String> linkCheckBox = null;
	static ArrayList<String> colorValues = null;

    NodeAction na;

    ProgressBar pb = null;

    Teleport tp = null;

    ColorChooser colorChooser = null;
    ZoneColors defaultOEO_port_util_C;

	/**
	 * Constructor that initializes everything, adds graph data to m_vis
	 */
	public MainDisplay(String file_location, DataSets ds)
	{
		super(new Visualization());
		MainDisplay.file_location = file_location;

		linkCheckBox = new ArrayList<String>();
		colorValues = new ArrayList<String>();

		linkSet = ds.getLinkSet();
		networkSet = ds.getNetworkSet();
		nodeSet = ds.getNodeSet();

		//colorChooser = new ColorChooser(MainDisplay.file_location, nodeSet);

		readColorValues();

		//good, node location list
		nlocate_list = new NodeLocationList("node");
		//elocate_list = new NodeLocationList("edge");

		//good, wavelengthlist
		wavelengthList = new ArrayList<Wavelength>();
		readWavelengthData();

		//writeEdgeDataXML();

		//good
		addGraph();

		//good
		addNodeToGraph();

		//good, sets up menu
		menu = new Menu(nlocate_list);

		//good
		setPatternArrays();

		//good
		//bGraph = new BlockingGraph();

		//good
		pd = new ProcessData(this);

		//good
		buttons = new Buttons(pd, this);

		//good
		checkBoxes = new CheckBoxes(nodeSet.getNSList2(), linkSet.getLSList2(), this);

		//good
		slider = new Slider();

		//good
		nodeList = new ArrayList<Node>();

		//good
		na = new NodeAction(this.nlocate_list);
		this.addControlListener(na);

		//good
		pb = new ProgressBar();

		//good
		tp = new Teleport(this, buttons);

		//good
		ndv = new NetworkDataVisualizer();

		threadExecutor = Executors.newFixedThreadPool(4);

		showGUI();

		//bar graph for each node
		ngv = new NodeGraphVisualizer(nodeItems, buttons, this);

		//numerical data representation for each edge
		wdv = new WavelengthDataVisualizer(edgeGraph, this);

		setColorChanged();

	}
	public ProcessData getProcessData()
	{
		return this.pd;
	}

	public boolean getColorChanged(int i)
	{
		return colorChanged[i];
	}

	private void setColorChanged()
	{
		colorChanged = new boolean[this.graph.getNodeCount()];
		for(int i = 0; i < this.graph.getNodeCount(); i += 2)
		{
			colorChanged[i] = false;
			colorChanged[i+1] = false;
		}
	}

	/**
	 * Initializes the computation of node color and barGraph
	 * and refreshes the visualization
	 * @param networkBlocking
	 * @param trafficData
	 * @param index
//	 * @param nodeList
	 * @throws InterruptedException
	 */
	public void startVisualizer(double networkBlocking, StringBuffer networkData, long index, StringBuffer trafficData) throws InterruptedException
	{
		ngv.setBlocking(networkBlocking);
		ndv.setData(networkData);
		pb.setProgressData(index);

		//multithread display
		threadExecutor.execute(ngv);
		threadExecutor.execute(pb);
		threadExecutor.execute(ndv);
		threadExecutor.execute(wdv);

		Thread.sleep(10);
		MainDisplay.zoneManager.recalculateFlexibility();
		m_vis.run("catchThem");
		m_vis.repaint();

		ProcessData.prev_blocking_num = ProcessData.blocking_num;

		Iterator nodeItems = m_vis.items("graph.nodes");
		NodeItem aNodeItem = null;
		if(MainDisplay.autoPause == true && networkBlocking > 0.0)
		{
			ArrayList<NodeData> temp;
			String s = "BLOCKING at: ";
			for(int i = 0; i < MainDisplay.nodeList.size(); i ++)
			{
				aNodeItem = (NodeItem) nodeItems.next();
				if(MainDisplay.nodeList.get(i).getNDList().get(0).getData() > 0)
				{
					s += "Node "+(i+1)+" ";
					aNodeItem.setFillColor(ColorLib.hex("#FF0000"));
				}
			}
			AlertWindow aw = new AlertWindow(s);
			System.out.println("Paused");

			m_vis.repaint();
			this.repaint();
			buttons.getPause().doClick();
		}
	}

	private void showGUI()
	{
		//good
		this.initZoneManager();

		//good
		Menu menu = this.getMenu();
		menu.setAction(m_vis);

		/** good
		 *
		 */
		//sets up the frame that will hold the visualizations
		JFrame frame = new JFrame("Fiber Network");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setLayout();

		frame.setJMenuBar(menu.getMenuBar());

		//frame.getContentPane().add(temp.getBlockingGraph().getChartPanel(), BorderLayout.LINE_START);

		frame.getContentPane().add(this, BorderLayout.CENTER);

        JPanel opanel = new JPanel();
        opanel.setLayout(new BoxLayout(opanel, BoxLayout.Y_AXIS));
        opanel.add(this.buttons.getStart());
        opanel.add(this.buttons.getPause());
        opanel.add(this.buttons.getResume());
        for(int i = 0; i < this.checkBoxes.getNSB().length; i ++)
        {
        	opanel.add(this.checkBoxes.getNSB()[i]);
        }
        opanel.add(this.checkBoxes.getAutoPause());
        opanel.add(this.buttons.getScan());
        opanel.add(this.buttons.getRefresh());

        JPanel opanel2 = new JPanel();
        opanel2.setLayout(new BoxLayout(opanel2, BoxLayout.X_AXIS));
        opanel2.add(this.getSlider());
        opanel2.add(this.getPb());
        opanel2.add(this.getTp());
        opanel2.add(this.getNdv());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        								this, opanel);
        split.setOneTouchExpandable(true);
        split.setDividerLocation(850);

        JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        						split, opanel2);
        split2.setDividerLocation(600);

        frame.add(split2);
        frame.pack();           // layout components in window
        frame.setVisible(true); // show the window
	}

	private static void readColorValues()
	{
		FileInputStream colorStream;
		String strLine;

		try
		{
			colorStream = new FileInputStream("Colors.txt");
			DataInputStream in = new DataInputStream(colorStream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));

	        while ((strLine = br.readLine()) != null)
	        {
	            colorValues.add(strLine);
	        }
	        in.close();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public NetworkDataVisualizer getNdv() {
		return ndv;
	}

	public Slider getSlider() {
		return slider;
	}

	public ProgressBar getPb() {
		return pb;
	}

	public Teleport getTp() {
		return tp;
	}

	public Visualization getVis()
	{
		return m_vis;
	}

	public void writeEdgeDataXML()
	{
		int i, j, counter = 0;

		try
		{
			FileWriter fstream = new FileWriter("EdgeData.xml");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			out.newLine();
			out.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\">");
			out.newLine();
			out.write("<graph edgedefault=\"undirected\">");
			out.newLine();
			out.write("<key id=\"name\" for=\"node\" attr.name=\"name\" attr.type=\"string\" />");
			out.newLine();
			out.write("<key id=\"num\" for=\"node\" attr.name=\"num\" attr.type=\"integer\" />");
			out.newLine();

			for(i = 0; i < wavelengthList.size(); i ++)
			{
				counter++;
				out.write("<node id=\""+(counter)+"\">");
				out.newLine();
				out.write("<data key=\"name\"></data>");
				out.newLine();
				out.write("<data key=\"num\">"+(counter)+"</data>");
				out.newLine();
				out.write("</node>");
				out.newLine();
			}
			out.write("</graph>");
			out.newLine();
			out.write("</graphml>");
			out.newLine();
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void readWavelengthData()
	{
		FileInputStream in = null;
        Scanner scan = null;
        int source, destination;
        int midX, midY;

        //tries to open file to read
        try
        {
        	in = new FileInputStream("NodeData.xml");
        	scan = new Scanner(in);
        }
        catch (IOException e)
        {
        	System.out.println("Cannot open NodeData.xml");
        }

        //regex that parses input strings
        String ptrStr = "<edge source=\"(.*?)\" target=\"(.*)\"></edge>";
        Pattern p = Pattern.compile(ptrStr);
		Matcher m = null;

		//reads in data and parses
		while(scan.hasNextLine())
		{
			String str = scan.nextLine();

			m = p.matcher(str);

			if(m.find())
			{
				source = Integer.parseInt(m.group(1).trim());
				destination = Integer.parseInt(m.group(2).trim());

				midX = (nlocate_list.getXCoor(source - 1) + nlocate_list.getXCoor(destination - 1)) / 2;
				midY = (nlocate_list.getYCoor(source - 1) + nlocate_list.getYCoor(destination - 1)) / 2;

				wavelengthList.add(new Wavelength(source, destination, midX, midY, this.linkSet.getLSList2()));
			}
		}
	}

	public NodeSet getNodeSet()
	{
		return this.nodeSet;
	}

	public LinkSet getLinkSet()
	{
		return this.linkSet;
	}

	private void setPatternArrays()
	{
		ArrayList nsl = nodeSet.getNSList();
		ArrayList lsl = linkSet.getLSList();

		ptrStrAry = "\\[node\\]";
		for(int i = 0; i < nsl.size(); i ++)
		{
			if(i == (nsl.size()-1))
			{
				ptrStrAry += " "+nsl.get(i)+"=(.*)";
			}
			else if(nsl.get(i).equals("node-ooo-traffic-bw-ratio"))
			{
				ptrStrAry += nsl.get(i)+"=(.*),";
			}
			else
			{
				ptrStrAry += " "+nsl.get(i)+"=(.*),";
			}
		}
		//ptrStrAry = "\\[node\\] load=(.*?), node=(.*?), node-blocking=(.*?), node-oeo-port-util=(.*?), node-oeo-traffic-bw-ratio=(.*?),node-ooo-traffic-bw-ratio=(.*?), node-unused-bw-ratio=(.*)";
		System.out.println("ptrStrAry= "+ptrStrAry);
		ptrAry = Pattern.compile(ptrStrAry);


		ptrStrAry2 = "\\[link\\]";
		for(int i = 0; i < lsl.size(); i++)
		{
			if(i == (lsl.size()-1))
			{
				ptrStrAry2 += " "+lsl.get(i)+"=(.*)";
			}
			else
			{
				ptrStrAry2 += " "+lsl.get(i)+"=(.*),";
			}
			//ptrStrAry2 = "\\[link\\] source=(.*?), destination=(.*?), wavelength-usage-ratio=(.*), .*";
		}
		System.out.println("ptrStrAry2= "+ptrStrAry2);
		//System.out.println(ptrStrAry2);
		ptrAry2 = Pattern.compile(ptrStrAry2);

	}

	private void addGraph()
	{
		//reads NodeData.xml that contains initial data of the nodes
		try
	    {
	    	graph = new GraphMLReader().readGraph("NodeData.xml");
	    	edgeGraph = new GraphMLReader().readGraph("EdgeData.xml");
	    }
	    catch ( DataIOException e )
	    {
	    	e.printStackTrace();
	    	System.err.println("Error loading graph data. Exiting...");
	    	System.exit(1);
	    }

	    //adds the graph to visualization
		m_vis.addGraph("graph", graph);
		m_vis.addGraph("edgeGraph", edgeGraph);
	}

	private void addNodeToGraph()
	{
		//creates barGraph object
		barGraph = new Graph();

		//adds 9900 nodes to barGraph object
		for (int i = 0; i < 10000; i++)
		{
			barGraph.addNode();
		}

		//adds barGraph object to visualization
		m_vis.addGraph("barGraph", barGraph);

		//creates iterator for barGraph.nodes
		nodeItems = m_vis.items("barGraph.nodes");
	}

	/**
	 * returns menu instance
	 * @return
	 */
	public Menu getMenu()
	{
		return menu;
	}

	/**
	 * returns ZoneManager instance
	 * @return
	 */
	public ZoneManager getZoneManager()
	{
		return zoneManager;
	}

	/**
	 * returns iterator of barGraph.nodes
	 * @return
	 */
	public Iterator getItr()
	{
		return m_vis.items("barGraph.nodes");
	}

	/**
	 * creates zones and assigns specific nodes to those zones, the zones
	 * are located in coordinates as indicated by nodeLocationAry
	 */
	public void initZoneManager()
	{
		zoneManager = new ZoneManager(m_vis, new ForceSimulatorRemovableForces());
		Iterator nodeItems = m_vis.items("graph.nodes");
		Iterator edgeItems = m_vis.items("edgeGraph.nodes");
		NodeItem node = null;
		int zoneNumber, x = 0, y = 0, xconstant, wlconstant, i, j;

		//writes out data to test.xml to show where the barGraphs should be located,
		//corresponding to node locations
		try
		{
			FileWriter fstream = new FileWriter("test.xml");
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<zonexml>");
			out.newLine();

			for(i = 0; i < nlocate_list.getSize(); i ++)
			{
				nodeList.add(new Node(this.getNodeSet().getNSList2()));

				//sets location of each node
				node = (NodeItem) nodeItems.next();
				//System.out.println("node: "+node.getX()+" "+node.getY());
				//System.out.println("init zoneManager: "+nlocate_list.getXCoor(i)+" "+nlocate_list.getYCoor(i));
				//zoneNumber = zoneManager.createAndAddZone(new CircularZoneShape(nlocate_list.getXCoor(i), nlocate_list.getYCoor(i), 4));
				zoneNumber = zoneManager.createAndAddZone(new RectangularZoneShape(nlocate_list.getXCoor(i), nlocate_list.getYCoor(i),20,20));
				//zoneManager.addItemToZone(node, zoneNumber);
				zoneManager.addItemToZoneAndCatch(node, zoneNumber);

				xconstant = -28;
				for(j = 0; j < nodeSet.getNSList2().size(); j ++)
				{
					out.write("<zone shape=\"rectangular\" name="+"\"Node "+(i+1)+"_"+nodeSet.getNSList2().get(j)+"\""+" type=\"flexible\" shapedata=\""+(nlocate_list.getXCoor(i) + xconstant)+","+(nlocate_list.getYCoor(i)+10)+",7,8\" "+"itemcolor=\"0,0,0\" fillcolor=\""+colorValues.get(j)+"\"></zone>");
					out.newLine();
					xconstant -= 8;
				}
			}

			/*for(i = 0; i < wavelengthList.size(); i ++)
			{
				wlconstant = 0;
				for(j = 0; j < linkSet.getLSList2().size(); j ++)
				{
					out.write("<zone shape=\"rectangular\" name=\"Edge "+i+"_"+linkSet.getLSList2().get(j)+"\" type=\"flexible\" shapedata=\""+(wavelengthList.get(i).getMidX()-wlconstant)+","+wavelengthList.get(i).getMidY()+",7,8\" "+"itemcolor=\"0,0,0\" fillcolor=\"221,160,221\"></zone>");
					out.newLine();
					wlconstant -= 8;
				}
			}*/
			out.write("</zonexml>");
			out.close();
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}


		for(i = 0; i < wavelengthList.size(); i ++)
		{
			//sets location of each edge node (wavelength data)
			zoneNumber = zoneManager.createAndAddZone(new CircularZoneShape(wavelengthList.get(i).getMidX(), wavelengthList.get(i).getMidY(), 1));
			node = (NodeItem) edgeItems.next();
			zoneManager.addItemToZone(node, zoneNumber);
		}

		//creates LabelRenderer object that will show the name of the nodes
		LabelRenderer r = new LabelRenderer("name");
        //r.setRoundedCorner(20, 20);
        rf = new DefaultRendererFactory(r);
        m_vis.setRendererFactory(rf);

		//fill in colors for nodes
        ColorAction fill = new ColorAction("graph.nodes",
        		VisualItem.FILLCOLOR, ColorLib.gray(60));
        ColorAction text = new ColorAction("graph.nodes",
                VisualItem.TEXTCOLOR, ColorLib.gray(255));
        ColorAction edge = new ColorAction("graph.edges",
        		VisualItem.STROKECOLOR, ColorLib.gray(0));

        // create an action list containing all color assignments
        ActionList color = new ActionList();
        color.add(fill);
        color.add(text);
        color.add(edge);

        //fill in colors for edge nodes
        ColorAction fill2 = new ColorAction("edgeGraph.nodes",
        		VisualItem.FILLCOLOR, ColorLib.rgb(0, 0, 0));
        ColorAction text2 = new ColorAction("edgeGraph.nodes",
                VisualItem.TEXTCOLOR, ColorLib.gray(255));
        ActionList color2 = new ActionList();
        color2.add(fill2);
        color2.add(text2);


		ForceDirectedLayout fdl = new ForceDirectedLayout("graph", zoneManager.getForceSimulator(), false);

		//ForceDirectedLayout fdl = new ForceDirectedLayout("graph", false);
		ForceDirectedLayout fdl2 = new ForceDirectedLayout("edgeGraph", true);

		ActionList layout = new ActionList(100);
		layout.add(color);
		layout.add(color2);
		layout.add(new ZoneGuardAction(zoneManager));
		layout.add(fdl);
		layout.add(fdl2);
		layout.add(new RepaintAction());
		//layout.setStepTime(Activity.INFINITY);
		m_vis.putAction("layout", layout);

		setSize(900, 700);
		setHighQuality(false);
		pan(0, 0);
		addControlListener(new ZoomControl());
		addControlListener(new DragControl());
		addControlListener(new PanControl());
		//addPaintListener(new ZoneBorderDrawing(zoneManager));
		m_vis.setInteractive("graph.edges", null, false);
		m_vis.run("layout");

		zoneManager.setZoneFactory(new BarChartDecorator_ZoneFactory());
		//zoneManager.setZoneAggregateItemFieldValueAssignment(new BarChartDecorator_ZoneAggregateItemFieldValueAssignment());
		//zoneManager.addColumnToZoneAggregateTable("rotation", double.class);
		zoneManager.addZonesFromFile("test.xml");
        zoneManager.addZoneRenderer(rf);
        curr_aFill = zoneManager.getZoneColorAction();

		// bundle the color actions
		colors = new ActionList();
		colors.add(curr_aFill);

		catchThem = new ActionList();
		catchThem.setPacingFunction(new SlowInSlowOutPacer());
		catchThem.add(colors);
		catchThem.add(new ZoneGuardAction(zoneManager));
		//catchThem.add(fdl);
		catchThem.add(zoneManager.getZoneLayout(true));
		catchThem.add(new ColorAnimator("barGraph.nodes"));
		catchThem.add(new LocationAnimator("barGraph.nodes"));
		catchThem.add(new RepaintAction());
		m_vis.putAction("catchThem", catchThem);
		zoneManager.setAllZonesVisible(false);

		/*defaultLoad_C = MainDisplay.zoneManager.getZone("Node 1_load").getColors();
		defaultBlocking_C = MainDisplay.zoneManager.getZone("Node 1_node-blocking").getColors();
		defaultOEO_traffic_bw_ratio_C = MainDisplay.zoneManager.getZone("Node 1_node-oeo-traffic-bw-ratio").getColors();
		defaultOOO_traffic_bw_ratio_C = MainDisplay.zoneManager.getZone("Node 1_node-ooo-traffic-bw-ratio").getColors();
		defaultUnused_bw_ratio_C = MainDisplay.zoneManager.getZone("Node 1_node-unused-bw-ratio").getColors();
		defaultWavelength_C = MainDisplay.zoneManager.getZone("edge_0").getColors();*/
		defaultOEO_port_util_C = MainDisplay.zoneManager.getZone("Node 1_node-oeo-port-util").getColors();
	}

	public void changeColor(String zoneName, int i)
	{
		MainDisplay.zoneManager.getZone(zoneName).setColors(
				new ZoneColors(ColorLib.color(Color.black), ColorLib.color(Color.black)));
		curr_aFill = zoneManager.getZoneColorAction();
		colors = new ActionList();
		colors.add(curr_aFill);

		catchThem = new ActionList();
		catchThem.setPacingFunction(new SlowInSlowOutPacer());
		catchThem.add(colors);
		catchThem.add(new ZoneGuardAction(zoneManager));
		//catchThem.add(fdl);
		catchThem.add(zoneManager.getZoneLayout(true));
		catchThem.add(new ColorAnimator("barGraph.nodes"));
		catchThem.add(new LocationAnimator("barGraph.nodes"));
		catchThem.add(new RepaintAction());
		m_vis.putAction("catchThem", catchThem);
		this.colorChanged[i] = true;
	}

	public void defaultColor(String zoneName, int i)
	{
		MainDisplay.zoneManager.getZone(zoneName).setColors(this.defaultOEO_port_util_C);
		curr_aFill = zoneManager.getZoneColorAction();
		colors = new ActionList();
		colors.add(curr_aFill);

		catchThem = new ActionList();
		catchThem.setPacingFunction(new SlowInSlowOutPacer());
		catchThem.add(colors);
		catchThem.add(new ZoneGuardAction(zoneManager));
		//catchThem.add(fdl);
		catchThem.add(zoneManager.getZoneLayout(true));
		catchThem.add(new ColorAnimator("barGraph.nodes"));
		catchThem.add(new LocationAnimator("barGraph.nodes"));
		catchThem.add(new RepaintAction());
		m_vis.putAction("catchThem", catchThem);
		this.colorChanged[i] = false;
	}

	public void refresh()
	{
		this.repaint();
		m_vis.repaint();
	}

	public void test()
	{
		Iterator nodeItems = m_vis.items("graph.nodes");
		NodeItem aNodeItem = null;

		for(int i = 0; i < MainDisplay.nodeList.size(); i ++)
		{
			aNodeItem = (NodeItem) nodeItems.next();
			if(MainDisplay.nodeList.get(i).getNDList().get(0).getData() <= 0)
			{
				aNodeItem.setFillColor(ColorLib.gray(0));
			}
			else if(MainDisplay.nodeList.get(i).getNDList().get(0).getData() > 0)
			{
				aNodeItem.setFillColor(ColorLib.hex("#FF0000"));
			}
		}
		m_vis.repaint();
	}
}
