package com.bitplan.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bitplan.persistence.Manager;

/**
 * a generalized Resource for Managers
 * 
 * @author wf
 *
 * @param <MT> - the manager type
 * @param <T> - the type
 */
public abstract class BaseManagerResource<MT extends Manager<MT,T>, T> extends BaseResource<MT,T> {
  MT manager;
  
  @SuppressWarnings("rawtypes")
  static BaseManagerResource instance;

  /**
   * get the Manager
   * 
   * @return
   */
  public MT getManager() {
    return manager;
  }

  public void setManager(MT manager) {
    this.manager = manager;
  }

  @SuppressWarnings("rawtypes")
  public static BaseManagerResource getInstance() throws Exception {
    return instance;
  }

  @SuppressWarnings("rawtypes")
  static void setInstance(BaseManagerResource pinstance) {
    instance = pinstance;
  }

  public BaseManagerResource() {
    instance = this;
  }
  
  @GET
  @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
  public MT getManagerAsResponse() throws Exception {
    return getManager();
  }
  
  @GET
  @Produces({ MediaType.TEXT_HTML})
  @Path("at/{index}")
  public Response getElementByIndex(@Context UriInfo uri,
      @PathParam("index") Integer index) throws Exception {
    T element=this.getManager().getElements().get(index-1);
    rootMap.put("index",index);
    rootMap.put(elementName,element);
    Response response = super.templateResponse(template);
    return response;
  }
  
  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  @Path("at/{index}")
  public T getElementResponse(@Context UriInfo uri,
      @PathParam("index") Integer index) throws Exception {
    T element=this.getManager().getElements().get(index-1);
    return element;
  }
}
