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
  Copyright (C) 2012-2015 BITPlan GmbH

  Pater-Delp-Str. 1
  D-47877 Willich-Schiefbahn

  http://www.bitplan.com
 */
package com.bitplan.rest;

import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

/**
 * SSL Client Helper https://gist.github.com/outbounder/1069465
 * http://stackoverflow.com/questions/2145431/https-using-jersey-client
 * 
 * @author wf
 * 
 */
public class SSLClientHelper {
	protected static Logger LOGGER = Logger.getLogger("com.bitplan.rest");

	/**
	 * Taken from
	 * http://java.sun.com/javase/6/docs/technotes/guides/security/jsse/
	 * JSSERefGuide.html
	 * 
	 */
	static class ClientX509TrustManager implements X509TrustManager {

		/*
		 * The default PKIX X509TrustManager9. We'll delegate decisions to it, and
		 * fall back to the logic in this class if the default X509TrustManager
		 * doesn't trust it.
		 */
		X509TrustManager pkixTrustManager;

		ClientX509TrustManager(String trustStore, char[] password) throws Exception {
			this(new File(trustStore), password);
		}

		ClientX509TrustManager(File trustStore, char[] password) throws Exception {
			// create a "default" JSSE X509TrustManager.

			KeyStore ks = KeyStore.getInstance("JKS");

			ks.load(new FileInputStream(trustStore), password);

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("PKIX");
			tmf.init(ks);

			TrustManager tms[] = tmf.getTrustManagers();

			/*
			 * Iterate over the returned trustmanagers, look for an instance of
			 * X509TrustManager. If found, use that as our "default" trust manager.
			 */
			for (int i = 0; i < tms.length; i++) {
				if (tms[i] instanceof X509TrustManager) {
					pkixTrustManager = (X509TrustManager) tms[i];
					return;
				}
			}

			/*
			 * Find some other way to initialize, or else we have to fail the
			 * constructor.
			 */
			throw new Exception("Couldn't initialize");
		}

		/*
		 * Delegate to the default trust manager.
		 */
		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			try {
				pkixTrustManager.checkClientTrusted(chain, authType);
			} catch (CertificateException excep) {
        // do any special handling here, or rethrow exception.
			  LOGGER.log(Level.INFO,"Ignoring:"+excep.getMessage());
			}
		}

		/*
		 * Delegate to the default trust manager.
		 */
		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			try {
				pkixTrustManager.checkServerTrusted(chain, authType);
			} catch (CertificateException excep) {
				/*
				 * Possibly pop up a dialog box asking whether to trust the cert chain.
				 */
			  LOGGER.log(Level.INFO,"Ignoring:"+excep.getMessage());
			}
		}

		/*
		 * Merely pass this through.
		 */
		public X509Certificate[] getAcceptedIssuers() {
			return pkixTrustManager.getAcceptedIssuers();
		}
	}

	/**
	 * Inspired from
	 * http://java.sun.com/javase/6/docs/technotes/guides/security/jsse
	 * /JSSERefGuide.html
	 * 
	 */
	static class ClientX509KeyManager implements X509KeyManager {

		/*
		 * The default PKIX X509KeyManager. We'll delegate decisions to it, and fall
		 * back to the logic in this class if the default X509KeyManager doesn't
		 * trust it.
		 */
		X509KeyManager pkixKeyManager;

		/**
		 * construct this ClientKeyManager  with the given keystoreFileName and password
		 * @param keyStoreFileName
		 * @param password
		 * @throws Exception
		 */
		ClientX509KeyManager(String keyStoreFileName, char[] password) throws Exception {
			this(new File(keyStoreFileName), password);
		}

		/**
		 * construct this ClientKeyManager from the given keystore file and password
		 * @param keyStore
		 * @param password
		 * @throws Exception
		 */
		ClientX509KeyManager(File keyStore, char[] password) throws Exception {
			// create a "default" JSSE X509KeyManager.

			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyStore), password);

			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509",
					"SunJSSE");
			kmf.init(ks, password);

			KeyManager kms[] = kmf.getKeyManagers();

			/*
			 * Iterate over the returned keymanagers, look for an instance of
			 * X509KeyManager. If found, use that as our "default" key manager.
			 */
			for (int i = 0; i < kms.length; i++) {
				if (kms[i] instanceof X509KeyManager) {
					pkixKeyManager = (X509KeyManager) kms[i];
					return;
				}
			}

			/*
			 * Find some other way to initialize, or else we have to fail the
			 * constructor.
			 */
			throw new Exception("Couldn't initialize");
		}

		public PrivateKey getPrivateKey(String arg0) {
			return pkixKeyManager.getPrivateKey(arg0);
		}

		public X509Certificate[] getCertificateChain(String arg0) {
			return pkixKeyManager.getCertificateChain(arg0);
		}

		public String[] getClientAliases(String arg0, Principal[] arg1) {
			return pkixKeyManager.getClientAliases(arg0, arg1);
		}

		public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
			return pkixKeyManager.chooseClientAlias(arg0, arg1, arg2);
		}

		public String[] getServerAliases(String arg0, Principal[] arg1) {
			return pkixKeyManager.getServerAliases(arg0, arg1);
		}

		public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
			return pkixKeyManager.chooseServerAlias(arg0, arg1, arg2);
		}
	}

	/**
	 * hostname verifier
	 * 
	 * @author wf
	 * 
	 */
	public static class MyHostnameVerifier implements HostnameVerifier {

		@Override
		public boolean verify(String hostname, SSLSession session) {
			LOGGER.info("SSL Client verifying: " + hostname);
			return true;
		}
	}

	/**
	 * create a Client Configuration
	 * 
	 * @param keystorePassword
	 * @param keystorePath
	 * @param truststorePassword
	 * @param truststorePath
	 * 
	 * @return a Client Configuration
	 * @throws Exception
	 */
	public static ClientConfig configureClient(String truststorePath,
			String truststorePassword, String keystorePath, String keystorePassword)
			throws Exception {
		TrustManager trustManager[] = null;
		KeyManager keyManager[] = null;

		/**
		 * do we have a truststorePath?
		 */
		if (truststorePath != null) {
			trustManager = new TrustManager[] { new ClientX509TrustManager(truststorePath,
					truststorePassword.toCharArray()) };

		} else {
			trustManager = new TrustManager[] { new X509TrustManager() {
				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType)
						throws CertificateException {
				}
			} };
		}
		/**
		 * if keystorePath is available initialize a new ClientKeyManager
		 */
		if (keystorePath != null) {
			keyManager = new KeyManager[] { new ClientX509KeyManager(keystorePath,
					keystorePassword.toCharArray()) };
		}
		// Prepare the SSL context
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(keyManager, trustManager, new SecureRandom());
		} catch (java.security.GeneralSecurityException ex) {
			LOGGER.warning("could not init SSLContext:" + ex.getMessage());
		}
		// set the Properties of the connection
		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
		ClientConfig config = new DefaultClientConfig();
		try {
			config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES,
					new HTTPSProperties(new MyHostnameVerifier(), ctx));
		} catch (Exception e) {
			LOGGER.warning("could not add HTTPSProperties:" + e.getMessage());
		}
		return config;
	}

	/**
	 * create a SSL client based on the default client configuration
	 * 
	 * @return a default client
	 * @throws Exception
	 */
	public static Client createClient() throws Exception {
		return createClient(null, null, null, null);
	}

	/**
	 * create a SSL client with a trust store and key store
	 * 
	 * @param truststorePath
	 * @param truststorePassword
	 * @param keystorePath
	 * @param keystorePassword
	 * @return the Client
	 * @throws Exception
	 */
	public static Client createClient(String truststorePath,
			String truststorePassword, String keystorePath, String keystorePassword)
			throws Exception {
		return Client.create(SSLClientHelper.configureClient(truststorePath,
				truststorePassword, keystorePath, keystorePassword));
	}
}
