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
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.bitplan.hello.rest.ClickServer;
import com.bitplan.rest.RestServer;
import com.bitplan.rest.clicks.ClickStream;
import com.bitplan.rest.clicks.ClickStreamManager;

/**
 * test the click Filter
 * @author wf
 *
 */
public class TestClickFilter extends TestHelloServer {

  @Override
  public RestServer createServer() throws Exception {
    RestServer result = new ClickServer();
    return result;
  }
  
  @Test
  public void testClickFilter() throws Exception {
    // debug=true;
    ClickStreamManager csm=ClickStreamManager.getInstance();
    csm.setDebug(debug);
    int hits=10;
    for (int i=1;i<=10;i++) {
      super.check("/hello/hello", "Hello");
    }
    assertEquals(1,csm.getClickStreams().size());
    ClickStream clickStream=csm.getClickStreams().get(0);
    assertEquals(hits,clickStream.getPageHits().size());
    csm.logRotate();
    File jsonFile=csm.getJsonFile();
    assertTrue(jsonFile.exists());
  }
}
