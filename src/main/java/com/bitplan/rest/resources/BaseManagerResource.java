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
  String managerTemplate;
  /**
   * set the templates
   * @param managerTemplate
   * @param template
   */
  public void setTemplates(String managerTemplate, String template) {
	  this.template=template;
	  this.managerTemplate=managerTemplate;
  }
  
  /**
   * get the Manager
   * 
   * @return the managermvn 
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
