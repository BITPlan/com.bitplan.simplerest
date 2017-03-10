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
package com.bitplan.rest.resources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.rythmengine.RythmEngine;
import org.rythmengine.conf.RythmConfigurationKey;

/**
 * Base Template Resource
 * 
 * @author wf
 *
 */
public class TemplateResource {
  protected boolean debug=true;
  @Context
  protected javax.ws.rs.core.HttpHeaders httpHeaders;

  @Context
  protected UriInfo uri;


  protected Map<String, Object> rootMap = new HashMap<String, Object>();

  private RythmEngine engine;
  Map<String, Object> conf = new HashMap<String, Object>();

  private File templateRoot=new File("src/main/rythm/jersey");
  
  /**
   * set the template Root
   * @see http://rythmengine.org/doc/configuration.md#home_template_dir
   * @param path
   */
  public void setTemplateRoot(String path) {
    templateRoot=new File(path);
    conf.put(RythmConfigurationKey.HOME_TEMPLATE.getKey(), templateRoot);
  }
  
  /**
   * get the Rythm engine
   * 
   * @return
   */
  private RythmEngine getEngine() {
    if (engine == null) {
      conf.put("codegen.compact.enabled", false);
      setTemplateRoot(templateRoot.getAbsolutePath());
      engine = new RythmEngine(conf);
    }
    return engine;
  }

  /**
   * get the Response for the given template
   * 
   * @param templateName
   *          - the name of the template file
   * @return - the Response
   */
  public Response templateResponse(String templateName) {
    File templateFile=new File(templateRoot,templateName);
    String text=getEngine().render(templateFile, rootMap);
    Response result = Response.status(Response.Status.OK).entity(text).build();
    return result;
  }

  /**
   * put all form parameters into template map
   * @param form
   */
  public void formToMap(MultivaluedMap<String, String> form) {
    // take all inputs
    for (String key:form.keySet()) {
      String value=form.getFirst(key);
      if (debug)
        System.out.println(key+"="+value);
      rootMap.put(key,value);
    }
    
  }
}