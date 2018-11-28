package evaluation.evalBench.task;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstract base class representing a question a user of a visualization tool has to answer in an evaluation process.
 * It is typically used with a {@link Task}. 
 *
 * Every subclass should provide  <a href="https://jaxb.dev.java.net">jaxb</a> annotations for XML unmarshalling
 *
 * @author Stephan Hoffmann, Alexander Rind, David Bauer
 */
@XmlType(name="questionType")
public abstract class Question implements Cloneable {
    
    // TODO optional question

    private String    m_questionId;

    private String    m_questionText;
    
    private boolean m_allowIndentLayout = false;

    /**
     * get the correct answer(s) for this question. usually used for logging or journal
     * @return the correct answer(s) for this task
     */
    public abstract Object getCorrectAnswer();

    /**
     * get the given answer(s) of the test person. usually used for logging or journal
     * @return the answer(s) of the test person for this question
     */
    public abstract Object getGivenAnswer();
    
    /**
     * determines if there is a correct answer to this question (e.g., lookup task).
     * @return false, if there is no correct answer (e.g., subjective preference).
     */
    public boolean hasGroundTruth() {
        return getCorrectAnswer() != null;
    }
    
    /**
     * <b>Deprecated: use {@link #determineError()} instead.</b>
     * @return <em>1.0</em> for a correct answer or a <em>value smaller than 1.0</em> for a wrong answer.
     * Typically, a wrong answer results in <em>0.0</em> but other behaviors are possible.
     */
    @Deprecated
    public double determineCorrectness() {
        return (determineError() - 1.0) * -1.0;
    }

    /**
     * determines if the question was answered correctly and, if not, measure the error.
     * Typically, a wrong answer results in <em>1.0</em> but each subclass may have a particular range.
     * As a convention, subclasses should return <em>1.0</em>, if either no correct answer or no given answer are set,
     * but callers should refer to {@link #hasGroundTruth()} and {@link #getGivenAnswer()} instead.
     * @return <em>0.0</em> for a correct answer or a <em>positive value</em> for a wrong answer.
     */
    public double determineError() {
        return (getCorrectAnswer() != null 
                && getCorrectAnswer().equals(getGivenAnswer())) ? 0.0 : 1.0;
    }

    /**
     * get the given answer(s) of the test person as a String. usually used for logging or journal
     * @return a String representing the answer(s) of the test person for this question
     */
    public String exportGivenAnswer() {
        return String.valueOf(getGivenAnswer());
    }

    /**
     * get the correct answer(s) for this question as a String. usually used for logging or journal
     * @return a String representing the correct answer(s) for this question
     */
    public String exportCorrectAnswer() {
        return String.valueOf(getCorrectAnswer());
    }

    /**
     * Standard constructor. Initializes question with empty id and text
     */
    protected Question(){
        this("", "");
    }

    /**
     * Constructor for question with given id and text.
     * @param aQuestionId       question id (should be unique)
     * @param aQuestionText     question which is displayed to the test person
     */
    protected Question(String aQuestionId, String aQuestionText){
        m_questionId = aQuestionId;
        m_questionText = aQuestionText;
    }

    // question definition getters & setters

    /**
     * Get the question id (should be unique).
     * 
     * @return the id of the question
     */
    @XmlAttribute(name = "id", required = true)
    public String getQuestionId() {
        return m_questionId;
    }

    /**
     * Set the question id (should be unique).
     * @param questionId the id of the question
     */
    public void setQuestionId(String questionId) {
        this.m_questionId = questionId;
    }

    /**
     * Get the question which is displayed to the test person
     * @return a string which contains the question which is displayed to the test person
     */
    @XmlElement(name = "question-text", required = true)
    public String getQuestionText() {
        return m_questionText;
    }

    /**
     * Set the question which is displayed to the test person
     * @param questionText a string which contains the question which is displayed to the test person
     */
    public void setQuestionText(String questionText) {
        this.m_questionText = questionText;
    }
    
    public boolean isAllowIndentLayout() {
        return m_allowIndentLayout;
    }

    @XmlAttribute(name = "allow-indent-layout", required = false)
    public void setAllowIndentLayout(boolean m_allowIndentLayout) {
        this.m_allowIndentLayout = m_allowIndentLayout;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("id", m_questionId).
                append("type", m_questionText).
                toString();
    }
    
    @Override
    public Question clone() {
    	try {
    		Question clone = (Question) super.clone();
    		// only immutable fields
	    	return clone;
		} catch (CloneNotSupportedException e) {
            throw new Error("This should not occur since we implement Cloneable");
        }
    }
}
