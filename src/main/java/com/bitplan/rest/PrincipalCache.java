/**
 * Copyright (C) 2011-2013 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 * 
 */
package com.bitplan.rest;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Cache for Principal Information
 * @author wf
 *
 */
public class PrincipalCache {

	protected static Map<String,Principal> principalCache=new HashMap<String,Principal>();
	
	/**
	 * add the given principal to the cache
	 * @param principal
	 */
	public static void add(Principal principal) {
		String id=getId(principal);
		principalCache.put(id, principal);
	}
	
	/**
	 * get the Principal for the given id
	 * @param id
	 * @return
	 */
	public static Principal get(String id) {
		Principal result=principalCache.get(id);
		return result;
	}


	/**
	 * return an id for the principal
	 * @param principal
	 * @return
	 */
	public static String getId(Principal principal) {
		return principal.getName();
	}

}
