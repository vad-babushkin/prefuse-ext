package evaluation.evalBench.panel;

import java.awt.FlowLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.task.YesNoQuestion;

/**
 * @author David
 * 
 */
public class YesNoQuestionPanelStrategy extends QuestionPanelStrategy {

	private ButtonGroup radioButtonGroup;
	private JRadioButton yesButton, noButton;

	/**
	 * @param aTask
	 */
	public YesNoQuestionPanelStrategy(YesNoQuestion aTask) {
		super(aTask);
	}

	@Override
	public boolean checkForCorrectInput() {

		if (yesButton.isSelected()) {
			((YesNoQuestion) super.getQuestion()).setGivenAnswer(true);
			setErrorMessage("");
			return true;
		} else if (noButton.isSelected()) {
			((YesNoQuestion) super.getQuestion()).setGivenAnswer(false);
			setErrorMessage("");
			return true;
		} else {
			setErrorMessage(EvaluationResources.getString("yesnoquestion.errorSelect"));
			return false;
		}
	}

	@Override
	public JPanel getNewAnsweringPanel() {
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new FlowLayout());

		yesButton = new JRadioButton(EvaluationResources.getString("yesnoquestion.yes"));
		noButton = new JRadioButton(EvaluationResources.getString("yesnoquestion.no"));

		innerPanel.add(yesButton);
		innerPanel.add(noButton);

		radioButtonGroup = new ButtonGroup();
		radioButtonGroup.add(yesButton);
		radioButtonGroup.add(noButton);

		return innerPanel;
	}

	@Override
	public void inputFinished() {
		// TODO Auto-generated method stub

	}	
}
