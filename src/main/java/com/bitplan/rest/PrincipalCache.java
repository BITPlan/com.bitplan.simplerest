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
	 * @return the principal
	 */
	public static Principal get(String id) {
		Principal result=principalCache.get(id);
		return result;
	}


	/**
	 * return an id for the principal
	 * @param principal
	 * @return the id of the given principal
	 */
	public static String getId(Principal principal) {
		return principal.getName();
	}

}
