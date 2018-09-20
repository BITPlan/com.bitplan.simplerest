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

import org.junit.Test;

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
  
  /**
   * get exampleUser
   * @return
   */
  public static User getExampleUserScott() {
    UserManager um=UserManagerImpl.getInstance();
    User user=new UserImpl(um,"scott","Scott","Bruce","scott@tiger.com","tiger","CEO","since 2016-01");
    um.add(user);
    return user;
  }
  
  
  @Test
  public void testBasicAuth() throws Exception {
    // debug=true;
    User user=getExampleUserScott();
    // we need the unencrypted password here
    user.setPassword("tiger");
    setUser(user);
    super.check("/hello/hello", "Hello");
  }

}
