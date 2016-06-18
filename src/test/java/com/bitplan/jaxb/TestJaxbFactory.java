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

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.junit.Ignore;
import org.junit.Test;

import com.bitplan.rest.users.ObjectFactory;
import com.bitplan.rest.users.User;
import com.bitplan.rest.users.UserImpl;
import com.bitplan.rest.users.UserManager;
import com.bitplan.rest.users.UserManagerImpl;

/**
 * test the JaxBFactory
 * @author wf
 *
 */
public class TestJaxbFactory {
  boolean debug=false;
  boolean moxy=true;

  @XmlRootElement
  @XmlAccessorType(XmlAccessType.FIELD)
  public static class Customer {
    String name;
    String firstname;
    @XmlElementWrapper(name="orders")
    @XmlElement(name="order")
    List<Order> orders=new ArrayList<Order>();
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
    JaxbFactory<Customer> customerFactory = new JaxbFactory<Customer>(Customer.class);
    // String jaxbContext = JAXBContext.newInstance(Customer.class)
    String jaxbContextType=customerFactory.getJAXBContext().getClass().getName();
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
    debug=false;
    JaxbFactory<Customer> customerFactory = new JaxbFactory<Customer>(Customer.class, Order.class);
    Customer customer=new Customer();
    customer.name="Doe";
    customer.firstname="John";
    for (int i=1;i<=3;i++) {
      Order order=new Order();
      order.item="Item "+i;
      order.count=i;
      order.orderId="Id"+i;
      customer.orders.add(order);
    }
    String xml=customerFactory.asXML(customer);
    if (debug)
      System.out.println("'"+xml+"'");
    assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
        "<customer>\n" + 
        "   <name>Doe</name>\n" + 
        "   <firstname>John</firstname>\n" + 
        "   <orders>\n" + 
        "      <order>\n" + 
        "         <orderId>Id1</orderId>\n" + 
        "         <item>Item 1</item>\n" + 
        "         <count>1</count>\n" + 
        "      </order>\n" + 
        "      <order>\n" + 
        "         <orderId>Id2</orderId>\n" + 
        "         <item>Item 2</item>\n" + 
        "         <count>2</count>\n" + 
        "      </order>\n" + 
        "      <order>\n" + 
        "         <orderId>Id3</orderId>\n" + 
        "         <item>Item 3</item>\n" + 
        "         <count>3</count>\n" + 
        "      </order>\n" + 
        "   </orders>\n" + 
        "</customer>\n" + 
        "",xml);
    Customer customer2=customerFactory.fromXML(xml);
    assertNotNull(customer2);
    assertEquals("Doe",customer2.name);
    assertEquals("John",customer2.firstname);
    String json=customerFactory.asJson(customer);
    if (debug)
      System.out.println(json);
    assertEquals("{\n" + 
        "   \"customer\" : {\n" + 
        "      \"name\" : \"Doe\",\n" + 
        "      \"firstname\" : \"John\",\n" + 
        "      \"orders\" : {\n" + 
        "         \"order\" : [ {\n" + 
        "            \"orderId\" : \"Id1\",\n" + 
        "            \"item\" : \"Item 1\",\n" + 
        "            \"count\" : 1\n" + 
        "         }, {\n" + 
        "            \"orderId\" : \"Id2\",\n" + 
        "            \"item\" : \"Item 2\",\n" + 
        "            \"count\" : 2\n" + 
        "         }, {\n" + 
        "            \"orderId\" : \"Id3\",\n" + 
        "            \"item\" : \"Item 3\",\n" + 
        "            \"count\" : 3\n" + 
        "         } ]\n" + 
        "      }\n" + 
        "   }\n" + 
        "}",json);
    Customer customer3=customerFactory.fromJson(json);
    assertNotNull(customer3);
    assertEquals("Doe",customer3.name);
    assertEquals("John",customer3.firstname);
  };
  
  /**
   * unmarshal the UserManager from the given JAXBContext
   * @param jaxbContext
   * @param xml
   * @return
   * @throws Exception
   */
  public UserManager unmarshalFromContext(javax.xml.bind.JAXBContext jaxbContext, String xml) throws Exception {
    StringReader stringReader = new StringReader(xml);
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    UserManager um=(UserManager) unmarshaller.unmarshal(stringReader);
    return um;
  }

  @Ignore
  public void testUserManager() throws Exception {
    UserManager um=new UserManagerImpl();
    um.add(new UserImpl("jd001","John","Doe","john@doe.org","badpassword","John is our admin"));
    um.add(new UserImpl("bs001","Bill","Smith","bill@smith.com","simplesecret","Bill is our secretary"));
    String xml=um.asXML();
    System.out.println(xml);
    // there should be not clear text password in the xml representation
    assertFalse(xml.contains("badpassword"));
    String metaxml="<?xml version=\"1.0\"?>\n" + 
        "<xml-bindings\n" + 
        "    xmlns=\"http://www.eclipse.org/eclipselink/xsds/persistence/oxm\"\n" + 
        "    package-name=\"com.bitplan.rest.users\">\n" + 
        "    <xml-schema\n" + 
        "        namespace=\"http://www.example.com/user\"\n" + 
        "        element-form-default=\"QUALIFIED\"/>\n" + 
        "    <java-types>\n" + 
        "            <java-type name=\"UserManagerImpl\">\n" + 
        "            <xml-root-element name=\"UserManager\"/>\n" + 
        "            <java-attributes>\n" + 
        "                <xml-element java-attribute=\"users\">\n" + 
        "                   <xml-element-wrapper name=\"users\"/>\n"+
        "                </xml-element>\n"+
        "            </java-attributes>\n" + 
        "        </java-type>\n" + 
        "        <java-type name=\"UserImpl\">\n" + 
        "            <xml-root-element name=\"User\"/>\n" + 
        "            <xml-type prop-order=\"id name firstname email password comment \"/>\n" + 
        "            <java-attributes>\n" + 
        "                <xml-element java-attribute=\"name\" name=\"name\"/>\n" + 
        "                <xml-element java-attribute=\"firstName\" name=\"firstName\"/>\n" + 
        "                <xml-element java-attribute=\"email\" name=\"email\"/>\n" + 
        "                <xml-element java-attribute=\"password\" name=\"password\"/>\n" + 
        "                <xml-element java-attribute=\"comment\" name=\"comment\"/>\n" + 
        "            </java-attributes>\n" + 
        "        </java-type>\n" + 
        "    </java-types>\n" + 
        "</xml-bindings>";
    javax.xml.bind.JAXBContext jaxbContext4 = JaxbFactory.createJAXBContext("com.bitplan.rest.users", metaxml);
    UserManager um4 = unmarshalFromContext(jaxbContext4,xml);
    assertNotNull(um4);
    
    Map<String, Object> properties = new HashMap<String, Object>();
    Class[] classes={ObjectFactory.class};
    javax.xml.bind.JAXBContext jaxbContext = JAXBContextFactory.createContext(classes,properties);
    UserManager um3 = unmarshalFromContext(jaxbContext,xml);
    assertNotNull(um3);
    UserManager um2 = UserManagerImpl.fromXml(xml);
    assertEquals(2,um2.getUsers().size());
    for (User user:um2.getUsers()) {
      System.out.println(user.getId());
    }
    User jd = um2.getById("jd001");
    jd.deCrypt(um2.getCrypt());
    assertEquals("badpassword",jd.getPassword());
  }
}
