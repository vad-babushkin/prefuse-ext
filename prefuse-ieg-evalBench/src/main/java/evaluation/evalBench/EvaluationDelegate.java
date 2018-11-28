package evaluation.evalBench;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.session.EvaluationSessionGroup;
import evaluation.evalBench.task.Task;

/**
 * The delegate of the evaluation framework must be implemented on the visualization tool side.
 * The methods of this interface make it possible to react on different events within an evaluation process.
 * These events include the start of an {@link EvaluationSessionGroup}, the start and end of an
 * {@link EvaluationSession} and the start and end of a {@link Task} execution.
 *
 *
 */
public interface EvaluationDelegate {
    
    // TODO shorter method name easier to keep overview??

    /**
     * This method will be called when an {@link EvaluationSessionGroup} was set in the
     * {@link EvaluationManager} to prepare the gui for a distinct {@link EvaluationSessionGroup}
     * @param aSessionGroup
     */
    public void prepareForEvaluationSessionGroup(EvaluationSessionGroup aSessionGroup);

    /**
     * This method will be called before an evaluation session will be started to prepare the GUI and
     * vis tool for this session
     * @param aSession
     */
    public void prepareForEvaluationSession(EvaluationSession aSession);

    /**
     * This method will be called after a session was finished
     * @param aSession
     */
    public void evaluationSessionDidFinish(EvaluationSession aSession);

    /**
     * This method will be called if the test person wants to reset the current GUI
     * to it's initial state
     * @param evaluationSession
     */
    public void resetGUIForEvaluationSession(EvaluationSession evaluationSession);

    /**
     * This method will be called before a task will be started to prepare the GUI and
     * vis tool for this task
     * @param aTask
     */
    public void prepareForEvaluationTask(Task aTask);

    /**
     * This method will be called after the task was answered.
     * @param aTask
     */
    public void evaluationTaskWasAnswered(Task aTask);

}
