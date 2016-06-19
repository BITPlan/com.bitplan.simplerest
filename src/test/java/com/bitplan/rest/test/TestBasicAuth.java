/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.rest.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bitplan.hello.rest.HelloServer;
import com.bitplan.hello.rest.SecuredHelloServer;
import com.bitplan.rest.RestServer;
import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;
import com.bitplan.rest.users.UserImpl;
import com.bitplan.rest.users.UserManagerImpl;

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
    User user=new UserImpl();
    user.setId("scott");
    user.setPassword("tiger");
    user.setName("Scott");
    user.setFirstname("Bruce");
    setUser(user);
    super.check("/hello/hello", "Hello");
  }

}
