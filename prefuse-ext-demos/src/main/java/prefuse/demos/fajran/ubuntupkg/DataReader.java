package prefuse.demos.fajran.ubuntupkg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.util.TypeLib;

public class DataReader {

	public Graph readGraph(String fnNodes, String fnEdges) throws IOException {
		return readGraph(new FileInputStream(fnNodes), new FileInputStream(fnEdges));
	}
	
	public Graph readGraph(InputStream isNodes, InputStream isEdges) throws IOException {
		BufferedReader br;
		String line;
		int row;
		
		/*
		 * Read nodes
		 */
		
		Table nodes = new Table();
		nodes.addColumn("id", int.class);
		nodes.addColumn("pkg", String.class);
		
		br = new BufferedReader(new InputStreamReader(isNodes));
		while ((line = br.readLine()) != null) {
			String[] p = line.split("\\s+");
			
			row = nodes.addRow();
			nodes.set(row, 0, Integer.parseInt(p[0]));
			nodes.set(row, 1, p[1]);
		}
		
		/*
		 * Read edges
		 */
		
		Table edges = new Table();
		edges.addColumn("src", int.class);
		edges.addColumn("dst", int.class);
		
		br = new BufferedReader(new InputStreamReader(isEdges));
		while ((line = br.readLine()) != null) {
			String[] p = line.split("\\s+");
			
			row = edges.addRow();
			edges.set(row, 0, Integer.parseInt(p[0]));
			edges.set(row, 1, Integer.parseInt(p[1]));
		}
		
		/*
		 * Create graph
		 */
		
		Graph graph = new Graph(nodes, edges, true, "id", "src", "dst");
		
		return graph;
	}

}
