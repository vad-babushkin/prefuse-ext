package evaluation.evalBench.task;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author David Bauer, Alexander Rind
 * 
 */
public class TextInputQuestion extends Question {
    
    // TODO property ignore case?

    private String m_correctAnswer = null;
	private String m_givenAnswer = null;
	private boolean m_singleLine = true;

	private String m_regEx = null;

	public TextInputQuestion() {
		this("", "");
	}

	/**
	 * @param aQuestionId
	 * @param aQuestionText
	 */
	public TextInputQuestion(String aQuestionId, String aQuestionText) {
		super(aQuestionId, aQuestionText);
	}

    @Override
    @XmlElement
	public String getCorrectAnswer() {
        return m_correctAnswer;
    }

    public TextInputQuestion(String aQuestionId, String aQuestionText,
            String correctAnswer, boolean singleLine, String regEx) {
        super(aQuestionId, aQuestionText);
        this.m_correctAnswer = correctAnswer;
        this.m_singleLine = singleLine;
        this.m_regEx = regEx;
    }

    public void setCorrectAnswer(String m_correctAnswer) {
        this.m_correctAnswer = m_correctAnswer;
    }

    @Override
    @XmlElement
    public String getGivenAnswer() {
        return m_givenAnswer;
    }

    /**
     * @param givenAnswer
     *            - The string that was written by the user
     */
    public void setGivenAnswer(String givenAnswer) {
		this.m_givenAnswer = givenAnswer;
	}

	/**
	 * @param regEx
	 *            - sets the regular expression
	 */
	public void setRegEx(String regEx) {
		m_regEx = regEx;
	}

	/**
	 * @return gets the regular expression
	 */
	public String getRegEx() {
		return m_regEx;
	}

	/**
	 * @param value
	 *            - TRUE for a single-lined textbox, FALSE for a multi-lined
	 *            textfield
	 */
	public void setSingleLine(boolean value) {
		m_singleLine = value;
	}

	/**
	 * @return singleLine - If this value is TRUE a single-lined textbox will be
	 *         presented to the user
	 */
	@XmlElement (defaultValue = "true")
	public boolean getSingleLine() {
		return m_singleLine;
	}

}
