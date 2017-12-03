/**
 * Copyright (c) 2016-2017 BITPlan GmbH
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.bitplan.json.JsonAble;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * 
 * @author wf
 *
 */
public class ClickStream implements JsonAble {
  
  String referrer;
  String url;
  String ip;
  String userAgent;
  String acceptLanguage;
  Date timeStamp;
  List<PageHit> pageHits=new ArrayList<PageHit>();
  
  /**
   * add the given page hit
   * @param pageHit
   */
  public void addPageHit(PageHit pageHit) {
    pageHits.add(pageHit);
  }
  
  /**
   * create a new ClickStream
   * @param request
   * @param headers 
   * @param initialHit
   */
  public ClickStream(ContainerRequest request,MultivaluedMap<String, String> headers, PageHit initialHit, String ip) {
    // is this part of an existing ClickStream?
    referrer = request.getHeaderValue("referer"); // Yes, with the legendary misspelling.
    this.url=request.getAbsolutePath().toString();
    this.ip=ip;
    this.userAgent=headers.getFirst("user-agent");
    this.acceptLanguage=headers.getFirst("accept-language");
    this.timeStamp=new Date();
    addPageHit(initialHit);
  }

  @Override
  public void reinit() {
    
  }

  @Override
  public void fromMap(Map<String, Object> map) {
    
  }

}
