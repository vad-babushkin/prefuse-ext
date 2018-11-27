package ieg.test.prefuse.data;

import prefuse.data.Table;
import prefuse.data.column.Column;
import prefuse.data.event.ColumnListener;

/**
 * keeps track of maximum and minimum values in a column.
 * 
 * XXX Experimental code!
 * 
 * <p><i>Note:</i> the case that an extreme value is removed is not handled 
 * 
 * <p><i>Note:</i> only supports LongColumn and IntColumn  
 * 
 * <p><i>Alternative implementation:</i> index
 * 
 * @author Rind
 */
public class ExtremeValueListener implements ColumnListener {
    
    private long maximum = Long.MIN_VALUE;
    private long minimum = Long.MAX_VALUE;

    public ExtremeValueListener() {
        // do nothing
    }

    public ExtremeValueListener(Table table, String colName) {
        table.getColumn(colName).addColumnListener(this);
    }
    
    public long getMaximum() {
        return maximum;
    }

    public long getMinimum() {
        return minimum;
    }

    @Override
    public void columnChanged(Column src, int type, int start, int end) {
        // should never be called
        throw new IllegalStateException();
    }

    @Override
    public void columnChanged(Column src, int idx, int prev) {
        columnChanged(src, idx, (long) prev);
    }

    @Override
    public void columnChanged(Column src, int idx, long prev) {
        long value = src.getLong(idx); 
        if (value > this.maximum)
            this.maximum = value;
        if (value < this.minimum)
            this.minimum = value;
    }

    @Override
    public void columnChanged(Column src, int idx, float prev) {
        // should never be called
        throw new IllegalStateException();
    }

    @Override
    public void columnChanged(Column src, int idx, double prev) {
        // should never be called
        throw new IllegalStateException();
    }

    @Override
    public void columnChanged(Column src, int idx, boolean prev) {
        // should never be called
        throw new IllegalStateException();
    }

    @Override
    public void columnChanged(Column src, int idx, Object prev) {
        // should never be called
        throw new IllegalStateException();
    }
}
