/**
 * Copyright (C) 2014-1016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.rest.test;

import org.junit.Before;

import com.bitplan.hello.rest.HelloServer;
import com.bitplan.rest.RestServer;
import com.bitplan.rest.User;

/**
 * test the appbuilder rest server
 * @author wf
 *
 */
public class TestHelloServer extends TestRestServer {
	@Before
	public void initServer() throws Exception {
		startServer();
	}

	@Override
	public RestServer createServer() throws Exception {
		RestServer result = new HelloServer();
		return result;
	}
	
}
