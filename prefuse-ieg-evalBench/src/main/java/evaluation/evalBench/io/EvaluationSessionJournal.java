package evaluation.evalBench.io;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.Task;

/**
 * the interface for an evaluation journal which is responsible to record tasks
 * {@link Task} for a specific session {@link EvaluationSession}.
 * 
 * Implementations of this class are responsible to sanitize the task properties
 * according to their data format.
 */
public interface EvaluationSessionJournal {

    /**
     * records the given task with it's configurations
     * @param aTask task to be recorded
     */
    public void recordTask(Task aTask);

    /**
     * will be called when the session has finished (e.g. to close the file)
     */
    public void sessionFinished();

    /**
     * Start a new journal for the given session
     * @param aSession session to be recorded
     */
    public void setSession(EvaluationSession aSession);
    // TODO setSession() not externally called; journal objects not reused


}
