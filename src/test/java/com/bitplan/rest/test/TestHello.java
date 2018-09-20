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
/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.rest.test;

import static org.junit.Assert.*;

import java.net.URI;

import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.rest.RestServer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * simple test for Hello Server
 * 
 * @author wf
 *
 */
public class TestHello extends TestHelloServer {

  @Test
  public void testHello() throws Exception {
    super.check("/hello/hello", "Hello");
  }

  @Ignore
  public void testEcho() throws Exception {
    // https://java.net/jira/browse/GRIZZLY-1377
    String values[] = { "World", "ÄÖÜßäöü" };
    for (String value : values) {
      super.check("/hello/hello/echo/" + value, value);
    }
  }

  @Ignore
  public void testUmlaute() throws Exception {
    super.startServer();
    URI uri = new URI("http://localhost:8085/hello/hello/echo/ÄÖÜßäöü");
    System.out.println(uri.toASCIIString());
    System.out.println(uri.toString());
    WebResource wrs = Client.create().resource(uri);
    String result = wrs.accept("text/html; charset=utf-8").get(String.class);
    assertEquals("ÄÖÜßäöü", result);
  }

  @Test
  public void testMultipleTestServers() throws Exception {
    debug = true;
    for (int i = 1; i <= 3; i++) {
      RestServer lServer = super.createServer();
      super.startServer(lServer);
      if (debug)
        System.out.println(lServer.getUrl());
    }
  }
  
  @Test
  public void testStopServer() {
    assertNotNull(rs);
    super.stopServer();
    assertNull(rs);
  }

  /**
   * in case of
   * java.net.BindException: Address already in use
   * the server should not be started but throw an exception
   * @throws Exception 
   */
  @Test
  public void testAdressAlreadyInUse() throws Exception {
    RestServer lServer;
    // create a second server
    lServer = super.createServer();
    // start it with not checking port binding
    super.startServer(lServer, false);
    assertNotNull(lServer);
    assertNotNull(lServer.getException());
    assertTrue(lServer.getException() instanceof java.net.BindException);
  }

}
