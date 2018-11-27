package ieg.util.xml;

import java.util.*;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * generic mapping of a HashMap for JAXB (experimental).
 * <p>
 * <b>Disclaimer:</b> This is as general as I managed to write this class, however:
 * <li>
 * It will probably not work with keys or values 
 * that are supported by JAXB out of the box (e.g., {@link java.awt.Color}).
 * <li> 
 * Generated XML declares xs and xsi namespaces multiple times, 
 * though it is enough to declare them once in the root element.     
 * <p>
 * A workaround is to use this class as a template for an adapter 
 * and a marshalled form without generics (e.g., 
 * {@link visuexplore.persistence.CategoricalPaletteXmlAdapter})
 * 
 * @author Alex Rind
 * 
 * @param <K> key 
 * @param <V> value
 */
public class MapAdapters {
    
    /*
     * insert subclasses if needed; the subclasses are not actually necessary --
     * JaxbDemo moreData also works with the generic class
     */
    public static class StringStringLinkedHashMapXmlAdapter extends
            LinkedHashMapXmlAdapter<String, String> {
    }

    public static class IntegerStringLinkedHashMapXmlAdapter extends
            LinkedHashMapXmlAdapter<Integer, String> {
    }

    public static class LinkedHashMapXmlAdapter<K, V> extends
            XmlAdapter<MapAdapters.MarshalledForm<K, V>, LinkedHashMap<K, V>> {

        @Override
        public MarshalledForm<K, V> marshal(LinkedHashMap<K, V> v)
                throws Exception {
            MarshalledForm<K, V> result = new MarshalledForm<K, V>();

            for (Map.Entry<K, V> var : v.entrySet()) {
                result.entries.add(new KeyValuePair<K, V>(var.getKey(), var
                        .getValue()));
            }

            return result;
        }

        @Override
        public LinkedHashMap<K, V> unmarshal(MarshalledForm<K, V> v)
                throws Exception {
            LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();

            for (KeyValuePair<K, V> var : v.entries) {
                result.put(var.key, var.value);
            }

            return result;
        }
    }

    static class MarshalledForm<K, V> {
        @XmlElement(name = "option")
        LinkedList<KeyValuePair<K, V>> entries = new LinkedList<KeyValuePair<K, V>>();
    }

    static class KeyValuePair<K, V> {
        @XmlElement
        // @XmlAttribute(required = true)
        K key;

        @XmlElement
        // @XmlValue
        V value;

        public KeyValuePair() {
        }

        KeyValuePair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
}
