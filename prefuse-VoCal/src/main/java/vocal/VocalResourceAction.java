///**
// * @author Luiz Guilherme Oliveira dos Santos
// * Universidade Federal Fluminense || P�lo Universit�rio de Rio das Ostras
// *
// * Projeto de Pesquisa: Explicando Provas em L�gica de Descri��o
// * Orientador: Fernando Naufel
// *
// *
// */
//
//package prefuse.demos.vocal;
//
//import java.awt.Component;
//import java.awt.event.ActionEvent;
//
//import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
//import edu.stanford.smi.protegex.owl.model.RDFResource;
//import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
//import edu.stanford.smi.protegex.owl.ui.conditions.ConditionsTable;
//import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
//
//
//
//public class VocalResourceAction extends ResourceAction {
//
//	public VocalResourceAction() {
//        super("Show concept tree", OWLIcons.getNerdSmilingIcon() );
//    }
//
//	public void actionPerformed(ActionEvent e) {
//		RDFResource res = getResource();
//		OWLNamedClass editedClass = ((ConditionsTable) getComponent()).getEditedCls();
//
//		// TODO: Montar a �rvore da conjun��o de *todas as condi��es do bloco onde o usu�rio clicou*
//
//		ConceptTree t = new ConceptTree( res );
//		ConceptTreeView tv = new ConceptTreeView( t );
//		VocalUI ui = new VocalUI( tv );
//
///*
//		//	To write the tree:
//		TreeMLWriter treeWriter = new TreeMLWriter();
//		try {
//			treeWriter.writeGraph( t, "vocal/tree.xml" );
//		} catch (DataIOException exc) {
//			exc.printStackTrace();
//		}
//*/
//
//    }
//
//
//    public boolean isSuitable(Component component, RDFResource resource) {
//        if (component instanceof ConditionsTable) {
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//
//}//end class
