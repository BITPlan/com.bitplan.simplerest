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

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * http://www.eclipse.org/eclipselink/documentation/2.6/moxy/
 * advanced_concepts006.htm
 * 
 * @author wf
 *
 */
public class MapAdapter<T> extends XmlAdapter<List<T>, Map<String, T>> {
  
  @Override
  public String marshal(Map<String, T> map) throws Exception {
    MapWrap<T> mapwrap=new MapWrap<T>(map);
    String xml=mapwrap.asXML();
    return xml;
  }

  @Override
  public Map<String, T> unmarshal(String xml) throws Exception {
    MapWrap<T> mapwrap=new MapWrap<T>(null);
    Map<String, T> map = mapwrap.fromXML(xml);
    return map;
  }

}
