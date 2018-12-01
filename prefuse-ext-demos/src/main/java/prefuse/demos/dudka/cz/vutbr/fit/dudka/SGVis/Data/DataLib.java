package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

import java.util.Iterator;

import prefuse.util.collections.IntIterator;

/**
 * General usage static methods.
 * This is a library class (containings only static methods).
 */
public final class DataLib {
	private DataLib() { }
	/**
	 * Check if object iterated by given has minimal count of items.
	 * This method destructs iterator!
	 * @param iter Iterator to use for iteration (and check).
	 * @param minSize Minimal requested count of items.
	 * @return Returns true, if object has at least minSize of items.
	 */
	public static <T> boolean hasMinSize(Iterator<T> iter, int minSize) {
		for(int i=0; i<minSize; i++) {
			if (!iter.hasNext())
				return false;
			iter.next();
		}
		return true;
	}
	/**
	 * Check if given (iterable) object has minimal count of items.
	 * This method destructs iterator!
	 * @param container Object to check.
	 * @param minSize Minimal requested count of items.
	 * @return Returns true, if object has at least minSize of items.
	 */
	public static <T> boolean hasMinSize(Iterable<T> container, int minSize) {
		return hasMinSize( container.iterator(), minSize);
	}
	/*private static class IntIteratorWrapper implements Iterator<Object> {
		private IntIterator iter;

		public IntIteratorWrapper(IntIterator iter) {
			this.iter = iter;
		}
		public boolean hasNext() {
			return iter.hasNext();
		}
		public Object next() {
			return iter.next();
		}
		public void remove() {
			throw new AssertionError("not implemented");
		}
	}*/
	/**
	 * Check if object iterated by given has minimal count of items.
	 * This method destructs iterator!
	 * @param iter Iterator to use for iteration (and check).
	 * @param minSize Minimal requested count of items.
	 * @return Returns true, if object has at least minSize of items.
	 */
	/*public static boolean hasMinSize(IntIterator iter, int minSize) {
			return hasMinSize(new IntIteratorWrapper(iter), minSize);
	}*/
}
