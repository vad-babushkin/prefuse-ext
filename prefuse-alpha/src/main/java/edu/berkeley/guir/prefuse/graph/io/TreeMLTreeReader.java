package edu.berkeley.guir.prefuse.graph.io;

import edu.berkeley.guir.prefuse.graph.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

public class TreeMLTreeReader
		extends AbstractTreeReader {
	public Tree loadTree(InputStream paramInputStream)
			throws IOException {
		try {
			TreeMLHandler localTreeMLHandler = new TreeMLHandler();
			SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
			SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
			localSAXParser.parse(paramInputStream, localTreeMLHandler);
			return localTreeMLHandler.getTree();
		} catch (SAXException localSAXException) {
			localSAXException.printStackTrace();
		} catch (ParserConfigurationException localParserConfigurationException) {
			localParserConfigurationException.printStackTrace();
		}
		return null;
	}

	public class TreeMLHandler
			extends DefaultHandler {
		public static final String TREE = "tree";
		public static final String BRANCH = "branch";
		public static final String LEAF = "leaf";
		public static final String ATTR = "attribute";
		public static final String NAME = "name";
		public static final String VALUE = "value";
		private Tree m_tree = null;
		private TreeNode m_root = null;
		private TreeNode m_activeNode = null;
		private boolean m_directed = false;
		private boolean inNode;
		private boolean inEdge;

		public TreeMLHandler() {
		}

		public void startDocument() {
			this.m_tree = null;
		}

		public void endDocument() {
			this.m_tree = new DefaultTree(this.m_root);
		}

		public void endElement(String paramString1, String paramString2, String paramString3) {
			if ((paramString3.equals("branch")) || (paramString3.equals("leaf"))) {
				this.m_activeNode = this.m_activeNode.getParent();
			}
		}

		public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
			if ((paramString3.equals("branch")) || (paramString3.equals("leaf"))) {
				DefaultTreeNode localDefaultTreeNode;
				if (this.m_activeNode == null) {
					localDefaultTreeNode = new DefaultTreeNode();
					this.m_root = localDefaultTreeNode;
				} else {
					localDefaultTreeNode = new DefaultTreeNode();
					this.m_activeNode.addChild(new DefaultEdge(this.m_activeNode, localDefaultTreeNode));
				}
				this.m_activeNode = localDefaultTreeNode;
			} else if (paramString3.equals("attribute")) {
				parseAttribute(paramAttributes);
			}
		}

		protected void parseAttribute(Attributes paramAttributes) {
			String str2 = null;
			String str3 = null;
			for (int i = 0; i < paramAttributes.getLength(); i++) {
				String str1 = paramAttributes.getQName(i);
				if (str1.equals("name")) {
					str2 = paramAttributes.getValue(i);
				} else if (str1.equals("value")) {
					str3 = paramAttributes.getValue(i);
				}
			}
			if ((str2 == null) || (str3 == null)) {
				System.err.println("Attribute under-specified");
				return;
			}
			this.m_activeNode.setAttribute(str2, str3);
		}

		public Tree getTree() {
			return this.m_tree;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/TreeMLTreeReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */