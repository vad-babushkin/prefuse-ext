import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;


import evaluation.evalBench.EvaluationDelegate;
import evaluation.evalBench.EvaluationManager;
import evaluation.evalBench.panel.TaskDialog;
import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.session.EvaluationSessionGroup;
import evaluation.evalBench.task.Task;


public class EvalBenchSampleFrame extends JFrame implements EvaluationDelegate {
	
	private static final long serialVersionUID = 1L;
	
	private static final String FRAME_TITLE = "VisualizationTool";

	private JPanel evalPanel;
	private JPanel visPanel;
	
	private TaskDialog taskDialog;

	
	/**
	 * Initialize new Frame with title and add a visualization panel
	 */
	public EvalBenchSampleFrame(){
		this.setTitle(FRAME_TITLE);
		setJMenuBar(createMenuBar());
		
		visPanel = getVisualizationPanel(true); 
		
		add(visPanel, BorderLayout.CENTER);
		taskDialog= new TaskDialog(null);
	}
	
	/**
	 * create a new menu bar
	 * @return menubar
	 */
	private JMenuBar createMenuBar() {
		final JMenuBar bar = new JMenuBar();
		
		final JMenu evalMenu = new JMenu("Evaluation");
		evalMenu.add(new EvalAction());
		bar.add(evalMenu);

		return bar;
	}
	
	/**
	 * create a JPanel with a visualization
	 * @return
	 */
	public JPanel getVisualizationPanel(boolean initial){
		JPanel aPanel = new JPanel();
		aPanel.setLayout(new BorderLayout());
		aPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		aPanel.setPreferredSize(new Dimension(400, 600));
		if (initial)
		{
			aPanel.add(new JLabel("<html><body>" +
					"<center><b>Visualization Panel</b></center><br>" +
					"Choose <i>Evaluation</i> in the menu bar to start the evaluation</body>" +
					"</html>", JLabel.CENTER), BorderLayout.CENTER);
		}
		else
		{
			
		}
		
		return aPanel;
	}
	
	private void setVisPanelToNormalState()
	{
		visPanel.setVisible(false);
		visPanel.removeAll();
		visPanel.add(new JLabel("<html><body>" +
				"<center><b>Visualization Panel</b></center><br>" +
				"Choose <i>Evaluation</i> in the menu bar to start the evaluation</body>" +
				"</html>", JLabel.CENTER), BorderLayout.CENTER);
		visPanel.setVisible(true);
		pack();
	}
	
	private void setVisPanelToEvalState()
	{
		visPanel.setVisible(false);
		visPanel.removeAll();
		visPanel.add(new JLabel("<html><body>" +
				"<center><b>Visualization Panel</b></center>"
				, JLabel.CENTER), BorderLayout.CENTER);
		visPanel.setVisible(true);
		pack();
	}
	
	/**
	 * create evaluationPanel for answering tasks
	 */
	private void addEvaluationPanel(){
		if (evalPanel == null) {
			evalPanel = new JPanel();
			evalPanel.setLayout(new BorderLayout());
			evalPanel.setPreferredSize(new Dimension(250, 600));
	
		}
		
		add(evalPanel, BorderLayout.EAST);
		pack();
		
	}
	
	/**
	 * remove evaluation panel from frame
	 */
	private void removeEvaluationPanel(){
		evalPanel.removeAll();
		remove(evalPanel);
		pack();
	}
	

	/**
	 * set the task panel as a child of the evaluation panel
	 * @param aPanel a task panel
	 */
	private void setMyEvaluationPanel(JPanel aPanel){
		evalPanel.setVisible(false);
		evalPanel.removeAll();
		evalPanel.add(aPanel, BorderLayout.CENTER);
		evalPanel.setVisible(true);
	}
	

	/*
	 * ------------------- Evaluation Delegate Methods -------------------
	 */
	
	/**
	 * a session group is about to begin
	 */
	public void prepareForEvaluationSessionGroup(EvaluationSessionGroup sessionGroup) {
				
		// set vis state
		setVisPanelToEvalState();
						
		// add an evaluation frame to the right side of the frame.  
		addEvaluationPanel();
		// choose a session and trigger the execution with a defined task list (demotasks.xml)
		EvaluationManager.getInstance().startEvaluationSession(sessionGroup.getSessionList().get(0), "xml/demotasks.xml"); 
		
	}
	
	/**
	 * a session is about to begin
	 */
	public void prepareForEvaluationSession(EvaluationSession aSession) {
	
		// prepare for a evaluation session, e.g. load a dataset for the visualization
		
		// show an info dialog 
        taskDialog.announceSession(aSession, true);
		
	}
	
	/**
	 * a evaluation session did finish
	 */
	public void evaluationSessionDidFinish(EvaluationSession aSession) {
		
		// show an info dialog 
		JOptionPane.showMessageDialog(this, "Session " + aSession.getTitle() + " did finish");

		// remove evaluation panel from GUI
		removeEvaluationPanel();
		
		// set visualization state back to normal
		setVisPanelToNormalState();
		
	}
	
	/**
	 * a task from the current session is about to begin 
	 */
	public void prepareForEvaluationTask(Task aTask) {
		
        taskDialog.showDescription(aTask);
        
		// get a task panel from the manager
		setMyEvaluationPanel( EvaluationManager.getInstance().getPanelForTask(aTask));
	}

	/**
	 * a task from the current session was answered
	 */
	public void evaluationTaskWasAnswered(Task aTask) {
		
        // do something after a task was finished (e.g. update GUI)
        if ("true".equals(aTask.getConfiguration().get("Training"))) {

            boolean repeat = taskDialog.showFeedback(aTask);
            if (repeat) {
                EvaluationManager.getInstance()
                .getSessionGroup()
                        .getActiveSubSession().addTask(aTask, 0);
            }
        }
	}


	/**
	 * the reset button on a task panel was pressed
	 */
	public void resetGUIForEvaluationSession(EvaluationSession aSession) {
		
		// reset the visualization to the initial state of the session 
		
	}

}
