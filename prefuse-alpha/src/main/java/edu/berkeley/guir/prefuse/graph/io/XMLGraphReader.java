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
import java.util.HashMap;

public class XMLGraphReader
		extends AbstractGraphReader
		implements GraphReader {
	protected Class NODE_TYPE = DefaultNode.class;

	public Graph loadGraph(InputStream paramInputStream)
			throws IOException {
		try {
			XMLGraphHandler localXMLGraphHandler = new XMLGraphHandler();
			SAXParserFactory localSAXParserFactory = SAXParserFactory.newInstance();
			SAXParser localSAXParser = localSAXParserFactory.newSAXParser();
			localSAXParser.parse(paramInputStream, localXMLGraphHandler);
			return localXMLGraphHandler.getGraph();
		} catch (SAXException localSAXException) {
			localSAXException.printStackTrace();
		} catch (ParserConfigurationException localParserConfigurationException) {
			localParserConfigurationException.printStackTrace();
		}
		return null;
	}

	public Class getNodeType() {
		return this.NODE_TYPE;
	}

	public void setNodeType(Class paramClass) {
		this.NODE_TYPE = paramClass;
	}

	public class XMLGraphHandler
			extends DefaultHandler {
		public static final String NODE = "node";
		public static final String EDGE = "edge";
		public static final String ATT = "att";
		public static final String ID = "id";
		public static final String LABEL = "label";
		public static final String SOURCE = "source";
		public static final String TARGET = "target";
		public static final String TYPE = "type";
		public static final String NAME = "name";
		public static final String VALUE = "value";
		public static final String LIST = "list";
		public static final String WEIGHT = "weight";
		public static final String GRAPH = "graph";
		public static final String DIRECTED = "directed";
		protected Graph m_graph = null;
		protected HashMap m_nodeMap = new HashMap();
		private Node m_activeNode = null;
		private Edge m_activeEdge = null;
		private boolean m_directed = false;
		private boolean inNode;
		private boolean inEdge;

		public XMLGraphHandler() {
		}

		public void startDocument() {
			this.m_nodeMap.clear();
		}

		public void endElement(String paramString1, String paramString2, String paramString3) {
			if (paramString3.equals("node")) {
				this.m_activeNode = null;
				this.inNode = false;
			} else if (paramString3.equals("edge")) {
				this.m_activeEdge = null;
				this.inEdge = false;
			}
		}

		public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) {
			if (paramString3.equals("graph")) {
				int i = paramAttributes.getIndex("directed");
				this.m_directed = ((i >= 0) && (paramAttributes.getValue(i).equals("1")));
				this.m_graph = new DefaultGraph(this.m_directed);
			} else {
				Object localObject;
				if (paramString3.equals("node")) {
					localObject = parseNode(paramAttributes);
					this.m_activeNode = ((Node) localObject);
					this.inNode = true;
				} else if (paramString3.equals("edge")) {
					localObject = parseEdge(paramAttributes);
					this.m_activeEdge = ((Edge) localObject);
					this.inEdge = true;
				} else if (paramString3.equals("att")) {
					parseAttribute(paramAttributes);
				}
			}
		}

		protected Node parseNode(Attributes paramAttributes) {
			String str2 = null;
			for (int i = 0; i < paramAttributes.getLength(); i++) {
				if (paramAttributes.getQName(i).equals("id")) {
					str2 = paramAttributes.getValue(i);
				}
			}
			if (str2 == null) {
				System.err.println("Node missing id");
				return null;
			}
			Node localNode = null;
			try {
				localNode = (Node) XMLGraphReader.this.NODE_TYPE.newInstance();
			} catch (Exception localException) {
				throw new RuntimeException(localException);
			}
			localNode.setAttribute("id", str2);
			this.m_nodeMap.put(str2, localNode);
			for (int j = 0; j < paramAttributes.getLength(); j++) {
				String str1 = paramAttributes.getQName(j);
				if (!str1.equals("id")) {
					localNode.setAttribute(str1, paramAttributes.getValue(j));
				}
			}
			this.m_graph.addNode(localNode);
			return localNode;
		}

		protected Edge parseEdge(Attributes paramAttributes) {
			String str2 = null;
			String str3 = null;
			String str4 = null;
			String str5 = null;
			for (int i = 0; i < paramAttributes.getLength(); i++) {
				String str1 = paramAttributes.getQName(i);
				if (str1.equals("source")) {
					str2 = paramAttributes.getValue(i);
				} else if (str1.equals("target")) {
					str3 = paramAttributes.getValue(i);
				} else if (str1.equals("label")) {
					str4 = paramAttributes.getValue(i);
				} else if (str1.equals("weight")) {
					str5 = paramAttributes.getValue(i);
				}
			}
			Node localNode1 = (Node) this.m_nodeMap.get(str2);
			Node localNode2 = (Node) this.m_nodeMap.get(str3);
			if ((str2 == null) || (str3 == null) || (localNode1 == null) || (localNode2 == null)) {
				System.err.println("Edge missing source or target");
				return null;
			}
			DefaultEdge localDefaultEdge = new DefaultEdge(localNode1, localNode2, this.m_directed);
			if (str4 != null) {
				localDefaultEdge.setAttribute("label", str4);
			}
			if (str5 != null) {
				localDefaultEdge.setAttribute("weight", str5);
			}
			this.m_graph.addEdge(localDefaultEdge);
			return localDefaultEdge;
		}

		protected void parseAttribute(Attributes paramAttributes) {
			String str2 = null;
			String str3 = null;
			String str4 = null;
			for (int i = 0; i < paramAttributes.getLength(); i++) {
				String str1 = paramAttributes.getQName(i);
				if (str1.equals("type")) {
					str2 = paramAttributes.getValue(i);
				} else if (str1.equals("name")) {
					str3 = paramAttributes.getValue(i);
				} else if (str1.equals("value")) {
					str4 = paramAttributes.getValue(i);
				}
			}
			if (((str2 != null) && (str2.equals("list"))) || (str3 == null) || (str4 == null)) {
				System.err.println("Attribute under-specified");
				return;
			}
			if (this.inNode) {
				this.m_activeNode.setAttribute(str3, str4);
			} else if (this.inEdge) {
				this.m_activeEdge.setAttribute(str3, str4);
			}
		}

		public Graph getGraph() {
			return this.m_graph;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/graph/io/XMLGraphReader.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */