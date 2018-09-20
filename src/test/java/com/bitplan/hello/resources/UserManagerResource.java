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
package com.bitplan.hello.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.bitplan.rest.User;
import com.bitplan.rest.resources.BaseManagerResource;
import com.bitplan.rest.users.UserManagerImpl;

@Path("/users")
/**
 * UserManager
 * @author wf
 *
 */
public class UserManagerResource extends BaseManagerResource<UserManagerImpl,User> {
  
  /**
   * constructor
   */
  public UserManagerResource() {
    setManager(UserManagerImpl.getInstance());
  }
  
  @GET
  @Produces("text/html")
  public Response getUsers() {
    rootMap.put("userManager", UserManagerImpl.getInstance());
    Response response = super.templateResponse("users.rythm");
    return response;
  }
 
}
