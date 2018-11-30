package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.Graph;

import java.io.*;

public abstract class AbstractGraphWriter
		implements GraphWriter {
	public void writeGraph(Graph paramGraph, String paramString)
			throws FileNotFoundException, IOException {
		writeGraph(paramGraph, new FileOutputStream(paramString));
	}

	public void writeGraph(Graph paramGraph, File paramFile)
			throws FileNotFoundException, IOException {
		writeGraph(paramGraph, new FileOutputStream(paramFile));
	}

	public abstract void writeGraph(Graph paramGraph, OutputStream paramOutputStream)
			throws IOException;
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/AbstractGraphWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */