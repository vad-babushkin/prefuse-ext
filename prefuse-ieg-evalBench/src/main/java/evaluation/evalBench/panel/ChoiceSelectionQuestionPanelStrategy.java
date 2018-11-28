package evaluation.evalBench.panel;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.TreeSet;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;

import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.task.ChoiceSelectionQuestion;
import evaluation.evalBench.task.ChoiceSelectionQuestion.ChoiceOption;

/**
 * Subclass of {@link QuestionPanelStrategy} Task Panel Strategy for a
 * {@link ChoiceSelectionQuestion} with check boxes or radio buttons for the
 * answer
 * 
 * @author Stephan Hoffmann, Barbara Neubauer, Alexander Rind
 */
public class ChoiceSelectionQuestionPanelStrategy extends QuestionPanelStrategy
		implements ActionListener {

	protected ArrayList<JToggleButton> checkBoxes;
	private ButtonGroup radioButtonGroup;

	/**
	 * Constructor taking a {@link ChoiceSelectionQuestion}
	 * 
	 * @param aTask
	 *            {@link ChoiceSelectionQuestion}
	 */
	public ChoiceSelectionQuestionPanelStrategy(ChoiceSelectionQuestion aTask) {

		super(aTask);
	}

	/**
	 * provides an answering field (JPanel) with check boxes
	 * 
	 * @return an answering field
	 */
	@Override
	public JPanel getNewAnsweringPanel() {

		String markup;

		this.checkBoxes = new ArrayList<JToggleButton>();
		radioButtonGroup = new ButtonGroup();
		JPanel innerPanel = new JPanel();

		ChoiceSelectionQuestion choiceQuestion = (ChoiceSelectionQuestion) this
				.getQuestion();

		GridLayout thisLayout = new GridLayout(choiceQuestion
				.getPossibleAnswers().size(), 1);
		thisLayout.setHgap(5);
		thisLayout.setVgap(5);
		innerPanel.setLayout(thisLayout);

		// maxValue
		for (ChoiceOption choicePair : choiceQuestion.getPossibleAnswers()) {
//		    org.apache.log4j.Logger.getLogger(this.getClass()).error(choicePair.getLabel());
			JToggleButton aCheckBox = (choiceQuestion.getMaxChoices() == 1) ? new JRadioButton()
					: new JCheckBox();

			markup = "";
			markup = "<html><body>";
			markup += (choicePair.getImage() == null) ? "" : "<img src='"
					+ this.getClass().getResource(choicePair.getImage())
					+ "'/><br />";

			if (choicePair.getDisplayLabel() == null
					&& choicePair.getImage() == null) {
				markup += "<div align='center'>" + choicePair.getLabel()
						+ "</div>";
			}

			markup += (choicePair.getDisplayLabel() == null) ? ""
					: "<div align='center'>" + choicePair.getDisplayLabel()
							+ "</div>";

			markup += "</body></html>";
			aCheckBox.setText(markup);

			aCheckBox.setName(choicePair.getLabel());
			aCheckBox.addActionListener(this);
			aCheckBox.setActionCommand(choicePair.getLabel());
			if (choiceQuestion.getMaxChoices() == 1)
				radioButtonGroup.add(aCheckBox);

			innerPanel.add(aCheckBox);
			checkBoxes.add(aCheckBox);
		}

		return innerPanel;
	}

	@Override
	public void inputFinished() {

	}

	/**
	 * action listener for check boxes
	 * 
	 * @param actionEvent
	 *            action
	 */
	public void actionPerformed(ActionEvent actionEvent) {

		this.setErrorMessage((""));

		// check if task is single choice type and deselect all other checkboxes
		// ChoiceSelectionTask choiceTask = (ChoiceSelectionTask)this.getTask();
		// if (choiceTask.getSingleChoice()){
		//
		// for (JCheckBox aBox : checkBoxes) {
		// if (!actionEvent.getActionCommand().equals(aBox.getActionCommand()))
		// {
		// aBox.setSelected(false);
		// }
		// }
		//
		// }
	}

	/**
     * checks for correct input for this task and sets the selected checkboxes
     * as answers for the task
     *
     * @return false if wrong number of checkbox was selected
     */
    @Override
    public boolean checkForCorrectInput() {

        ChoiceSelectionQuestion question = (ChoiceSelectionQuestion) super
				.getQuestion();

		TreeSet<String> givenAnswer = new TreeSet<String>();

		for (JToggleButton aBox : checkBoxes) {
			if (aBox.isSelected())
				givenAnswer.add(aBox.getName());
		}

        // no box selected (and not required to select any)
        if (givenAnswer.isEmpty() && question.getMinChoices() > 0) {
            this.setErrorMessage((EvaluationResources.getString("choiceselectionquestion.errorNone")));
            return false;
        }

        // answer count out of required range
        if (givenAnswer.size() > question.getMaxChoices() || givenAnswer.size() < question.getMinChoices()) {
            if (question.getMaxChoices() == question.getMinChoices()) {
                this.setErrorMessage(
                        String.format(EvaluationResources.getString("choiceselectionquestion.errorCountExact"),
                                question.getMinChoices()));
            } else {
                this.setErrorMessage(String.format(EvaluationResources.getString("choiceselectionquestion.errorCount"),
                        question.getMinChoices(), question.getMaxChoices()));
            }
            return false;
        }

        // all ok
        question.setGivenAnswer(givenAnswer);
        this.setErrorMessage("");
        return true;
    }
}
