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

import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.api.core.HttpRequestContext;

/**
 * 
 * @author wf
 *
 */
public class PageHit {
  String path;
  Date timeStamp;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Date getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Date timeStamp) {
    this.timeStamp = timeStamp;
  }

  /**
   * create a page hit for the given request and headers
   * 
   * @param req
   * @param headers 
   */
  public PageHit(HttpRequestContext req, MultivaluedMap<String, String> headers) {
    this.timeStamp=new Date();
    path=req.getPath();
  }

}
