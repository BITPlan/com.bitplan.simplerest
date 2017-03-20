package com.bitplan.jaxb;

import java.io.File;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;

import com.bitplan.persistence.Manager;

/**
 * a Manager implementation based on JaxB xmlStorage
 * @author wf
 *
 * @param <MT>
 * @param <T>
 */
public abstract class ManagerImpl<MT,T> implements Manager<MT,T> {
  transient String xmlPath;
  transient File xmlFile;
  
  /**
   * save me to an xmlFile
   * 
   * @param xmlFile
   * @throws Exception
   */
  public void saveAsXML(File xmlFile) throws Exception {
    String xml = asXML();
    FileUtils.writeStringToFile(xmlFile, xml, "UTF-8");
  }
  
  public void save() throws Exception {
     saveAsXML(xmlFile);
  }
  
  @XmlTransient
  public String getXmlPath() {
    return xmlPath;
  }
  public void setXmlPath(String path) {
    xmlPath=path;
    xmlFile=new File(path);
  }
  
  public void setXmlFile(File xmlFile) {
    this.xmlFile=xmlFile;
  }
  
  public File getXmlFile() {
    return xmlFile;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String asJson() throws JAXBException {
    return getFactory().asJson((MT) this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public String asXML() throws JAXBException {
    return getFactory().asXML((MT) this);
  }
  
}
