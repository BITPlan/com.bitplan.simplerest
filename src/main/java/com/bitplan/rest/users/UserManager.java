/**
 * Copyright (C) 2011-2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.rest.users;

import java.util.List;

import com.bitplan.rest.Crypt;
import com.bitplan.rest.XMLPresentable;

/**
 * handler for User info and credentials
 */
public interface UserManager extends XMLPresentable<UserManager> {
  /**
   * get the users
   * @return
   */
  public List<User> getUsers();

  /**
   * add the given user
   * @param user
   */
  public void add(User user);
  
  /**
   * get a user by the given id
   * @param id
   * @return the user
   */
  public User getById(String id);
  
  /**
   * return the Crypt interface for this manager
   * @return
   */
  public Crypt getCrypt();
  
}
