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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.bitplan.json.JsonAble;
import com.sun.jersey.spi.container.ContainerRequest;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * 
 * @author wf
 *
 */
public class ClickStream implements JsonAble {

  String referrer;
  private String url;
  private String ip;
  private String domain;
  String userAgentHeader;
  String acceptLanguage;
  Date timeStamp;
  private List<PageHit> pageHits = new ArrayList<PageHit>();
  private UserAgent userAgent;

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public List<PageHit> getPageHits() {
    return pageHits;
  }

  public UserAgent getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(UserAgent userAgent) {
    this.userAgent = userAgent;
  }

  public String getReferrer() {
    return referrer;
  }

  public void setReferrer(String referrer) {
    this.referrer = referrer;
  }

  public String getAcceptLanguage() {
    return acceptLanguage;
  }

  public void setAcceptLanguage(String acceptLanguage) {
    this.acceptLanguage = acceptLanguage;
  }

  public Date getTimeStamp() {
    return timeStamp;
  }

  public void setTimeStamp(Date timeStamp) {
    this.timeStamp = timeStamp;
  }

  public void setPageHits(List<PageHit> pageHits) {
    this.pageHits = pageHits;
  }

  /**
   * add the given page hit
   * 
   * @param pageHit
   */
  public void addPageHit(PageHit pageHit) {
    getPageHits().add(pageHit);
  }

  /**
   * create a new ClickStream
   * 
   * @param userAgentAnalyzer
   * @param request
   * @param headers
   * @param initialHit
   */
  public ClickStream(UserAgentAnalyzer userAgentAnalyzer,
      ContainerRequest request, MultivaluedMap<String, String> headers,
      PageHit initialHit, String ip) {
    // is this part of an existing ClickStream?
    referrer = request.getHeaderValue("referer"); // Yes, with the legendary
                                                  // misspelling.
    this.setUrl(request.getAbsolutePath().toString());
    this.setIp(ip);
    checkDomain();
    this.userAgentHeader = headers.getFirst("user-agent");
    checkUserAgent(userAgentAnalyzer);
    this.acceptLanguage = headers.getFirst("accept-language");
    this.timeStamp = new Date();
    addPageHit(initialHit);
  }

  public void checkUserAgent(UserAgentAnalyzer userAgentAnalyzer) {
    setUserAgent(userAgentAnalyzer.parse(userAgentHeader));
  }

  public void checkDomain() {
    if (domain == null) {
      setDomain(ip);
      try {
        InetAddress ia = InetAddress.getByName(ip);
        setDomain(ia.getCanonicalHostName());
      } catch (UnknownHostException e) {
        // ignore
      }
    }
  }

  @Override
  public void reinit() {

  }

  @Override
  public void fromMap(Map<String, Object> map) {

  }

}
