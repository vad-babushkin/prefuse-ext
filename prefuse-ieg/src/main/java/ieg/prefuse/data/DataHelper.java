package ieg.prefuse.data;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.column.ColumnMetadata;
import prefuse.data.expression.Predicate;
import prefuse.data.tuple.TupleSet;
import prefuse.util.collections.IntIterator;

public class DataHelper {

    /**
     * build a graph from a table.  
     * 
     * @param table the table from which to build a graph
     * @return the created graph
     * */
    public static Graph buildGraph(Table table) {
        return buildGraph(table, null, null, null);
    }

    /**
     * build a graph from a table.  
     * 
     * @param table the table from which to build a graph
     * @param groupField the field to group tuples
     * @param sortField the field to sort tuples
     * @param missingValue 
     * @return the created graph
     * */
    public static Graph buildGraph(Table table, String groupField, String sortField, Predicate missingValue) {
        // create a graph with node schema like input table schema
        Schema schema = table.getSchema();
        Graph graph = new Graph(schema.instantiate(), true);
        if (groupField != null)
            graph.getEdgeTable().addColumn(groupField, String.class);

        // remember previous node for each time series
        TreeMap<String, Node> prevTupleMap = new TreeMap<String, Node>();

//      for (int i = 0; i < table.getTupleCount(); i++) {
//      Tuple t = (Tuple) table.getTuple(i);

        IntIterator rows = sortField!= null ? table.rowsSortedBy(sortField, true) : table.rows();
        
        while (rows.hasNext()) {
            Tuple t = (Tuple) table.getTuple(rows.nextInt());
            String code = groupField != null ? t.getString(groupField) : "default";
            
            if (missingValue != null && missingValue.getBoolean(t)) {
                // skip this tuple and interrupt the graph
                prevTupleMap.remove(code);
                continue;
            }
            

            // create a new graph node and fill it with input values
            Node cur = graph.addNode();
            for (int col = 0; col < t.getColumnCount(); col++) {
                cur.set(col, t.get(col));
            }

            // connect if possible
            if (prevTupleMap.containsKey(code)) {
                Edge edge = graph.addEdge(prevTupleMap.get(code), cur);
                if (groupField != null)
                    edge.set(groupField, code);
            }

            // remember node for connection
            prevTupleMap.put(code, cur);
        }

        return graph;
    }

    /**
     * build a debug string on meta data of a table column
     * @param t the table to debug
     * @param col the column number of the data field to retrieve
     * @return a debug string
     */
    public static String debugColumnMeta(Table t, int col) {
        return t.getColumnName(col)
                + " type="
                + t.getColumnType(col).getName()
                + " min="
                + t.getString(
                        t.getMetadata(t.getColumnName(col)).getMinimumRow(), col)
                + " max="
                + t.getString(
                        t.getMetadata(t.getColumnName(col)).getMaximumRow(), col);
    }
    
    /**
     * build a debug string on values of a column
     * @param t the table to debug
     * @param col the column number of the data field to retrieve
     * @return a debug string
     */
    public static String debugColumnValues(Table t, int col) {
        StringBuilder sb = new StringBuilder(t.getString(0, col));
        for (int i=1; i < t.getRowCount(); i++) {
            sb.append(", ");
            sb.append(t.getString(i, col));
        }
        return sb.toString();
    }
    
    /**
     * Helper method to dump specific columns of a table or other tuple set to some output stream
     * @param out the output to use
     * @param table the Table to print
     * @param cols the name of the columns
     */
    public static void printTable(PrintStream out, TupleSet table, String... cols) {
    	printTable(out,table,null,cols);
    }
    
    /**
     * Helper method to dump specific columns of a table or other tuple set to some output stream
     * @param out the output to use
     * @param table the Table to print
     * @param info additional information interface
     * @param cols the name of the columns
     */
    @SuppressWarnings("unchecked")
    public static void printTable(PrintStream out, TupleSet table, AdditionalNodeInformation info, String... cols) {
        
        for (String c : cols) 
            out.printf(" %19s", c + " ");
            
		if (info != null)
			out.printf(info.provideHeading(table));
        
        out.println();

        Iterator<Tuple> i = table.tuples();
        while (i.hasNext()) {
            Tuple tuple = i.next();
            
            for (String c : cols) 
                if (tuple.canGetString(c))
                    out.printf(" %19s", tuple.getString(c) + " ");
            
    		if (info != null)
    			out.printf(info.provideAdditionalInformation(tuple));

            out.println();
        }
    }
    
    public static void printForest(PrintStream out, Table table, Iterable<? extends Tuple> roots, int depth, String idColumn, String... cols) {
    	printForest(out,table,roots,depth,idColumn,null,cols);
    }

    public static int pathCount;
    public static void printForest(PrintStream out, Table table, Iterable<? extends Tuple> roots, int depth, String idColumn, AdditionalNodeInformation info, String... cols) {
        
    	for(int i=0; i<depth; i++)
    		out.printf("  ");

    	out.printf("|");
    	
        for (String c : cols) 
            out.printf(" %19s", c + " ");
       
		if (info != null)
			out.printf(info.provideHeading(table));
        
        out.println();
        
        pathCount = 0;

        for(Tuple iTuple : roots) {
        	printForestRecursion(out, iTuple, 0, depth, info, cols);        	
        }
        
        out.println("Total number of paths: "+pathCount);
    }
    
    public static void printForestRecursion(PrintStream out, Tuple tuple, int currentDepth, int maxDepth, AdditionalNodeInformation info, String... cols) {
    	
    	for(int i=0; i<currentDepth; i++)
    		out.printf("  ");    	
    	out.printf("%2d",currentDepth);
    	for(int i=currentDepth+1; i<maxDepth; i++)
    		out.printf("  ");    	

    	out.printf("|");
    	
        for (String c : cols) 
            if (tuple.canGetString(c))
                out.printf(" %19s", tuple.getString(c) + " ");
        
		if (info != null)
			out.printf(info.provideAdditionalInformation(tuple));

        out.println();
        
    	if ( Node.class.isAssignableFrom(tuple.getClass())) {
    		    
    		Iterator<?> childs = ((Node)tuple).inNeighbors();
    		if(!childs.hasNext())
    			pathCount++;
    		while(childs.hasNext()) {
    			Tuple iTuple = (Tuple) childs.next();
    			printForestRecursion(out,iTuple,currentDepth+1,maxDepth,info,cols);
    		}
    	}
    }
    
    /**
     * Helper method to dump specific columns of a graph, starting at a given node, treating that one as root, ignoring cycles
     * @param out the output to use
     * @param start the node to start printing
     * @param info additional information interface
     * @param cols the name of the columns
     */
    public static void printGraph(PrintStream out, Node start, AdditionalNodeInformation info, String... cols) {

    	int depth = graphDepthHelper(start,new ArrayList<Node>());    	    
    	
		for(int j=0; j<depth;j++)
			out.printf("  ");
        for (String c : cols) 
            out.printf(" %19s", c + " ");
		if (info != null)
			out.printf(info.provideHeading(start.getTable()));
            
        out.println();

        printGraphHelper(out,start,info,cols,depth,new ArrayList<Node>(),0);
    }    
    
    /**
     * Helper method to dump a graph, starting at a given node, treating that one as root, ignoring cycles
     * @param out the output to use
     * @param start the node to start printing
     */
    public static void printGraph(PrintStream out, Node start) {
        printGraph(out, start, null);
    }
    
    /**
     * Helper method to dump a graph, starting at a given node, treating that one as root, ignoring cycles
     * @param out the output to use
     * @param start the node to start printing
     * @param info additional information interface
     */
    public static void printGraph(PrintStream out, Node start, AdditionalNodeInformation info) {
    	Table table = start.getTable();
        String[] cols = new String[table.getColumnCount()];
        for (int i=0; i< cols.length; i++) 
            cols[i] = table.getColumnName(i);
        
        printGraph(out, start, info, cols);
    }
    
    /**
     * Calculates depth of a graph from a node, treating that one as root, ignoring cycles
     * @param current node to recurse on
     * @param visited list of visited nodes
     * @return current depth
     */
    private static int graphDepthHelper(Node current,ArrayList<Node> visited) {
    	visited.add(current);
    	Node iNode;
    	int maxDepth = 0;
    	for(Iterator<?> i = current.neighbors(); i.hasNext();) {
    		iNode=(Node)i.next();
    		if(!visited.contains(iNode))
    			maxDepth = Math.max(maxDepth, graphDepthHelper(iNode,visited));   		
    	}
    	
    	return maxDepth+1;
    }
    
    /**
     * Helper method to dump specific columns of a graph, starting at a given node, treating that one as root, ignoring cycles
     * @param out the output to use
     * @param current node to recurse on
     * @param info additional information interface
     * @param cols the name of the columns
     * @param depth maximum graph depth
     * @param visited list of visited nodes
     * @param level current level
     */
    private static void printGraphHelper(PrintStream out, Node current, AdditionalNodeInformation info, String[] cols,int depth, ArrayList<Node> visited,int level) {
    	visited.add(current);
		for(int j=0; j<level;j++)
			out.printf("  ");
		out.printf("%2d", level);
		for(int j=level; j<depth-1;j++)
			out.printf("  ");
        for (String c : cols) 
            if (current.canGetString(c))
                out.printf(" %19s", current.getString(c) + " ");
		if(info != null)
			out.printf(info.provideAdditionalInformation(current));
        out.println();
    	Node iNode;
    	for(Iterator<?> i = current.neighbors(); i.hasNext();) {
    		iNode=(Node)i.next();
    		if(!visited.contains(iNode)) {
    	        printGraphHelper(out,iNode,info,cols,depth,visited,level+1);
    		}
    	}    	
    }
    
    public interface AdditionalNodeInformation {
    	public String provideHeading(TupleSet table);
    	public String provideAdditionalInformation(Tuple node);
    }
    
    /**
     * Alternative using {@link Schema#toString()}:
     * <tt>System.out.println(table.getSchema());</tt>
     * 
     * @param table
     */
    public static void printMetadata(PrintStream out, Table table) {
        out.println(" #            name       type      default     minimum      maximum");

        boolean empty = table.getRowCount() == 0;
        for (int i = 0; i < table.getColumnCount(); i++) {
            String name = table.getColumnName(i);
            @SuppressWarnings("rawtypes")
            Class type = table.getColumnType(i);
            ColumnMetadata meta = table.getMetadata(name);

            out.printf("%2d %15s %10s %12s", i, name, type.getSimpleName(),
                    table.getColumn(i).getDefaultValue());
            if (!empty) {
                out.printf("%12s %12s%n", table.get(meta.getMinimumRow(), i),
                        table.get(meta.getMaximumRow(), i));
            } else {
                out.printf("%12s %12s%n", "n.a.", "n.a.");
            }
        }
    }
    
    /**
     * Helper method to dump a table to some output stream
     * @param out the output to use
     * @param table the Table to print
     */
    public static void printTable(PrintStream out, Table table) {
        String[] cols = new String[table.getColumnCount()];
        for (int i=0; i< cols.length; i++) 
            cols[i] = table.getColumnName(i);
    	
    	printTable(out, table, null, cols);
    }

    /**
     * Helper method to dump a table to some output stream
     * @param out the output to use
     * @param table the Table to print
     * @param info additional information interface
     */
    public static void printTable(PrintStream out, Table table, AdditionalNodeInformation info) {
        String[] cols = new String[table.getColumnCount()];
        for (int i=0; i< cols.length; i++) 
            cols[i] = table.getColumnName(i);
        
        printTable(out, table, info, cols);
    }
}
