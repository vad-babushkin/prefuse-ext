package evaluation.evalBench.task;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.namespace.QName;

public class Configuration implements Cloneable {
    
    private Map<QName, String> extension;

    @XmlAnyAttribute
    public Map<QName, String> getAny() {
        return extension;
    }

    public void setAny(Map<QName, String> extension) {
        this.extension = extension;
    }
    
    @Override
    public Configuration clone() {
    	try {
    		Configuration clone = (Configuration) super.clone();
    		if (extension != null) {
		        clone.extension = new HashMap<QName, String>();    // assumption specific subclass of Map   
		        for (Entry<QName, String> pair : extension.entrySet()) {
		        	clone.extension.put(pair.getKey(), pair.getValue());
		        }
    		}
	    	return clone;
		} catch (CloneNotSupportedException e) {
            throw new Error("This should not occur since we implement Cloneable");
        }
    }
}
