package evaluation.evalBench.io;

import ieg.util.xml.JaxbMarshaller;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.ChoiceSelectionQuestion;
import evaluation.evalBench.task.InsightTask;
import evaluation.evalBench.task.IntervalSelectionQuestion;
import evaluation.evalBench.task.ItemSelectionQuestion;
import evaluation.evalBench.task.QuantitativeQuestion;
import evaluation.evalBench.task.Task;

@Deprecated
public class JaxbJournal implements EvaluationSessionJournal {

    private static final String FILE_SUFFIX = "_journal.xml";

    private int taskIndex;

    private String filePrefix;

    private Journal journal = new Journal();

    /**
     * Constructor for the journal
     * @param aSession the session that belongs to the journal
     */
    public JaxbJournal(EvaluationSession aSession) {
        setSession(aSession);
    }

    @Override
    public void recordTask(Task aTask) {
        String path = filePrefix + taskIndex + FILE_SUFFIX;
        taskIndex++;
        
        journal.tasks.clear();
        journal.tasks.add(aTask);
        JaxbMarshaller.save(path, journal);
    }

    @Override
    public void sessionFinished() {
        // nothing to do here
    }

    @Override
    public void setSession(EvaluationSession aSession) {
        journal.participant_id = aSession.getSessionGroup().getParticipantId();
        taskIndex = 0;
        
        filePrefix = aSession.getSessionGroup().getOutputManager()
                .getJournalFile(aSession.getTitle(), "_").getPath();
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.NONE)
    private static class Journal {

        @XmlElement
        String participant_id = "";

        @XmlElements({
                @XmlElement(name = "choice-selection", type = ChoiceSelectionQuestion.class),
                @XmlElement(name = "interval-selection", type = IntervalSelectionQuestion.class),
                @XmlElement(name = "quantitative", type = QuantitativeQuestion.class),
                @XmlElement(name = "insight", type = InsightTask.class),
                @XmlElement(name = "item-selection", type = ItemSelectionQuestion.class) })
        LinkedList<Task> tasks = new LinkedList<Task>();

    }
}
