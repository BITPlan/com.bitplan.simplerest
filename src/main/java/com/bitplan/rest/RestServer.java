package com.bitplan.rest;

/**
 * Rest Server Interface (without Guice) 
 * @author wf
 *
 */
public interface RestServer {

	/**
	 * set the Guice Module to be used for dependency injection
	 * 
	 * @param pGuiceModule
	 */
	public abstract Object getStarter();

	public abstract void setStarter(Object starter);

	/**
	 * @return the settings
	 */
	public abstract RestServerSettings getSettings();

	/**
	 * @param settings
	 *          the settings to set
	 */
	public abstract void setSettings(RestServerSettings settings);

	/**
	 * stop
	 */
	public abstract void stop();

	/**
	 * get the base url
	 * 
	 * @return
	 */
	public abstract String getUrl();

	/**
	 * start the WebServer
	 * 
	 * @see http
	 *      ://jersey.576304.n2.nabble.com/Embedded-Grizzly-with-MVC-templates
	 *      -td4189027.html for SSL security
	 * 
	 * @throws Exception
	 */
	public abstract void startWebServer() throws Exception;

	/**
	 * run the server thread
	 */
	public abstract void run();

	/**
	 * start server in separate thread
	 * 
	 * @param args
	 */
	public abstract RestServer startServer(String[] args);

}