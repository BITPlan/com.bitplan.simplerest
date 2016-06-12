/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.rest.test;

import org.junit.Test;

import com.bitplan.rest.RestServer;

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
