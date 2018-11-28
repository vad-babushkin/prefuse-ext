package evaluation.evalBench;

import javax.swing.JPanel;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import evaluation.evalBench.io.CsvJournalFactory;
import evaluation.evalBench.io.EvaluationSessionJournal;
import evaluation.evalBench.io.JournalFactory;
import evaluation.evalBench.io.TaskListCreator;
import evaluation.evalBench.io.XMLTaskListCreator;
import evaluation.evalBench.panel.DefaultEvaluationPanelFactory;
import evaluation.evalBench.panel.PanelFactory;
import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.session.EvaluationSessionGroup;
import evaluation.evalBench.task.Task;
import evaluation.evalBench.util.DateHelper;
import ieg.util.lang.MoreMath;

/**
 * The EvaluationManager is a singleton responsible for interaction logging and management of evaluation session
 * groups {@link EvaluationSessionGroup}, and their sub sessions {@link EvaluationSession} and calls the evaluation
 * delegate {@link EvaluationDelegate}, which should be implemented in the visualization tool, on
 * certain events. The delegate has the be set with {@link EvaluationManager#setDelegate(EvaluationDelegate)}
 *
 * The EvaluationManager should be used to configure an evaluation process: set up the evaluation session group
 * {@link EvaluationSessionGroup} containing sub sessions {@link EvaluationSession}. It contains a
 * {@link PanelFactory} for the creation of {@link evaluation.evalBench.panel.TaskPanel}, by default
 * a {@link DefaultEvaluationPanelFactory}. For the creation of {@link evaluation.evalBench.task.TaskList}s,
 * a {@link TaskListCreator} implementation can be used.
 * */
public class EvaluationManager {
    
    // TODO move to config file
    public static final String LOG_PATTERN = "%d ; %m%n";

    private static EvaluationManager sInstance;

    private EvaluationDelegate      m_delegate;
    private PanelFactory            m_panelFactory;
    private EvaluationSessionGroup  m_sessionGroup;
    private TaskListCreator         m_taskListCreator;
    
    private FileAppender            m_logfile;
    // TODO merge with sessionGroup.participantId
    private String                  m_logUniqueID;

	private final Logger logger = Logger.getLogger("EvaluationManager");

	private JournalFactory			m_journalFactory;

	private boolean					m_resetSupported = true;

    /**
     * Static method "getInstance()" returns the only instance of the class
     * It is synchronized to provide thread safety
     * @return the only instance of this class
     */
    public synchronized static EvaluationManager getInstance() {
        // TODO use double-checked locking to avoid expensive locking using a synchronized method    	
		if (sInstance == null)
			sInstance = new EvaluationManager();

		return sInstance;

	}

    /**
     * constructor called on the first getInstance call. Initializes logging to the current directory
     * and creates a {@link DefaultEvaluationPanelFactory} and an empty {@link EvaluationSessionGroup}
     */
	private EvaluationManager() {

        // this.initLoggingForExploration();

		logger.fatal(
                EvaluationResources.getString("eval.evaluationStarted")
                );

        m_panelFactory = new DefaultEvaluationPanelFactory();
        m_sessionGroup = new EvaluationSessionGroup("");
        m_taskListCreator = new XMLTaskListCreator();
        m_journalFactory = new CsvJournalFactory();
	}

    /**
     * Set a {@link PanelFactory} implementation for the current {@link EvaluationSession}
     * @param aFactory {@link PanelFactory} implementation
     */
    public void setPanelFactory(PanelFactory aFactory){
        m_panelFactory = aFactory;
    }

    public PanelFactory getPanelFactory() {
        return m_panelFactory;
    }

    /**
     * Set the {@link EvaluationDelegate}, which should be implemented on the visualization tool side to react
     * on certain events.
     * @param aDelegate {@link EvaluationDelegate} implementation
     */
    public void setDelegate(EvaluationDelegate aDelegate){
        m_delegate = aDelegate;
    }
    
    /**
     * Sets a new factory that needs to be called if a session is about to start and
     * the journal for that session will be created depending on that factory
     * @param aFactory
     */
    public void setJournalFactory(JournalFactory aFactory)
    {
    	m_journalFactory = aFactory;
    }

    /**
     * Set the {@link TaskListCreator} for the current {@link EvaluationSession}
     * @param aTaskListCreator {@link TaskListCreator} implementation
     */
    public void setTaskListCreator(TaskListCreator aTaskListCreator){
        m_taskListCreator = aTaskListCreator;
    }

     /**
     * Calls the {@link PanelFactory} implementation, which is by default a {@link DefaultEvaluationPanelFactory} and
     * can be set with {@link EvaluationManager#setPanelFactory(evaluation.evalBench.panel.PanelFactory)}
     *
     * @param aTask         task which should be known by the set {@link PanelFactory}
     * @return              a {@link evaluation.evalBench.panel.TaskPanel} for the given task
     */
	public JPanel getPanelForTask(Task aTask){
        return m_panelFactory.getPanelForTask(aTask);
    }

    /**
     * Set a new session group for the evaluation process. Calls the evaluation delegate
     * {@link EvaluationDelegate#prepareForEvaluationSessionGroup(EvaluationSessionGroup)}
     * @param aSessionGroup new session group
     */
    public void setSessionGroup(EvaluationSessionGroup aSessionGroup){

        m_sessionGroup = aSessionGroup;



        if (m_delegate != null){
            m_delegate.prepareForEvaluationSessionGroup(aSessionGroup);
        }else{
            System.err.println("No EvaluationDelegate was set!");
        }

    }
    
    public EvaluationSessionJournal getJournalForSession(EvaluationSession aSession)
    {
		return m_journalFactory.journalForSession(aSession);
    	
    }

    /**
     *
     * @return the current session group
     */
    public EvaluationSessionGroup getSessionGroup(){
        return m_sessionGroup;
    }

    /**
     * starts a new session which has to be part of the current session group {@link EvaluationSessionGroup}.
     * Calls {@link EvaluationDelegate#prepareForEvaluationSession(EvaluationSession)}. For the duration of the session
     * the interaction logging will be saved in the same directory the
     * {@link evaluation.evalBench.io.EvaluationSessionJournal} for this session.
     *
     * @param aSession      evaluation session which should be started
     * @param taskFileName  a path to a file which contains the tasks for this session (e.g., in xml format)
     *                      (null if the {@link TaskListCreator} does not need a file
     *
     */
    public void startEvaluationSession(EvaluationSession aSession, String taskFileName){

        // load tasks
        if (m_taskListCreator != null){
            aSession.setTaskList(
                    m_taskListCreator.getTaskListForSession(aSession, taskFileName)
            );
        }else {
            System.err.println("Error: no TaskListCreator was set");
        }


        m_sessionGroup.startSubSession(aSession);

        this.initLoggingForSession(aSession);

        if (m_delegate != null){
            m_delegate.prepareForEvaluationSession(aSession);
        }

        EvaluationManager.getInstance().log(
                    EvaluationResources.getString("evalframe.window.preparingFinished")
					);

        this.log("session started: " + aSession.getTitle() +" config: "+ aSession.getConfiguration());
        EvaluationManager.getInstance().continueActiveSession();

    }

    /**
     * This method should be called when from a {@link evaluation.evalBench.panel.TaskPanel}, when the
     * corresponding {@link Task} was answered and the "Next" button was clicked. Calls the delegate
     * {@link EvaluationDelegate#evaluationTaskWasAnswered(Task)}. Afterwards the next task from the current
     * {@link EvaluationSession} will be fetched and the {@link EvaluationDelegate#prepareForEvaluationTask(Task)}
     * will be called. When the delegate returns, this task will start the execution.
     *
     * If the current {@link EvaluationSession} does not provide a next task, the session is marked as finished and
     * {@link EvaluationDelegate#evaluationSessionDidFinish(EvaluationSession)} is called.
     */
    public void continueActiveSession(){

        this.log(EvaluationResources.getString("eval.continueSession"));

        if (m_sessionGroup != null ) {

            EvaluationSession expSession = m_sessionGroup.getActiveSubSession();

            if (expSession == null ){
                System.err.println("Evaluation Manager: you have to set the active session in the session group before starting the experiment!");
                return;
            }

            if (expSession.getCurrentTask() != null){

                expSession.getCurrentTask().stopTaskExecution();

                if (m_delegate != null){
                    m_delegate.evaluationTaskWasAnswered(expSession.getCurrentTask());
                }
            }

            if ( expSession.hasMoreTasks()){

                Task aTask = expSession.getNextTask();

                if(m_delegate != null){
                    m_delegate.prepareForEvaluationTask(aTask);
                }

                this.log("task started: id:" + aTask.getTaskId() +" config: "+ aTask.getConfiguration() +" task type: "+ aTask.getTaskType() + ";");

                aTask.startTaskExecution();
            }
            else {
                expSession.getNextTask();

                // set logging to default file
                // this.initLoggingForExploration();

                if (m_delegate != null){
                    m_delegate.evaluationSessionDidFinish(m_sessionGroup.getActiveSubSession());
                    // the delegate might start the next evaluation session
                    // thus it can already be running when the delegate returns 
                }
            }
        }
    }

    public boolean isResetSupported() {
		return m_resetSupported;
	}

	public void setResetSupported(boolean resetSupported) {
		this.m_resetSupported = resetSupported;
	}

    /**
     * This method should be called when from a {@link evaluation.evalBench.panel.TaskPanel}, when the
     * test person clicks the "Reset Visualization" button and calls
     * {@link EvaluationDelegate#resetGUIForEvaluationSession(EvaluationSession)}
     */
    public void resetCurrentVisualization(){
        if (m_sessionGroup.getActiveSubSession() != null && m_delegate != null && m_resetSupported){

            this.log(EvaluationResources.getString("eval.resetVis"));

            m_delegate.resetGUIForEvaluationSession(m_sessionGroup.getActiveSubSession());
        }

    }

    /**
     * log a message, will be saved either in the current directory, or if an {@link EvaluationSession} is in progress
     * to the same directory as the {@link evaluation.evalBench.io.EvaluationSessionJournal}
     * @param msg a log message
     */
	public void log(String msg) {
		logger.fatal(msg + ";");

	}
	
	/**
	 * ensure log file appender and random id are initialized. 
	 */
	private void ensureLoggingInitialized() {
        if (this.m_logfile == null) {
            m_logfile = new FileAppender();
            m_logfile.setAppend(false);
            m_logfile.setLayout(new PatternLayout(LOG_PATTERN));
            
            Logger.getRootLogger().addAppender(m_logfile);
        }
        
        if (this.m_logUniqueID == null) {
            this.m_logUniqueID = MoreMath.getRandomAlphaNum(4);
        }
	}
	
    /**
     * configure the logger for free exploration.
     * sets the log filename to log_randomId_date and directory to working directory
     */
    public void initLoggingForExploration() {
        // log4j must be configured by the delegate
        
        // first session --> initialize appender
        this.ensureLoggingInitialized();

        // determine log file name
        String date = DateHelper.getCurrentTime("yyyy-MM-dd_HH-mm-ss");
        String logFileName = "log_" + this.m_logUniqueID + "_" + date + ".log";

        m_logfile.setFile(logFileName);
        m_logfile.activateOptions();
    }

    /**
     * configure the logger for a session.
     * sets the log filename to log_participantId_date and directory to the same as the evaluation journal
     * @param aSession the session for which the logger should be configured
     */
    private void initLoggingForSession(EvaluationSession aSession) {
        // log4j must be configured by the delegate
        
        // first session --> initialize appender
        this.ensureLoggingInitialized();

        // determine log file name
        String logFileName = m_sessionGroup.getOutputManager()
                .getLogFile(aSession.getTitle(), "_interaction.log").getPath();

        m_logfile.setFile(logFileName);
       m_logfile.activateOptions();
    }

	/**
	 * closes the log file and stops interaction logging.
	 * To be used in combination with {@link #initLoggingForExploration()}.
	 */
	public void stopLogging() {
        if (this.m_logfile != null) {
        	logger.fatal(EvaluationResources.getString("eval.evaluationStopLogging"));
        	Logger.getRootLogger().removeAppender(m_logfile);
        	m_logfile.close();
        	m_logfile = null;
        }
	}
	
    /**
     * logs a shutdown message
     */
	public void shutdown() {
		logger.fatal(
                EvaluationResources.getString("eval.evaluationShutdown")
        );
	}
}
