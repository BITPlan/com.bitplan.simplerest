/**
 * Copyright (C) 2011-2016 BITPlan GmbH
 *
 * Pater-Delp-Str. 1
 * D-47877 Willich-Schiefbahn
 *
 * http://www.bitplan.com
 *
 */
package com.bitplan.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * encryption helper
 * @author wf
 *
 */
@SuppressWarnings("restriction")
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

	private static String base64Encode(byte[] bytes) {
		// NB: This class is internal, and you probably should use another impl
		return new BASE64Encoder().encode(bytes);
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

	private static byte[] base64Decode(String property) throws IOException {
		// NB: This class is internal, and you probably should use another impl
		return new BASE64Decoder().decodeBuffer(property);
	}

}