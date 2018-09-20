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
package com.bitplan.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;
import com.bitplan.rest.users.ObjectFactory;
import com.bitplan.rest.users.UserImpl;
import com.bitplan.rest.users.UserManagerImpl;

import example.Company;
import example.Employee;
import example.EmployeeType;

/**
 * test the JaxBFactory
 * 
 * @author wf
 *
 */
public class TestJaxbFactory {
  protected Logger LOGGER = Logger.getLogger("com.bitplan.jaxb");

  protected boolean debug = false;
  boolean moxy = true;

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Customer {
    String name;
    String firstname;
    @XmlElementWrapper(name = "orders")
    @XmlElement(name = "order")
    List<Order> orders = new ArrayList<Order>();
  }

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Order {
    @XmlID
    String orderId;
    String item;
    int count;
  }

  @XmlRootElement(name = "customer")
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class CustomerWithMap {
    String name;
    String firstname;
    @XmlElementWrapper(name = "orders")
    @XmlElement(name = "order")
    List<Order> orders = new ArrayList<Order>();

    @XmlTransient
    private Map<String, Order> ordermap = new LinkedHashMap<String, Order>();

    public void reinit() {
      for (Order order : orders) {
        ordermap.put(order.orderId, order);
      }
    }
  }

  @Test
  public void testVersion() throws JAXBException {
    JaxbFactory<Customer> customerFactory = new JaxbFactory<Customer>(
        Customer.class);
    // String jaxbContext = JAXBContext.newInstance(Customer.class)
    String jaxbContextType = customerFactory.getJAXBContext().getClass()
        .getName();
    if (debug) {
      LOGGER.log(Level.INFO, jaxbContextType);
    }
    // Oracle/Sun default:
    String expected = "com.sun.xml.bind.v2.runtime.JAXBContextImpl";
    if (moxy)
      expected = "org.eclipse.persistence.jaxb.JAXBContext"; // jersey-moxy
    assertEquals("Expecting configured JaxB implementation", expected,
        jaxbContextType);
  }

  /**
   * add the given number of orders to the given list of orders
   * 
   * @param orders
   * @param count
   */
  public void addOrders(List<Order> orders, int count) {
    for (int i = 1; i <= count; i++) {
      Order order = new Order();
      order.item = "Item " + i;
      order.count = i;
      order.orderId = "Id" + i;
      orders.add(order);
    }
  }

  @Test
  public void testJaxbFactory() throws Exception {
    debug = false;
    JaxbFactory<Customer> customerFactory = new JaxbFactory<Customer>(
        Customer.class, Order.class);
    Customer customer = new Customer();
    customer.name = "Doe";
    customer.firstname = "John";
    addOrders(customer.orders, 3);
    String xml = customerFactory.asXML(customer);
    if (debug) {
      LOGGER.log(Level.INFO, "'" + xml + "'");
    }
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<customer>\n" + "   <name>Doe</name>\n"
        + "   <firstname>John</firstname>\n" + "   <orders>\n"
        + "      <order>\n" + "         <orderId>Id1</orderId>\n"
        + "         <item>Item 1</item>\n" + "         <count>1</count>\n"
        + "      </order>\n" + "      <order>\n"
        + "         <orderId>Id2</orderId>\n"
        + "         <item>Item 2</item>\n" + "         <count>2</count>\n"
        + "      </order>\n" + "      <order>\n"
        + "         <orderId>Id3</orderId>\n"
        + "         <item>Item 3</item>\n" + "         <count>3</count>\n"
        + "      </order>\n" + "   </orders>\n" + "</customer>\n" + "", xml);
    Customer customer2 = customerFactory.fromXML(xml);
    assertNotNull(customer2);
    assertEquals("Doe", customer2.name);
    assertEquals("John", customer2.firstname);
    String json = customerFactory.asJson(customer);
    if (debug) {
      LOGGER.log(Level.INFO, json);
    }
    assertEquals("{\n" + "   \"customer\" : {\n"
        + "      \"name\" : \"Doe\",\n" + "      \"firstname\" : \"John\",\n"
        + "      \"orders\" : {\n" + "         \"order\" : [ {\n"
        + "            \"orderId\" : \"Id1\",\n"
        + "            \"item\" : \"Item 1\",\n"
        + "            \"count\" : 1\n" + "         }, {\n"
        + "            \"orderId\" : \"Id2\",\n"
        + "            \"item\" : \"Item 2\",\n"
        + "            \"count\" : 2\n" + "         }, {\n"
        + "            \"orderId\" : \"Id3\",\n"
        + "            \"item\" : \"Item 3\",\n"
        + "            \"count\" : 3\n" + "         } ]\n" + "      }\n"
        + "   }\n" + "}", json);
    Customer customer3 = customerFactory.fromJson(json);
    assertNotNull(customer3);
    assertEquals("Doe", customer3.name);
    assertEquals("John", customer3.firstname);
  };

  /**
   * unmarshal the UserManager from the given JAXBContext
   * 
   * @param jaxbContext
   * @param xml
   * @return
   * @throws Exception
   */
  public UserManager unmarshalFromContext(
      javax.xml.bind.JAXBContext jaxbContext, String xml) throws Exception {
    StringReader stringReader = new StringReader(xml);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    UserManager um = (UserManager) unmarshaller.unmarshal(stringReader);
    return um;
  }

  /**
   * get the UserManager
   * 
   * @return
   */
  public UserManager getUserManager() {
    UserManager um = new UserManagerImpl();
    um.add(new UserImpl(um, "jd001", "John", "Doe", "john@doe.org",
        "badpassword", "admin", "John is our admin"));
    um.add(new UserImpl(um, "bs001", "Bill", "Smith", "bill@smith.com",
        "simplesecret", "secretary", "Bill is our secretary"));
    return um;
  }

  /**
   * get an xml representation of the UserManager
   * 
   * @return
   * @throws Exception
   */
  public String getUserManagerXml() throws Exception {
    UserManager um = getUserManager();
    String xml = um.asXML();
    if (debug) {
      LOGGER.log(Level.INFO, xml);
    }
    return xml;
  }

  @Test
  public void testUserManager() throws Exception {
    String xml = getUserManagerXml();
    // there should be not clear text password in the xml representation
    assertFalse(xml.contains("badpassword"));
  }

  @Test
  @Ignore
  public void testUnMarshalViaMetaXML() throws Exception {
    String xml = getUserManagerXml();
    File metaxml = new File("src/test/resources/test/UserManager.xml");
    JaxbFactory<UserManager> jaxbFactory = new JaxbFactory<UserManager>(
        UserManagerImpl.class);
    jaxbFactory.setBinding("com.bitplan.rest.users", metaxml);
    UserManager um = jaxbFactory.fromXML(xml);
    checkUsers(um);
  }

  @SuppressWarnings("rawtypes")
  @Test
  public void testUnmarshalViaObjectFactory() throws Exception {
    String xml = getUserManagerXml();
    if (debug) {
      LOGGER.log(Level.INFO, xml);
    }
    Map<String, Object> properties = new HashMap<String, Object>();
    Class[] classes = { ObjectFactory.class };
    javax.xml.bind.JAXBContext jaxbContext = JAXBContextFactory.createContext(
        classes, properties);
    UserManagerImpl um3 = (UserManagerImpl) unmarshalFromContext(jaxbContext,
        xml);
    um3.reinitUserById();
    checkUsers(um3);
  }

  @Test
  public void testUnmarshalViaFromXml() throws Exception {
    String xml = getUserManagerXml();
    UserManager um2 = UserManagerImpl.fromXml(xml);
    checkUsers(um2);
  }

  /**
   * check the correctness of the userManager content unmarshalled in comparsion
   * to the original
   * 
   * @param um
   * @throws Exception
   */
  public void checkUsers(UserManager um) throws Exception {
    assertEquals(2, um.getUsers().size());
    if (debug) {
      for (User user : um.getUsers()) {
        LOGGER.log(Level.INFO, user.getId());
      }
    }
    UserManager um1 = getUserManager();
    for (User user : um1.getUsers()) {
      User otherUser = um.getById(user.getId());
      assertEquals(user.getPassword(), otherUser.getPassword());
      String xml1 = user.asXML();
      String xml2 = otherUser.asXML();
      assertEquals(xml1, xml2);
    }
  }

  /**
   * get the example company
   * 
   * @return the company
   */
  public Company getCompany() {
    Company company = new Company();
    company.setCompanyId("doeinc");
    company.setCompanyName("Doe & Partner Inc.");
    for (int i = 1; i <= 3; i++) {
      Employee employee = new Employee();
      employee.setEmpId(i);
      employee.setEmpName("worker " + i);
      employee.setSalary(i * 10000.0);
      employee.setType(EmployeeType.values()[i - 1]);
      company.getEmployees().add(employee);
    }
    return company;
  }

  /**
   * check that the two companies have the same content and structure
   * 
   * @param company
   * @param company2
   */
  public void checkCompany(Company company, Company company2) {
    assertNotNull(company2);
    assertEquals(company.getCompanyId(), company2.getCompanyId());
    assertEquals(company.getCompanyName(), company2.getCompanyName());
    for (int i = 0; i < company.getEmployees().size(); i++) {
      Employee employee = company.getEmployees().get(i);
      Employee otherEmployee = company2.getEmployees().get(i);
      assertEquals(employee.getEmpId(), otherEmployee.getEmpId());
      assertEquals(employee.getEmpName(), otherEmployee.getEmpName());
      assertEquals(employee.getSalary(), otherEmployee.getSalary(), 1e-15);
      assertEquals(employee.getType(), otherEmployee.getType());
    }
  }

  public static final File EMPLOYEE_BINDING = new File(
      "src/test/resources/test/Employee.xml");

  @Test
  public void testXmlBindings() throws Exception {
    // example from
    // http://www.eclipse.org/eclipselink/documentation/2.6/moxy/runtime003.htm
    JaxbFactory<Company> jaxbFactory = new JaxbFactory<Company>(Company.class);
    jaxbFactory.setBinding("example", EMPLOYEE_BINDING);
    Company company = getCompany();
    String xml = jaxbFactory.asXML(company);
    if (debug) {
      LOGGER.log(Level.INFO, xml);
    }
    Company company2 = jaxbFactory.fromXML(xml);
    checkCompany(company, company2);
  }

  @Test
  @Ignore
  public void testXmlBindingsWithModifiedNodeNames() throws Exception {
    JaxbFactory<Company> jaxbFactory = new JaxbFactory<Company>(Company.class);
    jaxbFactory.setBinding("example", EMPLOYEE_BINDING);
    Company company = getCompany();
    String xml = jaxbFactory.asXML(company);
    xml = xml.replace("company", "corporacion");
    xml = xml.replace("employee", "empleado");
    if (debug) {
      LOGGER.log(Level.INFO, xml);
    }
    Company company3 = jaxbFactory.fromXML(xml);
    checkCompany(company, company3);
  }

  /**
   * get an example Customer with Map
   * 
   * @return
   */
  public CustomerWithMap getCustomerWithMap(int count) {
    CustomerWithMap customer = new CustomerWithMap();
    customer.firstname = "John";
    customer.name = "Doe";
    addOrders(customer.orders, count);
    customer.reinit();
    return customer;
  }

  @Test
  public void testMap() throws Exception {
    CustomerWithMap customer = getCustomerWithMap(2);
    JaxbFactory<CustomerWithMap> jaxbFactory = new JaxbFactory<CustomerWithMap>(
        CustomerWithMap.class);
    jaxbFactory.setMarshalListener(new MarshalListener());
    String xml = jaxbFactory.asXML(customer);
    if (debug) {
      LOGGER.log(Level.INFO, xml);
    }
    jaxbFactory.setUnmarshalListener(new UnmarshalListener());
    CustomerWithMap customer2 = jaxbFactory.fromXML(xml);
    assertNotNull(customer2);
    assertEquals(2, customer2.orders.size());
    customer2.reinit();
    assertNotNull(customer2.ordermap);
    Order o1 = customer2.ordermap.get("Id1");
    assertNotNull(o1);
    assertEquals("Item 1", o1.item);
  }

  /**
   * get the XMLId key field for the given class
   * 
   * @param clazz
   * @return - the keyField
   */
  @SuppressWarnings("rawtypes")
  public Field getKeyField(Class clazz) {
    for (Field field : clazz.getDeclaredFields()) {
      for (Annotation a : field.getAnnotations()) {
        if (a.annotationType() == XmlID.class) {
          return field;
        }
      }
    }
    return null;
  }

  /**
   * a MapMarshalListener
   * 
   * @author wf
   *
   */
  public class MarshalListener extends Marshaller.Listener {
    @SuppressWarnings("rawtypes")
    Map<Class, Field> keyFields = new HashMap<Class, Field>();

    @Override
    public void afterMarshal(Object source) {
      if (debug) {
        LOGGER.log(Level.INFO, "after marshal: "
            + source.getClass().getSimpleName());
      }
    }

    @Override
    public void beforeMarshal(Object source) {
      if (debug) {
        LOGGER.log(Level.INFO, "before marshal: "
            + source.getClass().getSimpleName());
      }
      Field keyField = getKeyField(source.getClass());
      if (keyField != null) {
        keyFields.put(source.getClass(), keyField);
        if (debug) {
          LOGGER.log(Level.INFO, source.getClass().getSimpleName()
              + " has key " + keyField.getName());
        }
      }
    }
  }

  /**
   * listener for Unmarshal events
   * 
   * @author wf
   *
   */
  public class UnmarshalListener extends Unmarshaller.Listener {
    public void log(String title, Object target, Object parent) {
      if (debug) {
        String targetName="null";
        String parentName="null";
        if (target!=null) targetName=target.getClass().getSimpleName();
        if (parent!=null) parentName=parent.getClass().getSimpleName();
        LOGGER.log(Level.INFO, title+": "
            + targetName + "->"
            + parentName);
      }
    }

    @Override
    public void afterUnmarshal(Object target, Object parent) {
      log("after unmarshal", target, parent);
    }

    @Override
    public void beforeUnmarshal(Object target, Object parent) {
      log("before unmarshal: ", target, parent);
    }
  }
}
