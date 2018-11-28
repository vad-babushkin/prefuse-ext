package evaluation.evalBench.task;

import javax.xml.bind.annotation.XmlElement;

/**
 * single-item post-task questionnaire with rating scale from 0 to 150 and verbal labels.
 * Initially developed by Zijlstra and van Doorn (1985), 
 * this implementation is based on Sauro and Lewis 
 * (2012, Quantifying the User Experience, p. 214). 
 * @author Alexander Rind
 */
public class SubjectiveMentalEffortQuestion extends Question {
	
	private int givenAnswer;

	@Override
	public Object getCorrectAnswer() {
		return null;
	}

	@XmlElement(required = false)
	@Override
	public Object getGivenAnswer() {
		return givenAnswer;
	}

	public void setGivenAnswer(int givenAnswer) {
		this.givenAnswer = givenAnswer;
	}
}
