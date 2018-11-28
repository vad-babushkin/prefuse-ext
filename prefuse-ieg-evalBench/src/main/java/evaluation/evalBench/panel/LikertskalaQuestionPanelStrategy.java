package evaluation.evalBench.panel;

import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.task.LikertskalaQuestion;

/**
 * @author David, Alexander Rind
 *
 */
public class LikertskalaQuestionPanelStrategy extends QuestionPanelStrategy {

	private ButtonGroup radioGroup;
	private ArrayList<JRadioButton> radioList;

	/**
	 * @param aQuestion
	 */
	public LikertskalaQuestionPanelStrategy(LikertskalaQuestion aQuestion) {
		super(aQuestion);
	}

	@Override
	public boolean checkForCorrectInput() {

		JRadioButton selectedButton = null;

		for (JRadioButton button : radioList) {
			if (button.isSelected())
				selectedButton = button;
		}

		if (selectedButton != null) {
			try {
				((LikertskalaQuestion) super.getQuestion()).setGivenAnswer(Integer
						.parseInt(selectedButton.getName()));
				setErrorMessage("");
				return true;
			} catch (Exception e) {
				setErrorMessage(EvaluationResources.getString("likertskalaquestion.errorInt"));
				return false;
			}

		} else {
			setErrorMessage(EvaluationResources.getString("likertskalaquestion.errorButton"));
			return false;
		}

	}

    @Override
    public JPanel getNewAnsweringPanel() {
        LikertskalaQuestion question = (LikertskalaQuestion) super.getQuestion();

        radioGroup = new ButtonGroup();
        radioList = new ArrayList<JRadioButton>();
        JLabel minJLabel = new JLabel(question.getLeftLabel());
        minJLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JLabel maxJLabel = new JLabel(question.getRightLabel());
        JRadioButton button;

        JPanel answer = new JPanel();
        GroupLayout layout = new GroupLayout(answer);
        answer.setLayout(layout);
        layout.setAutoCreateGaps(true);

        SequentialGroup hoGrp = layout.createSequentialGroup();
        ParallelGroup veGrp = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);

        hoGrp.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        hoGrp.addComponent(minJLabel);
        veGrp.addComponent(minJLabel);

        for (int i = 0; i < question.getCountOptions(); i++) {
            button = new JRadioButton("");
            button.setName(String.valueOf(i));

            radioGroup.add(button);
            radioList.add(button);

            hoGrp.addComponent(button, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE,
                    GroupLayout.PREFERRED_SIZE);
            veGrp.addComponent(button);
        }

        hoGrp.addComponent(maxJLabel);
        veGrp.addComponent(maxJLabel);
        layout.linkSize(SwingConstants.HORIZONTAL, minJLabel, maxJLabel);
        hoGrp.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);

        layout.setHorizontalGroup(hoGrp);
        layout.setVerticalGroup(veGrp);

        return answer;
    }

    @Override
    public void inputFinished() {
        // nothing to do here
    }
}
