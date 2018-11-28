package evaluation.evalBench.panel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;

import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.Task;

public class TaskDialog {

    private JFrame tmpFrame;

    public TaskDialog(Rectangle hiddenArea) {
        initTmpFrame(hiddenArea);
    }

    private void initTmpFrame(Rectangle hiddenArea) {
        this.tmpFrame = new JFrame();
        tmpFrame.setAlwaysOnTop(true);
        tmpFrame.setResizable(false);
        if (hiddenArea == null) {
            tmpFrame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        } else {
            tmpFrame.setBounds(hiddenArea);
        }
        tmpFrame.setVisible(false);
    }

    private JTextPane prepareTextPane(String text) {
        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setBackground(tmpFrame.getBackground());
        textPane.setSize(550, Short.MAX_VALUE);

        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        Font font = UIManager.getFont("Label.font");
        String bodyRule = "body { font-family: " + font.getFamily() + "; "
                + "font-size: " + font.getSize() + "pt; "
                + "text-align: justify;}" // width: 550px; }";
                + "td {padding: 3px 2px 0px 0px; margin: 0px; };";
        ((HTMLDocument) textPane.getDocument()).getStyleSheet().addRule(
                bodyRule);

        textPane.setText(text);

        return textPane;
    }

    /**
     * shows a dialog to inform about the start of a new session. Optionally
     * recommends to make s break.
     * 
     * @param session
     * @param recommendBreak
     */
    public void announceSession(EvaluationSession session,
            boolean recommendBreak) {
        String sessionText = String.format(
                EvaluationResources.getString("taskdialog.startSession"),
                session.getTitle());
        // sessionText +=
        // "\nDatenset: "+aDataSet.getFileName()+", Config: "+aConfigSet.getConfigFileName();
        if (recommendBreak) {
            sessionText += "<p>"
                    + EvaluationResources
                            .getString("taskdialog.recommendBreak");
        }
        tmpFrame.setVisible(true);
        JTextPane freeSizeText = prepareTextPane(sessionText);
        JOptionPane.showMessageDialog(tmpFrame, freeSizeText);
        // tmpFrame.setVisible(false);
    }

    /**
     * shows the description of the {@link Task} so that
     * @param aTask
     */
    public void showDescription(Task aTask) {
        tmpFrame.setVisible(true);

        String text = aTask.getTaskDescription() + "<p>"
                + aTask.getTaskInstruction();

        JTextPane task_descr = prepareTextPane(text);

        task_descr.setSize(new Dimension(550, Short.MAX_VALUE));
        Dimension preferredSize = task_descr.getPreferredSize();
        System.out.println("# " + task_descr.getPreferredSize());
        task_descr.setPreferredSize(new Dimension(550, preferredSize.height));

        JOptionPane.showMessageDialog(tmpFrame, task_descr,
                EvaluationResources.getString("taskdialog.task.title"), 
                JOptionPane.QUESTION_MESSAGE);

        tmpFrame.setVisible(false);
    }

    /**
     * shows the given and correct answers to all {@link Question}s as well as
     * the execution time. If any answer was incorrect, the user can choose if
     * they want to repeat the {@link Task}.
     * 
     * @param aTask
     * @return true if any answer was incorrect and the user chose to repeat
     */
    public boolean showFeedback(Task aTask) {
        boolean allCorrect = true;
        StringBuilder msg = new StringBuilder("<table>");

        for (Question question : aTask.getQuestions()) {
            msg.append("<tr><td>");
            msg.append(EvaluationResources.getString("taskdialog.feedback.correctAnswer"));
            msg.append("</td><td>");

            if (question.hasGroundTruth()) {
                allCorrect = allCorrect && (question.determineError() == 0.0);
                msg.append(question.exportCorrectAnswer());
            } else {
                msg.append(EvaluationResources.getString("taskdialog.feedback.noGroundTruth"));
            }

            msg.append("</td></tr><tr><td>");
            msg.append(EvaluationResources.getString("taskdialog.feedback.givenAnswer"));
            msg.append("</td><td>");
            msg.append(question.exportGivenAnswer());
            msg.append("</td></tr>");
        }

        long minutes = (aTask.getExecutionTime() / 1000) / 60;
        long seconds = (aTask.getExecutionTime() / 1000) % 60;
        long millisec = (aTask.getExecutionTime()) % 1000;
        msg.append("<tr><td>");
        msg.append(EvaluationResources.getString("taskdialog.feedback.executionTime"));
        msg.append("</td><td>");
        msg.append(String.format(
                EvaluationResources.getString("taskdialog.feedback.time"),
                minutes, seconds, millisec));
        msg.append("</td></tr>");
        msg.append("</table>");

        if (allCorrect) {
            msg.insert(0, EvaluationResources.getString("taskdialog.feedback.correct.msg"));
            JTextPane freeSizeText = prepareTextPane(msg.toString());
            JOptionPane.showMessageDialog(tmpFrame, freeSizeText,
                    EvaluationResources.getString("taskdialog.feedback.correct.title"),
                    JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else {
            Object[] options = {
                    EvaluationResources.getString("taskdialog.feedback.repeat.yes"),
                    EvaluationResources.getString("taskdialog.feedback.repeat.no") };
            msg.insert(0, EvaluationResources.getString("taskdialog.feedback.wrong.msg"));
            msg.append("<p>");
            msg.append(EvaluationResources.getString("taskdialog.feedback.repeat.question"));
            JTextPane freeSizeText = prepareTextPane(msg.toString());
            int n = JOptionPane.showOptionDialog(tmpFrame, freeSizeText,
                    EvaluationResources.getString("taskdialog.feedback.wrong.title"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,
                    options, options[1]);
            return (n == 0);
        }
    }

/*
    public static void main(String[] arg) {
        java.util.Locale.setDefault(java.util.Locale.US);
        Task t = new Task(
                "1",
                "alex dfg defg dfg wefd sfd dsvd sf sf sf sf csf swf sf sfw sf sfdsv vxdv scvev dgv dsfve sdfv svce sd dfv dvd c frc dvf c f dffvbrc  rb ceved dsvevd dvew dv                              dvdv dvdv s",
                "asdfsss fsdfs sfwscsac scfs sacw xcwcx swdcs sc scvwsx f dfbr dbre d dfbrsd scvwfc fcbhf grcfhb erfgv fs dfgdb ff defv");
        evaluation.evalBench.task.QuantitativeQuestion q = new evaluation.evalBench.task.QuantitativeQuestion(
                "q1", "aassff sdsad", 100, "m", 20);
        t.setQuestion(q);
        t.setStartDate(new java.util.Date());
        t.setFinishDate(new java.util.Date(t.getStartDate().getTime() + 2000));
        q.setGivenAnswer(130.0d);
        EvaluationSession session = new EvaluationSession("mySession");

        TaskDialog d = new TaskDialog(null);
        d.announceSession(session, true);
        d.showDescription(t);
        d.showFeedback(t);
        System.exit(0);
    }
*/
}
