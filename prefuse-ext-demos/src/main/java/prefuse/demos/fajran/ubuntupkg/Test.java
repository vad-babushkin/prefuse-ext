package prefuse.demos.fajran.ubuntupkg;

import java.io.IOException;
import java.util.Iterator;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Table edges, nodes;
		
		nodes = new Table();
		nodes.addColumn("id", int.class);
		nodes.addColumn("pkg", String.class);
		
		edges = new Table();
		edges.addColumn("src", int.class);
		edges.addColumn("dst", int.class);
		
		int row; 
		row = nodes.addRow();
		nodes.set(row, 0, 1);
		nodes.set(row, 1, "Satu");
		
		row = nodes.addRow();
		nodes.set(row, 0, 2);
		nodes.set(row, 1, "Dua");
		
		row = nodes.addRow();
		nodes.set(row, 0, 3);
		nodes.set(row, 1, "Tiga");
		
		row = edges.addRow();
		edges.set(row, 0, 1);
		edges.set(row, 1, 2);
		
		row = edges.addRow();
		edges.set(row, 0, 1);
		edges.set(row, 1, 3);
		
		Graph g = new Graph(nodes, edges, false, "id", "src", "dst");
		
//		Graph g = null;
//		try {
//			final String BASE = "/Users/iang/kuliah2/tesis/test/ubuntu/data/small/";
//		    g = new DataReader().readGraph(BASE + "/nodes.txt", BASE + "/edges.txt");
//		} catch ( IOException e ) {
//		    e.printStackTrace();
//		    System.err.println("Error loading graph. Exiting...");
//		    System.exit(1);
//		}
		
		Node n = g.getNode(0);
		
		System.out.println("Node: " + n);
		System.out.println("Degree: " + n.getDegree());
		System.out.println("In Degree: " + n.getInDegree());
		System.out.println("Out Degree: " + n.getOutDegree());
		
		Iterator iter = n.outNeighbors();
		while (iter.hasNext()) {
			System.out.println("Out Neighbor: " + iter.next());
		}
		
		iter = n.outEdges();
		while (iter.hasNext()) {
			System.out.println("Out Edge: " + iter.next());
		}
		
//		iter = g.nodes();
//		while (iter.hasNext()) {
//			System.out.println("> Node: " + iter.next());
//		}
//		
//		iter = g.edges();
//		while (iter.hasNext()) {
//			System.out.println("> Edge: " + iter.next());
//		}
		
		
	}

}
