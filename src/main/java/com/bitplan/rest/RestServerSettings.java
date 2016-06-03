package com.bitplan.rest;

import java.util.Map;

/**
 * Copyright (C) 2011 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */

/**
 * settings for a Rest Server
 * @author wf
 *
 */
public interface RestServerSettings {

	/**
	 * @return the host
	 */
	public abstract String getHost();

	/**
	 * @param host the host to set
	 */
	public abstract void setHost(String host);

	/**
	 * @return the port
	 */
	public abstract int getPort();

	/**
	 * @param port the port to set
	 */
	public abstract void setPort(int port);
	
	/**
	 * @return the debug
	 */
	public boolean isDebug();

	/**
	 * @param debug the debug to set
	 */
	public void setDebug(boolean debug);
	
	/**
	 * getter for SSL usage
	 * @return true if https / SSL is used
	 */
	public boolean isSecure();
	
	/**
	 * set https / SSL usage
	 * @param pSecure true if SSL should be forced
	 */
	public void setSecure(boolean pSecure);

	/**
	 * @return the wantClientAuth
	 */
	public boolean isWantClientAuth();

	/**
	 * @param wantClientAuth the wantClientAuth to set
	 */
	public void setWantClientAuth(boolean wantClientAuth);

	/**
	 * @return the needClientAuth
	 */
	public boolean isNeedClientAuth();
	
	/**
	 * @param needClientAuth the needClientAuth to set
	 */
	public void setNeedClientAuth(boolean needClientAuth);
	
	/**
	 * @return the timeOut
	 */
	public abstract long getTimeOut();

	/**
	 * @param timeOut the timeOut to set
	 */
	public abstract void setTimeOut(long timeOut);

	public abstract String getPackages();

	public abstract void setPackages(String packages);
	
	/**
	 * get the context Path
	 * @return
	 */
	public String getContextPath();
	
	/**
	 * set the context path
	 * @param contextPath
	 * e.g. 
	 * /app - this will be preprended to all url'path 
	 * so /app/entity will call the path /entity 
	 */
	public void setContextPath(String contextPath);

	/**
	 * parse Command line arguments
	 * 
	 * @param args
	 */
	public abstract void parseArguments(String[] args);

	String[] getContainerRequestFilters();

	void setContainerRequestFilters(String[] containerRequestFilters);
	
	/**
	 * @return the persistence property file name
	 */
	public String getPersistencePropertyFileName();

	/**
	 * @param pName the persistence property file name to set
	 */
	public void setPersistencePropertyFileName(String pName);

	/**
	 * add a class path handler
	 * @param string classpathPath
	 * @param string documentRoot
	 * @throws Exception 
	 */
	public abstract void addClassPathHandler(String documentRoot,String classpathPath) throws Exception;
	
	/**
	 * get the ClassPath Handlers
	 * @return
	 */
	public Map<String,String> getClassPathHandlers();

	

}