package evaluation.evalBench.session;

import java.util.ArrayList;

import evaluation.evalBench.io.OutputManager;

/**
 * represents a group of evaluation sessions {@link EvaluationSession} for a test person
 */
public class EvaluationSessionGroup {

    // TODO add counter and methods like hasNext() next() --> simplify evaluationSessionDidFinish() -- look for more robust method
    
    private String m_participantId;

    private ArrayList<EvaluationSession> m_SessionList = new ArrayList<EvaluationSession>();

    private EvaluationSession m_activeSubSession = null;
    
    private OutputManager m_outputManager;

    /**
     * constructor, creates a new array list of evaluation sessions ({@link EvaluationSession}) for a specific
     * test person
     * @param participantId
     */
    public EvaluationSessionGroup(String participantId){
        m_participantId = participantId;
        this.m_outputManager = new OutputManager(m_participantId);
    }

    /**
     *
     * @return the id of the test person
     */
    public String getParticipantId() {
        return m_participantId;
    }


    /**
     * adds a evaluation session to the group {@link EvaluationSession}
     * @param aEvaluationSession
     */
    public void addSession(EvaluationSession aEvaluationSession){
        aEvaluationSession.setSessionGroup(this);
        m_SessionList.add(aEvaluationSession);
    }

    /**
     * starts the specified evaluation session, if available
     * @param aSubSession
     */
    public void startSubSession(EvaluationSession aSubSession){
        if (m_SessionList.indexOf(aSubSession) != -1){
            m_activeSubSession = aSubSession;
            m_activeSubSession.startSession();
        }

    }


    /**
     * provides the currently active session in the group
     * @return active session
     */
    public EvaluationSession getActiveSubSession(){
        return m_activeSubSession;
    }
    
    /**
     * get the next {@link EvaluationSession}.
     * <p>Warning: does not work if the same EvaluationSession object is added more than once. 
     * @return the subsequent evaluation session
     */
    public EvaluationSession getNextSubSession() {
    	// before 1st session 
    	if (m_activeSubSession == null && m_SessionList.size() > 0) {
    		return m_SessionList.get(0);
    	}
    		
    	int index = m_SessionList.indexOf(m_activeSubSession);
    	if (index != -1 && index + 1 < m_SessionList.size()) {
    		return m_SessionList.get(index + 1);
    	}
    	else {
    		return null;
    	}
    }

    /**
     * check if there are more {@link EvaluationSession}s in the group.
     * <p>Warning: does not work if the same EvaluationSession object is added more than once. 
     * @return true if there is at least one evaluation session after the current one
     */
    public boolean hasMoreSessions() {
    	// before 1st session 
    	if (m_activeSubSession == null) {
    		return (m_SessionList.size() > 0);
    	} else {
    		int index = m_SessionList.indexOf(m_activeSubSession);
    		return (index != -1 && index + 1 < m_SessionList.size());
    	}
    }

    /**
     * provides the complete session list
     * @return session list
     */
    public ArrayList<EvaluationSession> getSessionList() {
        return m_SessionList;
    }

    /**
     * @return the output manager
     */
    public OutputManager getOutputManager() {
        return m_outputManager;
    }
}
