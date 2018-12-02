package vocal;

import prefuse.data.io.DataIOException;
import prefuse.data.io.TreeMLWriter;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;

public class ConceptTreeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		OWLModel owlModel = ProtegeOWL.createJenaOWLModel();
		owlModel.getNamespaceManager().setDefaultNamespace( "http://www.ic.uff.br/~fnaufel/ontology#" );

		OWLNamedClass A = owlModel.createOWLNamedClass( "A" );
		owlModel.createOWLNamedClass( "B" );
		owlModel.createOWLNamedClass( "C" );
		owlModel.createOWLNamedClass( "D" );
		owlModel.createOWLObjectProperty( "R" );
		owlModel.createOWLObjectProperty( "S" );

		A.createOWLIndividual( "a1" );
		A.createOWLIndividual( "a2" );

		owlModel.createOWLNamedClass( "THING" );
		owlModel.createOWLNamedClass( "Medico" );
		owlModel.createOWLNamedClass( "Advogado" );
		owlModel.createOWLObjectProperty( "temFilho" );

//		String string = "(temFilho some owl:Thing) and " +
//		"temFilho only ((temFilho only Medico) and (temFilho only not Advogado))";

//		String string = "(temFilho some owl:Thing) and " +	"temFilho only (temFilho only (Medico and (not Advogado)))";

//		String string = "(A or B or C) and (hasChild some (A and B))";


//		String string = "(temFilho some owl:Thing) and " +
//					"temFilho only not ((temFilho some not Medico) or (temFilho some Advogado))";

		String string = "temFilho has a1"; // Syntax for hasValue restrictions!

		RDFResource res = owlModel.createRDFSClassFromExpression( string );

		ConceptTree t = new ConceptTree( res );

		TreeMLWriter treeWriter = new TreeMLWriter();

		try {
			treeWriter.writeGraph(
			 t, "br/uff/puro/dct/gltc/vocal/tree.xml"
			);
		} catch (DataIOException e) {
			e.printStackTrace();
		}

	}

}
