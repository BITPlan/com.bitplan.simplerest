/**
 * Copyright (c) 2016-2019 BITPlan GmbH
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
package com.bitplan.rest.providers;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
/*import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;*/
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * configurable header propagation for Jersey 1.x
 * non functional as of 2019-02-10 - can only be used for debugging at this time
 * 
 * @author wf
 *
 */
@Provider
public class HeaderPropagationFilter
extends ClientFilter
    implements ContainerRequestFilter, ContainerResponseFilter  {

  public static boolean debug = true;

  @Context
  private HttpContext ctx;

  /**
   * if debug is on show the given header value map
   * 
   * @param title
   *          the title to use
   * @param headers
   *          - the map of header values to show
   */
  public void showDebug(String title, MultivaluedMap<String, ?> headers) {
    if (debug) {
      System.out.println("HeaderPropagation " + title + " ...");
      for (String key : headers.keySet()) {
        System.out.println(String.format("  %s=%s", key, headers.get(key)));
      }
    }
  }

  @Override
  public ContainerRequest filter(ContainerRequest request) {
    MultivaluedMap<String, String> headersFromRequest = request
        .getRequestHeaders();
    showDebug("request", headersFromRequest);
    return request;
  }

  @Override
  public ContainerResponse filter(ContainerRequest request,
      ContainerResponse response) {
    MultivaluedMap<String, String> headersFromRequest = request
        .getRequestHeaders();
    showDebug("response", headersFromRequest);
    return response;
  }

  @Override
  public ClientResponse handle(ClientRequest request)
      throws ClientHandlerException {
    showDebug("client",request.getHeaders());
    ClientResponse response = getNext().handle(request);
    return response;
  }

}
