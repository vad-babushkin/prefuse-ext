package evaluation.evalBench.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateHelper {
	private final StringBuilder m_sB = new StringBuilder();

	private final static Calendar cal = Calendar.getInstance();

	public static Date addDate(Date date, int amount, int type) {

		cal.setTime(date);
		// cal.add(field, amount)

		switch (type) {
		case Calendar.DATE:
			cal.add(Calendar.DATE, amount);

		}
		return cal.getTime();

	}

	public static Date addDate(long time, int amount, int type) {
		Date date = new Date(time);

		return addDate(date, amount, type);

	}

	public static long addTime(long time, int amount, int type) {
		Date date = new Date(time);
		return addDate(date, amount, type).getTime();

	}

	/**
	 * This method will return a useful string representation for the difference
	 * of tow given dates
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static List<String> getDifferenceBetweenDates(Date date1, Date date2) {

		// long difference = Math.abs(date1.getTime() - date2.getTime());
		//
		// long weeks = difference / (1000 * 60 * 60 * 24 * 7);
		//		
		// DateHelper dh = new DateHelper();
		//		
		//		
		//		
		// long days = difference / (1000 * 60 * 60 * 24);
		// long hours = difference / (1000 * 60 * 60);
		//
		// dh.appendTimeValue(" Wochen",weeks);
		//		
		// // sb.append("Zeitdifferenz: ");
		// // if(weeks != 0)
		// // {
		// // sb.append(weeks + " Wochen");
		// // }
		// //
		// // if(days != 0) {}
		//		

		DateHelper dh = new DateHelper();
		String s = dh.getDifferenceBetweenDates1(date1, date2);

		List<String> result = new LinkedList<String>();
		result.add(s);
		// result.add("Zeitdifferenz: ");
		// result.add("Wochen " + weeks);
		// result.add("Tage " + days);
		// result.add("Stunden " + hours);

		return result;
	}

	public String getDifferenceBetweenDates1(Date date1, Date date2) {
		m_sB.setLength(0);
		long tempDiff;
		long hours;

		long difference = Math.abs(date1.getTime() - date2.getTime());

		long weeks = difference / (1000 * 60 * 60 * 24 * 7);
		tempDiff = difference % (1000 * 60 * 60 * 24 * 7);

		long days = tempDiff / (1000 * 60 * 60 * 24);
		tempDiff = tempDiff % (1000 * 60 * 60 * 24);

		if (days != 0) {
			long hourDiff = tempDiff % (days * 1000 * 60 * 60 * 24);
			hours = hourDiff / (1000 * 60 * 60);

		}

		else {
			long hourDiff = tempDiff % (1000 * 60 * 60 * 24);
			hours = hourDiff / (1000 * 60 * 60);
		}

		appendTimeValue(" w, ", weeks);
		if (Locale.getDefault().getLanguage().equals("de"))
			appendTimeValue(" t, ", days);
		else
			appendTimeValue(" d, ", days);
		appendTimeValue(" h", hours);
//		m_sB.append(";");
		return m_sB.toString();

	}

	public String dateToString(Date date,String format) 
	{
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		SimpleDateFormat df = new SimpleDateFormat(format);
		df.setTimeZone(TimeZone.getDefault());
		
		return df.format(date);
	}
	
	
	public static String getCurrentTime(String format) {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }
	
	private void appendTimeValue(String text, long timeValue) {
		if (timeValue != 0) {
			m_sB.append(timeValue + " " + text);
		}

	}
}
