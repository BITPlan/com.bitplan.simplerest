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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

/**
 * encryption helper
 * @author wf
 *
 */
public class CryptImpl implements Crypt {

	private char[] cypher;
	private byte[] salt;

	/**
	 * create me from a password and salt
	 * 
	 * @param pCypher
	 * @param pSalt
	 */
	public CryptImpl(String pCypher, String pSalt) {
		this.cypher = pCypher.toCharArray();
		this.salt = pSalt.getBytes();
	}


	/* (non-Javadoc)
   * @see com.bitplan.rest.Crypt#encrypt(java.lang.String)
   */
	@Override
  public String encrypt(String property) throws GeneralSecurityException,
			UnsupportedEncodingException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(cypher));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, 20));
		return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
	}

	/* (non-Javadoc)
   * @see com.bitplan.rest.Crypt#decrypt(java.lang.String)
   */
	@Override
  public String decrypt(String property) throws GeneralSecurityException,
			IOException {
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance("PBEWithMD5AndDES");
		SecretKey key = keyFactory.generateSecret(new PBEKeySpec(cypher));
		Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
		pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, 20));
		return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
	}

	/**
	 * encode the given bytes
	 * @param bytesToEncode - the bytes to encode
	 * @return the encoded bytes
	 */
	private static String base64Encode(byte[] bytesToEncode) {
    // NB: This class is internal, and you probably should use another implementation
	  // see https://stackoverflow.com/a/32227181/1497139
	  // String encryptedValue = new BASE64Encoder().encode(bytesToEncode);
	  String encryptedValue = new String(Base64.getEncoder().encode(bytesToEncode));
    return encryptedValue;
  }
	
	/**
	 * decode the given encryptedDataString
	 * @param encryptedDataString - the encrypted string
	 * @return the decoded byte array
	 * @throws IOException
	 */
	private static byte[] base64Decode(String encryptedDataString) throws IOException {
		// NB: This class is internal, and you probably should use another implementation
	  //  byte[] decodedValue= new BASE64Decoder().decodeBuffer(encryptedDataString);
	  byte[] decodedValue = Base64.getDecoder().decode(encryptedDataString);
		return decodedValue;
	}

}