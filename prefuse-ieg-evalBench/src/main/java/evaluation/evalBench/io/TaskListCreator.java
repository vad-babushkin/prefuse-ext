package evaluation.evalBench.io;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.Task;

import java.util.ArrayList;

/**
 * interface for a task list creator which takes a {@link EvaluationSession}
 * and provides an array list of {@link Task}s
 */
public interface TaskListCreator {
    /**
     * this method should provide a list of tasks for a specified session
     * @param aSession      session containing the configurations which will be set for each task
     * @param taskFileName  path to a file containing the tasks (e.g. xml or csv)
     * @return              array list of {@link Task}s
     */
    ArrayList<Task> getTaskListForSession(EvaluationSession aSession, String taskFileName);

}
