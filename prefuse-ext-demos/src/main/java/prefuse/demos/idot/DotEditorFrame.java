package prefuse.demos.idot;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Class showing an editor window where the user can
 * edit the graph file in DOT format.
 * 
 * The window has buttons for updating the visualized
 * graph to match the edited content, for saving the modified file,
 * and for closing the window.
 */
public class DotEditorFrame extends JFrame {
	/**
	 * A JTextArea where the dot file is shown and can be edited
	 */
	private JTextArea editorArea;
	
	/**
	 * Holds a reference to the display whose runLayout() method is called
d	 * whenever the "Apply changes" button is clicked
	 */
	private DotDisplay updatedDisplay;
	
	/**
	 * Creates a new editor window holding both an editable view
	 * of the dot file contents, and a buttons to apply the changes,
	 * save the changes to a file, and close the window.
	 * 
	 * @param idot  the iDot applicatiuon associated with this editor window 
	 * @param disp  the display to be updated when the apply button is clicked
	 */
         private static final int FRAME_WIDTH  = 65;
         private static final int FRAME_HEIGHT = 25;
         
         public DotEditorFrame(final iDot idot, DotDisplay disp) {
		super(idot.getFileName() + " - Dot Editor");
		
		updatedDisplay = disp;
		
		editorArea = new JTextArea(disp.getDotFileContents(), 
                  FRAME_HEIGHT, FRAME_WIDTH);
		editorArea.setFont(new java.awt.Font(Config.TEXTAREA_FONT_NAME, 
		Config.TEXTAREA_FONT_STYLE, Config.TEXTAREA_FONT_SIZE));
	
		add(new JScrollPane(editorArea), BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		
		JButton applyButton = new JButton("Apply changes");
		applyButton.setMnemonic(KeyEvent.VK_A);
//		applyButton.setToolTipText("apply the changes to the prologue");
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// validation? none
				if(updatedDisplay != null) {
					updatedDisplay.setDotFileContents(editorArea.getText());
					updatedDisplay.storeVisibleState();
					updatedDisplay.runDOTLayout();
					updatedDisplay.restoreVisibleState();
					updatedDisplay.runFilterUpdate();
				}
			}			
		});
		buttons.add(applyButton);
		
		JButton saveButton = new JButton("Save...");
		saveButton.setMnemonic(KeyEvent.VK_S);
//		saveButton.setToolTipText("save the file");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				idot.save(editorArea.getText());
			}			
		});
		buttons.add(saveButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
//		cancelButton.setToolTipText("cancel editing");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}			
		});
		buttons.add(cancelButton);

		add(buttons, BorderLayout.SOUTH);
		pack();
	}

	/**
	 * Reloads the graph data to be edited from the {@link DotDisplay}
	 * component passed in the constructor, and updates the window title
	 * using the file name from the parameter of this method.
	 * 
	 * @param filename the name of the file to be used on the title bar
	 */
	public void reload(String filename) {
		setTitle(filename + " - Dot Editor");
		editorArea.setText(updatedDisplay.getDotFileContents());
	}
}
