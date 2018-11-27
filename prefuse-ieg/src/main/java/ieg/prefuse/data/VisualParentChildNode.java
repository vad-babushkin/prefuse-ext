package ieg.prefuse.data;

import java.util.Iterator;

import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.tuple.TupleManager;
import prefuse.visual.VisualGraph;
import prefuse.visual.tuple.TableEdgeItem;
import prefuse.visual.tuple.TableNodeItem;

/**
 * Warning: Experimental.
 * @author Rind
 */
public class VisualParentChildNode extends TableNodeItem {
    
    /**
     * Warning: Invalidates existing tuples.
     * @param vg
     */
    public static void useWith(VisualGraph vg) {
        TupleManager ntm = new TupleManager(vg.getNodeTable(), vg, VisualParentChildNode.class);
        TupleManager etm = new TupleManager(vg.getEdgeTable(), vg, TableEdgeItem.class);
        vg.setTupleManagers(ntm, etm);
        vg.getNodeTable().setTupleManager(ntm);
        vg.getEdgeTable().setTupleManager(etm);
    }

    // ----- child methods -----

    @Override
    public int getChildCount() {
        return super.m_graph.getInDegree(this);
    }

    @Override
    public ParentChildNode getChild(int idx) {
        int cc = getChildCount();
        if (idx < 0 || idx >= cc)
            return null;
        
//        int[] links = (int[]) m_links.get(node, INLINKS);
//        return getSourceNode(links[idx]);
//
//        int c = getGraph().getChildRow(m_row, idx);
//        return (ParentChildNode) (c < 0 ? null : m_graph.getNode(c));
        throw new UnsupportedOperationException("does not work yet -- ask Alex");
    }

    @Override
    public ParentChildNode getFirstChild() {
        return getChild(0);
    }

    @Override
    public ParentChildNode getLastChild() {
        return getChild(getChildCount() - 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<? extends ParentChildNode> children() {
        return super.inNeighbors();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<? extends Edge> childEdges() {
        return super.inEdges();
    }

    /**
     * Links a node as child to this node.
     * 
     * @param child
     *            The node that will be added as child.
     * @return the edge that was added between the two nodes
     */
    public Edge linkWithChild(ParentChildNode child) {
//        if (Logger.getLogger(this.getClass()).isTraceEnabled()) {
//            Logger.getLogger(this.getClass()).trace(
//                    "link with child: " + this.getRow() + " <- "
//                            + child.getRow() + " my childs: "
//                            + this.getChildCount() + " total childs: "
//                            + super.m_graph.getEdgeCount() + " total nodes: "
//                            + super.m_graph.getNodeCount());
//        }

        return super.m_graph.addEdge(child, this);
    }

    // ----- parent methods -----

    /**
     * Gets the number of parent nodes
     * 
     * @return the number of parent nodes
     */
    public int getParentCount() {
        return super.m_graph.getOutDegree(this);
    }

    public ParentChildNode getParent(int idx) {
//        int c = getGraph().getParentRow(m_row, idx);
//        return (ParentChildNode) (c < 0 ? null : m_graph.getNode(c));
        throw new UnsupportedOperationException("does not work yet -- ask Alex");
    }

    public ParentChildNode getFirstParent() {
        return getParent(0);
    }

    public ParentChildNode getLastParent() {
        return getParent(getParentCount() - 1);
    }

    @SuppressWarnings("unchecked")
    public Iterator<? extends ParentChildNode> parents() {
        return super.outNeighbors();
    }

    @SuppressWarnings("unchecked")
    public Iterator<? extends Edge> parentEdges() {
        return super.outEdges();
    }

    /**
     * Links a node as parent to this node.
     * 
     * @param parent
     *            The node that will be added as parent.
     * @return the edge that was added between the two nodes
     */
    public Edge linkWithParent(ParentChildNode parent) {
//        if (Logger.getLogger(this.getClass()).isTraceEnabled()) {
//            Logger.getLogger(this.getClass()).trace(
//                    "link with parent: " + this.getRow() + " -> "
//                            + parent.getRow() + " my childs: "
//                            + this.getChildCount() + " total childs: "
//                            + super.m_graph.getEdgeCount() + " total nodes: "
//                            + super.m_graph.getNodeCount());
//        }

        return super.m_graph.addEdge(this, parent);
    }

    // ----- methods that do not make sense -----

    @Override
    public Node getParent() {
        throw new UnsupportedOperationException();
        // return super.getParent();
    }

    @Override
    public Edge getParentEdge() {
        throw new UnsupportedOperationException();
        // return super.getParentEdge();
    }

    @Override
    public int getChildIndex(Node child) {
        throw new UnsupportedOperationException();
        // return super.getChildIndex(child);
    }

    @Override
    public Node getPreviousSibling() {
        throw new UnsupportedOperationException();
        // return super.getPreviousSibling();
    }

    @Override
    public Node getNextSibling() {
        throw new UnsupportedOperationException();
        // return super.getNextSibling();
    }
    
}
