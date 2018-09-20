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
package com.bitplan.hello.rest;

import com.bitplan.rest.UserManager;
import com.bitplan.rest.users.UserImpl;
import com.bitplan.rest.users.UserManagerImpl;

/**
 * the HelloServer with BasicAuthentication enabled
 * 
 * @author wf
 */
public class SecuredHelloServer extends HelloServer {
  /**
   * get the UserManager
   * 
   * @return
   */
  public UserManager getUserManager() {
    UserManager um = new UserManagerImpl();
    try {
      um.add(new UserImpl(um,"scott", "Bruce", "Scott", "bruce.scott@oracle.com",
          "tiger", "admin",
          "Scott's password is in the public domain"));
    } catch (Throwable th) {
      throw new RuntimeException(th);
    }
    return um;
  }

  /**
   * create a secured hello Server
   * 
   * @throws Exception
   */
  public SecuredHelloServer() throws Exception {
    super();
    this.getSettings().setSecure(true);
    this.getSettings().setUserManager(getUserManager());
  }

  /**
   * start Server
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    SecuredHelloServer rs = new SecuredHelloServer();
    rs.settings.parseArguments(args);
    rs.startWebServer();
  } // main
}
