package ieg.util.xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

/**
 * Xml serializer with <a href="https://jaxb.dev.java.net">jaxb</a>
 * 
 * @author Alex Rind, Thomas Turic 
 */
public class JaxbMarshaller {

	/**
	 * loads an object from an XML file. The XML file has to be in UTF-8.
	 * 
	 * @param xmlFile
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
    public static Object load(String xmlFile, Class... clazz) {
		Object model = null;
		try {
			model = loadUser(xmlFile, clazz);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return model;
	}

	/**
	 * loads an object from an XML file without catching exceptions. 
	 * The XML file has to be in UTF-8.
	 * 
	 * @param xmlFile
	 * @param clazz
	 * @return
	 * @throws IOException
	 * @throws JAXBException
	 */
	@SuppressWarnings("rawtypes")
	public static Object loadUser(String xmlFile, Class... clazz)
			throws IOException, JAXBException {
	    return loadUser(new FileInputStream(xmlFile), clazz);
	}
		
    @SuppressWarnings("rawtypes")
    public static Object loadUser(URL xmlFile, Class... clazz)
            throws IOException, JAXBException {
        return loadUser(xmlFile.openStream(), clazz);
    }
        
	@SuppressWarnings("rawtypes")
	public static Object loadUser(InputStream xmlFile, Class... clazz) 
            throws IOException, JAXBException {
        Reader reader = new BufferedReader(new InputStreamReader(xmlFile,
                "UTF-8"));

        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller m = context.createUnmarshaller();

            Object model = m.unmarshal(reader);

            return model;
            
        } finally {
            reader.close();
        }
	}

	/**
	 * serializes an object and writes it to an XML file. The XML file is always
	 * in UTF-8.
	 * 
	 * @param xmlFile
	 * @param obj
	 */
    public static void save(String xmlFile, Object o) {
        save(xmlFile, o, o.getClass());
    }
    
    /**
     * serializes an object and writes it to an XML file. The XML file is always
     * in UTF-8.
     * 
     * @param xmlFile
     * @param obj
     * @param clazz
     */
	@SuppressWarnings("rawtypes")
    public static void save(String xmlFile, Object o, Class... clazz) {
		try {
			Writer writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(xmlFile), "UTF-8"));
			JAXBContext context = JAXBContext.newInstance(clazz);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(o, writer);
			writer.close();

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * generate a XML Schema file for a hierarchy if JAXB annotated classes. 
	 * @param xsdFile path of the output file
	 * @param classes annotated classes, typically the root class is sufficient  
	 * @throws JAXBException
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
    public static void generateSchema(final String xsdFile, Class... classes) throws JAXBException, IOException {
        class MySchemaOutputResolver extends SchemaOutputResolver {
            public Result createOutput(String namespaceUri,
                    String suggestedFileName) throws IOException {
                return new StreamResult(xsdFile);
            }
        }

        JAXBContext context = JAXBContext.newInstance(classes);
        context.generateSchema(new MySchemaOutputResolver());
    }
}
