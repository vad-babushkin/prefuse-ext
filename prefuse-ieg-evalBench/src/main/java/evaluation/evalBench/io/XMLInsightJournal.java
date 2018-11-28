package evaluation.evalBench.io;

import ieg.util.xml.XmlCalendarConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.Insight;
import evaluation.evalBench.task.InsightTask;
import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.Task;

/**
 * experiment journal for insights with XML output. This journal is optimized
 * for {@link InsightTask}s and does not store the task description with every
 * insight. All other tasks are ignored.
 * 
 * @author Rind
 */
@Deprecated
public class XMLInsightJournal implements EvaluationSessionJournal {

    // StAX tutorial see http://java.kompf.de/jaxpstreamwriter.html

    // note that SAX cannot flush between insights (& is more complicated)

    private static final String URI_NS = "http://ieg.ifs.tuwien.ac.at/projects/evalbench/insight/";
    private static final String URI_NS_XHTML = "http://www.w3.org/1999/xhtml";

    private static final String FILE_SUFFIX = "_insights.xml";

    private XmlCalendarConverter timeConv;
    private XMLStreamWriter writer = null;
    private String participantId;
    private int index = 0;

    public XMLInsightJournal(EvaluationSession aSession) {
        setSession(aSession);
    }

    @Override
    public void setSession(EvaluationSession aSession) {
        if (writer != null) {
            Logger.getLogger(XMLInsightJournal.class).warn(
                    "Output is still open.");
            return;
        }
        
        File journalFile = aSession.getSessionGroup().getOutputManager()
                .getJournalFile(aSession.getTitle(), FILE_SUFFIX);
        participantId = aSession.getSessionGroup().getParticipantId();

        try {
            open(journalFile);
        } catch (Exception e) {
            Logger.getLogger(XMLInsightJournal.class).warn(
                    "Could not open insight journal", e);
            try {
                writer.close();
            } catch (XMLStreamException e1) {
            }
            writer = null;
        }
    }

    private void open(File file) throws FileNotFoundException,
            XMLStreamException, FactoryConfigurationError,
            DatatypeConfigurationException {
        // open writer
        FileOutputStream fos = new FileOutputStream(file);
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fos,
                "UTF-8");

        // start document
        writer.setDefaultNamespace(URI_NS);
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n");
        writer.writeStartElement(URI_NS, "insights");
        if (URI_NS.length() > 0) {
            writer.writeDefaultNamespace(URI_NS);
        }
        writer.writeNamespace("html", URI_NS_XHTML);
        writer.writeCharacters("\n");
        // writer.writeStartElement(URI_NS, "participant");
        // writer.writeCharacters(participantId);
        // writer.writeEndElement();

        // reset index
        index = 0;
        timeConv = new XmlCalendarConverter();
    }

    @Override
    public void recordTask(Task aTask) {
        
        // check correct task type
        Iterator<Question> i = aTask.getQuestions().iterator();
        if (i.hasNext()) {
            Question qu = i.next();
            if (qu instanceof InsightTask) {
                Insight insight;
                insight = ((InsightTask) qu).getInsight();

                // check still open
                if (writer == null) {
                    Logger.getLogger(XMLInsightJournal.class).warn(
                            "Output closed, could not tried record insight: "
                                    + StringUtils.abbreviate(insight.getText(), 32));
                    return;
                }

                try {
                    writer.writeStartElement(URI_NS, "insight");
                    writer.writeAttribute("participant", participantId);
                    writer.writeAttribute("index", Integer.toString(index++));
                    writer.writeCharacters("\n");

                    writer.writeStartElement(URI_NS, "date");
                    writer.writeCharacters(timeConv.toXML(aTask.getFinishDate()));
                    writer.writeEndElement();
                    writer.writeCharacters("\n");

                    writer.writeStartElement(URI_NS, "text");
                    this.writeWithLineBreaks(insight.getText());
                    writer.writeEndElement();
                    writer.writeCharacters("\n");

                    for (Entry<String, Integer> e : insight.getCharacteristicsValues()
                            .entrySet()) {
                        writer.writeStartElement(URI_NS, "criterion");
                        writer.writeAttribute("key", e.getKey());
                        if (e.getValue() != null) {
                            writer.writeCharacters(Integer.toString(e.getValue()));
                        }
                        writer.writeEndElement();
                        writer.writeCharacters("\n");
                    }

                    if (aTask.getScreenshotFilename() != null) {
                        writer.writeStartElement(URI_NS, "screenshot");
                        writer.writeCharacters(aTask.getScreenshotFilename());
                        writer.writeEndElement();
                        writer.writeCharacters("\n");
                    }

                    writer.writeEndElement();
                    writer.writeCharacters("\n");
                    writer.flush();
                } catch (XMLStreamException e) {
                    Logger.getLogger(XMLInsightJournal.class).warn(
                            "Could not record insight: "
                                    + StringUtils.abbreviate(insight.getText(), 32), e);
                }
            } else {
            Logger.getLogger(XMLInsightJournal.class).info(
                    "Skipped task (not an insight)");
            }
        }
    }

    private void writeWithLineBreaks(String in) throws XMLStreamException {
        String[] lines = in.split("\n");

        writer.writeCharacters(lines[0]);

        for (int i = 1; i < lines.length; i++) {
            writer.writeEmptyElement(URI_NS_XHTML, "br");
            writer.writeCharacters("\n");
            writer.writeCharacters(lines[i]);
        }
    }

    @Override
    public void sessionFinished() {
        if (writer == null) {
            Logger.getLogger(XMLInsightJournal.class).warn(
                    "Output closed, could not close it.");
            return;
        }
        try {
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            Logger.getLogger(XMLInsightJournal.class).warn(
                    "Could not write end of document.", e);
        }
        try {
            writer.close();
        } catch (XMLStreamException e) {
            Logger.getLogger(XMLInsightJournal.class).warn(
                    "Could not close output.", e);
        }
        writer = null;
    }
}
