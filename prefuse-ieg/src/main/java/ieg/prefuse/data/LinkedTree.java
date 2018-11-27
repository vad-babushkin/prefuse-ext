package ieg.prefuse.data;

import java.util.Iterator;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.expression.ColumnExpression;
import prefuse.data.expression.ComparisonPredicate;
import prefuse.data.expression.NumericLiteral;
import prefuse.data.tuple.TableTuple;

/**
 * Alternative tree implementation using links to first child, next sibling, and
 * parent. This implementation consumes less memory and is faster than
 * {@link Tree} but lacks the possibility to store data on edges. Some features
 * are not implemented (e.g. removing nodes).
 * 
 * @author Rind
 */
public class LinkedTree extends Table {

    public static final String FIELD_DEPTH = "_depth";

    public static final String FIELD_PARENT = "_parent";
    public static final String FIELD_FIRST_CHILD = "_child";
    public static final String FIELD_NEXT_SIBLING = "_sibling";

    LinkedNode root;

    public LinkedTree() {
    	this(null);
    }
    
    public LinkedTree(Schema s) {
   		super(0, s == null ? 4 : s.getColumnCount()+4, LinkedNode.class);
        super.addColumn(FIELD_DEPTH, int.class, -1);
        super.addColumn(FIELD_PARENT, int.class, -1);
        super.addColumn(FIELD_FIRST_CHILD, int.class, -1);
        super.addColumn(FIELD_NEXT_SIBLING, int.class, -1);
        if (s != null) {
            super.addColumns(s);
        }
    }

    public LinkedNode addRoot() {
        this.root = (LinkedNode) super.getTuple(super.addRow());
        root.set(FIELD_DEPTH, 0);
        return root;
    }

    public LinkedNode addChild(Tuple parent) {
        int addedRow = super.addRow();
        LinkedNode added = (LinkedNode) super.getTuple(addedRow);
        added.set(FIELD_PARENT, parent.getRow());

        int siblingRow = parent.getInt(FIELD_FIRST_CHILD);
        if (siblingRow == -1) {
            parent.setInt(FIELD_FIRST_CHILD, addedRow);
        } else {
            int nextSiblingRow = super.getInt(siblingRow, FIELD_NEXT_SIBLING);
            while (nextSiblingRow > -1) {
                siblingRow = nextSiblingRow;
                nextSiblingRow = super.getInt(siblingRow, FIELD_NEXT_SIBLING);
            }
            super.setInt(siblingRow, FIELD_NEXT_SIBLING, addedRow);
        }

        added.set(FIELD_DEPTH, parent.getInt(FIELD_DEPTH) + 1);

        return added;
    }

    @SuppressWarnings("rawtypes")
    public Iterator nodesAtDepth(int depth) {
        super.index(FIELD_DEPTH);
        return super.tuples(new ComparisonPredicate(ComparisonPredicate.EQ,
                new ColumnExpression(FIELD_DEPTH), new NumericLiteral(depth)));
    }

    @SuppressWarnings("rawtypes")
    public Iterator leaves() {
        super.index(FIELD_FIRST_CHILD);
        return super
                .tuples(new ComparisonPredicate(ComparisonPredicate.EQ,
                        new ColumnExpression(FIELD_FIRST_CHILD),
                        new NumericLiteral(-1)));
    }

    public LinkedNode getRoot() {
        return root;
    }

    public LinkedNode getNode(int row) {
        return (LinkedNode) getTuple(row);
    }

    public static class LinkedNode extends TableTuple implements Node {

        @Override
        public Node getParent() {
            int row = this.getInt(FIELD_PARENT);
            return row >= 0 ? (LinkedNode) this.m_table.getTuple(row) : null;
        }

        @Override
        public Node getFirstChild() {
            int row = this.getInt(FIELD_FIRST_CHILD);
            return row >= 0 ? (LinkedNode) this.m_table.getTuple(row) : null;
        }

        @Override
        public Node getNextSibling() {
            int row = this.getInt(FIELD_NEXT_SIBLING);
            return row >= 0 ? (LinkedNode) this.m_table.getTuple(row) : null;
        }

        @Override
        public int getDepth() {
            return this.getInt(FIELD_DEPTH);
        }

        @Override
        public Iterator<Tuple> children() {
            return new ChildIterator(m_table, m_row);
        }
        
        public LinkedNode addChild() {
        	return ((LinkedTree)this.getTable()).addChild(this);
        }

        @Override
        public Graph getGraph() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getInDegree() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getOutDegree() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getDegree() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator inEdges() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator outEdges() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator edges() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator inNeighbors() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator outNeighbors() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator neighbors() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Edge getParentEdge() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getChildCount() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getChildIndex(Node child) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Node getChild(int idx) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Node getLastChild() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Node getPreviousSibling() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator childEdges() {
            throw new UnsupportedOperationException();
        }

    }

    static class ChildIterator implements Iterator<Tuple> {

        Table table;
        int nextRow;

        public ChildIterator(Table table, int parentRow) {
            this.table = table;
            this.nextRow = table.getInt(parentRow, FIELD_FIRST_CHILD);
        }

        @Override
        public boolean hasNext() {
            return nextRow > -1;
        }

        @Override
        public Tuple next() {
            int row = nextRow;
            nextRow = table.getInt(row, FIELD_NEXT_SIBLING);
            return table.getTuple(row);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
