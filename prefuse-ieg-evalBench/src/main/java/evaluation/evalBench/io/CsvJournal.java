package evaluation.evalBench.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.Task;

/**
 * Implementation of {@link EvaluationSessionJournal}. Records a
 * {@link EvaluationSession} to a comma separated file in a directory indicating
 * the test persons id and date.
 * 
 * <p>
 * Line breaks and the field separator (";") are replaced by space.
 * 
 * @author Stephan Hoffmann, Alexander Rind
 * 
 * @see EvaluationSessionJournal
 */
public class CsvJournal implements EvaluationSessionJournal {

	private static final String SEPARATOR = ";";

    private static final String MULTI_VALUE_SEPARATOR = ",";

    private static final String[] HEADERS = { "participant", "taskIndex",
            "task", "taskType", "startTime", "duration_ms", "taskDescription",
            "questionId", "questionType", "questionText", "correctness", "error", "givenAnswer",
            "correctAnswer" };

	private static final String FILE_SUFFIX = "_journal.csv";

	protected BufferedWriter bufferedWriter;

	/**
	 * task configuration keys that will be recorded in the journal. Their keys
	 * and order are determined from the first task.
	 */
	private String[] configurationKeys;

	/**
	 * index of the current task in a session by actual execution order. This is
	 * <b>NOT</b> the task id.
	 */
	private int mTaskNumber = 0;

	/**
	 * if the next field is the first in a record. Thus, it will not be prefixed
	 * with a field separator.
	 */
	private boolean newRecord = true;

	private String participant_id = "";

    // TODO make correctness optional  -- private boolean recordCorrectness = true;

	/**
	 * Constructor for the journal
	 * 
	 * @param aSession
	 *            the session that belongs to the journal
	 */
	public CsvJournal(EvaluationSession aSession) {
		setSession(aSession);
	}

	/**
	 * sets the session for the journal, creates directories and the file with
	 * the name of the evaluation session
	 * 
	 * @param aSession
	 */
	public void setSession(EvaluationSession aSession) {

		File journalFile = aSession.getSessionGroup().getOutputManager()
				.getJournalFile(aSession.getTitle(), FILE_SUFFIX);

		mTaskNumber = 0;
		participant_id = aSession.getSessionGroup().getParticipantId();

		try {
			// Create file
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(journalFile), "UTF-8"));
			// cannot write header here because we need configuration keys
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error writing to file: " + e.getMessage());
		}
	}

	/**
	 * writes the task to the journal, along with the task index, task id, task
	 * type, start date, duration, correctness, given answer(s), correct
	 * answer(s) and all existing configurations
	 * 
	 * @param aTask
	 *            answered task
	 */
	@SuppressWarnings("deprecation")
	public void recordTask(Task aTask) {

		mTaskNumber++;

		// 1991-02-04 09:23:15
		SimpleDateFormat aSimpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");

		// write the metaline for the journal csv (first task)
		Map<String, String> configuration = aTask.getConfiguration();

		// first record --> add header
		if (mTaskNumber == 1) {
			// determined keys and order (need to stay fixed for each file)
			Set<String> set = configuration.keySet();
			configurationKeys = set.toArray(new String[set.size()]);

			try {
				for (String header : HEADERS) {
					writeField(header);
				}
				for (String header : configurationKeys) {
					writeField(header);
				}

				writeEndOfRecord();
			} catch (Exception e) {// Catch exception if any
				Logger.getLogger(CsvJournal.class).warn(
						"Error writing journal to file.", e);
			}
		}

		try {

			//loop through all the questions in the current task
			for (Question question : aTask.getQuestions()) {
				// write taskline
				writeField(participant_id);
				writeField(mTaskNumber);
				writeField(aTask.getTaskId());
				writeField(aTask.getTaskType());
				writeField(aSimpleDateFormat.format(aTask.getStartDate()));
				writeField(aTask.getExecutionTime());
				writeField(aTask.getTaskDescription());
				
				//write question specific
				writeField(question.getQuestionId());
				writeField(question.getClass().getSimpleName());
				writeField(question.getQuestionText());
				writeField(question.determineCorrectness());
				writeField(question.determineError());
				writeField(question.getGivenAnswer());
				writeField(question.getCorrectAnswer());
				
				for (String str : configurationKeys) {
					writeField(configuration.get(str));
				}
				
				writeEndOfRecord();
			}

		} catch (Exception e) {// Catch exception if any
			Logger.getLogger(CsvJournal.class).warn(
					"Error writing journal to file.", e);
		}
	}

	/**
	 * closes the file
	 */
	public void sessionFinished() {
		try {
			// Close the output stream
			bufferedWriter.close();
		} catch (IOException ignored) {
		}

		mTaskNumber = 0;
	}

	/**
	 * write a field to the CSV file. Sanitizes the string, handles
	 * <tt>null</tt>, and adds field separators.
	 * 
	 * @param fieldValue
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
    private void writeField(Object fieldValue) throws IOException {
		if (!newRecord) {
			bufferedWriter.write(SEPARATOR);
		}
		newRecord = false;

		String sanitized;
		
        // handle null, arrays (!), collections, otherwise call toString() 
		if (fieldValue == null) {
		    sanitized = "null";
		} else if (fieldValue.getClass().isArray()) {
		    sanitized = ArrayUtils.toString(fieldValue);
		} else if (fieldValue instanceof Iterable) {
		    sanitized = StringUtils.join(((Iterable) fieldValue), MULTI_VALUE_SEPARATOR);
		} else {
	        sanitized = String.valueOf(fieldValue);
		}

		// handle line breaks
		sanitized = sanitized.replaceAll("(\r\n|\n\r|\r|\n)", " ");
		// handle CSV separator
		sanitized = sanitized.replace(SEPARATOR, " ");

		// quotation marks would be added here
		bufferedWriter.write(sanitized);
	}

	private void writeEndOfRecord() throws IOException {
		bufferedWriter.newLine();
		newRecord = true;
		bufferedWriter.flush();
	}
}
