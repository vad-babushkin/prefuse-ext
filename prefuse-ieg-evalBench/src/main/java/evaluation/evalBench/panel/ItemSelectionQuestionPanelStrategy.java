package evaluation.evalBench.panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import org.apache.commons.lang3.StringUtils;

import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.task.ItemSelectionQuestion;

public class ItemSelectionQuestionPanelStrategy extends QuestionPanelStrategy{
	JTextPane itemPane;
	//JToggleButton jStartSelectionButton;
	SortedSet<String> selectedItems;
	
	public ItemSelectionQuestionPanelStrategy(ItemSelectionQuestion aTask) {
        super(aTask);
        selectedItems = new TreeSet<String>();
    }
	
	@Override
	public boolean checkForCorrectInput() {
		this.setErrorMessage((""));
		if (selectedItems != null && selectedItems.size()>0){
            //jStartSelectionButton.setSelected(false);

            ((ItemSelectionQuestion)super.getQuestion()).setGivenAnswer(selectedItems);
            return true;
        }

        // otherwise show error msg
        this.setErrorMessage(EvaluationResources.getString("itemsel.taskpanel.error"));
		return false;
	}

	@Override
	public JPanel getNewAnsweringPanel() {
		JPanel innerPanel = new JPanel();

        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));

		{
            /*jStartSelectionButton = new JToggleButton();
            jStartSelectionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            jStartSelectionButton.setText("Markieren starten");
            jStartSelectionButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					if(jStartSelectionButton.isSelected()){
						errorMessage = ("");
						JAXBController.working_settings.setEvaluationSelectionStarted(true);
						jStartSelectionButton.setText("Markieren beenden");
					}else{
						JAXBController.working_settings.setEvaluationSelectionStarted(false);
						jStartSelectionButton.setText("Markieren starten");
					}
					
				}
            	
            });
            jStartSelectionButton.setForeground(Color.BLACK);
            jStartSelectionButton.setBackground(Color.WHITE);*/
//            Border line = new LineBorder(Color.BLACK);
//            Border margin = new EmptyBorder(5, 15, 5, 15);
//            Border compound = new CompoundBorder(line, margin);
           // jStartSelectionButton.setBorder(compound);
            JLabel selectedItems_Label = new JLabel();
            selectedItems_Label.setFont(new Font("Arial",Font.BOLD,12));
            selectedItems_Label.setAlignmentX(Component.CENTER_ALIGNMENT);
            selectedItems_Label.setPreferredSize(new Dimension(190,25));
            selectedItems_Label.setMinimumSize(new Dimension(190,25));
            selectedItems_Label.setMaximumSize(new Dimension(190,25));
            selectedItems_Label.setText(EvaluationResources
                    .getString("itemsel.taskpanel.feedback"));
            itemPane = new JTextPane();

            //itemPane.setText("Gewaehlte Items:" );
            itemPane.setBackground(innerPanel.getBackground());
            itemPane.setEditable(false);
            
            // setting the height
            itemPane.setPreferredSize(new Dimension(190, 70));
            itemPane.setMaximumSize(new Dimension(190, 70));
            itemPane.setMinimumSize(new Dimension(190, 70));

//            answeringPanel.add(Box.createHorizontalGlue());
			innerPanel.add(Box.createRigidArea(new Dimension(40,25)));
			innerPanel.add(selectedItems_Label);

//			answeringPanel.add(Box.createVerticalStrut(30));
            innerPanel.add(itemPane);
//			answeringPanel.add(Box.createRigidArea(new Dimension(40,25)));
		}
		
		return innerPanel;
	}

	@Override
	public void inputFinished() {
		// TODO Auto-generated method stub
		
	}
	
	public void addSelectionItem(String item){
		if(!selectedItems.contains(item)){
			selectedItems.add(item);
			showSelectedItems();
			this.setErrorMessage((""));
		}else{
			selectedItems.remove(item);
			showSelectedItems();
		}
	}
	
	public void removeSelectionItem(String item){
		if(selectedItems.contains(item)){
			selectedItems.remove(item);
			showSelectedItems();
		}
	}
	
	public void removeAllItems(){
		selectedItems.clear();
		showSelectedItems();
	}
	
	private void showSelectedItems(){
		itemPane.setText(StringUtils.join(selectedItems, "\n"));
	}
}
