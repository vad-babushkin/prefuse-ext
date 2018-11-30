package edu.berkeley.guir.prefuse.collections;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class WrapAroundIterator
		implements Iterator {
	private int m_cur;
	private int m_count;
	private int m_size;
	private List m_items;

	public WrapAroundIterator(List paramList, int paramInt) {
		this(paramList, paramInt, paramList.size() - 1);
	}

	public WrapAroundIterator(List paramList, int paramInt1, int paramInt2) {
		if (paramInt1 > paramInt2) {
			throw new IllegalArgumentException();
		}
		this.m_items = paramList;
		this.m_cur = paramInt1;
		this.m_count = 0;
		this.m_size = (paramInt2 + 1);
	}

	public boolean hasNext() {
		return this.m_count < this.m_size;
	}

	public Object next() {
		if (this.m_count >= this.m_size) {
			throw new NoSuchElementException("Iterator has no next element.");
		}
		int i = this.m_cur;
		this.m_cur = (++this.m_cur % this.m_size);
		this.m_count += 1;
		return this.m_items.get(i);
	}

	public void remove() {
		throw new UnsupportedOperationException("Remove not supported");
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/WrapAroundIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */