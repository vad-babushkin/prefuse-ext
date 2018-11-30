package edu.berkeley.guir.prefuse.util.display;

import edu.berkeley.guir.prefuse.Display;
import edu.berkeley.guir.prefuse.util.io.IOLib;
import edu.berkeley.guir.prefuse.util.io.SimpleFileFilter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashSet;

public class ExportDisplayAction
		extends AbstractAction {
	private Display display;
	private JFileChooser chooser;
	private ScaleSelector scaler;

	public ExportDisplayAction(Display paramDisplay) {
		this.display = paramDisplay;
		this.scaler = new ScaleSelector();
		this.chooser = new JFileChooser();
		this.chooser.setDialogType(1);
		this.chooser.setDialogTitle("Export Prefuse Display...");
		this.chooser.setAcceptAllFileFilterUsed(false);
		HashSet localHashSet = new HashSet();
		String[] arrayOfString = ImageIO.getWriterFormatNames();
		for (int i = 0; i < arrayOfString.length; i++) {
			String str = arrayOfString[i].toLowerCase();
			if ((str.length() == 3) && (!localHashSet.contains(str))) {
				localHashSet.add(str);
				this.chooser.setFileFilter(new SimpleFileFilter(str, str.toUpperCase() + " Image (*." + str + ")"));
			}
		}
		localHashSet.clear();
		localHashSet = null;
		this.chooser.setAccessory(this.scaler);
	}

	public void actionPerformed(ActionEvent paramActionEvent) {
		File localFile = null;
		this.scaler.setImage(this.display.getOffscreenBuffer());
		int i = this.chooser.showSaveDialog(this.display);
		if (i == 0) {
			localFile = this.chooser.getSelectedFile();
		} else {
			return;
		}
		String str1 = ((SimpleFileFilter) this.chooser.getFileFilter()).getExtension();
		String str2 = IOLib.getExtension(localFile);
		if (!str1.equals(str2)) {
			localFile = new File(localFile.toString() + "." + str1);
		}
		double d = this.scaler.getScale();
		boolean bool = false;
		try {
			BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
			System.out.print("Saving image " + localFile.getName() + ", " + str1 + " format...");
			bool = this.display.saveImage(localBufferedOutputStream, str1, d);
			localBufferedOutputStream.flush();
			localBufferedOutputStream.close();
			System.out.println("\tDONE");
		} catch (Exception localException) {
			bool = false;
		}
		if (!bool) {
			JOptionPane.showMessageDialog(this.display, "Error Saving Image!", "Image Save Error", 0);
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/util/display/ExportDisplayAction.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */