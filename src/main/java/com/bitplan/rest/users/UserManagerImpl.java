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
import com.bitplan.jaxb.JaxbFactoryApi;
import com.bitplan.jaxb.ManagerImpl;
import com.bitplan.rest.Crypt;
import com.bitplan.rest.CryptImpl;
import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;

@XmlRootElement(name="UserManager")
public class UserManagerImpl extends ManagerImpl<UserManagerImpl,User> implements UserManager {
  static JaxbFactory<UserManagerImpl> jaxbFactory;
  
  transient List<User> users=new ArrayList<User>();
  @XmlTransient
  Map<String,User> userById=new HashMap<String,User>();

  @XmlTransient
  private CryptImpl crypt;

  /**
   * @return the users
   */
  @XmlElementWrapper(name="users")
  @XmlElement(name="User", type=UserImpl.class)
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
   * @return the factory
   */
  public static JaxbFactory<UserManagerImpl> getJaxbFactory() {
    if (jaxbFactory==null) {
      jaxbFactory=new JaxbFactory<UserManagerImpl>(UserManagerImpl.class,ObjectFactory.class);
    }
    return jaxbFactory;
  }
  
  @Override
  public JaxbFactoryApi<UserManagerImpl> getFactory() {
    return getJaxbFactory();
  }
 

  @Override
  public void add(User user) {
    this.users.add(user);
    this.userById.put(user.getId(), user);
  }
  
  /**
   * get a user by the given id
   * @param id - the user id to lookup by
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
   * reinitialize the UserById Map
   */
  public void reinitUserById() {
    List<User> lusers = getUsers();
    for (User luser:lusers) {
      userById.put(luser.getId(), luser);
    }    
  }

  /**
   * create a user manager form the given xml string
   * @param xml - the xml string
   * @return the user manager
   * @throws Exception
   */
  public static UserManager fromXml(String xml) throws Exception {
    UserManagerImpl um=(UserManagerImpl) getJaxbFactory().fromXML(xml);
    um.reinitUserById();
    return um;
  }
  
  private static UserManagerImpl instance=null;
  
  /**
   * get a UserManager Instance
   * @return the instance
   */
  public static UserManagerImpl getInstance() {
    if (instance==null) {
      instance=new UserManagerImpl();
    }
    return instance;
  }

  @Override
  @XmlTransient
  public List<User> getElements() {
    return this.getUsers();
  }

}
