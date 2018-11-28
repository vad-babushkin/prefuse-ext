package evaluation.evalBench.task;

import java.util.Date;

/**
 * @author David Bauer, Alexander Rind
 * 
 */
import javax.xml.bind.annotation.XmlElement;

public class DateQuestion extends Question {

    private Date m_correctAnswer = null;
	private Date m_givenAnswer = null;

	public DateQuestion() {
		this("", "");
	}

	public DateQuestion(String aQuestionId, String aQuestionText) {
		super(aQuestionId, aQuestionText);
	}

    @Override
    @XmlElement
    public Date getCorrectAnswer() {
        return m_correctAnswer;
    }

    public void setCorrectAnswer(Date m_correctAnswer) {
        this.m_correctAnswer = m_correctAnswer;
    }

    @Override
    @XmlElement
    public Date getGivenAnswer() {
        return m_givenAnswer;
    }

    public void setGivenAnswer(Date m_givenAnswer) {
        this.m_givenAnswer = m_givenAnswer;
    }
	
	@Override
	public Question clone() {
		DateQuestion clone = (DateQuestion) super.clone();
		
		if (m_correctAnswer != null) {
			clone.m_correctAnswer = new Date(m_correctAnswer.getTime());
		}
		
		if (m_givenAnswer != null) {
			clone.m_givenAnswer = new Date(m_givenAnswer.getTime());
		}
		
		return clone;
	}
}
