/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.rest.test;

import org.junit.Test;

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

}
