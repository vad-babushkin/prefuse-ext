package prefuse.demos.yamvia;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.Vector;


public class Ivipi 
{    

    public static JComponent choicePanel = null; 
    public static Controler controler = null;
    public static FocusControlTreeChoice focusControler = null;
    public static IvipiSql iSql = null;
    public static JPanel mainPanel;
    public static DataPlot dataPlot;
    private static IvipiData iData = new IvipiData ();

    
    public static void main (String argv[]) 
    {
        JFrame frame = null;
 
        // Connection with the database
	    	try{
	    			iSql = new IvipiSql();
		}
    	catch (Exception ex)
		{
			System.out.println("Exception: \n\n"+ex.getMessage());
		}

        // Initialisation of the object that will detect the click on the TreeView
        focusControler = new FocusControlTreeChoice ();
        
        // Initialisation of panel of choices
        choicePanel = ChoicePanel.init ();
        
        // Initialisation du Controler
        controler = new Controler ();
        
        String query = iData.getQueryMovies(new Vector ());
        
        // 
        dataPlot = new DataPlot (query);
        
        // Main Panel
        mainPanel = new JPanel (new BorderLayout());
        mainPanel.add(choicePanel, BorderLayout.NORTH);
        mainPanel.add(dataPlot, BorderLayout.CENTER);
        
        // Main Frame of the application
        frame = new JFrame("Ivipi");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

}
