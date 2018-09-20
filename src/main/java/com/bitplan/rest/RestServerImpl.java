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
package com.bitplan.rest;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URI;
import java.net.URL;
import java.security.Principal;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.http.HttpRequestPacket;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.HttpServerFilter;
import org.glassfish.grizzly.http.server.HttpServerProbe;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.grizzly.http.server.util.Globals;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;
import org.glassfish.grizzly.ssl.SSLEngineConfigurator;
import org.glassfish.grizzly.utils.Charsets;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import com.bitplan.rest.basicauth.BasicAuthSecurityProvider;
import com.bitplan.rest.cors.CORSFilter;
import com.google.inject.AbstractModule;
import com.sun.jersey.api.container.ContainerFactory;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.spi.container.servlet.ServletContainer;
//import com.google.inject.servlet.GuiceFilter;
// import com.sun.jersey.guice.JerseyServletModule;

/**
 * standalone RESTful Server for Jersey and Guice
 * 
 * @author wf
 * 
 */
public class RestServerImpl implements Runnable, UncaughtExceptionHandler,
    RestServer {

  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest");
  // TODO add Guice Support
  // http://randomizedsort.blogspot.de/2011/05/using-guice-ified-jersey-in-embedded.html
  //
  // FIXME needs error handling see
  // http://jots.mypopescu.com/post/1031879539/my-experience-with-jersey-jax-rs
  // http://bhaveshthaker.com/blog/184/technical-article-customize-handling-server-side-exceptions-with-error-codes-using-exceptionmapper-with-jersey-jax-rs-in-java/
  protected RestServerSettings settings = new RestServerSettingsImpl();
  private static RestServerImpl instance;

  // object to be notified of start of server (if any)
  Object starter;
  private HttpServer httpServer;
  protected AbstractModule guiceModule;
  protected String application; // e.g.
                                // com.bitplan.testrestarchitecture.test.CustomerApplication
  protected boolean useServerDefaults = true;
  protected boolean useServlet = false;
  private WebappContext context;
  private Thread serverThread;

  // any exception that might have happened
  private Throwable exception;

  // are we running?
  private boolean running;
  private DateTime startTime;
  private DateTime stopTime;

  /**
   * @return the httpServer
   */
  public HttpServer getHttpServer() {
    return httpServer;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerInterface#getStarter()
   */
  @Override
  public Object getStarter() {
    return starter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.resthelper.RestServerInterface#setStarter(java.lang.Object)
   */
  @Override
  public synchronized void setStarter(Object starter) {
    this.starter = starter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerInterface#getSettings()
   */
  @Override
  public RestServerSettings getSettings() {
    return settings;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.resthelper.RestServerInterface#setSettings(com.bitplan.resthelper
   * .RestServerSettings)
   */
  @Override
  public void setSettings(RestServerSettings settings) {
    this.settings = settings;
  }

  /**
   * constructor
   */
  protected RestServerImpl() {
    instance = this;
  }

  /**
   * get my Instance
   * 
   * @return the instance of this restserver
   */
  public static RestServer getInstance() {
    return instance;
  }

  /**
   * stop this server
   * 
   * @see com.bitplan.rest.RestServer#stop()
   */
  @Override
  public void stop() {
    if (httpServer != null) {
      httpServer.shutdown();
      httpServer = null;
      running = false;
      stopTime = DateTime.now();
      Seconds secs = Seconds.secondsBetween(startTime, stopTime);
      System.out.println("finished after " + secs.getSeconds() + " secs");
      // if someone is waiting for us let him continue ..
      informStarter();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerInterface#getUrl()
   */
  @Override
  public String getUrl() {
    String protocol = "http";
    if (settings.isSecure()) {
      protocol = "https";
    }
    String url = protocol + "://" + settings.getHost() + ":"
        + settings.getPort();
    return url;
  }

  public static final String DocumentRoot = "target/classes/webcontent";

  // http://stackoverflow.com/questions/12398037/whats-the-best-way-to-start-a-grizzlywebserver
  // GrizzlyWebServer srv = new
  // GrizzlyWebServer(settings.getPort(),DocumentRoot);
  // ServletAdapter jerseyAdapter = new ServletAdapter();
  //
  // jerseyAdapter.addInitParameter(
  // PackagesResourceConfig.PROPERTY_PACKAGES,packages);
  // jerseyAdapter.setServletInstance(new ServletContainer());
  //
  // srv.addGrizzlyAdapter(jerseyAdapter, new String[]{"/"})

  // http://stackoverflow.com/questions/15769415/grizzly-and-servletcontainercontext
  /*
   * private static final String JERSEY_SERVLET_CONTEXT_PATH = "";
   * 
   * private static URI getBaseURI() { return
   * UriBuilder.fromUri("http://localhost").port(8080).path("/").build(); }
   * 
   * public static final URI BASE_URI = getBaseURI(); // Create HttpServer and
   * register dummy "not found" HttpHandler /* HttpServer httpServer =
   * GrizzlyServerFactory.createHttpServer(BASE_URI, new HttpHandler() {
   * 
   * @Override public void service(Request rqst, Response rspns) throws
   * Exception { rspns.setStatus(404, "Not found");
   * rspns.getWriter().write("404: not found"); } });
   * 
   * // Initialize and register Jersey Servlet WebappContext context = new
   * WebappContext("WebappContext", JERSEY_SERVLET_CONTEXT_PATH);
   * ServletRegistration registration = context.addServlet("ServletContainer",
   * ServletContainer.class);
   * registration.setInitParameter(ServletContainer.RESOURCE_CONFIG_CLASS,
   * rc.getClass().getName());
   * registration.setInitParameter(ClassNamesResourceConfig.PROPERTY_CLASSNAMES,
   * LolCat.class.getName()); registration.addMapping("/*");
   * context.deploy(httpServer);
   * 
   * System.in.read(); httpServer.stop();
   * 
   * //
   * http://stackoverflow.com/questions/11342981/basic-authentication-for-grizzly
   * -server //
   * http://stackoverflow.com/questions/14608162/basic-http-authentication
   * -with-jersey-grizzly
   */

  /**
   * create a Server based on an url and possibly a ResourceConfig
   * 
   * @param url
   * @param rc
   * @param secure
   *          - true if SSL should be used
   * @param contextPath
   * @return the HttpServer
   * @throws Exception
   */
  public HttpServer createHttpServer(String url, ResourceConfig rc,
      boolean secure, String contextPath) throws Exception {
    // HttpServer result = GrizzlyServerFactory.createHttpServer(url, rc);
    // http://grepcode.com/file/repo1.maven.org/maven2/com.sun.jersey/jersey-grizzly2/1.6/com/sun/jersey/api/container/grizzly2/GrizzlyServerFactory.java#GrizzlyServerFactory.createHttpServer%28java.net.URI%2Ccom.sun.jersey.api.container.grizzly2.ResourceConfig%29
    HttpServer result = new HttpServer();
    final NetworkListener listener = new NetworkListener("grizzly",
        settings.getHost(), settings.getPort());
    result.addListener(listener);
    // do we need SSL?
    if (secure) {
      listener.setSecure(secure);
      SSLEngineConfigurator sslEngineConfigurator = createSSLConfig(true);
      listener.setSSLEngineConfig(sslEngineConfigurator);
    }
    // Map the path to the processor.
    final ServerConfiguration config = result.getServerConfiguration();
    final HttpHandler handler = ContainerFactory.createContainer(
        HttpHandler.class, rc);
    config.addHttpHandler(handler, contextPath);
    return result;
  }

  // FIXME
  public static String TRUSTSTORE_PASSWORD = "changeit";

  /**
   * get the given Store
   * 
   * @param type
   * @param name
   * @return the store file
   * @throws IOException
   */
  public File getStoreFile(String type, String name) throws IOException {
    final ClassLoader cl = RestServerImpl.class.getClassLoader();
    URL surl = cl.getResource(name);
    File result = null;
    if (surl != null) {
      LOGGER.log(Level.WARNING, "getting " + type + " from " + surl.toString());
      result = File.createTempFile(name, ".tmp");
      FileUtils.copyURLToFile(surl, result);
    } else {
      LOGGER.log(Level.WARNING, "could not get " + type + " from resource "
          + name);
    }
    return result;
  }

  /**
   * create SSL Configuration
   * 
   * @param isServer
   *          true if this is for the server
   * @return
   * @throws Exception
   */
  private SSLEngineConfigurator createSSLConfig(boolean isServer)
      throws Exception {
    final SSLContextConfigurator sslContextConfigurator = new SSLContextConfigurator();
    // override system properties
    final File cacerts = getStoreFile("server truststore",
        "truststore_server.jks");
    if (cacerts != null) {
      sslContextConfigurator.setTrustStoreFile(cacerts.getAbsolutePath());
      sslContextConfigurator.setTrustStorePass(TRUSTSTORE_PASSWORD);
    }

    // override system properties
    final File keystore = getStoreFile("server keystore", "keystore_server.jks");
    if (keystore != null) {
      sslContextConfigurator.setKeyStoreFile(keystore.getAbsolutePath());
      sslContextConfigurator.setKeyStorePass(TRUSTSTORE_PASSWORD);
    }

    //
    boolean clientMode = false;
    // force client Authentication ...
    boolean needClientAuth = settings.isNeedClientAuth();
    boolean wantClientAuth = settings.isWantClientAuth();
    SSLEngineConfigurator result = new SSLEngineConfigurator(
        sslContextConfigurator.createSSLContext(), clientMode, needClientAuth,
        wantClientAuth);
    return result;
  }

  /**
   * show Debug Information for the given request
   * 
   * @param req
   */
  public void showDebug(Request req) {
    for (String attrName : req.getAttributeNames()) {
      System.out.println("req attr: " + attrName + "="
          + req.getAttribute(attrName));
    }
    Object certobj = req.getAttribute("javax.servlet.request.X509Certificate");
    if (certobj != null) {
      System.out.println("certificate " + certobj.getClass().getName()
          + " found");
      if (certobj instanceof java.security.cert.X509Certificate[]) {
        java.security.cert.X509Certificate[] certs = (X509Certificate[]) certobj;
        for (java.security.cert.X509Certificate cert : certs) {
          System.out.println("issuer DN:" + cert.getIssuerDN().getName());
          System.out.println("subject DN: " + cert.getSubjectDN().getName());
        }
      }
    }
    for (String headerName : req.getHeaderNames()) {
      System.out.println("req header: " + headerName + "="
          + req.getHeader(headerName));
    }
  }

  /**
   * get the SSL Client certificate for the given request (if any)
   * 
   * @param req
   *          - the request to check
   * @return - the X509Certificate
   */
  protected X509Certificate getCertificate(Request req) {
    X509Certificate result = null;
    Object certobj = req.getAttribute(Globals.CERTIFICATES_ATTR);
    if (certobj != null) {
      String msg = "certificate " + certobj.getClass().getName() + " found";
      LOGGER.finer(msg);
      if (certobj instanceof java.security.cert.X509Certificate[]) {
        java.security.cert.X509Certificate[] certs = (X509Certificate[]) certobj;
        if (certs.length > 0)
          result = certs[0];
      }
    }
    return result;
  }

  /**
   * check the principal
   * 
   * @param principal
   * @throws Exception
   */
  protected Principal checkPrincipal(Principal principal) throws Exception {
    LOGGER.info("Principal is " + principal.getClass().getName());
    LOGGER.info("DN=" + principal.getName());
    // no check of principal
    return principal;
    // CN=Client, OU=HQ Schiefbahn, O=BITPlan GmbH, L=Willich, ST=Germany, C=DE
  }

  /**
   * check that the SSL Client Certificate is valid and return the checked
   * Principal
   * 
   * @param req
   */
  protected Principal checkSSLClientCertificate(Request req) {
    X509Certificate clientCert = this.getCertificate(req);
    Principal result = null;
    if (clientCert != null) {
      Principal subjectDN = clientCert.getSubjectDN();
      try {
        result = checkPrincipal(subjectDN);
      } catch (Exception e) {
        LOGGER.log(Level.SEVERE,
            "checkPrincipal failed with exception " + e.getMessage());
      }
    } else {
      LOGGER.log(Level.SEVERE,
          "SSL Client certificate is missing for " + req.getRequestURI());
    }
    return result;
  }

  /**
   * add a httphandler at the given path
   * 
   * @param context
   *          - document root or classpath
   * @param path
   * @param staticHandler
   */
  public void addHttpHandler(String context, String path, boolean staticHandler) {
    HttpHandler handler = null;
    String type = "";
    if (staticHandler) {
      handler = new StaticHttpHandler(context);
      type = "static";
    } else {
      handler = new CLStaticHttpHandler(this.getClass().getClassLoader(),
          context);
      type = "classpath";
    }
    if (!path.endsWith("/")) {
      path += "/";
    }
    LOGGER.log(Level.INFO, "adding " + type + " httphandler " + context + "->"
        + path);
    httpServer.getServerConfiguration().addHttpHandler(handler, path);
  }

  @SuppressWarnings("unchecked")
  @Override
  public synchronized void createServer() throws Exception {
    if (settings == null)
      throw new IllegalArgumentException(
          "Can't start RestServer Settings are not set");
    // / create a Grizzly Server
    String url = getUrl();
    // http://jersey.java.net/nonav/documentation/latest/user-guide.html#d4e52

    // which packages do ew need to configure?
    String packages = settings.getPackages();
    context = null;
    try {
      if (packages!=null) {
        // do we want basic authentication with a user manager?
        if (settings.getUserManager()!=null) {
          // add the package with the BasicAuthSecurityProvider
          packages+=";com.bitplan.rest.basicauth";
        }
        // for CORS Filter
        packages+=";com.bitplan.rest.cors";
        String pa[] = packages.split(";");
        ResourceConfig rc = new PackagesResourceConfig(pa);
        // more config for provider
        UserManager userManager = settings.getUserManager();
        if (userManager!=null) {
          rc.getContainerRequestFilters().add(new BasicAuthSecurityProvider(userManager));
        }
        rc.getContainerRequestFilters().add(new CORSFilter());
        // http://stackoverflow.com/questions/3677064/jax-rs-jersey-howto-force-a-response-contenttype-overwrite-content-negotiatio
        rc.getMediaTypeMappings().put("json", MediaType.APPLICATION_JSON_TYPE);
        rc.getMediaTypeMappings().put("xml", MediaType.APPLICATION_XML_TYPE);
        rc.getMediaTypeMappings().put("txt", MediaType.TEXT_PLAIN_TYPE);
        // FIXME
        rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING,
            Boolean.TRUE);

        String[] containerRequestFilters = settings
            .getContainerRequestFilters();
        if (containerRequestFilters != null) {
          rc.getProperties().put(
              ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
              containerRequestFilters);
        }
        if (useServlet) {
          // http://stackoverflow.com/questions/15769415/grizzly-and-servletcontainercontext
          httpServer = GrizzlyServerFactory.createHttpServer(new URI(url),
              new HttpHandler() {

                @Override
                public void service(Request rqst, Response rspns)
                    throws Exception {
                  rspns.setStatus(404, "Not found");
                  rspns.getWriter().write("404: not found");
                }
              });
          context = new WebappContext("Context", "");
          ServletRegistration registration = context.addServlet(
              "ServletContainer", ServletContainer.class);
          registration.setInitParameter(
              "com.sun.jersey.config.property.packages", packages);
          // add security filter (which handles http basic authentication)
          /**
           * registration.setInitParameter(
           * ResourceConfig.PROPERTY_CONTAINER_REQUEST_FILTERS,
           * containerRequestFilters);
           */
          registration.addMapping("/*");
        } else {
          httpServer = createHttpServer(url, rc, settings.isSecure(),
              settings.getContextPath());
        }
      } else {
        httpServer = createHttpServer(url, null, settings.isSecure(),
            settings.getContextPath());
      }
    } catch (Throwable th) {
      System.err.println(th.getMessage());
      th.printStackTrace();
    }

    if (httpServer == null) {
      throw new Exception("Couldn't create http server");
    }

    // remote address handling
    httpServer.getServerConfiguration().getMonitoringConfig()
        .getWebServerConfig().addProbes(new HttpServerProbe.Adapter() {
          @SuppressWarnings("rawtypes")
          @Override
          public void onRequestReceiveEvent(HttpServerFilter filter,
              Connection connection, Request request) {
            String remote_addr = request.getRemoteAddr();
            HttpRequestPacket req = request.getRequest();
            String msg = "remote addr is " + remote_addr
                + " for request of type " + req.getClass().getName();
            if (settings.isDebug())
              LOGGER.info(msg);
            req.setHeader("remote_addr", remote_addr);
            // showDebug(request);
            Principal principal=null;
            if (settings.isSecure()) {
              principal = RestServerImpl.this
                  .checkSSLClientCertificate(request);
              if (principal == null) {
                // FIXME - we'd like to get a proper status here!
                if (settings.isNeedClientAuth()) {
                  LOGGER.log(Level.SEVERE,
                      "no principal with needClientAuth=true");
                  request.setRequestURI("invalidaccess");
                }
              }
            } 
            // did we get a principal for this request?
            if (principal != null) {
              // http://stackoverflow.com/questions/909185/jersey-security-and-session-management
              // LOGGER.info("remote user: "+request.getRemoteUser());

              // this would have been simple but does not work see
              // http://stackoverflow.com/questions/20105005/unsupportedoperationexception-getuserprincipal
              request.setUserPrincipal(principal);

              // work-around
              PrincipalCache.add(principal);
              String principal_id = PrincipalCache.getId(principal);
              req.setHeader("principal_id", principal_id);
            }
            // debug(req);

          }

          @SuppressWarnings("rawtypes")
          @Override
          public void onRequestCompleteEvent(HttpServerFilter filter,
              Connection connection, Response response) {
            // do nothing
            if (settings.isDebug())
              LOGGER.info("request complete");
          }
        });

    // add configured classpath handlers
    for (String path : this.getSettings().getClassPathHandlers().keySet()) {
      String clPath = this.getSettings().getClassPathHandlers().get(path);
      // relative or absolute path?
      if (!path.startsWith("/")) {
        path = settings.getContextPath() + "/" + path;
      }
      addHttpHandler(clPath, path, false);
    }

    // set default encoding
    httpServer.getServerConfiguration().setDefaultQueryEncoding(
        Charsets.UTF8_CHARSET);
  }

  /**
   * handle basic authentication request
   * @param userManager - the information about users
   * @param filter
   * @param connection
   * @param request
   * @param req
   * @return - a valid principal that is a User from the UserManager that has given the right basic auth credentials
   */
  @SuppressWarnings("rawtypes")
  protected Principal handleBasicAuth(UserManager userManager,
      HttpServerFilter filter, Connection connection, Request request,
      HttpRequestPacket req) {
    String auth = req.getHeader("authorization");
    if (auth==null) {
      throw new WebApplicationException(Status.UNAUTHORIZED);
    }
    return null;
  }

  /**
   * if there is some thread waiting for us to start
   * inform it that it may continue
   */
  public void informStarter() {
    if (starter != null) {
      synchronized (starter) {
        starter.notify();
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerInterface#startWebServer()
   */
  @Override
  public synchronized void startWebServer() throws Exception {
    createServer();
    // start server
    httpServer.start();
    if (context != null) {
      context.deploy(httpServer);
    }

    // FIXME?
    /*
     * if (guiceModule != null) { WebappContext context =
     * createWebappContext(""); context.deploy(srv); }
     */
    LOGGER.log(Level.INFO, "starting server for URL: " + getUrl());
    running = true;
    informStarter();
    int sleep = 0;
    // timeOut is in Secs - we go by 1/20th of sec = 50 millisecs
    while (running && (sleep < settings.getTimeOut() * 20)) {
      // System.out.println(sleep+":"+running);
      // sleep 50 millisecs
      Thread.sleep(50);
      sleep++;
    }
    stop();
  }

  /**
   * create a Web Application Context
   * 
   * @param path
   * @return
   */
  /*
   * private static final WebappContext createWebappContext(String path) { if
   * (path == null) throw new NullPointerException(
   * "Can not create WebappContext with a null context path"); if
   * ("/".equals(path)) path = ""; // In order for Guice to work ok
   * WebappContext context = new WebappContext("RestServerContext", path);
   * context.addListener(GuiceListener.class);
   * context.addFilter(GuiceFilter.class.getName(), GuiceFilter.class)
   * .addMappingForUrlPatterns(null, "/*"); return context; }
   */

  /**
   * check the availability of a port
   * 
   * @param port
   * @return if the port is available
   */
  public boolean isPortAvailable(int port) {
    try (java.net.Socket ignored = new java.net.Socket("localhost", port)) {
      return false;
    } catch (IOException ignored) {
      return true;
    }
  }

  /**
   * @return the exception
   */
  public Throwable getException() {
    return exception;
  }

  /**
   * @param exception
   *          the exception to set
   */
  public void setException(Throwable exception) {
    this.exception = exception;
  }

  /**
   * start
   * 
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception {
    instance = new RestServerImpl();
    instance.settings.parseArguments(args);
    instance.startWebServer();
  } // main

  /*
   * (non-Javadoc)
   * 
   * @see com.bitplan.resthelper.RestServerInterface#run()
   */
  @Override
  public void run() {
    try {
      this.startWebServer();
    } catch (Exception e) {
      if (settings.isDebug())
        e.printStackTrace();
      exception = e;
      stop();
    }
  }

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    stop();
    exception = e;
    throw new RuntimeException(e);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.bitplan.resthelper.RestServerInterface#startServer(java.lang.String[])
   */
  @Override
  public RestServer startServer(String[] args) {
    settings.parseArguments(args);
    startTime = DateTime.now();
    serverThread = new Thread(this);
    serverThread.setUncaughtExceptionHandler(this);
    serverThread.start();
    return this;
  }

}
