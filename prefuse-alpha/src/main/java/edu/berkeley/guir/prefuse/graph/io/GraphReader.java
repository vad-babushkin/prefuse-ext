package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract interface GraphReader {
	public abstract Graph loadGraph(String paramString)
			throws FileNotFoundException, IOException;

	public abstract Graph loadGraph(URL paramURL)
			throws IOException;

	public abstract Graph loadGraph(File paramFile)
			throws FileNotFoundException, IOException;

	public abstract Graph loadGraph(InputStream paramInputStream)
			throws IOException;
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/GraphReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */