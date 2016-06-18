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

import javax.xml.bind.annotation.XmlRootElement;

import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.rest.Crypt;

@XmlRootElement(name = "User")
public class UserImpl implements User {
  static JaxbFactory<User> jaxbFactory;
  String id;
  String name;
  String firstname;
  String email;
  String password;
  String comment;

  /**
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   *          the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#getFirstname()
   */
  @Override
  public String getFirstname() {
    return firstname;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#setFirstname(java.lang.String)
   */
  @Override
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#getEmail()
   */
  @Override
  public String getEmail() {
    return email;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#setEmail(java.lang.String)
   */
  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#getPassword()
   */
  @Override
  public String getPassword() {
    return password;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#setPassword(java.lang.String)
   */
  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#getComment()
   */
  @Override
  public String getComment() {
    return comment;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.rest.User1#setComment(java.lang.String)
   */
  @Override
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * no args constructor
   */
  public UserImpl() {} // make JaxB happy;
  
  /**
   * create me from the given parameters
   * 
   * @param id
   * @param name
   * @param firstname
   * @param email
   * @param password
   * @param comment 
   */
  public UserImpl(String id, String name, String firstname, String email,
      String password, String comment) {
    this.id = id;
    this.name = name;
    this.firstname = firstname;
    this.email = email;
    this.password = password;
    this.comment=comment;
  }

  /**
   * get the JAXBFactory for this class
   * 
   * @return
   */
  public static JaxbFactory<User> getJaxbFactory() {
    if (jaxbFactory == null) {
      jaxbFactory = new JaxbFactory<User>(UserImpl.class);
    }
    return jaxbFactory;
  }

  @Override
  public String asXML() throws Exception {
    String xml = getJaxbFactory().asXML(this);
    return xml;
  }

  @Override
  public void encrypt(Crypt crypt) {
    try {
      this.password = crypt.encrypt(password);
    } catch (Throwable th) {
      throw new RuntimeException(th);
    }
  }

  @Override
  public void deCrypt(Crypt crypt) {
    try {
      this.password = crypt.decrypt(password);
    } catch (Throwable th) {
      throw new RuntimeException(th);
    }
  }

}
