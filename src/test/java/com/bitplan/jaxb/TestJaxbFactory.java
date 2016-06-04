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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

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

}
