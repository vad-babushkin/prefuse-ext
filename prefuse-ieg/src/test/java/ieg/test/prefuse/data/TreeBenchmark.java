package ieg.test.prefuse.data;

import ieg.prefuse.data.LinkedTree;
import ieg.prefuse.data.PointerTree;

import java.util.Iterator;

import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Tree;
import prefuse.data.Tuple;

/**
 * Compares the time and memory requirements of {@link Tree}, 
 * {@link LinkedTree}, and {@link PointerTree}.
 * 
 * @author Rind
 */
public class TreeBenchmark {

    private static final int SIZE = 2 * 100 * 1000;
    private static final int SAMPLES = 20;
    private static final String HEADER = "sample; task; class; time; memory; playload\n";
    private static final String FORMAT = "%2d; %9s; %9s; %4d; %4d; %8d %n";

    static Schema schema = new Schema();
    static {
        schema.addColumn("int", int.class);
        schema.addColumn("double", double.class);
        schema.addColumn("boolean", boolean.class);
        schema.addColumn("string", String.class);
        // schema.addColumn("int2", int.class);
        // schema.addColumn("double2", double.class);
        // schema.addColumn("boolean2", boolean.class);
        // schema.addColumn("string2", String.class);
        // schema.addColumn("int3", int.class);
        // schema.addColumn("double3", double.class);
        // schema.addColumn("boolean3", boolean.class);
        // schema.addColumn("string3", String.class);
    }

    @SuppressWarnings("rawtypes")
    private static void testTree() {
        long time, mem;
        int value = 0; // we have to do something that looks important to the
                       // compiler/optimizer

        for (int i = 0; i < SAMPLES; i++) {
            time = 0 - System.currentTimeMillis();
            Tree tree = new Tree();
            tree.getNodeTable().addColumns(schema);
            Node parent = tree.addRoot();
            for (int j = 1; j < SIZE; j++) {
                Node n = tree.addChild(parent);
                if (Math.random() < 0.5) {
                    parent = n;
                }
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "add", "tree", time, mem, value);

            time = 0 - System.currentTimeMillis();
            for (int j = 0; j < SIZE; j++) {
                Iterator iter = tree.getNode((int) (Math.random() * SIZE))
                        .children();
                value = 0;
                while (iter.hasNext()) {
                    value += ((Node) iter.next()).getInt("int");
                }
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "childs", "tree", time, mem, value);

            time = 0 - System.currentTimeMillis();
            for (int j = 0; j < SIZE; j++) {
                Node node = tree
                        .getNode((int) (Math.random() * (SIZE - 1) + 1))
                        .getParent();
                value = node.getInt("int");
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "parent", "tree", time, mem, value);

            // Tree does not support getting nodes by depth
            // time = 0 - System.currentTimeMillis();
            // for (int j=0; j<SIZE; j++) {
            // Node p = tree. .getNode((int)(Math.random()*20)).getParent();
            // value = parent.getInt("int");
            // }
            // time += System.currentTimeMillis();
            // System.out.printf(FORMAT, i, "depth", "tree", time, value);

            System.gc();
        }
    }

    @SuppressWarnings("rawtypes")
    private static void testPointerTree() {
        long time, mem;
        int value = 0; // we have to do something that looks important to the
                       // compiler/optimizer

        for (int i = 0; i < SAMPLES; i++) {
            time = 0 - System.currentTimeMillis();
            PointerTree tree = new PointerTree();
            tree.addColumns(schema);
            Node parent = tree.addRoot();
            for (int j = 1; j < SIZE; j++) {
                Node n = tree.addChild(parent);
                if (Math.random() < 0.5) {
                    parent = n;
                }
                tree.indexByParent();
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "add", "point", time, mem, value);

            time = 0 - System.currentTimeMillis();
            for (int j = 0; j < SIZE; j++) {
                Iterator iter = tree.getNode((int) (Math.random() * SIZE))
                        .children();
                value = 0;
                while (iter.hasNext()) {
                    value += ((Node) iter.next()).getInt("int");
                }
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "childs", "point", time, mem, value);

            time = 0 - System.currentTimeMillis();
            for (int j = 0; j < SIZE; j++) {
                Node node = tree
                        .getNode((int) (Math.random() * (SIZE - 1) + 1))
                        .getParent();
                value = node.getInt("int");
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "parent", "point", time, mem, value);

            // time = 0 - System.currentTimeMillis();
            // for (int j=0; j<SIZE; j++) {
            // Iterator iter = tree.depth((int)(Math.random()*20));
            // value = 0;
            // while (iter.hasNext()) {
            // value += ((Node)iter.next()).getInt("int");
            // }
            // }
            // time += System.currentTimeMillis();
            // mem = (Runtime.getRuntime().totalMemory() -
            // Runtime.getRuntime().freeMemory()) / 1024;
            // System.out.printf(FORMAT, i, "depth", "point", time, mem, value);

            System.gc();
        }
    }

    private static void testLinkedTree() {
        long time, mem;
        int value = 0; // we have to do something that looks important to the
                       // compiler/optimizer

        for (int i = 0; i < SAMPLES; i++) {
            time = 0 - System.currentTimeMillis();
            LinkedTree tree = new LinkedTree();
            tree.addColumns(schema);
            Node parent = tree.addRoot();
            for (int j = 1; j < SIZE; j++) {
                Node n = tree.addChild(parent);
                if (Math.random() < 0.5) {
                    parent = n;
                }
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "add", "link", time, mem, value);

            time = 0 - System.currentTimeMillis();
            for (int j = 0; j < SIZE; j++) {
                Iterator<Tuple> iter = tree.getNode(
                        (int) (Math.random() * SIZE)).children();
                value = 0;
                while (iter.hasNext()) {
                    value += iter.next().getInt("int");
                }
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "childs", "link", time, mem, value);

            time = 0 - System.currentTimeMillis();
            for (int j = 0; j < SIZE; j++) {
                Node node = tree
                        .getNode((int) (Math.random() * (SIZE - 1) + 1))
                        .getParent();
                value = node.getInt("int");
            }
            time += System.currentTimeMillis();
            mem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                    .freeMemory()) / 1024;
            System.out.printf(FORMAT, i, "parent", "link", time, mem, value);

            // time = 0 - System.currentTimeMillis();
            // for (int j=0; j<SIZE; j++) {
            // Iterator iter = tree.depth((int)(Math.random()*20));
            // value = 0;
            // while (iter.hasNext()) {
            // value += ((Node)iter.next()).getInt("int");
            // }
            // }
            // time += System.currentTimeMillis();
            // mem = (Runtime.getRuntime().totalMemory() -
            // Runtime.getRuntime().freeMemory()) / 1024;
            // System.out.printf(FORMAT, i, "depth", "point", time, mem, value);

            System.gc();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.print(HEADER);
        testLinkedTree();
        testPointerTree();
        testTree();
        testPointerTree();
        testTree();
        testLinkedTree();
    }

}
