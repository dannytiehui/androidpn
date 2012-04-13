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
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * SSL Key Manager Factory class.  
 *
 * @author Sehwan Noh (sehnoh@gmail.com)
 */
public class SSLKeyManagerFactory {

    private static final Log log = LogFactory
            .getLog(SSLKeyManagerFactory.class);

    public static KeyManager[] getKeyManagers(String storeType,
            String keystore, String keypass) throws NoSuchAlgorithmException,
            KeyStoreException, IOException, CertificateException,
            UnrecoverableKeyException {
        KeyManager[] keyManagers;
        if (keystore == null) {
            keyManagers = null;
        } else {
            if (keypass == null) {
                keypass = "";
            }
            KeyStore keyStore = KeyStore.getInstance(storeType);
            keyStore.load(new FileInputStream(keystore), keypass.toCharArray());

            KeyManagerFactory keyFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyFactory.init(keyStore, keypass.toCharArray());
            keyManagers = keyFactory.getKeyManagers();
        }
        return keyManagers;
    }

    public static KeyManager[] getKeyManagers(KeyStore keystore, String keypass) {
        KeyManager[] keyManagers;
        try {
            if (keystore == null) {
                keyManagers = null;
            } else {
                KeyManagerFactory keyFactory = KeyManagerFactory
                        .getInstance(KeyManagerFactory.getDefaultAlgorithm());
                if (keypass == null) {
                    keypass = SSLConfig.getKeyPassword();
                }

                keyFactory.init(keystore, keypass.toCharArray());
                keyManagers = keyFactory.getKeyManagers();
            }
        } catch (KeyStoreException e) {
            keyManagers = null;
            log.error("SSLKeyManagerFactory startup problem.", e);
        } catch (NoSuchAlgorithmException e) {
            keyManagers = null;
            log.error("SSLKeyManagerFactory startup problem.", e);
        } catch (UnrecoverableKeyException e) {
            keyManagers = null;
            log.error("SSLKeyManagerFactory startup problem.", e);
        }
        return keyManagers;
    }

}
