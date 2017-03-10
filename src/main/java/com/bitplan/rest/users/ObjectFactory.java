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
package com.bitplan.rest.users;

import javax.xml.bind.annotation.XmlRegistry;

import com.bitplan.rest.User;
import com.bitplan.rest.UserManager;

/**
 * see
 * http://blog.bdoughan.com/2010/07/moxy-jaxb-map-interfaces-to-xml.html?
 * showComment=1331064265196#c8558952364670027314
 * 
 * @author wf
 *
 */
@XmlRegistry
public class ObjectFactory {

  public UserManagerImpl createUserManagerImpl() {
    return new UserManagerImpl();
  }

  public UserManager createUserManager() {
    return new UserManagerImpl();
  }
  
  public UserImpl createUserImpl() {
    return new UserImpl();
  }
  
  public User createUser() {
    return new UserImpl();
  }
}
