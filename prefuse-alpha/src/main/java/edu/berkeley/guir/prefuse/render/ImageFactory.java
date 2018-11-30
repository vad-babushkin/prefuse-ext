package edu.berkeley.guir.prefuse.render;

import edu.berkeley.guir.prefuse.VisualItem;
import edu.berkeley.guir.prefuse.graph.Entity;

import java.awt.*;
import java.net.URL;
import java.util.*;

public class ImageFactory {
	private int m_imageCacheSize = 500;
	private int m_maxImageWidth = 100;
	private int m_maxImageHeight = 100;
	private boolean m_asynch = true;
	private Map imageCache = new LinkedHashMap((int) (this.m_imageCacheSize + 1.3333334F), 0.75F, true) {
		public boolean removeEldestEntry(Map.Entry paramAnonymousEntry) {
			return size() > ImageFactory.this.m_imageCacheSize;
		}
	};
	private Map loadMap = new HashMap(50);
	private Map loadingMap = new HashMap(50);
	private Set loadingSet = new HashSet(50);
	private final Component component = new Component() {
	};
	private final MediaTracker tracker = new MediaTracker(this.component);
	private int nextTrackerID = 0;

	public ImageFactory() {
		this(-1, -1);
	}

	public ImageFactory(int paramInt1, int paramInt2) {
		setMaxImageDimensions(paramInt1, paramInt2);
	}

	public void setMaxImageDimensions(int paramInt1, int paramInt2) {
		this.m_maxImageWidth = paramInt1;
		this.m_maxImageHeight = paramInt2;
	}

	public void setImageCacheSize(int paramInt) {
		this.m_imageCacheSize = paramInt;
	}

	public Image getImage(String paramString) {
		Image localImage = (Image) this.imageCache.get(paramString);
		Object localObject;
		if ((localImage == null) && (!this.loadMap.containsKey(paramString))) {
			localObject = getImageURL(paramString);
			if (localObject == null) {
				System.err.println("Null image: " + paramString);
				return null;
			}
			localImage = Toolkit.getDefaultToolkit().createImage((URL) localObject);
			if (!this.m_asynch) {
				waitForImage(localImage);
				addImage(paramString, localImage);
			} else {
				int i = ++this.nextTrackerID;
				this.tracker.addImage(localImage, i);
				this.loadMap.put(paramString, new LoadMapEntry(i, localImage));
			}
		} else if ((localImage == null) && (this.loadMap.containsKey(paramString))) {
			localObject = (LoadMapEntry) this.loadMap.get(paramString);
			if (this.tracker.checkID(((LoadMapEntry) localObject).id, true)) {
				addImage(paramString, ((LoadMapEntry) localObject).image);
				this.loadMap.remove(paramString);
				this.tracker.removeImage(((LoadMapEntry) localObject).image, ((LoadMapEntry) localObject).id);
			}
		} else {
			return localImage;
		}
		return (Image) this.imageCache.get(paramString);
	}

	public Image addImage(String paramString, Image paramImage) {
		if ((this.m_maxImageWidth > -1) || (this.m_maxImageHeight > -1)) {
			paramImage = getScaledImage(paramImage);
			paramImage.getWidth(null);
		}
		this.imageCache.put(paramString, paramImage);
		return paramImage;
	}

	protected void waitForImage(Image paramImage) {
		int i = ++this.nextTrackerID;
		this.tracker.addImage(paramImage, i);
		try {
			this.tracker.waitForID(i, 0L);
		} catch (InterruptedException localInterruptedException) {
			localInterruptedException.printStackTrace();
		}
		this.tracker.removeImage(paramImage, i);
	}

	protected URL getImageURL(String paramString) {
		URL localURL = null;
		if ((paramString.startsWith("http:/")) || (paramString.startsWith("ftp:/")) || (paramString.startsWith("file:/"))) {
			try {
				localURL = new URL(paramString);
			} catch (Exception localException) {
				localException.printStackTrace();
			}
		} else {
			localURL = ImageFactory.class.getResource(paramString);
			if ((localURL == null) && (!paramString.startsWith("/"))) {
				localURL = ImageFactory.class.getResource("/" + paramString);
			}
		}
		return localURL;
	}

	protected Image getScaledImage(Image paramImage) {
		int i = paramImage.getWidth(null) - this.m_maxImageWidth;
		int j = paramImage.getHeight(null) - this.m_maxImageHeight;
		Image localImage;
		if ((i > j) && (i > 0) && (this.m_maxImageWidth > -1)) {
			localImage = paramImage.getScaledInstance(this.m_maxImageWidth, -1, 4);
			paramImage.flush();
			return localImage;
		}
		if ((j > 0) && (this.m_maxImageHeight > -1)) {
			localImage = paramImage.getScaledInstance(-1, this.m_maxImageHeight, 4);
			paramImage.flush();
			return localImage;
		}
		return paramImage;
	}

	public void preloadImages(Iterator paramIterator, String paramString) {
		boolean bool = this.m_asynch;
		this.m_asynch = false;
		String str = null;
		while ((paramIterator.hasNext()) && (this.imageCache.size() <= this.m_imageCacheSize)) {
			Object localObject = paramIterator.next();
			if ((localObject instanceof Entity)) {
				str = ((Entity) localObject).getAttribute(paramString);
			} else if ((localObject instanceof VisualItem)) {
				str = ((VisualItem) localObject).getAttribute(paramString);
			}
			if (str != null) {
				getImage(str);
			}
		}
		this.m_asynch = bool;
	}

	private class LoadMapEntry {
		public int id;
		public Image image;

		public LoadMapEntry(int paramInt, Image paramImage) {
			this.id = paramInt;
			this.image = paramImage;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/render/ImageFactory.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */