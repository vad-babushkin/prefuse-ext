package evaluation.evalBench.io;


import evaluation.evalBench.session.EvaluationSession;

/**
 * interface for a journal factory class which should return a {@link EvaluationSessionJournal} to
 * provide a journal for a {@link EvaluationSession} that enables the exchange of the used journal. 
 * @author Stephan Hoffmann
 *
 */

public interface JournalFactory {
	EvaluationSessionJournal journalForSession(EvaluationSession session);
}
