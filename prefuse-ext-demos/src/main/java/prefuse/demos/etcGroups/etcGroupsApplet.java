package prefuse.demos.etcGroups;

import prefuse.util.ui.JPrefuseApplet;


public class etcGroupsApplet extends JPrefuseApplet {

    public void init() {

        String filename = getParameter("file"); 
        String field = getParameter("field"); 
        String host = getParameter("host"); 
        String dbName = getParameter("dbName"); 
        String dbUser = getParameter("dbUser"); 
        String dbPass = getParameter("dbPass"); 
        boolean initialState; 
        if ( getParameter("initialState").equals("true") )
        {
           initialState = true;
        }
        else
        {
           initialState = false;
        }

        etcGroupsML.getMouseListener().setContext(this.getAppletContext());
        etcGroupsQL.getMouseListener().setContext(this.getAppletContext());

        if ( dbName != null )
        {
           System.out.println( "Showing db: " + dbName + " linked on " + field + " (SQL mode)\n");
           this.setContentPane(etcGroupsQL.demo(field, host, dbName, dbUser, dbPass, initialState));
        }
        else
        {
           System.out.println( "Showing tree: " + filename + " linked on " + field + " (GraphML mode) \n");
           this.setContentPane(etcGroupsML.demo(filename, field, host, initialState));
        }
    }
    
} // end of class etcGroupsApplet
