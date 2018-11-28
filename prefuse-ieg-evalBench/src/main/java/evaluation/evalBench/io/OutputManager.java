package evaluation.evalBench.io;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

@XmlAccessorType(XmlAccessType.NONE)
public class OutputManager {

    /**
     * {@link java.util.Formatter format string} for output directory. The
     * directory will be created if it does not exist. Arguments:
     * 
     * <li>participant id <li>current date and time
     * 
     * <p>
     * See {@link java.util.Formatter}
     */
    @XmlElement
    private String directoryFormat = "evaluationJournal/%1$s_%2$tF";

    /**
     * {@link java.util.Formatter format string} for journal file name.
     * Arguments:
     * 
     * <li>participant id <li>session title <li>current date and time
     * 
     * <p>
     * See {@link java.util.Formatter}
     */
    @XmlElement
    private String journalPrefix = "%s_%s_%3$tH%3$tM%3$tS";

    /**
     * {@link java.util.Formatter format string} for log file name.
     * Arguments:
     * 
     * <li>participant id <li>session title <li>current date and time
     * 
     * <p>
     * See {@link java.util.Formatter}
     */
    @XmlElement
    private String logPrefix = "%s_%s_%3$tH%3$tM%3$tS";

    /**
     * {@link java.util.Formatter format string} for screen shot file names.
     * Arguments:
     * 
     * <li>participant id <li>task id <li>current date and time
     * 
     * <p>
     * See {@link java.util.Formatter}
     */
    @XmlElement
    private String screenshotName = "%2$s_%1$s_%3$tH%3$tM%3$tS";
    
    @XmlElement
    private String stateFileName = "%2$s_%1$s_%3$tH%3$tM%3$tS";
    
    @XmlTransient
    private File outputDirectory;

    @XmlTransient
    private String participantId;

    @XmlTransient
    private Robot robot = null;

    public OutputManager(String participantId) {
        this.participantId = participantId;
    }

    private void checkDirectory() {
        if (outputDirectory == null) {
            String dirName = String.format(directoryFormat, participantId,
                    new Date());
            outputDirectory = new File(dirName);
        }

        if (!outputDirectory.canWrite()) {
            try {
                boolean success = outputDirectory.mkdirs();

                if (success) {
                    Logger.getLogger(OutputManager.class).info(
                            "Directory: " + outputDirectory.getPath()
                                    + " created");
                } else {
                    Logger.getLogger(OutputManager.class).warn(
                            "Could not create directory: "
                                    + outputDirectory.getPath() + "!");
                }
            } catch (SecurityException e) {
                Logger.getLogger(OutputManager.class).warn(
                        "Directory creation error.", e);
            }
        }
    }
    
    public File getOutputDirectory()
    {
    	return new File (outputDirectory, "");
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public File getJournalFile(String sessionTitle, String suffix) {
        checkDirectory();
        String fileName = String.format(journalPrefix, participantId,
                sessionTitle, new Date()) + suffix;
        return new File(outputDirectory, fileName);
    }

    public File getLogFile(String sessionTitle, String suffix) {
        checkDirectory();
        String fileName = String.format(logPrefix, participantId,
                sessionTitle, new Date()) + suffix;
        return new File(outputDirectory, fileName);
    }

    public File getStateFile(String taskId, Date date, String suffix) {
        checkDirectory();
        String fileName = String.format(stateFileName, participantId,
                taskId, date) + suffix;
        return new File(outputDirectory, fileName);
    }

    public String takeScreenshot(String taskId) {
        return takeScreenshot(taskId, new Date());
    }
    
    public String takeScreenshot(String taskId, Date date) {
        String fileName = String.format(screenshotName, participantId, taskId,
                date) + ".png";

        try {
            if (robot == null) {
                robot = new Robot();
            }
            // TODO consider multi display -- see code from TimeRider
            // saving only main frame is not possible <- application dependent
            Rectangle captureSize = new Rectangle(Toolkit.getDefaultToolkit()
                    .getScreenSize());
            BufferedImage bufferedImage = robot
                    .createScreenCapture(captureSize);
            File outputFile = new File(outputDirectory, fileName);
            ImageIO.write(bufferedImage, "png", outputFile);
        } catch (AWTException e) {
            Logger.getLogger(OutputManager.class).warn(
                    "Could not take a screenshot for the task");
        } catch (IOException e) {
            Logger.getLogger(OutputManager.class).warn(
                    "Could not save the screenshot for the task");
        }

        return fileName;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO remove experimental main method
        BasicConfigurator.configure();
        OutputManager o = new OutputManager("9725123");
        o.checkDirectory();
        System.out.println(o.getJournalFile("scatter", "_journal.csv"));
    }
}
