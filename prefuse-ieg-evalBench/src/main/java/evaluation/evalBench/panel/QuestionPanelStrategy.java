package evaluation.evalBench.panel;

import evaluation.evalBench.task.Question;
import javax.swing.*;

/**
 * abstract class for a TaskPanelStrategy to be used by an {@link PanelFactory} to create panels for a task
 *
 * @author Stephan Hoffmann
 */
public abstract class QuestionPanelStrategy {

        private Question m_question;
        private String errorMessage = "";

        public QuestionPanelStrategy(Question aQuestion){
            m_question = aQuestion;
        }
        
        public Question getQuestion(){
            return this.m_question;
        }

        /**
        * has to be implemented by subclasses
        * @return true if all mandatory fields or other inputs have been entered
        */
        public abstract boolean checkForCorrectInput();

       /**
        * has to be implemented by subclasses
        * @return a JPanel which contains the answering possibilities for the task type (e.g. input fields)
        */
        public abstract JComponent getNewAnsweringPanel();


        // TODO unnecessary? this can be performed by check for correct input
       /**
        * has to be implemented by subclasses
        * responsible to set the answers for a specific task
        */
        public abstract void inputFinished();

        public String getErrorMessage() {
            return errorMessage;
        }

        protected void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

}
