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
 
package org.androidpn.server.container;

import java.io.File;

import org.androidpn.server.util.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.webapp.WebAppContext;

*//** 
 * This class starts a instance on the configured port and loads the admin
 * console web application. 
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 *//*
public class AdminConsole {

    private static final Log log = LogFactory.getLog(AdminConsole.class);

    private String adminHost;

    private int adminPort;

    private Server adminServer;

    private ContextHandlerCollection contexts;

    private boolean httpStarted = false;

    *//**
     * Constuctor that create a Jetty module.
     * 
     * @param homeDir the application home directory
     *//*
    public AdminConsole(String homeDir) {
        contexts = new ContextHandlerCollection();
        Context context = new WebAppContext(contexts, homeDir + File.separator
                + "console", "/");
        context.setWelcomeFiles(new String[] { "index.jsp" });

        adminHost = Config.getString("admin.console.host", "127.0.0.1");
        adminPort = Config.getInt("admin.console.port", 8080);
        adminServer = new Server();
        adminServer.setSendServerVersion(false);
    }

    *//**
     * Starts the Jetty server instance.
     *//*
    public void startup() {
        if (adminPort > 0) {
            Connector httpConnector = new SelectChannelConnector();
            httpConnector.setHost(adminHost);
            httpConnector.setPort(adminPort);
            adminServer.addConnector(httpConnector);
        }

        if (adminServer.getConnectors() == null
                || adminServer.getConnectors().length == 0) {
            adminServer = null;
            log.warn("Admin console not started due to configuration error.");
            return;
        }

        adminServer
                .setHandlers(new Handler[] { contexts, new DefaultHandler() });

        try {
            adminServer.start();
            httpStarted = true;
            log.debug("Admin console started.");
        } catch (Exception e) {
            log.error("Could not start admin conosle server", e);
        }
    }

    *//**
     * Shuts down the Jetty server instance.
     *//*
    public void shutdown() {
        try {
            if (adminServer != null && adminServer.isRunning()) {
                adminServer.stop();
            }
        } catch (Exception e) {
            log.error("Error stopping admin console server", e);
        }
        adminServer = null;
    }

    *//**
     * Restarts the Jetty server instance.
     *//*
    public void restart() {
        try {
            adminServer.stop();
            adminServer.start();
        } catch (Exception e) {
            log.error(e);
        }
    }

    *//**
     * Returns the collection of Jetty contexts used in the admin console.
     *  
     * @return the Jetty context handlers 
     *//*
    public ContextHandlerCollection getContexts() {
        return contexts;
    }

    *//**
     * Returns the host name of the admin console.
     * 
     * @return the host name of the admin console.
     *//*
    public String getAdminHost() {
        return adminHost;
    }

    *//**
     * Returns the port of the admin console.
     * 
     * @return the port of the admin console.
     *//*
    public int getAdminPort() {
        return adminPort;
    }

    *//**
     * Returns the start stutus of the admin console.
     * 
     * @return true if the admin console has been started, false otherwise.  
     *//*
    public boolean isHttpStarted() {
        return httpStarted;
    }

}
*/