///**
// *
// */
//package prefuse.demos.vocal;
//
//import java.awt.BorderLayout;
//
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//
///**
// * @author fnaufel
// *
// */
//public class VocalUI {
//
//	ConceptTreeView conceptTreeView;
//	protected JFrame frame;
//	protected JPanel mainPanel;
//
//	public VocalUI( ConceptTreeView tv ) {
//
//		conceptTreeView = tv;
//
//		mainPanel = new JPanel( new BorderLayout() );
//		mainPanel.setBackground( VocalConfig.mainPanelBackgroundColor );
//        mainPanel.setForeground( VocalConfig.mainPanelForegroundColor );
//        mainPanel.add( tv, BorderLayout.CENTER );
//
//		frame = new JFrame( "VoCAL Concept Tree" );
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setContentPane( mainPanel );
//        frame.pack();
//        frame.setVisible( true );
//
//	}
//
//}
