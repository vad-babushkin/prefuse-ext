package ieg.prefuse.data;

import java.util.Iterator;

import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;
import prefuse.data.Tree;
import prefuse.data.Tuple;
import prefuse.data.expression.ColumnExpression;
import prefuse.data.expression.ComparisonPredicate;
import prefuse.data.expression.NumericLiteral;
import prefuse.data.tuple.TableTuple;

/**
 * Alternative tree implementation using links to the parent and an index on
 * this column. This implementation consumes less memory than {@link Tree} but
 * is slower than {@link LinkedTree}.
 * 
 * @author Rind
 */
public class PointerTree extends Table {

    public static final String FIELD_DEPTH = "_depth";
    public static final String FIELD_PARENT = "_parent";

    PointerNode root;

    public PointerTree() {
        super(0, 2, PointerNode.class);
        super.addColumn(FIELD_DEPTH, int.class);
        super.addColumn(FIELD_PARENT, int.class);
    }

    public void indexByParent() {
        super.index(FIELD_PARENT);
    }

    public void indexByDepth() {
        super.index(FIELD_DEPTH);
    }

    public PointerNode addRoot() {
        this.root = (PointerNode) super.getTuple(super.addRow());
        root.set(FIELD_DEPTH, 0);
        return root;
    }

    public PointerNode addChild(Tuple parent) {
        PointerNode added = (PointerNode) super.getTuple(super.addRow());
        added.set(FIELD_PARENT, parent.getRow());
        added.set(FIELD_DEPTH, parent.getInt(FIELD_DEPTH) + 1);
        return added;
    }

    @SuppressWarnings("rawtypes")
    public Iterator depth(int depth) {
        return super.tuples(new ComparisonPredicate(ComparisonPredicate.EQ,
                new ColumnExpression(FIELD_DEPTH), new NumericLiteral(depth)));
    }

    public PointerNode getRoot() {
        return root;
    }

    public PointerNode getNode(int row) {
        return (PointerNode) getTuple(row);
    }

    public static class PointerNode extends TableTuple implements Node {

        @Override
        public Node getParent() {
            int row = this.getInt(FIELD_PARENT);
            return row >= 0 ? (PointerNode) this.m_table.getTuple(row) : null;
        }

        @Override
        public int getDepth() {
            return this.getInt(FIELD_DEPTH);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator children() {
            return m_table.tuples(new ComparisonPredicate(
                    ComparisonPredicate.EQ, new ColumnExpression(FIELD_PARENT),
                    new NumericLiteral(m_row)));
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
        public Node getFirstChild() {
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

        @Override
        public Node getNextSibling() {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Iterator childEdges() {
            throw new UnsupportedOperationException();
        }

    }

}
