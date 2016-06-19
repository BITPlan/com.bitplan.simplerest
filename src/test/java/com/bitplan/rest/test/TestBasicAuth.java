/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.rest.test;

import org.junit.Test;

import com.bitplan.hello.rest.SecuredHelloServer;
import com.bitplan.rest.RestServer;
import com.bitplan.rest.User;
import com.bitplan.rest.users.UserImpl;

/**
 * test basic authentication
 * @author wf
 *
 */
public class TestBasicAuth extends TestHelloServer {
  
  
  @Override
  public RestServer createServer() throws Exception {
    RestServer result = new SecuredHelloServer();
    return result;
  }
  
  @Test
  public void testBasicAuth() throws Exception {
    debug=true;
    User user=new UserImpl();
    user.setId("scott");
    user.setPassword("tiger");
    user.setName("Scott");
    user.setFirstname("Bruce");
    setUser(user);
    super.check("/hello/hello", "Hello");
  }

}
