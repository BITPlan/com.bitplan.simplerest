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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;

import com.bitplan.json.JsonAble;
import com.bitplan.json.JsonManagerImpl;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.spi.container.ContainerRequest;

import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * manager for click streams
 * 
 * @author wf
 *
 */
public class ClickStreamManager extends JsonManagerImpl<ClickStream>
    implements JsonAble {
  protected transient Logger LOGGER = Logger.getLogger("com.bitplan.rest.clicks");
  
  private boolean debug = false;
  // limits - the default is 10000 click streams per day
  // the maximum number of click stream per logging time period
  int MAX_CLICKSTREAMS = 10000;
  // the length of a logging time period in seconds
  int LOGGING_TIME_PERIOD = 86400;
  // maximum amount of time for a click stream session
  int MAX_SESSION_TIME = 1800; // 30 minutes

  // what amount to wait until to flush the log again
  final int FLUSH_PERIOD = 60;

  Date startTime = new Date();
  Date lastFlush = new Date();
  Date lastLogRotate = new Date();
  private String fileName;

  transient HashMap<String, ClickStream> clickStreamsByIp = new MaxSizeHashMap<String, ClickStream>(
      MAX_CLICKSTREAMS);
  public static final UserAgentAnalyzer userAgentAnalyzer=UserAgentAnalyzer.newBuilder().hideMatcherLoadStats()
      .withCache(25000).build();

  private List<ClickStream> clickStreams = new ArrayList<ClickStream>();

  public boolean isDebug() {
    return debug;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

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
   * 
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
    // is this a localhost ip?
    if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
      // try getting the X-Forward-For address
      // https://stackoverflow.com/questions/760283/apache-proxypass-how-to-preserve-original-ip-address
      ip = headers.getFirst("X-Forwarded-For");
    }
    if (isDebug())
      showDebug(request, req, headers);
    ClickStream clickStream = clickStreamsByIp.get(ip);
    if (clickStream != null) {
      clickStream.addPageHit(pageHit);
    } else {
      clickStream = new ClickStream(userAgentAnalyzer, request, headers,
          pageHit, ip);
      clickStreamsByIp.put(ip, clickStream);
      getClickStreams().add(clickStream);
    }
    // do we need to flush the log?
    Date now = new Date();
    if (durationSecs(lastFlush, now) >= FLUSH_PERIOD) {
      flush();
    }
    // the Logging period is over
    if (durationSecs(lastLogRotate, now) >= LOGGING_TIME_PERIOD) {
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
      this.setFileName(this.getJsonFile().getName());
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
    lastLogRotate = new Date();
    // remove old entries
    // avoid concurrent modification exeception
    // see https://stackoverflow.com/a/18448699/1497139
    int removeCount=0;
    int before=this.getClickStreams().size();
    List<ClickStream> toRemove=new ArrayList<ClickStream>();
    for (ClickStream clickStream:this.getClickStreams()) {
      if (this.durationSecs(clickStream.timeStamp,
          lastLogRotate) >= MAX_SESSION_TIME) {     
        this.clickStreamsByIp.remove(clickStream.getIp());
        toRemove.add(clickStream);
        removeCount++;
      }
    }
    this.getClickStreams().removeAll(toRemove);
    
    int after=this.getClickStreams().size();
    LOGGER.log(Level.INFO,String.format("removed %5d - %5d = %5d clickStreams", before,removeCount,after));
    // initial save
    flush();
  }

  @Override
  public File getJsonFile() {
    String home = System.getProperty("user.home");
    File configDirectory = new File(home + "/.clickstream/");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmm");
    String jsonFileName = "clickstream" + df.format(lastLogRotate) + ".json";
    File jsonFile = new File(configDirectory, jsonFileName);
    return jsonFile;
  }

  /**
   * get a singleton
   * 
   * @return the singleton instance
   */
  public static ClickStreamManager getInstance() {
    if (instance == null) {
      synchronized (ClickStreamManager.class) {
        instance = new ClickStreamManager(ClickStream.class);
      }
    }
    return instance;
  }

  @Override
  public void reinit() {
    if (clickStreamsByIp == null)
      clickStreamsByIp = new MaxSizeHashMap<String, ClickStream>(
          MAX_CLICKSTREAMS);
    for (ClickStream clickStream : this.clickStreams) {
      this.clickStreamsByIp.put(clickStream.getIp(), clickStream);
    }
  }

  @Override
  public void fromMap(Map<String, Object> map) {

  }

  /**
   * show the debug information
   * 
   * @param request
   * @param req
   * @param headers
   */
  public void showDebug(ContainerRequest request, HttpRequestContext req,
      MultivaluedMap<String, String> headers) {

    if (isDebug()) {
      System.out.println(req.getPath(false));
      System.out.println(req.getAbsolutePath());
      for (String key : headers.keySet()) {
        System.out.println(key + "=" + headers.getFirst(key));
      }
    }
    // https://wiki.selfhtml.org/wiki/HTTP/Header/User-Agent
  }

  /**
   * get the clickstream for the given ip
   * 
   * @param ip
   * @return - the clickstream
   */
  public ClickStream getClickStream(String ip) {
    ClickStream result = this.clickStreamsByIp.get(ip);
    return result;
  }

}
