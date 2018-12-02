package prefuse.demos.idot;

import java.awt.Font;

/**
 * Contains some configuration options, like the path to the dot executable.
 * The two things that are most likely to need editing in this file are the
 * path to dot or the arguments for it, and the initial value of the prologue
 * used in dot files.
 *
 * @see #DOT_COMMAND
 * @see #INITIAL_DOT_PROLOGUE
 */
public class Config {
	/**
	 * Declared private to prevent instantiation - all methods and fields are static.
	 */
	private Config() {
	}

	/**
	 * if true, print various debug information to the standard output
	 */
	public static final boolean print = false;

	/**
	 * the title of the main window
	 */
	public static final String TITLE = "iDot";

	/**
	 * version of the program
	 */
	public static final String VERSION = "Version 1.0";

	/**
	 * The command and parameters used to add layout coordinates
	 * to input in dot format. More parameters can be added to the array
	 * if needed.
	 * Used in class {@link DotLayout}.
	 */
	public static final String[] DOT_COMMAND = {
//          "dot", 
			"/usr" + java.io.File.separatorChar + "bin" + java.io.File.separatorChar + "dot",
			"-Tdot"};

	/**
	 * The name of the font used in JTextArea components
	 * (currently only user is the DotEditorFrame).
	 */
	public static final String TEXTAREA_FONT_NAME = "Lucida Sans Typewriter";

	/**
	 * The style of the font used in JTextArea components.
	 */
	public static final int TEXTAREA_FONT_STYLE = Font.PLAIN;

	/**
	 * The default size of the font used in JTextArea components.
	 */
	public static final int TEXTAREA_FONT_SIZE = 14;

	/**
	 * The name of the font used in the node labels.
	 */
	public static final String NODE_FONT_NAME = "SansSerif";

	/**
	 * The style of the font used in the node labels.
	 */
	public static final int NODE_FONT_STYLE = Font.PLAIN;

	/**
	 * The default size of the font used in the node labels.
	 */
	public static final int NODE_FONT_DEFAULT_SIZE = 12;

	/**
	 * The dots-per-inch resolution of the coordinate system.
	 * Dot assumes 72 DPI when determining the node and edge
	 * coordinates, so use that.
	 * <p>
	 * Using the return value of
	 * Toolkit.getDefaultToolkit().getScreenResolution();
	 * would sound intuitive, but leads to too big nodes when compared
	 * to node locations and font sizes.
	 */
	public static final double DPI = 72;
}
