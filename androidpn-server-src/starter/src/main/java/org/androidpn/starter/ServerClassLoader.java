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
package org.androidpn.starter;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/** 
 * Class desciption here.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class ServerClassLoader extends URLClassLoader {

    /**
     * Constructs the classloader.
     * 
     * @param parent
     *            the parent class loader (or null for none).
     * @param confDir
     *            the directory to load configration files from.
     * @param libDir
     *            the directory to load jar files from.
     * @throws java.net.MalformedURLException
     *             if the libDir path is not valid.
     */
    public ServerClassLoader(ClassLoader parent, File confDir, File libDir)
            throws MalformedURLException {
        super(new URL[] { confDir.toURI().toURL(), libDir.toURI().toURL() },
                parent);

        File[] jars = libDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                boolean accept = false;
                String smallName = name.toLowerCase();
                if (smallName.endsWith(".jar")) {
                    accept = true;
                } else if (smallName.endsWith(".zip")) {
                    accept = true;
                }
                return accept;
            }
        });

        if (jars == null) {
            return;
        }

        for (int i = 0; i < jars.length; i++) {
            if (jars[i].isFile()) {
                addURL(jars[i].toURI().toURL());
            }
        }
    }

}
