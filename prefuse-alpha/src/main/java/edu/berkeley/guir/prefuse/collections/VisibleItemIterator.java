package edu.berkeley.guir.prefuse.collections;

import edu.berkeley.guir.prefuse.VisualItem;

import java.util.Iterator;
import java.util.List;

public class VisibleItemIterator
		implements Iterator {
	private Iterator m_iter;
	private VisualItem m_item;

	public VisibleItemIterator(List paramList, boolean paramBoolean) {
		if (paramList.isEmpty()) {
			this.m_item = null;
		} else {
			this.m_iter = (paramBoolean ? new ReverseListIterator(paramList) : paramList.iterator());
			while ((this.m_iter.hasNext()) && (!(this.m_item = (VisualItem) this.m_iter.next()).isVisible())) {
			}
			if (!this.m_item.isVisible()) {
				this.m_item = null;
			}
		}
	}

	public boolean hasNext() {
		return this.m_item != null;
	}

	public Object next() {
		if (this.m_item != null) {
			VisualItem localVisualItem = this.m_item;
			while ((this.m_iter.hasNext()) && (!(this.m_item = (VisualItem) this.m_iter.next()).isVisible())) {
			}
			if ((!this.m_iter.hasNext()) && ((this.m_item == localVisualItem) || (!this.m_item.isVisible()))) {
				this.m_item = null;
			}
			return localVisualItem;
		}
		throw new IllegalStateException("Iterator has no next element.");
	}

	public void remove() {
		throw new UnsupportedOperationException("Remove not supported.");
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/VisibleItemIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */