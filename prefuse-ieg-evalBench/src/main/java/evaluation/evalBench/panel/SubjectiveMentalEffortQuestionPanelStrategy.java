package evaluation.evalBench.panel;

import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSlider;

import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.SubjectiveMentalEffortQuestion;

/**
 * task panel for {@link SubjectiveMentalEffortQuestion} 
 * showing a range slider from 0 to 150 with verbal labels.
 * Initially developed by Zijlstra and van Doorn (1985), 
 * this implementation is based on Sauro and Lewis 
 * (2012, Quantifying the User Experience, p. 214). 
 * @author Alexander Rind
 */
public class SubjectiveMentalEffortQuestionPanelStrategy extends QuestionPanelStrategy {
	
	// TODO initialize unset
	// TODO more space  
	// TODO ticks at labels
	// TODO secondary scale with numbers
	
	private JSlider slider;

	public SubjectiveMentalEffortQuestionPanelStrategy(Question aTask) {
		super(aTask);
	}

	@Override
	public boolean checkForCorrectInput() {
		return true;
	}

	@Override
	public JComponent getNewAnsweringPanel() {
		slider = new JSlider(0, 150, 150);
		slider.setOrientation(JSlider.VERTICAL);

		//Create the label table
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put( new Integer(113), new JLabel("Tremendously hard to do") );
		labelTable.put( new Integer(101), new JLabel("Very, very hard to do") );
		labelTable.put( new Integer( 85), new JLabel("Very hard to do") );
		labelTable.put( new Integer( 70), new JLabel("Pretty hard to do") );
		labelTable.put( new Integer( 57), new JLabel("Rather hard to do") );
		labelTable.put( new Integer( 38), new JLabel("Fairly hard to do") );
		labelTable.put( new Integer( 26), new JLabel("A bit hard to do") );
		labelTable.put( new Integer( 11), new JLabel("Not very hard to do") );
		labelTable.put( new Integer(  1), new JLabel("Not at all hard to do") );
		slider.setLabelTable(labelTable);
		slider.setPaintLabels(true);
		
//		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		
		return slider;
	}

	@Override
	public void inputFinished() {
		int value = slider.getValue();
		((SubjectiveMentalEffortQuestion) super.getQuestion()).setGivenAnswer(value);
	}
	
	

}
