package evaluation.evalBench.io;

import evaluation.evalBench.session.EvaluationSession;
import evaluation.evalBench.task.Task;
import evaluation.evalBench.task.TaskList;
import ieg.util.xml.JaxbMarshaller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * implementation of TaskListCreator. Assumes that every task subclass provides
 * <a href="https://jaxb.dev.java.net">jaxb</a> annotations.
 */
@SuppressWarnings("rawtypes")
public class XMLTaskListCreator implements TaskListCreator {

 //   private static final String INPUT_TYPE_KEY = "inputType";
    
    private boolean loadAsResource = false; 

	private Class[] classesToBeBound = new Class[] {TaskList.class};


    public ArrayList<Task> getTaskListForSession(EvaluationSession aSession, String taskFileName) {

        TaskList taskList = new TaskList();

        /**
         * unmarshall the xml tasklist
         */
        try {
            URL uri = null;
            if (this.loadAsResource) {
                uri = Thread.currentThread().getContextClassLoader()
                        .getResource(taskFileName);
            }

            if (uri == null) {
                taskList = (TaskList) JaxbMarshaller.loadUser(taskFileName, classesToBeBound);
            } else {
                taskList = (TaskList) JaxbMarshaller.loadUser(uri.openStream(), classesToBeBound);
            }
        } catch (Exception e) {

            System.err.println("error: could not load tasklist");
            e.printStackTrace();
        }

        /**
         * set session configurations and input configuration for each task
         */
		// TODO should this code be moved somewhere else?
        Map<String,String> sessionConfig = aSession.getConfiguration();

        for (Task aTask : taskList.getTasks()){

            Map<String,String> aConfig = aTask.getConfiguration();

            Set<String> set = sessionConfig.keySet();

            for (String str : set) {

                aConfig.put(str, sessionConfig.get(str));
            }
            
            // add unprocessed config to old config style
            
            // TODO: unify
            
            if (aTask.getConfigurationUnprocessed() != null)
            {
            	Set<QName> qSet = aTask.getConfigurationUnprocessed().getAny().keySet();
            	QName[] configurationKeys = qSet.toArray(new QName[qSet.size()]);
            	        	
            	for (QName name : configurationKeys) {
            		
            		aConfig.put(name.toString(), aTask.getConfigurationUnprocessed().getAny().get(name).toString());
            		
        		}
            }
            
            

        }

        return taskList.getTasks();
    }


    /**
     * set whether the task list will be loaded as a resource.
     * Thus the task list can be included in the JAR file.
     * @param loadAsResource true, if the task list is loaded as a resource.
     */
    public void setLoadAsResource(boolean loadAsResource) {
        this.loadAsResource = loadAsResource;
    }


    /**
     * get whether the task list will be loaded as a resource.
     * Thus the task list can be included in the JAR file.
     * @return true, if the task list is loaded as a resource.
     */
    public boolean isLoadAsResource() {
        return loadAsResource;
    }


	public Class[] getClassesToBeBound() {
		return classesToBeBound;
	}


	public void setClassesToBeBound(Class... classesToBeBound) {
		this.classesToBeBound = classesToBeBound;
	}
}
