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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
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
	
	protected Logger LOGGER=Logger.getLogger("com.bitplan.rest.test");
	
	protected boolean debug = true;
	protected static RestServer rs;
	// use a Date as a semaphore
	private static Date semaphore=new Date();
	public int testPort=8085;
	
	String[] contentTypes = { "text/xml", "text/html", "text/plain",
			"application/json", "application/xml" };

	/**
	 * start the Server
	 * @return the baseUrl
	 * @throws Exception 
	 */
	public String startServer() throws Exception {
		if (rs == null) {
			rs=createServer();
			rs.getSettings().setPort(testPort);
			rs.setStarter(semaphore);
			String args[] = {};
			rs.startServer(args);
			synchronized (semaphore) {
				semaphore.wait();
			}
		}
		String baseUrl = rs.getUrl().replace("0.0.0.0","localhost");
		return baseUrl;
	}
	
	 /**
   * check the given path whether it contains the expected string
   * @param path
   * @param expected
   * @throws Exception
   */
  protected void check(String path, String expected) throws Exception {
    String responseString=getResponseString("text/html", path);
    assertTrue(responseString.contains(expected));
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
	 * @throws IOException
	 */
	public String upload(String url, File uploadFile) throws IOException {
		WebResource resource = Client.create().resource(url);
		FormDataMultiPart form = new FormDataMultiPart();
		form.field("fileName", uploadFile.getName());
		FormDataBodyPart fdp = new FormDataBodyPart("content",
				new FileInputStream(uploadFile),
				MediaType.APPLICATION_OCTET_STREAM_TYPE);
		form.bodyPart(fdp);
		String response = resource.type(MediaType.MULTIPART_FORM_DATA).post(String.class, form);
		return response;
	}
	

	/**
	 * get the given resource for the given path (relative to base url of server
	 * to be started
	 * 
	 * @param path - either a relative or absolute path
	 *     for relative paths not starting with "http" the server is started and the relative
	 *     path is added to the servers base url
	 * @return
	 * @throws Exception 
	 */
	public WebResource getResource(String path) throws Exception {
		String url=path;
		if (!path.startsWith("http")) {
			url = startServer() + path;
		}
		WebResource wrs = Client.create().resource(url);
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
		WebResource wrs = getResource(path);
		if (debug)
			System.out.println("contentType:" + contentType);
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
			LOGGER.log(Level.INFO," posting to path "+path+" data='"+data+"'");
		ClientResponse response=wrs.accept(contentType).type(contentType).post(ClientResponse.class,data);
		return response;
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
	public ClientResponse getPostResponse(String contentType, String path,
			Map<String, String> pFormData, boolean debug) throws Exception {
		MultivaluedMap<String, String> lFormData = new MultivaluedMapImpl();
		for (String key : pFormData.keySet()) {
			lFormData.add(key, pFormData.get(key));
		}
		WebResource wrs = getResource(path);
		ClientResponse response = wrs.type(
				MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(ClientResponse.class,
				lFormData);
		// do not force response string - further calls to getResponseString will fail
		boolean force=false;
		if (debug && force) {
			String responseString = this.getResponseString(response);
			LOGGER.log(Level.INFO,responseString);
		}
		return response;
	}
	
	
	/**
	 * get the given image from the path
	 * @param path
	 * @return
	 * @throws Exception
	 */
	protected BufferedImage getImageResponse(String path) throws Exception {
		WebResource wrs = getResource(path);
		BufferedImage image=this.getImageResponse(wrs);
		return image;
	}
	
	/**
	 * get the given image;
	 * @param wrs
	 * @return
	 */
	protected BufferedImage getImageResponse(WebResource wrs)  throws Exception {
		String contentType="image/jpeg";
		ClientResponse imageResponse = wrs.accept(contentType).get(ClientResponse.class);
		assertEquals(wrs.getURI().getPath(),Response.Status.OK.getStatusCode(), imageResponse.getStatus());
		BufferedImage image = imageResponse.getEntity(BufferedImage.class);
		assertNotNull(image);
		return image;
	}
	
	/**
	 * get the zip Response for the givne path
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
	 * @param wrs
	 * @return
	 * @throws Exception
	 */
	protected ZipInputStream getZipResponse(WebResource wrs) throws Exception {
		String contentType="application/x-zip-compressed";
		ClientResponse zipResponse = wrs.accept(contentType).get(ClientResponse.class);
		assertEquals(wrs.getURI().getPath(),Response.Status.OK.getStatusCode(), zipResponse.getStatus());
		ZipInputStream result=new ZipInputStream(zipResponse.getEntityInputStream());
		return result;
	}
	
	/**
	 * create a string from a list of strings
	 * @param lines
	 * @return
	 */
	protected String stringListToString(List<String> lines) {
		String result="";
		for (String line:lines) {
			result+=line+"\n";
		}
		return result;
	}

	public static RestServer getRs() {
		if (rs==null)
			throw new IllegalStateException("static access only possible after startServer in a Test");
		return rs;
	}

	public static void setRs(RestServer rs) {
		TestRestServer.rs = rs;
	}
}
