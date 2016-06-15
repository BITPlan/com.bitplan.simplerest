/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.rest.test;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.rest.RestServer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * simple test for Hello Werver
 * @author wf
 *
 */
public class TestHello extends TestHelloServer{

  @Test
  public void testHello() throws Exception {
    super.check("/hello/hello", "Hello");
  }
  
  @Ignore
  public void testEcho() throws Exception {
    // https://java.net/jira/browse/GRIZZLY-1377
    String values[]={"World","ÄÖÜßäöü"};
    for (String value:values) {
      super.check("/hello/hello/echo/"+value,value);
    }
  }
  
  @Ignore
  public void testUmlaute() throws Exception {
    super.startServer();
    URI uri=new URI("http://localhost:8085/hello/hello/echo/ÄÖÜßäöü");
    System.out.println(uri.toASCIIString());
    System.out.println(uri.toString());
    WebResource wrs = Client.create().resource(uri);
    String result= wrs.accept("text/html; charset=utf-8").get(String.class);
    assertEquals("ÄÖÜßäöü",result);
  }
  
  @Test
  public void testMultipleTestServers() throws Exception {
    debug=true;
    for (int i=1;i<=3;i++) {
      RestServer lServer = super.createServer();
      super.startServer(lServer);
      if (debug)
        System.out.println(lServer.getUrl());
    }
  }

}
