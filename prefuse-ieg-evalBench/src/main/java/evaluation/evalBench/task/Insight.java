package evaluation.evalBench.task;

import java.util.Map.Entry;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Deprecated
public class Insight {

    @XmlElement
    private String text;

    private LinkedHashMap<String, Integer> characteristics = new LinkedHashMap<String, Integer>();

    // alternative: only store numbers and depend on InsightTask for meaning
    // private int[] characteristics;

    // TODO time is redundant (also in Task)
    private long timeMillis;

    // TODO application state
    // TODO screenshot

    @SuppressWarnings("unused")
    private Insight() {
    }

    public Insight(String text) {
        this.timeMillis = System.currentTimeMillis();
        this.text = text;
    }

    @XmlTransient
    public String getText() {
        return text;
    }

    public LinkedHashMap<String, Integer> getCharacteristicsValues() {
        return characteristics;
    }

    public void setCharacteristicsValues(LinkedHashMap<String, Integer> characteristicsValues) {
        characteristics = characteristicsValues;
    }

    public void addCharacteristicsValue(String key, Integer value) {
        this.characteristics.put(key, value);
    }

    @XmlTransient
    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(text);
        for (Entry<String, Integer> e : characteristics.entrySet()) {
            str.append(", ");
            str.append(e.getKey());
            str.append("=");
            str.append(e.getValue());
        }
        return str.toString();
    }
}
