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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

@Path("/hello")
/**
 * Simple Hello Resource
 * @author wf
 *
 */
public class HelloResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;

  @GET
  public String getHello() {
    return "Hello";
  }
  
  @GET
  @Produces("text/html")
  @Path("echo/{value}")
  public String getEcho(@PathParam("value") String value) {
    System.out.println(request.getMethod()+":"+uriInfo.getPath());
    return value;
  }
}
