package edu.berkeley.guir.prefuse.event;

import edu.berkeley.guir.prefuse.Display;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PrefuseEventPlayback {
	private static HashSet s_mouseEvents = new HashSet();
	private static HashSet s_keyEvents = new HashSet();
	private Display display;
	private List events;
	private long startTime;

	public PrefuseEventPlayback(Display paramDisplay, String paramString) {
		this.display = paramDisplay;
		this.events = new ArrayList();
		try {
			parseLogFile(paramString);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	public void play() {
		Runnable local1 = new Runnable() {
			public void run() {
				PrefuseEventPlayback.this.playInternal();
			}
		};
		new Thread(local1).start();
	}

	public void playInternal() {
		EventQueue localEventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
		long l1 = System.currentTimeMillis();
		long l2 = this.startTime;
		int i = 0;
		while (i < this.events.size()) {
			long l3 = System.currentTimeMillis();
			long l4 = l3 - l1;
			l1 = l3;
			l2 += l4;
			EventEntry localEventEntry = (EventEntry) this.events.get(i);
			while ((localEventEntry != null) && (localEventEntry.time < l2)) {
				AWTEvent localAWTEvent = getEvent(l3, localEventEntry.event);
				localEventQueue.postEvent(localAWTEvent);
				if (i < this.events.size() - 1) {
					localEventEntry = (EventEntry) this.events.get(++i);
				} else {
					i++;
					localEventEntry = null;
				}
			}
			if (localEventEntry != null) {
				long l5 = localEventEntry.time - l2;
				try {
					Thread.sleep(l5);
				} catch (Exception localException) {
				}
			}
		}
	}

	private void parseLogFile(String paramString)
			throws IOException {
		this.startTime = -1L;
		BufferedReader localBufferedReader = new BufferedReader(new FileReader(paramString));
		String str;
		while ((str = localBufferedReader.readLine()) != null) {
			parseLine(str);
		}
		localBufferedReader.close();
	}

	private void parseLine(String paramString) {
		String[] arrayOfString = paramString.split("\t");
		long l = Long.parseLong(arrayOfString[0]);
		if (this.startTime == -1L) {
			this.startTime = l;
		}
		boolean bool1 = s_mouseEvents.contains(arrayOfString[1]);
		boolean bool2 = s_keyEvents.contains(arrayOfString[1]);
		if ((bool1) || (bool2)) {
			String str = null;
			for (int i = 2; i < arrayOfString.length; i++) {
				if (arrayOfString[i].startsWith("[")) {
					str = arrayOfString[i];
				}
			}
			Object localObject = null;
			if (bool1) {
				localObject = parseMouseEvent(l, str);
			} else {
				localObject = parseKeyEvent(l, str);
			}
			this.events.add(new EventEntry(l, (EventParams) localObject));
		}
	}

	public AWTEvent getEvent(long paramLong, EventParams paramEventParams) {
		if ((paramEventParams instanceof MouseParams)) {
			return getMouseEvent(paramLong, (MouseParams) paramEventParams);
		}
		if ((paramEventParams instanceof KeyParams)) {
			return getKeyEvent(paramLong, (KeyParams) paramEventParams);
		}
		return null;
	}

	public MouseEvent getMouseEvent(long paramLong, MouseParams paramMouseParams) {
		if (paramMouseParams.id != 507) {
			return new MouseEvent(this.display, paramMouseParams.id, paramLong, paramMouseParams.modifiers, paramMouseParams.x, paramMouseParams.y, paramMouseParams.clickCount, false, paramMouseParams.button);
		}
		return new MouseWheelEvent(this.display, paramMouseParams.id, paramLong, paramMouseParams.modifiers, paramMouseParams.x, paramMouseParams.y, paramMouseParams.clickCount, false, paramMouseParams.scrollType, paramMouseParams.scrollAmount, paramMouseParams.wheelRotation);
	}

	public KeyEvent getKeyEvent(long paramLong, KeyParams paramKeyParams) {
		return new KeyEvent(this.display, paramKeyParams.id, paramLong, paramKeyParams.modifiers, paramKeyParams.keyCode, paramKeyParams.keyChar);
	}

	public MouseParams parseMouseEvent(long paramLong, String paramString) {
		paramString = paramString.substring(1, paramString.length() - 1);
		String[] arrayOfString = paramString.split(",");
		MouseParams localMouseParams = new MouseParams();
		localMouseParams.id = Integer.parseInt(getValue(arrayOfString[0]));
		localMouseParams.x = Integer.parseInt(getValue(arrayOfString[1]));
		localMouseParams.y = Integer.parseInt(getValue(arrayOfString[2]));
		localMouseParams.button = Integer.parseInt(getValue(arrayOfString[3]));
		localMouseParams.clickCount = Integer.parseInt(getValue(arrayOfString[4]));
		localMouseParams.modifiers = Integer.parseInt(getValue(arrayOfString[5]));
		if (localMouseParams.id == 507) {
			localMouseParams.scrollType = Integer.parseInt(getValue(arrayOfString[6]));
			localMouseParams.scrollAmount = Integer.parseInt(getValue(arrayOfString[7]));
			localMouseParams.wheelRotation = Integer.parseInt(getValue(arrayOfString[8]));
		}
		return localMouseParams;
	}

	public KeyParams parseKeyEvent(long paramLong, String paramString) {
		paramString = paramString.substring(1, paramString.length() - 1);
		String[] arrayOfString = paramString.split(",");
		KeyParams localKeyParams = new KeyParams();
		localKeyParams.id = Integer.parseInt(getValue(arrayOfString[0]));
		localKeyParams.keyCode = Integer.parseInt(getValue(arrayOfString[1]));
		localKeyParams.keyChar = getValue(arrayOfString[2]).charAt(0);
		localKeyParams.modifiers = Integer.parseInt(getValue(arrayOfString[3]));
		return null;
	}

	private String getValue(String paramString) {
		return paramString.substring(paramString.indexOf("=") + 1);
	}

	static {
		s_mouseEvents.add("ITEM-DRAGGED");
		s_mouseEvents.add("ITEM-MOVED");
		s_mouseEvents.add("ITEM-WHEEL-MOVED");
		s_mouseEvents.add("ITEM-CLICKED");
		s_mouseEvents.add("ITEM-PRESSED");
		s_mouseEvents.add("ITEM-RELEASED");
		s_mouseEvents.add("ITEM-ENTERED");
		s_mouseEvents.add("ITEM-EXITED");
		s_mouseEvents.add("MOUSE-ENTERED");
		s_mouseEvents.add("MOUSE-EXITED");
		s_mouseEvents.add("MOUSE-PRESSED");
		s_mouseEvents.add("MOUSE-RELEASED");
		s_mouseEvents.add("MOUSE-CLICKED");
		s_mouseEvents.add("MOUSE-DRAGGED");
		s_mouseEvents.add("MOUSE-MOVED");
		s_mouseEvents.add("MOUSE-WHEEL-MOVED");
		s_keyEvents.add("KEY-PRESSED");
		s_keyEvents.add("KEY-RELEASED");
		s_keyEvents.add("KEY-TYPED");
		s_keyEvents.add("ITEM-KEY-PRESSED");
		s_keyEvents.add("ITEM-KEY-RELEASED");
		s_keyEvents.add("ITEM-KEY-TYPED");
	}

	public class KeyParams
			extends PrefuseEventPlayback.EventParams {
		int keyCode;
		char keyChar;

		public KeyParams() {
			super();
		}
	}

	public class MouseParams
			extends PrefuseEventPlayback.EventParams {
		int x;
		int y;
		int button;
		int clickCount;
		int scrollType;
		int scrollAmount;
		int wheelRotation;

		public MouseParams() {
			super();
		}
	}

	public class EventParams {
		int id;
		int modifiers;

		public EventParams() {
		}
	}

	public class EventEntry {
		long time;
		PrefuseEventPlayback.EventParams event;

		public EventEntry(long paramLong, PrefuseEventPlayback.EventParams paramEventParams) {
			this.time = paramLong;
			this.event = paramEventParams;
		}
	}
}


/* Location:              /home/vad/work/JAVA/2018.11.30/prefuse-apps.jar!/edu/berkeley/guir/prefuse/event/PrefuseEventPlayback.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */