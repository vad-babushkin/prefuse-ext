package evaluation.evalBench.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.Task;

/**
 * experiment journal with XML output. This journal supports all {@link Question}
 * classes and stores all details that are annotated for JAXB.
 * 
 * @author Rind
 */
public class XMLJournal implements EvaluationSessionJournal {

    // combine StAX and JAXB
    private static final String URI_NS = "http://ieg.ifs.tuwien.ac.at/projects/evalbench/journal/";
    private static final String FILE_SUFFIX = "_journal.xml";

    private XMLStreamWriter writer = null;
    private Marshaller marsh = null;

    private String participantId;

    public XMLJournal(EvaluationSession aSession) {
        setSession(aSession);
    }

    @Override
    public void setSession(EvaluationSession aSession) {
        if (writer != null) {
            Logger.getLogger(XMLJournal.class).warn("Output is still open.");
            return;
        }

        File journalFile = aSession.getSessionGroup().getOutputManager()
                .getJournalFile(aSession.getTitle(), FILE_SUFFIX);
        participantId = aSession.getSessionGroup().getParticipantId();

        try {
            open(journalFile);
        } catch (Exception e) {
            Logger.getLogger(XMLJournal.class).warn(
                    "Could not open insight journal", e);
            try {
                writer.close();
            } catch (XMLStreamException e1) {
            }
            writer = null;
        }
    }

    private void open(File file) throws XMLStreamException,
            FactoryConfigurationError, FileNotFoundException {
        // open writer
        FileOutputStream fos = new FileOutputStream(file);
        writer = XMLOutputFactory.newInstance().createXMLStreamWriter(fos,
                "UTF-8");

        // start document
        writer.setDefaultNamespace(URI_NS);
        writer.writeStartDocument("UTF-8", "1.0");
        writer.writeCharacters("\n");
        writer.writeStartElement(URI_NS, "journal");
        if (URI_NS.length() > 0) {
            writer.writeDefaultNamespace(URI_NS);
        }
        writer.writeCharacters("\n");

        writer.writeStartElement(URI_NS, "participant");
        writer.writeCharacters(participantId);
        writer.writeEndElement();
    }

    @Override
    public void recordTask(Task aTask) {
        try {
            if (marsh == null) {
                JAXBContext context = JAXBContext.newInstance(Task.class);
                marsh = context.createMarshaller();
                marsh.setProperty(Marshaller.JAXB_FRAGMENT, true);
            }
            marsh.marshal(aTask, writer);
            writer.writeCharacters("\n");
            writer.flush();
        } catch (Exception e) {
            Logger.getLogger(XMLJournal.class).warn(
                    "Could not record task " + aTask.getTaskId()
                            + ". Exception: " + e.getMessage());
            for (Question question : aTask.getQuestions()) {
                Logger.getLogger(XMLJournal.class).warn(
                        "Question "
                                + question.getQuestionId() + ": "
                                + StringUtils.abbreviate(
                                        question.exportGivenAnswer(), 20));
            }
        }
    }

    @Override
    public void sessionFinished() {
        if (writer == null) {
            Logger.getLogger(XMLJournal.class).warn(
                    "Output closed, could not close it.");
            return;
        }
        try {
            writer.writeEndElement();
            writer.writeCharacters("\n");
            writer.writeEndDocument();
        } catch (XMLStreamException e) {
            Logger.getLogger(XMLJournal.class).warn(
                    "Could not write end of document.", e);
        }
        try {
            writer.close();
        } catch (XMLStreamException e) {
            Logger.getLogger(XMLJournal.class).warn("Could not close output.",
                    e);
        }
        writer = null;
        marsh = null;
    }
}
