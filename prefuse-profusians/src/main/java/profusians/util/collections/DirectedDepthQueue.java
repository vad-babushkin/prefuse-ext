package profusians.util.collections;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * profusians utility class for directed graphs
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */
public class DirectedDepthQueue {

    private  LinkedList m_list = new LinkedList();

    private  HashMap m_map = new HashMap();

    public void clear() {
	m_list.clear();
	m_map.clear();
    }

    public boolean isEmpty() {
	return m_list.isEmpty();
    }

    public void add( Object o, int downDepth, int upDepth) {
	m_list.add(o);
	m_map.put(o, getArray(downDepth, upDepth));
    }

    public void visit( Object o, int downDepth, int upDepth) {
	m_map.put(o, getArray(downDepth, upDepth));
    }

    public int getSuccessorDepth( Object o) {
	 int[] d = (int[]) m_map.get(o);
	return (d == null ? -1 : d[0]);
    }

    public int getPredecessorDepth( Object o) {
	 int[] d = (int[]) m_map.get(o);
	return (d == null ? -1 : d[1]);
    }

    public Object removeFirst() {
	return m_list.removeFirst();
    }

    public Object removeLast() {
	return m_list.removeLast();
    }

    private int[] getArray( int a, int b) {
	 int[] d = new int[2];
	d[0] = a;
	d[1] = b;
	return d;
    }

} // end of class DirectedDepthQueue
