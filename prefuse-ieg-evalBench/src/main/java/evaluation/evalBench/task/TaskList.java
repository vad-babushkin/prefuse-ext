package evaluation.evalBench.task;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a collection of tasks (a wrapper of ArrayList for JAXB unmarshalling)
 */
@XmlRootElement
@XmlType(name="taskListType")
public class TaskList {
    private ArrayList<Task> m_tasks = new ArrayList<Task>();

    @XmlElement(name = "task", required=true)
    public ArrayList<Task> getTasks() {

		return m_tasks;
	}


}
