package ieg.util.lang;

import java.util.Iterator;

/**
 * Convenience class to use for each with legacy code (e.g., prefuse).
 * <p>
 * <i>Warning:</i> An object of this class can only be used once, because it
 * always returns the same {@link Iterator} object.
 * 
 * @author Rind
 */
@SuppressWarnings("rawtypes")
public class CustomIterable implements Iterable {

    private Iterator iterator;

    public CustomIterable(Iterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator iterator() {
        return iterator;
    }
}
