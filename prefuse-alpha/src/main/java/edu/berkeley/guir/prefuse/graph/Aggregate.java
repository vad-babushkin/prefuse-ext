package edu.berkeley.guir.prefuse.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Aggregate
		extends DefaultEntity {
	protected static final Class LIST_TYPE = ArrayList.class;
	protected List m_entities;

	public Aggregate() {
		try {
			this.m_entities = ((List) LIST_TYPE.newInstance());
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public Iterator getAggregateEntities() {
		return this.m_entities.iterator();
	}

	public boolean isAggregateEntity(Entity paramEntity) {
		return this.m_entities.indexOf(paramEntity) > -1;
	}

	public int getNumAggregateEntities() {
		return this.m_entities.size();
	}

	public void addAggregateEntity(Entity paramEntity) {
		if (isAggregateEntity(paramEntity)) {
			throw new IllegalStateException("Entity already contained in aggregate!");
		}
		this.m_entities.add(paramEntity);
	}

	public boolean removeAggregateEntity(Entity paramEntity) {
		return this.m_entities.remove(paramEntity);
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/Aggregate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */