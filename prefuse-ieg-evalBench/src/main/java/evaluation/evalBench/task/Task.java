package evaluation.evalBench.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstract base class representing a task a user of a visualization tool has to carry out in an evaluation process.
 *
 * Every subclass should provide  <a href="https://jaxb.dev.java.net">jaxb</a> annotations for XML unmarshalling
 *
 * @author Stephan Hoffmann, Alexander Rind
 */
@XmlRootElement
@XmlType(name="taskType", propOrder={})
@XmlSeeAlso(value = { SubjectiveMentalEffortQuestion.class })
public class Task implements Cloneable{

    // task definition
    private String    m_taskId;
    private String    m_taskType;

    private String    m_taskDescription;
    private String    m_taskInstruction;

    private ArrayList<Question> m_questions;

    // task execution
    private Date      m_startDate;
    private Date      m_finishDate;
    private Integer   m_runningIndex;
    private String    m_screenshot; 

    private Hashtable<String,String> m_configuration;
    private Configuration m_config_unprocessed;

    /**
     * Standard constructor. Initializes Task with empty id and description
     */
    protected Task(){
        this("", "");
    }

    /**
     * Constructor for task with given id and description
     * @param aTaskId           unique task id
     * @param aTaskDescription  task description visible to test person
     */
    protected Task(String aTaskId, String aTaskDescription){
        this(aTaskId, aTaskDescription, "");
    }

    /**
     * Constructor for task with given id, description and instruction
     * @param aTaskId           unique task id
     * @param aTaskDescription  task description visible to test person
     * @param aTaskInstruction  additional task instruction for the test person
     */
    public Task(String aTaskId, String aTaskDescription, String aTaskInstruction){
        this.m_taskId = aTaskId;
        this.m_taskDescription = aTaskDescription;
        this.m_taskInstruction = aTaskInstruction;
        this.m_configuration = new Hashtable<String, String>();
        this.m_taskType = "";
    }

    /**
     * Get the configurations for this task. A configuration could e.g. be the visualization type or the dataset which
     * be used with this task. The configurations are key value pairs.
     * @return a Hashtable containing the configurations for this task as key value pairs
     */
    public Hashtable<String,String> getConfiguration() {
    	
        return m_configuration;
    }
    
   
    

    /**
     * Set the configurations for this task. A configuration could e.g. be the visualization type or the dataset which
     * be used with this task. The configurations are key value pairs.
     * @param aConfiguration a Hashtable containing the configurations for this task as key value pairs
     */
    public void setConfiguration(Hashtable<String,String> aConfiguration) {
        this.m_configuration = aConfiguration;
    }

    // task-definition getters & setters
    
  
    @XmlElement(name = "task-config", type = Configuration.class)
   	public Configuration getConfigurationUnprocessed() {
    	    	
   		return m_config_unprocessed;
   	}
       
       
    public void setConfigurationUnprocessed(Configuration config) {
    	   
   		this.m_config_unprocessed = config;
   	}

    /**
     * Get the task type
     * @return a string representing the task type
     */
    @XmlElement(name = "task-type")
    public String getTaskType() {
    	
        return m_taskType;
    }

    /**
     * Set the task type
     * @param m_taskType a string representing the task type
     */
    public void setTaskType(String m_taskType) {
    	
        this.m_taskType = m_taskType;
    }

    /**
     * Get the task id
     * @return a unique string
     */
    @XmlAttribute(name = "id", required=true)
    public String getTaskId() {
        return m_taskId;
    }

    /**
     * Set the task id
     * @param m_taskId a unique string
     */
    public void setTaskId(String m_taskId) {
        this.m_taskId = m_taskId;
    }

    /**
     * Get the task description which is displayed to the test person
     * @return a string which contains the task description which is displayed to the test person
     */
    @XmlElement(name = "task-description")
    public String getTaskDescription() {
        return m_taskDescription;
    }

    /**
     * Set the task description which is displayed to the test person
     * @param m_taskDescription a string which contains the task description which is displayed to the test person
     */
    public void setTaskDescription(String m_taskDescription) {
        this.m_taskDescription = m_taskDescription;
    }

    /**
     * Get the task instructions which are displayed to the test person additionally to the task description
     * @return a string which contains the task instructions which are displayed to the test person
     */
    @XmlElement(name = "task-instruction")
    public String getTaskInstruction() {
        return m_taskInstruction;
    }

    /**
     * Set the task instructions which are displayed to the test person additionally to the task description
     * @param m_taskInstruction a string which contains the task instructions which are displayed to the test person
     */
    public void setTaskInstruction(String m_taskInstruction) {
        this.m_taskInstruction = m_taskInstruction;
    }

    @XmlElementWrapper(name = "questions", required=true)
    @XmlElements({
            @XmlElement(name = "question", type = Question.class),
            @XmlElement(name = "choice-selection", type = ChoiceSelectionQuestion.class),
            // @XmlElement(name = "interval_selection", type = IntervalSelectionTask.class),
            @XmlElement(name = "quantitative", type = QuantitativeQuestion.class),
            @XmlElement(name = "text-input", type = TextInputQuestion.class),
            @XmlElement(name = "date", type = DateQuestion.class),
            @XmlElement(name = "likert", type = LikertskalaQuestion.class),
            // @XmlElement(name = "insight", type = InsightTask.class),
            @XmlElement(name = "item-selection", type = ItemSelectionQuestion.class),
            @XmlElement(name = "yesno", type = YesNoQuestion.class) })
    public ArrayList<Question> getQuestions() {
        return m_questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.m_questions = questions;
    }
    
    public void setQuestion(Question question) {
        this.m_questions = new ArrayList<Question>(1);
        m_questions.add(question);
    }

    // task-execution getters & setters

    /**
     * Get the index of the task. This can be used to determine the index of the task in a list of task for logging
     * @return the index of the task
     */
    // TODO running index is never used
    public Integer getRunningIndex() {
        return m_runningIndex;
    }

    /**
     * Set the index of the task. This can be used to determine the index of the task in a list of task for logging
     * @param runningIndex the index of the task
     */
    public void setRunningIndex(Integer runningIndex) {
        this.m_runningIndex = runningIndex;
    }

    /**
     * @return the date at which the task was started
     */
    public Date getStartDate(){
        return m_startDate;
    }

    /**
     * Set the date at which the task was started, which is used to determine the duration of the task
     * @param aStartDate the date at which the task was started
     */
    public void setStartDate(Date aStartDate){
        this.m_startDate = aStartDate;
    }

    /**
     * @return the date at which the task was finished
     */
    public Date getFinishDate(){
        return m_finishDate;
    }

    /**
     * Set the date at which the task was finished, which is used to determine the duration of the task
     * @param aFinishDate the date at which the task was finished
     */
    public void setFinishDate(Date aFinishDate){
        this.m_finishDate = aFinishDate;
    }

    // task execution time

    /**
     * Sets the start date for a task to the current time
     * @return start date: the date at which the task was started
     */
    public Date startTaskExecution(){
        this.m_startDate = new Date();
        return m_startDate;

    }

    /**
     * sets the finish date for a task to the current time
     * @return finish date: the date at which the task was finished
     */
    public Date stopTaskExecution(){
        this.m_finishDate = new Date();
        return m_finishDate;
    }

    /**
     * get the time span between start and end of task
     * @return the number of milliseconds between start and end of task
     */
    public long getExecutionTime(){

        if (m_finishDate != null && m_startDate != null){
            return this.m_finishDate.getTime() - this.m_startDate.getTime();
        }

        return 0;
    }

    /**
     * @return the m_screenshot
     */
    public String getScreenshotFilename() {
        return m_screenshot;
    }

    /**
     * @param m_screenshot the m_screenshot to set
     */
    public void setScreenshotFilename(String m_screenshot) {
        this.m_screenshot = m_screenshot;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("id", m_taskId).
                append("type", m_taskType).
                append("question-count", (m_questions == null) ? "null" : m_questions.size()).
                toString();
    }
    
    @Override
    public Task clone() {
    	try {
			Task clone = (Task) super.clone();
			
//		    private String    m_taskId;
//		    private String    m_taskType;
//		    private String    m_taskDescription;
//		    private String    m_taskInstruction;
//		    private Integer   m_runningIndex;
//		    private String    m_screenshot;
//			=> Immutable

//		    private ArrayList<Question> m_questions;
		    if (this.m_questions != null) {
				clone.m_questions = new ArrayList<Question>(m_questions.size()); 
				for (Question quest : m_questions) {
					clone.m_questions.add(quest.clone());
				}
		    }

//		    private Date      m_startDate;
		    if (this.m_startDate != null) {
			    clone.m_startDate = new Date(m_startDate.getTime());
		    }
		    
//		    private Date      m_finishDate;
		    if (this.m_finishDate != null) {
			    clone.m_finishDate = new Date(m_finishDate.getTime());
		    }
		    
//		    private Hashtable<String,String> m_configuration;
		    if (this.m_configuration != null) {
		        clone.m_configuration = new Hashtable<String, String>();
		        for (Entry<String, String> pair : m_configuration.entrySet()) {
		        	clone.m_configuration.put(pair.getKey(), pair.getValue());
		        }
		    }
		    
//		    private Configuration m_config_unprocessed;
		    if (this.m_config_unprocessed != null) {
		    	clone.m_config_unprocessed = m_config_unprocessed.clone();
		    }

	    	return clone;
		} catch (CloneNotSupportedException e) {
            throw new Error("This should not occur since we implement Cloneable");
        }
    }
}
