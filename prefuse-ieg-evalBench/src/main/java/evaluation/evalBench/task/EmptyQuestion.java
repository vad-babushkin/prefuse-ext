package evaluation.evalBench.task;

import evaluation.evalBench.EvaluationResources;

/**
 * An empty subclass of {@link Task}.
 */
public class EmptyQuestion extends Question{

    public EmptyQuestion(){
        super("emptyTask", EvaluationResources.getString("emptytask.description"));
    }

    @Override
    public Object getCorrectAnswer() {
        return null;
    }

    @Override
    public Object getGivenAnswer() {
        return null;
    }
    
    @Override
    public String exportGivenAnswer(){
       return "";
    }

    @Override
    public String exportCorrectAnswer(){
       return "";
    }
}
