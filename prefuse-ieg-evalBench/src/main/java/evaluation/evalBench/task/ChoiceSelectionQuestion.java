package evaluation.evalBench.task;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang3.StringUtils;

/**
 * Class representing a choice selection task a user of a visualization tool has
 * to carry out in an evaluation process i.e. the user can decide between
 * multiple predefined answers. Single choice or multiple choice types are
 * supported.
 * 
 * 
 * E.g. Which of the following countries is in Europe?
 * 
 * 1. Austria 2. China
 * 
 * This class provides <a href="https://jaxb.dev.java.net">jaxb</a> annotations
 * 
 * @author Stephan Hoffmann, Alexander Rind, David Bauer
 */
public class ChoiceSelectionQuestion extends Question {

	// TODO make configure separator (later)?
	private static final String SEPARATOR = ", ";

	public static class ChoiceOption {
		private String m_label;
		private String m_image = null;
		private String m_displayLabel = null;

		public ChoiceOption() {
		}

		public ChoiceOption(String label) {
			this(label, null);
		}

		public ChoiceOption(String label, String image) {
			this(label, image, null);
		}

		public ChoiceOption(String label, String image, String displayLabel) {
			
			if (label != null)
				m_label = label;
			else
				throw new NullPointerException("Label cannot be null!");
				
			m_image = image;
			m_displayLabel = displayLabel;
		}

        /**
         * get the label of the option. This is printed in the Journal and
         * displayed if nothing else is specified.
         * 
         * @return
         */
        @XmlAttribute(required = true)
        public String getLabel() {
            return m_label;
        }

        public void setLabel(String label) {
            this.m_label = label;
        }

        /**
         * get the image to be displayed with this option.
         * 
         * @return
         */
        @XmlElement
        public String getImage() {
            return m_image;
        }

        public void setImage(String image) {
            this.m_image = image;
        }
        
        /**
         * get the label to be displayed instead of the label or in combination
         * with the image.
         * 
         * @return
         */
        @XmlElement
        public String getDisplayLabel() {
            return m_displayLabel;
        }

        public void setDisplayLabel(String displayLabel) {
            this.m_displayLabel = displayLabel;
        }
	}

	protected SortedSet<String> correctAnswers = null;
	protected SortedSet<String> givenAnswers = null;

	protected ArrayList<ChoiceOption> possibleAnswers;

	// TODO show max choices in Strategy (if applicable)
    /**
     * maximum number of selectable options. Set to <tt>1</tt> for single-choice
     * selection. Set to <tt>null</tt>, if the number should not be restricted.
     */
    private Integer m_maxChoices = 1;

    private int m_minChoices = 1;

	/**
	 * Standard constructor, initializes the object with empty taskId,
	 * taskDescription and answer lists
	 */
	public ChoiceSelectionQuestion() {
		this("", "", null, new ArrayList<ChoiceOption>());
	}

	/**
	 * Constructor that takes all necessary information for the task
	 * 
	 * @param aQuestionId
	 *            the unique id for the task
	 * @param aQuestionText
	 *            the description of the task for the test person
	 * @param aCorrectAnswersArrayList
	 *            an ArrayList containing a set of the correct answers as
	 *            strings (one answer for single choice)
	 * @param aPossibleAnswersArrayList
	 *            an ArrayList containing a set of the possible answers as
	 *            strings from which the test person can choose
	 */
	public ChoiceSelectionQuestion(String aQuestionId, String aQuestionText,
	        SortedSet<String> aCorrectAnswersArrayList,
			ArrayList<ChoiceOption> aPossibleAnswersArrayList) {

		super(aQuestionId, aQuestionText);
		this.correctAnswers = aCorrectAnswersArrayList;
		this.possibleAnswers = aPossibleAnswersArrayList;
	}

    /**
     * set the maximum number of selectable options. Set to <tt>1</tt> for
     * single-choice selection. Set to <tt>null</tt>, if the number should not
     * be restricted.
     * 
     * @param maxChoices
     *            - sets the maximum number of selectable options
     */
    @XmlElement(defaultValue = "1", nillable = true)
    public void setMaxChoices(Integer maxChoices) {
        m_maxChoices = maxChoices;
    }

	/**
	 * @return gets the maximum number of selectable options
	 */
	public Integer getMaxChoices() {
		return m_maxChoices;
	}


	public int getMinChoices() {
        return m_minChoices;
    }

    /**
     * Set to <tt>0</tt>, if it is allow the no option is selected.
     *
     * @param minChoices
     */
    @XmlElement(required = false, defaultValue = "1")
    public void setMinChoices(int minChoices) {
        this.m_minChoices = minChoices;
    }

    /**
	 * Get the correct answers of this task
	 * 
	 * @return an ArrayList of strings containing the correct answers
	 */
	@Override
	@XmlElementWrapper(name = "givenAnswers", required = false)
	@XmlElement(name = "givenAnswer")
	public SortedSet<String> getGivenAnswer() {
		return givenAnswers;
	}

	/**
	 * Sets the answers that the test person has entered
	 * 
	 * @param aGivenAnswersArrayList
	 *            an ArrayList of strings containing the given answers
	 */
	public void setGivenAnswer(SortedSet<String> aGivenAnswersArrayList) {
		this.givenAnswers = aGivenAnswersArrayList;
	}

	/**
	 * Get the correct answers of this task
	 * 
	 * @return an ArrayList of strings containing the correct answers
	 */
	@Override
	@XmlElementWrapper(name = "correctAnswers", required = false)
	@XmlElement(name = "correctAnswer")
	public SortedSet<String> getCorrectAnswer() {
		return correctAnswers;
	}

	/**
	 * Sets the correct answers for this task
	 * 
	 * @param correctAnswers
	 *            an ArrayList containing a set of the correct answers as
	 *            strings (one answer for single choice)
	 */
	public void setCorrectAnswer(SortedSet<String> correctAnswers) {
		this.correctAnswers = correctAnswers;
	}

	/**
	 * Sets the answers from which the test person can pick one or more possible
	 * answers
	 * 
	 * @param possibleAnswers
	 *            an ArrayList containing a set of the possible answers as
	 *            strings from which the test person can choose
	 */
	public void setPossibleAnswers(ArrayList<ChoiceOption> possibleAnswers) {
		this.possibleAnswers = possibleAnswers;
	}

	/**
	 * get the possible answers of this task
	 * 
	 * @return an ArrayList containing a set of the possible answers as strings
	 *         from which the test person can choose
	 */
	@XmlElementWrapper(name = "possibleAnswers")
	@XmlElement(name = "possibleAnswer")
	public ArrayList<ChoiceOption> getPossibleAnswers() {
		return this.possibleAnswers;
	}

	@Override
	public String exportGivenAnswer() {
		return StringUtils.join(givenAnswers, SEPARATOR);
	}

	@Override
	public String exportCorrectAnswer() {
		return StringUtils.join(correctAnswers, SEPARATOR);
	}
	
	@Override
	public Question clone() {
		ChoiceSelectionQuestion clone = (ChoiceSelectionQuestion) super.clone();
		
		if (possibleAnswers != null) {
			clone.possibleAnswers = new ArrayList<ChoiceOption>(possibleAnswers.size());
			for (ChoiceOption opt : possibleAnswers) {
				ChoiceOption optClone = new ChoiceOption(opt.m_label, opt.m_image, opt.m_displayLabel);
				clone.possibleAnswers.add(optClone);
			}
		}
		
		if (givenAnswers != null) {
			clone.givenAnswers = new TreeSet<String>(); // assumption of specific subclass of SortedSet
			for (String ans : givenAnswers) {
				clone.givenAnswers.add(ans);
			}
		}
		
		if (correctAnswers != null) {
			clone.correctAnswers = new TreeSet<String>(); // assumption of specific subclass of SortedSet
			for (String ans : correctAnswers) {
				clone.correctAnswers.add(ans);
			}
		}
		
		return clone;
	}
}
