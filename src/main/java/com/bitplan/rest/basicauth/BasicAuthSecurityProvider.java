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
package com.bitplan.rest.basicauth;

import java.security.Principal;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import com.bitplan.rest.PrincipalCache;
import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;
import com.bitplan.rest.users.UserImpl;
import com.sun.jersey.api.container.MappableContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class BasicAuthSecurityProvider implements ContainerRequestFilter {
  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest.basicauth");

  @Context
  private HttpContext ctx;

  private UserManager userManager;

  public static boolean enabled = true;
  private static final String REALM = "simpleREST SecurityProvider";

  /**
   * create me with the given userManager
   * 
   * @param userManager
   */
  public BasicAuthSecurityProvider(UserManager userManager) {
    this.userManager = userManager;
  }

  // http://www.hameister.org/JEE7_JAXRS2_Filter.html
  @Override
  public ContainerRequest filter(ContainerRequest request) {
    HttpRequestContext req = ctx.getRequest();
    // ExtendedUriInfo uriinfo = ctx.getUriInfo();
    LOGGER.log(Level.INFO, "req is " + req.getClass().getName() + "/"
        + request.getClass().getName());
    // UnsupportedOperationException
    // Principal principal=req.getUserPrincipal();
    // Principal principal = request.getUserPrincipal();
    String principal_id = request.getHeaderValue("principal_id");
    Principal principal = PrincipalCache.get(principal_id);
    if (principal != null) {
      LOGGER.log(Level.INFO,
          "principal in SecurityProvider is " + principal.getName());
      request.setSecurityContext(new Authorizer(principal));
      LOGGER.log(Level.INFO, "request " + request.getPath());
    } else {
      User user;
      try {
        user = authenticate(request);
      } catch (WebApplicationException wae) {
        throw wae;
      }
      request.setSecurityContext(new Authorizer(user));
    }
    return request;
  }

  /**
   * 401 triggering exception
   */
  public class AuthenticationException extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    /**
     * create an Authentication Exception with the given message and realm
     * 
     * @param message
     * @param realm
     */
    public AuthenticationException(String message, String realm) {
      super(Response.status(Response.Status.UNAUTHORIZED)
          .header("WWW-Authenticate", "Basic realm=\"" + realm + "\"")
          .entity(message).type(MediaType.TEXT_PLAIN).build());
      this.realm = realm;
    }

    private String realm = null;

    public String getRealm() {
      return this.realm;
    }

  }

  /**
   * authenticate the user given a request
   * 
   * @param request
   * @return
   */
  private User authenticate(ContainerRequest request)
      throws WebApplicationException {
    // Extract authentication credentials
    String authentication = request
        .getHeaderValue(ContainerRequest.AUTHORIZATION);
    if (authentication == null) {
      if (enabled)
        throw new MappableContainerException(new AuthenticationException(
            "Authentication credentials are required", REALM));
      else
        return null;
    }
    if (!authentication.startsWith("Basic ")) {
      return null;
      // additional checks should be done here
      // "Only HTTP Basic authentication is supported"
    }
    authentication = authentication.substring("Basic ".length());
    String[] values = new String(Base64.base64Decode(authentication))
        .split(":");
    if (values.length < 2) {
      throw new WebApplicationException(400);
      // "Invalid syntax for username and password"
    }
    String username = values[0];
    String password = values[1];
    if ((username == null) || (password == null)) {
      throw new WebApplicationException(400);
      // "Missing username or password"
    }

    // Validate the extracted credentials
    User user = userManager.getById(username);
    boolean valid = false;
    if (user != null) {
      String encryptedPassword;
      try {
        encryptedPassword = userManager.getCrypt().encrypt(password);
        if (encryptedPassword.equals(user.getPassword())) {
          LOGGER.log(
              Level.INFO,
              "USER " + user.getId() + "(" + user.getFirstname() + " "
                  + user.getName() + ") AUTHENTICATED");
          valid = true;
        }
      } catch (Exception e) {
        // TODO Auto-generated catch block
      }
    }
    if (!valid) {
      LOGGER.log(Level.INFO, "USER NOT AUTHENTICATED");
      throw new MappableContainerException(new AuthenticationException(
          "Invalid username or password\r\n", REALM));
    }
    return user;
  }

  /**
   * a Security context
   * 
   * @author wf
   *
   */
  public class Authorizer implements SecurityContext {

    private User user;
    private Principal principal;
    String scheme;

    /**
     * initialize me with a given user
     * 
     * @param user
     */
    public Authorizer(final User user) {
      this.user = user;
      this.scheme = SecurityContext.BASIC_AUTH;
      this.principal = new Principal() {

        public String getName() {
          return user.getId();
        }
      };
    }

    /**
     * initialize the Authorizer with a predefined principal
     * 
     * @param pPrincipal
     */
    public Authorizer(Principal pPrincipal) {
      this.scheme = SecurityContext.CLIENT_CERT_AUTH;
      this.principal = pPrincipal;
      this.user = new UserImpl();
      user.setId(pPrincipal.getName());
      user.setRole("user");
    }

    public Principal getUserPrincipal() {
      return this.principal;
    }

    public boolean isUserInRole(String role) {
      return (role.equals(user.getRole()));
    }

    public boolean isSecure() {
      return "https".equals(ctx.getUriInfo().getRequestUri().getScheme());
    }

    public String getAuthenticationScheme() {
      return this.scheme;
    }
  }

  // http://www.solutionoferror.com/java/use-containerrequestfilter-in-jersey-without-web-xml-79849.asp
  // http://stackoverflow.com/questions/17300218/jersey-containerrequestfilter-not-triggered
  // http://subversion.jfrog.org/artifactory/public/tags/2.1.0/rest/src/main/java/org/artifactory/rest/common/RestAuthenticationFilter.java
  // Possible basic auth solution:
  // http://sites.gbif.org/common-resources/gbif-common-ws/xref/org/gbif/ws/server/filter/AuthFilter.html
  // http://2rdscreenretargeting.blogspot.de/2012/06/secure-jersey-with-oauth2.html
}
