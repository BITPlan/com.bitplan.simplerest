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

import java.security.Principal;

import com.bitplan.rest.Crypt;
import com.bitplan.rest.XMLPresentable;

/**
 * general user interface
 * @author wf
 *
 */
public interface User extends XMLPresentable<User>, Principal{
  
  /**
   * @return the id
   */
  public abstract String getId();

  /**
   * @param id the id to set
   */
  public abstract void setId(String id);
  
  /**
   * @return the name
   */
  public abstract String getName();

  /**
   * @param name the name to set
   */
  public abstract void setName(String name);

  /**
   * @return the firstname
   */
  public abstract String getFirstname();

  /**
   * @param firstname the firstname to set
   */
  public abstract void setFirstname(String firstname);

  /**
   * @return the email
   */
  public abstract String getEmail();

  /**
   * @param email the email to set
   */
  public abstract void setEmail(String email);

  /**
   * @return the password
   */
  public abstract String getPassword();

  /**
   * @param password the password to set
   */
  public abstract void setPassword(String password);

  /**
   * @return the comment
   */
  public abstract String getComment();

  /**
   * @param comment the comment to set
   */
  public abstract void setComment(String comment);
  
  /**
   * security measure
   * @param crypt
   */
  public void encrypt(Crypt crypt);
  
  /**
   * decrypt me with the given crypt interface
   * @param crypt
   */
  public void deCrypt(Crypt crypt);
  
}
