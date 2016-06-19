/**
 * Copyright (C) 2014-2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
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
