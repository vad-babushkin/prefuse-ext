/**
 * 
 */
package prefuse.demos.idot.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A simple filter for filtering files in a file selection dialog based on their
 * extensions.
 */
public class ExtensionFileFilter extends FileFilter {
	
	/** desciption for this filter */
	private String desc;
	
	/** 
	 * the extensions for the files that are to be accepted
	 */
	private String[] extensions = null;
	
	/**
	 * Constructs a file filter which accepts files with the
	 * given extension.
	 * @param ext   the extension, including the dot (e.g. ".xml")
	 * @param desc  description for the accepted files
	 */
	public ExtensionFileFilter(String ext, String desc) {
		this(new String[] { ext }, desc);
	}

	/**
	 * Constructs a file filter which accepts files with the
	 * given extensions.
	 * @param exts   the extensions, including the dot (e.g. {".xml", ".dot"})
	 * @param desc  description for the accepted files
	 */
	public ExtensionFileFilter(String[] exts, String desc) {
		this.desc = desc;

		extensions = new String[exts.length];
		System.arraycopy(exts, 0, extensions, 0, exts.length);
	}

	/*
	 * Returns the description for this filter
	 * 
	 *  (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	public String getDescription() {
		return desc;
	}

	/*
	 * Returns true if the file has an allowed extension or if it is a directory
	 * 
	 *  (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		if(f.isDirectory())
			return true;
		
		if(extensions != null) {
			for(String ext : extensions)
				if(f.getName().endsWith(ext))
					return true;
		}
		
		return false;
	}
}
