/**
 * Copyright (C) 2015-2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.hello.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
/**
 * Simple Hello Resource
 * @author wf
 *
 */
public class HelloResource {

  @GET
  public String getHello() {
    return "Hello";
  }
}
