package edu.berkeley.guir.prefuse.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleElementIterator
		implements Iterator {
	private Object object;

	public SingleElementIterator(Object paramObject) {
		this.object = paramObject;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
		return this.object != null;
	}

	public Object next() {
		if (this.object != null) {
			Object localObject = this.object;
			this.object = null;
			return localObject;
		}
		throw new NoSuchElementException();
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/collections/SingleElementIterator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */