package evaluation.evalBench.panel;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.task.TextInputQuestion;

/**
 * @author David
 * 
 */
public class TextInputQuestionPanelStrategy extends QuestionPanelStrategy {

	private JTextComponent textInputField;

	/**
	 * @param aQuestion
	 */
	public TextInputQuestionPanelStrategy(TextInputQuestion aQuestion) {
		super(aQuestion);
	}

	@Override
	public boolean checkForCorrectInput() {

		TextInputQuestion textQuestion = (TextInputQuestion) super.getQuestion();

		if (!textInputField.getText().equals("")) {

            if (textQuestion.getRegEx() != null
                    && textQuestion.getRegEx().length() > 0
                    && !textInputField.getText().matches(
                            textQuestion.getRegEx())) {

				setErrorMessage(EvaluationResources.getString("textinputquestion.errorPattern"));
				return false;
			}

			((TextInputQuestion) super.getQuestion()).setGivenAnswer(textInputField
					.getText());
			setErrorMessage("");
			return true;
		} else {
			setErrorMessage(EvaluationResources.getString("textinputquestion.errorText"));
			return false;
		}

	}

	@Override
	public JPanel getNewAnsweringPanel() {
		JPanel innerPanel = new JPanel();
		innerPanel.setLayout(new BorderLayout());

		TextInputQuestion textQuestion = (TextInputQuestion) super.getQuestion();

		if (textQuestion.getSingleLine()) {
		    textInputField = new JTextField(); 
            innerPanel.add(textInputField, BorderLayout.CENTER);
        } else {
            JTextArea textArea = new JTextArea();
            
            textArea.setRows(5);
            textArea.setColumns(20);
            
            Font font = UIManager.getFont("Label.font");
            textArea.setFont(font);
            
            textInputField = textArea;
			innerPanel.add(new JScrollPane(textInputField),
					BorderLayout.CENTER);
		}

		return innerPanel;
	}

	@Override
	public void inputFinished() {
		// TODO Auto-generated method stub

	}
}
