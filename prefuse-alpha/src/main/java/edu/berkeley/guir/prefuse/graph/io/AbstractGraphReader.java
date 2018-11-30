package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.Graph;

import java.io.*;
import java.net.URL;

public abstract class AbstractGraphReader
		implements GraphReader {
	public Graph loadGraph(String paramString)
			throws FileNotFoundException, IOException {
		return loadGraph(new FileInputStream(paramString));
	}

	public Graph loadGraph(URL paramURL)
			throws IOException {
		return loadGraph(paramURL.openStream());
	}

	public Graph loadGraph(File paramFile)
			throws FileNotFoundException, IOException {
		return loadGraph(new FileInputStream(paramFile));
	}

	public abstract Graph loadGraph(InputStream paramInputStream)
			throws IOException;
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/AbstractGraphReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */