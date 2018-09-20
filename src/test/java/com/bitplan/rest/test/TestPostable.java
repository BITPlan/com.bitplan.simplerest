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
package com.bitplan.rest.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;
import com.bitplan.rest.resources.TemplateResource;
import com.bitplan.rest.users.UserImpl;

/**
 * test the postable interface
 * @author wf
 *
 */
public class TestPostable {
  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest.test");
  boolean debug=false;
  
  @Test
  public void testXML() throws Exception {
    UserManager um = TestUserManagerResource.getUserManager();
    User user=um.getUsers().get(0);
    String xml=user.asXML();
    if (debug)
      LOGGER.log(Level.INFO,xml);
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<User>\n" + 
        "   <comment>since 2016-01</comment>\n" + 
        "   <email>bruce.scott@tiger.com</email>\n" + 
        "   <firstname>Bruce</firstname>\n" + 
        "   <id>scott</id>\n" + 
        "   <name>Scott</name>\n" + 
        "   <password>MpllhlgPJvQ=</password>\n" + 
        "   <role>CEO</role>\n" + 
        "</User>\n",xml);
    xml=um.asXML();
    if (debug)
      LOGGER.log(Level.INFO,xml);
  }
  
  /**
   * test the postable interface
   */
  @Test
  public void testPostable() {
    debug=false;
    UserManager um = TestUserManagerResource.getUserManager();
    User user = um.getUsers().get(0);
    Map<String, String> map = user.asMap();
    if (debug)
      for (String key : map.keySet()) {
        LOGGER.log(Level.INFO, key + "=" + map.get(key));
      }
    assertEquals("scott",map.get("id"));
    assertEquals("Bruce",map.get("firstname"));
    assertEquals("CEO",map.get("role"));
    assertEquals("since 2016-01",map.get("comment"));
    assertEquals("bruce.scott@tiger.com",map.get("email"));
    // FIXME check password which should be decryptable
    User newUser=new UserImpl();
    newUser.fromMap(map);
    assertEquals("scott",newUser.getId());
    assertEquals("Bruce",newUser.getFirstname());
    assertEquals("CEO",newUser.getRole());
    assertEquals("since 2016-01",newUser.getComment());
    assertEquals("bruce.scott@tiger.com",newUser.getEmail());
  }
  
  @Test
  public void testImplode() {
    List<String> slist=new ArrayList<String>();
    slist.add("s1");
    slist.add("s2");
    String imploded=TemplateResource.implode(slist, "-");
    assertEquals("s1-s2",imploded);
  }

}
