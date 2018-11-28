package evaluation.evalBench.task;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *  Class representing a time interval selection task a user of a visualization tool has to carry out in an evaluation
 *  process.
 *  The test person is to select a start date and an end date. The correctness is calculated by comparing the
 *  selected start or end date with the corresponding correct start and end date, which has to be set before.
 *  It is also possible to set a tolerance value in milliseconds for the dates, which is used
 *
 */
public class IntervalSelectionQuestion extends Question {


    private Date m_correctIntervalStartDate;
    private Date m_correctIntervalEndDate;
    private long m_intervalTolerance;

    private Date m_answeredIntervalStartDate;
    private Date m_answeredIntervalEndDate;


    public IntervalSelectionQuestion(){

        this("", "", new Date(), new Date(), 0);
    }

    public IntervalSelectionQuestion(String aQuestionId, String aQuestionText,
                               Date aIntervalStartDate, Date aIntervalEndDate, long aIntervalTolerance){

        super(aQuestionId, aQuestionText);
        m_answeredIntervalStartDate = new Date();
        m_answeredIntervalEndDate = new Date();
        this.setIntervalAndTolerance(aIntervalStartDate, aIntervalEndDate, aIntervalTolerance);

    }

//    public IntervalSelectionQuestion(String aQuestionId, String aQuestionText, String aQuestionInstruction,
//                               Date aIntervalStartDate, Date aIntervalEndDate, long aIntervalTolerance){
//
//        super(aTaskId, aTaskDescription, aTaskInstruction);
//        m_answeredIntervalStartDate = new Date();
//        m_answeredIntervalEndDate = new Date();
//        this.setIntervalAndTolerance(aIntervalStartDate, aIntervalEndDate, aIntervalTolerance);
//    }

    public Date getAnsweredIntervalStartDate() {
        return m_answeredIntervalStartDate;
    }

    public void setAnsweredIntervalStartDate(Date aAnsweredIntervalStartDate) {
        this.m_answeredIntervalStartDate = aAnsweredIntervalStartDate;
    }

    public Date getAnsweredntervalEndDate() {
        return m_answeredIntervalEndDate;
    }

    public void setAnsweredIntervalEndDate(Date aAnsweredIntervalEndDate) {
        this.m_answeredIntervalEndDate = aAnsweredIntervalEndDate;
    }


    public void setIntervalAndTolerance(Date aIntervalStartDate, Date aIntervalEndDate, long aIntervalTolerance) {
        this.m_correctIntervalStartDate = aIntervalStartDate;
        this.m_correctIntervalEndDate = aIntervalEndDate;
        this.m_intervalTolerance = aIntervalTolerance;
    }

    @XmlElement(name = "intervalStart")
    @XmlSchemaType(name = "date")
    public Date getIntervalStartDate() {
        return m_correctIntervalStartDate;
    }


    public void setIntervalStartDate(Date aIntervalStartDate) {

        this.m_correctIntervalStartDate = aIntervalStartDate;
    }

    @XmlElement(name = "intervalEnd")
    @XmlSchemaType(name = "date")
    public Date getIntervalEndDate() {


        return m_correctIntervalEndDate;
    }

    public void setIntervalEndDate(Date aIntervalEndDate) {

        this.m_correctIntervalEndDate = aIntervalEndDate;
    }

    @XmlElement(name = "tolerance")
    public long getIntervalTolerance() {

        return m_intervalTolerance;
    }

    public void setIntervalTolerance(long aIntervalTolerance) {

        this.m_intervalTolerance = aIntervalTolerance;
    }

    @Override
    public double determineError() {
        if (m_correctIntervalStartDate != null && m_correctIntervalEndDate != null){

            long startDiff = Math.abs( m_correctIntervalStartDate.getTime() - m_answeredIntervalStartDate.getTime() );
            long endDiff   = Math.abs( m_correctIntervalEndDate.getTime() - m_answeredIntervalEndDate.getTime() );

            // check if dates are in the limit of tolerance
            if (startDiff  <= m_intervalTolerance && endDiff <= m_intervalTolerance ) {
                return 0.0;
            }
        }

        return 1.0;
    }

    @Override
    public String exportGivenAnswer(){

        SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return aSimpleDateFormat.format(m_answeredIntervalStartDate) +"-"+aSimpleDateFormat.format(m_answeredIntervalEndDate);
    }

    @Override
    public String exportCorrectAnswer(){

        SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return aSimpleDateFormat.format(m_correctIntervalStartDate)+"-"+aSimpleDateFormat.format(m_correctIntervalEndDate);
    }
    
    @Override
    public Object getGivenAnswer(){
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getCorrectAnswer(){
        throw new UnsupportedOperationException();
    }
	
	@Override
	public Question clone() {
		// TODO convert shallow copy to a deep copy
		return super.clone();
	}
}
