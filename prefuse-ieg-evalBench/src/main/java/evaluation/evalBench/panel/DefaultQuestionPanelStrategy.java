package evaluation.evalBench.panel;

import evaluation.evalBench.task.Question;

import javax.swing.*;

/**
 * Default strategy for unknown tasks
 *
 * @author Stephan Hoffmann
 */
public class DefaultQuestionPanelStrategy extends QuestionPanelStrategy{

    /**
     * Constructor taking a {@link evaluation.evalBench.task.Task}
     * @param aTask any Task

     */
    public DefaultQuestionPanelStrategy(Question aTask) {

        super(aTask);
    }



    @Override
    public boolean checkForCorrectInput() {
        return true;
    }

    @Override
    public JComponent getNewAnsweringPanel() {
        return new JLabel();
    }

    @Override
    public void inputFinished() {
    }
}
