package evaluation.evalBench.task;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author David Bauer, Alexander Rind
 * 
 */
public class YesNoQuestion extends Question {

    private Boolean m_correctAnswer = null;
	private Boolean m_givenAnswer = null;

	public YesNoQuestion() {
		this("", "");
	}

	/**
	 * @param aQuestionId
	 * @param aQuestionText
	 */
	public YesNoQuestion(String aQuestionId, String aQuestionText) {
		super(aQuestionId, aQuestionText);
	}

    public YesNoQuestion(String aQuestionId, String aQuestionText,
            Boolean correctAnswer) {
        super(aQuestionId, aQuestionText);
        this.m_correctAnswer = correctAnswer;
    }

    @Override
    @XmlElement
    public Boolean getCorrectAnswer() {
        return m_correctAnswer;
    }

    public void setCorrectAnswer(Boolean correctAnswer) {
        this.m_correctAnswer = correctAnswer;
    }

    @Override
    @XmlElement
    public Boolean getGivenAnswer() {
        return m_givenAnswer;
    }

    /**
     * @param givenAnswer
     *            - The answer selected by the user
     */
    public void setGivenAnswer(Boolean givenAnswer) {
        this.m_givenAnswer = givenAnswer;
    }
}
