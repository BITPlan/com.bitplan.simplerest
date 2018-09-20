/**
 * Copyright (c) 2016-2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.simplerest
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bitplan.jaxb;

import java.io.File;
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

import org.apache.commons.io.FileUtils;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;

/**
 * generic JaxbFactory
 * 
 * using the Java Architecture for XML Binding JAXB
 * to convert Java objects XML and JSON documents and back
 * 
 * https://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding
 * 
 * @author wf
 *
 * @param <T>
 *          - the type to be marshalled and unmarshalled
 */
public class JaxbFactory<T> implements JaxbFactoryApi<T> {
  // since Java generics are implemented using type erasure
  // http://stackoverflow.com/questions/339699/java-generics-type-erasure-when-and-what-happens
  // we need to no the runtime type of the class to be handled by this factory
  final Class<? extends T> classOfT;

  // a JAXBContext can handle the marshalling and unmarshalling of a set of
  // classes
  // this is a specific JAXBContext for T with the type classOfT
  private JAXBContext context;

  // a set of "neighbour" classes may also be specified
  @SuppressWarnings("rawtypes")
  private Class[] otherClasses = new Class[0];

  public boolean novalidate=false;
  public boolean fragment=false;
  
  private Marshaller.Listener marshalListener=null;
  private Unmarshaller.Listener unmarshalListener=null;

  /**
   * @return the listener
   */
  public Marshaller.Listener getMarshalListener() {
    return marshalListener;
  }
  
  /**
   * @param marshalListener the marshalListener to set
   */
  public void setMarshalListener(Marshaller.Listener marshalListener) {
    this.marshalListener = marshalListener;
  }

  /**
   * @return the unmarshalListener
   */
  public Unmarshaller.Listener getUnmarshalListener() {
    return unmarshalListener;
  }

  /**
   * @param unmarshalListener the unmarshalListener to set
   */
  public void setUnmarshalListener(Unmarshaller.Listener unmarshalListener) {
    this.unmarshalListener = unmarshalListener;
  }

  /**
   * allow access to the type that would otherwise not be available due to Java
   * type erasure
   * 
   * @return the classOfT
   */
  public Class<? extends T> getClassOfT() {
    return classOfT;
  }

  // create a logger to be use for info and debug messages (if any)
  protected static java.util.logging.Logger LOGGER = java.util.logging.Logger
      .getLogger("com.bitplan.jaxb");

  /**
   * construct me for the given T class - workaround for java generics type
   * erasure
   * 
   * @param pClassOfT
   */
  public JaxbFactory(Class<? extends T> pClassOfT) {
    if (pClassOfT==null) {
      throw new IllegalArgumentException("JaxbFactory can not be instantiated with a null class");
    }
    classOfT = pClassOfT;
  }

  /**
   * construct me with the given neighbour classes pOtherClasses
   * 
   * @param pClassOfT
   * @param pOtherClasses
   */
  @SuppressWarnings("rawtypes")
  public JaxbFactory(Class<T> pClassOfT, Class... pOtherClasses) {
    this(pClassOfT);
    otherClasses = pOtherClasses;
  }

  /**
   * create a JAXBContext for the given contextPath and XML MetaData
   * 
   * @param contextPath
   * @param bindingXml
   * @return a JAXBContext
   * @throws Exception
   */
  public static JAXBContext createJAXBContext(String contextPath,
      String bindingXml) throws Exception {
    StringReader sr = new StringReader(bindingXml);
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, sr);
    JAXBContext lcontext = JAXBContextFactory.createContext(contextPath,
        JaxbFactory.class.getClassLoader(), properties);
    return lcontext;
  }

  /**
   * set the context based on the given contextPath and xmlBinding File
   * 
   * @param contextPath
   * @param xmlBinding
   * @throws Exception
   */
  public void setBinding(String contextPath, File xmlBinding) throws Exception {
    String bindingXml = FileUtils.readFileToString(xmlBinding);
    context = createJAXBContext(contextPath, bindingXml);
  }

  /**
   * get the JaxB Context for me
   * 
   * @return - the JaxB Context
   * @throws JAXBException
   */
  @SuppressWarnings("rawtypes")
  public JAXBContext getJAXBContext() throws JAXBException {
    if (context == null) {
      // http://stackoverflow.com/questions/21185947/set-moxy-as-jaxb-provider-programatically
      Map<String, Object> properties = new HashMap<String, Object>();
      Class[] classes = new Class[otherClasses.length + 1];
      int index = 0;
      classes[index++] = classOfT;
      for (Class clazz : otherClasses) {
        classes[index++] = clazz;
      }
      context = JAXBContextFactory.createContext(classes, properties);
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
    if (unmarshalListener!=null) {
      u.setListener(unmarshalListener);
    }
    if (novalidate) {
      u.setEventHandler(new ValidationEventHandler() {
        @Override
        public boolean handleEvent(ValidationEvent event) {
          return true;
        }
      });
    }
    return u;
  }

  /**
   * unmarshal the given string s using the unmarshaller u
   * 
   * @param u
   * @param s
   * @return - the Object of Type T
   * @throws Exception
   */
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
      Throwable ex=jex;
      if (jex.getLinkedException()!=null) {
        ex=jex.getLinkedException();
      }
      String msg = "JAXBException: " + ex.getMessage();
      LOGGER.log(Level.SEVERE, msg);
      LOGGER.log(Level.SEVERE, s);
      throw (new Exception(msg, ex));
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
    if (this.marshalListener!=null) {
      marshaller.setListener(marshalListener);
    }
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
    if (fragment) {
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
    }
    String result = getString(marshaller, instance);
    return result;
  }

}
