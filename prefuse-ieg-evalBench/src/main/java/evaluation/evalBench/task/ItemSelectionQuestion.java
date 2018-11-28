package evaluation.evalBench.task;

import java.util.SortedSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang3.StringUtils;

public class ItemSelectionQuestion extends Question{
	private static final String SEPARATOR = ", ";
	
    protected SortedSet<String> m_answeredSelected_Item_ID = null;
	protected SortedSet<String> m_correctSelected_Item_ID = null;
	
	public ItemSelectionQuestion(){
	    super();
	}
	
    public ItemSelectionQuestion(String aQuestionId, String aQuestionText){
        super(aQuestionId, aQuestionText);
    }
    
	public ItemSelectionQuestion(String aQuestionId, String aQuestionText, SortedSet<String> correctItemIDs){
		super(aQuestionId, aQuestionText);
//		m_answeredSelected_Item_ID = new TreeSet<String>();
		m_correctSelected_Item_ID = correctItemIDs;
	}
	
	@Override
    @XmlElementWrapper(name = "givenAnswers") 
    @XmlElement(name = "givenAnswer")
	public SortedSet<String> getGivenAnswer() {
		return m_answeredSelected_Item_ID;
	}
	
	public void setGivenAnswer(SortedSet<String> answeredSelectedItemID) {
		m_answeredSelected_Item_ID = answeredSelectedItemID;
	}
	
	@XmlElementWrapper(name = "correctAnswers") 
	@XmlElement(name = "correctAnswer")
	public SortedSet<String> getCorrectAnswer() {
		return m_correctSelected_Item_ID;
	}
	
	public void setCorrectAnswer(SortedSet<String> mCorrectSelectedItemID) {
		m_correctSelected_Item_ID = mCorrectSelectedItemID;
	}
	
	@Override
	public String exportCorrectAnswer() {
	    return StringUtils.join(m_correctSelected_Item_ID, SEPARATOR);
	}

	@Override
	public String exportGivenAnswer() {
        return StringUtils.join(m_answeredSelected_Item_ID, SEPARATOR);
	}
	
	@Override
	public Question clone() {
		// TODO convert shallow copy to a deep copy
		return super.clone();
	}
}
