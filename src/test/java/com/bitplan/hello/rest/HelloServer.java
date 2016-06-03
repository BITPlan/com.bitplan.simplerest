package com.bitplan.hello.rest;

import com.bitplan.rest.RestServerImpl;

/**
 * Hello Server
 * @author wf
 *
 */
public class HelloServer extends RestServerImpl {
  /**
   * construct Hello Server
   * setting defaults
   * @throws Exception 
   */
  public HelloServer() throws Exception {
    settings.setHost("0.0.0.0");
    settings.setPort(8111);
    String packages="com.bitplan.hello.resources;";
    settings.setContextPath("/hello");
    settings.addClassPathHandler("/", "/static/");
  // >>>{code}{SmartCRMConstructor}{SmartCRM}
  // <<<{code}{SmartCRMConstructor}{SmartCRM}
    settings.setPackages(packages);
   }
  
   /**
   * start Server
   * 
   * @param args
   * @throws Exception
   */
   public static void main(String[] args) throws Exception {
     HelloServer rs=new HelloServer();
     rs.settings.parseArguments(args);
     rs.startWebServer();
   } // main
}
