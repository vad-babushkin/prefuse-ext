package edu.berkeley.guir.prefuse.graph;

import java.util.Map;

public abstract interface Entity {
	public abstract String getAttribute(String paramString);

	public abstract Map getAttributes();

	public abstract void setAttribute(String paramString1, String paramString2);

	public abstract void setAttributes(Map paramMap);

	public abstract void clearAttributes();
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/Entity.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */