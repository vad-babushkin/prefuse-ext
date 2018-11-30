package edu.berkeley.guir.prefuse.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultEntity
		implements Entity {
	protected Map m_attributes;

	public String getAttribute(String paramString) {
		if (this.m_attributes == null) {
			return null;
		}
		return (String) this.m_attributes.get(paramString);
	}

	public Map getAttributes() {
		if (this.m_attributes == null) {
			return Collections.EMPTY_MAP;
		}
		return this.m_attributes;
	}

	public void setAttribute(String paramString1, String paramString2) {
		if (this.m_attributes == null) {
			this.m_attributes = new HashMap(3, 0.9F);
		}
		this.m_attributes.put(paramString1, paramString2);
	}

	public void setAttributes(Map paramMap) {
		Iterator localIterator = paramMap.keySet().iterator();
		while (localIterator.hasNext()) {
			Object localObject1 = localIterator.next();
			Object localObject2 = paramMap.get(localObject1);
			if ((!(localObject1 instanceof String)) || (!(localObject2 instanceof String))) {
				throw new IllegalArgumentException("Non-string value contained in attribute map");
			}
		}
		this.m_attributes = paramMap;
	}

	public void clearAttributes() {
		if (this.m_attributes != null) {
			this.m_attributes.clear();
		}
	}

	public String toString() {
		return "Entity[" + getAttributeString() + "]";
	}

	protected String getAttributeString() {
		StringBuffer localStringBuffer = new StringBuffer();
		Iterator localIterator = this.m_attributes.keySet().iterator();
		while (localIterator.hasNext()) {
			String str1 = (String) localIterator.next();
			String str2 = (String) this.m_attributes.get(str1);
			localStringBuffer.append(str1).append('=').append(str2);
			if (localIterator.hasNext()) {
				localStringBuffer.append(',');
			}
		}
		return localStringBuffer.toString();
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/DefaultEntity.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */