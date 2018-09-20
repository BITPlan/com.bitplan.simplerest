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
/**
  Copyright (C) 2016 BITPlan GmbH
  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn
  http://www.bitplan.com
 */
package com.bitplan.rest.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.bitplan.rest.Crypt;
import com.bitplan.rest.CryptImpl;

/**
 * test encryption
 * 
 * @author wf
 *
 */
public class TestCrypt {
  boolean debug = false;

  @Test
  public void testCrypt() throws Exception {
    Crypt pcf = new CryptImpl("XkMhYb57ljt4pR3rA14w3w7V1NWdojRa", "p4qzVBSR");
    String originalPassword = "secretPassword";
    if (debug)
      System.out.println("Original password: " + originalPassword);
    String encryptedPassword = pcf.encrypt(originalPassword);
    if (debug)
      System.out.println("Encrypted password: " + encryptedPassword);
    String decryptedPassword = pcf.decrypt(encryptedPassword);
    if (debug)
      System.out.println("Decrypted password: " + decryptedPassword);
    assertEquals(originalPassword, decryptedPassword);
  }

}
