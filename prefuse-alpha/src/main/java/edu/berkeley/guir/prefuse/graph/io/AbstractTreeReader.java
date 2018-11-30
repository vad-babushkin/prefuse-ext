package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.Tree;

import java.io.*;
import java.net.URL;

public abstract class AbstractTreeReader
		implements TreeReader {
	public Tree loadTree(String paramString)
			throws FileNotFoundException, IOException {
		return loadTree(new FileInputStream(paramString));
	}

	public Tree loadTree(URL paramURL)
			throws IOException {
		return loadTree(paramURL.openStream());
	}

	public Tree loadTree(File paramFile)
			throws FileNotFoundException, IOException {
		return loadTree(new FileInputStream(paramFile));
	}

	public abstract Tree loadTree(InputStream paramInputStream)
			throws IOException;
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/AbstractTreeReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */