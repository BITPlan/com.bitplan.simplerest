package com.bitplan.rest.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import com.bitplan.persistence.Manager;

/**
 * Base Resource for NonOlet
 * 
 * @author wf
 *
 */
public class BaseResource<MT extends Manager<MT,T>,T> extends TemplateResource {
  @Context
  UriInfo uriInfo;

  @Context
  Request request;
  String template;
  
  String elementName;
  MT manager;

  public MT getManager() {
    return manager;
  }

  public void setManager(MT manager) {
    this.manager = manager;
  }

  public String getElementName() {
    return elementName;
  }

  public void setElementName(String elementName) {
    this.elementName = elementName;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  /**
   * constructor for a BaseResource
   */
  public BaseResource() {
  }
  
}
