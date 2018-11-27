package ieg.util.xml;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

/**
 * Convert date time object to and from a XML compliant string.
 * 
 * <p>
 * The date format is closely related to the dates and times described in ISO
 * 8601 (see <a href="http://www.w3.org/TR/xmlschema-2/#dateTime">XML Schema 1.0
 * Part 2, Section 3.2.7</a>). Internally, {@link DatatypeFactory} is used,
 * because this format is not supported by {@link java.text.SimpleDateFormat}.
 * Further alternatives would be JodaTime or
 * {@link javax.xml.bind.DatatypeConverter}.
 * 
 * <p>
 * Instances of this class are not safe for use by multiple threads.
 * 
 * @author Rind
 * 
 */
public class XmlCalendarConverter {

    private GregorianCalendar cal;
    private DatatypeFactory factory;

    public XmlCalendarConverter() throws DatatypeConfigurationException {
        factory = DatatypeFactory.newInstance();
        cal = new GregorianCalendar();
    }

    public String toXML(long millis) {
        cal.setTimeInMillis(millis);
        return toXML(cal);
    }

    public String toXML(Date date) {
        cal.setTime(date);
        return toXML(cal);
    }

    public String toXML(GregorianCalendar cal) {
        return factory.newXMLGregorianCalendar(cal).toXMLFormat();
    }

    public GregorianCalendar toCalendar(String xml)
            throws IllegalArgumentException {
        return factory.newXMLGregorianCalendar(xml).toGregorianCalendar();
    }

    public Date toDate(String xml) {
        cal = toCalendar(xml);
        return cal.getTime();
    }

    public long toTimeInMillis(String xml) {
        cal = toCalendar(xml);
        return cal.getTimeInMillis();
    }
}
