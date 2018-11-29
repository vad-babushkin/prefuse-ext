package profusians.zonemanager.zone.io;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.URL;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import prefuse.util.ColorLib;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.zone.attributes.ZoneAttributes;
import profusians.zonemanager.zone.colors.ZoneColors;
import profusians.zonemanager.zone.shape.DefaultZoneShape;
import profusians.zonemanager.zone.shape.ZoneShape;

/**
 * This class implements a file handler, responsible for reading of the zone xml
 * file. See the demos coming along with the zone manager package and the
 * documentation for examples of zone xml files.
 * 
 * @author <a href="http://goosebumps4all.net">martin dudek</a>
 * 
 */

public class ZoneFileHandler {

    private static ZoneManager m_zoneManager;

    
    public static boolean addZonesFromFile( ZoneManager zoneManager,
	    String location) {
	return addZonesFromFile(zoneManager, location,false);
    }


    public static boolean addZonesFromFile( ZoneManager zoneManager,
	    FileReader fr) {

	return addZonesFromFile(zoneManager,new InputSource(fr),false);
    }

    
    public static boolean addZonesFromUrl( ZoneManager zoneManager,
	    URL zoneXmlUrl) {
	InputStreamReader fr = null;
	
	if (zoneXmlUrl == null) {
	    System.out.println("ZoneFileHandler.addZonesFromFile() method called with invalid URL: null");
	    return false;
	}

	try {

	    fr = new InputStreamReader(zoneXmlUrl.openStream());
	} catch ( Exception e) {
	    System.out.println("Problem while reading file "
		    + zoneXmlUrl.getFile() + " : " + e.getMessage());
	    return false;
	}
	return addZonesFromFile(zoneManager, new InputSource(fr),false);
    }
    
    public static boolean printZoneFileAsMethod(String location) {
	System.out.println("public void addZones(ZoneManager zManager) {");
	boolean result =  addZonesFromFile(m_zoneManager, location,true);
	System.out.println("}");
	return result;
    }


    private static boolean addZonesFromFile( ZoneManager zoneManager,
	    String location, boolean printMethodOnly) {	
	FileReader fr = null;
	try {
	    fr = new FileReader(location);
	} catch ( Exception e) {
	    System.out.println("Problem while reading file " + location + " : "
		    + e.getMessage());
	    return false;
	}
	return addZonesFromFile(zoneManager, new InputSource(fr),printMethodOnly);
    }

    private static boolean addZonesFromFile(ZoneManager zoneManager, InputSource fr, boolean printMethodOnly) {
	m_zoneManager = zoneManager;

	XMLReader myReader = null;

	try {

	    myReader = XMLReaderFactory.createXMLReader();

	    ZoneFileParser myContentHandler = new ZoneFileParser(printMethodOnly);

	    myReader.setContentHandler(myContentHandler);
	    myReader.setErrorHandler(myContentHandler);

	    myReader.parse(fr);

	} catch ( Exception e) {
	    System.out.println("Problem while parsing the zone xml file: "
		    + e.getMessage());
	    return false;
	}
	return true;
    }

    

    public static class ZoneFileParser extends DefaultHandler {
	boolean printMode;

	public ZoneFileParser() {
	    printMode = false;
	}

	public ZoneFileParser(boolean methodMode) {
	    // TODO Auto-generated constructor stub
	   printMode = methodMode;
	}


	public void startElement( String uri, String localName,
		String qName, Attributes attributes) {

	    try {
		if (qName.equalsIgnoreCase("zone")) {

		    String shape = attributes.getValue("shape");
		    String shapedata = attributes.getValue("shapedata");
		    String name = attributes.getValue("name");
		    String info = attributes.getValue("info");
		    String itemColorString = attributes
		    .getValue("itemcolor");
		    String fillColorString = attributes
		    .getValue("fillcolor");

		    if (info == null) {
			info = "";
		    }

		    boolean flexZone = isFlexible(attributes);
		    boolean flexZoneItems = hasFlexibleItems(attributes);

		    String grav = attributes.getValue("gravconstant");
		    float gravConstant = 0;

		    int itemColor = stringToColor(itemColorString);
		    int fillColor = stringToColor(fillColorString);

		    String zoneShapeClassName = "profusians.zonemanager.zone.shape."
			+ shape.substring(0, 1).toUpperCase()
			+ shape.substring(1).toLowerCase() + "ZoneShape";

		    Class zoneShapeClass = Class
		    .forName(zoneShapeClassName);

		    Constructor con = zoneShapeClass
		    .getConstructor(new Class[] { String.class });

		    ZoneShape zShape = (ZoneShape) con
		    .newInstance(new Object[] { shapedata });

		    if (grav != null) {
			gravConstant = Float.parseFloat(grav);
		    } else {
			gravConstant = DefaultZoneShape.getDefaultGravConst(zShape);
		    }

		    if (printMode) {
			String[] pa = zoneShapeClassName.split("\\."); 
			System.out.println("zManager.createAndAddZone(new " + pa[pa.length-1] + "(" + shapedata + "), new ZoneColors(" + itemColor + ", " + fillColor + "), new ZoneAttributes( \"" + name + "\", " + 
				flexZone + ", " +  flexZoneItems + ", " +  gravConstant + "f, \"" +  info + "\"));"); 
		    } else {
			m_zoneManager.createAndAddZone(zShape, new ZoneColors(
				itemColor, fillColor), new ZoneAttributes(name,
					flexZone, flexZoneItems, gravConstant, info));
		    }
		}
	    } catch ( Exception e) {
		System.out.println("problems while creating zone "
			+ e.getMessage());
	    }
	}

	public void endElement( String uri, String localName,
		String qName) {

	}

	public void characters( char[] ch, int start,
		int length) {

	}

	public void warning( SAXParseException e) {

	}

	public void error( SAXParseException e) {

	}

	public void fatalError( SAXParseException e) {
	    System.out.println(e.getMessage());
	}

	private int stringToColor( String colorAsString) {
	    if (colorAsString == null) {
		return 0;
	    }
	    String[] values = colorAsString.split(",");
	    if ((values.length < 3) || (values.length > 4)) {
		System.out.println("Problem while parsing color value ");
		return 0;
	    }
	    int[] numbers = new int[values.length];
	    for (int i = 0; i < values.length; i++) {
		numbers[i] = Integer.parseInt(values[i].trim());
	    }

	    if (values.length == 3) {
		return ColorLib.rgb(numbers[0], numbers[1], numbers[2]);
	    } else {
		return ColorLib.rgba(numbers[0], numbers[1], numbers[2],
			numbers[3]);
	    }

	}

	private boolean isFlexible( Attributes attributes) {
	    String flex = attributes.getValue("type");
	    if (flex == null) {
		return false;
	    } else if (flex.compareToIgnoreCase("flexible") == 0) {
		return true;
	    }
	    return false;
	}

	private boolean hasFlexibleItems( Attributes attributes) {
	    String flex = attributes.getValue("itemtype");
	    if (flex == null) {
		return false;
	    } else if (flex.compareToIgnoreCase("flexible") == 0) {
		return true;
	    }
	    return false;
	}

    }

}
