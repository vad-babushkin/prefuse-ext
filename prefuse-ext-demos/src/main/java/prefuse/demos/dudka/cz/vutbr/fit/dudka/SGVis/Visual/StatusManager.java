package prefuse.demos.dudka.cz.vutbr.fit.dudka.SGVis.Visual;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class manages statusbar content.
 * It is only used by GraphView class to perform "intelligent" status behavior.
 * There can be a set of observers added by addListener method.
 */
class StatusManager {
	/**
	 * Default value for default show-status timeout (in milliseconds).
	 */
	public static int DEF_TIMEOUT = 2500;
	
	private String statusText;
	private boolean statusErr;
	private ArrayList<ChangeListener> statusListeners;
	private final GraphView gview;
	private int timeout;
	private Timer timer;
	
	public StatusManager(GraphView gview) {
		this.gview = gview;
		this.timeout = DEF_TIMEOUT;
		this.timer = null;
		statusListeners = new ArrayList<ChangeListener>(); 
	}
	
	/**
	 * Returns default show-status timeout (in milliseconds)
	 */
	public synchronized int getTimeout() {
		return timeout;
	}
	
	/**
	 * Set default timeout show-status timeout (in milliseconds)
	 * @param timeout Show-status timeout (in milliseconds)
	 */
	public synchronized void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Add status observer to list.
	 * @param listener Object which wants to know about status changes.
	 */
	public synchronized void addListener(ChangeListener listener) {
		this.statusListeners.add(listener);
	}

	/**
	 * Undisplay text in statusbar.
	 */
	public synchronized void setStatus() {
		this.destroyTimer();
		this.setStatus(" ");
	}
	
	/**
	 * Display text in statusbar.
	 * @param status Text to display in statusbar.
	 */
	public synchronized void setStatus(String status) {
		this.destroyTimer();
		this.setStatus(status, false);
	}
	
	/**
	 * Display text in statusbar and undisplay it after default timeout.
	 * @param status Text to display in statusbar.
	 */
	public synchronized void showStatus(String status) {
		this.showStatus(status, this.timeout);
	}
	
	/**
	 * Display text in statusbar and undisplay it after desired timeout.
	 * @param status Text to display in statusbar.
	 * @param timeout Timeout to undisplay displayed text.
	 */
	public synchronized void showStatus(String status, int timeout) {
		this.destroyTimer();
		this.setStatus(status);
		this.initTimer(timeout);
	}
	
	/**
	 * Display error message in statusbar.
	 * @param text Error message text.
	 */
	public synchronized void showErrror(String text) {
		this.destroyTimer();
		this.setStatus(text, true);
	}
	
	/**
	 * Returns current status message.
	 */
	public synchronized String getStatus() {
		return this.statusText;
	}
	
	/**
	 * Return true if error message is displayed in statusbar.
	 */
	public synchronized boolean isStatusError() {
		return this.statusErr;
	}
	
	private synchronized void setStatus(String status, boolean err) {
		this.statusText = status;
		this.statusErr = err;
		for (ChangeListener listener: statusListeners)
			listener.stateChanged(new ChangeEvent(gview));
	}
	private synchronized void destroyTimer() {
		if (null==timer)
			return;
		timer.stop();
		timer = null;
	}
	private synchronized void initTimer(int timeout) {
		final StatusManager sm = this;
		ActionListener timerListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sm.setStatus();
			}
		};
		timer = new Timer(timeout, timerListener);
		timer.start();
	}
}
