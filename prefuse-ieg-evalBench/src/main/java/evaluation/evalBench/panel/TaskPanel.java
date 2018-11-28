package evaluation.evalBench.panel;

import evaluation.evalBench.EvaluationManager;
import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.task.Task;

import javax.swing.*;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * A JPanel subclass used to display the abstract properties of a {@link Task}:
 * task description, a button to mark the task as finished, a reset button which
 * triggers the visualization tool to return to the initial state and an error
 * panel, which shows additional information for task execution if something
 * went wrong. It uses a {@link QuestionPanelStrategy} for task answering
 * issues.
 * 
 * @author Stephan Hoffmann
 */
public class TaskPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private int m_evaluationPanelWidth;
	private JTextPane errorMessagePanel;

	private Task task;
	private ArrayList<QuestionPanelStrategy> taskPanelStrategyList;

	/**
	 * Initializes the task panel with a task
	 * 
	 * @param aTask
	 *            a subclass of Task
	 * @param aStrategy
	 *            the task panel strategy which should be used with this task
	 * @param panelWidth
	 *            the width of the panel (needs to be set)
	 */
	public TaskPanel(Task aTask,
			ArrayList<QuestionPanelStrategy> aStrategyList, int panelWidth) {
		super();
		this.task = aTask;
		this.m_evaluationPanelWidth = panelWidth;
		this.taskPanelStrategyList = aStrategyList;

		this.initGui();

	}

	/**
	 * sets an error message if the test person did not finish the task (e.g.
	 * did not fill in all mandatory fields)
	 * 
	 * @param msg
	 */
	protected void setErrorMessage(String msg) {
		this.errorMessagePanel.setText(msg);

		View v = errorMessagePanel.getUI().getRootView(errorMessagePanel);
		v.setSize(m_evaluationPanelWidth, Integer.MAX_VALUE);
		float preferredHeight = v.getPreferredSpan(View.Y_AXIS) + 20;

		// setting the height
		errorMessagePanel.setPreferredSize(new Dimension(
				m_evaluationPanelWidth, (int) preferredHeight));
		errorMessagePanel.setMaximumSize(new Dimension(m_evaluationPanelWidth,
				(int) preferredHeight));

	}

	protected void initGui() {

		this.removeAll();

		this.setLayout(new BorderLayout());

		JPanel taskPanel = new JPanel();

		taskPanel.setBorder(BorderFactory.createTitledBorder("Task"));

		JTextPane taskDescriptionPanel = new JTextPane();

		taskDescriptionPanel.setContentType("text/html");
		this.setHTMLSystemFont(taskDescriptionPanel);

		this.errorMessagePanel = new JTextPane();

		try {

			taskPanel.setLayout(new BoxLayout(taskPanel, BoxLayout.Y_AXIS));

			{
				taskPanel.add(taskDescriptionPanel);

				taskDescriptionPanel.setBorder(BorderFactory.createEmptyBorder(
						5, 5, 5, 5));
				taskDescriptionPanel.setText(task.getTaskDescription()
				// TODO handle task (!) description and instructions
				+ "<br><br>" + task.getTaskInstruction());

				// calculate the height for html textpane
				View v = taskDescriptionPanel.getUI().getRootView(
						taskDescriptionPanel);
				v.setSize(m_evaluationPanelWidth, Integer.MAX_VALUE);
				float preferredHeight = v.getPreferredSpan(View.Y_AXIS) + 70;

				// setting the height
				taskDescriptionPanel.setPreferredSize(new Dimension(
						m_evaluationPanelWidth, (int) preferredHeight));
				taskDescriptionPanel.setMaximumSize(new Dimension(
						m_evaluationPanelWidth, (int) preferredHeight));
				taskDescriptionPanel.setMinimumSize(new Dimension(
						m_evaluationPanelWidth, (int) preferredHeight));

				taskDescriptionPanel.setEditable(false);

				taskDescriptionPanel.setBackground(this.getBackground());

			}

			{
				if (taskPanelStrategyList != null) {
					appendQuestionPanels(taskPanel);
				}

			}
			{
				taskPanel.add(errorMessagePanel);
				errorMessagePanel.setBorder(BorderFactory.createEmptyBorder(5,
						5, 5, 5));

				errorMessagePanel.setForeground(new Color(255, 0, 0));
				errorMessagePanel.setEditable(false);
				errorMessagePanel.setBackground(this.getBackground());

				View v = errorMessagePanel.getUI().getRootView(
						errorMessagePanel);
				v.setSize(m_evaluationPanelWidth - 10, Integer.MAX_VALUE);
				float preferredHeight = v.getPreferredSpan(View.Y_AXIS) + 20;

				// setting the height
				errorMessagePanel.setPreferredSize(new Dimension(
						m_evaluationPanelWidth, (int) preferredHeight));
				errorMessagePanel.setMaximumSize(new Dimension(
						m_evaluationPanelWidth, (int) preferredHeight));

			}
			{

				JButton nextTaskButton = new JButton(
						EvaluationResources.getString("taskpanel.nextButton"));

				nextTaskButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						nextTaskActionPerformed();
					}
				});

				nextTaskButton.setAlignmentX(Component.CENTER_ALIGNMENT);
				taskPanel.add(nextTaskButton);
				taskPanel.add(Box.createVerticalGlue());
			}
			{
				this.add(taskPanel, BorderLayout.CENTER);

				if (EvaluationManager.getInstance().isResetSupported()) {
					JButton resetButton = new JButton(
							EvaluationResources.getString("taskpanel.resetButton"));
					resetButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							resetButtonActionPerformed();
						}
					});

				JPanel southPanel = new JPanel();

				southPanel.add(resetButton, BorderLayout.NORTH);
				southPanel
						.add(Box.createVerticalStrut(50), BorderLayout.CENTER);

				this.add(southPanel, BorderLayout.SOUTH);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void appendQuestionPanels(JPanel taskPanel) {
		for (QuestionPanelStrategy strategy : taskPanelStrategyList) {
			
			JLabel descriptionLabel = new JLabel(strategy.getQuestion().getQuestionText());
			JComponent answerPanel = strategy.getNewAnsweringPanel();

			JPanel questionPanel = new JPanel();
			questionPanel.setLayout(new BorderLayout(5, 15));
			questionPanel.add(descriptionLabel, BorderLayout.NORTH);
			questionPanel.add(answerPanel, BorderLayout.CENTER);

			questionPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			questionPanel.setMaximumSize(questionPanel.getPreferredSize());

			taskPanel.add(questionPanel);
		}
	}

	/**
	 * helper for html JTextPane: sets the font to system font
	 * 
	 * @param aPane
	 *            a JTextPane with html text content
	 */
	private void setHTMLSystemFont(JTextPane aPane) {

		// add a CSS rule to force body tags to use the default label font
		// instead of the value in javax.swing.text.html.default.csss
		Font font = UIManager.getFont("Label.font");
		String bodyRule = "body { font-family: " + font.getFamily() + "; "
				+ "font-size: " + font.getSize() + "pt; }";
		((HTMLDocument) aPane.getDocument()).getStyleSheet().addRule(bodyRule);

	}

	/**
	 * ActionHandler for next task button
	 * 
	 */
	private void nextTaskActionPerformed() {

		boolean everythingCorrect = true;
		String errorMessages = "";

		for (QuestionPanelStrategy strategy : taskPanelStrategyList) {
			if (!strategy.checkForCorrectInput()) {
				everythingCorrect = false;
				errorMessages += " - "+strategy.getErrorMessage() +"\n";
			}
		}

		if (everythingCorrect) {
			this.errorMessagePanel.setVisible(false);
			for (QuestionPanelStrategy strategy : taskPanelStrategyList)
				strategy.inputFinished();
			// TODO handle multiple questions in a task panel
			EvaluationManager.getInstance().log(
					"task finished: id:" + this.task.getTaskId());
			// EvaluationManager.getInstance().log("task finished: id:" +
			// this.task.getTaskId() +" config: "+ this.task.getConfiguration()
			// +" task type: "+ this.task.getTaskType());
			EvaluationManager.getInstance().continueActiveSession();

		} else {
			this.setErrorMessage(errorMessages);
			this.errorMessagePanel.setVisible(true);
		}
	}

	/**
	 * ActionHandler for reset button
	 * 
	 */
	private void resetButtonActionPerformed() {
		EvaluationManager.getInstance().resetCurrentVisualization();

	}

}
