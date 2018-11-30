package edu.berkeley.guir.prefuse.collections;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReverseListIterator
		implements Iterator {
	ListIterator m_iter;

	public ReverseListIterator(List paramList) {
		this.m_iter = paramList.listIterator();
		while (this.m_iter.hasNext()) {
			this.m_iter.next();
		}
	}

	public void remove() {
		throw new UnsupportedOperationException("Remove not supported");
	}

	public boolean hasNext() {
		return this.m_iter.hasPrevious();
	}

	public Object next() {
		return this.m_iter.previous();
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/ReverseListIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */