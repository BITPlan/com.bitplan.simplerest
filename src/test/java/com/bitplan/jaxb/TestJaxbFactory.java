/**
 * Copyright (C) 2015-2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.jaxb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.io.FileUtils;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.rest.users.ObjectFactory;
import com.bitplan.rest.users.User;
import com.bitplan.rest.users.UserImpl;
import com.bitplan.rest.users.UserManager;
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
  boolean debug = false;
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
    String orderId;
    String item;
    int count;
  }

  @Test
  public void testVersion() throws JAXBException {
    JaxbFactory<Customer> customerFactory = new JaxbFactory<Customer>(
        Customer.class);
    // String jaxbContext = JAXBContext.newInstance(Customer.class)
    String jaxbContextType = customerFactory.getJAXBContext().getClass()
        .getName();
    if (debug)
      System.out.println(jaxbContextType);
    // Oracle/Sun default:
    String expected = "com.sun.xml.bind.v2.runtime.JAXBContextImpl";
    if (moxy)
      expected = "org.eclipse.persistence.jaxb.JAXBContext"; // jersey-moxy
    assertEquals("Expecting configured JaxB implementation", expected,
        jaxbContextType);
  }

  @Test
  public void testJaxbFactory() throws Exception {
    debug = false;
    JaxbFactory<Customer> customerFactory = new JaxbFactory<Customer>(
        Customer.class, Order.class);
    Customer customer = new Customer();
    customer.name = "Doe";
    customer.firstname = "John";
    for (int i = 1; i <= 3; i++) {
      Order order = new Order();
      order.item = "Item " + i;
      order.count = i;
      order.orderId = "Id" + i;
      customer.orders.add(order);
    }
    String xml = customerFactory.asXML(customer);
    if (debug)
      System.out.println("'" + xml + "'");
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
    if (debug)
      System.out.println(json);
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
    um.add(new UserImpl("jd001", "John", "Doe", "john@doe.org", "badpassword",
        "John is our admin"));
    um.add(new UserImpl("bs001", "Bill", "Smith", "bill@smith.com",
        "simplesecret", "Bill is our secretary"));
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
    if (debug)
      System.out.println(xml);
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
        System.out.println(user.getId());
      }
    }
    UserManager um1 = getUserManager();
    for (User user : um1.getUsers()) {
      User otherUser = um.getById(user.getId());
      otherUser.deCrypt(um.getCrypt());
      user.deCrypt(um1.getCrypt());
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
    if (debug)
      System.out.println(xml);
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
    if (debug)
      System.out.println(xml);
    Company company3 = jaxbFactory.fromXML(xml);
    checkCompany(company, company3);
  }

}
