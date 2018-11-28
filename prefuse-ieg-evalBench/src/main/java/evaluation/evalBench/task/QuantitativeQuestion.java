package evaluation.evalBench.task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Class representing a quantitative question a user of a visualization tool has to
 * answer in an evaluation process. E.g. What value has variable x on date y?
 * 
 * @author Stephan Hoffmann, David Bauer
 */
@XmlRootElement(name = "quantitativeTask")
public class QuantitativeQuestion extends Question {

	/**
	 * correct value or null (if there is no ground truth)
	 */
	private Double m_correctValue = null;
	
	/**
	 * tolerance; if null a small tolerance will be used
	 */
	private Double m_tolerance = null;

	private Double m_answeredValue;

	private String m_unit;

	private boolean m_isInteger;
	private boolean m_useSpinner;
    private UI uiComponent = null;

	/**
	 * minimum possible value (otherwise error message)
	 */
	private double m_minimum;

    /**
     * maximum possible value (otherwise error message)
     */
    private double m_maximum;
    
	/**
	 * label displayed near the minimum of the slider 
	 */
	private String m_minimumString;
	
	/**
	 * label displayed near the maximum of the slider
	 */
	private String m_maximumString;

	private double m_stepsize;

	/**
	 * Standard constructor, initializes the object with empty taskId,
	 * taskDescription and sets the correct value and tolerance to zero
	 */
	public QuantitativeQuestion() {
		super("", "");
		this.setCorrectValueAndTolerance(Double.NEGATIVE_INFINITY, "", 0.0);
	}

	/**
	 * Constructor that takes all necessary information for the task
	 * 
	 * @param aQuestionId
	 *            the unique id for the task
	 * @param aQuestionText
	 *            the description of the task for the test person
	 * @param aCorrectValue
	 *            the correct answer for the task (i.e. a specific double value)
	 * @param aUnit
	 *            the unit of the quantitative value (e.g. mg/dl)
	 * @param aTolerance
	 *            the tolerance which should be used to calculate the
	 *            correctness
	 */
	public QuantitativeQuestion(String aQuestionId, String aQuestionText,
			double aCorrectValue, String aUnit, double aTolerance) {
		super(aQuestionId, aQuestionText);
		this.setCorrectValueAndTolerance(aCorrectValue, aUnit, aTolerance);
	}

	/**
	 * 
	 * @return true if the answer has to be an integer, false if the task
	 *         accepts an answer in double format
	 */
	@XmlElement(name = "is-integer", defaultValue="true")
	public boolean isInteger() {
		return m_isInteger;
	}

	/**
	 * set the task to accept only integer values
	 * 
	 * @param setInteger
	 *            if true, the task accepts only integer type as an answer,
	 *            false if double type
	 */
	public void setInteger(boolean setInteger) {
		this.m_isInteger = setInteger;
	}

	public void setStepsize(double stepsize) {
		m_stepsize = stepsize;
	}

    @XmlElement(name = "step-size", defaultValue="1.0")
	public double getStepsize() {
		return m_stepsize;
	}

	public void setMaximum(double maximum) {
		m_maximum = maximum;
	}

	public double getMaximum() {
		return m_maximum;
	}

	public void setMinimum(double minimum) {
		m_minimum = minimum;
	}

	public double getMinimum() {
		return m_minimum;
	}

	public String getMaximumString() {
		return m_maximumString;
	}

	public void setMaximumString(String maximumString) {
		m_maximumString = maximumString;
	}

	public String getMinimumString() {
		return m_minimumString;
	}

	public void setMinimumString(String minimumString) {
		m_minimumString = minimumString;
	}

    /**
     * @param useSpinner
     *            - If there should be a JSpinner instead of a slider control
     *            this has to be TRUE
     */
    @XmlElement(name = "use-spinner", defaultValue = "true")
    @Deprecated
    public void setUseSpinner(boolean useSpinner) {
        this.m_useSpinner = useSpinner;
        this.uiComponent = (useSpinner) ? UI.SPINNER : UI.SLIDER;
    }

	/**
	 * @return useSpinner - returns the value of useSpinner
	 */
    @Deprecated
	public boolean getUseSpinner() {
		return m_useSpinner;
	}

	public UI getUiComponent() {
        if (uiComponent == null) {
            return this.m_useSpinner ? UI.SPINNER : UI.SLIDER;
        }
        return uiComponent;
    }

    @XmlElement(name = "ui-component", required = false)
    public void setUiComponent(UI uiComponent) {
        this.uiComponent = uiComponent;
        this.m_useSpinner = (uiComponent == UI.SPINNER);
    }

    /**
	 * set the correct answer (i.e. specific double value) and the tolerance
	 * which should be used to calculate the correctness
	 * 
	 * @param aValue
	 * @param aTolerance
	 */
	public void setCorrectValueAndTolerance(double aValue, double aTolerance) {
		this.setCorrectAnswer(aValue);

		if (aTolerance >= 0.0) {
			this.m_tolerance = aTolerance;
		} else {
			this.m_tolerance = 0.0;
		}
	}

	/**
	 * set the correct answer (i.e. specific double value) and the tolerance
	 * which should be used to calculate the correctness and the corresponding
	 * unit
	 * 
	 * @param aValue
	 * @param aUnit
	 * @param aTolerance
	 */
	public void setCorrectValueAndTolerance(double aValue, String aUnit,
			double aTolerance) {
		this.setUnit(aUnit);
		this.setCorrectValueAndTolerance(aValue, aTolerance);
	}

	/**
	 * get the unit of the quantitative value
	 * 
	 * @return
	 */
	@XmlElement(name = "unit", defaultValue = "")
	public String getUnit() {
		return m_unit;
	}

	/**
	 * set the unit of the quantitative value
	 * 
	 * @param aUnit
	 */
	public void setUnit(String aUnit) {
		this.m_unit = aUnit;
	}

    /**
     * get the quantitative value the test person has given
     * 
     * @return answer or <tt>null</tt> if the question was not answered yet
     */
	@Override
	@XmlElement
    public Double getGivenAnswer() {
        return m_answeredValue;
    }

	/**
	 * set the answer for this task
	 * 
	 * @param m_answeredValue
	 */
	public void setGivenAnswer(Double m_answeredValue) {
		this.m_answeredValue = m_answeredValue;
	}
	
	// TODO export...Answer round to reasonable precision and include unit

	/**
	 * get the correct value for this task
	 * 
	 * @return
	 */
	@Override
	@XmlElement(name = "correctValue")
	public Double getCorrectAnswer() {
		return m_correctValue;
	}

	/**
	 * set the correct value for this task which will be used for the
	 * correctness calculation along with the tolerance
	 * 
	 * @param m_correctValue
	 */
	public void setCorrectAnswer(Double m_correctValue) {
		this.m_correctValue = m_correctValue;
	}

	/**
	 * get the tolerance value for this task which will be used for the
	 * correctness calculation along with the correct value
	 * 
	 * @return
	 */
	@XmlElement(nillable = true)
	public Double getTolerance() {
		return m_tolerance;
	}

	/**
	 * set the tolerance value for this task which will be used for the
	 * correctness calculation along with the correct value
	 * 
	 * @param m_tolerance
	 */
	public void setTolerance(Double m_tolerance) {
		this.m_tolerance = m_tolerance;
	}

	/**
	 * Answer within tolerance returns 0.0, else 1.0.
	 * If no tolerance is set the absolute difference is returned (0.0 ... positive infinity).
	 * @return absolute deviation; if a tolerance is set a binary value (0.0 or 1.0).
	 */
	@Override
	public double determineError() {
		if (m_correctValue == null || m_answeredValue == null) {
			return 1.0;
		}

		double error = Math.abs(m_correctValue - m_answeredValue);
		if (m_tolerance == null) {
			return error;
		} else if (error <= m_tolerance) {
			return 0.0;
		} else {
			return 1.0;
		}
	}

    @XmlType(name = "quantitative-question-ui-type")
    @XmlEnum
    public enum UI {
        TEXT, SPINNER, SLIDER;
    }
}
