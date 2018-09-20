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

import org.junit.After;
import org.junit.Before;

import com.bitplan.hello.rest.HelloServer;
import com.bitplan.rest.RestServer;

/**
 * test the Hello rest server
 * @author wf
 *
 */
public class TestHelloServer extends TestRestServer {
	@Before
	public void initServer() throws Exception {
		startServer();
	}
	
	@After
	public void stopTheServer() throws Exception {
	  super.stopServer();
	}

	@Override
	public RestServer createServer() throws Exception {
		RestServer result = new HelloServer();
		return result;
	}
	
}
