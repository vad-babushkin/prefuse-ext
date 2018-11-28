package evaluation.evalBench.task;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

@XmlAccessorType(XmlAccessType.NONE)
@Deprecated
public class InsightTask extends Question {

    @XmlElementWrapper(name = "characteristics", required = true)
    @XmlElement(name = "name")
    private String[] characteristics = { "Relevance", "Certainty",
            "Complexity", "Directedness" };

    @XmlElement(name = "characteristics-levels", required = true)
    private int characteristicsLevels = 4;

    @XmlElement(name = "characteristics-required", required = false)
    private boolean characteristicsRequired = false;

    // XXX pull up to Task? necessary at all?
    @XmlElement(name = "make-screenshot", required = false)
    private boolean makeScreenshot = true;

    private Insight insight = null;

    /**
     * Standard constructor, initializes the object with empty taskId,
     * taskDescription and answer lists
     */
    public InsightTask() {
        super();
    }

    public InsightTask(String aTaskId, String aTaskDescription) {
        super(aTaskId, aTaskDescription);
    }

    public InsightTask(String aTaskId, String aTaskDescription,
            String aTaskInstruction) {
        super(aTaskId, aTaskDescription);
    }

    public String[] getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String[] characteristics) {
        this.characteristics = characteristics;
    }

    public int getCharacteristicsLevels() {
        return characteristicsLevels;
    }

    public void setCharacteristicsLevels(int characteristicsLevels) {
        this.characteristicsLevels = characteristicsLevels;
    }

    public boolean isCharacteristicsRequired() {
        return characteristicsRequired;
    }

    public void setCharacteristicsRequired(boolean characteristicsRequired) {
        this.characteristicsRequired = characteristicsRequired;
    }

    public boolean isMakeScreenshot() {
        return makeScreenshot;
    }

    public void setMakeScreenshot(boolean makeScreenshot) {
        this.makeScreenshot = makeScreenshot;
    }

    @XmlElement
    public Insight getInsight() {
        return insight;
    }

    public void setInsights(Insight insight) {
        this.insight = insight;
    }

    public double getTaskCorrectness() {
        // there has to be a insight
        return (insight != null) ? 1d : 0d;
    }

    @Override
    public String getGivenAnswer() {
        return insight.toString();
    }

    @Override
    public String getCorrectAnswer() {
        // insight task does not have a single correct answer
        return "";
    }
}
