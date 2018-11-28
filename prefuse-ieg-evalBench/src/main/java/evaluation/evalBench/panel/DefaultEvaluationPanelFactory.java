package evaluation.evalBench.panel;

import java.util.ArrayList;

import evaluation.evalBench.task.ChoiceSelectionQuestion;
import evaluation.evalBench.task.DateQuestion;
import evaluation.evalBench.task.ItemSelectionQuestion;
import evaluation.evalBench.task.LikertskalaQuestion;
import evaluation.evalBench.task.QuantitativeQuestion;
import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.SubjectiveMentalEffortQuestion;
import evaluation.evalBench.task.Task;
import evaluation.evalBench.task.TextInputQuestion;
import evaluation.evalBench.task.YesNoQuestion;

import javax.swing.*;

/**
 * Default evaluation panel factory implementation. Returns a task panel for the
 * three default tasks: {@link QuantitativeQuestion},
 * {@link ChoiceSelectionQuestion}, and {@link ItemSelectionQuestion}. For every
 * other task type an empty panel will be returned
 * 
 * @author Stephan Hoffmann, Alexander Rind
 */
public class DefaultEvaluationPanelFactory implements PanelFactory {

	private int m_width = 200;

	/**
	 * Creates a tailored {@link TaskPanel} for a given subclass of {@link Task}
	 * 
	 * @param aTask
	 *            a specific {@link Task}
	 * @return TaskPanel {@link TaskPanel} used to answer the task
	 */
	@Override
	public JPanel getPanelForTask(Task aTask) {

		if (aTask != null) {
		    
	        ArrayList<QuestionPanelStrategy> strategyList = new ArrayList<QuestionPanelStrategy>();
	        for (Question question : aTask.getQuestions()) {
	            strategyList.add(getStrategy(question));
	        }
		    
			return new TaskPanel(aTask, strategyList, m_width);
		}

		return null;
	}

    @Override
    public QuestionPanelStrategy getStrategy(Question question) {

        if (question instanceof QuantitativeQuestion) {
            return new QuantitativeQuestionPanelStrategy(
                    (QuantitativeQuestion) question);
        } else if (question instanceof ChoiceSelectionQuestion) {
            return new ChoiceSelectionQuestionPanelStrategy(
                    (ChoiceSelectionQuestion) question);
        } else if (question instanceof ItemSelectionQuestion) {
            return new ItemSelectionQuestionPanelStrategy(
                    (ItemSelectionQuestion) question);
        } else if (question instanceof DateQuestion) {
            return new DateQuestionPanelStrategy((DateQuestion) question);
        } else if (question instanceof LikertskalaQuestion) {
            return new LikertskalaQuestionPanelStrategy(
                    (LikertskalaQuestion) question);
        } else if (question instanceof TextInputQuestion) {
            return new TextInputQuestionPanelStrategy(
                    (TextInputQuestion) question);
        } else if (question instanceof SubjectiveMentalEffortQuestion) {
            return new SubjectiveMentalEffortQuestionPanelStrategy(question);
        } else if (question instanceof YesNoQuestion) {
            return new YesNoQuestionPanelStrategy((YesNoQuestion) question);
            // TODO reimplement InsightTask
            // } else if (aTask instanceof InsightTask) {
            // aStrategy = new InsightTaskPanelStrategy(aTask);
        } else {
            return new DefaultQuestionPanelStrategy(question);
        }
    }

	public void setPanelWidth(int pixel) {
		this.m_width = pixel;
	}

	public int getPanelWidth() {
		return this.m_width;
	}
}
