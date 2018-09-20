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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MediaType;

import org.junit.BeforeClass;
import org.junit.Test;

import com.bitplan.rest.UserManager;
import com.bitplan.rest.users.UserImpl;
import com.bitplan.rest.users.UserManagerImpl;

/**
 * simple test for Rythm Template based resource
 * 
 * @author wf
 *
 */
public class TestUserManagerResource extends TestHelloServer {
  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest.test");

  static UserManager um;

  /**
   * get the user manager with a few example users
   * @return - the usermanager
   */
  public static UserManager getUserManager() {
    if (um == null) {
      um = UserManagerImpl.getInstance();
      um.getUsers().clear();
      um.add(new UserImpl(um, "scott", "Scott", "Bruce",
          "bruce.scott@tiger.com", "tiger", "CEO", "since 2016-01"));
      um.add(new UserImpl(um, "scott", "Doe", "John", "john@doe.com",
          "mightymouse", "Janitor", "since 2017-01"));
      assertEquals(2, um.getUsers().size());
    }
    return um;
  }

  @BeforeClass
  public static void prepareUserManager() {
    um = getUserManager();
  }

  @Test
  public void testUserManageResource() throws Exception {
    debug = false;
    String path = "/hello/users";
    super.check(path, "Scott");
    String responseXml = getResponseString("application/xml; charset=UTF-8",
        path);
    if (debug) {
      LOGGER.log(Level.INFO, responseXml);
    }
    assertTrue(
        responseXml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\""));
    responseXml = getResponseString("text/html; charset=UTF-8", path + ".xml");
    assertTrue(
        responseXml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\""));
    String responseJson = getResponseString(
        MediaType.APPLICATION_JSON + "; charset=UTF-8", path);
    if (debug) {
      LOGGER.log(Level.INFO, responseJson);
    }
    assertTrue("Json", responseJson.contains("\"users\":{\"User\""));
    responseJson = getResponseString("text/html; charset=UTF-8",
        path + ".json");
    assertTrue("Json", responseJson.contains("\"users\":{\"User\""));
  }

}
