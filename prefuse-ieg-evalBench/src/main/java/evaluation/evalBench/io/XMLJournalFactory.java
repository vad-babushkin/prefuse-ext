package evaluation.evalBench.io;

import evaluation.evalBench.session.EvaluationSession;

/**
 * Implementation of the abstract {@link JournalFactory} that returns a XMLJournal to the client. 
 * @author Stephan Hoffmann
 *
 */
public class XMLJournalFactory implements JournalFactory {

	public EvaluationSessionJournal journalForSession(EvaluationSession session) {
		
		return new XMLJournal(session);
	}

}
