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
package com.bitplan.rest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.jaxb.JaxbFactoryApi;
import com.bitplan.jaxb.JaxbPersistenceApi;

/**
 * RestServer Settings - JaxB Version
 * 
 * @author wf
 *
 */
@XmlRootElement(name="RestServerSettings")
public class RestServerSettingsImpl implements RestServerSettings,
    JaxbPersistenceApi<RestServerSettingsImpl> {

  @Option(name = "-contextPath", usage = "Sets the context Path for the server")
  String contextPath = "";

  @Option(name = "-host", usage = "Sets the hostname to run from")
  String host = "localhost";

  @Option(name = "-port", usage = "TCP Port to run from")
  protected int port = 8080;

  @Option(name = "-secure", usage = "true if https/SSL should be used")
  protected boolean secure = false;

  @Option(name = "-debug", usage = "true if debug information should be shown")
  protected boolean debug = false;

  @Option(name = "-wantClientAuth", usage = "true if SSL ClientAuthentication should be made available")
  protected boolean wantClientAuth = false;

  @Option(name = "-needClientAuth", usage = "true if SSL ClientAuthentication should be enforced")
  protected boolean needClientAuth = false;

  @Option(name = "-timeout", usage = "timeout in seconds after which server shuts itself down")
  long timeOut = 86400 * 365;

  @Option(name = "-packages", usage = "classpath entries to search entities")
  String packages;

  // @Option(name="-containerRequestFilters",usage="filters for all requests")
  String[] containerRequestFilters;

  @Option(name = "-persistencePropertyFileName", usage = "persistence property file name")
  String persistencePropertyFileName;

  @Option(name = "-config", usage = "configuration filename with command line options in XMLformat")
  String configfilename;
  
  @Option(name="-options", usage="application specific options")
  String options;

  public boolean testmode = false;

  /**
   * @return the contextPath
   */
  public String getContextPath() {
    return contextPath;
  }

  /**
   * @param contextPath
   *          the contextPath to set
   */
  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  /**
   * @return the debug
   */
  public boolean isDebug() {
    return debug;
  }

  /**
   * @param debug
   *          the debug to set
   */
  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerSettingsInterface#getHost()
   */
  @Override
  public String getHost() {
    return host;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.resthelper.RestServerSettingsInterface#setHost(java.lang.String
   * )
   */
  @Override
  public void setHost(String host) {
    this.host = host;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerSettingsInterface#getPort()
   */
  @Override
  public int getPort() {
    return port;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerSettingsInterface#setPort(int)
   */
  @Override
  public void setPort(int port) {
    this.port = port;
  }

  @Override
  public boolean isSecure() {
    return secure;
  }

  @Override
  public void setSecure(boolean pSecure) {
    secure = pSecure;
  }

  /**
   * @return the wantClientAuth
   */
  public boolean isWantClientAuth() {
    return wantClientAuth;
  }

  /**
   * @param wantClientAuth
   *          the wantClientAuth to set
   */
  public void setWantClientAuth(boolean wantClientAuth) {
    this.wantClientAuth = wantClientAuth;
  }

  /**
   * @return the needClientAuth
   */
  public boolean isNeedClientAuth() {
    return needClientAuth;
  }

  /**
   * @param needClientAuth
   *          the needClientAuth to set
   */
  public void setNeedClientAuth(boolean needClientAuth) {
    this.needClientAuth = needClientAuth;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerSettingsInterface#getTimeOut()
   */
  @Override
  public long getTimeOut() {
    return timeOut;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerSettingsInterface#setTimeOut(long)
   */
  @Override
  public void setTimeOut(long timeOut) {
    this.timeOut = timeOut;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerSettingsInterface#getPackages()
   */
  @Override
  public String getPackages() {
    return packages;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.resthelper.RestServerSettingsInterface#setPackages(java.lang
   * .String)
   */
  @Override
  public void setPackages(String packages) {
    this.packages = packages;
  }

  /**
   * @return the options
   */
  public String getOptions() {
    return options;
  }

  /**
   * @param options the options to set
   */
  public void setOptions(String options) {
    this.options = options;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.restinterface.RestServerSettings#getContainerRequestFilters()
   */
  @Override
  public String[] getContainerRequestFilters() {
    return containerRequestFilters;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.restinterface.RestServerSettings#setContainerRequestFilters
   * (java.lang.String)
   */
  @Override
  public void setContainerRequestFilters(String[] containerRequestFilters) {
    this.containerRequestFilters = containerRequestFilters;
  }

  /**
   * @return the persistence property file name
   */
  public String getPersistencePropertyFileName() {
    return persistencePropertyFileName;
  }

  /**
   * @param pName
   *          the the persistence property file name to set
   */
  public void setPersistencePropertyFileName(String pName) {
    this.persistencePropertyFileName = pName;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.resthelper.RestServerSettingsInterface#parseArguments(java.
   * lang.String[])
   */
  @Override
  public void parseArguments(String[] args) {
    CmdLineParser parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
      if (configfilename != null)
        this.fromXMLFile(new File(configfilename));
    } catch (Exception e) {
      // handling of wrong arguments
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      if (!testmode)
        System.exit(1);
    }
  }

  /**
   * readme from the given XML file
   * @param file
   * @throws Exception
   */
  public void fromXMLFile(File file) throws Exception {
    String xml=FileUtils.readFileToString(file);
    RestServerSettings s = getFactory().fromXML(xml);
    fromOther(s);
  }
  
  /**
   * initialize me from another set of Settings
   * @param s
   */
  public void fromOther(RestServerSettings s) {
    this.setContainerRequestFilters(s.getContainerRequestFilters());
    this.setContextPath(s.getContextPath());
    this.setDebug(s.isDebug());
    this.setHost(s.getHost());
    this.setNeedClientAuth(s.isNeedClientAuth());
    this.setPackages(s.getPackages());
    this.setPersistencePropertyFileName(s.getPersistencePropertyFileName());
    this.setPort(s.getPort());
    this.setSecure(s.isSecure());
    this.setTimeOut(s.getTimeOut());
    this.setWantClientAuth(s.isWantClientAuth());
  }

  /**
   * add the given class Path Handler
   */
  Map<String, String> classPathHandlers = new HashMap<String, String>();

  /**
   * access to user authentication information
   */
  private UserManager userManager;

  @Override
  public void addClassPathHandler(String directoryRoot, String classpathPath)
      throws Exception {
    if (classPathHandlers.containsKey(directoryRoot))
      throw new Exception("duplicate directoryRoot '" + directoryRoot + "'");
    classPathHandlers.put(directoryRoot, classpathPath);
  }

  @Override
  public Map<String, String> getClassPathHandlers() {
    return classPathHandlers;
  }
  
  @Override
  public JaxbFactoryApi<RestServerSettingsImpl> getFactory() {
    return new JaxbFactory<RestServerSettingsImpl>(RestServerSettingsImpl.class);
  }

  @Override
  public String asJson() throws JAXBException {
    String result=getFactory().asJson(this);
    return result;
  }

  @Override
  public String asXML() throws JAXBException {
    String result=getFactory().asXML(this);
    return result;
  }

  @Override
  public void setUserManager(UserManager userManager) {
     this.userManager=userManager;
  }

  @Override
  public UserManager getUserManager() {
    return userManager;
  }

}
