package ieg.prefuse.action.assignment;

import prefuse.action.assignment.ColorAction;
import prefuse.data.expression.Predicate;
import prefuse.visual.VisualItem;

/**
 * Sets the color based on the a field storing the indices of the palette array.
 * 
 * @author Atanasov and Schindler
 */
public class PaletteIndexColorAction extends ColorAction {

    /**
     * data field storing the index in the palette.
     */
    String paletteIndexField;

    /**
     * palette storing the color to be assigned.
     */
    int[] _palette;

    /**
     * @param group
     *            the aggregate group, e.g. GROUP_AGGREGATES
     * @param paletteIndexField
     *            the data column where the colorIndex is stored, e.g.
     *            COL_COLOR_INDEX
     * @param palette
     *            the color palette use for coloring, e.g.
     *            HorizonColorPalette.getColorPalette(...)
     */
    public PaletteIndexColorAction(String group, String paletteIndexField,
            String colorField, int[] palette) {
        super(group, colorField);
        this.paletteIndexField = paletteIndexField;
        this._palette = palette;
    }

    /**
     * @param group
     *            the aggregate group, e.g. GROUP_AGGREGATES
     * @param paletteIndexField
     *            the data column where the colorIndex is stored, e.g.
     *            COL_COLOR_INDEX
     * @param palette
     *            the color palette, e.g.
     *            HorizonColorPalette.getColorPalette(...)
     * @param filter
     *            the filter predicate
     */
    public PaletteIndexColorAction(String group, String paletteIndexField,
            String colorField, int[] palette, Predicate filter) {
        super(group, filter, colorField);
        this.paletteIndexField = paletteIndexField;
        this._palette = palette;
    }

    @Override
    public int getColor(VisualItem item) {
        int colorIndex = item.getInt(paletteIndexField);
        int color = _palette[colorIndex];
        return color;
    }

    /**
     * sets the color palette, e.g. HorizonColorPalette.getColorPalette(...)
     * call this after a setting change (if the user chooses a different
     * palette)
     */
    public void setPalette(int[] palette) {
        _palette = palette;
    }

}
