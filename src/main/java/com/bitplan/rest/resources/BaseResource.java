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
