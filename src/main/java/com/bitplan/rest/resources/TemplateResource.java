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
package com.bitplan.rest.resources;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  protected Logger LOGGER = Logger.getLogger("com.bitplan.rest.resources");
  
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
   * @see <a href="http://rythmengine.org/doc/configuration.md#home_template_dir">Rythmengine documentation</a>
   * @param path - the path to the template root
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
   * create a standard Map from a MultivaluedMap
   * 
   * @param form - the form
   * @return the map
   */
  public Map<String, String> asMap(MultivaluedMap<String, String> form) {
    Map<String,String> map = new HashMap<String, String>();
    // take all inputs
    for (String key:form.keySet()) {
      String value=form.getFirst(key);
      if (debug)
        LOGGER.log(Level.INFO,key+"="+value);
      map.put(key,value);
    }
    return map;
  }
  
  /**
   * implode the given string list to a single string separating the parts with the given separator
   * @param slist
   * @param separator
   * @return the imploded string
   */
  public static String implode(List<String> slist, String separator) {
    StringBuilder builder=new StringBuilder();
    boolean first=true;
    for( String s : slist) {
      if (first)
        first=false;
      else
        builder.append( separator);
      builder.append( s);
      
    }
    return builder.toString();
  }
  
  /**
   * add the given formValues to the rootmap using the default separator character ";"
   * @param form
   */
  public void formToMap(MultivaluedMap<String, String> form) {
    formToMap(form,";");
  }
    
  /**
   * convert form to Map
   * @param form - the form
   * @param separator - the separator character to use
   */
  public void formToMap(MultivaluedMap<String, String> form, String separator) {
    // take all inputs
    for (String key:form.keySet()) {
      List<String> values = form.get(key);
      String value=implode(values,separator);
      if (debug)
        LOGGER.log(Level.INFO,key+"="+value);
      rootMap.put(key,value);
    }
  }
      
}
