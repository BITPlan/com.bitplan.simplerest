/**
 * Copyright (C) 2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.jaxb;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 * wrap a Map of a generic Type
 * http://stackoverflow.com/questions/13272288/is-it-possible-to-
 * programmatically-configure-jaxb/13273022#13273022
 * 
 * @author wf
 *
 * @param <T>
 */
public class MapWrap<T> {
  public static boolean debug=true;
  protected Logger LOGGER = Logger.getLogger("com.bitplan.jaxb");

  private List<T> items = new ArrayList<T>();

  @XmlAnyElement(lax = true)
  public List<T> getItems() {
    return items;
  }

  @XmlTransient
  Map<String, T> map;

  @SuppressWarnings("rawtypes")
  @XmlTransient
  protected Class itemClazz;
  @SuppressWarnings("rawtypes")
  @XmlTransient
  protected Class clazz;
  @XmlTransient
  protected Field keyField;

  @SuppressWarnings("rawtypes")
  private static JaxbFactory jaxbFactory;

  /**
   * zero argument constructor to make JaxB happy
   */
  public MapWrap() {

  }

  /**
   * initialize me from a map
   * 
   * @param map
   *          - the map
   */
  public MapWrap(Map<String, T> map) {
    // remember the map for debugging and knowing whether it is null
    this.map = map;
    clazz = this.getClass();
    // we need a map to continue
    if (map != null) {
      // loop over all map entries (in order if it is a LinkedHashMap)
      for (Entry<String, T> entry : map.entrySet()) {
        // get the value
        T value = entry.getValue();
        // put it into the items list so we can marshal it
        items.add(value);
        // if we don't have the type information yet get it
        if (clazz == null) {
          setItemClass(value.getClass());
        }
      }
    }
  }
  
  /**
   * set my Class
   * 
   * @param clazz
   * 
   */
  @SuppressWarnings("rawtypes")
  public void setItemClass(Class clazz) {
    this.itemClazz = clazz;
    for (Field field : clazz.getDeclaredFields()) {
      for (Annotation a : field.getAnnotations()) {
        if (a.annotationType() == XmlID.class) {
          keyField = field;
        }
      }
    }
    if (keyField == null) {
      throw new RuntimeException("MapWrap needs an XmlID field in "
          + clazz.getName() + " to work");
    }
  }

  /**
   * get the JAXBFactory for the wrapped class
   * 
   * @return
   */
  @SuppressWarnings("unchecked")
  public JaxbFactory<MapWrap<T>> getJaxbFactory() {
    if (jaxbFactory == null) {
      if (itemClazz == null) {
        throw new IllegalStateException("itemClass may not be null");
      }
      jaxbFactory = new JaxbFactory<MapWrap<T>>(clazz, itemClazz);
      jaxbFactory.fragment = true;
    }
    return (JaxbFactory<MapWrap<T>>) jaxbFactory;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public JAXBElement<MapWrap> unmarshal(String xml) throws JAXBException {
    Unmarshaller unmarshaller = getJaxbFactory().getUnmarshaller();
    StringReader stringReader = new StringReader(xml.trim());
    Object result = unmarshaller.unmarshal(stringReader);
    return (JAXBElement<MapWrap>) result;
  }
  
  /**
   * Unmarshal XML to MapWrap and return List value.
   */
  private static <T> List<T> unmarshal(Unmarshaller unmarshaller,
          Class<T> clazz, String xmlLocation) throws JAXBException {
      StreamSource xml = new StreamSource(xmlLocation);
      MapWrap<T> MapWrap = (MapWrap<T>) unmarshaller.unmarshal(xml,
              MapWrap.class).getValue();
      return MapWrap.getItems();
  }

  /**
   * Wrap map in MapWrap, then leverage JAXBElement to supply root element 
   * information.
   * @param marshaller
   * @param map
   * @param name
   * @throws JAXBException
   */
  private static JAXBElement<MapWrap>getJAXBElement (Map<String,?> map, String name)
          throws JAXBException {
      QName qName = new QName(name);
      MapWrap<?> mapWrap = new MapWrap(map);
      JAXBElement<MapWrap> jaxbElement = new JAXBElement<MapWrap>(qName,
              MapWrap.class, mapWrap);
      return jaxbElement;
  }

  /**
   * get me as XML
   * 
   * @return the xml representation
   * @throws Exception
   */
  public String asXML() throws Exception {
    if (debug) {
      LOGGER.log(Level.INFO, "asXML for " + items.size());
    }
    JAXBElement<MapWrap> jbe = getJAXBElement(map,"items");
    Marshaller marshaller = getJaxbFactory().getMarshaller(this);
    String xml=getJaxbFactory().getString(marshaller, this);
    /*
     * String xml = "";
     * String delim = "";
     * for (Entry<String, T> entry : map.entrySet()) {
     * String xmlnode = "";
     * try {
     * xmlnode = getJaxbFactory().asXML(entry.getValue());
     * } catch (Exception ex) {
     * LOGGER.log(Level.SEVERE, ex.getMessage());
     * throw ex;
     * }
     * xml += delim + xmlnode;
     * delim = "\n";
     * }
     */
    return xml;
  }

  /**
   * create a Map of Ts from the given XML
   * 
   * @return a Map of elements
   * @throws Exception
   */
  public Map<String, T> fromXML(String xml) throws Exception {
    MapWrap<T> mapwrap = getJaxbFactory().fromXML(xml);
    /*
     * Map<String, T> map = new HashMap<String, T>();
     * if (clazz != null) {
     * String[] nodes = xml.split("(?=<" + clazz.getSimpleName().toLowerCase()
     * + ">)");
     * for (String node : nodes) {
     * // check that node is not empty
     * if (!"".equals(node.trim())) {
     * T t = getJaxbFactory().fromXML(node);
     * String key = (String) keyField.get(t);
     * map.put(key, t);
     * }
     * }
     * }
     */
    return map;
  }
}
