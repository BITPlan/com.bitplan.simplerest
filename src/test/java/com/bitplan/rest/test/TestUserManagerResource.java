/**
 * Copyright (c) 2016-2017 BITPlan GmbH
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

  @Test
  public void testUserManageResource() throws Exception {
    UserManager um=UserManagerImpl.getInstance();
    um.getUsers().clear();
    um.add(new UserImpl(um,"scott","Scott","Bruce","bruce.scott@tiger.com","tiger","CEO","since 2016-01"));
    um.add(new UserImpl(um,"scott","Doe","John","john@doe.com","mightmouse","Janitor","since 2017-01"));
    assertEquals(2,um.getUsers().size());
    // debug=true;
    super.check("/hello/users", "Scott");
  }

}