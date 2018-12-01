package prefuse.demos.yamvia;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.DataShapeAction;
import prefuse.action.filter.VisibilityFilter;
import prefuse.action.layout.AxisLabelLayout;
import prefuse.action.layout.AxisLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.controls.Control;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.ToolTipControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Table;
import prefuse.data.expression.AndPredicate;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.io.DelimitedTextTableReader;
import prefuse.data.io.sql.ConnectionFactory;
import prefuse.data.io.sql.DatabaseDataSource;
import prefuse.data.query.ListQueryBinding;
import prefuse.data.query.RangeQueryBinding;
import prefuse.data.query.SearchQueryBinding;
import prefuse.render.AxisRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.Renderer;
import prefuse.render.RendererFactory;
import prefuse.render.ShapeRenderer;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.UpdateListener;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JRangeSlider;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualTable;
import prefuse.visual.expression.VisiblePredicate;
import prefuse.visual.sort.ItemSorter;

public class DataPlot extends JPanel{

	public static DatabaseDataSource db = null;
	public static Table t = null;
	public static VisualTable vt = null;
	
	
	/*public static void main(String[] args) {
        JFrame f = demo();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
    
    public static JFrame demo() {
        // load the data
        Table t = null;
        
        String tquery = null;
		
        tquery="SELECT mname, myear, gname, mrating FROM "Movie" "M", "Genre" "G", "GMInvolvement" "GM" WHERE "+
		""M".oid = "GM".oidmovie AND "G".oid = "GM".oidgenre AND "M".mrating IS NOT NULL AND "M".myear IS NOT NULL";
    	try {
            DatabaseDataSource db = ConnectionFactory.getDatabaseConnection("org.postgresql.Driver", "jdbc:postgresql://localhost/infoviz", "ivipi", "ivipi");
            t = db.getData(t, tquery, "mname");
            db.loadData(t, tquery, "mname");
        } catch ( Exception e ) {
            e.printStackTrace();
        } 
        
        JFrame frame = new JFrame("YAMVIA. yetanothermovievisualizationapproach.");
        frame.setContentPane(new DataShit(t));
        frame.pack();
        return frame;
    }*/
    
    // ------------------------------------------------------------------------
    private String m_title = "YAMVIA.";

    private JFastLabel m_instr = new JFastLabel("hover over a movie item...");
//    private JFastLabel m_details;
    
    public static Visualization m_vis;
    private Display m_display;
    private Rectangle2D m_dataB = new Rectangle2D.Double();
    private Rectangle2D m_xlabB = new Rectangle2D.Double();
    private Rectangle2D m_ylabB = new Rectangle2D.Double();
    
    public DataPlot(String query) 
    {
        super(new BorderLayout());
        
        //tquery="SELECT mname, myear, gname, mrating FROM \"Movie\" \"M\", \"Genre\" \"G\", \"GMInvolvement\" \"GM\" WHERE "+
		//"\"M\".oid = \"GM\".oidmovie AND \"G\".oid = \"GM\".oidgenre AND \"M\".mrating IS NOT NULL AND \"M\".myear IS NOT NULL";
    	try {
            db = ConnectionFactory.getDatabaseConnection("org.postgresql.Driver", "jdbc:postgresql://localhost/infoviz", "ivipi", "ivipi");
            t = db.getData(t, query, "mname");
            db.loadData(t, query, "mname");
        } catch ( Exception e ) {
            e.printStackTrace();
        } 
        
        // --------------------------------------------------------------------
        // STEP 1: setup the visualized data
        final String group = "table";
        
        final Visualization vis = new Visualization();
        m_vis = vis;       
        
        Predicate p = (Predicate)
        ExpressionParser.parse("[mrating] >= 0"); 
        vt = vis.addTable(group, t, p);
        
        
        
        LabelRenderer sr = new LabelRenderer("mname");
        sr.setRoundedCorner(8, 8);
        sr.setMaxTextWidth(64);
        MyRendererFactory mrf = new MyRendererFactory(sr);
        vis.setRendererFactory(mrf);
        
        
        // --------------------------------------------------------------------
        // STEP 2: create actions to process the visual data

        // set up dynamic queries, search set
        RangeQueryBinding receiptsQ = new RangeQueryBinding(vt, "mrating");
        RangeQueryBinding yearQ = new RangeQueryBinding(vt, "myear");
        ListQueryBinding   yearsQ    = new ListQueryBinding(vt, "myear");
       // ListQueryBinding genreQ = new ListQueryBinding(vt, "gname");
        SearchQueryBinding searchQ = new SearchQueryBinding(vt, "mname");
        
        // construct the filtering predicate
        AndPredicate filter = new AndPredicate(yearsQ.getPredicate());
        //filter.add(genreQ.getPredicate());
        filter.add(searchQ.getPredicate());
        filter.add(receiptsQ.getPredicate());
        
        // set up the actions
        AxisLayout xaxis = new AxisLayout(group, "myear",
                Constants.X_AXIS, VisiblePredicate.TRUE);
        xaxis.setRangeModel(yearQ.getModel());

        //xaxis.add(new ForceDirectedLayout("myear", true));
        yearQ.getNumberModel().setValueRange(1999, 2006, 1999, 2006);

        yearQ.getNumberModel().setValueRange(2000, 2006, 2000, 2006);
        
        AxisLayout yaxis = new AxisLayout(group, "mrating",
                Constants.Y_AXIS, VisiblePredicate.TRUE);
        yaxis.setRangeModel(receiptsQ.getModel());
        receiptsQ.getNumberModel().setValueRange(0.0,10.0,0.0,10.0);
        
        xaxis.setLayoutBounds(m_dataB);
        yaxis.setLayoutBounds(m_dataB);
        
        AxisLabelLayout ylabels = new AxisLabelLayout("ylab", yaxis, m_ylabB);
        System.out.println (ylabels.getSpacing());
        AxisLabelLayout xlabels = new AxisLabelLayout("xlab", xaxis, m_xlabB, 150);
        vis.putAction("xlabels", xlabels);
        
        int[] palette = ColorLib.getHotPalette(19);
        
        DataColorAction fill = new DataColorAction(group, "oidgenre",
                Constants.ORDINAL, VisualItem.FILLCOLOR, palette);
        ColorAction text = new ColorAction (group, VisualItem.TEXTCOLOR, ColorLib.gray(0));  
        ColorAction stroke = new ColorAction (group, VisualItem.STROKECOLOR, ColorLib.gray(255));
        
        ActionList draw = new ActionList();
        draw.add(fill);
        draw.add(stroke);
        draw.add(xaxis);
        draw.add(yaxis); 
        draw.add(ylabels);
        draw.add(xlabels);
        draw.add(text);
        draw.add(new RepaintAction());
        vis.putAction("draw", draw);

        ActionList update = new ActionList();
        update.add(new VisibilityFilter(group, filter));
        //update.add(xaxis);
        //update.add(yaxis);
        //update.add(ylabels);
        update.add(fill);
        update.add(stroke);
        update.add(xaxis);
        update.add(yaxis); 
        update.add(ylabels);
        update.add(xlabels);
        update.add(text);
        update.add(new RepaintAction());
        vis.putAction("update", update);
        
        UpdateListener lstnr = new UpdateListener() {
            public void update(Object src) {
                vis.run("update");
            }
        };
        filter.addExpressionListener(lstnr);
        
        // --------------------------------------------------------------------
        // STEP 4: set up a display and ui components to show the visualization

        m_display = new Display(vis);
        /*m_display.setItemSorter(new ItemSorter() {
            public int score(VisualItem item) {
                int score = super.score(item);
                if ( item.isInGroup(group) )
                    score += item.getInt("mrating");
                return score;
            }
        });*/
        m_display.setBorder(BorderFactory.createEmptyBorder(0,50,50,50));
        m_display.setSize(1024,460);
        m_display.setHighQuality(false);
        m_display.setBackground(new java.awt.Color(0, 0, 0));
        m_display.addControlListener(new ZoomControl());
        m_display.addControlListener(new DragControl());
        m_display.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                displayLayout();
            }
        });
        displayLayout();
        
//        m_details = new JFastLabel(m_title);
//        m_details.setPreferredSize(new Dimension(75,20));
//        m_details.setVerticalAlignment(SwingConstants.BOTTOM);
        
        m_instr.setPreferredSize(new Dimension(500, 20));
        m_instr.setHorizontalAlignment(SwingConstants.RIGHT);
        m_instr.setVerticalAlignment(SwingConstants.BOTTOM);
        
        ToolTipControl ttc = new ToolTipControl("mrating");
        Control hoverc = new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent evt) {
                if ( item.isInGroup(group) ) {
                  m_instr.setText(item.getString("mname"));
                  item.setFillColor(item.getStrokeColor());
                  item.setStrokeColor(ColorLib.gray(255));
                  item.getVisualization().repaint();
                }
            }
            public void itemExited(VisualItem item, MouseEvent evt) {
                if ( item.isInGroup(group) ) {
                  m_instr.setText("hover over a movie item...");
                  item.setFillColor(item.getEndFillColor());
                  item.setStrokeColor(item.getEndStrokeColor());
                  item.getVisualization().repaint();
                }
            }
        };
        m_display.addControlListener(ttc);
        m_display.addControlListener(hoverc);
        
        
        // --------------------------------------------------------------------        
        // STEP 5: launching the visualization
        
        this.addComponentListener(lstnr);
        
        // details
//        Box infoBox = new Box(BoxLayout.X_AXIS);
//        infoBox.add(Box.createHorizontalStrut(5));
//        infoBox.add(Box.createHorizontalGlue());
//        infoBox.add(Box.createHorizontalStrut(5));
//        infoBox.add(m_instr);
//        infoBox.add(Box.createHorizontalStrut(5));

        // set up search box
        JSearchPanel msearcher = searchQ.createSearchPanel();
        msearcher.setLabelText("Movie: ");
        msearcher.setShowResultCount(true);
        msearcher.setBorder(BorderFactory.createEmptyBorder(5,5,5,0));
        
        // create dynamic queries
        Box radioBox = new Box(BoxLayout.X_AXIS);
        radioBox.add(Box.createHorizontalStrut(5));
        radioBox.add(msearcher);
        radioBox.add(m_instr);
        radioBox.add(Box.createHorizontalGlue());
        radioBox.add(Box.createHorizontalStrut(5));
        radioBox.add(Box.createHorizontalStrut(16));

  
        
        JRangeSlider slidery = receiptsQ.createVerticalRangeSlider();
        slidery.setThumbColor(null);
        slidery.setMinExtent(1/4);
        slidery.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                m_display.setHighQuality(false);
            }
            public void mouseReleased(MouseEvent e) {
                m_display.setHighQuality(false);
                m_display.repaint();
            }
        });
        
        JRangeSlider sliderx = yearQ.createHorizontalRangeSlider();
        sliderx.setThumbColor(null);
        sliderx.setMinExtent(1/16);
        sliderx.addMouseListener(new MouseAdapter(){
        	public void mousePressed(MouseEvent f){
        		m_display.setHighQuality(false);
        	}
        	public void mouseReleased(MouseEvent f){
        		m_display.setHighQuality(false);
        		m_display.repaint();
        	}
        });
        
      
        
        
        vis.run("draw");

        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(m_display, BorderLayout.CENTER);
        centerPanel.add(slidery, BorderLayout.EAST);
        
        add(centerPanel, BorderLayout.CENTER);
        add(radioBox, BorderLayout.SOUTH);
        UILib.setColor(this, new java.awt.Color(0, 0, 0), Color.GRAY);
        m_instr.setFont(FontLib.getFont("Tahoma", 16));
    }
    
    public void displayLayout() {
        Insets i = m_display.getInsets();
        int w = m_display.getWidth();
        int h = m_display.getHeight();
        int iw = i.left+i.right;
        int ih = i.top+i.bottom;
        int aw = 85;
        int ah = 15;
        
        m_dataB.setRect(i.left, i.top, w-iw-aw, h-ih-ah);
        m_xlabB.setRect(i.left, h-ah-i.bottom, w-iw-aw, ah-10);
        m_ylabB.setRect(i.left, i.top, w-iw, h-ih-ah);
        
        m_vis.run("update");
        m_vis.run("xlabels");
    }
 
    
    public class MyRendererFactory implements RendererFactory{
    	
    	LabelRenderer sr;
    	Renderer arY = new AxisRenderer(Constants.RIGHT, Constants.TOP);
        Renderer arX = new AxisRenderer(Constants.CENTER, Constants.FAR_BOTTOM);
        
        public MyRendererFactory(LabelRenderer lr){
        	sr = lr;
        }
        
        public Renderer getRenderer(VisualItem item) {
            return item.isInGroup("ylab") ? arY :
                   item.isInGroup("xlab") ? arX : sr;
        }
    }
    
}
