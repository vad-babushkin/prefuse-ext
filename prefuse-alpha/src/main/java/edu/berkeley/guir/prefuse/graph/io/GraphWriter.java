package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface GraphWriter {
	public abstract void writeGraph(Graph paramGraph, String paramString)
			throws FileNotFoundException, IOException;

	public abstract void writeGraph(Graph paramGraph, File paramFile)
			throws FileNotFoundException, IOException;

	public abstract void writeGraph(Graph paramGraph, OutputStream paramOutputStream)
			throws IOException;
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/GraphWriter.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */