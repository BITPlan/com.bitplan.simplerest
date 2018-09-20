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

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FileUtils;

import com.bitplan.persistence.Manager;

/**
 * a Manager implementation based on JaxB xmlStorage
 * 
 * @author wf
 *
 * @param <MT>
 * @param <T>
 */
public abstract class ManagerImpl<MT, T> implements Manager<MT, T> {
  transient String xmlPath;
  transient File xmlFile;

  /**
   * save me to an xmlFile
   * 
   * @param xmlFile
   * @throws Exception
   */
  public void saveAsXML(File xmlFile) throws Exception {
    sort();
    String xml = asXML();
    FileUtils.writeStringToFile(xmlFile, xml, "UTF-8");
  }

  /**
   * sort and save me
   */
  public void save() throws Exception {
    saveAsXML(xmlFile);
  }

  /**
   * override this function to sort
   */
  public void sort() {

  }

  @XmlTransient
  public String getXmlPath() {
    return xmlPath;
  }

  public void setXmlPath(String path) {
    xmlPath = path;
    xmlFile = new File(path);
  }

  public void setXmlFile(File xmlFile) {
    this.xmlFile = xmlFile;
  }

  @XmlTransient
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
