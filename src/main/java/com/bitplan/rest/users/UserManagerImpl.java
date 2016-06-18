/**
 * Copyright (C) 2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.rest.users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.rest.Crypt;
import com.bitplan.rest.CryptImpl;

@XmlRootElement(name="UserManager")
public class UserManagerImpl implements UserManager {
  static JaxbFactory<UserManager> jaxbFactory;
  
  List<User> users=new ArrayList<User>();
  @XmlTransient
  Map<String,User> userById=new HashMap<String,User>();

  @XmlTransient
  private CryptImpl crypt;

  /**
   * @return the users
   */
  @XmlElementWrapper(name="users")
  @XmlElement(name="user")
  public List<User> getUsers() {
    return users;
  }

  /**
   * @param users the users to set
   */
  public void setUsers(List<User> users) {
    this.users = users;
  }
  
  /**
   * get the JAXBFactory for this class
   * @return
   */
  public static JaxbFactory<UserManager> getJaxbFactory() {
    if (jaxbFactory==null) {
      jaxbFactory=new JaxbFactory<UserManager>(UserManager.class,ObjectFactory.class);
    }
    return jaxbFactory;
  }
  
  @Override
  public String asXML() throws Exception {
    String xml=getJaxbFactory().asXML(this);
    return xml;
  }

  @Override
  public void add(User user) {
    user.encrypt(getCrypt());
    this.users.add(user);
  }
  
  /**
   * get a user by the given id
   * @param id
   * @return the user
   */
  public User getById(String id) {
    User user=this.userById.get(id);
    return user;
  }

  @Override
  public Crypt getCrypt() {
    if (crypt==null) {
      crypt=new CryptImpl("YzMhYb57ljt4pR3rbklA3w8V1NWdojRa","s5qzAZ9x");
    }
    return crypt;
  }

  /**
   * create a user manager form the given xml file
   * @param xml
   * @return the user manager
   * @throws Exception
   */
  public static UserManager fromXml(String xml) throws Exception {
    UserManager um=getJaxbFactory().fromXML(xml);
    /*List<User> lusers = um.getUsers();
    for (User luser:lusers) {
      System.out.println(luser.getId());
      // um.userById.put(luser.getId(), luser);
    } */
    return um;
  }

}
