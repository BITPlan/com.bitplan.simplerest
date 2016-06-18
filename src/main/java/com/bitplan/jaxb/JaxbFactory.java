/**
 * Copyright (C) 2015-2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 *  * 
 */
package com.bitplan.jaxb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;

/**
 * gerneric JaxbFactory
 * 
 * @author wf
 *
 * @param <T>
 */
public class JaxbFactory<T> implements JaxbFactoryApi<T> {
  final Class<? extends T> classOfT;
  private JAXBContext context;
  @SuppressWarnings("rawtypes")
  private Class[] otherClasses=new Class[0];

  /**
   * allow access to the type that would otherwise not be available due to Java
   * type erasure
   * 
   * @return the classOfT
   */
  public Class<? extends T> getClassOfT() {
    return classOfT;
  }

  protected static java.util.logging.Logger LOGGER = java.util.logging.Logger
      .getLogger("com.bitplan.jaxb");

  /**
   * construct me for the given T class - workaround for java generics type
   * erasure
   * 
   * @param pClassOfT
   */
  public JaxbFactory(Class<? extends T> pClassOfT) {
    classOfT = pClassOfT;
  }
  
  /**
   * construct me with neighbour classes
   * @param pClassOfT
   * @param pOtherClasses
   */
  @SuppressWarnings("rawtypes")
  public JaxbFactory(Class<T> pClassOfT,Class...pOtherClasses) {
    this(pClassOfT);
    otherClasses=pOtherClasses;
  }
  
  /**
   * create a JAXBContext for the given contextPath and XML MetaData 
   * @param contextPath
   * @param xml
   * @return
   * @throws Exception
   */
  public static JAXBContext createJAXBContext(String contextPath,String xml) throws Exception {
    StringReader sr=new StringReader(xml);
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, sr);
    JAXBContext lcontext = JAXBContextFactory.createContext(contextPath, JaxbFactory.class.getClassLoader(), properties);
    return lcontext;
  }
  
  /**
   * get the JaxB Context for me
   * @return - the JaxB Context
   * @throws JAXBException 
   */
  @SuppressWarnings("rawtypes")
  public JAXBContext getJAXBContext() throws JAXBException {
    if (context==null) {
      // http://stackoverflow.com/questions/21185947/set-moxy-as-jaxb-provider-programatically
      Map<String, Object> properties = new HashMap<String, Object>();
      Class[] classes=new Class[otherClasses.length+1];
      int index=0;
      classes[index++]=classOfT;
      for (Class clazz:otherClasses) {
        classes[index++]=clazz;
      }
      context=JAXBContextFactory.createContext(classes, properties);
    }
    return context;
  }

  /**
   * get a fitting Unmarshaller
   * 
   * @return - the Unmarshaller for the classOfT set
   * @throws JAXBException
   */
  public Unmarshaller getUnmarshaller() throws JAXBException {
    JAXBContext lcontext = getJAXBContext();
    Unmarshaller u = lcontext.createUnmarshaller();
    u.setEventHandler(new ValidationEventHandler() {
      @Override
      public boolean handleEvent(ValidationEvent event) {
        return true;
      }
    });
    return u;
  }

  @SuppressWarnings("unchecked")
  public T fromString(Unmarshaller u, String s) throws Exception {
    // unmarshal the string to a Java object of type <T> (classOfT has the
    // runtime type)
    StringReader stringReader = new StringReader(s.trim());
    T result = null;
    // this step will convert from xml text to Java Object
    try {
      Object unmarshalResult = u.unmarshal(stringReader);
      if (classOfT.isInstance(unmarshalResult)) {
        result = (T) unmarshalResult;
      } else {
        String type = "null";
        if (unmarshalResult != null) {
          type = unmarshalResult.getClass().getName();
        }
        String msg = "unmarshalling returned " + type + " but "
            + classOfT.getName() + " was expected";
        throw new Exception(msg);
      }
    } catch (JAXBException jex) {
      String msg = "JAXBException: " + jex.getMessage();
      LOGGER.log(Level.SEVERE, msg);
      LOGGER.log(Level.SEVERE, s);
      throw (new Exception(msg, jex));
    }
    return result;
  }

  /**
   * get an instance of T for the given xml string
   * 
   * @param xml
   *          - the xml representation of the <T> instance
   * @return T
   * @throws Exception
   *           - if the conversion fails
   */
  public T fromXML(String xml) throws Exception {
    Unmarshaller u = this.getUnmarshaller();
    u.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
    T result = this.fromString(u, xml);
    return result;
  }

  /**
   * get an instance of T for the given json string
   * 
   * @param json
   *          - the json representation of the <T> instance
   * @return T
   * @throws Exception
   */
  public T fromJson(String json) throws Exception {
    Unmarshaller u = this.getUnmarshaller();
    u.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
    T result = this.fromString(u, json);
    return result;
  }

  /**
   * get a marshaller for the given <T> instance
   * 
   * @param instance
   *          - the instance to get a marshaller for
   * @return a marshaller for <T>
   * @throws JAXBException
   */
  public Marshaller getMarshaller(T instance) throws JAXBException {
    JAXBContext lcontext = getJAXBContext();
    Marshaller marshaller = lcontext.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    return marshaller;
  }

  /**
   * get the string representation of the given marshaller
   * 
   * @param marshaller
   * @param instance
   * @return the string representation for the given marshaller
   * @throws JAXBException
   */
  public String getString(Marshaller marshaller, T instance)
      throws JAXBException {
    StringWriter sw = new StringWriter();
    marshaller.marshal(instance, sw);
    String result = sw.toString();
    return result;
  }

  /**
   * create a Json representation for the given <T> instance
   * 
   * @param instance
   *          - the instance to convert to json
   * @return a Json representation of the given <T>
   * @throws JAXBException
   */
  public String asJson(T instance) throws JAXBException {
    Marshaller marshaller = getMarshaller(instance);
    marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
    marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
    String result = getString(marshaller, instance);
    return result;
  }

  /**
   * create an xml representation for the given <T> instance
   * 
   * @param instance
   *          - the instance to convert to xml
   * @return a xml representation of the given <T> instance
   * @throws JAXBException
   */
  @Override
  public String asXML(T instance) throws JAXBException {
    Marshaller marshaller = getMarshaller(instance);
    marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/xml");
    String result = getString(marshaller, instance);
    return result;
  }
}
