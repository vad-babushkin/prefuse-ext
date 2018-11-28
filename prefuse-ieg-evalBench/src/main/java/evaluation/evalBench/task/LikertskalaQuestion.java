package evaluation.evalBench.task;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author David Bauer, Alexander Rind
 * 
 */
public class LikertskalaQuestion extends Question {

	private Integer m_givenAnswer = null;

    private Integer m_correctAnswer = null;
    
    /**
     * label for the left-most option on the likert scale. If <tt>null</tt> a
     * generic text should be used.
     */
    private String m_leftLabel = null;

    /**
     * label for the right-most option on the likert scale. If <tt>null</tt> a
     * generic text should be used.
     */
    private String m_rightLabel = null;
    
	private int m_countOptions;

	public LikertskalaQuestion() {
		this("", "", "low", "high", 4);
	}

	/**
	 * @param aQuestionId
	 * @param aQuestionText
	 * @param leftLabel
	 * @param aRightLabel
	 * @param countOptions
	 */
	public LikertskalaQuestion(String aQuestionId, String aQuestionText,
			String leftLabel, String rightLabel, int countOptions) {
		super(aQuestionId, aQuestionText);

		m_givenAnswer = null;
		m_leftLabel = leftLabel;
		m_rightLabel = rightLabel;
		m_countOptions = countOptions;
	}

    @Override
    @XmlElement
	public Integer getGivenAnswer() {
        return m_givenAnswer;
    }

    public void setGivenAnswer(Integer givenAnswer) {
        this.m_givenAnswer = givenAnswer;
    }


    @Override
    @XmlElement
    public Integer getCorrectAnswer() {
        return m_correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.m_correctAnswer = correctAnswer;
    }

	/**
	 * @return countOptions - Number of options (JRadioButtons) that are
	 *         available to choose from
	 */
    @XmlElement (name = "count-options", required = true)
	public int getCountOptions() {
		return m_countOptions;
	}

	public void setCountOptions(int countOptions) {
        this.m_countOptions = countOptions;
    }

    /**
	 * @return a String that is printed at the left of the scale
	 */
	@XmlElement (name = "left-label")
	public String getLeftLabel() {
		return m_leftLabel;
	}

    public void setLeftLabel(String leftLabel) {
        this.m_leftLabel = leftLabel;
    }

	/**
	 * @return a String that is printed at the right of the scale
	 */
    @XmlElement (name = "right-label")
	public String getRightLabel() {
		return m_rightLabel;
	}

    public void setRightLabel(String rightLabel) {
        this.m_rightLabel = rightLabel;
    }
}
