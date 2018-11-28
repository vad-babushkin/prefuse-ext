package com.caffetools.vizualization;

import com.caffetools.javacc.Token;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Caffe block
 */
public class CaffeBlock {
    public static final Log log = LogFactory.getLog(CaffeBlock.class);

    /** Label data field */
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";

    private String blockKind;
    private Multimap<String, Object> properties ;

    public CaffeBlock(String kind) {
        this.blockKind = kind;
        this.properties = ArrayListMultimap.create();
    }

    public String getBlockKind() {
        return blockKind;
    }

    public void setBlockKind(String blockKind) {
        this.blockKind = blockKind;
    }

    public Multimap<String, Object> getProperties() {
        return properties;
    }

    public void add(Token t1, Token t2) {
        add(t1.image, t2.image);
    }

    public void add(String t1, Object t2){
        properties.put(unquote(t1), unquote(t2));
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(blockKind);
        toStringBuilder(b, 0);
        return b.toString();
    }

    public void toStringBuilder(StringBuilder b, int pad) {
        b.append(" {\n");
        properties.entries().stream()
            .forEach(entry -> {
                b.append(StringUtils.repeat("\t", pad)).append(entry.getKey());
                if (entry.getValue() instanceof String) {
                    b.append("\t: ").append(entry.getValue()).append("\n");
                } else if (entry.getValue() instanceof CaffeBlock) {
                    ((CaffeBlock) entry.getValue()).toStringBuilder(b, pad + 1);
                }
            });

        b.append(StringUtils.repeat("\t", pad)).append("}\n");

    }

    public String toHtml(){
        StringBuilder b = new StringBuilder("<br>");
        b.append(blockKind).append("</b>");
        toHtmlStringBuilder(b);
        return b.toString();
    }

    public void toHtmlStringBuilder(StringBuilder b) {
        b.append(" {\n<div style=\"padding-left:10px\">");
        properties.entries().stream()
            .forEach(entry ->{
                b.append("<div style=\"padding-left:10px\">").append("<b>").append(entry.getKey()).append("</b>");
                if(entry.getValue() instanceof String){
                    b.append(" : ").append(entry.getValue()).append("</div>\n");
                }
                else if(entry.getValue() instanceof CaffeBlock){
                    ((CaffeBlock)entry.getValue()).toHtmlStringBuilder(b);
                    b.append("</div>");
                }
            });

        b.append("</div>}\n");

    }

    public Collection<String> getPropertyStrings(String s) {
        List<String> l = new ArrayList<>();
        properties.get(s).stream().forEach(entry ->  {
            l.add((String) entry);
        });
        if(l.size()==0){
            log.info("Property '" + s + "' is null.");
            return Collections.emptyList();
        }
        return l;
    }

    public Collection<Object> getPropertyValues(String s) {
        return properties.get(s);
    }

    public String getName(){
        return getFirstValue(NAME);
    }

    public String getType(){
        return getFirstValue(TYPE);
    }

    public String getFirstValue(String name){
        final Collection<Object> names = properties.get(name);
        if(names.size()>0)
            return (String) names.iterator().next();
        else
            return null;
    }

    public static <T> T unquote(T s){
        if(s instanceof String) {
            String trim = ((String)s).trim();
            if (trim.charAt(0) == '"')
                trim = trim.substring(1);
            if (trim.charAt(trim.length() - 1) == '"')
                trim = trim.substring(0, trim.length() - 1);
            return (T)trim;
        }
        else
            return s;
    }


}
