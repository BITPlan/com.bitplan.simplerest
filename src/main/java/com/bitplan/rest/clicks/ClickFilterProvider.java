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
package com.bitplan.rest.clicks;

import java.util.logging.Logger;

import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class ClickFilterProvider implements ContainerRequestFilter {
  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest.clicks");

  @Context
  private HttpContext ctx;

  @Override
  public ContainerRequest filter(ContainerRequest request) {
    HttpRequestContext req = ctx.getRequest();
    ClickStreamManager.getInstance().addPageHit(request,req);
    request.getRequestHeaders().add("remote_addr",req.getHeaderValue("remote_addr"));
    return request;
  }

}
