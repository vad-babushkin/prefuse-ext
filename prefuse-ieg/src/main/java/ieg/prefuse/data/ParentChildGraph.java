package ieg.prefuse.data;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.data.tuple.TableEdge;
import prefuse.data.tuple.TupleManager;

/**
 * Hierarchical structure where a node can have multiple parents and multiple
 * children. This is realized as a directed {@link Graph}, where inbound edges
 * connect a node to its children and outgoing edges connect it to its parents.
 * Note that edges direction is opposite in the {@link Tree} class.
 * 
 * <p>
 * The proxy tuple type {@link ParentChildNode} provides suitable convenience methods.
 * 
 * <p>
 * Added:         201?-??-?? / AR<br>
 * Modifications: 2013-06-11 / AR / tuple manager intialized
 * </p>
 * 
 * @author Rind
 */
public class ParentChildGraph extends Graph {

    /**
     * Instances are always directed.
     */
    public ParentChildGraph() {
        super(true);
        initTupleManagers(ParentChildNode.class, TableEdge.class);
    }
    
    public ParentChildGraph(Table nodes, boolean initTupleManager) {
        super(nodes, true);
        if (initTupleManager) {
            initTupleManagers(ParentChildNode.class, TableEdge.class);
        }
    }

    public ParentChildGraph(Table nodes,
            Class<? extends ParentChildNode> nodeType) {
        super(nodes, true);
        initTupleManagers(nodeType, TableEdge.class);
    }

    public ParentChildGraph(Table nodes, Table edges, boolean initTupleManager) {
        super(nodes, edges, true);
        if (initTupleManager) {
            initTupleManagers(ParentChildNode.class, TableEdge.class);
        }
    }

    public ParentChildGraph(Table nodes, Table edges,
            Class<? extends ParentChildNode> nodeType) {
        super(nodes, edges, true);
        initTupleManagers(nodeType, TableEdge.class);
    }

    public void initTupleManagers(Class<? extends ParentChildNode> nodeType,
            Class<? extends Edge> edgeType) {
        TupleManager nodeTuples = new TupleManager(super.getNodeTable(), this,
                nodeType);
        TupleManager edgeTuples = new TupleManager(super.getEdgeTable(), this,
                edgeType);
        initTupleManagers(nodeTuples, edgeTuples);
    }

    public void initTupleManagers(TupleManager nodeTuples,
            TupleManager edgeTuples) {
        super.setTupleManagers(nodeTuples, edgeTuples);
        super.getNodeTable().setTupleManager(nodeTuples);
        super.getEdgeTable().setTupleManager(edgeTuples);
    }

    /**
     * Get the number of children of the given node id.
     * 
     * @param node
     *            a node id (node table row number)
     * @return the number of child nodes for the given node
     */
    public int getChildCount(int node) {
        return getInDegree(node);
    }

    /**
     * Get the child node id at the given index.
     * 
     * @param node
     *            the parent node id (node table row number)
     * @param idx
     *            the child index
     * @return the child node id (node table row number)
     */
    public int getChildRow(int node, int idx) {
        int cc = getChildCount(node);
        if (idx < 0 || idx >= cc)
            return -1;
        int[] links = (int[]) m_links.get(node, INLINKS);
        return getSourceNode(links[idx]);
    }

    /**
     * Get the number of parents of the given node id.
     * 
     * @param node
     *            a node id (node table row number)
     * @return the number of parent nodes for the given node
     */
    public int getParentCount(int node) {
        return getOutDegree(node);
    }

    /**
     * Get the parent node id at the given index.
     * 
     * @param node
     *            the child node id (node table row number)
     * @param idx
     *            the parent index
     * @return the parent node id (node table row number)
     */
    public int getParentRow(int node, int idx) {
        int cc = getParentCount(node);
        if (idx < 0 || idx >= cc)
            return -1;
        int[] links = (int[]) m_links.get(node, OUTLINKS);
        return getTargetNode(links[idx]);
    }

    // TODO implement depth using m_links as cache for such metadata

}
