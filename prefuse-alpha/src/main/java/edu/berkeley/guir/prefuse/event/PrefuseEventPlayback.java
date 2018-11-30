/*
 * Created on Jan 10, 2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package edu.berkeley.guir.prefuse.event;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.berkeley.guir.prefuse.Display;

/**
 * @author jheer
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PrefuseEventPlayback {

	private static HashSet s_mouseEvents = new HashSet();
	private static HashSet s_keyEvents = new HashSet();
	static {
		s_mouseEvents.add(PrefuseEventLogger.ITEM_DRAGGED);
		s_mouseEvents.add(PrefuseEventLogger.ITEM_MOVED);
		s_mouseEvents.add(PrefuseEventLogger.ITEM_WHEEL_MOVED);
		s_mouseEvents.add(PrefuseEventLogger.ITEM_CLICKED);
		s_mouseEvents.add(PrefuseEventLogger.ITEM_PRESSED);
		s_mouseEvents.add(PrefuseEventLogger.ITEM_RELEASED);
		s_mouseEvents.add(PrefuseEventLogger.ITEM_ENTERED);
		s_mouseEvents.add(PrefuseEventLogger.ITEM_EXITED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_ENTERED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_EXITED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_PRESSED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_RELEASED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_CLICKED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_DRAGGED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_MOVED);
		s_mouseEvents.add(PrefuseEventLogger.MOUSE_WHEEL_MOVED);
		
		s_keyEvents.add(PrefuseEventLogger.KEY_PRESSED);
		s_keyEvents.add(PrefuseEventLogger.KEY_RELEASED);
		s_keyEvents.add(PrefuseEventLogger.KEY_TYPED);
		s_keyEvents.add(PrefuseEventLogger.ITEM_KEY_PRESSED);
		s_keyEvents.add(PrefuseEventLogger.ITEM_KEY_RELEASED);
		s_keyEvents.add(PrefuseEventLogger.ITEM_KEY_TYPED);
	} //
	
	private Display display;
	private List events;
	private long startTime;
	
	public PrefuseEventPlayback(Display display, String logfile) {
		this.display = display;
		this.events = new ArrayList();
		try {
			parseLogFile(logfile);
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	} //
	
	public void play() {
		Runnable runner = new Runnable() {
			public void run() {
				playInternal();
			} //
		};
		new Thread(runner).start();
	} //
	
	public void playInternal() {
		EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		long realtime = System.currentTimeMillis();
		long playtime = startTime;
		int i = 0;
		while ( i < events.size() ) {
			long mark = System.currentTimeMillis();
			long elapsed = mark-realtime;
			realtime = mark;
			playtime += elapsed;
			
			EventEntry entry = (EventEntry)events.get(i);
			while ( entry != null && entry.time < playtime ) {
				AWTEvent event = getEvent(mark, entry.event);
				queue.postEvent(event);
				if ( i < events.size()-1 )
					entry = (EventEntry)events.get(++i);
				else {
					i++; entry = null;
				}
			}
			if ( entry != null ) {
				long sleeptime = entry.time - playtime;
				try {
					Thread.sleep(sleeptime);
				} catch ( Exception e ) {}
			}
		}
	} //
	
	private void parseLogFile(String logfile) throws IOException {
		startTime = -1L;
		BufferedReader br = new BufferedReader(new FileReader(logfile));
		String line;
		
		while ((line=br.readLine()) != null) {
			parseLine(line);
		}
		br.close();
	} //
	
	private void parseLine(String line) {
		String[] toks = line.split("\t");
		long time = Long.parseLong(toks[0]);
		if ( startTime == -1L ) { startTime = time;	}
		boolean mouseEvent = s_mouseEvents.contains(toks[1]);
		boolean keyEvent   = s_keyEvents.contains(toks[1]);
		if ( mouseEvent || keyEvent ) {
			String data = null;
			for ( int i=2; i<toks.length; i++ ) {
				if ( toks[i].startsWith("[") )
					data = toks[i];
			}
			EventParams ep = null;
			if ( mouseEvent ) {
				ep = parseMouseEvent(time, data);
			} else {
				ep = parseKeyEvent(time, data);
			}
			events.add(new EventEntry(time, ep));
		}
	} //
	
	public AWTEvent getEvent(long time, EventParams ep) {
		if ( ep instanceof MouseParams ) {
			return getMouseEvent(time, (MouseParams)ep);
		} else if ( ep instanceof KeyParams ) {
			return getKeyEvent(time, (KeyParams)ep);
		} else {
			return null;
		}
	} //
	
	public MouseEvent getMouseEvent(long time, MouseParams mp) {
		if ( mp.id != MouseEvent.MOUSE_WHEEL ) {
			return new MouseEvent(display, mp.id, time, mp.modifiers,
					mp.x, mp.y, mp.clickCount, false, mp.button);
		} else {
			return new MouseWheelEvent(display, mp.id, time, mp.modifiers,
					mp.x, mp.y, mp.clickCount, false, mp.scrollType,
					mp.scrollAmount, mp.wheelRotation);
		}
	} //
	
	public KeyEvent getKeyEvent(long time, KeyParams kp) {
		return new KeyEvent(display, kp.id, time, kp.modifiers, kp.keyCode, kp.keyChar);
	} //
	
	public MouseParams parseMouseEvent(long time, String data) {
		data = data.substring(1,data.length()-1);
		String[] toks = data.split(",");
		MouseParams mp = new MouseParams();
		mp.id = Integer.parseInt(getValue(toks[0]));
		mp.x = Integer.parseInt(getValue(toks[1]));
		mp.y = Integer.parseInt(getValue(toks[2]));
		mp.button = Integer.parseInt(getValue(toks[3]));
		mp.clickCount = Integer.parseInt(getValue(toks[4]));
		mp.modifiers = Integer.parseInt(getValue(toks[5]));
		
		if ( mp.id == MouseEvent.MOUSE_WHEEL ) {
			mp.scrollType = Integer.parseInt(getValue(toks[6]));
			mp.scrollAmount = Integer.parseInt(getValue(toks[7]));
			mp.wheelRotation = Integer.parseInt(getValue(toks[8]));
		}
		return mp;
	} //
	
	public KeyParams parseKeyEvent(long time, String data) {
		data = data.substring(1,data.length()-1);
		String[] toks = data.split(",");
		KeyParams kp = new KeyParams();
		kp.id = Integer.parseInt(getValue(toks[0]));
		kp.keyCode = Integer.parseInt(getValue(toks[1]));
		kp.keyChar = getValue(toks[2]).charAt(0);
		kp.modifiers = Integer.parseInt(getValue(toks[3]));
		return null;
	} //
	
	private String getValue(String token) {
		return token.substring(token.indexOf("=")+1);
	} //
	
	public class EventEntry {
		long time;
		EventParams event;
		public EventEntry(long time, EventParams ep) {
			this.time = time;
			this.event = ep;
		}
	} //
	
	public class EventParams {
		int id;
		int modifiers;
	} //
	
	public class MouseParams extends EventParams {
		int x;
		int y;
		int button;
		int clickCount;
		//-------------
		int scrollType;
		int scrollAmount;
		int wheelRotation;
	} //
	
	public class KeyParams extends EventParams {
		int keyCode;
		char keyChar;
	} //
	
} // end of class PrefuseEventPlayback
