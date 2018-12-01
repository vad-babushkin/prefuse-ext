package prefuse.demos.etcGroups;

/**
 * DescriptionMouseListener :
 * process mouse events in the description zone.
 */

import java.io.*;
import java.util.*;
import java.net.*;
import java.applet.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class DescriptionMouseListener implements MouseListener {

  protected AppletContext m_apctxt;
  protected String m_url = new String();

    public void setContext(AppletContext apctxt) {
       m_apctxt = apctxt;
    }

    public AppletContext getContext() {
       return m_apctxt;
    }

    public void setUrl(String url) {
       if ( url.indexOf( "http://" ) != 0 )
       {
          m_url = "http://" + url; 
       }
       else
       {
          m_url = url;
       }
    }

    public void mousePressed(MouseEvent e) {
       // System.out.println("Mouse pressed; # of clicks: " + e.getClickCount());
       if ( ( m_url.compareTo("") != 0 ) && ( m_url.compareTo("http://na") != 0 ) && ( m_url.compareTo("na") != 0 ) )
       {
          try 
          {
            m_apctxt.showDocument( new URL(m_url), new String("_blank") );
          } catch ( Exception mfue ) {
            System.out.println("Wrong url : " + m_url );
          }
       }
    }

    public void mouseReleased(MouseEvent e) {
       // System.out.println("Mouse released; # of clicks: " + e.getClickCount());
    }

    public void mouseEntered(MouseEvent e) {
       // System.out.println("Mouse entered");
    }

    public void mouseExited(MouseEvent e) {
       // System.out.println("Mouse exited");
    }

    public void mouseClicked(MouseEvent e) {
       // System.out.println("Mouse clicked (# of clicks: " + e.getClickCount() + ")");
    }
}
