package edu.berkeley.guir.prefuse.graph.external;

import edu.berkeley.guir.prefuse.ItemRegistry;

import java.io.File;
import java.io.IOException;

public class FileSystemLoader
		extends GraphLoader {
	public FileSystemLoader(ItemRegistry paramItemRegistry) {
		super(paramItemRegistry, "filename");
	}

	protected void getNeighbors(ExternalNode paramExternalNode) {
		String str = paramExternalNode.getAttribute("filename");
		File localFile1 = new File(str);
		File localFile2 = localFile1.getParentFile();
		if (localFile2 != null) {
			loadNode(0, paramExternalNode, localFile2);
		}
		File[] arrayOfFile = localFile1.listFiles();
		if (arrayOfFile == null) {
			return;
		}
		for (int i = 0; i < arrayOfFile.length; i++) {
			loadNode(0, paramExternalNode, arrayOfFile[i]);
		}
	}

	protected void getChildren(ExternalTreeNode paramExternalTreeNode) {
		String str = paramExternalTreeNode.getAttribute("filename");
		File localFile = new File(str);
		File[] arrayOfFile = localFile.listFiles();
		if (arrayOfFile == null) {
			return;
		}
		for (int i = 0; i < arrayOfFile.length; i++) {
			loadNode(1, paramExternalTreeNode, arrayOfFile[i]);
		}
	}

	protected void getParent(ExternalTreeNode paramExternalTreeNode) {
		String str = paramExternalTreeNode.getAttribute("filename");
		File localFile1 = new File(str);
		File localFile2 = localFile1.getParentFile();
		if (localFile2 != null) {
			loadNode(2, paramExternalTreeNode, localFile2);
		}
	}

	public ExternalEntity loadNode(int paramInt, ExternalEntity paramExternalEntity, File paramFile) {
		Object localObject = null;
		try {
			paramFile = paramFile.getCanonicalFile();
			String str1 = paramFile.getName();
			if (this.m_cache.containsKey(str1)) {
				localObject = (ExternalEntity) this.m_cache.get(str1);
			} else {
				if (paramInt == 0) {
					localObject = new ExternalNode();
				} else {
					localObject = new ExternalTreeNode();
				}
				String str2 = paramFile.getName();
				((ExternalEntity) localObject).setAttribute("label", str2.equals("") ? paramFile.getPath() : str2);
				((ExternalEntity) localObject).setAttribute("filename", paramFile.getPath());
				((ExternalEntity) localObject).setAttribute("size", String.valueOf(paramFile.length()));
				((ExternalEntity) localObject).setAttribute("modified", String.valueOf(paramFile.lastModified()));
			}
			foundNode(paramInt, paramExternalEntity, (ExternalEntity) localObject, null);
		} catch (IOException localIOException) {
			localIOException.printStackTrace();
		}
		return (ExternalEntity) localObject;
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/external/FileSystemLoader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */