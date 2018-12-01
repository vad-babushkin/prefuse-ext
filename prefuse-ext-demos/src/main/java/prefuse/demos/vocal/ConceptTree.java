//package prefuse.demos.vocal;
//
//import java.util.Collection;
//import java.util.Iterator;
//
//import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
//import edu.stanford.smi.protegex.owl.model.OWLCardinality;
//import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
//import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
//import edu.stanford.smi.protegex.owl.model.OWLHasValue;
//import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
//import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
//import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
//import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
//import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
//import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
//import edu.stanford.smi.protegex.owl.model.RDFProperty;
//import edu.stanford.smi.protegex.owl.model.RDFResource;
//import prefuse.data.Node;
//import prefuse.data.Tree;
//
///**
// *
// * Prefuse Tree representing a concept expression.
// *
// * @author <code>fnaufel@gmail.com</code>
// * @version 2007-12-02
// *
// */
//public class ConceptTree extends Tree implements VocalConstants {
//
//	/**
//	 * Concept expression which originated this ConceptTree object
//	 */
//	RDFResource originalResource;
//
//
//	public ConceptTree( RDFResource res ) {
//
//		Node conceptRoot;
//
//		originalResource = res;
//		defineSchema();
//		conceptRoot = addRoot();
//		buildTree( res, conceptRoot );
//
//	}
//
//	/**
//	 * Recursive method to transform an RDFResource into a ConceptTree.
//	 *
//	 * TODO:
//	 *
//	 * <ul>
//	 * 	<li>How to represent RDFSDataTypes??</li>
//	 * </ul>
//	 *
//	 * @param res The resource to be transformed into a (sub)tree
//	 * @param node The root node of the (sub)tree to be generated.
//	 * 			Note that this node has all attribute values initialized to <code>""</code> by this method.
//	 */
//	protected void buildTree( RDFResource res, Node node ) {
//
//		initNode( node );
//
//		if( res instanceof OWLNamedClass ) {
//			node.set( "contents", "namedClass" );
//			node.set( "conceptNames", getResourceName( res ) );
//		}
//		else if( res instanceof OWLComplementClass ) {
//			node.set( "contents", "not" );
//			OWLComplementClass complClass = (OWLComplementClass) res;
//			RDFResource compl = complClass.getComplement();
//
//			if( compl instanceof OWLNamedClass ) {
//				node.set( "connection", "embedded" );
//				node.set( "conceptNames", getResourceName( compl ) );
//			}
//			else {
//				node.set( "connection", "edges" );
//				Node child = addChild( node );
//				buildTree( compl, child );
//			}
//		}
//		else if( res instanceof OWLUnionClass ) {
//			node.set( "contents", "or" );
//			Collection ops = ((OWLUnionClass) res).getOperands();
//			processNAryClassOperands( ops, node );
//		}
//		else if( res instanceof OWLIntersectionClass ) {
//			node.set( "contents", "and" );
//			Collection ops = ((OWLIntersectionClass) res).getOperands();
//			processNAryClassOperands( ops, node );
//		}
//		else if( res instanceof OWLAllValuesFrom ) {
//			node.set( "contents", "only" );
//			node.set( "connection", "edges" );
//			OWLAllValuesFrom allRestr = (OWLAllValuesFrom) res;
//
//			RDFProperty prop = allRestr.getOnProperty();
//			node.set( "roleNames", getResourceName( prop ) );
//
//			Node child = addChild( node );
//			RDFResource filler = allRestr.getFiller();
//			buildTree( filler, child );
//		}
//		else if( res instanceof OWLSomeValuesFrom ) {
//			node.set( "contents", "some" );
//			node.set( "connection", "edges" );
//			OWLSomeValuesFrom someRestr = (OWLSomeValuesFrom) res;
//
//			RDFProperty prop = someRestr.getOnProperty();
//			node.set( "roleNames", getResourceName( prop ) );
//
//			Node child = addChild( node );
//			RDFResource filler = someRestr.getFiller();
//			buildTree( filler, child );
//		}
//		else if( res instanceof OWLMaxCardinality ) {
//			node.set( "contents", "max" );
//			node.set( "connection", "embedded" ); // Or should this be ""?
//			OWLMaxCardinality maxRestr = (OWLMaxCardinality) res;
//
//			RDFProperty prop = maxRestr.getOnProperty();
//			node.set( "roleNames", getResourceName( prop ) );
//
//			int n = maxRestr.getCardinality();
//			node.set( "numbers", String.valueOf( n ) );
//		}
//		else if( res instanceof OWLMinCardinality ) {
//			node.set( "contents", "min" );
//			node.set( "connection", "embedded" ); // Or should this be ""?
//			OWLMinCardinality minRestr = (OWLMinCardinality) res;
//
//			RDFProperty prop = minRestr.getOnProperty();
//			node.set( "roleNames", getResourceName( prop ) );
//
//			int n = minRestr.getCardinality();
//			node.set( "numbers", String.valueOf( n ) );
//		}
//		else if( res instanceof OWLCardinality ) {
//			node.set( "contents", "exactly" );
//			node.set( "connection", "embedded" ); // Or should this be ""?
//			OWLCardinality cardRestr = (OWLCardinality) res;
//
//			RDFProperty prop = cardRestr.getOnProperty();
//			node.set( "roleNames", getResourceName( prop ) );
//
//			int n = cardRestr.getCardinality();
//			node.set( "numbers", String.valueOf( n ) );
//		}
//		else if( res instanceof OWLHasValue ) {
//			node.set( "contents", "contains" );
//			node.set( "connection", "embedded" ); // Or should this be ""?
//			OWLHasValue hv = (OWLHasValue) res;
//			RDFResource r = (RDFResource) hv.getHasValue();
//			node.set( "individualNames", getResourceName( r ) );
//			// TODO: Now this can be either an RDFResource, an RDFSLiteral or a primitive value.
//			// I am only treating RDFResource for now.
//		}
//		else if( res instanceof OWLEnumeratedClass ) {
//			node.set( "contents", "oneOf" );
//			node.set( "connection", "embedded" ); // Or should this be ""?
//			OWLEnumeratedClass enumRestr = (OWLEnumeratedClass) res;
//
//			String names = "";
//			for( Iterator i = enumRestr.listOneOf(); i.hasNext(); ) {
//				RDFResource r = (RDFResource) i.next();
//				names += getResourceName( r ) + ( i.hasNext()? ITEM_SEPARATOR : "" );
//			}
//			node.set( "individualNames", names );
//		}
//
//	}
//
//	protected void processNAryClassOperands( Collection ops, Node node ) {
//
//		RDFResource op = null;
//		boolean allLiterals = true;
//
//		for( Iterator i = ops.iterator(); i.hasNext(); ) {
//			op = (RDFResource) (i.next());
//			if( ! isLiteral( op ) ) {
//				allLiterals = false;
//				break;
//			}
//		}
//
//		if( allLiterals ) {
//			String opsStr = "";
//			for( Iterator i = ops.iterator(); i.hasNext(); ) {
//				op = (RDFResource) (i.next());
//				if( op instanceof OWLNamedClass )
//					opsStr += getResourceName( op ) + ( i.hasNext()? ITEM_SEPARATOR : "" );
//				else {
//					OWLComplementClass compl = (OWLComplementClass) op;
//					opsStr += "NOT " + getResourceName( compl.getComplement() ) + ( i.hasNext()? ITEM_SEPARATOR : "" );
//				}
//			}
//
//			node.set( "connection", "embedded" );
//			node.set( "conceptNames", opsStr );
//		}
//		else {
//			node.set( "connection", "edges" );
//			for( Iterator i = ops.iterator(); i.hasNext(); ) {
//				op = (RDFResource) (i.next());
//				Node child = addChild( node );
//				buildTree( op, child );
//			}
//		}
//
//	}
//
//	protected boolean isLiteral( RDFResource op ) {
//
//		if( op instanceof OWLNamedClass )
//			return true;
//
//		if( op instanceof OWLComplementClass && ((OWLComplementClass) op).getComplement() instanceof OWLNamedClass )
//			return true;
//
//		return false;
//
//	}
//
//	protected String getResourceName( RDFResource res ) {
//
//		return res.getName();
//
//	}
//
//	protected void initNode( Node n ) {
//
//		n.set( "contents", "" );
//		n.set( "connection", "" );
//		n.set( "conceptNames", "" );
//		n.set( "roleNames", "" );
//		n.set( "numbers", "" );
//		n.set( "individualNames", "" );
//
//	}
//
//	protected void defineSchema() {
//
//		try {
//			addColumn( "contents", java.lang.Class.forName( "java.lang.String" ) );
//			addColumn( "connection", java.lang.Class.forName( "java.lang.String" ) );
//			addColumn( "conceptNames", java.lang.Class.forName( "java.lang.String" ) );
//			addColumn( "roleNames", java.lang.Class.forName( "java.lang.String" ) );
//			addColumn( "numbers", java.lang.Class.forName( "java.lang.String" ) );
//			addColumn( "individualNames", java.lang.Class.forName( "java.lang.String" ) );
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//}
