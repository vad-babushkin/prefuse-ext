package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.ItemRegistry;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class CompositeItemIterator
		implements Iterator {
	private Iterator[] m_iter;
	private Object[] m_item;
	private Comparator m_comp;
	private boolean m_reverse;
	private int m_emptyCount;

	public CompositeItemIterator(List paramList, Comparator paramComparator, boolean paramBoolean1, boolean paramBoolean2) {
		int i = paramList.size();
		this.m_emptyCount = 0;
		this.m_iter = new Iterator[i];
		this.m_item = new Object[i];
		for (int j = 0; j < i; j++) {
			List localList = ((ItemRegistry.ItemEntry) paramList.get(j)).getItemList();
			if (paramBoolean1) {
				this.m_iter[j] = new VisibleItemIterator(localList, paramBoolean2);
			} else if (paramBoolean2) {
				this.m_iter[j] = new ReverseListIterator(localList);
			} else {
				this.m_iter[j] = localList.iterator();
			}
			if (this.m_iter[j].hasNext()) {
				this.m_item[j] = this.m_iter[j].next();
			} else {
				this.m_emptyCount += 1;
			}
		}
		this.m_reverse = paramBoolean2;
		this.m_comp = paramComparator;
	}

	public boolean hasNext() {
		return this.m_emptyCount < this.m_item.length;
	}

	public Object next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		int i = -1;
		for (int j = 0; j < this.m_item.length; j++) {
			if (this.m_item[j] != null) {
				if (i == -1) {
					i = j;
				} else {
					int k = this.m_comp.compare(this.m_item[i], this.m_item[j]);
					if (this.m_reverse) {
						k *= -1;
					}
					i = k < 0 ? i : j;
				}
			}
		}
		Object localObject = null;
		try {
			localObject = this.m_item[i];
		} catch (Exception localException) {
			System.out.println("");
		}
		this.m_item[i] = (this.m_iter[i].hasNext() ? this.m_iter[i].next() : null);
		if (this.m_item[i] == null) {
			this.m_emptyCount += 1;
		}
		return localObject;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/CompositeItemIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */