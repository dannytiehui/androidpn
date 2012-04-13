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
package org.androidpn.server.starter;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/** 
 * This is a bootstrap class that starts the XMPP server.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ServerStarter {

    private static Logger logger = Logger.getLogger("ServerStarter");

    private static final String DEFAULT_CONF_DIR = "conf";

    private static final String DEFAULT_LIB_DIR = "lib";

    public static void main(String[] args) {
        try {
            // FileHandler fh = new FileHandler("../logs/starter.log");
            // fh.setFormatter(new SimpleFormatter());
            // logger.addHandler(fh);
            StreamHandler sh = new StreamHandler(System.out,
                    new SimpleFormatter());
            logger.addHandler(sh);
            logger.setLevel(Level.ALL);
            new ServerStarter().start();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    @SuppressWarnings("unchecked")
    private void start() {
        try {
            final ClassLoader parent = findParentClassLoader();

            String baseDirString = System.getProperty("base.dir", "..");

            File confDir = new File(baseDirString + File.separator
                    + DEFAULT_CONF_DIR);
            if (!confDir.exists()) {
                throw new RuntimeException("Conf directory "
                        + confDir.getAbsolutePath() + " does not exist.");
            }

            File libDir = new File(baseDirString + File.separator
                    + DEFAULT_LIB_DIR);
            if (!libDir.exists()) {
                throw new RuntimeException("Lib directory "
                        + libDir.getAbsolutePath() + " does not exist.");
            }

            ClassLoader loader = new ServerClassLoader(parent, confDir, libDir);

            Thread.currentThread().setContextClassLoader(loader);

            Class containerClass = loader
                    .loadClass("org.androidpn.server.xmpp.XmppServer");
            containerClass.newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ClassLoader findParentClassLoader() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        if (parent == null) {
            parent = this.getClass().getClassLoader();
            if (parent == null) {
                parent = ClassLoader.getSystemClassLoader();
            }
        }
        return parent;
    }

}