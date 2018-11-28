

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import evaluation.evalBench.EvaluationManager;
import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.session.EvaluationSessionGroup;
//import evaluation.evalBench.io.XMLJournalFactory;

public class EvalAction extends AbstractAction {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EvalAction() {
		
		putValue(NAME,  "Start Evaluation...");
	}
	
	/**
	 * set up sample experiment
	 */
	public void actionPerformed(ActionEvent arg0) {
		
		// change journal to xml type  
		//EvaluationManager.getInstance().setJournalFactory(new XMLJournalFactory());
		
		// create new session group for participant one
		EvaluationSessionGroup sessionGroup = new EvaluationSessionGroup("participant1");
		
		// create training session for visualization type A
		EvaluationSession trainingA = new EvaluationSession("TrainingA"); 
		trainingA.getConfiguration().put("VisualizationType", "A");
		
		// create actual session for visualization type A
		EvaluationSession sessionA = new EvaluationSession("EvaluationA"); 
		sessionA.getConfiguration().put("VisualizationType", "A");
		
		// create training session for visualization type B
		EvaluationSession trainingB = new EvaluationSession("TrainingB"); 
		trainingB.getConfiguration().put("VisualizationType", "B");
		
		// create actual session for visualization type A
		EvaluationSession sessionB = new EvaluationSession("EvaluationB"); 
		sessionB.getConfiguration().put("VisualizationType", "B");
		
		// add sessions to group
		sessionGroup.addSession(trainingA); 
		sessionGroup.addSession(sessionA); 
		sessionGroup.addSession(trainingB); 
		sessionGroup.addSession(sessionB);
		
		// set session group (triggers execution of session group)
		EvaluationManager.getInstance().setSessionGroup(sessionGroup); 
	}

}
