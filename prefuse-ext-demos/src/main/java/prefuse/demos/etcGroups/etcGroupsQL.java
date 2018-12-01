package prefuse.demos.etcGroups;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.lang.*;

import javax.swing.*;
import javax.swing.text.*;

import java.sql.*;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.GroupAction;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.PolarLocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.RadialTreeLayout;
import prefuse.action.layout.RandomLayout;
import prefuse.action.layout.graph.FruchtermanReingoldLayout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.DragControl;
import prefuse.controls.FocusControl;
import prefuse.controls.HoverActionControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Edge;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.Schema;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.DefaultTupleSet;
import prefuse.data.tuple.TupleSet;
import prefuse.data.search.SearchTupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.render.PolygonRenderer;
import prefuse.render.ImageFactory;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.PrefuseLib;
import prefuse.util.ui.JFastLabel;
import prefuse.util.ui.JSearchPanel;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualItem;
import prefuse.visual.VisualGraph;
import prefuse.visual.AggregateItem;
import prefuse.visual.AggregateTable;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import prefuse.data.expression.Predicate;
import prefuse.data.expression.BooleanLiteral;
import prefuse.data.expression.parser.ExpressionParser;

/**
 * /etc/groups
 *
 * @version 0.2
 * @author <a href="http://ydegoyon.free.fr">yves degoyon</a>
 */
public class etcGroupsQL extends Display implements ItemListener {

    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private static final String treeAggregates = "aggregates";
    private static final String linear = "linear";
    private static final String font = "Tahoma";
    private static final int tMaxTLength = 25;
    private static final int tMaxTLines = 25;
    
    // graphic structures
    private static JPanel gPanel;
    private static JTextPane description = new JTextPane();
    private static JScrollPane descriptionScrollPane;
    private static JTabbedPane legendTabbedPane;

    private static etcGroupsQL m_gview;
    private static Graph m_graph = null;
    private static LabelRenderer m_nodeRenderer;
    private static EdgeRenderer m_edgeRenderer;
    private static PolygonRenderer m_aggregateRenderer;
    // private static FruchtermanReingoldLayout m_thisLayout;
    // private RadialTreeLayout m_thisLayout;
    private RandomLayout m_thisLayout;
    private static CollapsedSubtreeLayout m_subLayout;
    private static AggregateLayout m_aggrLayout;
    private static AggregateDragControl m_agc;
    private static AggregateTable m_at;
    private static NodeDragControl m_noc;
    private static DescriptionMouseListener m_descriptionMouseListener = new DescriptionMouseListener();
    private static JGSearchPanel m_search;
    private static boolean m_resetButtons = false;
    private static String [] m_selectedFields = new String[10];
    private static JCheckBox [] m_selectCbs = new JCheckBox[10];
    private static int m_nbscb = 0;
    private static ButtonGroup m_STypeBG;
    private static JLabel m_searchResults;
    
    private static String m_label = "label";
    private static String m_host = "localhost";
    private static String m_dbName = "nodedges";
    private static String m_dbUser = "root";
    private static String m_dbPass = "";
    private static Connection m_sqlCon = null;

    // database description
    private static String[] m_nodeFields = new String[] {
         "nid",
         "name",
         "acronym",
         "location",
         "country",
         "website",
         "contact",
         "description",
         "type",
         "image",
         "theme",
         "groups",
         "esf1", 
         "esf2", 
         "esf3" 
         };

    private static Class[] m_nodeFieldsClass = new Class[] {
         int.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class,
         String.class
         };

    private static String[] m_edgeFields = new String[] {
         "source",
         "target",
         "type"
         };

    private static Class[] m_edgeFieldsClass = new Class[] {
         int.class,
         int.class,
         String.class
         };

    private static Table m_nodeTable;
    private static Table m_edgeTable;

    // fields filter hashes
    private static TreeSet themeSet;
    private static TreeSet nTypeSet;
    private static TreeSet imageSet;
    private static TreeSet lTypeSet;
    private static TreeSet countrySet;
    private static TreeSet groupSet;
    private static TreeSet esfSet;

    // utility hashes
    private static HashSet lCbSet;
    private static HashSet llCbSet;
    private static HashSet visibleItems;
    private static HashMap nodeIds;

    private static int nbMaxThemes = 30;
    private static int[] themeColors = new int[] {
            ColorLib.rgba(255,200,200,150), // theme-0.jpg
            ColorLib.rgba(200,255,200,150), // theme-1.jpg
            ColorLib.rgba(200,200,255,150), // theme-2.jpg
            ColorLib.rgba(80,156,93,150), // theme-3.jpg
            ColorLib.rgba(27,245,89,150), // theme-4.jpg
            ColorLib.rgba(39,67,247,150), // theme-5.jpg
            ColorLib.rgba(90,2,35,150), // theme-6.jpg
            ColorLib.rgba(98,234,145,150), // theme-7.jpg
            ColorLib.rgba(78,90,134,150), // theme-8.jpg
            ColorLib.rgba(234,28,178,150), // theme-9.jpg
            ColorLib.rgba(98,167,90,150), // theme-10.jpg
            ColorLib.rgba(76,167,37,150), // theme-11.jpg
            ColorLib.rgba(190,45,123,150), // theme-12.jpg
            ColorLib.rgba(89,173,34,150), // theme-13.jpg
            ColorLib.rgba(164,90,134,150), // theme-14.jpg
            ColorLib.rgba(93,145,28,150), // theme-15.jpg
            ColorLib.rgba(78,22,123,150), // theme-16.jpg
            ColorLib.rgba(78,123,39,150), // theme-17.jpg
            ColorLib.rgba(89,62,156,150), // theme-18.jpg
            ColorLib.rgba(89,134,12,150), // theme-19.jpg
            ColorLib.rgba(67,183,134,150), // theme-20.jpg
            ColorLib.rgba(97,242,17,150), // theme-21.jpg
            ColorLib.rgba(134,13,231,150), // theme-22.jpg
            ColorLib.rgba(32,167,98,150), // theme-23.jpg
            ColorLib.rgba(156,198,12,150), // theme-24.jpg
            ColorLib.rgba(45,167,189,150), // theme-25.jpg
            ColorLib.rgba(89,34,234,150), // theme-26.jpg
            ColorLib.rgba(145,90,25,150), // theme-27.jpg
            ColorLib.rgba(178,89,134,150), // theme-28.jpg
            ColorLib.rgba(67,189,78,150), // theme-29.jpg
        };
    private static int[] rthemeColors;

    private static int nbMaxEdgeTypes = 5;
    private static int[] edgeColors = new int[] {
            ColorLib.rgb(255,180,180), 
            ColorLib.rgb(0,230,0), 
            ColorLib.rgb(200,200,200),
            ColorLib.rgb(45,123,231),
            ColorLib.rgb(123,245,20)
        };
    private static int[] redgeColors;

    private static boolean[] filterTypes;
    private static boolean[] filterCountries;
    private static boolean[] filterThemes;
    private static boolean[] filterGroups;
    private static boolean[] filterEsfs;

    private static boolean initialState;

    private static String filterTString; // condition used for types
    private static String filterDString; // condition used for themes
    private static String filterCString; // condition used for countries
    private static String filterGString; // condition used for groups
    private static String filterEString; // condition used for esfs
    private static String filterString; // condition used for the filters

    // --------------------------------------------------- //
    //                                                     //
    // init() : init global static structures              //
    //                                                     //
    // --------------------------------------------------- //

    public static void init() {
      filterTString = new String(""); // condition used for types
      filterDString = new String(""); // condition used for themes
      filterCString = new String(""); // condition used for countries
      filterGString = new String(""); // condition used for groups
      filterEString = new String(""); // condition used for esfs
      filterString = new String(""); // condition used for the filters
      themeSet = new TreeSet();
      nTypeSet = new TreeSet();
      imageSet = new TreeSet();
      lTypeSet = new TreeSet();
      countrySet = new TreeSet();
      groupSet = new TreeSet();
      esfSet = new TreeSet();
      lCbSet = new HashSet();
      llCbSet = new HashSet();
      visibleItems = new HashSet();
      nodeIds = new HashMap();
    }

    public static DescriptionMouseListener getMouseListener() {
        return m_descriptionMouseListener;
    }

    protected static void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, font);

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);
    }
    
    // --------------------------------------------------- //
    //                                                     //
    // hideAll() : hide all the nodes and edges            //
    //                                                     //
    // --------------------------------------------------- //

    public static void hideAll(Visualization viz) {
      TupleSet oNodes  = viz.getGroup(treeNodes);
      TupleSet oEdges  = viz.getGroup(treeEdges);
      TupleSet oAggregates  = viz.getGroup(treeAggregates);
      Tuple tuple;
      VisualItem item;
      Iterator iNodes = oNodes.tuples();
      Iterator iEdges = oEdges.tuples();
      Iterator iAggregates = oAggregates.tuples();
      while ( iNodes.hasNext() )
      {
         tuple = (Tuple)iNodes.next();
         item = (VisualItem)tuple;
         item.setVisible( false );
         if ( visibleItems.contains( new Integer( item.getInt("nid") ) ) )
         {
           visibleItems.remove( new Integer( item.getInt("nid") ) );
         }
      } 
      while ( iEdges.hasNext() )
      {
         tuple = (Tuple)iEdges.next();
         item = (VisualItem)tuple;
         item.setVisible( false );
      } 
      while ( iAggregates.hasNext() )
      {
         tuple = (Tuple)iAggregates.next();
         item = (VisualItem)tuple;
         item.setVisible( false );
      } 
    }

    // ------------------------------------------------------------------ //
    //                                                                    //
    // selectNodes( String sqlClause, boolean isSelected, boolean reset ) //
    // select nodes corresponding to a query                              //
    //                                                                    //
    // ------------------------------------------------------------------ //

    public static int selectNodes(String sqlClause, boolean isSelected, boolean reset ) {

      int nf;
      int no;

         // clear graph if reset requested
         if ( ( reset ) && ( m_graph != null ) )
         {
            m_graph.clear();
            visibleItems.clear();
         }
         no=m_graph.getNodeCount();

         if ( ( sqlClause.equals("") ) && ( isSelected == false ) )
         {
            return 0;
         }

         try 
         {
         Statement stmt = m_sqlCon.createStatement ();
         ResultSet rs = stmt.executeQuery ( "SELECT * FROM nodes " + sqlClause + ";" );

         while ( rs.next() )
         {
            if ( !visibleItems.contains( new Integer( rs.getInt("nid") ) ) ) 
            {
              Node newNode = m_graph.addNode();
              for ( nf=0; nf<m_nodeFields.length; nf++ )
              {
                 if ( m_nodeFieldsClass[nf] == String.class )
                 {
                    String dbString = rs.getString(nf+1);
                    newNode.setString( m_nodeFields[nf],  dbString );
                 } 
                 else if ( m_nodeFieldsClass[nf] == int.class )
                 {
                    newNode.setInt( m_nodeFields[nf],  rs.getInt(nf+1) );
                 }
                 else
                 {
                    System.out.println( "Unsupported class : field : " + m_nodeFields[nf] );
                 }
              }
              int nid = newNode.getInt( "nid" );
              visibleItems.add( new Integer( nid ) );
              nodeIds.put( new Integer(nid), new Integer(no) ); 
              no++;
              // System.out.println( "adding node : " + nid );
            }
          }

          rs.close();

        }
        catch (Exception ex) {

           System.out.println ("\n*** Exception caught ***\n");

           while (ex != null) {
              if ( ex instanceof SQLException )
              {
                  System.out.println ("SQLState: " + ((SQLException)ex).getSQLState ());
                  System.out.println ("Message:  " + ((SQLException)ex).getMessage ());
                  System.out.println ("Vendor:   " + ((SQLException)ex).getErrorCode ());
                  ex = ((SQLException)ex).getNextException ();
                  System.out.println ("");
              }
           }
       }

       return no;
    }

    // --------------------------------------------------- //
    //                                                     //
    // updateEdges() : update edges                        //
    //                                                     //
    // --------------------------------------------------- //

    public static void updateEdges(Visualization viz) {

      int nf;

         // create fake links to handle color codes
         if ( visibleItems.size() > 0 )
         {
           Iterator iNLTypes =  lTypeSet.iterator();
           while ( iNLTypes.hasNext() )
           {
              String ltype = (String) iNLTypes.next();
              int idEdge = m_graph.addEdge( 0, 0 );
              Edge newEdge = m_graph.getEdge( idEdge );
              newEdge.setString( "type", ltype );
           }
         }        

         try 
         {
         Statement stmt = m_sqlCon.createStatement ();
         ResultSet rs = stmt.executeQuery ( "SELECT * FROM edges;" );

         while ( rs.next() )
         {
            int source = rs.getInt( 1 );
            int target = rs.getInt( 2 );

            if ( visibleItems.contains( new Integer( source ) ) && visibleItems.contains( new Integer( target ) ) )
            {
              // System.out.println( "adding edge : source : " + source + " target : " + target );
              int idEdge = m_graph.addEdge( ((Integer)nodeIds.get( new Integer( source ) )).intValue(), ((Integer)nodeIds.get( new Integer( target ) )).intValue() );
              Edge newEdge = m_graph.getEdge( idEdge );

              for ( nf=0; nf<m_edgeFields.length; nf++ )
              {
                 if ( m_edgeFieldsClass[nf] == String.class )
                 {
                    newEdge.setString( m_edgeFields[nf],  rs.getString(nf+1) );
                 } 
                 else if ( m_edgeFieldsClass[nf] == int.class )
                 {
                    newEdge.setInt( m_edgeFields[nf],  ((Integer)nodeIds.get( new Integer( rs.getInt(nf+1) ) ) ).intValue() );
                 }
                 else
                 {
                    System.out.println( "Unsupported class : field : " + m_edgeFields[nf] );
                 }
              }
            }
          }

          rs.close();

        }
        catch (SQLException ex) {

           System.out.println ("\n*** SQLException caught ***\n");

           while (ex != null) {
                  System.out.println ("SQLState: " + ex.getSQLState ());
                  System.out.println ("Message:  " + ex.getMessage ());
                  System.out.println ("Vendor:   " + ex.getErrorCode ());
                  ex = ex.getNextException ();
                  System.out.println ("");
           }
       }
    }

    // --------------------------------------------------- //
    //                                                     //
    // updateAggregates() : update aggregates              //
    //                                                     //
    // --------------------------------------------------- //

    public static void updateAggregates(Visualization viz) {
      Iterator iNThemes =  themeSet.iterator();
      TupleSet oNodes  = viz.getGroup(treeNodes);
      String theme;
      int it=0;
      
      m_at.clear();
      m_agc.reset();

      while ( iNThemes.hasNext() )
      {
        theme = (String) iNThemes.next();
        AggregateItem aitem = (AggregateItem)m_at.addItem();
        aitem.setInt("id", it);
        ExpressionParser.disable_tracing();
        Predicate selectP = (Predicate)ExpressionParser.parse( "(theme=='" + theme + "')" );
        Iterator ipNodes = oNodes.tuples(selectP);

        while ( ipNodes.hasNext() )
        {
           aitem.addItem((VisualItem)ipNodes.next());
        }
        it++;
      }
    }

    // ---------------------------------------------------------- //
    //                                                            //
    // expandOneLevel() : expand one level around seleted nodes   //
    //                                                            //
    // ---------------------------------------------------------- //

    public static void expandOneLevel(Visualization viz, TupleSet theseItems) {
      Tuple tuple;
      VisualItem item;
      Iterator iSNodes = theseItems.tuples();
      int nId, nAdded=0;

      while ( iSNodes.hasNext() )
      {
         tuple = (Tuple)iSNodes.next();
         nId = tuple.getInt("nid");

         try 
         {
         Statement stmt = m_sqlCon.createStatement ();
         ResultSet rs = stmt.executeQuery ( "SELECT * FROM edges WHERE ( source=" + nId + ") OR ( target = " + nId + ");" );

         while ( rs.next() )
         {
            int source = rs.getInt( 1 );
            int target = rs.getInt( 2 );

            if ( ( source != nId ) && !visibleItems.contains( new Integer( source ) ) )
            {
              // System.out.println( "adding node : " + source );
              nAdded += selectNodes( "WHERE nid=" + source, true, false );
            }
            if ( ( target != nId ) && !visibleItems.contains( new Integer( target ) ) )
            {
              // System.out.println( "adding node : " + target );
              nAdded += selectNodes( "WHERE nid=" + target, true, false );
            }
          }

          rs.close();

        }
        catch (SQLException ex) {

           System.out.println ("\n*** SQLException caught ***\n");

           while (ex != null) {
                  System.out.println ("SQLState: " + ex.getSQLState ());
                   System.out.println ("Message:  " + ex.getMessage ());
                   System.out.println ("Vendor:   " + ex.getErrorCode ());
                   ex = ex.getNextException ();
                   System.out.println ("");
           }
        }
      }

      if ( nAdded == 0 ) return;
      
      // update edges
      updateEdges( viz );

      // update aggregates
      updateAggregates( viz );

      viz.run( "filter" );
    }

    // ---------------------------------------------------------- //
    //                                                            //
    // filterItems( String sqlClause, boolean isSelected ) :       //
    // hide/show items with this criteria                         //
    //                                                            //
    // ---------------------------------------------------------- //

    public void filterItems( String sqlClause, boolean isSelected ) 
    {

      selectNodes( sqlClause, isSelected, true );

      // update edges
      updateEdges( m_vis );

      // update aggregates
      updateAggregates( m_vis );

      m_vis.run( "filter" );
    }

    // ---------------------------------------------------------- //
    //                                                            //
    // filterEdges(Predicate condition, boolean isSelected)       // 
    // hide/show edges with this criteria                         //
    //                                                            //
    // ---------------------------------------------------------- //

    public void filterEdges( Predicate condition, boolean isSelected ) 
    {
      TupleSet oEdges  = m_vis.getGroup(treeEdges);
      TupleSet oNodes  = m_vis.getGroup(treeNodes);
      Iterator ifEdges;
      VisualItem item;
      if ( condition == null )
      {
        ifEdges = oEdges.tuples();
      }
      else
      {
        ifEdges = oEdges.tuples(condition);
      }
      while ( ifEdges.hasNext() )
      {
         VisualItem vitem = (VisualItem)ifEdges.next();
         vitem.setVisible( isSelected );
      }
      m_vis.run( "filter" );
    }
    
    // ---------------------------------------------------------- //
    //                                                            //
    // setFilterBoxes(boolean) : set Filter selection boxes       //
    //                                                            //
    // ---------------------------------------------------------- //

    public static void setFilterBoxes( boolean isSelected ) 
    {
       m_resetButtons = true;
       Iterator checkBoxes =  llCbSet.iterator();
       while ( checkBoxes.hasNext() )
       {
          JCheckBox jcb = (JCheckBox)checkBoxes.next();
          jcb.setSelected( isSelected );
       }
       m_resetButtons = false;
    }

    // ---------------------------------------------------------- //
    //                                                            //
    // setNodeBoxes(boolean) : set Node selection boxes           //
    //                                                            //
    // ---------------------------------------------------------- //

    public static void setNodeBoxes( boolean isSelected ) 
    {
       m_resetButtons = true;
       Iterator checkBoxes =  lCbSet.iterator();
       while ( checkBoxes.hasNext() )
       {
         JCheckBox jcb = (JCheckBox)checkBoxes.next();
         jcb.setSelected( isSelected );
       }
       // remove all filter conditions
       int nt, nc;
       for ( nt=0; nt<nTypeSet.size(); nt++ )
       {
         filterTypes[nt]=isSelected;
       }
       for ( nt=0; nt<themeSet.size(); nt++ )
       {
         filterThemes[nt]=isSelected;
       }
       for ( nc=0; nc<countrySet.size(); nc++ )
       {
         filterCountries[nc]=isSelected;
       }
       for ( nt=0; nt<groupSet.size(); nt++ )
       {
         filterGroups[nt]=isSelected;
       }
       for ( nt=0; nt<esfSet.size(); nt++ )
       {
         filterEsfs[nt]=isSelected;
       }
       m_resetButtons = false;
    }

    // --------------------------------------------------- //
    //                                                     //
    // itemStateChanged() : handle filter actions          //
    //                                                     //
    // --------------------------------------------------- //

    public void itemStateChanged(ItemEvent e) 
    {
        JCheckBox cb = (JCheckBox)e.getItemSelectable();
        String itemPressed = (String)cb.getText();
        boolean isSelected = cb.isSelected();

        if ( m_resetButtons ) return;

        m_search.setQuery("");
        m_searchResults.setText("            ");

        // check for a filter on edges
        if ( itemPressed.equals( "All Links" ) )
        {
           setFilterBoxes( isSelected );
           filterEdges( null, isSelected );
           return;
        }
        else
        {
           // scan link types
           Iterator iNLTypes =  lTypeSet.iterator();
           while ( iNLTypes.hasNext() )
           {
              String ltype = (String) iNLTypes.next();
              if ( itemPressed.equals( ltype ) )
              {
                filterEdges( (Predicate)ExpressionParser.parse( "(type=='" + ltype + "')" ), isSelected );
                return;
              }
           }
        }

        // check for a filter on nodes
        if ( itemPressed.equals( "All Nodes" ) )
        {
           setNodeBoxes( isSelected );
           setFilterBoxes( isSelected );
           filterItems( "", isSelected );
        }
        else
        {
           // scan types
           Iterator iNTypes =  nTypeSet.iterator();
           int nt=0, nc;
           filterTString = "";
           while ( iNTypes.hasNext() )
           {
              String type = (String) iNTypes.next();
              if ( itemPressed.equals( type ) )
              {
                 filterTypes[nt] = isSelected;
              }
              if ( filterTypes[nt] )
              {
                type = type.replaceAll( "\'", "\\\\\'" );
                if ( filterTString.equals("") )
                {
                   filterTString = "( (type='"+type+"') "; 
                }
                else
                {
                   filterTString += "OR (type='"+type+"') "; 
                }
              }
              nt++;
           }
           if ( !filterTString.equals("") )
           {
             filterTString += " )";
           }

           // scan themes
           Iterator iNThemes =  themeSet.iterator();
           nt=0;
           filterDString = "";
           while ( iNThemes.hasNext() )
           {
              String theme = (String) iNThemes.next();
              if ( itemPressed.equals( theme ) )
              {
                 filterThemes[nt] = isSelected; 
              }
              if ( filterThemes[nt] )
              {
                theme = theme.replaceAll( "\'", "\\\\\'" );
                if ( filterDString.equals("") )
                {
                   filterDString = "( (theme='"+theme+"') "; 
                }
                else
                {
                   filterDString += "OR (theme='"+theme+"') "; 
                }
              }
              nt++;
           }
           if ( !filterDString.equals("") )
           {
             filterDString += " )";
           }

           // scan countries
           Iterator iNCountries =  countrySet.iterator();
           filterCString = "";
           nc=0;
           while ( iNCountries.hasNext() )
           {
              String country = (String) iNCountries.next();
              if ( itemPressed.equals( country ) )
              {
                 filterCountries[nc] = isSelected; 
              }
              if ( filterCountries[nc] )
              {
                country = country.replaceAll( "\'", "\\\\\'" );
                if ( filterCString.equals("") )
                {
                   filterCString = "( (country='"+country+"') "; 
                }
                else
                {
                   filterCString += "OR (country='"+country+"') "; 
                }
              }
              nc++;
           }
           if ( !filterCString.equals("") )
           {
             filterCString += " )";
           }

           // scan groups
           Iterator iNGroups =  groupSet.iterator();
           filterGString = "";
           nc=0;
           while ( iNGroups.hasNext() )
           {
              String group = (String) iNGroups.next();
              if ( itemPressed.equals( group ) )
              {
                 filterGroups[nc] = isSelected; 
              }
              if ( filterGroups[nc] )
              {
                group = group.replaceAll( "\'", "\\\\\'" );
                if ( filterGString.equals("") )
                {
                   filterGString = "( (groups='"+group+"') "; 
                }
                else
                {
                   filterGString += "OR (groups='"+group+"') "; 
                }
              }
              nc++;
           }
           if ( !filterGString.equals("") )
           {
             filterGString += " )";
           }

           // scan esfs
           Iterator iNEsfs =  esfSet.iterator();
           filterEString = "";
           nc=0;
           while ( iNEsfs.hasNext() )
           {
              String esf = (String) iNEsfs.next();
              if ( itemPressed.equals( esf ) )
              {
                 filterEsfs[nc] = isSelected; 
              }
              if ( filterEsfs[nc] )
              {
                if ( filterEString.equals("") )
                {
                   filterEString = "( (esf1='"+esf+"') OR (esf2='"+esf+"') OR (esf3='"+esf+"') "; 
                }
                else
                {
                   filterEString += "OR (esf1='"+esf+"') OR (esf2='"+esf+"') OR (esf3='"+esf+"') "; 
                }
              }
              nc++;
           }
           if ( !filterEString.equals("") )
           {
             filterEString += " )";
           }

           filterString = "";
           if ( !filterTString.equals("") )
           {
              filterString = "WHERE " + filterTString;
           }
           if ( !filterDString.equals("") )
           {
             if ( filterString.equals("") )
             {
                filterString = "WHERE " + filterDString;
             }
             else 
             {
                filterString += " AND " + filterDString;
             }
           }
           if ( !filterCString.equals("") )
           {
             if ( filterString.equals("") )
             {
                filterString = "WHERE " + filterCString;
             }
             else 
             {
                filterString += " AND " + filterCString;
             }
           }
           if ( !filterGString.equals("") )
           {
             if ( filterString.equals("") )
             {
                filterString = "WHERE " + filterGString;
             }
             else 
             {
                filterString += " AND " + filterGString;
             }
           }
           if ( !filterEString.equals("") )
           {
             if ( filterString.equals("") )
             {
                filterString = "WHERE " + filterEString;
             }
             else 
             {
                filterString += " AND " + filterEString;
             }
           }
           if ( !filterString.equals("") )
           {
             System.out.println( "filter : " + filterString );
             filterItems( filterString, true );
           }
           else
           {
             filterItems( "", false );
           }
        }
    }

    public etcGroupsQL(Graph g, String label, String host) 
    {
        super(new Visualization());
        m_label = label;
        m_host = host;
        Tuple tuple;
        String theme, image, type, country, group, esf, ltype;
        int ni, na, nt, nc, nagg=0, netype=0, ntype=0, ng, ne;

        // init global static structures
        init();

        // disable prefuse messages
        ExpressionParser.disable_tracing();

        // -- set up visualization --
        VisualGraph vg = m_vis.addGraph(tree, g);
        m_vis.setInteractive(treeEdges, null, true);
        
        m_at = m_vis.addAggregates(treeAggregates);
        m_at.addColumn(VisualItem.POLYGON, float[].class);
        m_at.addColumn("id", int.class);

        try 
        {
          Statement stmt = m_sqlCon.createStatement ();
          ResultSet rs = stmt.executeQuery ( "SELECT DISTINCT theme FROM nodes ORDER BY theme;" );

          while ( rs.next() )
          {
            theme = rs.getString(1);
            if  ( ( theme != null ) && ( !theme.equals( "na" ) ) )
            {
               if ( ++nagg >= nbMaxThemes )
               {
                  System.out.println( "ERROR : too many themes defined, ignored... (" + theme + ")" );
               }
               else
               {
                  themeSet.add(theme);
               }
            }
          }

          rs.close();

          rs = stmt.executeQuery ( "SELECT DISTINCT type, image FROM nodes ORDER BY type;" );

          while ( rs.next() )
          {
            type = rs.getString(1);
            image = rs.getString(2);
            if ( ( type != null ) && ( !type.equals( "na" ) ) && ( image != null ) && ( !image.equals( "na" ) ) )
            {
               ntype++;
               imageSet.add( image );
               nTypeSet.add( type );
            }
          }

          rs.close();

          rs = stmt.executeQuery ( "SELECT DISTINCT country FROM nodes ORDER BY country;" );

          while ( rs.next() )
          {
            country = rs.getString(1);
            if ( ( country != null ) && ( !country.equals( "na" ) ) )
            {
               countrySet.add( country );
            }
          }

          rs.close();

          rs = stmt.executeQuery ( "SELECT DISTINCT groups FROM nodes ORDER BY groups;" );

          while ( rs.next() )
          {
            group = rs.getString(1);
            if ( ( group != null ) && ( !group.equals( "na" ) ) )
            {
               groupSet.add( group );
            }
          }

          rs.close();

          rs = stmt.executeQuery ( "SELECT DISTINCT esf1 FROM nodes ORDER BY esf1;" );

          while ( rs.next() )
          {
            esf = rs.getString(1);
            if ( ( esf != null ) && ( !esf.equals( "na" ) ) )
            {
               esfSet.add( esf );
            }
          }

          rs.close();

          rs = stmt.executeQuery ( "SELECT DISTINCT esf2 FROM nodes ORDER BY esf2;" );

          while ( rs.next() )
          {
            esf = rs.getString(1);
            if ( ( esf != null ) && ( !esf.equals( "na" ) ) )
            {
               esfSet.add( esf );
            }
          }

          rs.close();

          rs = stmt.executeQuery ( "SELECT DISTINCT esf3 FROM nodes ORDER BY esf3;" );

          while ( rs.next() )
          {
            esf = rs.getString(1);
            if ( ( esf != null ) && ( !esf.equals( "na" ) ) )
            {
               esfSet.add( esf );
            }
          }

          rs = stmt.executeQuery ( "SELECT DISTINCT type FROM edges ORDER BY type;" );

          while ( rs.next() )
          {
            ltype = rs.getString(1);
            if ( ( ltype != null ) && ( !ltype.equals( "na" ) ) )
            {
               netype++;
               if ( netype > nbMaxEdgeTypes )
               {
                  System.out.println( "ERROR : too many edge types defined, ignored... (" + ltype + ")" );
               }
               else
               {
                  lTypeSet.add( ltype );
               }
            }
          }

          rs.close();

        }
        catch (SQLException ex) {

            System.out.println ("\n*** SQLException caught ***\n");

            while (ex != null) {
                    System.out.println ("SQLState: " + ex.getSQLState ());
                System.out.println ("Message:  " + ex.getMessage ());
                System.out.println ("Vendor:   " + ex.getErrorCode ());
                ex = ex.getNextException ();
                System.out.println ("");
            }
        }

        filterTypes = new boolean[nTypeSet.size()];
        for ( nt=0; nt<nTypeSet.size(); nt++ )
        {
           filterTypes[nt]=initialState;
        }
        filterThemes = new boolean[themeSet.size()];
        for ( nt=0; nt<themeSet.size(); nt++ )
        {
           filterThemes[nt]=initialState;
        }
        filterCountries = new boolean[countrySet.size()];
        for ( nc=0; nc<countrySet.size(); nc++ )
        {
           filterCountries[nc]=initialState;
        }
        filterGroups = new boolean[groupSet.size()];
        for ( ng=0; ng<groupSet.size(); ng++ )
        {
           filterGroups[ng]=initialState;
        }
        filterEsfs = new boolean[esfSet.size()];
        for ( ne=0; ne<esfSet.size(); ne++ )
        {
           filterEsfs[ne]=initialState;
        }

        // only set the number of useful colors
        rthemeColors = new int[themeSet.size()];
        for ( na=0; na<themeSet.size(); na++ )
        {
           rthemeColors[na] = themeColors[na];
        }

        // only set the number of useful colors
        redgeColors = new int[lTypeSet.size()];
        for ( nt=0; nt<lTypeSet.size(); nt++ )
        {
           redgeColors[nt] = edgeColors[nt];
        }

        m_agc = new AggregateDragControl(nbMaxThemes);
        m_noc = new NodeDragControl(m_vis);

        // -- set up renderers --
        m_nodeRenderer = new LabelRenderer(m_label, "image" );
        m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
        m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
        m_nodeRenderer.setRoundedCorner(16,16);
        m_nodeRenderer.setImageField( "image" );
        m_nodeRenderer.setMaxImageDimensions( 25, 20 );

        m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_LINE, Constants.EDGE_ARROW_FORWARD);
        m_edgeRenderer.setArrowHeadSize( 10, 5 );
        m_edgeRenderer.setArrowType( Constants.EDGE_ARROW_FORWARD );
        m_edgeRenderer.setDefaultLineWidth( 3 );

        m_aggregateRenderer = new PolygonRenderer(Constants.POLY_TYPE_STACK);
        // m_aggregateRenderer.setCurveSlack(0.15f);
        
        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        rf.add(new InGroupPredicate(treeAggregates), m_aggregateRenderer);
        m_vis.setRendererFactory(rf);
               
        // -- set up processing actions --
        
        // colors
        ItemAction nodeColor = new NodeColorAction(treeNodes);
        ItemAction textColor = new TextColorAction(treeNodes);
        m_vis.putAction("textColor", textColor);
        
        DataColorAction edgeTypeColor = new DataColorAction(treeEdges, "type", Constants.ORDINAL, VisualItem.STROKECOLOR, redgeColors);
        
        FontAction fonts = new FontAction(treeNodes, FontLib.getFont(font, 10));
        fonts.add("ingroup('_focus_')", FontLib.getFont(font, 11));
        
        // recolor
        ActionList recolor = new ActionList();
        recolor.add(nodeColor);
        recolor.add(textColor);
        m_vis.putAction("recolor", recolor);
        
        // repaint
        ActionList repaint = new ActionList();
        repaint.add(recolor);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);
        
        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);
        
        ColorAction aStroke = new ColorAction(treeAggregates, VisualItem.STROKECOLOR);
        aStroke.setDefaultColor(ColorLib.gray(200));
        aStroke.add("_hover", ColorLib.rgb(255,100,100));
        m_vis.putAction("aStroke", aStroke);

        ColorAction aFill = new DataColorAction(treeAggregates, "id", Constants.ORDINAL, VisualItem.FILLCOLOR, rthemeColors);
        m_vis.putAction("aFill", aFill);

        // create the tree layout action
        // m_thisLayout = new FruchtermanReingoldLayout(tree);
        // m_thisLayout = new RadialTreeLayout(tree);
        m_thisLayout = new RandomLayout(tree);
        m_vis.putAction("thisLayout", m_thisLayout);
        
        m_subLayout = new CollapsedSubtreeLayout(tree);
        m_vis.putAction("subLayout", m_subLayout);
        
        m_aggrLayout = new AggregateLayout(treeAggregates, m_agc);
        m_vis.putAction("aggrLayout", m_aggrLayout);
        
        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(m_thisLayout);
        filter.add(m_subLayout);
        filter.add(new TreeRootAction(tree));
        filter.add(fonts);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeTypeColor);
        m_vis.putAction("filter", filter);
        
        // create the aggregate drawing
        ActionList layouts = new ActionList();
        layouts.add(m_thisLayout);
        layouts.add(m_subLayout);
        filter.add(fonts);
        filter.add(textColor);
        filter.add(nodeColor);
        filter.add(edgeTypeColor);
        m_vis.putAction("letout", layouts);
        
        // animated transition
        ActionList animate = new ActionList(1250);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new PolarLocationAnimator(treeNodes, linear));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new RepaintAction());
        animate.add(m_aggrLayout);
        animate.add(aFill);
        animate.add(aStroke);
        m_vis.putAction("animate", animate);
        m_vis.alwaysRunAfter("filter", "animate");
        
        // ------------------------------------------------
        
        // initialize the display
        setSize(950,450);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new PanControl());
        // addControlListener(new FocusControl(1, "filter"));
        addControlListener(new HoverActionControl("repaint"));
        addControlListener(m_noc);
        addControlListener(m_agc);
        
        // ------------------------------------------------
        
        // filter graph and perform layout
        m_vis.run("filter");
        
        // maintain a set of items that should be interpolated linearly
        // this isn't absolutely necessary, but makes the animations nicer
        // the PolarLocationAnimator should read this set and act accordingly
        m_vis.addFocusGroup(linear, new DefaultTupleSet());
        m_vis.getGroup(Visualization.FOCUS_ITEMS).addTupleSetListener(
             new TupleSetListener() {
                 public void tupleSetChanged(TupleSet t, Tuple[] add, Tuple[] rem) {
                     TupleSet linearInterp = m_vis.getGroup(linear);
                     if ( add.length < 1 ) return; linearInterp.clear();
                     if ( add[0] instanceof Node )
                     { 
                       for ( Node n = (Node)add[0]; n!=null; n=n.getParent() )
                       {
                         linearInterp.addTuple(n);
                       }
                     }
                }
            }
        );
        
    }
    
    // ------------------------------------------------------------------------
    
    public static void main(String argv[]) {
        String label = "name";
        
        if ( argv.length > 1 ) {
            label = argv[0];
        }
        
        UILib.setPlatformLookAndFeel();
        
        JFrame frame = new JFrame("networks as we know them");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(demo(label, m_host, m_dbName, m_dbUser, m_dbPass, false));
        frame.pack();
        frame.setVisible(true);
    }
    
    public static JPanel demo( final String label, 
                              final String host,   
                              final String dbName,
                              final String dbUser,
                              final String dbPass,
                              boolean initialState) {

      int nf;

        try {
            if ( m_graph != null )
            {
               m_graph.clear();
               m_gview.getVisualization().reset();
            }

            m_nodeTable = new Table();
            for ( nf=0; nf<m_nodeFields.length; nf++ )
            {
               m_nodeTable.addColumn( m_nodeFields[nf], m_nodeFieldsClass[nf] );
            }
            
            m_edgeTable = new Table();
            for ( nf=0; nf<m_edgeFields.length; nf++ )
            {
               m_edgeTable.addColumn( m_edgeFields[nf], m_edgeFieldsClass[nf] );
            }

            m_graph = new Graph( m_nodeTable, m_edgeTable, false );
  
            m_host = host;
            m_dbName = dbName;
            m_dbUser = dbUser;
            m_dbPass = dbPass;

            String mysqlurl = "jdbc:mysql://" + m_host + "/" + m_dbName + "?useUnicode=true&characterEncoding=UTF-8";

            try
            {
               Class.forName ("com.mysql.jdbc.Driver").newInstance ();
               m_sqlCon = DriverManager.getConnection (mysqlurl, m_dbUser, m_dbPass);
               System.out.println ("Database connection established");
            }
            catch (Exception e)
            {
               e.printStackTrace();
               System.err.println ("Cannot connect to database server : " + mysqlurl );
               if (m_sqlCon != null)
               {
                   try
                   {
                       m_sqlCon.close ();
                       System.out.println ("Database connection terminated");
                   }
                   catch (Exception ce) { 
                       ce.printStackTrace();
                       System.out.println ("Database close problem");
                   }
                   m_sqlCon = null;
                }
            }

        } catch ( Exception e ) {
                e.printStackTrace();
        }
        return demo(m_graph, label, host, initialState);
    }
    
    public static void updateDescription(VisualItem item) {

        if ( item.isInGroup( treeNodes ) )
        {
          StyledDocument doc = description.getStyledDocument();
          description.setText("");
          try 
          {
            if ( item.canGetString("name") )
            {
               String name = item.getString("name");
 
               if ( ( name != null ) && ( !name.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Name : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), name + "\n", doc.getStyle("regular") );
               }
            }
            if ( item.canGetString("acronym") )
            {
               String acronym = item.getString("acronym");
 
               if ( ( acronym != null ) && ( !acronym.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Acronym : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), acronym + "\n", doc.getStyle("regular") );
               }
            }
            if ( item.canGetString("location") )
            {
               String location = item.getString("location");
 
               if ( ( location != null ) && ( !location.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Location : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), location + "\n", doc.getStyle("regular") );
               }
            }
            if ( item.canGetString("country") )
            {
               String country = item.getString("country");
 
               if ( ( country != null ) && ( !country.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Country : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), country + "\n", doc.getStyle("regular") );
               }
            }
            if ( item.canGetString("website") )
            {
               String website = item.getString("website");
 
               if ( ( website != null ) && ( !website.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Web Site : " ), doc.getStyle("bold") );
  
                 if ( website.indexOf( "http://" ) != 0 )
                 {
                     website = "http://" + website;
                 }
                 if ( website.charAt( website.length()-1 ) == '/' )
                 {
                    website = website.substring(0,website.length()-1);
                 }
                 try {
                   website = URLDecoder.decode( website, "UTF-8" );
                 } catch ( Exception uee ) {
                    System.out.println("Could not decode URL : " + website );
                 }
  
                 doc.insertString(doc.getLength(), website + "\n", doc.getStyle("regular") );
               }
               m_descriptionMouseListener.setUrl( website );
               try {
                   m_descriptionMouseListener.getContext().showDocument( new URL( "http://" + m_host + "/etc/getthumbnail.pl?url=" + website ), "fthumbnail" );
               } catch ( Exception mfue ) {
                  System.out.println("Wrong url : " + website );
                  mfue.printStackTrace();
               }
            }
            if ( item.canGetString("contact") )
            {
               String contact = item.getString("contact");
 
               if ( ( contact != null ) && ( !contact.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Contact : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), contact + "\n", doc.getStyle("regular") );
               }
            }
            if ( item.canGetString("theme") )
            {
               String theme = item.getString("theme");
 
               if ( ( theme != null ) && ( !theme.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Theme : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), theme + "\n", doc.getStyle("regular") );
               }
            }
            if ( item.canGetString("group") )
            {
               String group = item.getString("group");
 
               if ( ( group != null ) && ( !group.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Group : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), group + "\n", doc.getStyle("regular") );
               }
            }
            if ( item.canGetString("description") )
            {
               String description = item.getString("description");
 
               if ( ( description != null ) && ( !description.equals( "na" ) ) )
               {
                 doc.insertString(doc.getLength(), new String( "Description : " ), doc.getStyle("bold") );
                 doc.insertString(doc.getLength(), description + "\n", doc.getStyle("regular") );
               }
            }
            // descriptionScrollPane.getVerticalScrollBar().setValue(0);
            description.setCaretPosition(0);
               
          } catch (BadLocationException ble) {
            System.err.println("Couldn't insert text into text pane.");
          }

          // activate the description zone
          legendTabbedPane.setVisible(false);
          descriptionScrollPane.setVisible(true);
          descriptionScrollPane.requestFocusInWindow();
          gPanel.repaint();
       }
    }

    // clear description zone
    public static void clearDescription( VisualItem item ) {
        if ( item.isInGroup( treeNodes ) )
        {
           StyledDocument doc = description.getStyledDocument();
           description.setText("");

           // activate the description zone
           legendTabbedPane.setVisible(true);
           descriptionScrollPane.setVisible(false);
           gPanel.repaint();
       }
    }

    // returns an ImageIcon, or null if the path was invalid.
    protected static ImageIcon createImageIcon(String path) 
    {
        java.net.URL imgURL = etcGroupsQL.class.getResource(path);
        if (imgURL != null) 
        {
            return new ImageIcon(imgURL);
        } 
        else 
        {
            System.err.println("ERROR : couldn't find icon image : " + path);
            return null;
        }
    }

    public static JPanel demo(Graph g, final String label, final String host, boolean initialState) {
        // create a new radial tree view
        m_gview = new etcGroupsQL(g, label, host);
        Visualization vis = m_gview.getVisualization();
        int nLineSize = 25;
        m_gview.setBorder(BorderFactory.createEtchedBorder( new Color( ColorLib.rgb(98,58,254) ), new Color( ColorLib.rgb(134,234,12) ) ) );
        m_gview.setSize( 600, 400 );
        m_gview.setHighQuality(true);
        initialState = initialState;
        
        // set mouse actions
        m_gview.addControlListener(new ControlAdapter() {
            public void itemEntered(VisualItem item, MouseEvent e) {
                // update description
                updateDescription( item ); 
            }
            public void itemExited(VisualItem item, MouseEvent e) {
                // clear description
                clearDescription( item ); 
            }
            public void itemClicked(VisualItem item, MouseEvent e) {
                if ( e.getClickCount() == 2 )
                {
                    String website = item.getString("website");
                    if ( website.indexOf( "http://" ) != 0 )
                    {
                        website = "http://" + website;
                    }
                    if ( website.charAt( website.length()-1 ) == '/' )
                    {
                       website = website.substring(0,website.length()-1);
                    }
                    try {
                      website = URLDecoder.decode( website, "UTF-8" );
                    } catch ( Exception uee ) {
                       System.out.println("Could not decode URL : " + website );
                    }

                    if ( ( website.compareTo("") != 0 ) && ( website.compareTo("http://na") != 0 ) )
                    {
                       try 
                       {
                         m_descriptionMouseListener.getContext().showDocument( new URL(website), new String("_blank") );
                       } catch ( Exception mfue ) {
                         System.out.println("Wrong url : " + website );
                         mfue.printStackTrace();
                       }
                    }
                }
                // expand one level around that node
                DefaultTupleSet items = new DefaultTupleSet();
                items.addTuple( (Tuple) item );
                expandOneLevel( item.getVisualization(), (TupleSet) items );

                item.getVisualization().run("filter");
            }
        });
        
        // the panel used to show the description of each node
        descriptionScrollPane = new JScrollPane(description);
        descriptionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        descriptionScrollPane.setPreferredSize(new Dimension(200, 400));
        descriptionScrollPane.setMinimumSize(new Dimension(200, 400));
        StyledDocument doc = description.getStyledDocument();
        addStylesToDocument(doc);
        description.setFont(FontLib.getFont(font, Font.PLAIN, 12));
        description.setEditable(false);
        description.setBorder(BorderFactory.createEtchedBorder( new Color( ColorLib.rgb(23,78,90) ), new Color( ColorLib.rgb(200,24,134) ) ) );
        
        // the panel used to show the legend and used as a filter
        legendTabbedPane = new JTabbedPane();
        legendTabbedPane.setPreferredSize(new Dimension(330, 400));
        legendTabbedPane.setMinimumSize(new Dimension(330, 400));

        // node types panel
        if ( nTypeSet.size() > 0 )
        {
          JPanel typePanel = new JPanel();
          if ( nTypeSet.size() <= 14 )
          {
            typePanel.setLayout(new GridLayout(14, 1));
          }
          else
          {
            typePanel.setLayout(new BoxLayout(typePanel, BoxLayout.Y_AXIS));
          }
          typePanel.setAlignmentY(TOP_ALIGNMENT);
          typePanel.setPreferredSize(new Dimension(1000, (nTypeSet.size()+1)*nLineSize));
          typePanel.setMinimumSize(new Dimension(1000, (nTypeSet.size()+1)*nLineSize));
          Iterator iNTypes =  nTypeSet.iterator();
          Iterator iITypes =  imageSet.iterator();
          while ( iNTypes.hasNext() )
          {
             String type = (String) iNTypes.next();
             String image = (String) iITypes.next();
             JCheckBox checkType = new JCheckBox(type);
             checkType.setSelected(initialState);
             checkType.addItemListener( m_gview );
             ImageIcon typeIcon = createImageIcon( image );
             JLabel labelType = new JLabel( typeIcon );
             Box typeBox = new Box(BoxLayout.X_AXIS);
             typeBox.add( checkType );
             lCbSet.add(checkType);
             typeBox.add(Box.createHorizontalStrut(3));
             typeBox.add( labelType );
             typeBox.setAlignmentX(LEFT_ALIGNMENT); 
             typePanel.add( typeBox );
          }
          JCheckBox checkType = new JCheckBox("All Nodes");
          checkType.setSelected(initialState);
          checkType.addItemListener( m_gview );
          Box typeBox = new Box(BoxLayout.X_AXIS);
          typeBox.add( checkType );
          lCbSet.add(checkType);
          typeBox.add(Box.createHorizontalStrut(3));
          typeBox.setAlignmentX(LEFT_ALIGNMENT); 
          typePanel.add( typeBox );
    
          JScrollPane sTypePanel = new JScrollPane( typePanel );
          sTypePanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          ImageIcon iconNodes = createImageIcon("icons/network-group.png");
          legendTabbedPane.addTab("Nodes Type", iconNodes, sTypePanel, "Nodes Type");
        }

        // themes panel
        if ( themeSet.size() > 0 )
        {
          JPanel themePanel = new JPanel();
          if ( themeSet.size() <= 14 )
          {
            themePanel.setLayout(new GridLayout(14, 1));
          }
          else
          {
            themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.Y_AXIS));
          }
          themePanel.setAlignmentY(TOP_ALIGNMENT); 
          themePanel.setPreferredSize(new Dimension(1000, (themeSet.size()+1)*nLineSize));
          themePanel.setMinimumSize(new Dimension(1000, (themeSet.size()+1)*nLineSize));
          Iterator iNThemes =  themeSet.iterator();
          int it=0;
          while ( iNThemes.hasNext() )
          {
             String theme = (String) iNThemes.next();
             String image = "icons/theme-"+it+".jpg";
             JCheckBox checkTheme = new JCheckBox(theme);
             checkTheme.setSelected(initialState);
             checkTheme.addItemListener( m_gview );
             ImageIcon themeIcon = createImageIcon( image );
             JLabel labelTheme = new JLabel( themeIcon );
             Box themeBox = new Box(BoxLayout.X_AXIS);
             themeBox.add( checkTheme );
             lCbSet.add(checkTheme);
             themeBox.add(Box.createHorizontalStrut(3));
             themeBox.add( labelTheme );
             themeBox.setAlignmentX(LEFT_ALIGNMENT); 
             themePanel.add( themeBox );
             it++;
          }
          JCheckBox checkTheme = new JCheckBox("All Nodes");
          checkTheme.setSelected(initialState);
          checkTheme.addItemListener( m_gview );
          Box themeBox = new Box(BoxLayout.X_AXIS);
          themeBox.add( checkTheme );
          lCbSet.add(checkTheme);
          themeBox.add(Box.createHorizontalStrut(3));
          themeBox.setAlignmentX(LEFT_ALIGNMENT); 
          themePanel.add( themeBox );
    
          JScrollPane sThemePanel = new JScrollPane( themePanel );
          sThemePanel.setAlignmentX(LEFT_ALIGNMENT); 
          ImageIcon iconThemes = createImageIcon("icons/theme-0.jpg");
          legendTabbedPane.addTab("Theme", iconThemes, sThemePanel, "Theme of Activity");
        }

        // countries panel
        if ( countrySet.size() > 0 )
        {
          JPanel countryPanel = new JPanel();
          if ( countrySet.size() <= 14 )
          {
            countryPanel.setLayout(new GridLayout(14, 1));
          }
          else
          {
            countryPanel.setLayout(new BoxLayout(countryPanel, BoxLayout.Y_AXIS));
          }
          countryPanel.setAlignmentY(TOP_ALIGNMENT);
          countryPanel.setPreferredSize(new Dimension(1000, (countrySet.size()+1)*nLineSize));
          countryPanel.setMinimumSize(new Dimension(1000, (countrySet.size()+1)*nLineSize));
          Iterator iNCountries =  countrySet.iterator();
          while ( iNCountries.hasNext() )
          {
             String country = (String) iNCountries.next();
             String image = "icons/"+country+".png";
             image = image.replaceAll( " ", "-" );
             JCheckBox checkCountry = new JCheckBox(country);
             checkCountry.setSelected(initialState);
             checkCountry.addItemListener( m_gview );
             ImageIcon countryIcon = createImageIcon( image );
             JLabel labelCountry = new JLabel( countryIcon );
             Box countryBox = new Box(BoxLayout.X_AXIS);
             countryBox.add( checkCountry );
             lCbSet.add( checkCountry );
             countryBox.add(Box.createHorizontalStrut(3));
             countryBox.add( labelCountry );
             countryBox.setAlignmentX(LEFT_ALIGNMENT); 
             countryPanel.add( countryBox );
          }
          JCheckBox checkCountry = new JCheckBox("All Nodes");
          checkCountry.setSelected(initialState);
          checkCountry.addItemListener( m_gview );
          Box countryBox = new Box(BoxLayout.X_AXIS);
          countryBox.add( checkCountry );
          lCbSet.add( checkCountry );
          countryBox.add(Box.createHorizontalStrut(3));
          countryBox.setAlignmentX(LEFT_ALIGNMENT); 
          countryPanel.add( countryBox );
    
          JScrollPane sCountryPanel = new JScrollPane( countryPanel );
          sCountryPanel.setAlignmentX(LEFT_ALIGNMENT); 
          ImageIcon iconCountries = createImageIcon("icons/England.png");
          legendTabbedPane.addTab("Country", iconCountries, sCountryPanel, "Country");
        }

        // esfs panel
        if ( esfSet.size() > 0 )
        {
          JPanel esfPanel = new JPanel();
          if ( esfSet.size() <= 14 )
          {
            esfPanel.setLayout(new GridLayout(14, 1));
          }
          else
          {
            esfPanel.setLayout(new BoxLayout(esfPanel, BoxLayout.Y_AXIS));
          }
          esfPanel.setAlignmentY(TOP_ALIGNMENT);
          esfPanel.setPreferredSize(new Dimension(1000, (esfSet.size()+1)*nLineSize));
          esfPanel.setMinimumSize(new Dimension(1000, (esfSet.size()+1)*nLineSize));
          Iterator iNEsfs =  esfSet.iterator();
          while ( iNEsfs.hasNext() )
          {
             String esf = (String) iNEsfs.next();
             JCheckBox checkEsf = new JCheckBox(esf);
             checkEsf.setSelected(initialState);
             checkEsf.addItemListener( m_gview );
             Box esfBox = new Box(BoxLayout.X_AXIS);
             esfBox.add( checkEsf );
             lCbSet.add( checkEsf );
             esfBox.add(Box.createHorizontalStrut(3));
             esfBox.setAlignmentX(LEFT_ALIGNMENT); 
             esfPanel.add( esfBox );
          }
          JCheckBox checkEsf = new JCheckBox("All Nodes");
          checkEsf.setSelected(initialState);
          checkEsf.addItemListener( m_gview );
          Box esfBox = new Box(BoxLayout.X_AXIS);
          esfBox.add( checkEsf );
          lCbSet.add( checkEsf );
          esfBox.add(Box.createHorizontalStrut(3));
          esfBox.setAlignmentX(LEFT_ALIGNMENT); 
          esfPanel.add( esfBox );
    
          JScrollPane sEsfPanel = new JScrollPane( esfPanel );
          sEsfPanel.setAlignmentX(LEFT_ALIGNMENT); 
          legendTabbedPane.addTab("Esf", null, sEsfPanel, "Esf");
        }

        // groups panel
        if ( groupSet.size() > 0 )
        {
          JPanel groupPanel = new JPanel();
          if ( groupSet.size() <= 14 )
          {
            groupPanel.setLayout(new GridLayout(14, 1));
          }
          else
          {
            groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.Y_AXIS));
          }
          groupPanel.setAlignmentY(TOP_ALIGNMENT); 
          groupPanel.setPreferredSize(new Dimension(1000, groupSet.size()*nLineSize));
          groupPanel.setMinimumSize(new Dimension(1000, groupSet.size()*nLineSize));
          Iterator iNGroups =  groupSet.iterator();
          while ( iNGroups.hasNext() )
          {
             String group = (String) iNGroups.next();
             JCheckBox checkGroup = new JCheckBox(group);
             checkGroup.setSelected(initialState);
             checkGroup.addItemListener( m_gview );
             Box groupBox = new Box(BoxLayout.X_AXIS);
             groupBox.add( checkGroup );
             lCbSet.add(checkGroup);
             groupBox.setAlignmentX(LEFT_ALIGNMENT); 
             groupBox.add(Box.createHorizontalStrut(3));
             groupPanel.add( groupBox );
          }
    
          JScrollPane sGroupPanel = new JScrollPane( groupPanel );
          sGroupPanel.setAlignmentX(LEFT_ALIGNMENT); 
          legendTabbedPane.addTab("Group", null, sGroupPanel, "Declared Groups");
        }

        // links panel
        if ( lTypeSet.size() > 0 )
        {
          JPanel linkPanel = new JPanel();
          if ( lTypeSet.size() <= 14 )
          {
            linkPanel.setLayout(new GridLayout(14, 1));
          }
          else
          {
            linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));
          }
          linkPanel.setAlignmentY(TOP_ALIGNMENT);
          linkPanel.setPreferredSize(new Dimension(1000, (lTypeSet.size()+1)*nLineSize));
          linkPanel.setMinimumSize(new Dimension(1000, (lTypeSet.size()+1)*nLineSize));
          Iterator iNLTypes =  lTypeSet.iterator();
          int il=0;
          while ( iNLTypes.hasNext() )
          {
             String ltype = (String) iNLTypes.next();
             String image = "icons/link-"+il+".gif";
             JCheckBox checkLType = new JCheckBox(ltype);
             checkLType.setSelected(initialState);
             checkLType.addItemListener( m_gview );
             ImageIcon lTypeIcon = createImageIcon( image );
             JLabel labelLtype = new JLabel( lTypeIcon );
             Box lTypeBox = new Box(BoxLayout.X_AXIS);
             lTypeBox.add( checkLType );
             llCbSet.add( checkLType );
             lTypeBox.add(Box.createHorizontalStrut(3));
             lTypeBox.add( labelLtype );
             lTypeBox.setAlignmentX(LEFT_ALIGNMENT); 
             linkPanel.add( lTypeBox );
             il++;
          }
          JCheckBox checkLType = new JCheckBox("All Links");
          checkLType.setSelected(initialState);
          checkLType.addItemListener( m_gview );
          Box lTypeBox = new Box(BoxLayout.X_AXIS);
          lTypeBox.add( checkLType );
          llCbSet.add( checkLType );
          lTypeBox.add(Box.createHorizontalStrut(3));
          lTypeBox.setAlignmentX(LEFT_ALIGNMENT); 
          linkPanel.add( lTypeBox );
    
          JScrollPane sLinkPanel = new JScrollPane( linkPanel );
          sLinkPanel.setAlignmentX(LEFT_ALIGNMENT); 
          sLinkPanel.setAlignmentY(TOP_ALIGNMENT);
          ImageIcon iconLTypes = createImageIcon("icons/link-0.gif");
          legendTabbedPane.addTab("Link Type", iconLTypes, sLinkPanel, "Link Type");
        }

        // create a global search panel on the selected fields
        JLabel searchOnLabel = new JLabel("Search on :");
        JLabel searchTypeLabel = new JLabel("Search type :");
        m_searchResults = new JLabel("               ");

        String [] searchedFields = new String[1]; // for compatibility with JSearchPanel
        searchedFields[0] = "name";
        m_search = new JGSearchPanel(  (TupleSet)vis.getGroup(treeNodes),
                                       (SearchTupleSet)null,
                                       searchedFields,
                                       true,
                                       false, // monitorKeyStrokes set to false
                                       vis );
        m_search.setShowResultCount(true);
        m_search.setBorder(BorderFactory.createEmptyBorder(5,5,4,0));
        m_search.setFont(FontLib.getFont(font, Font.PLAIN, 11));
        m_search.setLabelText( ">> search >" );

        // create zone for selecting search fields
        Box fbox = new Box(BoxLayout.X_AXIS);
        fbox.setAlignmentX(LEFT_ALIGNMENT); 
        fbox.add(searchOnLabel);
        m_nbscb=0;
        m_selectedFields[m_nbscb] = new String( "name" );
        m_selectCbs[m_nbscb]  = new JCheckBox( m_selectedFields[m_nbscb] );
        m_selectCbs[m_nbscb].setSelected(true);
        fbox.add(m_selectCbs[m_nbscb]);
        m_nbscb++;
        m_selectedFields[m_nbscb] = new String( "acronym" );
        m_selectCbs[m_nbscb]  = new JCheckBox( m_selectedFields[m_nbscb] );
        m_selectCbs[m_nbscb].setSelected(true);
        fbox.add(m_selectCbs[m_nbscb]);
        m_nbscb++;
        m_selectedFields[m_nbscb] = new String( "website" );
        m_selectCbs[m_nbscb]  = new JCheckBox( m_selectedFields[m_nbscb] );
        m_selectCbs[m_nbscb].setSelected(false);
        fbox.add(m_selectCbs[m_nbscb]);
        m_nbscb++;
        m_selectedFields[m_nbscb] = new String( "contact" );
        m_selectCbs[m_nbscb]  = new JCheckBox( m_selectedFields[m_nbscb] );
        m_selectCbs[m_nbscb].setSelected(false);
        fbox.add(m_selectCbs[m_nbscb]);
        m_nbscb++;
        m_selectedFields[m_nbscb] = new String( "description" );
        m_selectCbs[m_nbscb]  = new JCheckBox( m_selectedFields[m_nbscb] );
        m_selectCbs[m_nbscb].setSelected(false);
        fbox.add(m_selectCbs[m_nbscb]);
        m_nbscb++;

        // create the search type radio button
        JRadioButton globalButton = new JRadioButton("global");
        globalButton.setActionCommand("global");
        globalButton.setSelected(true);

        JRadioButton exactButton = new JRadioButton("exact");
        exactButton.setActionCommand("exact");
        exactButton.setSelected(false);

        JRadioButton prefixButton = new JRadioButton("prefix");
        prefixButton.setActionCommand("prefix");
        prefixButton.setSelected(false);

        m_STypeBG = new ButtonGroup();
        m_STypeBG.add(globalButton);
        m_STypeBG.add(exactButton);
        m_STypeBG.add(prefixButton);

        Box tbox = new Box(BoxLayout.X_AXIS);
        tbox.setAlignmentX(LEFT_ALIGNMENT); 
        tbox.add(searchTypeLabel);
        tbox.add(globalButton);
        tbox.add(exactButton);
        tbox.add(prefixButton);

        Box sbox = new Box(BoxLayout.Y_AXIS);
        sbox.setAlignmentX(LEFT_ALIGNMENT); 
        sbox.add(tbox);
        sbox.add(fbox);

        Box rsbox = new Box(BoxLayout.Y_AXIS);
        rsbox.setAlignmentX(LEFT_ALIGNMENT); 
        rsbox.add(m_search);
        rsbox.add(m_searchResults);

        Box box = new Box(BoxLayout.X_AXIS);
        box.add(Box.createHorizontalStrut(10));
        box.add(sbox);
        box.add(Box.createHorizontalStrut(10));
        box.add(Box.createHorizontalGlue());
        box.add(rsbox);
        box.add(Box.createHorizontalStrut(3));
        
        gPanel = new JPanel(new BorderLayout(20,0));
        gPanel.add(box, BorderLayout.NORTH);
        gPanel.add(m_gview, BorderLayout.WEST);
        gPanel.add(descriptionScrollPane, BorderLayout.CENTER);
        gPanel.add(legendTabbedPane, BorderLayout.EAST);
        legendTabbedPane.setVisible(true);
        descriptionScrollPane.setVisible(false);

        description.addMouseListener( m_descriptionMouseListener );
        
        Color BACKGROUND = Color.WHITE;
        Color FOREGROUND = Color.DARK_GRAY;
        UILib.setColor(gPanel, BACKGROUND, FOREGROUND);

        return gPanel;
    }
    
    // ------------------------------------------------------------------------
    //
    // Switch the root of the tree by requesting a new spanning tree
    // at the desired root
    //
    public static class TreeRootAction extends GroupAction {
        public TreeRootAction(String graphGroup) {
            super(graphGroup);
        }
        public void run(double frac) {

          DefaultTupleSet items = new DefaultTupleSet();

            TupleSet focus = m_vis.getGroup(Visualization.FOCUS_ITEMS);
            if ( focus==null || focus.getTupleCount() == 0 ) return;
            
            Graph g = (Graph)m_vis.getGroup(m_group);
            Node f = null;
            while ( !g.containsTuple(f=(Node)focus.tuples().next()) ) {
                f = null;
            }
            if ( f == null ) return;

            g.getSpanningTree(f);
        }
    }
    
    //
    // Set node fill colors
    //
    public static class NodeColorAction extends ColorAction {
        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR, ColorLib.rgba(255,255,255,0));
            add("_hover", ColorLib.rgb(0,255,0));
            add("ingroup('_search_')", ColorLib.rgb(255,190,190));
            add("ingroup('_focus_')", ColorLib.rgb(255,0,0));
        }
                
    } // end of inner class NodeColorAction
    
    //
    // Set node text colors
    //
    public static class TextColorAction extends ColorAction {
        public TextColorAction(String group) {
            super(group, VisualItem.TEXTCOLOR, ColorLib.gray(0));
            add("_hover", ColorLib.rgb(255,234,123));
        }
    } // end of inner class TextColorAction
    
    // --------------------------------------------------- //
    //                                                     //
    // JGSearchPanel : another JSearchPanel                //
    //                                                     //
    // --------------------------------------------------- //

    public static class JGSearchPanel extends JSearchPanel {
        private String[] m_fields;
        private String m_searchString;
        private Visualization m_vis;
        private Predicate m_condition;
        private String pString;

        public JGSearchPanel(TupleSet source, SearchTupleSet search, String[] fields, 
                             boolean autoIndex, boolean monitorKeystrokes, Visualization viz ) {
            super(source, search, fields, autoIndex, monitorKeystrokes );
            m_fields = fields;
            m_vis = viz;
        }

        // specific search algorithm ( depending on selected boxes )
        public void actionPerformed(java.awt.event.ActionEvent ae) {
          int ks;
          int nbSelected = 0;
          VisualItem vitem = null;
          String selectedMode;

            m_searchString = ae.getActionCommand();
            m_searchString = m_searchString.replaceAll( "\'", "\\\\\'" );
            selectedMode = m_STypeBG.getSelection().getActionCommand();
            hideAll( m_vis );
            if ( m_searchString.equals("") ) 
            {
               m_searchResults.setText("         " );
               m_vis.run( "filter" );
               return;
            }
            TupleSet oNodes  = m_vis.getGroup(treeNodes);
            pString="";
            for ( ks=0; ks<m_nbscb; ks++ )
            {
              // System.out.println( m_selectedFields[ks] + " : " + m_selectCbs[ks].isSelected() );
              // System.out.println( "Selected mode : " + selectedMode );
              if ( m_selectCbs[ks].isSelected() )
              {
                if ( selectedMode.equals("global") )
                {
                  if ( pString.equals("") )
                  {
                    pString += "WHERE ( ( " + m_selectedFields[ks] +  " LIKE '%" + m_searchString + "%' ) ";
                  }
                  else
                  {
                    pString += "OR ( " + m_selectedFields[ks] +  " LIKE '%" + m_searchString + "%' ) ";
                  }
                }
                if ( selectedMode.equals("exact") )
                {
                  if ( pString.equals("") )
                  {
                    pString += "WHERE ( ( " + m_selectedFields[ks] +  " = '" + m_searchString + "' ) ";
                  }
                  else
                  {
                    pString += "OR ( " + m_selectedFields[ks] +  " = '" + m_searchString + "' ) ";
                  }
                }
                if ( selectedMode.equals("prefix") )
                {
                  if ( pString.equals("") )
                  {
                    pString += "WHERE ( ( " + m_selectedFields[ks] +  " LIKE '" + m_searchString + "%' ) ";
                  }
                  else
                  {
                    pString += "OR ( " + m_selectedFields[ks] +  " LIKE '" + m_searchString + "%' ) ";
                  }
                }
              }
            }
            if ( !pString.equals("") )
            {
               pString += " )";
            }
            // System.out.println( pString );
            nbSelected = selectNodes( pString, true, true );

            // update edges
            updateEdges( m_vis );

            // update aggregates
            updateAggregates( m_vis );

            m_vis.run( "filter" );

            m_searchResults.setForeground( new Color(255,0,0) );

            m_searchResults.setText( ">> " + nbSelected + " match" + (nbSelected>1?"es":"") );
            if ( nbSelected == 0 )
            {
                legendTabbedPane.setVisible(true);
                descriptionScrollPane.setVisible(false);
            }
            else
            {
                setNodeBoxes( false );
                setFilterBoxes( false );
            }
 
            // update description
            if ( nbSelected == 1 )
            {
               updateDescription( vitem );
               Graph g = (Graph)m_vis.getGroup(tree);
               g.getSpanningTree( (Node)vitem );
            }

        }
 
        protected void searchUpdate() // nada please
        {
        }

        public void removeUpdate(javax.swing.event.DocumentEvent e) // nada please
        {
        }

        public void insertUpdate(javax.swing.event.DocumentEvent e)  // nada please
        {
        }

        public void changedUpdate(javax.swing.event.DocumentEvent e) // nada please
        {
        }

    } // end of inner class JGSearchPanel
    
    
} // end of class etcGroupsQL
