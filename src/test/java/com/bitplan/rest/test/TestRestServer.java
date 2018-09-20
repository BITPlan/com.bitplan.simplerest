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
package com.bitplan.rest.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Assert;

import com.bitplan.rest.RestServer;
import com.bitplan.rest.SSLClientHelper;
import com.bitplan.rest.User;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

/**
 * base class for RestServer tests
 * 
 * @author wf
 * 
 */
public abstract class TestRestServer {

  /**
   * server creation - needs to be implemented by derived tests
   * 
   * @return
   * @throws Exception
   */
  public abstract RestServer createServer() throws Exception;

  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest.test");

  protected boolean debug = false;
  protected static RestServer rs;
  // use a Date as a semaphore
  private static Date semaphore = new Date();
  public int testPort = 8085;

  String[] contentTypes = { "text/xml", "text/html", "text/plain",
      "application/json", "application/xml" };

  private User user;

  private Client client;

  /**
   * start the server scanning for the next available port
   * 
   * @param rs
   * @throws Exception
   */
  public void startServer(RestServer rs) throws Exception {
    startServer(rs, true);
  }

  /**
   * start the given Rest Server
   * 
   * @param rs
   * @return
   * @throws Exception
   */
  public void startServer(RestServer rs, boolean scanForAvailablePort)
      throws Exception {
    // find an available port
    if (scanForAvailablePort) {
      while (!rs.isPortAvailable(testPort))
        testPort++;
    }
    rs.getSettings().setPort(testPort);
    rs.setStarter(semaphore);
    String args[] = {};
    rs.startServer(args);
    synchronized (semaphore) {
      semaphore.wait();
    }
  }

  /**
   * set the user for basic authentication
   * @param user
   */
  public void setUser(User user) {
    this.user=user;
  }
  
  /**
   * get the base URL of the given RESTFul server
   * 
   * @param rs
   * @return
   */
  public String getBaseUrl(RestServer rs) {
    String baseUrl = rs.getUrl().replace("0.0.0.0", "localhost");
    return baseUrl;
  }

  /**
   * start the Server
   * 
   * @return the baseUrl
   * @throws Exception
   */
  public String startServer() throws Exception {
    if (rs == null) {
      rs = createServer();
      startServer(rs);
    }
    return getBaseUrl(rs);
  }
  
  /**
   * stop the server
   */
  public void stopServer() {
    if (rs!=null) {
      rs.stop();
      rs=null;
    }
  }

  /**
   * check the given path whether it contains the expected string
   * 
   * @param path
   * @param expected
   * @throws Exception
   */
  protected void check(String path, String expected) throws Exception {
    String responseString = getResponseString("text/html; charset=UTF-8", path);
    if (debug) {
      LOGGER.log(Level.INFO,"response for "+path+" is "+responseString);
    }
    assertTrue(expected, responseString.contains(expected));
  }

  /**
   * upload the given file
   * 
   * inspired by
   * http://neopatel.blogspot.de/2011/04/jersey-posting-multipart-data.html
   * 
   * @param url
   * @param uploadFile
   * @return the result
   * @throws Exception 
   */
  public String upload(String url, File uploadFile) throws Exception {
    WebResource resource = getResource(url);
    FormDataMultiPart form = new FormDataMultiPart();
    form.field("fileName", uploadFile.getName());
    FormDataBodyPart fdp = new FormDataBodyPart("content", new FileInputStream(
        uploadFile), MediaType.APPLICATION_OCTET_STREAM_TYPE);
    form.bodyPart(fdp);
    String response = resource.type(MediaType.MULTIPART_FORM_DATA).post(
        String.class, form);
    return response;
  }

  /**
   * 
   * http://stackoverflow.com/a/9542781/1497139
   * 
   * @param string
   * @return
   */
  public URI convertToValid(String string) {
    try {
      String decodedURL = URLDecoder.decode(string, "UTF-8");
      URL url = new URL(decodedURL);
      URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
          url.getPort(), url.getPath(), url.getQuery(), url.getRef());
      return uri;
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * get the given resource for the given path (relative to base url of server
   * to be started
   * 
   * @param path
   *          - either a relative or absolute path
   *          for relative paths not starting with "http" the server is started
   *          and the relative
   *          path is added to the servers base url
   * @return
   * @throws Exception
   */
  public WebResource getResource(String path) throws Exception {
    String url = path;
    if (!path.startsWith("http")) {
      url = startServer() + path;
    }
    if (debug)
      System.out.println(url);
    /*
     * FIXME - check issue and activate solution
     * URI uri = convertToValid(url);
     * if (debug)
     * System.out.println(uri.toASCIIString());
     */
    // path=path.replace("Ä","%C3%B6");
    if (rs.getSettings().isSecure()) {
      client=SSLClientHelper.createClient();
    } else {
      client = Client.create();
    }
    if (this.user!=null) {
      client.addFilter(new HTTPBasicAuthFilter(user.getId(),user.getPassword()));
    }
    WebResource wrs = client.resource(url);
    return wrs;
  }

  /**
   * get the Response for the given contentType and path
   * 
   * @param contentType
   * @param path
   * @return - the response
   * @throws Exception
   */
  public ClientResponse getResponse(String contentType, String path,
      boolean debug) throws Exception {
    if (debug)
      System.out.println(" contentType:" + contentType);
    WebResource wrs = getResource(path);
    ClientResponse cr = wrs.accept(contentType).get(ClientResponse.class);
    return cr;
  }

  /**
   * get the Response string
   * 
   * @param response
   * @return
   */
  public String getResponseString(ClientResponse response) {
    if (debug)
      System.out.println("status: " + response.getStatus());
    String responseText = response.getEntity(String.class);
    if (response.getStatus() != 200 && debug) {
      System.err.println(responseText);
    }
    Assert.assertEquals(200, response.getStatus());
    return responseText;
  }

  /**
   * get the Response for the given contentType and path
   * 
   * @param contentType
   * @param path
   * @return - the response
   * @throws Exception
   */
  public String getResponseString(String contentType, String path)
      throws Exception {
    return this.getResponseString(contentType, path, debug);
  }

  /**
   * get the Response for the given contentType and path
   * 
   * @param contentType
   * @param path
   * @return - the response
   * @throws Exception
   */
  public String getResponseString(String contentType, String path, boolean debug)
      throws Exception {
    ClientResponse cr = this.getResponse(contentType, path, debug);
    String response = this.getResponseString(cr);
    return response;
  }

  /**
   * get a POST response
   * 
   * @param contentType
   * @param path
   * @param data
   * @param debug
   * @return
   * @throws Exception
   */
  public ClientResponse getPostResponse(String contentType, String path,
      String data, boolean debug) throws Exception {
    WebResource wrs = getResource(path);
    if (debug)
      LOGGER.log(Level.INFO, " posting to path " + path + " data='" + data
          + "'");
    ClientResponse response = wrs.accept(contentType).type(contentType)
        .post(ClientResponse.class, data);
    return response;
  }
  
  /**
   * get a post RESPONSE
   * @param path
   * @param pFormData
   * @param debug
   * @return
   * @throws Exception
   */
  public ClientResponse getPostResponse(String path,
      Map<String, String> pFormData, boolean debug) throws Exception {
    return this.getPostResponse("text/html",path,pFormData,debug);
  }

  /**
   * get a Post response
   * 
   * @param contentType
   * @param path
   * @param pFormData
   * @param debug
   * @return
   * @throws Exception
   */
  public ClientResponse getPostResponse(String contentType,String path,
      Map<String, String> pFormData, boolean debug) throws Exception {
    MultivaluedMap<String, String> lFormData = new MultivaluedMapImpl();
    for (String key : pFormData.keySet()) {
      lFormData.add(key, pFormData.get(key));
    }
    WebResource wrs = getResource(path);
    ClientResponse response = wrs.type(
        MediaType.APPLICATION_FORM_URLENCODED_TYPE).accept(contentType).post(ClientResponse.class,
        lFormData);
    // do not force response string - further calls to getResponseString will
    // fail
    boolean force = false;
    if (debug && force) {
      String responseString = this.getResponseString(response);
      LOGGER.log(Level.INFO, responseString);
    }
    return response;
  }

  /**
   * get the given image from the path
   * 
   * @param path
   * @return
   * @throws Exception
   */
  protected BufferedImage getImageResponse(String path) throws Exception {
    WebResource wrs = getResource(path);
    BufferedImage image = this.getImageResponse(wrs);
    return image;
  }

  /**
   * get the given image;
   * 
   * @param wrs
   * @return
   */
  protected BufferedImage getImageResponse(WebResource wrs) throws Exception {
    String contentType = "image/jpeg";
    ClientResponse imageResponse = wrs.accept(contentType).get(
        ClientResponse.class);
    assertEquals(wrs.getURI().getPath(), Response.Status.OK.getStatusCode(),
        imageResponse.getStatus());
    BufferedImage image = imageResponse.getEntity(BufferedImage.class);
    assertNotNull(image);
    return image;
  }

  /**
   * get the zip Response for the givne path
   * 
   * @param path
   * @return
   * @throws Exception
   */
  protected ZipInputStream getZipResponse(String path) throws Exception {
    WebResource wrs = getResource(path);
    ZipInputStream result = getZipResponse(wrs);
    return result;
  }

  /**
   * get the given ZIP response
   * 
   * @param wrs
   * @return
   * @throws Exception
   */
  protected ZipInputStream getZipResponse(WebResource wrs) throws Exception {
    String contentType = "application/x-zip-compressed";
    ClientResponse zipResponse = wrs.accept(contentType).get(
        ClientResponse.class);
    assertEquals(wrs.getURI().getPath(), Response.Status.OK.getStatusCode(),
        zipResponse.getStatus());
    ZipInputStream result = new ZipInputStream(
        zipResponse.getEntityInputStream());
    return result;
  }

  /**
   * create a string from a list of strings
   * 
   * @param lines
   * @return
   */
  protected String stringListToString(List<String> lines) {
    String result = "";
    for (String line : lines) {
      result += line + "\n";
    }
    return result;
  }

  public static RestServer getRs() {
    if (rs == null)
      throw new IllegalStateException(
          "static access only possible after startServer in a Test");
    return rs;
  }

  public static void setRs(RestServer rs) {
    TestRestServer.rs = rs;
  }
}
