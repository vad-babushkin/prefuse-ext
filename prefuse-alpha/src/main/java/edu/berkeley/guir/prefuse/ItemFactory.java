package edu.berkeley.guir.prefuse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ItemFactory {
	private static final Class LIST_TYPE = LinkedList.class;
	private static final Class MAP_TYPE = HashMap.class;
	private Map m_entryMap;

	public ItemFactory() {
		try {
			this.m_entryMap = ((Map) MAP_TYPE.newInstance());
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public void addItemClass(String paramString, Class paramClass, int paramInt) {
		FactoryEntry localFactoryEntry = new FactoryEntry(paramString, paramClass, paramInt);
		this.m_entryMap.put(paramString, localFactoryEntry);
	}

	public VisualItem getItem(String paramString) {
		FactoryEntry localFactoryEntry = (FactoryEntry) this.m_entryMap.get(paramString);
		if (localFactoryEntry != null) {
			VisualItem localVisualItem = null;
			if (localFactoryEntry.itemList.isEmpty()) {
				try {
					localVisualItem = (VisualItem) localFactoryEntry.type.newInstance();
				} catch (Exception localException) {
					localException.printStackTrace();
				}
			} else {
				localVisualItem = (VisualItem) localFactoryEntry.itemList.remove(0);
			}
			return localVisualItem;
		}
		throw new IllegalArgumentException("The input string must be a recognized item class!");
	}

	public void reclaim(VisualItem paramVisualItem) {
		String str = paramVisualItem.getItemClass();
		FactoryEntry localFactoryEntry = (FactoryEntry) this.m_entryMap.get(str);
		paramVisualItem.clear();
		if (localFactoryEntry.itemList.size() <= localFactoryEntry.maxItems) {
			localFactoryEntry.itemList.add(paramVisualItem);
		}
	}

	private class FactoryEntry {
		int maxItems;
		Class type;
		String name;
		List itemList;

		FactoryEntry(String paramString, Class paramClass, int paramInt) {
			try {
				this.maxItems = paramInt;
				this.name = paramString;
				this.type = paramClass;
				this.itemList = ((List) ItemFactory.LIST_TYPE.newInstance());
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/ItemFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */