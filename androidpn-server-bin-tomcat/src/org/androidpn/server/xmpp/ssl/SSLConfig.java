/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.xmpp.ssl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.androidpn.server.util.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.org.apache.bcel.internal.util.ClassPath;

/** 
 * Configuration class for SSL settings.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class SSLConfig {

    private static final Log log = LogFactory.getLog(SSLConfig.class);

    private static SSLContext sslContext;

    private static String storeType;

    private static KeyStore keyStore;

    private static String keyStoreLocation;

    private static String keyPass;

    private static KeyStore trustStore;

    private static String trustStoreLocation;

    private static String trustPass;
    
    private static URL classPath;

    private SSLConfig() {
    }

    static {
        storeType = Config.getString("xmpp.ssl.storeType", "JKS");
        keyStoreLocation = Config.getString("xmpp.ssl.keystore", "conf"
                + File.separator + "security" + File.separator + "keystore");
        keyStoreLocation = classPath.getPath() + File.separator
                + keyStoreLocation;
        keyPass = Config.getString("xmpp.ssl.keypass", "changeit");
        trustStoreLocation = Config.getString("xmpp.ssl.truststore", "conf"
                + File.separator + "security" + File.separator + "truststore");
        trustStoreLocation = classPath.getPath()
                + File.separator + trustStoreLocation;
        trustPass = Config.getString("xmpp.ssl.trustpass", "changeit");
        
        classPath = SSLConfig.class.getResource("/");

        log.debug("keyStoreLocation=" + keyStoreLocation);
        log.debug("trustStoreLocation=" + trustStoreLocation);

        // Load keystore
        try {
            keyStore = KeyStore.getInstance(storeType);
            keyStore.load(new FileInputStream(keyStoreLocation), keyPass
                    .toCharArray());
        } catch (Exception e) {
            log.error(
                    "SSLConfig startup problem.\n" + "  storeType: ["
                            + storeType + "]\n" + "  keyStoreLocation: ["
                            + keyStoreLocation + "]\n" + "  keyPass: ["
                            + keyPass + "]", e);
            keyStore = null;
        }

        // Load truststore
        try {
            trustStore = KeyStore.getInstance(storeType);
            trustStore.load(new FileInputStream(trustStoreLocation), trustPass
                    .toCharArray());

        } catch (Exception e) {
            try {
                trustStore = KeyStore.getInstance(storeType);
                trustStore.load(null, trustPass.toCharArray());
            } catch (Exception ex) {
                log.error("SSLConfig startup problem.\n" + "  storeType: ["
                        + storeType + "]\n" + "  trustStoreLocation: ["
                        + trustStoreLocation + "]\n" + "  trustPass: ["
                        + trustPass + "]", e);
                trustStore = null;
            }
        }

        // Init factory        
        try {
            sslContext = SSLContext.getInstance("TLS");

            KeyManagerFactory keyFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFactory.init(keyStore, SSLConfig.getKeyPassword().toCharArray());

            TrustManagerFactory c2sTrustFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            c2sTrustFactory.init(trustStore);

            sslContext.init(keyFactory.getKeyManagers(), c2sTrustFactory
                    .getTrustManagers(), new java.security.SecureRandom());

        } catch (Exception e) {
            log.error("SSLConfig factory setup problem." + "  storeType: ["
                    + storeType + "]\n" + "  keyStoreLocation: ["
                    + keyStoreLocation + "]\n" + "  keyPass: [" + keyPass
                    + "]\n" + "  trustStoreLocation: [" + trustStoreLocation
                    + "]\n" + "  trustPass: [" + trustPass + "]", e);
            keyStore = null;
            trustStore = null;
        }
    }

    /**
     * Get the SSLContext.
     *
     * @return the SSLContext
     */
    public static SSLContext getc2sSSLContext() {
        return sslContext;
    }

    /**
     * Get the Key Store location.
     *
     * @return the keystore location
     */
    public static String getKeystoreLocation() {
        return keyStoreLocation;
    }

    /**
     * Get the Trust Store location.
     *
     * @return the Trust Store location
     */
    public static String getc2sTruststoreLocation() {
        return trustStoreLocation;
    }

    /**
     * Get the Store Type.
     * 
     * @return the Store Type
     */
    public static String getStoreType() {
        return storeType;
    }

    /**
     * Get the Key Store.
     *
     * @return the Key Store
     */
    public static KeyStore getKeyStore() throws IOException {
        if (keyStore == null) {
            throw new IOException();
        }
        return keyStore;
    }

    /**
     * Get the Key Store password.
     *
     * @return the key store password
     */
    public static String getKeyPassword() {
        return keyPass;
    }

    /** 
     * Get the Trust Store.
     *
     * @return the trust store
     */
    public static KeyStore getc2sTrustStore() throws IOException {
        if (trustStore == null) {
            throw new IOException();
        }
        return trustStore;
    }

    /**
     * Return the Trust Store password.
     *
     * @return the trust store password
     */
    public static String getc2sTrustPassword() {
        return trustPass;
    }

}
