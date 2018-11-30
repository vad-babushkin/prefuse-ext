package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.Tree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract interface TreeReader {
	public abstract Tree loadTree(String paramString)
			throws FileNotFoundException, IOException;

	public abstract Tree loadTree(URL paramURL)
			throws IOException;

	public abstract Tree loadTree(File paramFile)
			throws FileNotFoundException, IOException;

	public abstract Tree loadTree(InputStream paramInputStream)
			throws IOException;
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/TreeReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */