package evaluation.evalBench.session;

import java.util.ArrayList;
import java.util.Hashtable;

import evaluation.evalBench.EvaluationManager;
import evaluation.evalBench.io.EvaluationSessionJournal;
import evaluation.evalBench.task.Task;

/**
 * EvaluationSession is responsible for managing a single evaluation session.
 * It holds an array of {@link Task} instances and the corresponding configurations like
 * data set, visualization type ..
 * It is possible to set the order of the task list to random or sequential (in the order the tasks were added)
 *
 * This class is also responsible for managing the journal for this session
 * (i.e. when was a task started, how long was the duration for that task,
 * what the test person did answer,.. )
 *
 *
 */
public class EvaluationSession {

    private ArrayList<Task> m_taskList;
    private ArrayList<Task> m_finishedTaskList;
    private String          m_title;

    private Task            m_currentTask;
    private Hashtable<String, String> m_configuration;

    private EvaluationSessionGroup  m_sessionGroup = null;

    private EvaluationSessionJournal m_journal;

    private boolean m_sessionCompleted;
    private boolean m_randomOrder;

    /**
     * Constructor taking a session titles
     * @param sessionTitle
     */
    public EvaluationSession(String sessionTitle){

        super();

        m_title = sessionTitle;
        m_taskList = new ArrayList<Task>();
        m_finishedTaskList = new ArrayList<Task>();
        m_configuration = new Hashtable<String,String>();
        m_sessionCompleted = false;

        m_randomOrder = false;
    }

    /**
     *
     * @return the {@link EvaluationSessionJournal} of this session
     */
    public EvaluationSessionJournal getJournal() {
        return m_journal;
    }


    /**
     * set the {@link EvaluationSessionJournal} of this session
     * @param aJournal
     */
    public void setJournal(EvaluationSessionJournal aJournal) {
        this.m_journal = aJournal;
    }


    /**
     * triggers the creation of a journal
     */
    public void startSession(){
        m_sessionCompleted = false;
         m_journal = EvaluationManager.getInstance().getJournalForSession(this); 
    }

    /**
     *
     * @return true if the tasks are executed in random order, false if sequential
     */
    public boolean isRandomOrder() {
        return m_randomOrder;
    }

    /**
     * set the order of task execution (random or sequential)
     * @param m_randomOrder
     */
    public void setRandomOrder(boolean m_randomOrder) {
        this.m_randomOrder = m_randomOrder;
    }

    /**
     *
     * @return the title of the session
     */
    public String getTitle() {
        return m_title;
    }

    /**
     * set the title for the session
     * @param title session title
     */
    public void setTitle(String title) {
        m_title = title;
    }

    /**
     * configurations like data set, visualization type ..
     * @return a {@link java.util.Hashtable} with the configuration in value/key pairs
     */
    public Hashtable<String,String> getConfiguration() {
        return m_configuration;
    }

    /**
     * set configurations like data set, visualization type ..
     * @param aConfiguration
     */
    public void setConfiguration(Hashtable<String,String> aConfiguration) {
        this.m_configuration = aConfiguration;
    }

    /**
     *
     * @return the evaluation session group
     */
    public EvaluationSessionGroup getSessionGroup() {
        return m_sessionGroup;
    }

    /**
     * set the evaluation session group
     * @param sessionGroup
     */
    void setSessionGroup(EvaluationSessionGroup sessionGroup) {
        this.m_sessionGroup = sessionGroup;
    }

    /**
     * add a single task to the session
     * @param aTask
     */
    public void addTask(Task aTask){
        m_taskList.add(aTask);
        m_sessionCompleted = false;
    }

    public void addTask(Task aTask, int idx){
        m_taskList.add(idx, aTask);
        m_sessionCompleted = false;
    }

    /**
     * set an array list of tasks for this session
     * previously added tasks are deleted
     * @param taskList
     */
    public void setTaskList(ArrayList<Task> taskList){
        if (taskList!=null){
            this.m_taskList = taskList;
            m_sessionCompleted = false;
        }
    }

    /**
     * records the current task to the journal and adds it to the finished task list
     * currentTask is set to null
     */
    private void setCurrentTaskFinished(){

        if (m_currentTask != null){

            if (m_journal == null){
                m_journal = EvaluationManager.getInstance().getJournalForSession(this);
            }

            m_journal.recordTask(m_currentTask);
            m_finishedTaskList.add(m_currentTask);
            m_currentTask = null;
        }

    }

    /**
     * removes the given task from the unfinished tasklist and sets it as current task
     * @param aTask
     */
    private void setCurrentTask(Task aTask){
        int taskIndex = m_taskList.indexOf(aTask);

        if (taskIndex != -1){

            m_taskList.remove(taskIndex);

            m_currentTask = aTask;
        }
    }

    /**
     * provides the currently active task
     * @return
     */
    public Task getCurrentTask(){
        return m_currentTask;
    }

    /**
     *
     * @return true, if more tasks are available, false if every task was already finished
     */
    public boolean hasMoreTasks(){
        return !m_taskList.isEmpty();
    }

    /**
     * Removes the first task from the list of unfinished tasks. This is similar
     * to {@link #getNextTask()} without the side-effect of setting the current
     * task. Neither does it finish the session. The returned task can
     * conditionally be reinserted by {@link #addTask(Task, int)}.
     *
     * @return the skipped task
     */
    public Task extractNextTask() {
        //        setCurrentTaskFinished();
        if (hasMoreTasks()) {
            Task aTask = m_taskList.get(0);
            m_taskList.remove(0);
            return aTask;
        } else {
            return null;
        }
    }

    /**
     * get the next task from the list. the order is either sequential or randomized
     * @return null if no task is available (every task was finished)
     */
    public Task getNextTask(){

        setCurrentTaskFinished();

        if (this.hasMoreTasks()){

            if (m_randomOrder) {

                // pick a random task
                java.util.Random random = new java.util.Random();
                int index = random.nextInt(m_taskList.size());
                Task aTask = m_taskList.get(index);
                this.setCurrentTask(aTask);
                return aTask;

            }else {

                // pick the task on index 0
                Task aTask = m_taskList.get(0);
                this.setCurrentTask(aTask);
                return aTask;
            }


        }

        // no task available, session is completed

        m_sessionCompleted = true;
        m_journal.sessionFinished();

        return null;
    }

    /**
     *
     * @return true, if every task is finished
     */
    public boolean isSessionCompleted() {

        return m_sessionCompleted;
    }

	public int getTotalTaskCount() {
		return m_taskList.size() + m_finishedTaskList.size() + (m_currentTask != null ? 1 : 0);
	}

	/**
	 * zero based.
	 * @return
	 */
	public int getCurrentTaskIndex() {
		return m_finishedTaskList.size();
	}


    @Override
    public String toString() {
        return m_title;
    }
}
