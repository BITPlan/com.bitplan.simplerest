package com.bitplan.rest.test;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;
import com.bitplan.rest.users.UserImpl;

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

}
