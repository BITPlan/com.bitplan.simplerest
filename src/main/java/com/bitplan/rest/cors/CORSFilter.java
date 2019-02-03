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
package com.bitplan.rest.cors;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
* Filter that returns a response with headers that allows for Cross-Origin
* Requests (CORs) to be performed against the platform API.
*/
public class CORSFilter implements ContainerResponseFilter {
  @Override
  public ContainerResponse filter(final ContainerRequest request, final ContainerResponse   response) { 
      final ResponseBuilder resp = Response.fromResponse(response.getResponse());
      resp.header("Access-Control-Allow-Origin", "*")
      .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
      final String reqHead = request.getHeaderValue("Access-Control-Request-Headers");
      if (null != reqHead && !reqHead.equals(null)) {
      resp.header("Access-Control-Allow-Headers", reqHead);}
      response.setResponse(resp.build());
      return response;
 }
}