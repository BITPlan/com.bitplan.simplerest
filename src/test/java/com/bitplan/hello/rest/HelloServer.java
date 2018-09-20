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
package com.bitplan.hello.rest;

import com.bitplan.rest.RestServerImpl;
import com.bitplan.rest.providers.JsonProvider;
import com.bitplan.rest.users.UserManagerImpl;

/**
 * Hello Server
 * @author wf
 *
 */
public class HelloServer extends RestServerImpl {
  /**
   * construct Hello Server
   * setting defaults
   * @throws Exception 
   */
  public HelloServer() throws Exception {
    settings.setHost("0.0.0.0");
    settings.setPort(8111);
    settings.setContextPath("/hello");
    settings.addClassPathHandler("/", "/static/");
    String packages="com.bitplan.hello.resources;com.bitplan.rest.providers";
    JsonProvider.registerType(UserManagerImpl.class);
    settings.setPackages(packages);
   }
  
   /**
   * start Server
   * 
   * @param args
   * @throws Exception
   */
   public static void main(String[] args) throws Exception {
     HelloServer rs=new HelloServer();
     rs.settings.parseArguments(args);
     rs.startWebServer();
   } // main
}
