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

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.bitplan.datatypes.DefaultTypeConverter;
import com.bitplan.datatypes.TypeConverter;
import com.bitplan.jaxb.JaxbFactory;
import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;

@XmlRootElement(name = "User")
public class UserImpl implements User {
  static JaxbFactory<User> jaxbFactory;
  String id;
  String name;
  String firstname;
  String email;
  String password;
  String comment;
  String role;
  boolean encrypted = false;
  transient TypeConverter tc = new DefaultTypeConverter();

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
   * @return the role
   */
  public String getRole() {
    return role;
  }

  /**
   * @param role
   *          the role to set
   */
  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public void setTypeConverter(TypeConverter typeConverter) {
    tc = typeConverter;

  }

  @Override
  @XmlTransient
  public TypeConverter getTypeConverter() {
    return tc;
  }

  /**
   * create me from the given parameters
   * 
   * @param um - the Usermanager to use
   * @param id - the id of the user
   * @param name - the last name of the user e.g. Scott
   * @param firstname - e.g. Bruce
   * @param email - e.g. bruce.scott@tiger.com
   * @param password - encrypted or unencrypted password depending no settings
   * @param role - the role of the user e.g. CEO
   * @param comment - any comment
   */
  public UserImpl(UserManager um, String id, String name, String firstname,
      String email, String password, String role, String comment) {
    this.id = id;
    this.name = name;
    this.firstname = firstname;
    this.email = email;
    try {
      this.password = um.getCrypt().encrypt(password);
    } catch (Throwable th) {
      throw new RuntimeException(th);
    }
    this.role = role;
    this.comment = comment;
  }

  public UserImpl() {
  } // makes JaxB happy

  /**
   * get the JAXBFactory for this class
   * 
   * @return the factory
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
  public void fromMap(Map<String, String> map) {
    if (map.containsKey("id"))
      this.setId(tc.getString(map.get("id")));
    else
      this.setId(null);
    if (map.containsKey("name"))
      this.setName(tc.getString(map.get("name")));
    else
      this.setName(null);
    if (map.containsKey("firstname"))
      this.setFirstname(tc.getString(map.get("firstname")));
    else
      this.setFirstname(null);
    if (map.containsKey("email"))
      this.setEmail(tc.getString(map.get("email")));
    else
      this.setEmail(null);
    if (map.containsKey("password"))
      this.setPassword(tc.getString(map.get("password")));
    else
      this.setPassword(null);
    if (map.containsKey("comment"))
      this.setComment(tc.getString(map.get("comment")));
    else
      this.setComment(null);
    if (map.containsKey("role"))
      this.setRole(tc.getString(map.get("role")));
    else
      this.setRole(null);
  }

  @Override
  public Map<String, String> asMap() {
    Map<String, String> result = new HashMap<String, String>();
    result.put("id", tc.fromNullValue(this.getId()));
    result.put("name", tc.fromNullValue(this.getName()));
    result.put("firstname", tc.fromNullValue(this.getFirstname()));
    result.put("email", tc.fromNullValue(this.getEmail()));
    result.put("password", tc.fromNullValue(this.getPassword()));
    result.put("comment", tc.fromNullValue(this.getComment()));
    result.put("role", tc.fromNullValue(this.getRole()));
    return result;
  }

}
