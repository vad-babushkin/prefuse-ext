package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Data;

/**
 * Generic container's interface for SGVis data
 * This is an extension to Iterable interface.
 */
public interface IContainer<T>
	extends Iterable<T>
{
	/**
	 * Add item to container.
	 * @param t Item to add to container.
	 */
	public void add(T t);
}
