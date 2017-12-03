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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManagerImpl;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.spi.container.ContainerRequest;

/**
 * manager for click streams
 * 
 * @author wf
 *
 */
public class ClickStreamManager extends JsonManagerImpl<ClickStream>
    implements JsonAble {
  boolean debug=false;
  // limits - the default is 10000 click streams per day
  // the maximum number of click stream per logging time period
  int MAX_CLICKSTREAMS = 10000;
  // the length of a logging time period in seconds
  int LOGGING_TIME_PERIOD = 86400;
  // maximum amount of time for a click stream session
  int MAX_SESSION_TIME=1800; // 30 minutes

  // what amount to wait until to flush the log again
  final int FLUSH_PERIOD = 1;

  Date lastFlush = new Date();
  Date lastLogRotate = new Date();
  transient HashMap<String, ClickStream> clickStreamsByIp = new MaxSizeHashMap<String, ClickStream>(
      MAX_CLICKSTREAMS);
  private List<ClickStream> clickStreams = new ArrayList<ClickStream>();

  public List<ClickStream> getClickStreams() {
    return clickStreams;
  }

  public void setClickStreams(List<ClickStream> clickStreams) {
    this.clickStreams = clickStreams;
  }

  /**
   * enforce singleton pattern with private constructor
   * 
   * @param clazz
   */
  private ClickStreamManager(Class<ClickStream> clazz) {
    super(clazz);
  }
  
  /**
   * get the amount of seconds between the given two dates
   * @param from
   * @param to
   * @return - the amount of seconds
   */
  public long durationSecs(Date from, Date to) {
    long seconds = (to.getTime() - from.getTime()) / 1000;
    return seconds;
  }

  /**
   * add a pageHit
   * 
   * @param request
   * @param req
   * @throws Exception
   */
  public ClickStream addPageHit(ContainerRequest request,
      HttpRequestContext req) {
    MultivaluedMap<String, String> headers = req.getRequestHeaders();
    PageHit pageHit = new PageHit(request, headers);
    String ip = headers.getFirst("remote_addr");
    if (debug)
      showDebug(request,req,headers);
    ClickStream clickStream = clickStreamsByIp.get(ip);
    if (clickStream != null) {
      clickStream.addPageHit(pageHit);
    } else {
      clickStream = new ClickStream(request, headers, pageHit);
      clickStreamsByIp.put(ip, clickStream);
      getClickStreams().add(clickStream);
    }
    // do we need to flush the log?
    Date now = new Date();
    if (durationSecs(lastFlush,now) >= FLUSH_PERIOD) {
      flush();
    }
    // the Logging period is over
    if (durationSecs(lastLogRotate,now) >= LOGGING_TIME_PERIOD) {
      logRotate();
    }
    return clickStream;
  }

  static ClickStreamManager instance;

  /**
   * flush the current log
   */
  public void flush() {
    try {
      save();
    } catch (IOException e) {
      // ignore 
      // FIXME - shouldn't we log this incident?
    }
    lastFlush = new Date();
  }
  
  /**
   * let's rotate the log file
   */
  public void logRotate() {
    // start a new json file
    lastLogRotate=new Date();
    // remove old entries
    for (ClickStream clickStream:this.getClickStreams()) {
      if (this.durationSecs(clickStream.timeStamp,lastLogRotate)>=MAX_SESSION_TIME) {
        this.getClickStreams().remove(clickStream);
        this.clickStreamsByIp.remove(clickStream.ip);
      }
    }
    // initial save
    flush();
  }
  
  @Override
  public File getJsonFile() {
    String home = System.getProperty("user.home");
    File configDirectory=new File(home+"/.clickstream/");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmm");
    String jsonFileName ="clickstream"+df.format(lastLogRotate)+".json";
    File jsonFile = new File(configDirectory, jsonFileName);
    return jsonFile;
  }
  
  /**
   * get a singleton
   * 
   * @return
   */
  public static ClickStreamManager getInstance() {
    if (instance == null)
      instance = new ClickStreamManager(ClickStream.class);
    return instance;
  }

  @Override
  public void reinit() {
    
  }

  @Override
  public void fromMap(Map<String, Object> map) {

  }

  public void showDebug(ContainerRequest request,
      HttpRequestContext req, MultivaluedMap<String, String> headers) {

    if (debug) {
      System.out.println(req.getPath(false));
      System.out.println(req.getAbsolutePath());
      for (String key : headers.keySet()) {
        System.out.println(key + "=" + headers.getFirst(key));
      }
    }
    // https://wiki.selfhtml.org/wiki/HTTP/Header/User-Agent
  }

}
