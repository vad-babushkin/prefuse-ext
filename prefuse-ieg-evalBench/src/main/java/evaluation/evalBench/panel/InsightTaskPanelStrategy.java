package evaluation.evalBench.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.JTextComponent;

import evaluation.evalBench.EvaluationResources;
import evaluation.evalBench.task.Insight;
import evaluation.evalBench.task.InsightTask;
import evaluation.evalBench.task.Question;
import evaluation.evalBench.task.Task;

@Deprecated
public class InsightTaskPanelStrategy extends QuestionPanelStrategy {

    private JTextComponent insightsInput = null;
    
    /**
     * radio buttons for insight characteristics. First dimension are
     * characteristics, second dimension are levels of characteristics plus an
     * additional value for &quot;not selected&quot;
     */
    private JRadioButton[][] likert = null;

    // private JTextPane insightsPreview = null;

    public InsightTaskPanelStrategy(Question aTask) {
        super(aTask);
    }

    @Override
    public boolean checkForCorrectInput() {
        InsightTask task = (InsightTask) super.getQuestion();
        String[] characteristics = task.getCharacteristics();

        String insightText = insightsInput.getText().trim();
        Insight insight = new Insight(insightText);

        if (insightText.length() == 0) {
            super.setErrorMessage(EvaluationResources
                    .getString("insight.taskpanel.error.text"));
            return false;
        }

        StringBuilder errors = new StringBuilder();
        for (int y = 0; y < characteristics.length; y++) {
            int likertValue = -1;
            for (int i = 0; i <= task.getCharacteristicsLevels(); i++) {
                if (likert[y][i].isSelected()) {
                    likertValue = i;
                }
            }
            // check if no a radio button (except n.a. was selected)
            if (likertValue > 0) {
                insight.addCharacteristicsValue(characteristics[y], likertValue);
            } else {
                insight.addCharacteristicsValue(characteristics[y], null);
                errors.append(", ");
                errors.append(characteristics[y]);
            }
        }

        if (task.isCharacteristicsRequired() && errors.length() > 0) {
            errors.delete(0, 2);
            super.setErrorMessage(EvaluationResources
                    .getString("insight.taskpanel.error.characteristics"));
            return false;
        }

        task.setInsights(insight);
        return true;
    }

    @Override
    public void inputFinished() {
        // reset controls (insight studies might not create a new task panel)
        for (int y = 0; y < likert.length; y++) {
            likert[y][0].setSelected(true);
        }
        insightsInput.setText("");
    }

    @Override
    public JPanel getNewAnsweringPanel() {
        // textarea for insight input
        JTextArea textArea = new JTextArea(5, 20);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane areaScrollPane = new JScrollPane(textArea);
        areaScrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        areaScrollPane.setPreferredSize(new Dimension(220, 250));
        textArea.setToolTipText(EvaluationResources
                .getString("insights.input.textpane.tooltip"));
        insightsInput = textArea;

        // radio buttons for likert scale
        String[] vars = ((InsightTask) super.getQuestion()).getCharacteristics();
        int levels = ((InsightTask) super.getQuestion()).getCharacteristicsLevels();
        JPanel likertGrid = createLikertGrid(vars, levels);

        // put it all together in a panel
        JPanel answeringPanel = new JPanel();
        answeringPanel.setLayout(new BorderLayout());
        answeringPanel.add(areaScrollPane, BorderLayout.CENTER);
        answeringPanel.add(likertGrid, BorderLayout.SOUTH);
        return answeringPanel;
    }

    private JPanel createLikertGrid(String[] vars, int levels) {
        JPanel likertGrid = new JPanel();
        likertGrid.setLayout(new GridBagLayout());

        if (vars.length > 0 || levels < 3) {
            GridBagConstraints g = new GridBagConstraints();

            g.fill = GridBagConstraints.HORIZONTAL;
            g.gridwidth = 2;
            g.gridx = 1;
            g.anchor = GridBagConstraints.WEST;
            likertGrid.add(
                    new JLabel(EvaluationResources
                            .getString("insights.likert.left")), g);

            g.gridwidth = GridBagConstraints.REMAINDER;
            g.gridx = levels - 1;
            g.anchor = GridBagConstraints.EAST;
            g.insets.right = 5;
            JLabel endLabel = new JLabel(
                    EvaluationResources.getString("insights.likert.right"));
            endLabel.setHorizontalAlignment(JLabel.RIGHT);
            likertGrid.add(endLabel, g);

            g.insets.right = 0;
            g.gridwidth = 1;

            likert = new JRadioButton[vars.length][levels + 1];

            // each var makes a grid row and a button group
            for (int y = 0; y < vars.length; y++) {
                g.gridy = y + 1;
                g.gridx = 0;
                g.anchor = GridBagConstraints.WEST;
                g.weightx = 1.0;
                likertGrid.add(new JLabel(vars[y]), g);

                g.weightx = 0.0;
                g.anchor = GridBagConstraints.CENTER;
                g.gridx = GridBagConstraints.RELATIVE;
                ButtonGroup group = new ButtonGroup();

                // each level makes a grid cell and a radio button
                for (int i = 1; i <= levels; i++) {
                    likert[y][i] = new JRadioButton();
                    likert[y][i].setToolTipText(Integer.toString(i));
                    likertGrid.add(likert[y][i], g);
                    group.add(likert[y][i]);
                }
                
                // one extra radio button for not selected
                likert[y][0] = new JRadioButton();
                likert[y][0].setSelected(true);
                likert[y][0].setToolTipText("n.a.");
                group.add(likert[y][0]);
                // TODO optionally show n.a. button in GUI 
            }
        }

        return likertGrid;
    }

    public static void main(String[] arg) {
        // TODO remove experimental main method
        JFrame f = new JFrame();
        Task t = new Task("id", "desc", "com");
        Question q = new InsightTask("id", "desc", "com");
        t.setQuestion(q);
        JPanel tp = (new DefaultEvaluationPanelFactory()).getPanelForTask(t);
        f.add(tp);

        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension m_ScreenSize = tk.getScreenSize();
        int prefHeight = Math.min(m_ScreenSize.height - 25, 1000);
        f.setLocation(0, 0);
        f.setPreferredSize(new Dimension(200, prefHeight));
        f.pack();
        f.setVisible(true);
    }
}
