package ieg.util.xml;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Four adapters to serialize colors with JAXB.
 * 
 * <li> {@link FieldsAdapter} and {@link IntegerFieldsAdapter} serialize color as
 * three XML attributes (red, green, blue) 
 * <li> {@link StringAdapter} and {@link IntegerStringAdapter} serialize color as 
 * a hex encoded string (#rrggbb) 
 * <li> {@link FieldsAdapter} and {@link StringAdapter} work with
 * {@link java.awt.Color} objects 
 * <li> {@link IntegerFieldsAdapter} and {@link IntegerStringAdapter} work with 
 * colors saved as int (e.g., in prefuse)
 * 
 * @author Alex Rind
 * */
public class ColorAdapters {

    /**
     * color model that can be serialized with JAXB.
     * 
     * @author Alex Rind
     */
    static class ColorModel {
        @XmlAttribute(required = true)
        int red, green, blue;
        @XmlAttribute(required = false)
        int alpha = 255;
    }

    /**
     * converts a {@link java.awt.Color} object to a color model that can be
     * serialized with JAXB.
     * 
     * @author Alex Rind
     */
    public static class FieldsAdapter extends XmlAdapter<ColorModel, Color> {

        @Override
        public ColorModel marshal(Color v) throws Exception {
            ColorModel m = new ColorModel();
            m.alpha = v.getAlpha();
            m.red = v.getRed();
            m.green = v.getGreen();
            m.blue = v.getBlue();
            return m;
        }

        @Override
        public Color unmarshal(ColorModel v) throws Exception {
            return new Color(v.red, v.green, v.blue, v.alpha);
        }
    }

    /**
     * converts a prefuse color to a color model that can be serialized with
     * JAXB.
     * 
     * @author Alex Rind
     */
    public static class IntegerFieldsAdapter extends
            XmlAdapter<ColorModel, Integer> {

        @Override
        public ColorModel marshal(Integer v) throws Exception {
            ColorModel m = new ColorModel();
            m.alpha = (v >> 24) & 0xFF;
            m.red = (v >> 16) & 0xFF;
            m.green = (v >> 8) & 0xFF;
            m.blue = v & 0xFF;
            return m;
        }

        @Override
        public Integer unmarshal(ColorModel v) throws Exception {
            return new Integer(((v.alpha & 0xFF) << 24)
                    | ((v.red & 0xFF) << 16) | ((v.green & 0xFF) << 8)
                    | ((v.blue & 0xFF) << 0));
        }
    }

    /**
     * serializes a java.awt.Color object to a string with its RGB hex value
     * (e.g., #ff0000).
     * 
     * @author Alex Rind
     */
    public static class StringAdapter extends XmlAdapter<String, Color> {

        private String hex(int v) {
            if (v > 0xF)
                return Integer.toString(v, 16);
            else
                return "0" + Integer.toString(v, 16);
        }

        @Override
        public String marshal(Color v) throws Exception {
            return "#" + hex(v.getRed()) + hex(v.getGreen()) + hex(v.getBlue());
        }

        @Override
        public Color unmarshal(String v) throws Exception {
            return Color.decode(v);
        }
    }

    /**
     * serializes a prefuse color to a string with its RGB hex value (e.g.,
     * #ff0000).
     * 
     * @author Alex Rind
     */
    public static class IntegerStringAdapter extends XmlAdapter<String, Integer> {

        @Override
        public String marshal(Integer v) throws Exception {
            // (1) filter all bytes that do not encode red, green, blue
            // (2) add a bit in front to ensure all leading zeros are created
            // (3) convert to hex string 
            // (4) remove leading 1
            // (5) add a leading "#"
            return "#"
                    + Integer.toString((v & 0xFFFFFF) | 0x1000000, 16)
                            .substring(1);
        }

        @Override
        public Integer unmarshal(String v) throws Exception {
            // (1) remove leading "#"
            // (2) convert from hex string
            // (3) add alpha value of 255 (full opacity)
            return Integer.valueOf(v.substring(1), 16) | 0xFF000000;
        }
    }
}
