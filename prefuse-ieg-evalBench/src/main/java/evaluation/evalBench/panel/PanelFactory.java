package evaluation.evalBench.panel;

import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.Task;

import javax.swing.*;

/**
 * interface for a panel factory class which should return a {@link JPanel} to
 * provide a user interface to enable the accomplishment for a given
 * {@link Task} and a {@link QuestionPanelStrategy} for a given subclass of
 * {@link Question}.
 * 
 * @author Stephan Hoffmann, Alexander Rind
 */
public interface PanelFactory {

    /**
     * returns a taskpanel for a given task with a specified width of the panel
     * 
     * @param aTask
     *            task object with a list of {@link Question}s
     * @return a task panel for a given task
     */
    public JPanel getPanelForTask(Task aTask);

    /**
     * return a strategy for a given subclass of {@link Question}.
     * 
     * @param question
     * @return
     */
    public QuestionPanelStrategy getStrategy(Question question);

}
