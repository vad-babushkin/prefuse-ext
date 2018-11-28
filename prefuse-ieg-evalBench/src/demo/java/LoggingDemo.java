import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import evaluation.evalBench.EvaluationDelegate;
import evaluation.evalBench.EvaluationManager;
import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.session.EvaluationSessionGroup;
import evaluation.evalBench.task.Task;

/**
 * Almost minimal demo for using EvalBench for interaction logging.
 * @author rind
 */
public class LoggingDemo {

	/**
	 * show a frame with some buttons
	 */
	private static void buildAndShowFrame() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("could not set look and feel "
					+ e.getLocalizedMessage());
		}

		JFrame frame = new JFrame("EvalBench Demo :: Logging");

		JPanel visPane = new JPanel();
		visPane.setBorder(BorderFactory.createTitledBorder("Prototype"));
		visPane.add(new JButton(new DemoAction()));
		frame.getContentPane().add(visPane, BorderLayout.CENTER);

		JPanel evalPane = new JPanel();
		evalPane.add(new JButton(new StartSessionAction(1)));
		evalPane.add(new JButton(new StartSessionAction(2)));
		evalPane.add(new JButton(new RestartLoggingAction()));
		evalPane.add(new JButton(new StopLoggingAction()));
		frame.getContentPane().add(evalPane, BorderLayout.SOUTH);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * shows a frame with a button and start logging
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				buildAndShowFrame();

				// this starts logging with minimal overhead
				EvaluationManager.getInstance().initLoggingForExploration();
				
				// following code is needed ONLY IF using sessions
				EvaluationManager.getInstance().setDelegate(
						new DummyDelegate());
				EvaluationSessionGroup aSessionGroup = new EvaluationSessionGroup(
						"user");
				EvaluationManager.getInstance().setSessionGroup(aSessionGroup);
			}
		});
	}

	/**
	 * this simulates one of the interactions to be observed
	 */
	static class DemoAction extends AbstractAction {

		private static final long serialVersionUID = 1764598133118923984L;

		public DemoAction() {
			putValue(AbstractAction.NAME, "Perfom interaction");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			EvaluationManager.getInstance().log(getValue(AbstractAction.NAME).toString());
		}

	}

	/**
	 * starts a new interaction log file with a new timestamp
	 */
	static class RestartLoggingAction extends AbstractAction {

		private static final long serialVersionUID = -726618589690256076L;
		
		public RestartLoggingAction() {
			putValue(AbstractAction.NAME, "Restart logging");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			EvaluationManager.getInstance().initLoggingForExploration();
		}

	}

	/**
	 * stops logging 
	 */
	static class StopLoggingAction extends AbstractAction {

		private static final long serialVersionUID = 8764981638202257302L;

		public StopLoggingAction() {
			putValue(AbstractAction.NAME, "Stop logging");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			EvaluationManager.getInstance().stopLogging();
		}

	}

	/**
	 * configures EvalBench so that a dedicated session is started, which results in a separate file
	 */
	static class StartSessionAction extends AbstractAction {

		private static final long serialVersionUID = -726618589690256076L;
		
		private int number; 

		public StartSessionAction(int aNumber) {
			this.number = aNumber;
			putValue(AbstractAction.NAME, "Start session " + aNumber);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			EvaluationSession aSession = new EvaluationSession("session" + number);  
			String taskFileName = "xml/demotasks.xml";
			EvaluationManager.getInstance().getSessionGroup().addSession(aSession);
			EvaluationManager.getInstance().startEvaluationSession(aSession, taskFileName );
		}

	}

	/**
	 * does nothing but is needed for EvalBench sessions to work without error.
	 * This is not needed for <tt>initLoggingForExploration()</tt>
	 */
	static class DummyDelegate implements EvaluationDelegate {

		@Override
		public void prepareForEvaluationSessionGroup(
				EvaluationSessionGroup aSessionGroup) {
		}

		@Override
		public void prepareForEvaluationSession(EvaluationSession aSession) {
		}

		@Override
		public void evaluationSessionDidFinish(EvaluationSession aSession) {
		}

		@Override
		public void resetGUIForEvaluationSession(
				EvaluationSession evaluationSession) {
		}

		@Override
		public void prepareForEvaluationTask(Task aTask) {
		}

		@Override
		public void evaluationTaskWasAnswered(Task aTask) {
		}
	}

}
