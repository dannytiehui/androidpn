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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * SSL Trust Manager Factory class.
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class SSLTrustManagerFactory {

    private static final Log log = LogFactory
            .getLog(SSLTrustManagerFactory.class);

    public static TrustManager[] getTrustManagers(String storeType,
            String truststore, String trustpass)
            throws NoSuchAlgorithmException, KeyStoreException, IOException,
            CertificateException {
        TrustManager[] trustManagers;
        if (truststore == null) {
            trustManagers = null;
        } else {
            TrustManagerFactory trustFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            if (trustpass == null) {
                trustpass = "";
            }
            KeyStore keyStore = KeyStore.getInstance(storeType);
            keyStore.load(new FileInputStream(truststore), trustpass
                    .toCharArray());
            trustFactory.init(keyStore);
            trustManagers = trustFactory.getTrustManagers();
        }
        return trustManagers;
    }

    public static TrustManager[] getTrustManagers(KeyStore truststore,
            String trustpass) {
        TrustManager[] trustManagers;
        try {
            if (truststore == null) {
                trustManagers = null;
            } else {
                TrustManagerFactory trustFactory = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());
                if (trustpass == null) {
                    trustpass = SSLConfig.getc2sTrustPassword();
                }

                trustFactory.init(truststore);

                trustManagers = trustFactory.getTrustManagers();
            }
        } catch (KeyStoreException e) {
            trustManagers = null;
            log.error("SSLTrustManagerFactory startup problem.", e);
        } catch (NoSuchAlgorithmException e) {
            trustManagers = null;
            log.error("SSLTrustManagerFactory startup problem.", e);
        }
        return trustManagers;
    }

}
