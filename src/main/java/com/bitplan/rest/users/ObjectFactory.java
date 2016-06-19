package com.bitplan.rest.users;

import javax.xml.bind.annotation.XmlRegistry;

import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;

/**
 * see
 * http://blog.bdoughan.com/2010/07/moxy-jaxb-map-interfaces-to-xml.html?
 * showComment=1331064265196#c8558952364670027314
 * 
 * @author wf
 *
 */
@XmlRegistry
public class ObjectFactory {

  public UserManagerImpl createUserManagerImpl() {
    return new UserManagerImpl();
  }

  public UserManager createUserManager() {
    return new UserManagerImpl();
  }
  
  public UserImpl createUserImpl() {
    return new UserImpl();
  }
  
  public User createUser() {
    return new UserImpl();
  }
}
