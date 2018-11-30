package prefuse;

import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * 
 * @version 1.0
 * @author <a href="http://jheer.org">Jeffrey Heer</a> prefuse-apps(AT)jheer.org</a>
 */
public class PrefuseAppLauncher extends JFrame {

    public static void main(String[] args) {
        setLookAndFeel();
        AppDescription[] desc = getAppDescriptions();
        JFrame f = new PrefuseAppLauncher(desc);
        f.pack();
        f.setVisible(true);
    }
    
    public static AppDescription[] getAppDescriptions() {
        AppDescription[] desc = {
                new AppDescription("Radial Graph Explorer", "radial.png", "prefuse.radialexplorer.RadialGraphExplorer", "/friendster.xml"),
                new AppDescription("Graph Editor", "editor.png", "prefuse.grapheditor.GraphEditor", null),
                new AppDescription("Data Mountain", "datamountain.png", "prefuse.datamountain.DataMountain", "/data.xml")
        };
        return desc;
    }
    
    public static final void setLookAndFeel() {
        try {
            String laf = UIManager.getSystemLookAndFeelClassName();             
            UIManager.setLookAndFeel(laf);  
        } catch ( Exception e ) {}
    } //
    
    //-------------------------------------------------------------------------
    
    private AppDescription[] desc;
    private Action launcher = new LaunchAction();
    
    public PrefuseAppLauncher(AppDescription[] desc) {
        super("prefuse application launcher");
        this.desc = desc;
        initFrame();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    } //
    
    public void initFrame() {
        Container c = this.getContentPane();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        
        int numcols = 2;
        for ( int i=0; i <desc.length; i+=numcols ) {
            c.add(Box.createVerticalStrut(5));
            c.add(Box.createVerticalGlue());
            Box b = new Box(BoxLayout.X_AXIS);
            
            for ( int j=0; j<numcols && i+j<desc.length; j++ ) {
                b.add(Box.createHorizontalStrut(5));
                b.add(Box.createHorizontalGlue());
                JButton but = new JButton("An Application");
                but.setFont(new Font("Verdana",Font.BOLD,18));
                but.putClientProperty("app", desc[i+j]);
                but.setAction(launcher);
                but.setText(desc[i+j].name);
                if ( desc[i+j].image != null ) {
                    URL url = getClass().getResource(desc[i+j].image);
                    but.setIcon(new ImageIcon(url));
                }
                but.setVerticalTextPosition(SwingUtilities.BOTTOM);
                but.setHorizontalTextPosition(SwingUtilities.CENTER);
                b.add(but);
            }
            b.add(Box.createHorizontalStrut(5));
            b.add(Box.createHorizontalGlue());
            c.add(b);
        }
        c.add(Box.createVerticalStrut(5));
        c.add(Box.createVerticalGlue());
        this.validate();
    }
    
    public static class AppDescription {
        public static final int FRAME = 0;
        public static final int APP = 1;
        public int type;
        public String name;
        public String arg;
        public String image;
        public String classname;
        public AppDescription(int type, String name, String image, String classname, String arg) {
            this.type = type;
            this.name = name;
            this.image = image;
            this.classname = classname;
            this.arg = arg;
        }
        public AppDescription(String name, String image, String classname, String arg) {
            this(FRAME,name,image,classname, arg);
        }
        public void launch() {
            if ( type == FRAME ) {
                try {
                    Object o;
                    if ( arg == null ) { 
                        o = Class.forName(classname).newInstance();
                    } else {
                        Constructor c = Class.forName(classname)
                        	.getConstructor(new Class[] {String.class});
                        o = c.newInstance(new Object[] {arg});
                    }
                    if ( o instanceof JFrame ) {
                        JFrame f = (JFrame)o;
                        f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    }
                } catch ( Exception ex ) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Sorry, could not load application!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                try {
                    Runtime rt = Runtime.getRuntime();
                    rt.exec(classname);
                } catch ( Exception ex ) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Sorry, could not load application!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    public class LaunchAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JComponent jc = (JComponent)e.getSource();
            AppDescription d = (AppDescription)jc.getClientProperty("app");
            d.launch();            
        }
    }
    
} // end of class PrefuseAppLauncher
