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
